package com.crazy_putting.game.Screens;

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
import com.crazy_putting.game.Components.Colliders.CollisionManager;
import com.crazy_putting.game.Components.Graphics.ArrowGraphics3DComponent;
import com.crazy_putting.game.GameLogic.CourseManager;
import com.crazy_putting.game.GameLogic.GameManager;
import com.crazy_putting.game.GameLogic.GraphicsManager;
import com.crazy_putting.game.GameLogic.TerrainEditor;
import com.crazy_putting.game.GameObjects.GUI;
import com.crazy_putting.game.GameObjects.GameObject;
import com.crazy_putting.game.Graphics3D.TerrainGenerator;

/*
Handles the graphics of the in-Game screen, which is the 3D cam and 2D cam for the GUI and the tools to control the 3D environment
 */
public class GameScreen3D extends InputAdapter implements Screen {

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
    private float maxShootSpeed = CourseManager.getMaxSpeed();
    private boolean increaseSpeedBar = true;
    private GameObject shootArrow;
    private Vector3 dirShot;
    private boolean won = false;

    public GameScreen3D(GolfGame pGame, int pMode) {
        this.game = pGame;
        initCameras();
        initTerrain();
        gameManager = new GameManager(pGame, pMode);
        terrainEditor.addObserver(gameManager);
        gui = new GUI(game, gameManager, hudViewport, MenuScreen.Spline3D);
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
        cam3D = new PerspectiveCamera(67, Width3DScreen, Height2DScreen);
        cam3D.position.add(new Vector3(0, 1300, 0));
        cam3D.lookAt(0, 0, 0);
        cam3D.near = 1f;
        cam3D.far = 15000f;
        cam3D.update();
        camController = new CameraInputController(cam3D);
        camController.translateUnits = 50;
        newW = new Window("You Won!", new Skin(Gdx.files.internal("skin/plain-james-ui.json")));
        newW.setSize(200, 80);
        newW.add(new Label("Congratulations!!", new Skin(Gdx.files.internal("skin/plain-james-ui.json"))));
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

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if (MenuScreen.AI || checkGUIActive() || gameManager.anyBallIsMoving()) return false;

        dirShot = terrainEditor.getObject(screenX, screenY);
        if (dirShot == null) return false;
        speedCache = 0;
        Vector3 pos = terrainEditor.getObject(screenX, screenY);
        speedPressing = true;
        Vector3 playerPos = gameManager.getPlayer(gameManager.getActivePlayerIndex()).getPosition();
        shootArrow = new GameObject((new Vector3(playerPos)));
        int radius = 20;
        pos.y = playerPos.z + radius;
        ArrowGraphics3DComponent g = new ArrowGraphics3DComponent(new Vector3(playerPos), pos, Color.DARK_GRAY);
        shootArrow.addGraphicComponent(g);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (speedPressing && dirShot != null) {
            gui.addShootBar(-100);
            shootArrow.destroy();
            Vector3 playerPos = gameManager.getPlayer(gameManager.getActivePlayerIndex()).getPosition();
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

            float[][] input = new float[gameManager.getAmountPlayers()][2];
            for (int i = 0; i < gameManager.getAmountPlayers(); i++) {
                input[i][0] = speedCache;
                input[i][1] = angle;
            }
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
        if (speedPressing) {
            handleShootSpeed();
        }
        if (gameManager.isGameWon()) {
            fullScreenStage.addActor(newW);
            won = true;
        }

        retrieveGUIState();
        camController.update();//Input
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

    private void handleShootSpeed() {
        //System.out.println("toolbar" + _increaseSpeedBar);
        float step = maxShootSpeed / 100;
        //System.out.println(step);
        if (increaseSpeedBar) {
            //  if(_speedCache + step < _maxShootSpeed) {
            speedCache += step;
            gui.addShootBar(+1);
            //   }
        } else if (increaseSpeedBar == false) {
            speedCache -= step;
            gui.addShootBar(-1);
        }
        //System.out.println(_speedCache+" Speed");
        //System.out.println("Max Speed "+_maxShootSpeed);
        if (speedCache > maxShootSpeed || speedCache < 0) {
            boolean currentState = increaseSpeedBar;
            increaseSpeedBar = !currentState;
        }
    }

    private void updateCamera() {
        cam3D.update();
        cam2D.update();
        game.batch.setProjectionMatrix(cam2D.combined);

        int red = 66;
        int green = 134;
        int blue = 244;
        Gdx.gl.glClearColor((float) (red / 255.0), (float) (green / 255.0), (float) (blue / 255.0), 1);
        // Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void resize(int width, int height) {
        // _hudViewport.update(width,height);
        // _cam3D.viewportWidth = width*2f;
        // _cam3D.viewportHeight = _cam3D.viewportWidth * height/width;
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
