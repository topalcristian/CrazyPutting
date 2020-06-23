package com.golf.game.Bot;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.golf.game.Components.Colliders.SphereCollide;
import com.golf.game.Components.Graphics.GraphicsComponent;
import com.golf.game.Components.Graphics.SphereGraphicsComponent;
import com.golf.game.GameLogic.CourseManager;
import com.golf.game.GameLogic.GameManager;
import com.golf.game.GameObjects.*;
import com.golf.game.Others.Velocity;

import java.util.ArrayList;
import java.util.List;

public class PledgeBot {

    private Course course;
    private Ball ball;
    private Vector3 start_pos;
    private Vector3 goal_pos;
    private Hole hole;
    private boolean tookRandomShot;
    private int actualMoveCount = 0;
    private int initialMoveCount = 0;
    private int[] ball_positions = new int[2000];
    private double[] moves;
    private int duplicateCount = 0;
    private char direction; //o = onward, b = backward, l = left, r = right
    private List<GameObject> obstaclesList;
    private Vector3 initial_Position;
    private SphereCollide sp;
    private double distance;
    private double best_distance;
    private ArrayList<Vector3> points;
    private ArrayList<Node> path;
    private Map<Node> nodeMap;


    public PledgeBot(Ball ball, Hole hole, Course course, ArrayList<Node> path, Map<Node> nodeMap) {

        this.nodeMap = nodeMap;
        this.course = course;
        this.ball = ball;
        this.hole = hole;
        start_pos = CourseManager.getStartPosition(0);

        GameManager.allowedOffset = 0;
        points = new ArrayList<>();
        for (int i = 0; i < path.size() - 1; i++) {
            Vector3 pos = new Vector3(path.get(i).getxCoordinate(), path.get(i).getyCoordinate(), 0);
            Vector3 pos2 = new Vector3(path.get(i + 1).getxCoordinate(), path.get(i + 1).getyCoordinate(), 0);
            if (euclideanDistance(pos, pos2) < 5) {
                path.remove(i + 1);
            }
        }
        for (Node node : path) {
            points.add(new Vector3(node.getxCoordinate(), node.getyCoordinate(), 0));
        }

        calculateZPoints(points);
        createGraphicPoints(points);


    }

    public void move() {
        direction = 'o';
        ArrayList<Velocity> wayOut = new ArrayList<>();
        boolean found = false;
        int iteration = 1;
        int maxIteration = 30;
        int random = (int) (Math.random() * ((1) + 1));
        distance = euclideanDistance(ball.getPosition(), course.getGoalPosition(0));
        if (distance <= best_distance) {
            System.out.println("" + distance);
        }

        int initialBallXPos = (int) ball.getPreviousPosition().x;
        int initialBallYPos = (int) ball.getPreviousPosition().y;
        int[] initialBallPos = new int[]{initialBallXPos, initialBallYPos};

        int actualBallXPos = (int) ball.getPosition().x;
        int actualBallYPos = (int) ball.getPosition().y;
        int[] actualBallPos = new int[]{actualBallXPos, actualBallYPos};

        actualMoveCount++;
        tookRandomShot = false;
        boolean duplicate = false;

        while (!found && iteration <= maxIteration) {
            GeneticAlgorithm ga = new GeneticAlgorithm(hole, course, start_pos, false);
            ga.setSimple(true);
            ga.runGenetic();
            if (ga.getBestBall().getFitnessValue() == 0) {
                Velocity best_velocity = new Velocity(ga.getBestBall().getVelocityGA().speed,
                        ga.getBestBall().getVelocityGA().angle);
                wayOut.add(best_velocity);
                found = true;
                //ball.setVelocity(best_velocity);
            } else {
                System.out.println("Else");
                Velocity vel_wayOut = check_clear_direction(start_pos);
                wayOut.add(vel_wayOut);
                System.out.println("Found way");
                iteration++;
            }
        }

        for (Velocity velocity : wayOut) {
            System.out.println("Speed: " + velocity.speed + " Angle: " + velocity.angle);
        }

    }


