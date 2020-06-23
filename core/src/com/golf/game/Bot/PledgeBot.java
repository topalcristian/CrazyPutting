package com.golf.game.Bot;

////////////////////////////////////////////////////////////
//
// @Author: Boxho Sébastien
//
//
//
////////////////////////////////////////////////////////////

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
    private ArrayList<Velocity> wayOut = new ArrayList<>();
    private ArrayList<Vector3> points;
    private ArrayList<Vector3> positions = new ArrayList<Vector3>();
    private ArrayList<Node> path;
    private Map<Node> nodeMap;
    private Vector3 actual_position;

    ////////
    // ADD SYSOUT
    ////////

    public PledgeBot(Ball ball, Hole hole, Course course, ArrayList<Node> path, Map<Node> nodeMap) {
        //ArrayList<Node> path

        this.nodeMap = nodeMap;
        this.course = course;
        this.ball = ball;
        ball.fix(false);
        this.hole = hole;
        actual_position = CourseManager.getStartPosition(0);

        ///////////////////
        GameManager.allowedOffset = 0;
        points = new ArrayList<Vector3>();
        for (int i = 0; i < path.size() - 1; i++) {
            Vector3 pos = new Vector3(path.get(i).getxCoordinate(), path.get(i).getyCoordinate(), 0);
            Vector3 pos2 = new Vector3(path.get(i + 1).getxCoordinate(), path.get(i + 1).getyCoordinate(), 0);
            if (euclideanDistance(pos, pos2) < 5) {
                path.remove(i + 1);
            }
        }
        for (int i = 0; i < path.size(); i++) {
            points.add(new Vector3(path.get(i).getxCoordinate(), path.get(i).getyCoordinate(), 0));
        }
        /*for (Node node : path) {
            points.add(new Vector3(node.getxCoordinate(), node.getyCoordinate(), 0));
        }*/
        //  System.out.print(" (" + startX  + ", " + startY  + ") -> ");


        /*for (int i = 0; i < path.size(); i++) {
            if (i != path.size() - 1)
                System.out.print("(" + path.get(i).getxCoordinate() + ", " + path.get(i).getyCoordinate() + ") -> ");
            else System.out.println("(" + path.get(i).getxCoordinate() + ", " + path.get(i).getyCoordinate() + ") ");
        }*/

        calculateZPoints(points);
        createGraphicPoints(points);


        /*for (Vector3 point : points) {
            System.out.println("Point x " + point.x + " y " + point.y + " z " + point.z);
        }*/
        ///////////////////

        //sp = new SphereCollider(ball.getStartPosition(), 20);
        //best_distance = euclideanDistance(ball.getPosition(),hole.getPosition());
        //obstaclesList = course.getObstaclesList();
    }

    public ArrayList<Velocity> move() {
        direction = 'o';
        boolean found = false;
        int iteration = 0;
        int maxIteration = 30;

        int random = (int) (Math.random() * ((1 - 0) + 1)) - 0;

        /*distance = euclideanDistance(ball.getPosition(),course.getGoalPosition(0));
        if(distance <= best_distance)
        {
            System.out.println(""+ distance);
        }*/

        /*int initialBallXPos = (int)ball.getPreviousPosition().x;
        int initialBallYPos = (int)ball.getPreviousPosition().y;
        int[] initialBallPos = new int[]{initialBallXPos,initialBallYPos};

        int actualBallXPos = (int)ball.getPosition().x;
        int actualBallYPos = (int)ball.getPosition().y;
        int[] actualBallPos = new int[]{actualBallXPos,actualBallYPos};*/


        actualMoveCount++;
        tookRandomShot = false;
        boolean duplicate = false;
        System.out.println("Ball first position: " + ball.getPosition().x + " : " + ball.getPosition().y);
        //ball.setVelocityGA(30,40);
        //ball.setVelocity(30,40);


        //System.out.println("Ball final position: "+ ball.getPosition().x + " : "+ ball.getPosition().y );

        /*while (ball.isMoving())
        {

            System.out.println("Ball first position: "+ ball.getPosition().x + " : "+ ball.getPosition().y );
        }*/

        //maxIteration is there in case the bot runs into an infinite loop
        while (found == false && iteration <= maxIteration && !ball.isMoving()) {
            if (iteration >= 3) {
                //Is true if the bot is stuck in a loop with the same moves
                duplicate = check_old_moves(wayOut);
            }

            if (duplicate) {
                //We will take a random shot
                float random_speed = (float) (Math.random() * (350 - 1) + 1);
                float random_angle = (float) (Math.random() * (360 - 0) + 1);
                Velocity random_shot = new Velocity(random_speed, random_angle);
                ball.setVelocityGA(random_speed, random_angle);
                ball.setVelocity(random_shot);
                actual_position = ball.getPosition();
                wayOut.add(random_shot);
            } else {
                GeneticAlgorithm ga = new GeneticAlgorithm(hole, course, actual_position, false);
                ga.setSimple(true);
                ga.runGenetic();

                if (ga.getBestBall().getFitnessValue() == 0) {
                    Velocity best_velocity = new Velocity(ga.getBestBall().getVelocityGA().speed,
                            ga.getBestBall().getVelocityGA().angle);

                    actual_position = ga.getBestBall().getPosition();
                    wayOut.add(best_velocity);
                    found = true;
                    iteration++;

                    System.out.println("Gaol archived !");
                    System.out.println("Ball final position: " + actual_position.x + " : " + actual_position.y);
                } else {
                    System.out.println("Search pledge");
                    System.out.println("Ball position: " + actual_position.x + " : " + actual_position.y);

                    Velocity vel_wayOut = check_clear_direction(actual_position);
                    wayOut.add(vel_wayOut);

                    System.out.println("Found one shot");
                    System.out.println("Ball new position: " + actual_position.x + " : " + actual_position.y);

                    iteration++;
                }
            }
        }

        System.out.println("The way out is: ");
        for (int i = 0; i < wayOut.size(); i++) {
            System.out.println("Speed: " + wayOut.get(i).speed + " Angle: " + wayOut.get(i).angle);
        }

        ////////////////
        //  CHANGE FOR DISTANCE NOT VELOCITY
        ///////////////
        return wayOut;
    }

    //////////////////////////////////////////////////////////////////////////////
    /////////////                                                   //////////////
    //////////////////////////////////////////////////////////////////////////////

    public double euclideanDistance(Vector3 start, Vector3 goal) {
        double distance = (float) Math.sqrt(Math.pow(start.x - goal.x, 2) + Math.pow(start.y - goal.y, 2) + Math.pow(start.z - goal.z, 2));
        return distance;
    }

    public void calculateZPoints(ArrayList<Vector3> points) {
        for (int i = 0; i < points.size(); i++) {
            points.get(i).z = CourseManager.calculateHeight(points.get(i).x, points.get(i).y);
        }
    }

    public float calculateZ(float x, float y) {
        float z = CourseManager.calculateHeight(x, y);
        return z;
    }

    public void createGraphicPoints(ArrayList<Vector3> points) {
        for (int i = 0; i < points.size() - 1; i++) {
            SplinePoint point = new SplinePoint(new Vector3(points.get(i)));
            point.enabled = true;
            GraphicsComponent pointGraphics = new SphereGraphicsComponent(40, Color.YELLOW);
            point.addGraphicComponent(pointGraphics);
        }
    }

    public void update_position(Vector3 new_position) {
        actual_position = new_position;
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

    public boolean check_old_moves(ArrayList<Velocity> moves) {
        boolean duplicate = false;
        int size = moves.size();

        Velocity last_shot = moves.get(size - 1);
        Velocity second_last_shot = moves.get(size - 2);
        Velocity third_last_shot = moves.get(size - 3);

        if (last_shot.equals(second_last_shot)) {
            duplicate = true;
        } else if (last_shot.equals(third_last_shot)) {
            duplicate = true;
        }
        return duplicate;
    }

    public Velocity check_clear_direction(Vector3 start_position) {
        if (direction == 'o') {
            positions.clear();
            Velocity rightShot = check_right_direction(start_position);
            Velocity onwardShot = check_onward_direction(start_position);
            Velocity leftShot = check_left_direction(start_position);

            double right_distance = euclideanDistance(start_position, positions.get(0));
            double onward_distance = euclideanDistance(start_position, positions.get(1));
            double left_distance = euclideanDistance(start_position, positions.get(2));

            if (right_distance > onward_distance && right_distance > left_distance) {
                update_position(positions.get(0));
                direction = 'r';
                return rightShot;
            } else if (onward_distance > left_distance) {
                update_position(positions.get(1));
                return onwardShot;
            } else {
                update_position(positions.get(2));
                direction = 'l';
                return leftShot;
            }
        } else if (direction == 'b') {
            positions.clear();
            Velocity leftShot = check_left_direction(start_position);
            Velocity backwardShot = check_backward_direction(start_position);
            Velocity rightShot = check_right_direction(start_position);

            double left_distance = euclideanDistance(start_position, positions.get(0));
            double backward_distance = euclideanDistance(start_position, positions.get(1));
            double right_distance = euclideanDistance(start_position, positions.get(2));

            if (left_distance > right_distance && left_distance > backward_distance) {
                update_position(positions.get(0));
                direction = 'l';
                return leftShot;
            } else if (backward_distance > right_distance) {
                update_position(positions.get(1));
                return backwardShot;
            } else {
                update_position(positions.get(2));
                direction = 'r';
                return rightShot;
            }
        } else if (direction == 'l') {
            positions.clear();
            Velocity onwardShot = check_onward_direction(start_position);
            Velocity leftShot = check_left_direction(start_position);
            Velocity backwardShot = check_backward_direction(start_position);

            double onward_distance = euclideanDistance(start_position, positions.get(0));
            double left_distance = euclideanDistance(start_position, positions.get(1));
            double backward_distance = euclideanDistance(start_position, positions.get(2));

            if (onward_distance > left_distance && onward_distance > backward_distance) {
                update_position(positions.get(0));
                direction = 'o';
                return onwardShot;
            } else if (left_distance > backward_distance) {
                update_position(positions.get(1));
                return leftShot;
            } else {
                update_position(positions.get(2));
                direction = 'b';
                return backwardShot;
            }
        } else {
            positions.clear();
            Velocity backwardShot = check_backward_direction(start_position);
            Velocity rightShot = check_right_direction(start_position);
            Velocity onwardShot = check_onward_direction(start_position);

            double backward_distance = euclideanDistance(start_position, positions.get(0));
            double right_distance = euclideanDistance(start_position, positions.get(1));
            double onward_distance = euclideanDistance(start_position, positions.get(2));

            if (backward_distance > right_distance && backward_distance > onward_distance) {
                update_position(positions.get(0));
                direction = 'b';
                return backwardShot;
            } else if (right_distance > onward_distance) {
                update_position(positions.get(1));
                return rightShot;
            } else {
                update_position(positions.get(2));
                direction = 'o';
                return onwardShot;
            }
        }
    }

    /**
     * Tries to find the farthest possible shot in the onward (up) direction
     *
     * @param start_position
     * @return farthest possible shot
     */
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
        positions.add(ga.getEndPosition());

        if (solution_shot != null) {
            return solution_shot;
        } else {
            return new Velocity(0, 0);
        }
    }

    /**
     * Tries to find the farthest possible shot in the backward (down) direction
     *
     * @param start_position
     * @return farthest possible shot in Velocity
     */
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
        positions.add(ga.getEndPosition());

        if (solution_shot != null) {
            return solution_shot;
        } else {
            return new Velocity(0, 0);
        }
    }

    /**
     * Tries to find the farthest possible shot in the left direction
     *
     * @param start_position
     * @return farthest possible shot
     */
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
        positions.add(ga.getEndPosition());

        if (solution_shot != null) {
            return solution_shot;
        } else {
            return new Velocity(0, 0);
        }
    }

    /**
     * Tries to find the farthest possible shot in the right direction
     *
     * @param start_position
     * @return farthest possible shot
     */
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
        positions.add(ga.getEndPosition());

        if (solution_shot != null) {
            return solution_shot;
        } else {
            return new Velocity(0, 0);
        }
    }
}

