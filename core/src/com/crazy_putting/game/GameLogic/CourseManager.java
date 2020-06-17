package com.crazy_putting.game.GameLogic;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.crazy_putting.game.FormulaParser.HeightSolver;
import com.crazy_putting.game.FormulaParser.Vector2D;
import com.crazy_putting.game.GameLogic.Splines.BiCubicSpline;
import com.crazy_putting.game.GameObjects.Course;
import com.crazy_putting.game.GameObjects.GameObject;
import com.crazy_putting.game.Parser.Parser;

import java.util.List;

public class CourseManager {
    private static int _amoutCourse = 1;
    private static List<Course> _courseList;
    private static Course _activeCourse;
    private static String _cacheFileName;
    private static int _indexActive = 0;
    private static BiCubicSpline _spline;
    private static Vector2 _dimensions = new Vector2(2000, 2000);

    public static int getCourseAmount() {
        _amoutCourse = _courseList.size();
        return _amoutCourse;
    }

    public static void loadFile(String pFileName) {
        _cacheFileName = pFileName;
        _courseList = Parser.getCourses(_cacheFileName);
        if (_courseList != null) {
            _activeCourse = _courseList.get(0);
        } else {
            return;
        }

    }

    public static void initObstacles() {
        _activeCourse.initObstacles();
    }

    public static List<Course> getCourseList() {
        return _courseList;
    }

    public static Course getCourseWithID(int pID) {
        for (int i = 0; i < getCourseAmount(); i++) {
            if (_courseList.get(i).getID() == pID)
                return _courseList.get(i);
        }
        return null;
    }

    public static void setBiCubicSpline(BiCubicSpline spline) {
        _spline = spline;
        _dimensions = _spline.getDimensions();
    }

    public static Vector2 getCourseDimensions() {
        return _dimensions;
    }

    public static Course getCourseWithIndex(int pIndex) {
        return _courseList.get(pIndex);
    }

    public static void setActiveCourseWithID(int pID) {
        setActiveCourse(getCourseWithID(pID));
    }

    public static void setActiveCourseWithIndex(int pIndex) {
        setActiveCourse(_courseList.get(pIndex));
    }

    public static Course getActiveCourse() {
        return _activeCourse;
    }

    private static void setActiveCourse(Course pCourse) {
        _activeCourse = pCourse;
    }

    public static void addCourseToList(Course pCourse) {
        _courseList.add(pCourse);
        reWriteCourse();
    }

    public static void reWriteCourse() {
        if (_cacheFileName == null) return; //If we havent cache a filename then we should not proceed
        Parser.writeCourseList(_cacheFileName, _courseList);
    }

    public static Vector3 getStartPosition(int pPlayer) {
        Vector3 pos = _activeCourse.getStartBall(pPlayer);
        pos.z = calculateHeight(pos.x, pos.y);
        return pos;
    }

    public static void saveCourseSpline() {
        _activeCourse.setSplinePoints(_spline.getSplinePointsHeight());
    }

    public static int getIndexActive() {
        return _indexActive;
    }

    public static void reParseHeightFormula(int pNewIndex) {
        _indexActive = pNewIndex;
    }

    public static float calculateHeight(float x, float y) {
        if (_activeCourse == null) {
            return -1;
        }

        if (GameManager.mazeBotType.equals("simple")) {
            if (_activeCourse.checkObstaclesAt(new Vector3(x, y, 0))) {
                return -10;
            }
        }

        if (_spline != null) {
            return _spline.getHeightAt(new Vector2(x, y));
        } else {
            HeightSolver parser = new HeightSolver(_activeCourse.getHeight());
            float result = (float) parser.evaluate(new Vector2D(x, y));
            return result;
        }
    }

    public static void addObstacle(GameObject pObstacle) {
        _activeCourse.addObstacleToList(pObstacle);
    }

    public static Vector3 getGoalStartPosition(int pPlayer) {
        Vector3 pos = _activeCourse.getGoalPosition(pPlayer);
        pos.z = calculateHeight(pos.x, pos.y);
        return pos;
    }

    public static float getMaxSpeed() {
        return _activeCourse.getMaxSpeed();
    }

    public static void dispose() {
        _spline = null;
    }
}
