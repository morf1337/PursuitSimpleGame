package com.team00;

import java.util.Objects;

class Node implements Comparable<Node> {
    private int x, y;
    private int accumulatedCostAsG;
    private int heuristicEstimateAsH;
    private int totalEstimatedCostAsF;
    private Node parent;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        this.accumulatedCostAsG = 0;
        this.heuristicEstimateAsH = 0;
        this.totalEstimatedCostAsF = 0;
        this.parent = null;
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.totalEstimatedCostAsF, other.totalEstimatedCostAsF);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Node node = (Node) obj;
        return x == node.x && y == node.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getAccumulatedCostAsG() {
        return accumulatedCostAsG;
    }

    public int getHeuristicEstimateAsH() {
        return heuristicEstimateAsH;
    }

    public int getTotalEstimatedCostAsF() {
        return totalEstimatedCostAsF;
    }

    public Node getParent() {
        return parent;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setAccumulatedCostAsG(int accumulatedCostAsG) {
        this.accumulatedCostAsG = accumulatedCostAsG;
    }

    public void setHeuristicEstimateAsH(int heuristicEstimateAsH) {
        this.heuristicEstimateAsH = heuristicEstimateAsH;
    }

    public void setTotalEstimatedCostAsF(int totalEstimatedCostAsF) {
        this.totalEstimatedCostAsF = totalEstimatedCostAsF;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }
}
