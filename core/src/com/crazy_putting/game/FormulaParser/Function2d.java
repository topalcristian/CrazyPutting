package com.crazy_putting.game.FormulaParser;

public interface Function2d {
    double evaluate(Vector2D p);

    Vector2D gradient(Vector2D p, double delta);


}
