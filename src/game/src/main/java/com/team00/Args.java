package com.team00;

import java.util.HashMap;
import java.util.Map;
import java.io.InputStream;
import java.util.Properties;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.diogonunes.jcdp.color.api.Ansi;

@Parameters(separators = "=")
public class Args {
    public static final int CELLS_PLAYER = 1;
    public static final int CELLS_TARGET = 1;

    @Parameter(names = "--enemiesCount", required = true)
    private int enemiesCount;

    @Parameter(names = "--wallsCount", required = true)
    private int wallsCount;

    @Parameter(names = "--size", required = true)
    private int fieldSize;

    @Parameter(names = "--profile", required = true)
    private String profile;

    private Map<String, Character> chars = new HashMap<>();
    private Map<String, Ansi.BColor> colors = new HashMap<>();
    private Map<String, Integer> params = new HashMap<>();

    public void prepareArgumentsAndProperties() {
        validateParams();
        Properties properties = new Properties();

        String propertiesName = "application-" + profile + ".properties";
        try (InputStream propertiesStream = Main.class.getClassLoader().getResourceAsStream(propertiesName)) {
            properties.load(propertiesStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String emptyString = properties.getProperty("empty.char");
        char emptyChar = ' ';
        if (!emptyString.isEmpty()) {
            emptyChar = emptyString.charAt(0);
        }
        chars.put("enemyChar", properties.getProperty("enemy.char").charAt(0));
        chars.put("playerChar", properties.getProperty("player.char").charAt(0));
        chars.put("wallChar", properties.getProperty("wall.char").charAt(0));
        chars.put("goalChar", properties.getProperty("goal.char").charAt(0));
        chars.put("emptyChar", emptyChar);

        colors.put("enemyColor", Ansi.BColor.valueOf(properties.getProperty("enemy.color")));
        colors.put("playerColor", Ansi.BColor.valueOf(properties.getProperty("player.color")));
        colors.put("wallColor", Ansi.BColor.valueOf(properties.getProperty("wall.color")));
        colors.put("goalColor", Ansi.BColor.valueOf(properties.getProperty("goal.color")));
        colors.put("emptyColor", Ansi.BColor.valueOf(properties.getProperty("empty.color")));

        params.put("enemiesCount", enemiesCount);
        params.put("wallsCount", wallsCount);
        params.put("size", fieldSize);
        params.put("isProfileDev", profile.equals("dev") ? 1 : 0);
    }

    public Map<String, Character> getChars() {
        return chars;
    }

    public Map<String, Ansi.BColor> getColors() {
        return colors;
    }

    public Map<String, Integer> getParams() {
        return params;
    }

    private void validateParams() {
        int totalCells = fieldSize * fieldSize;
        int requiredCells = CELLS_PLAYER + CELLS_TARGET + enemiesCount + wallsCount;

        if (enemiesCount < 0 || wallsCount < 0 || fieldSize < 0) {
            throw new IllegalParametersException(String.format("all parameters must be positive"));
        } else if (totalCells < requiredCells) {
            throw new IllegalParametersException(
                    "Invalid parameters: The field dimensions do not allow all required elements to be placed. Please adjust the application launch parameters.");
        }
    }
}
