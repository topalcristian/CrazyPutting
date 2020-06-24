package com.golf.game.FormulaParser;


public class Vector2D {


    public double x;
    public double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }


    public double get_x() {
        return this.x;
    }


    public double get_y() {
        return this.y;
    }


    public void add(Vector2D a) {
        this.x += a.get_x();
        this.y += a.get_y();
    }

    public Vector2D addX(double val) {
        this.x += val;
        return this;
    }

    public Vector2D addY(double val) {
        this.y += val;
        return this;
    }


}