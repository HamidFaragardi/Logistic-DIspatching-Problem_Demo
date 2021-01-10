package base;

import a_star.AStar;
import approximation.LeastLaxityFirst;
import javafx.util.Pair;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private Pattern readPositionsPattern = Pattern.compile("(?:\\((.*?,.*?)\\))+");

    public static int USER_COUNT;       // k
    public static int DRIVER_COUNT;     // j
    public static int RESTAURANT_COUNT; // i

    private Position[] usersPosition;           // Index: 0:k-1
    private Position[] driversPosition;         // Index: 0:j-1
    private Position[] restaurantsPosition;     // Index: 0:i-1
    private int[] userExpectations;         // Index: 0:k-1
    private int[] userFoodPreparation;      // Index: 0:k-1
    private int[] userRestaurantIndex;      // Index: 0:k-1, values: 0:i-1

    private int[][] DTR;
    private int[][] RTU;
    private int case_No = 2; //If it is equal to one, the first case (Q 2.a) is executed; otherwise the second case (Q 2.d)
    public static void main(String[] args) {
        Main main = new Main();
        boolean wasFetchingDataSuccessful = main.readDataFromFile();

        if (!wasFetchingDataSuccessful) {
            return;
        }

        main.DTR = new int[Main.DRIVER_COUNT][Main.RESTAURANT_COUNT];
        main.RTU = new int[Main.RESTAURANT_COUNT][Main.USER_COUNT];
        main.calculateDistanceMatrices();

        LeastLaxityFirst lowLaxityFirst = new LeastLaxityFirst(
                main.usersPosition.clone(),
                main.driversPosition.clone(),
                main.restaurantsPosition.clone(),
                main.userExpectations.clone(),
                main.userFoodPreparation.clone(),
                main.userRestaurantIndex.clone(),
                Utils.arrayCopy(main.DTR),
                main.RTU.clone()
        );
        SolutionModel llfSolution = lowLaxityFirst.execute();

        AStar aStar = new AStar(
                llfSolution,
                main.usersPosition.clone(),
                main.driversPosition.clone(),
                main.restaurantsPosition.clone(),
                main.userExpectations.clone(),
                main.userFoodPreparation.clone(),
                main.userRestaurantIndex.clone(),
                base.Utils.arrayCopy(main.DTR),
                main.RTU.clone()
        );
        aStar.execute();
    }

    private boolean readDataFromFile() {
        String fileName =  "src/sample_input/example_1";
        if (case_No!=1)
            fileName = "src/sample_input/example_2";
        try {
            Files.lines(Paths.get(fileName)).forEach(line -> {
                if (line.toLowerCase().contains("driver".toLowerCase())) {
                    driversPosition = initializePositionArray(line);
                    DRIVER_COUNT = driversPosition.length;
                } else if (line.toLowerCase().contains("users =".toLowerCase())) {
                    usersPosition = initializePositionArray(line);
                    USER_COUNT = usersPosition.length;
                } else if (line.toLowerCase().contains("restaurants =".toLowerCase())) {
                    restaurantsPosition = initializePositionArray(line);
                    RESTAURANT_COUNT = restaurantsPosition.length;
                } else if (line.toLowerCase().contains("expectation =".toLowerCase())) {
                    userExpectations = initializeIntegerArrays(line);
                } else if (line.toLowerCase().contains("preparation".toLowerCase())) {
                    userFoodPreparation = initializeIntegerArrays(line);
                } else if (line.toLowerCase().contains("the number of the chosen restaurant per user".toLowerCase())) {
                    userRestaurantIndex = initializeIndexArrays(line);
                }
            });
            return true;
        } catch (Exception ex) {
            System.err.println("An error occurred while reading data from the input file.");
            return false;
        }
    }

    private Position[] initializePositionArray(String line) {
        Matcher matcher = readPositionsPattern.matcher(line);
        List<Pair<Integer, Integer>> positionsList = new ArrayList<>();
        while (matcher.find()) {
            String matchedText = matcher.group(1);
            positionsList.add(new Pair<>(Integer.parseInt(matchedText.split(",")[0]) - 1, Integer.parseInt(matchedText.split(",")[1]) - 1));
        }

        Position[] positions = new Position[positionsList.size()];
        for (int i = 0; i < positionsList.size(); i++) {
            positions[i] = new Position(positionsList.get(i).getKey(), positionsList.get(i).getValue());
        }
        return positions;
    }

    private int[] initializeIntegerArrays(String line) {
        String expectations = line.substring(line.indexOf("{") + 1, line.indexOf("}"));
        String[] splittedArr = expectations.split(",");
        int[] array = new int[splittedArr.length];
        for (int i = 0; i < splittedArr.length; i++) {
            array[i] = Integer.parseInt(splittedArr[i].trim());
        }
        return array;
    }

    private int[] initializeIndexArrays(String line) {
        String expectations = line.substring(line.indexOf("{") + 1, line.indexOf("}"));
        String[] splittedArr = expectations.split(",");
        int[] array = new int[splittedArr.length];
        for (int i = 0; i < splittedArr.length; i++) {
            array[i] = Integer.parseInt(splittedArr[i].trim()) - 1;
        }
        return array;
    }


    private void calculateDistanceMatrices() {
        for (int j = 0; j < Main.DRIVER_COUNT; j++) {
            for (int i = 0; i < Main.RESTAURANT_COUNT; i++) {
                Position driverPosition = driversPosition[j];
                Position restaurantPosition = restaurantsPosition[i];
                DTR[j][i] = calculateTimeDistance(driverPosition, restaurantPosition);
            }
        }

        for (int k = 0; k < Main.USER_COUNT; k++) {
            for (int i = 0; i < Main.RESTAURANT_COUNT; i++) {
                Position userPosition = usersPosition[k];
                Position restaurantPosition = restaurantsPosition[i];
                RTU[i][k] = calculateTimeDistance(userPosition, restaurantPosition);
            }
        }
    }

    private int calculateTimeDistance(Position position1, Position position2) {
        int deltaX = Math.abs(position1.x - position2.x);
        int deltaY = Math.abs(position1.y - position2.y);

        int diagonalMovements = Math.min(deltaX, deltaY);
        int adjacentMovements = Math.abs(deltaX - deltaY);

        return diagonalMovements * Parameters.DIAGONAL_TIME + adjacentMovements * Parameters.ADJACENT_TIME;
    }
}