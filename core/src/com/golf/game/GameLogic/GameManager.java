package com.golf.game.GameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.golf.game.Bot.*;
import com.golf.game.Components.Colliders.CollisionManager;
import com.golf.game.Components.Colliders.SphereCollide;
import com.golf.game.Components.Graphics.SphereGraphicsComponent;
import com.golf.game.GameObjects.Ball;
import com.golf.game.GameObjects.Hole;
import com.golf.game.Others.InputData;
import com.golf.game.Others.Velocity;
import com.golf.game.Parser.ReadAndAnalyse;
import com.golf.game.Physics.Physics;
import com.golf.game.Screens.GolfGame;

import java.util.ArrayList;

public class GameManager {
    public static int allowedOffset = 0;
    public static int simulationCounter;
    public static String mazeBotType = "";
    private Ball _ball;
    private Hole _hole;
    private GolfGame _game;
    private int _turns;
    private int _mode;
    private Ball Ball;
    private Hole Hole;
    private float[][] allInput;
    private ArrayList<Velocity> mazeVelocities = new ArrayList<>();


    public GameManager(GolfGame pGame, int pMode) {
        _mode = pMode;
        _game = pGame;

        if (_mode == 2)
            ReadAndAnalyse.calculate("myFile.txt");
        initGameObjects();
        _turns = 0;
        simulationCounter = 0;
        Physics.updateCoefficients();
    }

    public static boolean isBallInTheHole(Ball ball, Hole hole) {
        return Math.sqrt(Math.pow(ball.getPosition().x - hole.getPosition().x, 2) + Math.pow((ball.getPosition().y - hole.getPosition().y), 2) + Math.pow((ball.getPosition().z - hole.getPosition().z), 2)) < hole.getRadius();
    }

    private void initGameObjects() {

        allInput = new float[1][2];
        CourseManager.initObstacles();

        if (Ball != null)
            Ball.destroy();
        Ball = new Ball((CourseManager.getStartPosition(0)));
        Hole = new Hole((int) CourseManager.getActiveCourse().getGoalRadius(), (CourseManager.getGoalStartPosition(0)));

        int radius = 20;
        Ball.addGraphicComponent(new SphereGraphicsComponent(radius, Color.WHITE));
        SphereCollide sphere = new SphereCollide(CourseManager.getStartPosition(0), 10);
        Ball.addColliderComponent(sphere);
        Hole.addGraphicComponent(new SphereGraphicsComponent(radius * 2.0f, Color.BLACK));
        _ball = Ball;
        _hole = Hole;

    }


    public void update(float delta) {
        if (delta > 0.03) {
            delta = 0.00166f;
        }
        delta = 1 / 60f;
        if (mazeVelocities.size() == 0) {

            handleInput(_game.input);
        } else {

            if (!_ball.isMoving()) {
                _ball.fix(true);
                if (Float.isNaN(_ball.getPosition().x)) {
                    _ball.setPosition(CourseManager.getStartPosition(0));
                    _ball.setVelocity(0, 0);
                }
                _ball.setVelocity(mazeVelocities.get(0).speed, mazeVelocities.get(0).angle);
                _ball.fix(false);
                mazeVelocities.remove(0);
                increaseTurnCount();
            }
        }

        Physics.physics.update();
        CollisionManager.update();
        updateGameLogic(delta);


    }

    public void updateGameLogic(float pDelta) {
        if (isBallInTheHole(_ball, _hole) && _ball.isSlow()) {
            ballIsDone(_ball);
        }

    }

    private void ballIsDone(Ball ball) {

        ball.setVelocityComponents(0.0001f, 0.0001f);
        ball.fix(true);
        ball.enabled = false;
        Physics.physics.removeMovableObject(ball);
    }


