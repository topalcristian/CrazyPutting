package com.golf.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.golf.game.GameLogic.CourseManager;

import static com.golf.game.GameLogic.GraphicsManager.WINDOW_HEIGHT;
import static com.golf.game.GameLogic.GraphicsManager.WINDOW_WIDTH;

/**
 * Screen in which the user can choose from a variety of already defined courses(with properites such as height function,
 * friction coefficient etc.) or click a button to create his own course.
 */
public class ChooseCoursesScreen implements Screen {
    private static GolfGame game;
    private int _mode;
    private Stage stage;
    private SelectBox<String> selectBox;
    private Skin skin;
    private Label heightValue;
    private Label frictionValue;
    private Label startValue;
    private Label goalValue;
    private Label radiusValue;
    private Label maxVelocityValue;
    private TextButton confirmButton;
    private Label splines;
    private SpriteBatch batch;
    private Sprite sprite;
    private Table table;

    public ChooseCoursesScreen(GolfGame game, int pMode) {

        CourseManager.loadFile("coursesSpline.txt");
        ChooseCoursesScreen.game = game;
        _mode = pMode;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("skin/plain-james-ui.json"));

        /*
            Background
         */
        batch = game.batch;
        sprite = new Sprite(new Texture(Gdx.files.internal("background.jpg")));
        sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Label label = new Label("Choose course (Click the name)", skin);
        Vector2 labelSize = new Vector2(50, 50);
        label.setSize(labelSize.x, labelSize.y);
        label.setPosition(200, WINDOW_HEIGHT * 0.9f - 100);


        selectBox = new SelectBox<String>(skin);
        selectBox.setPosition(300, WINDOW_HEIGHT * 0.9f - 130);
        Vector2 selectBoxSize = new Vector2(200, 50);
        selectBox.setSize(selectBoxSize.x, selectBoxSize.y);
        String[] boxItems = new String[CourseManager.getCourseAmount()];
        for (int i = 0; i < CourseManager.getCourseAmount(); i++) {
            boxItems[i] = "Course " + i;
        }
        selectBox.setItems(boxItems);


        selectBox.addListener(new EventListener() {
                                  @Override
                                  public boolean handle(Event event) {
                                      if (event instanceof ChangeListener.ChangeEvent) {
                                          updateCourseInfo();
                                      }
                                      return true;
                                  }
                              }
        );


        Label courseProperties = new Label("Course properties:", skin);
        Label heightLabel = new Label("Height function", skin);
        heightLabel = new Label("Spline Mode", skin);
        Label frictionLabel = new Label("Friction coefficient", skin);
        Label startLabel = new Label("Start position", skin);
        Label goalLabel = new Label("Goal position", skin);
        Label radiusLabel = new Label("Radius of the target \t", skin);
        Label maxVelocityLabel = new Label("Max velocity", skin);


        heightValue = new Label(selectBox.getSelected(), skin);
        heightValue = new Label("", skin);
        frictionValue = new Label(selectBox.getSelected(), skin);
        startValue = new Label(selectBox.getSelected(), skin);
        goalValue = new Label(selectBox.getSelected(), skin);
        radiusValue = new Label(selectBox.getSelected(), skin);
        maxVelocityValue = new Label(selectBox.getSelected(), skin);

        splines = new Label("Spline info", skin);
        Label splineText = new Label("Spline Points Height", skin);
        TextButton createCourseButton = new TextButton("Create course", skin);
        createCourseButton.addListener(new ClickListener() {

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                setCourseCreator();
            }
        });


        /*
            Add previously initialized labels to a table.
         */
        updateCourseInfo();
        table = new Table();

        table.setWidth(stage.getWidth());
        table.align(Align.center | Align.top);
        table.setPosition(0, Gdx.graphics.getHeight());
        table.padTop(150);
        table.add(courseProperties).align(Align.left);
        table.row();
        table.add(heightLabel).align(Align.left);
        table.add(heightValue);
        table.row();
        table.add(frictionLabel).align(Align.left);
        table.add(frictionValue);
        table.row();
        table.add(startLabel).align(Align.left);
        table.add(startValue);
        table.row();
        table.add(goalLabel).align(Align.left);
        table.add(goalValue);
        table.row();
        table.add(radiusLabel).align(Align.left);
        table.add(radiusValue);
        table.row();
        table.add(maxVelocityLabel).align(Align.left);
        table.add(maxVelocityValue);
        table.row();
        table.add(createCourseButton).align(Align.left);

            table.row();
            table.add(splineText).align(Align.left);
            table.add(splines).align(Align.left);

        /*
            Add confirmation button.
         */
        confirmButton = new TextButton("Confirm", skin);
        Vector2 buttonSize = new Vector2(200, 50);
        confirmButton.setPosition(WINDOW_WIDTH / 2 - buttonSize.x / 2 + 100, buttonSize.y * 2 - 50);
        confirmButton.setSize(buttonSize.x, buttonSize.y);
        confirmButton.addListener(new ClickListener() {

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                confirmButtonClicked();
            }
        });
        confirmButton.setColor(Color.WHITE);

        /*
            Add all actors to stage so that they are displayed.
         */
        stage.addActor(table);
        stage.addActor(label);
        stage.addActor(confirmButton);
        stage.addActor(selectBox);
    }

    @Override
    public void show() {

    }

    public void setCourseCreator() {
        Gdx.app.getApplicationListener().dispose();
        game.setScreen(new CourseCreatorScreen(game));
    }

    /*
        update labels if user clicked on a new course.
     */
    public void updateCourseInfo() {

        frictionValue.setText(CourseManager.getCourseWithIndex(selectBox.getSelectedIndex()).getFriction() + "");
        startValue.setText(CourseManager.getCourseWithIndex(selectBox.getSelectedIndex()).getStartBall(0).toString());
        goalValue.setText(CourseManager.getCourseWithIndex(selectBox.getSelectedIndex()).getGoalPosition(0).toString());
        radiusValue.setText(CourseManager.getCourseWithIndex(selectBox.getSelectedIndex()).getGoalRadius() + "");
        maxVelocityValue.setText(CourseManager.getCourseWithIndex(selectBox.getSelectedIndex()).getMaxSpeed() + "");
        splines.setText(CourseManager.getCourseWithIndex(selectBox.getSelectedIndex()).toStringSplinePointsMatrix());

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


    public void confirmButtonClicked() {

        CourseManager.setActiveCourseWithIndex(selectBox.getSelectedIndex());
        if (selectBox.getSelectedIndex() != CourseManager.getIndexActive())
            CourseManager.reParseHeightFormula(selectBox.getSelectedIndex());

        Gdx.app.getApplicationListener().dispose();
        game.setScreen(new GameScreen3D(game, _mode));

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
        batch.dispose();


    }
}