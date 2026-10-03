package core.event;

import arcane.Events;

public class AppEvent {

    public static final class AppStarted extends AppEvent {
        public static void fire() {
            Events.fire(new AppStarted());
        }

    }

    public static class LoadingScreenEvent extends AppEvent{
        public Object content;
        public String title;
        public String text;
        public float progress;

        public LoadingScreenEvent(Object content, String title, String text, float progress) {
            this.content = content;
            this.title = title;
            this.text = text;
            this.progress = progress;
        }

        public static void fire(Object content, String title, String text, float progress) {
            Events.fire(new LoadingScreenEvent(content, title, text, progress));
        }

        public static void fire(float progress) {
            Events.fire(new LoadingScreenEvent(null, "", null, progress));
        }
    }

    public static class GameLoaded extends AppEvent {
        public static void fire() {
            Events.fire(new GameLoaded());
        }
    }

    public static class GamePaused extends AppEvent {
        public static void fire() {
            Events.fire(new GamePaused());
        }
    }

    public static class PauseGame extends  AppEvent {
        public static void fire() {
            Events.fire(new PauseGame());
        }
    }

    public static class ResumeGame extends  AppEvent {
        public static void fire() {
            Events.fire(new ResumeGame());
        }
    }

}
