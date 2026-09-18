package core.world.chunk.meshes;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ShortArray;

public final class ChunkMeshBuilder {

    private final Texture texture;
    private final FloatArray vertices = new FloatArray(false, 1024);
    private final ShortArray indices = new ShortArray(false, 1024);

    private int vertexCount;
    private static final int MAX_VERTEX_COUNT = 65_532;

    public ChunkMeshBuilder(Texture texture) {
        this.texture = texture;
    }

    public boolean canAddQuad() {
        return vertexCount + 4 <= MAX_VERTEX_COUNT;
    }

    public void add(TextureRegion region, float x, float y, float size, float packedColor) {
        if (!canAddQuad()) {
            throw new IllegalStateException("Chunk mesh has reached its vertex limit");
        }

        if (region.getTexture() != texture) {
            throw new IllegalArgumentException("TextureRegion belongs to a different texture");
        }

        int base = vertexCount;


        // Bottom-left
        addVertex(
            x, y,
            packedColor,
            region.getU(), region.getV2()
        );

        // Top-left
        addVertex(
            x, y + size,
            packedColor,
            region.getU(), region.getV()
        );

        // Top-right
        addVertex(
            x + size, y + size,
            packedColor,
            region.getU2(), region.getV()
        );

        // Bottom-right
        addVertex(
            x + size, y,
            packedColor,
            region.getU2(), region.getV2()
        );

        indices.add(base);
        indices.add(base + 1);
        indices.add(base + 2);

        indices.add(base + 2);
        indices.add(base + 3);
        indices.add(base);

        vertexCount += 4;
    }

    private void addVertex(float x, float y, float packedColor, float u, float v) {
        vertices.add(x);
        vertices.add(y);
        vertices.add(packedColor);
        vertices.add(u);
        vertices.add(v);
    }

    public boolean isEmpty() {
        return vertexCount == 0;
    }

    public MeshPart build() {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot build an empty chunk mesh");
        }
        Mesh mesh = new Mesh(
            true,
            vertexCount,
            indices.size,
            new VertexAttribute(
                VertexAttributes.Usage.Position,
                2,
                ShaderProgram.POSITION_ATTRIBUTE
            ),
            VertexAttribute.ColorPacked(),
            VertexAttribute.TexCoords(0)
        );

        mesh.setVertices(
            vertices.items,
            0,
            vertices.size
        );

        mesh.setIndices(
            indices.items,
            0,
            indices.size
        );

        return new MeshPart(
            texture,
            mesh,
            indices.size
        );
    }

}
