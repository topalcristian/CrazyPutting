package com.golf.game.Screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.golf.game.Components.Colliders.CollisionManager;
import com.golf.game.Components.Graphics.ArrowGraphicsComponent;
import com.golf.game.GameLogic.GameManager;
import com.golf.game.GameLogic.GraphicsManager;
import com.golf.game.GameLogic.TerrainEditor;
import com.golf.game.GameObjects.GUI;
import com.golf.game.GameObjects.GameObject;
import com.golf.game.Graphics3D.TerrainGenerator;


public class GameScreen extends InputAdapter implements Screen {

    public static int Width3DScreen;
    public static int Height3DScreen;
    public static int Width2DScreen;
    public static int Height2DScreen;
    final GolfGame game;
    Stage fullScreenStage;
    Window newW;
    private GameManager gameManager;
    private Camera cam3D;
    private Camera cam2D;
    private GUI gui;
    private FitViewport hudViewport;
    private FitViewport dialogViewPort;
    private TerrainEditor terrainEditor;
    private InputMultiplexer inputMain;
    private CameraInputController camController;
    private float speedCache;
    private boolean speedPressing = false;
    private GameObject shootArrow;
    private Vector3 dirShot;
    private boolean won = false;

    public GameScreen(GolfGame pGame, int pMode) {
        this.game = pGame;
        initCameras();
        initTerrain();
        gameManager = new GameManager(pGame, pMode);
        terrainEditor.addObserver(gameManager);
        gui = new GUI(game, gameManager, hudViewport);
        terrainEditor.setGUI(gui);
        initInput();
        Gdx.graphics.setVSync(true);
    }

    private void initCameras() {
        Width2DScreen = 300;
        Width3DScreen = Gdx.graphics.getWidth() - Width2DScreen;
        Height2DScreen = Height3DScreen = GraphicsManager.WINDOW_HEIGHT;
        cam2D = new OrthographicCamera();
        hudViewport = new FitViewport(Width2DScreen, Height2DScreen, cam2D);
        cam2D.update();
        dialogViewPort = new FitViewport(Width3DScreen, Height3DScreen, cam2D);
        fullScreenStage = new Stage(dialogViewPort, game.batch);
        cam3D = new PerspectiveCamera(90, Width3DScreen, Height2DScreen);
        cam3D.position.add(new Vector3(0, 1300, 0));
        cam3D.lookAt(0, 0, 0);
        cam3D.near = 1f;
        cam3D.far = 15000f;
        cam3D.update();
        camController = new CameraInputController(cam3D);
        newW = new Window("You Won!", new Skin(Gdx.files.internal("skin/plain-james-ui.json")));
        newW.setSize(200, 80);
        newW.add(new Label("Congratulations", new Skin(Gdx.files.internal("skin/plain-james-ui.json"))));
        newW.setModal(true);
        newW.setMovable(true);
        newW.setPosition(0, Gdx.graphics.getHeight() / 2 - newW.getHeight() / 2);
        newW.setVisible(true);

    }

    private void initTerrain() {
        terrainEditor = new TerrainEditor(cam3D, true);
    }

    private void initInput() {
        inputMain = new InputMultiplexer(this);
        inputMain.addProcessor(gui.getUIInputProcessor());
        inputMain.addProcessor(camController);
        Gdx.input.setInputProcessor(inputMain);
    }

    private void retrieveGUIState() {

        if (checkGUIActive() && !inputMain.getProcessors().contains(terrainEditor, true)) {
            inputMain.addProcessor(0, terrainEditor);
        } else if (!checkGUIActive() && inputMain.getProcessors().contains(terrainEditor, true))
            inputMain.removeProcessor(terrainEditor);

    }

    private boolean checkGUIActive() {
        boolean stateSpline = gui.isSplineEditActive();
        boolean changeBall = gui.isChangeBallActive();
        boolean changeHole = gui.isChangeHoleActive();
        boolean addObject = gui.isAddObjectsActive();
        boolean eraseObject = gui.isEraseObjectsActive();
        terrainEditor.updateGUIState(stateSpline, changeBall, changeHole, addObject, eraseObject);
        return (stateSpline || changeBall || changeHole || addObject || eraseObject);
    }

