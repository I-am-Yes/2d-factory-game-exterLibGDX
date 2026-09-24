package ui;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Null;

import java.util.function.Supplier;

public class InterfaceAction {

    public void sequenceMoveDown(Group object) {
        sequenceMove(object, 0f, -4f, 0.1f);
    }

    public void sequenceMoveUp(Group object) {
        sequenceMove(object, 0f, 4f, 0.1f);
    }

    public void sequenceMove(Group object, float x, float y, float duration) {
        sequenceMove(object, x, y, 1f, 1f, duration, duration);
    }

    public void sequenceMove(Group object, float x, float y, float x2, float y2, float duration) {
        sequenceMove(object, x, y, x2, y2, duration, duration);
    }

    public void sequenceMove(Group object, float x, float y, float x2, float y2, float duration, float duration2) {
        float originalX = object.getX();
        float originalY = object.getY();

        object.setTransform(true);
        object.addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.moveTo(originalX + x, originalY + y, duration),
                    Actions.scaleTo(x2, y2, duration2)
                )
            )
        );
    }

    public void moveTo(Actor object, Vector2 position, float duration) {
        moveTo(object, position.x, position.y, duration);
    }

    public void moveTo(Actor object, float x, float y, float duration) {
        object.clearActions();
        object.addAction(Actions.moveTo(x, y, duration));
    }

    public void DefaultHoverEffect(Group object) {
        DefaultHoverEffect(object, 0f, 4f, 0f, 0f, 0.1f, 0.1f);
    }

    public void DefaultHoverEffect(Group object, float duration, float duration2) {
        DefaultHoverEffect(object, 0f, 4f, 0f, 0f, duration, duration2);
    }

    public void DefaultHoverEffect(Group object, float x, float y, float x2, float y2, float duration, float duration2) {
        float originalX = object.getX();
        float originalY = object.getY();

        addHoverEffect(object,
            () -> Actions.moveTo(originalX + x, originalY + y, duration),
            () -> Actions.moveTo(originalX + x2, originalY + y2, duration2));
    }

    public void addHoverEffect(Group object, Supplier<Action> enterAction, Supplier<Action> exitAction) {
        addHoverEffect(object, enterAction, exitAction, null, null);
    }

    public void addHoverEffect(Group object,
                               Supplier<Action> enterAction, Supplier<Action> exitAction,
                               @Null Runnable onEnter, @Null Runnable onExit
    ) {
        object.setTransform(true);
        object.setOrigin(Align.center);

        object.addListener(new InputListener() {
            @Override
            public void enter(
                InputEvent event,
                float x,
                float y,
                int pointer,
                Actor fromActor
            ) {
                if (pointer == -1) {
                    object.clearActions();
                    object.addAction(enterAction.get());

                    if (onEnter != null) {
                        onEnter.run();
                    }
                }
            }

            @Override
            public void exit(
                InputEvent event,
                float x,
                float y,
                int pointer,
                Actor toActor
            ) {
                if (pointer == -1) {
                    object.clearActions();
                    object.addAction(exitAction.get());

                    if (onExit != null) {
                        onExit.run();
                    }
                }
            }
        });
    }

}