    public double euclideanDistance(Vector3 start, Vector3 goal) {
        return (float) Math.sqrt(Math.pow(start.x - goal.x, 2) + Math.pow(start.y - goal.y, 2) + Math.pow(start.z - goal.z, 2));
    }

    public void calculateZPoints(ArrayList<Vector3> points) {
        for (Vector3 point : points) {
            point.z = CourseManager.calculateHeight(point.x, point.y);
        }
    }

    public float calculateZ(float x, float y) {
        return CourseManager.calculateHeight(x, y);
    }

    public void createGraphicPoints(ArrayList<Vector3> points) {
        for (int i = 0; i < points.size() - 1; i++) {
            SplinePoint point = new SplinePoint(new Vector3(points.get(i)));
            point.enabled = true;
            GraphicsComponent pointGraphics = new SphereGraphicsComponent(40, Color.YELLOW);
            point.addGraphicComponent(pointGraphics);
        }
    }

    public boolean check_win() {
        float radius = hole.getRadius();
        boolean win = false;
        if (ball.getPosition().x >= hole.getPosition().x - radius &&
                ball.getPosition().x <= hole.getPosition().x + radius) {
            if (ball.getPosition().y >= hole.getPosition().y - radius &&
                    ball.getPosition().y <= hole.getPosition().y + radius) {
                win = true;
            }
        }
        return win;
    }

    public Velocity check_clear_direction(Vector3 start_position) {
        if (direction == 'o') {
            Velocity rightShot = check_right_direction(start_position);
            Velocity onwardShot = check_onward_direction(start_position);
            Velocity leftShot = check_left_direction(start_position);

            if (rightShot.speed > onwardShot.speed && rightShot.speed > leftShot.speed) {
                direction = 'r';
                return rightShot;
            } else if (onwardShot.speed > leftShot.speed) {
                return onwardShot;
            } else {
                direction = 'l';
                return leftShot;
            }
        } else if (direction == 'b') {
            Velocity leftShot = check_left_direction(start_position);
            Velocity backwardShot = check_backward_direction(start_position);
            Velocity rightShot = check_right_direction(start_position);

            if (leftShot.speed > rightShot.speed && leftShot.speed > backwardShot.speed) {
                direction = 'l';
                return leftShot;
            } else if (backwardShot.speed > rightShot.speed) {
                return backwardShot;
            } else {
                direction = 'r';
                return rightShot;
            }
        } else if (direction == 'l') {
            Velocity onwardShot = check_onward_direction(start_position);
            Velocity leftShot = check_left_direction(start_position);
            Velocity backwardShot = check_backward_direction(start_position);

            if (onwardShot.speed > leftShot.speed && onwardShot.speed > backwardShot.speed) {
                direction = 'o';
                return onwardShot;
            } else if (leftShot.speed > backwardShot.speed) {
                return leftShot;
            } else {
                direction = 'b';
                return backwardShot;
            }
        } else {
            Velocity backwardShot = check_backward_direction(start_position);
            Velocity rightShot = check_right_direction(start_position);
            Velocity onwardShot = check_onward_direction(start_position);

            if (backwardShot.speed > rightShot.speed && backwardShot.speed > onwardShot.speed) {
                direction = 'b';
                return backwardShot;
            } else if (rightShot.speed > onwardShot.speed) {
                return rightShot;
            } else {
                direction = 'o';
                return onwardShot;
            }
        }
    }

    public Velocity check_onward_direction(Vector3 start_position) {
        float x = start_position.x;
        float y = start_position.y;
        Velocity solution_shot = null;
        float y_distance = 1;
        Hole max_distance_point;
        Vector3 next_position = new Vector3(0, 0, 0);

        for (int i = 1; i <= 2000; i = i + hole.getRadius()) {
            float z = calculateZ(x, y + i);
            next_position.set(x, y + i, z);
            if (course.checkObstaclesAt(next_position) || z <= 0) {
                break;
            } else {
                y_distance = i;
            }
        }

        next_position = new Vector3(x, y + y_distance, calculateZ(x, y + y_distance));
        max_distance_point = new Hole(hole.getRadius(), next_position);

        GeneticAlgorithm ga = new GeneticAlgorithm(max_distance_point, course, start_position, false);
        ga.setSimple(true);
        ga.runGenetic();

        if (ga.getBestBall().getFitnessValue() == 0) {
            solution_shot = new Velocity(ga.getBestBall().getVelocityGA().speed,
                    ga.getBestBall().getVelocityGA().angle);
        }

        if (solution_shot != null) {
            return solution_shot;
        } else {
            return new Velocity(0, 0);
        }
    }

