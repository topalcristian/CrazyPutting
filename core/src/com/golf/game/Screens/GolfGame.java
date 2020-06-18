package com.golf.game.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.golf.game.GameLogic.GraphicsManager;
import com.golf.game.Others.InputData;

public class GolfGame extends Game {

    public SpriteBatch batch;
    public BitmapFont font;
    public InputData input;
    public Environment environment;
    public ModelBatch batch3D;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(1.1f);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        input = new InputData();
        this.setScreen(new MenuScreen(this));
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.7f, 1f));

        //environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.2251f, 0.4f, 0.72f, 0.1f));
        environment.add(new DirectionalLight().set(0.2251f, 0.4f, 0.072f, -.3f, -.3f, -.2f));
        batch3D = new ModelBatch();
        GraphicsManager.set3DEnvironment(environment);
    }

    @Override
    public void dispose() {

    }

    public void restart() {
        create();
    }

    @Override
    public void render() {
        super.render();

    }
}
