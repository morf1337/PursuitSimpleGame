package com.team00;

import java.util.*;

public class PathFinder {
    private List<List<Character>> gameField;
    private int rows, cols;
    private Map<String, Character> characterMap;
    private boolean isPathToPlayer;

    public static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    public PathFinder(List<List<Character>> gameField, Map<String, Character> characterMap) {
        this.gameField = gameField;
        this.rows = gameField.size();
        this.cols = gameField.get(0).size();
        this.characterMap = characterMap;
        isPathToPlayer = true;
    }

    public boolean isValid(int x, int y) {
        return x >= 0 && x < rows && y >= 0 && y < cols;
    }

    public Node getNextStep(Node start, Node finish, boolean isPathToPlayer) {
        this.isPathToPlayer = isPathToPlayer;
        List<Node> path = findPath(start, finish);
        if (path == null) {
            return null;
        } else {
            return path.get(1);
        }
    }

    private List<Node> findPath(Node start, Node finish) {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<Node> closedSet = new HashSet<>();

        start.setAccumulatedCostAsG(0);
        start.setHeuristicEstimateAsH(calculateH(start, finish));
        start.setTotalEstimatedCostAsF(start.getAccumulatedCostAsG() + start.getHeuristicEstimateAsH());
        openSet.add(start);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.equals(finish)) {
                List<Node> fullPath = getFullPath(current);
                return fullPath;
            }

            closedSet.add(current);

            for (Node neighbor : getNeighbors(current)) {
                if (closedSet.contains(neighbor)) {
                    continue;
                }
                int newGValue = current.getAccumulatedCostAsG() + 1;

                if (!openSet.contains(neighbor) || newGValue < neighbor.getAccumulatedCostAsG()) {
                    neighbor.setParent(current);
                    neighbor.setAccumulatedCostAsG(newGValue);
                    neighbor.setHeuristicEstimateAsH(calculateH(neighbor, finish));
                    neighbor.setTotalEstimatedCostAsF(
                            neighbor.getAccumulatedCostAsG() + neighbor.getHeuristicEstimateAsH());

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }
        return null;
    }

    private List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();

        for (int[] dir : DIRECTIONS) {
            int newX = node.getX() + dir[0];
            int newY = node.getY() + dir[1];

            if (isValid(newX, newY) && gameField.get(newX).get(newY) != characterMap.get("wallChar")) {
                if (isPathToPlayer) {
                    if (gameField.get(newX).get(newY) != characterMap.get("goalChar")) {
                        neighbors.add(new Node(newX, newY));
                    }
                } else {
                    neighbors.add(new Node(newX, newY));
                }
            }
        }
        return neighbors;
    }

    private int calculateH(Node current, Node finish) {
        return (int) (Math.pow(current.getX() - finish.getX(), 2) + Math.pow(current.getY() - finish.getY(), 2));
    }

    private List<Node> getFullPath(Node node) {
        List<Node> path = new ArrayList<>();
        while (node != null) {
            path.add(node);
            node = node.getParent();
        }
        Collections.reverse(path);
        return path;
    }
}