    public Velocity check_backward_direction(Vector3 start_position) {
        float x = start_position.x;
        float y = start_position.y;
        Velocity solution_shot = null;
        float y_distance = 1;
        Hole max_distance_point;
        Vector3 next_position = new Vector3(0, 0, 0);

        for (int i = 1; i <= 2000; i = i + hole.getRadius()) {
            float z = calculateZ(x, y - i);
            next_position.set(x, y - i, z);
            if (course.checkObstaclesAt(next_position) || z <= 0) {
                break;
            } else {
                y_distance = i;
            }
        }

        next_position = new Vector3(x, y - y_distance, calculateZ(x, y - y_distance));
        max_distance_point = new Hole(hole.getRadius(), next_position);

        GeneticAlgorithm ga = new GeneticAlgorithm(max_distance_point, course, start_position, false);
        ga.setSimple(true);
        ga.runGenetic();

        if (ga.getBestBall().getFitnessValue() == 0) {
            solution_shot = new Velocity(ga.getBestBall().getVelocityGA().speed,
                    ga.getBestBall().getVelocityGA().angle);
        }

        if (solution_shot != null) {
            return solution_shot;
        } else {
            return new Velocity(0, 0);
        }
    }

    public Velocity check_left_direction(Vector3 start_position) {
        float x = start_position.x;
        float y = start_position.y;
        Velocity solution_shot = null;
        float x_distance = 1;
        Hole max_distance_point;
        Vector3 next_position = new Vector3(0, 0, 0);

        for (int i = 1; i <= 2000; i = i + hole.getRadius()) {
            float z = calculateZ(x - i, y);
            next_position.set(x - i, y, z);
            if (course.checkObstaclesAt(next_position) || z <= 0) {
                break;
            } else {
                x_distance = i;
            }
        }

        next_position = new Vector3(x - x_distance, y, calculateZ(x - x_distance, y));
        max_distance_point = new Hole(hole.getRadius(), next_position);

        GeneticAlgorithm ga = new GeneticAlgorithm(max_distance_point, course, start_position, false);
        ga.setSimple(true);
        ga.runGenetic();

        if (ga.getBestBall().getFitnessValue() == 0) {
            solution_shot = new Velocity(ga.getBestBall().getVelocityGA().speed,
                    ga.getBestBall().getVelocityGA().angle);
        }

        if (solution_shot != null) {
            return solution_shot;
        } else {
            return new Velocity(0, 0);
        }
    }

    public Velocity check_right_direction(Vector3 start_position) {
        float x = start_position.x;
        float y = start_position.y;
        Velocity solution_shot = null;
        float x_distance = 1;
        Hole max_distance_point;
        Vector3 next_position = new Vector3(0, 0, 0);

        for (int i = 1; i <= 2000; i = i + hole.getRadius()) {
            float z = calculateZ(x + i, y);
            next_position.set(x + i, y, z);
            if (course.checkObstaclesAt(next_position) || z <= 0) {
                break;
            } else {
                x_distance = i;
            }
        }

        next_position = new Vector3(x + x_distance, y, calculateZ(x + x_distance, y));
        max_distance_point = new Hole(hole.getRadius(), next_position);

        GeneticAlgorithm ga = new GeneticAlgorithm(max_distance_point, course, start_position, false);
        ga.setSimple(true);
        ga.runGenetic();

        if (ga.getBestBall().getFitnessValue() == 0) {
            solution_shot = new Velocity(ga.getBestBall().getVelocityGA().speed,
                    ga.getBestBall().getVelocityGA().angle);
        }

        if (solution_shot != null) {
            return solution_shot;
        } else {
            return new Velocity(0, 0);
        }
    }
}

