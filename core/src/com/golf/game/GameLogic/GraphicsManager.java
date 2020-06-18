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
    private static List<GraphicsComponent> _graphicsComponentList = new ArrayList<GraphicsComponent>();
    private static Environment _environment;


    public static void addGraphics3DComponent(GraphicsComponent pGC) {
        _graphicsComponentList.add(pGC);
    }

    public static void set3DEnvironment(Environment pEnvironment) {
        _environment = pEnvironment;
    }

    public static List<GraphicsComponent> getAllGraphics3DComponents() {
        return _graphicsComponentList;
    }


    public static void render3D(ModelBatch pBatch, Camera pCam3D) {
        pBatch.begin(pCam3D);
        Gdx.gl.glViewport(0, 0, WINDOW_WIDTH - 300, Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
        for (int i = 0; i < _graphicsComponentList.size(); i++) {
            _graphicsComponentList.get(i).render(pBatch, _environment);
        }
        pBatch.end();
    }

    public static void deleteGraphicsComponent(GraphicsComponent comp) {
        _graphicsComponentList.remove(comp);
    }

    public static void clearGraphicsComponents() {
        for (GraphicsComponent g : _graphicsComponentList) {
            g.dispose();
            g.get_owner().deleteGraphicsComponent();

        }
        _graphicsComponentList.clear();
    }
}

