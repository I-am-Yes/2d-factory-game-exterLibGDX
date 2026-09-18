package core.world.chunk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import core.assets.AssetsHandler;
import core.controller.camera.CameraViewMode;
import core.world.chunk.cache.ChunkOverviewCache;
import core.world.chunk.cache.ChunkRenderCache;

public final class ChunkRenderer {

    private final AssetsHandler assets;
    private final ShaderProgram shader;

    private final Array<Chunk> visibleChunks = new Array<>(false, 128);
    private final ObjectMap<Chunk, ChunkRenderCache> renderCaches = new ObjectMap<>();
    private final ChunkOverviewCache overviewCache = new ChunkOverviewCache();

    private int lastMinChunkX = Integer.MIN_VALUE;
    private int lastMaxChunkX = Integer.MIN_VALUE;
    private int lastMinChunkY = Integer.MIN_VALUE;
    private int lastMaxChunkY = Integer.MIN_VALUE;

    private static final int OVERVIEW_PADDING_SAMPLES = 2;
    private int overviewMinChunkX = Integer.MAX_VALUE;
    private int overviewMinChunkY = Integer.MAX_VALUE;
    private int overviewMaxChunkX = Integer.MIN_VALUE;
    private int overviewMaxChunkY = Integer.MIN_VALUE;

    private static final float TARGET_SAMPLE_PIXELS = 2f;
    private static final ChunkRenderDetail[] DETAIL_LEVELS = ChunkRenderDetail.values();
    private ChunkRenderDetail currentDetail = ChunkRenderDetail.EXTREME;

    public ChunkRenderer(AssetsHandler assets) {
        this.assets = assets;

        //TODO: add context to this
        //TODO: convert to a chunk system

        shader = new ShaderProgram(
            """
            attribute vec2 a_position;
            attribute vec4 a_color;
            attribute vec2 a_texCoord0;

            uniform mat4 u_projTrans;

            varying vec4 v_color;
            varying vec2 v_texCoords;

            void main() {
                v_color = a_color;
                v_color.a *= 255.0 / 254.0;
                v_texCoords = a_texCoord0;

                gl_Position = u_projTrans
                    * vec4(a_position, 0.0, 1.0);
            }
            """,
            """
            #ifdef GL_ES
            precision mediump float;
            #endif

            varying vec4 v_color;
            varying vec2 v_texCoords;

            uniform sampler2D u_texture;

            void main() {
                gl_FragColor =
                    v_color
                    * texture2D(u_texture, v_texCoords);
            }
            """
        );

        if (!shader.isCompiled()) {
            throw new IllegalStateException(
                "Chunk shader compilation failed:\n"
                    + shader.getLog()
            );
        }
    }

    public void invalidateOverview() {
        overviewCache.invalidate();
    }

    public void invalidateVisibleChunks() {
        lastMinChunkX = Integer.MIN_VALUE;
        lastMaxChunkX = Integer.MIN_VALUE;
        lastMinChunkY = Integer.MIN_VALUE;
        lastMaxChunkY = Integer.MIN_VALUE;
    }

    public void drawCached(
        OrthographicCamera camera, ChunkManager chunkManager,
        CameraViewMode viewMode, ChunkRenderDetail renderDetail,
        float tileSize
    ) {
        ChunkRenderDetail selectedDetail = renderDetail;

        if (selectedDetail != currentDetail) {
            currentDetail = selectedDetail;
            invalidateVisibleChunks();
            overviewCache.invalidate();
        }

        boolean boundsChanged = updateVisibleBounds(camera, tileSize);
        boolean useOverview = usesOverviewCache();

        if (useOverview) {
            if (overviewCache.isDirty() || !overviewCoversVisibleBounds()) {
                rebuildOverview(chunkManager, tileSize);
            }
        } else if (boundsChanged) {
            collectVisibleChunks(chunkManager);
        }

        if (!useOverview) {
            int rebuildBudget = getRebuildBudget();
            int rebuildCount = 0;

            for (int i = 0; i < visibleChunks.size; i++) {
                Chunk chunk = visibleChunks.get(i);
                ChunkRenderCache cache = renderCaches.get(chunk);
                boolean needsRebuild =
                    cache == null || cache.needsRebuild(chunk, currentDetail);

                if (!needsRebuild) {
                    continue;
                }
                if (rebuildCount >= rebuildBudget) {
                    break;
                }

                requireCache(chunk, tileSize);
                rebuildCount++;
            }
        }

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(
            GL20.GL_SRC_ALPHA,
            GL20.GL_ONE_MINUS_SRC_ALPHA
        );

        shader.bind();

        shader.setUniformMatrix(
            "u_projTrans",
            camera.combined
        );

        shader.setUniformi("u_texture", 0);

        if (useOverview) {
            overviewCache.draw(shader);
            return;
        }

        // Preserve global layer ordering.
        for (int i = 0; i < visibleChunks.size; i++) {
            ChunkRenderCache cache = renderCaches.get(visibleChunks.get(i));
            if (cache != null) {
                cache.drawFloors(shader);
            }
        }

        for (int i = 0; i < visibleChunks.size; i++) {
            ChunkRenderCache cache = renderCaches.get(visibleChunks.get(i));
            if (cache != null) {
                cache.drawBuildings(shader);
            }
        }

        for (int i = 0; i < visibleChunks.size; i++) {
            ChunkRenderCache cache = renderCaches.get(visibleChunks.get(i));
            if (cache != null) {
                cache.drawGhosts(shader);
            }
        }

    }

