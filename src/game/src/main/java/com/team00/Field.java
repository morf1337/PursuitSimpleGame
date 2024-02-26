package com.team00;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Collections;

import com.diogonunes.jcdp.color.api.Ansi;
import com.diogonunes.jcdp.color.api.Ansi.BColor;
import com.diogonunes.jcdp.color.ColoredPrinter;

public class Field {
    private int fieldSize;
    private Random random = new Random();

    private GameEntity player = new GameEntity(0, 0, EntityType.PLAYER);
    private GameEntity goal = new GameEntity(0, 0, EntityType.GOALPOINT);

    private List<GameEntity> enemies = new ArrayList<>();
    private List<GameEntity> walls = new ArrayList<>();

    private Map<String, Character> chars;
    private Map<String, Ansi.BColor> colors;
    private Map<String, Integer> params;

    private List<List<Character>> field;


    public Field(Map<String, Integer> params, Map<String, Character> chars, Map<String, Ansi.BColor> colors) {
        this.params = params;
        this.chars = chars;
        this.colors = colors;
        fieldSize = params.get("size");
        generateField();
    }

    public void generateField() {
        field = new ArrayList<>(fieldSize);
        enemies.clear();
        walls.clear();
        for (int i = 0; i < fieldSize; i++) {
            ArrayList<Character> row = new ArrayList<>(fieldSize);
            for (int j = 0; j < fieldSize; j++) {
                row.add(chars.get("emptyChar"));
            }
            field.add(row);
        }

        generateObjects(chars.get("playerChar"), Args.CELLS_PLAYER);
        generateObjects(chars.get("goalChar"), Args.CELLS_TARGET);
        generateObjects(chars.get("wallChar"), params.get("wallsCount"));
        generateObjects(chars.get("enemyChar"), params.get("enemiesCount"));
    }

    public void updateField() {
        for (List<Character> row : field) {
            Collections.fill(row, chars.get("emptyChar"));
        }
        for (GameEntity wall : walls) {
            field.get(wall.getX()).set(wall.getY(), chars.get("wallChar"));
        }
        for (GameEntity enemy : enemies) {
            field.get(enemy.getX()).set(enemy.getY(), chars.get("enemyChar"));
        }
        field.get(player.getX()).set(player.getY(), chars.get("playerChar"));
        field.get(goal.getX()).set(goal.getY(), chars.get("goalChar"));
    }

    public void printField() {
        for (List<Character> row : field) {
            for (Character cell : row) {
                printColoredCharacter(cell);
            }
            System.out.println();
        }
    }

    public List<List<Character>> getField() {
        return field;
    }

    public List<GameEntity> getEnemiesList() {
        return enemies;
    }

    public GameEntity getPlayer() {
        return player;
    }

    public GameEntity getGoal() {
        return goal;
    }

    private void generateObjects(char charForObject, int count) {
        int x, y;
        for (int i = 0; i < count; i++) {
            do {
                x = random.nextInt(fieldSize);
                y = random.nextInt(fieldSize);
            } while (field.get(x).get(y) != chars.get("emptyChar"));

            if (charForObject == chars.get("playerChar")) {
                player.setX(x);
                player.setY(y);
            } else if (charForObject == chars.get("goalChar")) {
                goal.setX(x);
                goal.setY(y);
            } else if (charForObject == chars.get("enemyChar")) {
                enemies.add(new GameEntity(x, y, EntityType.ENEMY));
            } else {
                walls.add(new GameEntity(x, y, EntityType.WALL));
            }
            updateField();
        }
    }

    private void printColoredCharacter(char character) {
        ColoredPrinter cp = new ColoredPrinter.Builder(1, false)
                .foreground(Ansi.FColor.BLACK)
                .background(getBackgroundColor(character))
                .build();
        cp.print(character);
    }

    private Ansi.BColor getBackgroundColor(char character) {
        if (character == chars.get("enemyChar")) {
            return colors.get("enemyColor");
        } else if (character == chars.get("playerChar")) {
            return colors.get("playerColor");
        } else if (character == chars.get("wallChar")) {
            return colors.get("wallColor");
        } else if (character == chars.get("goalChar")) {
            return colors.get("goalColor");
        } else if (character == chars.get("emptyChar")) {
            return colors.get("emptyColor");
        }
        return BColor.BLACK;
    }
}