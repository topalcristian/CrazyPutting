package com.golf.game.GameLogic;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.golf.game.Components.Colliders.BoxCollide;
import com.golf.game.Components.Colliders.ColliderComponent;
import com.golf.game.Components.Colliders.CollisionManager;
import com.golf.game.Components.Graphics.BoxGraphicsComponent;
import com.golf.game.Components.Graphics.CustomGraphicsComponent;
import com.golf.game.Components.Graphics.GraphicsComponent;
import com.golf.game.GameLogic.Splines.BiCubicSpline;
import com.golf.game.GameLogic.Splines.Spliner;
import com.golf.game.GameObjects.GUI;
import com.golf.game.GameObjects.GameObject;
import com.golf.game.GameObjects.SplinePoint;
import com.golf.game.GraphicsGenerator.TerrainGenerator;

import java.util.List;

public class TerrainEditor extends InputAdapter {

    double[][] p = {{-50}, {50}, {50}, {50},
            {0}, {0}, {0}, {0},
            {0}, {0}, {0}, {0},
            {0}, {0}, {0}, {0}};
    private ModelInstance _terrainInstance;
    //3D dragging
    private int _selecting = -1;
    private Camera _cam3D;
    private boolean _splineEdit = false;
    private boolean _changeBall = false;
    private boolean _changeHole = false;
    private boolean _addObjects = false;
    private boolean _eraseObject = false;
    private Vector2 _buttonDownCoord = new Vector2();
    private Vector2 _buttonDragCoord = new Vector2();
    private boolean _dragging = false;
    private SplinePoint[][] _splinePoints;
    private float _sPointRadius = 40f;
    private GameObject _draggingPoint;
    private boolean _splineEnabled;
    private GUI _gui;
    private GameManager _observer;

    public TerrainEditor(Camera pCam3D, boolean pSplines) {
        _splineEnabled = pSplines;
        _cam3D = pCam3D;
        GameObject terrain = new GameObject();
        GraphicsComponent terrainGraphics = initTerrain();
        terrain.addGraphicComponent(terrainGraphics);
        _terrainInstance = terrainGraphics.getInstance();
    }

    public static void swapYandZ(Vector3 vec) {
        Vector3 cache = new Vector3(vec);
        vec.z = cache.y;
        vec.y = cache.z;
    }

    public void setGUI(GUI pGUI) {
        _gui = pGUI;
    }


    private GraphicsComponent initTerrain() {
        GraphicsComponent terrainGraphics;
        if (_splineEnabled) {
            terrainGraphics = new CustomGraphicsComponent(TerrainGenerator.generateModelTerrain(true, p));
            BiCubicSpline spline = TerrainGenerator.getSpline();
            _splinePoints = spline.getSplinePoints();
            CourseManager.setBiCubicSpline(spline);//change CourseManager to use splines instead of formula height
        } else {
            terrainGraphics = new CustomGraphicsComponent(TerrainGenerator.generateModelTerrain());
        }
        return terrainGraphics;
    }

    public void addObserver(GameManager pObserver) {
        _observer = pObserver;
    }

    public void updateGUIState(boolean pSpline, boolean ball, boolean hole, boolean addObj, boolean eraseObj) {
        if (_splineEnabled) {
            showSplinePoints();
            _splineEdit = pSpline;
            _changeBall = ball;
            _changeHole = hole;
            _addObjects = addObj;
            _eraseObject = eraseObj;
        }
    }

    private void showSplinePoints() {
        for (SplinePoint[] splinePoint : _splinePoints) {
            for (int j = 0; j < _splinePoints[0].length; j++) {
                splinePoint[j].enabled = _splineEdit;
            }
        }
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (_splineEdit) {
            _dragging = false;
            _draggingPoint = intersectSplinePoint(screenX, screenY);
            _buttonDownCoord.set(screenX, screenY);
            if (_draggingPoint != null) {
                GraphicsComponent gp = _draggingPoint.getGraphicComponent();
                gp.setColor(Color.FIREBRICK);
                return true;
            }
        }
        return false;
    }

    private boolean touchDownLogicMovedToTouchUp(int screenX, int screenY, int pointer, int button) {

        if (_changeBall) {
            Vector3 pos = getObject(screenX, screenY);
            changeBallPos(pos);
        } else if (_changeHole) {
            Vector3 pos = getObject(screenX, screenY);
            changeHolePos(pos);
        } else if (_addObjects) {

            Vector3 pos = getObject(screenX, screenY);
            addBox(pos);
        } else if (_eraseObject) {
            eraseObject(screenX, screenY);

        }
        return _selecting >= 0;
    }


    private void changeBallPos(Vector3 pPos) {
        if (pPos == null) return;
        Vector3 cachePos = new Vector3(pPos);
        pPos.y = cachePos.z;
        pPos.z = cachePos.y;
        _observer.updateBallPos(pPos);
    }

    private void changeHolePos(Vector3 pPos) {
        if (pPos == null) return;
        Vector3 cachePos = new Vector3(pPos);
        pPos.y = cachePos.z;
        pPos.z = cachePos.y;
        _observer.updateHolePos(pPos);
    }

    private void addBox(Vector3 pPos) {
        if (pPos == null) return;
        Vector3 cachePos = new Vector3(pPos);
        pPos.y = cachePos.z;
        pPos.z = cachePos.y;
        GameObject obstacle = new GameObject(pPos);
        Vector3 dim = _gui.getObstacleDimensions();
        obstacle.addGraphicComponent(new BoxGraphicsComponent(dim, Color.DARK_GRAY));
        BoxCollide box = new BoxCollide(pPos, new Vector3(_gui.getObstacleDimensions()));
        obstacle.addColliderComponent(box);
        CourseManager.addObstacle(obstacle);

    }

