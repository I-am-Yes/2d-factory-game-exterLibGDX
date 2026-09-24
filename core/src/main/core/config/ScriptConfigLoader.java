package core.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

public final class ScriptConfigLoader {
    private ScriptConfigLoader() {}

    public static void loadAll(String directory, Map<String, Object> apis) {

        FileHandle folder = Gdx.files.internal(directory);

        if (!folder.exists() || !folder.isDirectory()) {
            Gdx.app.error("ScriptConfigLoader", "config folder not found: " + directory);
        }

        FileHandle[] scripts = folder.list(".js");

        // Stable loading order: 00-game.js, 10-camera.js, 20-player.js...
        Arrays.sort(scripts, Comparator.comparing(FileHandle::name));

        for (FileHandle script : scripts) {
            load(script, apis);
        }

    }

    private static void load(FileHandle script, Map<String, Object> apis) {
        Context context = Context.enter();

        try {
            Scriptable scope = context.initStandardObjects();

            for (Map.Entry<String, Object> entry : apis.entrySet()) {
                ScriptableObject.putProperty(
                    scope,
                    entry.getKey(),
                    Context.javaToJS(entry.getValue(), scope)
                );
            }

            context.evaluateString(
                scope,
                script.readString("UTF-8"),
                script.path(),
                1,
                null
            );

            Gdx.app.log("ScriptConfigLoader", "Loaded: " + script.path());

        } catch (Exception exception) {
            Gdx.app.error(
                "ScriptConfigLoader",
                "Failed to load: " + script.path(),
                exception
            );
        } finally {
            Context.exit();
        }
    }

}
