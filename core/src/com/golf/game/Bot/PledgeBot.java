package com.golf.game.Bot;

import com.badlogic.gdx.math.Vector3;
import com.golf.game.GameLogic.CourseManager;
import com.golf.game.GameObjects.Ball;
import com.golf.game.GameObjects.Course;
import com.golf.game.GameObjects.Hole;
import com.golf.game.Others.Velocity;

import java.util.ArrayList;

/**
 * @Author: Boxho Sébastien
 * Project Crazy Putting 1-2
 * 2019-2020
 */
public class PledgeBot {

    private Course course;
    private Ball ball;
    private Hole hole;
    private int ShotCounter = 0;
    private char direction; //o = onward, b = backward, l = left, r = right
    private ArrayList<Velocity> wayOut = new ArrayList<Velocity>();
    private ArrayList<Vector3> positions = new ArrayList<Vector3>();
    private Vector3 actual_position;

    /**
     * Constructor
     *
     * @param course of the game
     * @param ball   starting point
     * @param hole   end point
     */
    public PledgeBot(Ball ball, Hole hole, Course course) {
        this.course = course;
        this.ball = ball;
        this.hole = hole;
        actual_position = CourseManager.getStartPosition(0);
    }

    /**
     * Main method, tries to find a solution of Velocities to reach the end point
     * from the starting point
     *
     * @return
     */
    public ArrayList<Velocity> move() {
        direction = 'o';
        boolean found = false;
        boolean duplicate = false;

        System.out.println("Hole position: " + hole.getPosition().x + " : " + hole.getPosition().y);
        System.out.println("Ball first position: " + ball.getPosition().x + " : " + ball.getPosition().y);

        int iteration = 0;
        int maxIteration = 50;

        //maxIteration is there in case the bot runs into an infinite loop
        while (found == false && iteration < maxIteration && !ball.isMoving()) {
            if (iteration >= 3) {
                //Is true if the bot is stuck in a loop with the same moves
                duplicate = check_old_moves(wayOut);
            }

            if (duplicate) {
                System.out.println("Found duplicate !");

                //We will take a random shot to get out of this loop
                float random_speed = (float) (Math.random() * (350 - 1) + 1);
                float random_angle = (float) (Math.random() * (360 - 0) + 1);
                Velocity random_shot = new Velocity(random_speed, random_angle);
                ball.setVelocityGA(random_speed, random_angle);
                ball.setVelocity(random_shot);
                actual_position = ball.getPosition();
                wayOut.add(random_shot);
            } else {
                // Tries if the shot form the actual position to the end point is possible
                GeneticAlgorithm ga = new GeneticAlgorithm(hole, course, actual_position, false);
                ga.setSimple(true);
                ga.runGenetic();

                if (ga.getBestBall().getFitnessValue() == 0) {
                    //If true, this means the end point was reached the game is successfully over
                    Velocity best_velocity = new Velocity(ga.getBestBall().getVelocityGA().speed,
                            ga.getBestBall().getVelocityGA().angle);

                    actual_position = ga.getEndPosition();
                    wayOut.add(best_velocity);
                    found = true;
                    iteration++;

                    System.out.println("Ball final position: " + actual_position.x + " : " + actual_position.y);
                    System.out.println("Gaol archived !");
                } else {
                    //Calls the pledge method
                    Velocity vel_wayOut = check_clear_direction(actual_position);
                    wayOut.add(vel_wayOut);

                    System.out.println("Pledge: Ball new position: " + actual_position.x + " : " + actual_position.y);

                    iteration++;
                }
            }
        }

        if (iteration == maxIteration) {
            System.out.println("-- MAX ITERATION / STOP --");
        } else {
            System.out.println("The way out is: ");
            for (int i = 0; i < wayOut.size(); i++) {
                //Prints out all the velocities to get from the starting point to the end point
                System.out.println("Speed: " + wayOut.get(i).speed + " Angle: " + wayOut.get(i).angle);
            }
        }
        return wayOut;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////                                 Methods                                                  //////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Calculates the euclidean distance between two points
     *
     * @param start, first point
     * @param goal,  second point
     * @return distance between those points
     */
    public double euclideanDistance(Vector3 start, Vector3 goal) {
        double distance = (float) Math.sqrt(Math.pow(start.x - goal.x, 2) + Math.pow(start.y - goal.y, 2) + Math.pow(start.z - goal.z, 2));
        return distance;
    }

    /**
     * Calculates the height of a given point
     *
     * @param x, the x value of a point
     * @param y, the y value of a point
     * @return z, the height in function of x and y
     */
    public float calculateZ(float x, float y) {
        float z = CourseManager.calculateHeight(x, y);
        return z;
    }

    /**
     * Updates the actual position of the ball
     *
     * @param new_position, new position of the ball
     */
    public void update_position(Vector3 new_position) {
        actual_position = new_position;
    }

    /**
     * Checks if the ball is inside the hole
     *
     * @return true if inside, false if not
     */
    public boolean check_win() {
        float radius = hole.getRadius();
        boolean win = false;
        if (actual_position.x >= hole.getPosition().x - radius &&
                actual_position.x <= hole.getPosition().x + radius) {
            if (actual_position.y >= hole.getPosition().y - radius &&
                    actual_position.y <= hole.getPosition().y + radius) {
                win = true;
            }
        }
        return win;
    }

    /**
     * Checks if there are some duplicates (is stuck in a loop) in the moves which were made
     *
     * @param moves, list of already made moves
     * @return true, if there are duplicates, false if not
     */
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

    /**
     * Main pledge method function, tries to find out in which direction it will make the next move
     *
     * @param start_position, the actual position of the ball
     * @return Velocity, the speed and angle of the next move
     */
    public Velocity check_clear_direction(Vector3 start_position) {
        //The next move depends on the actual direction of the ball
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
            return new Velocity(0,0);
        }
    }
}

