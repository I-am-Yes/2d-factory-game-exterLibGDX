package data;

public class PlayerData {

    private static float PLAYER_WIDTH = 4f;
    private static float PLAYER_HEIGHT = 5f;
    private static float PLAYER_SPEED = 8f;


    public static float getPlayerWidth() {
        return PLAYER_WIDTH;
    }

    public static void setPlayerWidth(float playerWidth) {
        PLAYER_WIDTH = playerWidth;
    }

    public static float getPlayerHeight() {
        return PLAYER_HEIGHT;
    }

    public static void setPlayerHeight(float playerHeight) {
        PLAYER_HEIGHT = playerHeight;
    }

    public static float getPlayerSpeed() {
        return PLAYER_SPEED;
    }

    public static void setPlayerSpeed(float playerSpeed) {
        PLAYER_SPEED = playerSpeed;
    }

}
