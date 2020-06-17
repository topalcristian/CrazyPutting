package com.crazy_putting.game.Bot;

public abstract class AbstractNode {

    private float BASICMOVEMENTCOST = 0;

    private int xIndex;
    private int yIndex;

    private boolean walkable;
    private boolean goal;

    private int xCoordinate;
    private int yCoordinate;

    private AbstractNode previous;
    private int movementPenalty; ///optional

    private float gCosts;
    private float hCosts;
    private float totalCost;

    public AbstractNode(int xIndex, int yIndex) {
        this.xIndex = xIndex;
        this.yIndex = yIndex;
        this.walkable = true;
        this.movementPenalty = 0;
    }

    public void setBASICMOVEMENTCOST(float value) {
        this.BASICMOVEMENTCOST = value;
    }

    public void setCoordinates(int x, int y) {
        this.xCoordinate = x;
        this.yCoordinate = y;
    }

    public int getxCoordinate() {
        return xCoordinate;
    }

    public int getyCoordinate() {
        return yCoordinate;
    }


    public int getxIndex() {
        return xIndex;
    }

    public int getyIndex() {
        return yIndex;
    }

    public boolean isWalkable() {
        return walkable;
    }

    public void setWalkable(boolean walkable) {
        this.walkable = walkable;
    }

    public boolean isGoal() {
        return goal;
    }

    public void setGoal(boolean goal) {
        this.goal = goal;
    }

    public AbstractNode getPrevious() {
        return previous;
    }

    public void setPrevious(AbstractNode previous) {
        this.previous = previous;
    }

    public void setgCosts(AbstractNode previousAbstractNode, float basicCost) {
        setgCosts(previousAbstractNode.getgCosts() + basicCost);
    }

    public float getgCosts() {
        return gCosts;
    }

    public void setgCosts(float gCosts) {
        this.gCosts = gCosts + movementPenalty;
    }

    public float gethCosts() {
        return hCosts;
    }

    protected void sethCosts(float hCosts) {
        this.hCosts = hCosts;
    }

    public void calculatehCosts(float goalX, float goalY) {
        int currentX, currentY;
        currentX = this.xIndex;
        currentY = this.yIndex;
        sethCosts(2 * (float) Math.sqrt(Math.pow(currentX - goalX, 2) + Math.pow(currentY - goalY, 2)));
    }

    public float getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(float newX, float newY) {
        calculatehCosts(newX, newY);
        this.totalCost = getgCosts() + gethCosts();
    }

    @Override
    public String toString() {
        return "(" + getxIndex() + ", " + getyIndex() + "): h: "
                + gethCosts() + " g: " + getgCosts() + " f: " + getTotalCost();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractNode other = (AbstractNode) obj;
        if (this.xIndex != other.xIndex) {
            return false;
        }
        return this.yIndex == other.yIndex;
    }

}
