package com.golf.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.golf.game.FormulaParser.HeightSolver;
import com.golf.game.FormulaParser.Vector2D;
import com.golf.game.GameLogic.CourseManager;
import com.golf.game.GameObjects.Course;

import static com.golf.game.GameLogic.GraphicsManager.WINDOW_HEIGHT;
import static com.golf.game.GameLogic.GraphicsManager.WINDOW_WIDTH;

public class CourseCreatorScreen implements Screen {
    private static GolfGame game;
    private Stage stage;
    private TextButton confirmButton;
    private TextField heightText;
    private TextField frictionText;
    private TextField startTextX;
    private TextField startTextY;
    private TextField goalTextX;
    private TextField goalTextY;
    private TextField radiusText;
    private TextField maxVelocityText;
    private Label errorLabel;

    private SpriteBatch batch;
    private Sprite sprite;
    private Table table;

    public CourseCreatorScreen(GolfGame game) {
        CourseManager.loadFile("coursesSpline.txt");

        CourseManager.initObstacles();

        CourseCreatorScreen.game = game;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        Skin skin = new Skin(Gdx.files.internal("skin/plain-james-ui.json"));


        batch = game.batch;
        sprite = new Sprite(new Texture(Gdx.files.internal("background.jpg")));
        sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Label title = new Label("Course creator", skin);
        title.setColor(Color.BLACK);
        title.setPosition(WINDOW_WIDTH / 2 - title.getWidth() / 2, WINDOW_HEIGHT * 0.8f);

        confirmButton = new TextButton("Confirm", skin);
        Vector2 buttonSize = new Vector2(200, 50);
        confirmButton.setPosition(WINDOW_WIDTH / 2 - buttonSize.x / 2, buttonSize.y * 2);
        confirmButton.setSize(buttonSize.x, buttonSize.y);
        confirmButton.addListener(new ClickListener() {

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                confirmButtonClicked();
            }
        });
        confirmButton.setColor(Color.WHITE);
        Label heightLabel = new Label("Height function", skin);
        heightText = new TextField("", skin);
        Label frictionLabel = new Label("Friction coefficient", skin);
        frictionText = new TextField("", skin);
        Label startLabelX = new Label("Start position X", skin);
        startTextX = new TextField("", skin);
        Label startLabelY = new Label("Start position Y", skin);
        startTextY = new TextField("", skin);
        Label goalLabelX = new Label("Goal position X", skin);
        goalTextX = new TextField("", skin);
        Label goalLabelY = new Label("Goal position Y", skin);
        goalTextY = new TextField("", skin);
        Label radiusLabel = new Label("Radius of the target", skin);
        radiusText = new TextField("", skin);
        Label maxVelocityLabel = new Label("Max velocity", skin);
        maxVelocityText = new TextField("", skin);
        errorLabel = new Label("", skin);
        errorLabel.setSize(200, 50);
        errorLabel.setPosition(300, buttonSize.y * 3 + 30);



        table = new Table();
        table.setWidth(stage.getWidth());
        table.align(Align.center | Align.top);
        table.setPosition(0, Gdx.graphics.getHeight());

        table.padTop(350);
        table.add(heightLabel);
        table.add(heightText);
        table.row();
        table.add(frictionLabel);
        table.add(frictionText);
        table.row();
        table.add(startLabelX);
        table.add(startTextX);

        table.add(startLabelY);
        table.add(startTextY);
        table.row();
        table.add(goalLabelX);
        table.add(goalTextX);
        table.add(goalLabelY);
        table.add(goalTextY);
        table.row();
        table.add(radiusLabel);
        table.add(radiusText);
        table.row();
        table.add(maxVelocityLabel);
        table.add(maxVelocityText);

        stage.addActor(errorLabel);
        stage.addActor(title);
        stage.addActor(table);
        stage.addActor(confirmButton);

    }

    public void confirmButtonClicked() {
        createCourse();
    }

    private void createCourse() {


        try {
            Course newCourse = new Course();
            newCourse.setName("Course without name D:");
            newCourse.setHeight(heightText.getText());
            newCourse.setFriction(Float.parseFloat(frictionText.getText()));
            Vector3 ball_start_position = new Vector3(Float.parseFloat(startTextX.getText()), Float.parseFloat(startTextY.getText()), 0);
            newCourse.setBallStartPos(ball_start_position, 0);
            Vector3 goalStartPosition = new Vector3(Float.parseFloat(goalTextX.getText()), Float.parseFloat(goalTextY.getText()), 0);
            newCourse.setGoalPosition(goalStartPosition, 0);
            newCourse.setGoalRadius(Float.parseFloat(radiusText.getText()));
            newCourse.setMaxSpeed(Float.parseFloat(maxVelocityText.getText()));
            if (!isBallOrGoalUnderWater(ball_start_position, goalStartPosition)) {
                CourseManager.addCourseToList(newCourse);
                CourseManager.setActiveCourseWithIndex(CourseManager.getCourseAmount() - 1);
                CourseManager.reWriteCourse();
                Gdx.app.getApplicationListener().dispose();
                game.setScreen(new MenuScreen(game));
            }
        } catch (Exception e) {
            System.out.println("Error saving course... Going Back to Menu");
            System.out.println(e.toString());
            errorLabel.setText("You must input values in text fields");
            confirmButton.addListener(new ClickListener() {

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    confirmButtonClicked();
                }
            });
        }
    }

    private boolean isBallOrGoalUnderWater(Vector3 pBallPos, Vector3 pGoalPos) {
        try {
            HeightSolver parser = new HeightSolver(heightText.getText());
            float resultBall = (float) parser.evaluate(new Vector2D(pBallPos.x, pBallPos.y));
            float resultGoal = (float) parser.evaluate(new Vector2D(pGoalPos.x, pGoalPos.y));
            if (resultBall < 0 || resultGoal < 0) {
                throw new IllegalArgumentException("Neither ball nor hole can be in water");
            } else return false;
        } catch (IllegalArgumentException e) {
            errorLabel.setText("Ball and hole starting position can't be in water");
            confirmButton.addListener(new ClickListener() {

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

                    confirmButtonClicked();
                }
            });
            return true;
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        sprite.draw(batch);
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

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
        stage.dispose();
    }

}
