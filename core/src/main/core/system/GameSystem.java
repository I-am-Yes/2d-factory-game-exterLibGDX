package core.system;

public interface GameSystem {

    default void update(float delta) {}

    default void render() {}

    default void resize(int width, int height) {}

    default void dispose() {}
}
