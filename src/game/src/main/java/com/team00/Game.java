package com.team00;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import com.beust.jcommander.JCommander;

public class Game {
    private Scanner scanner;
    private Field fieldObj;
    private PathFinder finder;
    private Map<String, Character> characterMap;
    private boolean isProfileDev;

    public Game(String[] args) {
        Args arguments = new Args();
        JCommander.newBuilder().addObject(arguments).build().parse(args);
        try {
            arguments.prepareArgumentsAndProperties();
        } catch (IllegalParametersException e) {
            System.err.println("Error: " + e.toString());
            System.exit(1);
        }

        scanner = new Scanner(System.in);
        characterMap = arguments.getChars();
        isProfileDev = arguments.getParams().get("isProfileDev") == 1;
        fieldObj = new Field(arguments.getParams(), characterMap, arguments.getColors());
        finder = new PathFinder(fieldObj.getField(), characterMap);

        generateValidField();
    }

    public void gameLoop() {
        GameEntity player = fieldObj.getPlayer();
        GameEntity goal = fieldObj.getGoal();
        Node playerNextStep = new Node(player.getX(), player.getY());

        boolean winner = true;
        printStartingGreetings();

        while (true) {
            fieldObj.updateField();
            fieldObj.printField();

            if (isPlayerBlocked() || processInput(playerNextStep) == '9') {
                winner = false;
                break;
            } else if (playerNextStep.equals(new Node(goal.getX(), goal.getY()))) {
                break;
            } else if (enemiesPursuitSuccess(playerNextStep)) {
                winner = false;
                break;
            }
        }

        printGoodBye(winner);
        scanner.close();
    }

    private void generateValidField() {
        int generationAttempts = 0;
        List<List<Character>> gameField = fieldObj.getField();
        GameEntity player = fieldObj.getPlayer();
        GameEntity goal = fieldObj.getGoal();

        while (finder.getNextStep(new Node(player.getX(), player.getY()), new Node(goal.getX(), goal.getY()),
                false) == null) {
            fieldObj.generateField();
            finder = new PathFinder(gameField, characterMap);
            generationAttempts++;
            if (generationAttempts > 100) {
                System.out.println("Too many attempts to generate, exiting...");
                System.exit(-1);
            }
        }
    }

    private void printStartingGreetings() {
        System.out.println(
                "Hi! This is The Game!!! For win, You need touch goal before you be caught by enemies. May force with you!");
        System.out.println("If you will want stop game, please, click button 9");
        System.out.println("Game will be started in ...");
        for (int i = 3; i > 0; i--) {
            try {
                System.out.println(i);
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Start!");
    }

    private void printGoodBye(boolean winner) {
        if (winner) {
            System.out.println("CONGRATS!!! YOU WIN :]");
        } else {
            System.out.println("YOU LOSE!! HA HA HA");
            System.out.println("=====GAME OVER=====");
        }
    }

    private boolean isPlayerBlocked() {
        GameEntity player = fieldObj.getPlayer();
        List<List<Character>> gameField = fieldObj.getField();
        int blockedDirections = 0;
        for (int[] dir : PathFinder.DIRECTIONS) {
            int newX = player.getX() + dir[0];
            int newY = player.getY() + dir[1];
            if (!finder.isValid(newX, newY) ||
                    (gameField.get(newX).get(newY) != characterMap.get("emptyChar")
                            && gameField.get(newX).get(newY) != characterMap.get("goalChar"))) {
                blockedDirections++;
            }
        }
        return blockedDirections == 4;
    }

    private char processInput(Node playerNextStep) {
        GameEntity player = fieldObj.getPlayer();
        List<List<Character>> gameField = fieldObj.getField();
        System.out.println(
                "Player, your move, please choose buttons for directions: W - up, S - down, A - left, D - right");

        char inputChar = '0';
        boolean validInput = false;
        while (!validInput) {
            playerNextStep.setX(player.getX());
            playerNextStep.setY(player.getY());
            inputChar = scanner.next().charAt(0);
            if (inputChar == '9') {
                return inputChar;
            }
            switch (Character.toUpperCase(inputChar)) {
                case 'W':
                    playerNextStep.setX(player.getX() - 1);
                    break;
                case 'S':
                    playerNextStep.setX(player.getX() + 1);
                    break;
                case 'A':
                    playerNextStep.setY(player.getY() - 1);
                    break;
                case 'D':
                    playerNextStep.setY(player.getY() + 1);
                    break;
                default:
                    break;
            }

            if (finder.isValid(playerNextStep.getX(), playerNextStep.getY()) &&
                    (gameField.get(playerNextStep.getX()).get(playerNextStep.getY()) == characterMap.get("emptyChar") ||
                            gameField.get(playerNextStep.getX()).get(playerNextStep.getY()) == characterMap
                                    .get("goalChar"))) {
                player.setX(playerNextStep.getX());
                player.setY(playerNextStep.getY());
                validInput = true;
            } else {
                System.out.println("Chosen way is blocked or button is not correct, try other directions!");
            }
        }
        return inputChar;
    }

    private boolean enemiesPursuitSuccess(Node playerNextStep) {
        GameEntity player = fieldObj.getPlayer();
        List<GameEntity> enemies = fieldObj.getEnemiesList();
        Node enemyNextStep;
        List<List<Character>> gameField = fieldObj.getField();
        System.out.println("Enemies moves:");
        for (GameEntity enemy : enemies) {
            fieldObj.updateField();
            finder = new PathFinder(gameField, characterMap);
            enemyNextStep = finder.getNextStep(new Node(enemy.getX(), enemy.getY()),
                    new Node(player.getX(), player.getY()), true);
            if (enemyNextStep != null
                    && gameField.get(enemyNextStep.getX()).get(enemyNextStep.getY()) != characterMap.get("enemyChar")) {
                if (isProfileDev) {
                    System.out.println("Confirm enemy №" + (enemies.indexOf(enemy) + 1) + " move by entering 8: ");
                    while (true) {
                        if (scanner.next().charAt(0) == '8') {
                            break;
                        } else {
                            System.out.println("Only 8 can save you from this infinite loop. Try again, please!");
                        }
                    }
                }

                enemy.setX(enemyNextStep.getX());
                enemy.setY(enemyNextStep.getY());
                if (!isProfileDev) {
                    fieldObj.printField();
                    System.out.println("---");
                }
            }
            if (enemyNextStep != null && enemyNextStep.equals(playerNextStep)) {
                return true;
            }
        }
        return false;
    }
}
