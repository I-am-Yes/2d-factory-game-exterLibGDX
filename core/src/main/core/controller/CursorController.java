package core.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;


public class CursorController {

    private Cursor defaultCursor;

    public void loadCursor() {
        Pixmap source = new Pixmap(Gdx.files.internal("cursor/cursor2.png"));

        int size = 32; // only 16, 32, 64 etc... works
        Pixmap scaled = new Pixmap(size, size, source.getFormat());
        scaled.drawPixmap(
            source,
            0, 0, source.getWidth(), source.getHeight(),
            0, 0, size, size
        );

        defaultCursor = Gdx.graphics.newCursor(scaled, 0, 0);
        Gdx.graphics.setCursor(defaultCursor);
        source.dispose();
        scaled.dispose();
    }

    public void disposeCursor() {
        if (defaultCursor != null) {
            defaultCursor.dispose();
        }
    }

}
