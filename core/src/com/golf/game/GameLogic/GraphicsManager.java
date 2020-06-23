package com.golf.game.GameLogic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.golf.game.Components.Graphics.GraphicsComponent;

import java.util.ArrayList;
import java.util.List;

public class GraphicsManager {
    public final static int WINDOW_WIDTH = 1920;
    public final static int WINDOW_HEIGHT = 1080;
    private static List<GraphicsComponent> graphicsComponentList = new ArrayList<>();
    private static Environment environment;


    public static void addGraphicsComponent(GraphicsComponent graphicsComponent) {
        graphicsComponentList.add(graphicsComponent);
    }

    public static void set3DEnvironment(Environment Environment) {
        environment = Environment;
    }


    public static void render3D(ModelBatch modelBatch, Camera pCam3D) {
        modelBatch.begin(pCam3D);
        Gdx.gl.glViewport(0, 0, WINDOW_WIDTH - 300, Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
        for (GraphicsComponent graphicsComponent : graphicsComponentList) {
            graphicsComponent.render(modelBatch, environment);
        }
        modelBatch.end();
    }

    public static void deleteGraphicsComponent(GraphicsComponent comp) {
        graphicsComponentList.remove(comp);
    }

    public static void clearGraphicsComponents() {
        for (GraphicsComponent g : graphicsComponentList) {
            g.dispose();
            g.getOwner().deleteGraphicsComponent();

        }
        graphicsComponentList.clear();
    }
}

