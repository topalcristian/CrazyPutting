package com.crazy_putting.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.crazy_putting.game.Physics.Euler;
import com.crazy_putting.game.Physics.Heuns3;
import com.crazy_putting.game.Physics.RK4;
import com.crazy_putting.game.Physics.Verlet;

public class MenuScreen implements Screen {

    public static MenuScreen finalMenu;
    public static boolean Mode3D = true; //TODO:Check if its better to implement this somewhere else
    public static boolean Spline3D = true; //TODO:Check if its better to implement this somewhere else
    public static boolean Multiplayer = false;
    public static boolean AI = false;
    private GolfGame golfGame;
    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;
    private Sprite sprite;
    private Table table;
    private TextButton soloButton;
    private TextButton fileButton;
    private TextButton aiButton;
    private TextButton multiplePlayersButton;
    private TextButton courseCreatorButton;
    private Skin skin2;
    private TextButton buttonPhysicsV;
    private TextButton buttonPhysicsH;
    private TextButton buttonPhysicsRK;
    private TextButton buttonPhysicsE;
    private TextButton button2D;
    private TextButton button3D;
    private TextButton button3DSpline;

    public MenuScreen(final GolfGame golfGame) {

        this.golfGame = golfGame;
        finalMenu = this;
        skin = new Skin(Gdx.files.internal("skin/plain-james-ui.json"));
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // background
        batch = golfGame.batch;
        sprite = new Sprite(new Texture(Gdx.files.internal("background.jpg")));
        sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // buttons
        soloButton = new TextButton("Player mode", skin);
        soloButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.getApplicationListener().dispose();
                AI = false;
                golfGame.setScreen(new ChooseCoursesScreen(golfGame, 1));
            }
        });

        fileButton = new TextButton("File input mode", skin);
        fileButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.getApplicationListener().dispose();
                AI = false;
                golfGame.setScreen(new ChooseCoursesScreen(golfGame, 2));
            }
        });

        aiButton = new TextButton("AI player mode", skin);
        aiButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AI = true;
                Gdx.app.getApplicationListener().dispose();
                golfGame.setScreen(new ChooseCoursesScreen(golfGame, 3)); // go to "ModesScreen" screen
            }
        });

        multiplePlayersButton = new TextButton("Multiple players mode", skin);
        multiplePlayersButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Multiplayer = true;
                AI = false;
                Gdx.app.getApplicationListener().dispose();
                golfGame.setScreen(new ChooseCoursesScreen(golfGame, 4)); // go to "ModesScreen" screen
            }
        });

        courseCreatorButton = new TextButton("Create course", skin);
        courseCreatorButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.getApplicationListener().dispose();
                AI = false;
                golfGame.setScreen(new CourseCreatorScreen(golfGame)); // go to "Course creator" screen
            }
        });

        button3DSpline = new TextButton("Spline", skin, "toggle");
        button3DSpline.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Spline3D = true;
            }
        });

        ButtonGroup buttonGroup = new ButtonGroup(button3DSpline);
//next set the max and min amount to be checked
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(1);
        buttonGroup.setChecked("Spline");


        buttonPhysicsV = new TextButton("Verlet", skin, "toggle");
        buttonPhysicsV.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new Verlet();
            }
        });

        buttonPhysicsH = new TextButton("Heun's3", skin, "toggle");
        buttonPhysicsH.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new Heuns3();
            }
        });

        buttonPhysicsRK = new TextButton("RK4", skin, "toggle");
        buttonPhysicsRK.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new RK4();
            }
        });

        buttonPhysicsE = new TextButton("Euler", skin, "toggle");
        buttonPhysicsE.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new Euler();
            }
        });

        ButtonGroup buttonGroupPhysics = new ButtonGroup(buttonPhysicsE, buttonPhysicsV, buttonPhysicsH, buttonPhysicsRK);
//next set the max and min amount to be checked
        buttonGroupPhysics.setMaxCheckCount(1);
        buttonGroupPhysics.setMinCheckCount(1);
        buttonGroupPhysics.setChecked("RK4");


        Table tableDimensions = new Table();
        tableDimensions.setWidth(stage.getWidth());
        //  tableDimensions.align(Align.center|Align.top);
        tableDimensions.setPosition(400, Gdx.graphics.getHeight() - 100);
        tableDimensions.row();
        tableDimensions.add(button3D).size(100, 50);
        tableDimensions.add(button2D).size(100, 50);
        tableDimensions.add(button3DSpline).size(100, 50);


        Table tablePhysics = new Table();
        tablePhysics.setWidth(stage.getWidth());
        //  tableDimensions.align(Align.center|Align.top);
        tablePhysics.setPosition(400, Gdx.graphics.getHeight() - 200);
        tablePhysics.row();
        tablePhysics.add(buttonPhysicsE).size(100, 50);
        tablePhysics.add(buttonPhysicsV).size(100, 50);
        tablePhysics.add(buttonPhysicsH).size(100, 50);
        tablePhysics.add(buttonPhysicsRK).size(100, 50);

        // table
        table = new Table();
        table.setWidth(stage.getWidth());
        table.align(Align.center | Align.top);
        table.setPosition(0, Gdx.graphics.getHeight());

        table.padTop(150);
        table.add(soloButton).size(300, 50).padBottom(20);
        table.row();
        table.add(fileButton).size(300, 50).padBottom(20);
        table.row();
        table.add(aiButton).size(300, 50).padBottom(20);
        table.row();
        table.add(multiplePlayersButton).size(300, 50).padBottom(20);
        table.row();
        table.add(courseCreatorButton).size(300, 50);

        stage.addActor(table);
        stage.addActor(tableDimensions);
        stage.addActor(tablePhysics);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // background
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        sprite.draw(batch);
        batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
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