    public void unload(Chunk chunk) {
        ChunkRenderCache cache = renderCaches.remove(chunk);
        if (cache != null) {
            cache.dispose();
        }
        visibleChunks.removeValue(chunk, true);
    }

    public ChunkRenderDetail getCurrentDetail() {
        return currentDetail;
    }

    private boolean usesOverviewCache() {
        return currentDetail.getTileStep() >= Chunk.SIZE;
    }

    private boolean overviewCoversVisibleBounds() {
        return lastMinChunkX >= overviewMinChunkX
            && lastMaxChunkX <= overviewMaxChunkX
            && lastMinChunkY >= overviewMinChunkY
            && lastMaxChunkY <= overviewMaxChunkY;
    }

    private void rebuildOverview(ChunkManager chunkManager, float tileSize) {
        int chunkStep = Math.max(1, currentDetail.getTileStep() / Chunk.SIZE);
        int padding = chunkStep * OVERVIEW_PADDING_SAMPLES;

        overviewMinChunkX = lastMinChunkX - padding;
        overviewMaxChunkX = lastMaxChunkX + padding;
        overviewMinChunkY = lastMinChunkY - padding;
        overviewMaxChunkY = lastMaxChunkY + padding;

        overviewCache.rebuild(
            overviewMinChunkX,
            overviewMaxChunkX,
            overviewMinChunkY,
            overviewMaxChunkY,
            currentDetail,
            chunkManager,
            assets,
            tileSize
        );
    }

    private ChunkRenderDetail selectRenderDetail(OrthographicCamera camera, float tileSize) {
        float pixelsPerWorldUnitX =
            Gdx.graphics.getWidth() / (camera.viewportWidth * camera.zoom);

        float pixelsPerWorldUnitY =
            Gdx.graphics.getHeight() / (camera.viewportHeight * camera.zoom);

        float pixelsPerTile = tileSize * Math.min(pixelsPerWorldUnitX, pixelsPerWorldUnitY);

        for (ChunkRenderDetail detail : DETAIL_LEVELS) {
            float pixelsPerSample = pixelsPerTile * detail.getTileStep();

            if (pixelsPerSample >= TARGET_SAMPLE_PIXELS) {
                return detail;
            }
        }

        return DETAIL_LEVELS[DETAIL_LEVELS.length - 1];
    }

    private int getRebuildBudget() {
        int budget = currentDetail.getRebuildBudget();
        if (budget <= 0) {
            //default 64
            budget = 64;
        }
        return budget;
    }

    private boolean updateVisibleBounds(OrthographicCamera camera, float tileSize) {
        float chunkWorldSize = Chunk.SIZE * tileSize;
        float halfWidth = camera.viewportWidth * camera.zoom * 0.5f;
        float halfHeight = camera.viewportHeight * camera.zoom * 0.5f;

        int minChunkX = MathUtils.floor((camera.position.x - halfWidth) / chunkWorldSize);
        int maxChunkX = MathUtils.floor((camera.position.x + halfWidth) / chunkWorldSize);
        int minChunkY = MathUtils.floor((camera.position.y - halfHeight) / chunkWorldSize);
        int maxChunkY = MathUtils.floor((camera.position.y + halfHeight) / chunkWorldSize);

        if (minChunkX == lastMinChunkX && maxChunkX == lastMaxChunkX
            && minChunkY == lastMinChunkY && maxChunkY == lastMaxChunkY
        ) return false;

        lastMinChunkX = minChunkX;
        lastMaxChunkX = maxChunkX;
        lastMinChunkY = minChunkY;
        lastMaxChunkY = maxChunkY;

        return true;
    }

    private void collectVisibleChunks(ChunkManager chunkManager) {
        visibleChunks.clear();
        for (int chunkX = lastMinChunkX; chunkX <= lastMaxChunkX; chunkX++) {
            for (int chunkY = lastMinChunkY; chunkY <= lastMaxChunkY; chunkY++) {
                Chunk chunk = chunkManager.getLoadedChunk(chunkX, chunkY);
                if (chunk != null) {
                    visibleChunks.add(chunk);
                }
            }
        }
    }

    private ChunkRenderCache requireCache(Chunk chunk, float tileSize) {
        ChunkRenderCache cache = renderCaches.get(chunk);

        if (cache == null) {
            cache = new ChunkRenderCache();
            renderCaches.put(chunk, cache);
        }

        if (cache.needsRebuild(chunk, currentDetail)) {
            cache.rebuild(chunk, assets, tileSize, currentDetail);
        }

        return cache;
    }

    public void dispose() {
        for (ChunkRenderCache cache : renderCaches.values()) {
            cache.dispose();
        }

        renderCaches.clear();
        visibleChunks.clear();

        shader.dispose();
        overviewCache.dispose();
    }

}
