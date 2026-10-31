package core.utils.unit.bot;

import arcane.Events;
import arcane.Time;
import arcane.math.MathUtils;
import arcane.utils.Array;
import arcane.utils.TimeUtils;
import core.utils.unit.UnitManager;

import static core.app.Vars.*;

public class BotManager extends UnitManager {
    private final Array<BotUnit> bots = new Array<>(false, 1024);

    private boolean follow = false;

    public BotManager() {
        float delta = Time.delta();

        Events.run(Events.Trigger.tickUpdate, () -> {

            TimeUtils.measureAndPrint("BotManager update: ", Time.UPDATE_INTERVAL, 1f,
                () -> {
                    updateBots();
                });

        });

    }


    public BotUnit spawnBot(float x, float y, float direction) {
        return spawnBot(null, x, y, direction);
    }

    public BotUnit spawnBot(BotUnit bot, float x, float y, float direction) {
        if (bot == null) bot = new BotUnit();
        bot.setPos(x, y);
        bot.setDirection(direction);

        addBot(bot);
        return bot;
    }

    public void updateBots() {
        followUpdate();
    }

    public void setBotsFollow(boolean follow) {
        this.follow = follow;
    }

    private void followUpdate() {
        if (!follow) return;

        float targetX = player.getX();
        float targetY = player.getY();
        for (BotUnit bot : bots) {
            if (bot.getX() != targetX || bot.getY() != targetY) {
//                bot.rotateTo(MathUtils.angleTo(bot.getX(), bot.getY(), targetX, targetY));
                bot.moveAndDirTo(targetX, targetY, MathUtils.angleTo(bot.getX(), bot.getY(), targetX, targetY));
            }
        }
    }

    public void addBot(BotUnit bot) {
        bots.add(bot);
    }

    public void removeBot(BotUnit bot) {
        bots.removeValue(bot, true);
    }

    public Array<BotUnit> getBots() {
        return bots;
    }

}