    private void eraseObject(int screenX, int screenY) {
        Ray ray = _cam3D.getPickRay(screenX, screenY, 0, 0, _cam3D.viewportWidth, _cam3D.viewportHeight);
        List<GameObject> obstacles = CourseManager.getActiveCourse().getObstaclesList();
        for (GameObject obj : obstacles) {
            ColliderComponent col = obj.getColliderComponent();
            Vector3 pos = new Vector3(obj.getPosition());
            swapYandZ(pos);
            if (Intersector.intersectRayBoundsFast(ray, pos, col.getDimensions())) {
                obj.enabled = false;
                obstacles.remove(col);
                CourseManager.getActiveCourse().deleteObstacle(obj);
                CollisionManager.deleteCollider(col);
                return;
            }
        }
    }

    private SplinePoint intersectSplinePoint(int screenX, int screenY) {
        Ray ray = _cam3D.getPickRay(screenX, screenY, 0, 0, _cam3D.viewportWidth, _cam3D.viewportHeight);
        Vector3 intersect = new Vector3();
        for (SplinePoint[] splinePoint : _splinePoints) {
            for (int j = 0; j < _splinePoints[0].length; j++) {
                Vector3 pos = new Vector3(splinePoint[j].getPosition());
                swapYandZ(pos);
                boolean found = Intersector.intersectRaySphere(ray, pos, _sPointRadius, intersect);
                if (found)
                    return splinePoint[j];
            }
        }
        return null;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!_splineEdit) return false;
        Vector2 prevDraggingPos;
        if (_dragging)
            prevDraggingPos = new Vector2(_buttonDragCoord); //If is first frame of drag then prev distance is 0
        else prevDraggingPos = new Vector2(screenX, screenY);

        _buttonDragCoord.set(screenX, screenY);
        float dist = _buttonDragCoord.dst2(_buttonDownCoord);
        if (dist > 2 && _draggingPoint != null)
            _dragging = true;
        if (_dragging) updateDraggingPoint(prevDraggingPos);
        return _dragging;
    }

    private void updateDraggingPoint(Vector2 prevDraggingPos) {
        SplinePoint spl = (SplinePoint) _draggingPoint;
        spl.setHeight(_draggingPoint.getPosition().z + prevDraggingPos.y - _buttonDragCoord.y);

        BiCubicSpline spline = TerrainGenerator.getSpline();
        for (Spliner sp : spline.getSplineList()) {
            spline.updateSplineCoeff(sp);
        }
        updateTerrain();
    }

    private void updateTerrain() {
        BiCubicSpline spline = TerrainGenerator.getSpline();
        List<Spliner> infoList = spline.getSplineList();
        for (int i = 0; i < infoList.size(); i++) {
            Mesh changinMesh = infoList.get(i).getNode().parts.get(0).meshPart.mesh;
            float[] vertices = new float[changinMesh.getNumVertices() * changinMesh.getVertexSize() / 4];
            vertices = changinMesh.getVertices(vertices);

            updateVertices(vertices, spline, i, infoList.get(i));
            changinMesh.updateVertices(0, vertices);
        }
    }

    private void updateVertices(float[] vert, BiCubicSpline spline, int pNode, Spliner info) {

        List<Vector3> triangles = TerrainGenerator.triangleList;
        int count = 0;
        int offset = pNode * 9600;//9600 vertices per node

        for (int i = 1; i < vert.length; i += 7) {
            boolean aboveWater = vert[i] >= -1;
            vert[i] = spline.getHeightAt(new Vector2(vert[i - 1], vert[i + 1]));
            if (vert[i] < -1 && aboveWater)
                vert[i + 2] = Color.toFloatBits(Color.BLUE.r, Color.BLUE.g, Color.BLUE.b, Color.BLUE.a);
            else if (!aboveWater && vert[i] >= -1)
                vert[i + 2] = Color.toFloatBits(Color.GREEN.r, Color.GREEN.g, Color.GREEN.b, Color.GREEN.a);
            triangles.get(count + offset).y = vert[i];
            count++;
        }

    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        touchDownLogicMovedToTouchUp(screenX, screenY, pointer, button);
        if (!_splineEdit) return false;
        if (_dragging) {
            GraphicsComponent gp = _draggingPoint.getGraphicComponent();
            System.out.println(_draggingPoint.getPosition());
            gp.setColor(Color.RED);
            _observer.updateObjectPos();
            updateObstaclesHeight();
            return true;
        }
        return false;
    }

    private void updateObstaclesHeight() {
        List<GameObject> obs = CourseManager.getActiveCourse().getObstaclesList();
        for (GameObject ob : obs) {
            ob.setPosition(new Vector3(ob.getPosition().x, ob.getPosition().y, CourseManager.calculateHeight(ob.getPosition().x, ob.getPosition().y)));
        }
    }

    public Vector3 getObject(int screenX, int screenY) {
        Ray ray = _cam3D.getPickRay(screenX, screenY, 0, 0, _cam3D.viewportWidth, _cam3D.viewportHeight);
        Vector3 position = new Vector3();
        _terrainInstance.transform.getTranslation(position);
        Vector3 intersectPos = new Vector3();
        if (Intersector.intersectRayTriangles(ray, TerrainGenerator.triangleList, intersectPos)) {
            return intersectPos;
        }

        return null;
    }

    public void dispose() {
        _terrainInstance = null;
        _draggingPoint = null;
        _cam3D = null;
        _gui = null;
        _observer = null;
        _splinePoints = null;
    }
}