    public void handleInput(InputData input) {
        if (_mode == 1) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.I) && !_ball.isMoving()) {
                Gdx.input.getTextInput(input, "Input data", "", "Input speed and direction separated with space");
            }
            if (input.getText() != null) {
                try {
                    String[] data = input.getText().split(" ");
                    float speed = Float.parseFloat(data[0]);
                    float angle = Float.parseFloat(data[1]);
                    allInput[0][0] = speed;
                    allInput[0][1] = angle;
                    input.clearText();
                    checkConstrainsAndSetVelocity(allInput);

                } catch (NumberFormatException e) {
                    Gdx.app.error("Exception: ", "You must input numbers");
                    e.getStackTrace();
                }
            }
        } else if (_mode == 2) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
                if (!_ball.isMoving() && _turns < ReadAndAnalyse.getN()) {
                    _ball.setVelocity(ReadAndAnalyse.getResult()[_turns][0], ReadAndAnalyse.getResult()[_turns][1]);
                    _ball.fix(false);
                    increaseTurnCount();
                }
            }
        } else if (_mode == 3) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.G) && !_ball.isMoving()) {
                GeneticAlgorithm GA = new GeneticAlgorithm(_hole, CourseManager.getActiveCourse(), CourseManager.getStartPosition(0), false);
                GA.runGenetic();
                Ball b = GA.getBestBall();
                float speed = b.getVelocityGA().speed;
                float angle = b.getVelocityGA().angle;
                _ball.setVelocity(speed, angle);
                _ball.fix(false);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.S) && !_ball.isMoving()) {
                mazeBotType = "simple";
                chooseMazeBot();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.A) && !_ball.isMoving()) {
                mazeBotType = "advanced";
                chooseMazeBot();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.X) && !_ball.isMoving()) {
                mazeBotType = "ss";
                chooseMazeBot();
            }
        }
    }

    public void shootBallFromGameScreen3DInput(float[][] input) {
        checkConstrainsAndSetVelocity(input);

    }

    public void chooseMazeBot() {
        allowedOffset = 30;
        int startX = Math.round(CourseManager.getStartPosition(0).x);
        int startY = Math.round(CourseManager.getStartPosition(0).y);
        Map<Node> nodeMap = new Map<>(2000, 2000, new ExampleFactory());
        ArrayList<Node> path = (ArrayList<Node>) nodeMap.findPath(startX, startY);

        if (path != null) {
            MazeBot mazeBot = new MazeBot(_ball, _hole, CourseManager.getActiveCourse(), path, nodeMap);
            switch (mazeBotType) {
                case "simple":
                    mazeVelocities = mazeBot.runSimpleMazeBot();
                    _ball.fix(false);
                    break;
                case "advanced":
                    mazeVelocities = mazeBot.runAdvancedMazeBot();
                    _ball.fix(false);
                    break;
                case "ss":
                    PledgeBot pledgeBot = new PledgeBot(_ball, _hole, CourseManager.getActiveCourse(), path, nodeMap);
                    pledgeBot.move();
                    _ball.fix(false);
                    break;
                default:
                    Gdx.app.log("Log", "Error: No bot was started");
                    break;
            }
        }
        _ball.setPosition(CourseManager.getStartPosition(0));
    }

    public boolean isGameWon() {
        return isBallInTheHole(Ball, Hole) && Ball.isSlow();
    }

    public Ball getPlayer() {
        return Ball;
    }

    public void checkConstrainsAndSetVelocity(float[][] input) {
        for (float[] floats : input) {
            float speed = checkMaxSpeedConstrain(floats[0]);
            float angle = floats[1];
            if (speed == 0) {
                speed = 0.000001f;
            }

            _ball.setVelocity(speed, angle);
            _ball.fix(false);

        }

    }

    public float checkMaxSpeedConstrain(float speed) {
        if (speed > CourseManager.getMaxSpeed()) {
            speed = CourseManager.getMaxSpeed();
        }
        return speed;
    }

    public void increaseTurnCount() {
        _turns++;
    }

    public Ball getBall() {
        return _ball;
    }

    public int getTurns() {
        return _turns;
    }



    public void saveBallAndHolePos() {

        CourseManager.getActiveCourse().setBallStartPos(Ball.getPosition(), 0);
        CourseManager.getActiveCourse().setGoalPosition(Hole.getPosition(), 0);

    }

    public void updateObjectPos() {

        Ball _ball = Ball;
        Hole _hole = Hole;
        _ball.getPosition().z = CourseManager.calculateHeight(_ball.getPosition().x, _ball.getPosition().y);
        _hole.getPosition().z = CourseManager.calculateHeight(_hole.getPosition().x, _hole.getPosition().y);

    }

    public void updateBallPos(Vector3 pos) {
        Ball.setPosition(pos);

    }

    public boolean BallIsMoving() {

        return Ball.isMoving();
    }

    public void updateHolePos(Vector3 pos) {
        Hole.setPosition(pos);
    }

    public int getMode() {
        return _mode;
    }


}
