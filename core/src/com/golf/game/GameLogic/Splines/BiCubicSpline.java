package com.golf.game.GameLogic.Splines;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.golf.game.Components.Graphics.GraphicsComponent;
import com.golf.game.Components.Graphics.SphereGraphicsComponent;
import com.golf.game.GameLogic.CourseManager;
import com.golf.game.GameObjects.SplinePoint;

import java.util.ArrayList;
import java.util.List;

public class BiCubicSpline {
    //Matrix to get the a values
    private double[][] A = {{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {-3, 3, 0, 0, -2, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {2, -2, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, -3, 3, 0, 0, -2, -1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 2, -2, 0, 0, 1, 1, 0, 0},
            {-3, 0, 3, 0, 0, 0, 0, 0, -2, 0, -1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, -3, 0, 3, 0, 0, 0, 0, 0, -2, 0, -1, 0},
            {9, -9, -9, 9, 6, 3, -6, -3, 6, -6, 3, -3, 4, 2, 2, 1},
            {-6, 6, 6, -6, -3, -3, 3, 3, -4, 4, -2, 2, -2, -2, -1, -1},
            {2, 0, -2, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 2, 0, -2, 0, 0, 0, 0, 0, 1, 0, 1, 0},
            {-6, 6, 6, -6, -4, -2, 4, 2, -3, 3, -3, 3, -2, -1, -2, -1},
            {4, -4, -4, 4, 2, 2, -2, -2, 2, -2, 2, -2, 1, 1, 1, 1}};
    private List<Spliner> _splineList = new ArrayList<>();
    private SplinePoint[][] _splinePoints = new SplinePoint[6][6];
    private Vector2 _dimensions;

    public BiCubicSpline(Vector2 posStart, float verticesPerSide, float pScale) {

        int sizeSide = 200;
        _dimensions = new Vector2(sizeSide * pScale, sizeSide * pScale);
        float[][] coursePoints = CourseManager.getActiveCourse().getSplinePoints();
        for (int i = 0; i < _splinePoints.length; i++) { // aRow
            for (int j = 0; j < _splinePoints.length; j++) { // bColumn

                float height = coursePoints[i][j];
                SplinePoint point = new SplinePoint(new Vector3(pScale * (posStart.x + verticesPerSide * i), pScale * (posStart.y + verticesPerSide * j), height));
                point.enabled = false;
                GraphicsComponent pointGraphics = new SphereGraphicsComponent(40, Color.RED);
                point.addGraphicComponent(pointGraphics);
                _splinePoints[i][j] = point;
            }
        }
    }

    public Spliner createSplineBlock(int[][] points, Vector2 posStart, Vector2 pDimensions, float pScale, Node pNode) {
        Spliner spline = new Spliner(posStart, pDimensions, pScale, pNode);

        _splineList.add(spline);
        updateSplineCoeff(spline, points);
        return spline;
    }

    public Vector2 getDimensions() {
        return _dimensions;
    }

    private double[][] mulMat(double[][] pPoints, double[][] pSOE) {
        int aRows = pPoints.length;
        int aColumns = pPoints[0].length;
        int bRows = pSOE.length;
        int bColumns = pSOE[0].length;

        if (aColumns != bRows) {
            throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
        }

        double[][] out = new double[aRows][bColumns];
        for (int i = 0; i < aRows; i++) { // aRow
            for (int j = 0; j < bColumns; j++) { // bColumn
                for (int k = 0; k < aColumns; k++) { // aColumn
                    out[i][j] += pPoints[i][k] * pSOE[k][j];
                }
            }
        }
        return out;
    }

    public Spliner updateSplineCoeff(Spliner info, int[][] pPoints) {

        double[][] cache = mulMat(A, getHeightInGrid(pPoints));
        double[][] newCoeff = {{cache[0][0], cache[4][0], cache[8][0], cache[12][0]},
                {cache[1][0], cache[5][0], cache[9][0], cache[13][0]},
                {cache[2][0], cache[6][0], cache[10][0], cache[14][0]},
                {cache[3][0], cache[7][0], cache[11][0], cache[15][0]}};
        info.setCoeff(newCoeff, pPoints);

        return info;
    }

    public Spliner updateSplineCoeff(Spliner info) {
        double[][] cache = mulMat(A, getHeightInGrid(info.getPoints()));
        double[][] newCoeff = {{cache[0][0], cache[4][0], cache[8][0], cache[12][0]},
                {cache[1][0], cache[5][0], cache[9][0], cache[13][0]},
                {cache[2][0], cache[6][0], cache[10][0], cache[14][0]},
                {cache[3][0], cache[7][0], cache[11][0], cache[15][0]}};
        info.setCoeff(newCoeff, info.getPoints());

        return info;
    }

    public double[][] getHeightInGrid(int[][] ind) {
        return new double[][]{{_splinePoints[ind[0][0]][ind[0][1]].getSplineHeight()}, {_splinePoints[ind[1][0]][ind[1][1]].getSplineHeight()}, {_splinePoints[ind[2][0]][ind[2][1]].getSplineHeight()}, {_splinePoints[ind[3][0]][ind[3][1]].getSplineHeight()},
                {0}, {0}, {0}, {0},
                {0}, {0}, {0}, {0},
                {0}, {0}, {0}, {0}};
    }

    /*
    If point is not in spline then return -10 as if it was water
     */
    public float getHeightAt(Vector2 pPos) {
        for (Spliner spline : _splineList) {
            if (spline.getRectangle().contains(pPos))
                return getHeightAt(pPos, spline);
        }
        return -10;

    }

    public float[][] getSplinePointsHeight() {
        float[][] points = new float[_splinePoints.length][_splinePoints[0].length];
        for (int i = 0; i < _splinePoints.length; i++) {
            for (int j = 0; j < _splinePoints[0].length; j++) {
                points[i][j] = (float) _splinePoints[i][j].getSplineHeight();
            }
        }
        return points;

    }

    public float getHeightAt(Vector2 pPos, Spliner spline) {
        Vector2 posLocal = spline.normPos(pPos);

        double[][] x = {{1, posLocal.x, Math.pow(posLocal.x, 2), Math.pow(posLocal.x, 3)}};
        double[][] y = {{1}, {posLocal.y}, {Math.pow(posLocal.y, 2)}, {Math.pow(posLocal.y, 3)}};
        double[][] out = mulMat(x, mulMat(spline.getCoeff(), y));

        return (float) out[0][0];
    }


    public SplinePoint[][] getSplinePoints() {
        return _splinePoints;
    }

    public List<Spliner> getSplineList() {
        return _splineList;
    }
}
