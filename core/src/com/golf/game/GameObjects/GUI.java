package com.golf.game.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.golf.game.Components.Graphics.GraphicsComponent;
import com.golf.game.GameLogic.CourseManager;
import com.golf.game.GameLogic.GameManager;
import com.golf.game.Screens.GolfGame;
import com.golf.game.Screens.Graphics;

public class GUI extends GameObject {

    Viewport view;
    private GolfGame _game;
    private GameManager _gameManager;
    private Skin _skin;
    private TextButton saveSplines;
    private Table table;
    private Stage UIStage;
    private Label speedText;
    private Label ball_position;
    private Label turnCount;
    private Label maxSpeed;
    private CheckBox _splineEdit;
    private CheckBox _changeBallPos;
    private CheckBox _changeHolePos;
    private CheckBox _addObjects;
    private CheckBox _eraseObject;


    private Slider _widthObstacle;
    private Slider _deepObstacle;
    private Slider _heightObstacle;
    private CheckBox _keepRatio;

    private Label _widthObstacleLabel;
    private Label _deepObstacleLabel;
    private Label _heightObstacleLabel;
    private Label controls;
    private Label simulationCounter;


    private Ball _activaBall;


    public GUI(GolfGame pGame, GameManager pGameManager, FitViewport viewPort) {
        _game = pGame;
        if (viewPort == null)
            view = new FitViewport(10, 10);
        else
            view = viewPort;
        assert viewPort != null;
        UIStage = new Stage(viewPort, _game.batch);
        UIStage.getViewport().setScreenBounds(Graphics.Width3DScreen, 0, Graphics.Width2DScreen - 1, Graphics.Height2DScreen - 1);
        UIStage.getViewport().apply();
        UIStage.setDebugAll(false);
        _gameManager = pGameManager;
        _skin = new Skin(Gdx.files.internal("skin/plain-james-ui.json"));
        saveSplines = new TextButton("Save Course", _skin);
        saveSplines.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                CourseManager.saveCourseSpline();
                _gameManager.saveBallAndHolePos();
                CourseManager.reWriteCourse();
            }
        });
        _splineEdit = new CheckBox("Spline Editor", _skin);
        _changeBallPos = new CheckBox("Change Ball Position", _skin);
        _changeHolePos = new CheckBox("Change Hole Position", _skin);
        _addObjects = new CheckBox("Add Objects", _skin);
        _eraseObject = new CheckBox("Erase Objects", _skin);
        initUI();
        _activaBall = _gameManager.getBall();
        updatePlayerActive();

    }

    private void initUI() {
        Skin skin = new Skin(Gdx.files.internal("skin/plain-james-ui.json"));
        table = new Table();

        table.setFillParent(true);
        table.center();

        speedText = new Label("Speed: " + _gameManager.getBall().getVelocity().getSpeed(), skin);

        int x = (int) _gameManager.getBall().getPosition().x;
        int y = (int) _gameManager.getBall().getPosition().y;
        int height = (int) CourseManager.calculateHeight(x, y);
        ball_position = new Label("Ball Position\n" + "height: " + height + "\nx:" + x + " y: " + y, skin);

        _widthObstacle = new Slider(20, 400, 10, false, skin);
        _deepObstacle = new Slider(20, 400, 10, false, skin);
        _heightObstacle = new Slider(20, 400, 10, false, skin);
        _widthObstacle.setValue(80);
        Label obstacleDim = new Label("Obstacle Dimensions:", skin);
        _widthObstacleLabel = new Label("Width: 80", skin);
        _deepObstacle.setValue(80);
        _deepObstacleLabel = new Label("Deep: 80", skin);
        _heightObstacle.setValue(80);
        _heightObstacleLabel = new Label("Height: 80", skin);
        _keepRatio = new CheckBox("Same Dimensions", skin);
        _keepRatio.setChecked(true);
        _widthObstacle.setVisible(true);
        turnCount = new Label("Turns: " + _gameManager.getTurns(), skin);
        maxSpeed = new Label("Max speed: " + CourseManager.getMaxSpeed() + "\n", skin);
        simulationCounter = new Label("Nr of simulations: " + GameManager.simulationCounter, skin);
        if (_gameManager.getMode() == 3) {
            controls = new Label("Controls: \nG - simple GA \nS - shortest MazeBot \nA - advanced MazeBot\nX - PledgeBot\" ", skin);
        } else {
            controls = new Label("Controls: \nInput velocity with the \nmouse or  press I", skin);
        }
        ButtonGroup buttonGroup = new ButtonGroup(_splineEdit, _changeBallPos, _changeHolePos, _addObjects, _eraseObject);
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(0);
        buttonGroup.uncheckAll();
        table.setDebug(false);
        table.add(maxSpeed).colspan(2);
        table.row();
        table.add(speedText).colspan(2);
        table.row();
        table.add(ball_position).colspan(2);
        table.row();
        table.add(turnCount).colspan(2);
        table.row();
        table.add(simulationCounter).colspan(2);
        table.row();
        table.add(controls).colspan(2);
        table.row();
        table.add(saveSplines).colspan(2);
        table.row();
        table.add(_splineEdit).colspan(2);
        table.row();
        table.add(_changeBallPos).colspan(2);
        table.row();
        table.add(_changeHolePos).colspan(2);
        table.row();
        table.add(_addObjects).colspan(2);
        table.row();
        table.add(_eraseObject).colspan(2);
        table.row();
        table.add(obstacleDim).colspan(2);
        table.row();
        table.add(_keepRatio).colspan(2);
        table.row();
        table.add(_widthObstacleLabel).align(Align.right);
        table.add(_widthObstacle).align(Align.left);
        table.row();
        table.add(_deepObstacleLabel).align(Align.right);
        table.add(_deepObstacle).align(Align.left);
        table.row();
        table.add(_heightObstacleLabel).align(Align.right);
        table.add(_heightObstacle).align(Align.left);
        UIStage.addActor(table);

    }

    private void updatePlayerActive() {
        GraphicsComponent graphBall = _activaBall.getGraphicComponent();
        graphBall.setColor(Color.WHITE);
        _activaBall = _gameManager.getPlayer();
        GraphicsComponent graphBall2 = _activaBall.getGraphicComponent();
        graphBall2.setColor(Color.PURPLE);
    }


    public InputProcessor getUIInputProcessor() {
        return UIStage;
    }

    private void updateSliders() {
        if (_widthObstacle.isDragging()) {
            _widthObstacleLabel.setText("Width: " + _widthObstacle.getValue());
            if (_keepRatio.isChecked())
                updateSlidersWithRatio(_widthObstacle.getValue());
        }
        if (_deepObstacle.isDragging()) {
            _deepObstacleLabel.setText("Deep: " + _deepObstacle.getValue());
            if (_keepRatio.isChecked())
                updateSlidersWithRatio(_deepObstacle.getValue());
        }
        if (_heightObstacle.isDragging()) {
            _heightObstacleLabel.setText("Height: " + _heightObstacle.getValue());
            if (_keepRatio.isChecked())
                updateSlidersWithRatio(_heightObstacle.getValue());
        }
    }

    public Vector3 getObstacleDimensions() {
        return new Vector3(_widthObstacle.getValue(), _deepObstacle.getValue(), _heightObstacle.getValue());
    }

    private void updateSlidersWithRatio(float val) {
        _widthObstacle.setValue(val);
        _widthObstacleLabel.setText("Width: " + _widthObstacle.getValue());
        _deepObstacle.setValue(val);
        _deepObstacleLabel.setText("Deep: " + _deepObstacle.getValue());
        _heightObstacle.setValue(val);
        _heightObstacleLabel.setText("Height: " + _heightObstacle.getValue());

    }

    public void render() {
        Ball ball = _gameManager.getPlayer();

        maxSpeed.setText("Max speed: " + CourseManager.getMaxSpeed());
        speedText.setText("Speed: " + (int) (_gameManager.getBall().getVelocity().getSpeed()));
        int x = (int) ball.getPosition().x;
        int y = (int) ball.getPosition().y;
        int height = (int) CourseManager.calculateHeight(x, y);
        ball_position.setText("Ball Position\n" + "height: " + height + "\nx:" + x + " y: " + y);
        turnCount.setText("Turns: " + _gameManager.getTurns());
        simulationCounter.setText("Nr of simulations: " + GameManager.simulationCounter);
        updateSliders();
        UIStage.draw();
        UIStage.act();

    }

    public boolean isSplineEditActive() {
        return _splineEdit.isChecked();
    }

    public boolean isChangeBallActive() {
        return _changeBallPos.isChecked();
    }

    public boolean isChangeHoleActive() {
        return _changeHolePos.isChecked();
    }

    public boolean isAddObjectsActive() {
        return _addObjects.isChecked();
    }


    public boolean isEraseObjectsActive() {
        return _eraseObject.isChecked();
    }

    public void dispose() {
        _gameManager = null;
        view = null;
        UIStage.dispose();


    }
}
