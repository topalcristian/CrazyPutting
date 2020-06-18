package com.golf.game.GameLogic;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.golf.game.FormulaParser.HeightSolver;
import com.golf.game.FormulaParser.Vector2D;
import com.golf.game.GameLogic.Splines.BiCubicSpline;
import com.golf.game.GameObjects.Course;
import com.golf.game.GameObjects.GameObject;
import com.golf.game.Parser.Parser;

import java.util.List;

public class CourseManager {
    private static int amountCourse = 1;
    private static List<Course> courseList;
    private static Course activeCourse;
    private static String cacheFileName;
    private static int indexActive = 0;
    private static BiCubicSpline spline;
    private static Vector2 dimensions = new Vector2(2000, 2000);

    public static int getCourseAmount() {


        amountCourse = courseList.size();
        return amountCourse;
    }

    public static void loadFile(String pFileName) {
        cacheFileName = pFileName;
        courseList = Parser.getCourses(cacheFileName);
        if (courseList != null) {
            activeCourse = courseList.get(0);
        } else {
            return;
        }

    }

    public static void initObstacles() {
        activeCourse.initObstacles();
    }


    public static void setBiCubicSpline(BiCubicSpline spline) {
        CourseManager.spline = spline;
        dimensions = CourseManager.spline.getDimensions();
    }

    public static Vector2 getCourseDimensions() {
        return dimensions;
    }

    public static Course getCourseWithIndex(int pIndex) {
        return courseList.get(pIndex);
    }

    public static void setActiveCourseWithIndex(int pIndex) {
        setActiveCourse(courseList.get(pIndex));
    }

    public static Course getActiveCourse() {
        return activeCourse;
    }

    private static void setActiveCourse(Course pCourse) {
        activeCourse = pCourse;
    }

    public static void addCourseToList(Course pCourse) {
        courseList.add(pCourse);
        reWriteCourse();
    }

    public static void reWriteCourse() {
        if (cacheFileName == null) return;
        Parser.writeCourseList(cacheFileName, courseList);
    }

    public static Vector3 getStartPosition(int pPlayer) {
        Vector3 pos = activeCourse.getStartBall(pPlayer);
        pos.z = calculateHeight(pos.x, pos.y);
        return pos;
    }

    public static void saveCourseSpline() {
        activeCourse.setSplinePoints(spline.getSplinePointsHeight());
    }

    public static int getIndexActive() {
        return indexActive;
    }

    public static void reParseHeightFormula(int pNewIndex) {
        indexActive = pNewIndex;
    }

    public static float calculateHeight(float x, float y) {
        if (activeCourse == null) {
            return -1;
        }

        if (GameManager.mazeBotType.equals("simple")) {
            if (activeCourse.checkObstaclesAt(new Vector3(x, y, 0))) {
                return -10;
            }
        }

        if (spline != null) {
            return spline.getHeightAt(new Vector2(x, y));
        } else {
            HeightSolver parser = new HeightSolver(activeCourse.getHeight());
            float result = (float) parser.evaluate(new Vector2D(x, y));
            return result;
        }
    }

    public static void addObstacle(GameObject pObstacle) {
        activeCourse.addObstacleToList(pObstacle);
    }

    public static Vector3 getGoalStartPosition(int pPlayer) {
        Vector3 pos = activeCourse.getGoalPosition(pPlayer);
        pos.z = calculateHeight(pos.x, pos.y);
        return pos;
    }

    public static float getMaxSpeed() {
        return activeCourse.getMaxSpeed();
    }

    public static void dispose() {
        spline = null;
    }
}