    /*

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if (MenuScreen.AI || checkGUIActive() || Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) return false;

        dirShot = terrainEditor.getObject(screenX, screenY);
        if (dirShot == null) return false;
        speedCache = 0;
        Vector3 pos = terrainEditor.getObject(screenX, screenY);
        speedPressing = true;
        Vector3 playerPos = gameManager.getPlayer().getPosition();
        shootArrow = new GameObject((new Vector3(playerPos)));
        int radius = 40;
        pos.y = playerPos.z + radius;
        ArrowGraphicsComponent g = new ArrowGraphicsComponent(new Vector3(playerPos), pos, Color.BLACK);
        shootArrow.addGraphicComponent(g);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (speedPressing && dirShot != null) {

            shootArrow.destroy();
            Vector3 playerPos = gameManager.getPlayer().getPosition();
            swapYZ(dirShot);
            Vector2 pos2 = new Vector2(playerPos.x, playerPos.y);
            Vector2 dir2 = new Vector2(dirShot.x, dirShot.y);
            float distance = dir2.dst(pos2);
            float initialAngle = (float) Math.toDegrees(Math.acos(Math.abs(pos2.x - dir2.x) / distance));
            float angle = 0;

            if (pos2.x < dir2.x && pos2.y < dir2.y) {
                angle = initialAngle;
            } else if (pos2.x > dir2.x && pos2.y < dir2.y) {
                angle = 180 - initialAngle;
            } else if (pos2.x > dir2.x && pos2.y > dir2.y) {
                angle = 180 + initialAngle;
            } else if (pos2.x < dir2.x && pos2.y > dir2.y) {
                angle = 360 - initialAngle;
            }

            float[][] input = new float[1][2];
            input[0][0] = speedCache;
            input[0][1] = angle;
            gameManager.shootBallFromGameScreen3DInput(input);
        }
        speedPressing = false;
        return false;

    }


    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) return false;
        shootArrow.destroy();
        Vector3 playerPos = gameManager.getPlayer().getPosition();
        Vector3 currentPos = terrainEditor.getObject(screenX, screenY);
        int radius = 40;
        currentPos.y = playerPos.z + radius;
        ArrowGraphicsComponent g = new ArrowGraphicsComponent(new Vector3(playerPos), currentPos, Color.DARK_GRAY);
        shootArrow.addGraphicComponent(g);
        return false;
    }
*/
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if (MenuScreen.AI || checkGUIActive() || gameManager.BallIsMoving()) return false;

        dirShot = terrainEditor.getObject(screenX, screenY);
        if (dirShot == null) return false;
        speedCache = 0;
        Vector3 pos = terrainEditor.getObject(screenX, screenY);

        speedPressing = true;
        Vector3 playerPos = gameManager.getPlayer().getPosition();
        shootArrow = new GameObject((new Vector3(playerPos)));
        int radius = 20;
        pos.y = playerPos.z + radius;
        ArrowGraphicsComponent g = new ArrowGraphicsComponent(new Vector3(playerPos), pos, Color.DARK_GRAY);
        shootArrow.addGraphicComponent(g);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (speedPressing && dirShot != null) {

            shootArrow.destroy();
            Vector3 playerPos = gameManager.getPlayer().getPosition();
            swapYZ(dirShot);
            Vector2 pos2 = new Vector2(playerPos.x, playerPos.y);
            Vector2 dir2 = new Vector2(dirShot.x, dirShot.y);
            float distance = dir2.dst(pos2);
            float initialAngle = (float) Math.toDegrees(Math.acos(Math.abs(pos2.x - dir2.x) / distance));
            float angle = 0;

            if (pos2.x < dir2.x && pos2.y < dir2.y) {
                angle = initialAngle;
            } else if (pos2.x > dir2.x && pos2.y < dir2.y) {
                angle = 180 - initialAngle;
            } else if (pos2.x > dir2.x && pos2.y > dir2.y) {
                angle = 180 + initialAngle;
            } else if (pos2.x < dir2.x && pos2.y > dir2.y) {
                angle = 360 - initialAngle;
            }

            float[][] input = new float[1][2];

            input[0][0] = speedCache;
            input[0][1] = angle;

            gameManager.shootBallFromGameScreen3DInput(input);
        }
        speedPressing = false;
        return false;

    }

    private void swapYZ(Vector3 v) {
        Vector3 cache = new Vector3(v);
        v.y = cache.z;
        v.z = cache.y;
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            dispose();
            game.restart();
            return;
        }
        if (gameManager.isGameWon()) {
            fullScreenStage.addActor(newW);
            won = true;
        }

        retrieveGUIState();
        camController.update();
        gameManager.update(delta);//Logic
        updateCamera();

        GraphicsManager.render3D(game.batch3D, cam3D);
        hudViewport.apply();
        gui.render();
        if (won) {
            dialogViewPort.apply();
            fullScreenStage.draw();
            fullScreenStage.act();
        }
    }


    private void updateCamera() {
        cam3D.update();
        cam2D.update();
        game.batch.setProjectionMatrix(cam2D.combined);
        Gdx.gl.glClearColor((float) (135 / 255.0), (float) (206 / 255.0), (float) (235 / 255.0), 1);

    }

    @Override
    public void resize(int width, int height) {

        cam3D.update();
    }

    @Override
    public void show() {
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        fullScreenStage.dispose();
        inputMain.clear();
        game.batch3D.dispose();
        GraphicsManager.clearGraphicsComponents();
        TerrainGenerator.dispose();
        CollisionManager.dispose();
        gameManager = null;
        terrainEditor.dispose();
        gui.dispose();
        terrainEditor = null;
        camController = null;
        cam3D = null;
        cam2D = null;

    }

}
