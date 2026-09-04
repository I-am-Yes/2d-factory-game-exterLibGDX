package ui;

import Data.map.FloorType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import core.controller.PlayerAction;

public class GameUI {

    private final Stage stage;
    private final Skin skin;
    private final Label selectedLabel;
    private final Label FPSLabel;
    private final Label VSyncLabel;

    public GameUI(PlayerAction playerAction) {
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        stage = new Stage(new ScreenViewport());

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        selectedLabel = new Label("Selected: none", skin);
        FPSLabel = new Label("FPS: 0", skin);
        VSyncLabel = new Label("VSync: off", skin);

        selectedLabel.setFontScale(2f);
        FPSLabel.setFontScale(2f);


        TextButton sand = new TextButton("Sand (2)", skin, "toggle");
        TextButton stone = new TextButton("Stone (3)", skin, "toggle");
        TextButton rock = new TextButton("Rock (4)", skin, "toggle");

        ButtonGroup<TextButton> group = new ButtonGroup<>(sand, stone, rock);
        group.setMaxCheckCount(1);
        group.setMinCheckCount(0);


        Table hotbar = new Table();
//        hotbar.add(sand).pad(4);
//        hotbar.add(stone).pad(4);
//        hotbar.add(rock).pad(4);

        //hotbar.setBackground("ui/hotbar/Rectangle 1.png");

        root.top().left();
//        root.add(selectedLabel).pad(8).left();
//        root.add(FPSLabel).pad(16).left();
//        root.add(VSyncLabel).pad(16).left();
        root.row();
        root.add(hotbar).expand().bottom().pad(12);
    }

    public void update(float deltaTime, FloorType floorType) {
        act(deltaTime);
        updateSelected(floorType);
        updateFPSLabel();
    }

    public void act(float delta) {
        stage.act(delta);
    }

    public void draw() {
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    public Stage getStage() {
        return stage;
    }

    public void updateSelected(FloorType floorType) {
        selectedLabel.setText(floorType == null ? "Selected: none" : "Selected: " + floorType);
    }

    public void updateFPSLabel() {
        FPSLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
    }

    public void updateVSyncLabel() {
        //VSyncLabel.setText("V-Sync: " + Gdx.graphics.get)
    }

}
