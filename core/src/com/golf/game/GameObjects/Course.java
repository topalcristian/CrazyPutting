package com.golf.game.GameObjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.golf.game.Components.Colliders.BoxCollide;
import com.golf.game.Components.Colliders.ColliderComponent;
import com.golf.game.Components.Colliders.SphereCollide;
import com.golf.game.Components.Graphics.BoxGraphicsComponent;
import com.golf.game.Components.Graphics.SphereGraphicsComponent;
import com.golf.game.GameLogic.CourseManager;
import com.golf.game.Parser.ObstacleData;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private int _ID;
    private String _name;
    private String _height;
    private float _friction;
    private Vector3[] _goalPosition;
    private float _goalRadius;
    private Vector3[] _startBall;
    private float _maxSpeed;
    private float[][] _splinePoints = new float[6][6];
    private List<GameObject> _obstacles = new ArrayList<>();
    private List<ObstacleData> cacheDataList = new ArrayList<>();

    public Course() {
        _goalPosition = new Vector3[4];
        _startBall = new Vector3[4];
        for (int i = 0; i < 4; i++) {
            _goalPosition[i] = new Vector3();
            _startBall[i] = new Vector3();
        }
    }

    public int getID() {
        return _ID;
    }

    public void setID(int pID) {
        _ID = pID;
    }

    public String getName() {
        return _name;
    }

    public void setName(String pName) {
        _name = pName;
    }

    public String getHeight() {
        return _height;
    }

    public void setHeight(String pFormula) {
        _height = pFormula;
    }


    public float getFriction() {
        return _friction;
    }

    public void setFriction(float pFriction) {
        _friction = pFriction;
    }

    public void setGoalPosition(Vector3 pPos, int pIndex) {
        if (pIndex >= _goalPosition.length) throw new ArrayIndexOutOfBoundsException();
        _goalPosition[pIndex] = pPos;
    }

    public Vector3 getGoalPosition(int pIndex) {
        if (pIndex >= _goalPosition.length) throw new ArrayIndexOutOfBoundsException();
        return _goalPosition[pIndex];
    }

    public float getGoalRadius() {
        return _goalRadius;
    }

    public void setGoalRadius(float pGoalRadius) {
        _goalRadius = pGoalRadius;
    }

    public void setBallStartPos(Vector3 pPos, int pIndex) {
        if (pIndex >= _goalPosition.length) throw new ArrayIndexOutOfBoundsException();
        _startBall[pIndex] = pPos;
    }

    public Vector3 getStartBall(int pIndex) {
        if (pIndex >= _startBall.length) throw new ArrayIndexOutOfBoundsException();
        return new Vector3(_startBall[pIndex]);
    }

    public float[][] getSplinePoints() {
        return _splinePoints;
    }

    public void setSplinePoints(float[][] points) {
        _splinePoints = points;
    }

    public void initObstacles() {
        createObstacle();
    }

    public String toStringSplinePoints() {
        StringBuilder out = new StringBuilder("" + _splinePoints.length + " " + _splinePoints[0].length + " ");
        for (float[] splinePoint : _splinePoints) {
            for (int j = 0; j < _splinePoints[0].length; j++) {
                out.append(splinePoint[j]).append("  ");
            }
        }
        return out.toString();
    }


    public void addObstacleToList(GameObject pObstacle) {
        _obstacles.add(pObstacle);
    }

    public boolean checkObstaclesAt(Vector3 pPosition) {
        for (GameObject obstacle : _obstacles) {
            ColliderComponent colliderComponent = obstacle.getColliderComponent();
            if (colliderComponent instanceof SphereCollide) {
                SphereCollide sphere = (SphereCollide) colliderComponent;
                if (sphere.containsPoint(pPosition)) return true;
            } else if (colliderComponent instanceof BoxCollide) {
                BoxCollide box = (BoxCollide) colliderComponent;
                if (box.containsPointPath(pPosition)) return true;
            }
        }
        return false;
    }

    public float getMaxSpeed() {
        return _maxSpeed;
    }

    public void setMaxSpeed(float pMax) {
        _maxSpeed = pMax;
    }

    @Override
    public String toString() {
        String out = "";
        out += ("\nCOURSE" + "");
        out += ("\nID: ") + getID();
        out += ("\nName: ") + getName();
        out += ("\nHeight: ") + getHeight();
        out += ("\nFriction: ") + getFriction();
        out += ("\nGoal Pos: ") + getGoalPosition(0);
        out += ("\nGoal Pos: ") + getGoalPosition(1);
        out += ("\nGoal Pos: ") + getGoalPosition(2);
        out += ("\nGoal Pos: ") + getGoalPosition(3);
        out += ("\nGoal Radius: ") + getGoalRadius();
        out += ("\nBall Start Pos: ") + getStartBall(0);
        out += ("\nBall Start Pos: ") + getStartBall(1);
        out += ("\nBall Start Pos: ") + getStartBall(2);
        out += ("\nBall Start Pos: ") + getStartBall(3);
        out += ("\nMax Speed: ") + getMaxSpeed();
        out += ("\nSpline Points: ") + toStringSplinePoints();
        out += getObstaclesString();
        return out;
    }

    private String getObstaclesString() {
        StringBuilder out = new StringBuilder();
        for (GameObject obstacle : _obstacles) {
            ColliderComponent colliderComponent = obstacle.getColliderComponent();
            if (colliderComponent instanceof SphereCollide) {
                SphereCollide sphere = (SphereCollide) colliderComponent;
                out.append("\nCollider type 1");
                out.append("\nPosition: ").append(obstacle.getPosition());
                out.append("\nDimensions: ").append(sphere.getDimensions());
            } else if (colliderComponent instanceof BoxCollide) {
                BoxCollide box = (BoxCollide) colliderComponent;
                out.append("\nCollider type 2");
                out.append("\nPosition: ").append(obstacle.getPosition());
                out.append("\nDimensions: ").append(box.getDimensions());
            }
        }
        return out.toString();
    }

    public List<String> getObstaclesStringList() {
        List<String> out = new ArrayList<>();
        out.add("\nObstacles: " + _obstacles.size());
        for (GameObject obstacle : _obstacles) {
            ColliderComponent colliderComponent = obstacle.getColliderComponent();
            if (colliderComponent instanceof SphereCollide) {
                SphereCollide sphere = (SphereCollide) colliderComponent;
                out.add("\nCollider type: 1");
                out.add("\nPosition: " + obstacle.getPosition().x + " " + obstacle.getPosition().y + " " + obstacle.getPosition().z + " ");
                out.add("\nDimensions: " + sphere.getDimensions().x + " " + sphere.getDimensions().y + " " + sphere.getDimensions().z + " ");
            } else if (colliderComponent instanceof BoxCollide) {
                BoxCollide box = (BoxCollide) colliderComponent;
                out.add("\nCollider type: 2");
                out.add("\nPosition: " + obstacle.getPosition().x + " " + obstacle.getPosition().y + " " + obstacle.getPosition().z + " ");
                out.add("\nDimensions: " + box.getDimensions().x + " " + box.getDimensions().y + " " + box.getDimensions().z + " ");
            }
        }
        return out;
    }

    public void addObstacle(int line, String value) {
        switch (line) {
            case 0:
                value = value.replace("Collider type: ", "");
                cacheDataList.add(new ObstacleData());
                cacheDataList.get(cacheDataList.size() - 1).type = Integer.parseInt(value);
                break;
            case 1:
                value = value.replace("Position: ", "");
                String[] pos = value.trim().split("\\s+");
                cacheDataList.get(cacheDataList.size() - 1).position = new Vector3(Float.parseFloat(pos[0]), Float.parseFloat(pos[1]), Float.parseFloat(pos[2]));
                break;
            case 2:
                value = value.replace("Dimensions: ", "");
                String[] dim = value.trim().split("\\s+");
                cacheDataList.get(cacheDataList.size() - 1).dimensions = new Vector3(Float.parseFloat(dim[0]), Float.parseFloat(dim[1]), Float.parseFloat(dim[2]));
                break;
        }
    }

    private void createObstacle() {
        for (ObstacleData cacheData : cacheDataList) {
            Vector3 pos = cacheData.position;
            pos.z = CourseManager.calculateHeight(pos.x, pos.y);
            GameObject obj = new GameObject(pos);
            switch (cacheData.type) {
                case 1:
                    SphereCollide sphere = new SphereCollide(cacheData.position, cacheData.dimensions.x);
                    obj.addColliderComponent(sphere);
                    SphereGraphicsComponent graphSphere = new SphereGraphicsComponent(cacheData.dimensions.x, Color.DARK_GRAY);
                    obj.addGraphicComponent(graphSphere);
                    break;
                case 2:
                    BoxCollide box = new BoxCollide(cacheData.position, cacheData.dimensions);
                    obj.addColliderComponent(box);
                    BoxGraphicsComponent boxGraph = new BoxGraphicsComponent(cacheData.dimensions, Color.DARK_GRAY);
                    obj.addGraphicComponent(boxGraph);
                    break;
            }
            addObstacleToList(obj);
        }
    }

    public void deleteObstacle(GameObject obj) {
        _obstacles.remove(obj);
    }

    public List<GameObject> getObstaclesList() {
        return _obstacles;
    }
}