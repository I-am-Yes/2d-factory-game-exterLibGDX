package core.world.chunk.meshes;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;

public final class MeshPart implements Disposable {

    private final Texture texture;
    private final Mesh mesh;
    private final int indexCount;

    public MeshPart(Texture texture, Mesh mesh, int indexCount) {
        this.texture = texture;
        this.mesh = mesh;
        this.indexCount = indexCount;
    }

    public void draw(ShaderProgram shader) {
        texture.bind(0);
        mesh.render(shader, GL20.GL_TRIANGLES, 0, indexCount);
    }

    @Override
    public void dispose() {
        mesh.dispose();
    }
}
