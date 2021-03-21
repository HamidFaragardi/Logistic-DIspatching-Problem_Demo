package base;


import a_star.AStar;
import approximation.LeastLaxityFirst;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Scanner;


public class Main {

    public static int USER_COUNT;       // k
    public static int DRIVER_COUNT;     // j
    public static int RESTAURANT_COUNT; // i

    private int[][] DTR;
    private int[][] RTU;

    public static void main(String[] args) throws CloneNotSupportedException, IOException {
        Main main = new Main();
        InputParameters inputParameters = main.readDataFromFile();

        if (inputParameters == null) {
            return;
        }

        main.DTR = new int[Main.DRIVER_COUNT][Main.RESTAURANT_COUNT];
        main.RTU = new int[Main.RESTAURANT_COUNT][Main.USER_COUNT];
        main.calculateDistanceMatrices(inputParameters);

        LeastLaxityFirst lowLaxityFirst = new LeastLaxityFirst(
                inputParameters.getUsersPosition().clone(),
                inputParameters.getDriversPosition().clone(),
                inputParameters.getRestaurantsPosition().clone(),
                inputParameters.getUserExpectations().clone(),
                inputParameters.getUserFoodPreparation().clone(),
                inputParameters.getUserRestaurantIndex().clone(),
                Utils.arrayCopy(main.DTR),
                main.RTU.clone()
        );
        SolutionModel llfSolution = lowLaxityFirst.execute();
        writeResults(llfSolution.getUserToDriver(), "LLF");

        AStar aStar = new AStar(
                llfSolution,
                inputParameters.getUsersPosition().clone(),
                inputParameters.getDriversPosition().clone(),
                inputParameters.getRestaurantsPosition().clone(),
                inputParameters.getUserExpectations().clone(),
                inputParameters.getUserFoodPreparation().clone(),
                inputParameters.getUserRestaurantIndex().clone(),
                base.Utils.arrayCopy(main.DTR),
                main.RTU.clone()
        );
        LinkedHashMap<Integer, Integer> execute = aStar.execute();
        writeResults(execute, "A Star");
    }

    private static void writeResults(LinkedHashMap<Integer, Integer> userToDriver, String algorithmName) throws IOException {
        try {
            LinkedHashMap<String, String> result = userToDriverResult(userToDriver);
            JSONObject jsonObject = new JSONObject(result);

            String directoryPath = "src/main/java/results";
            File directory = new File(directoryPath);
            long fileCount = 0;

            if (!directory.exists()) {
                boolean mkdir = directory.mkdir();
                if (!mkdir) {
                    return;
                }
            }

            if (directory.list() != null) {
                fileCount = Arrays.asList(directory.list()).stream().filter(x -> x.contains(algorithmName)).count();
            }

            Files.write(
                    Paths.get(directoryPath + "/Results - " + algorithmName + " - " + (fileCount + 1) + ".json"),
                    jsonObject.toJSONString().getBytes()
            );
        } catch (Exception ex) {
            // TODO: Exception!
        }
    }

    private static LinkedHashMap<String, String> userToDriverResult(LinkedHashMap<Integer, Integer> userToDriver) {
        LinkedHashMap<String, String> results = new LinkedHashMap<>();
        userToDriver.forEach((userIndex, driverIndex) -> results.put("User " + (userIndex + 1), "Driver " + (driverIndex + 1)));
        return results;
    }

    private InputParameters readDataFromFile() {
        String jarParentPath;
        try {
            System.out.println("***********************************************************");
            System.out.println("Hint: Add the input file in json format to this directory:");
            URI jarFileUri = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            String jarFilePath = URLDecoder.decode(jarFileUri.getPath(), "UTF-8");
            jarParentPath = new File(jarFilePath).getParent();
            System.out.println(jarParentPath);
            System.out.println();
            System.out.println("The name of the input file should follow this pattern: <CASE_NUMBER>.json.");
            System.out.println("For example, for case one, the input file should be named: 1.json");
            System.out.println("***********************************************************");

            if (jarParentPath == null) {
                throw new Exception();
            }
        } catch (Exception e) {
            System.err.println("An error occurred while extracting the jar parent directory!");
            e.printStackTrace();
            return null;
        }

        while (true) {
            Scanner inputScanner = new Scanner(System.in);
            System.out.println("Enter case number (for example, for input file '1.json', the case number would be: '1'): ");
            String inputCaseRawStr = inputScanner.next();
            int caseNo;
            try {
                caseNo = Integer.parseInt(inputCaseRawStr);
            } catch (Exception ex) {
                System.err.println("Wrong input-file name format.");
                System.out.println("Try again!");
                continue;
            }


            File inputFile = new File(jarParentPath, caseNo + ".json");
            if (!inputFile.exists()) {
                System.out.println("Input file is: " + inputFile.getPath());
                System.out.println("It seems that the input file does not exist.");
                System.out.println("Try again!");
                continue;
            }

            InputParameters inputParameters = null;

            try {
                ObjectMapper mapper = new ObjectMapper();
                inputParameters = mapper.readValue(inputFile, InputParameters.class);
                if (inputParameters == null) {
                    throw new Exception();
                }
            } catch (Exception ex) {
                System.err.println("Wrong input json format.");
                return null;
            }

            return inputParameters;
        }
    }

    private void calculateDistanceMatrices(InputParameters inputParameters) {
        for (int j = 0; j < Main.DRIVER_COUNT; j++) {
            for (int i = 0; i < Main.RESTAURANT_COUNT; i++) {
                Position driverPosition = inputParameters.getDriversPosition()[j];
                Position restaurantPosition = inputParameters.getRestaurantsPosition()[i];
                DTR[j][i] = calculateTimeDistance(driverPosition, restaurantPosition);
            }
        }

        for (int k = 0; k < Main.USER_COUNT; k++) {
            for (int i = 0; i < Main.RESTAURANT_COUNT; i++) {
                Position userPosition = inputParameters.getUsersPosition()[k];
                Position restaurantPosition = inputParameters.getRestaurantsPosition()[i];
                RTU[i][k] = calculateTimeDistance(userPosition, restaurantPosition);
            }
        }
    }

    private int calculateTimeDistance(Position position1, Position position2) {
        int deltaX = Math.abs(position1.getX() - position2.getX());
        int deltaY = Math.abs(position1.getY() - position2.getY());

        int diagonalMovements = Math.min(deltaX, deltaY);
        int adjacentMovements = Math.abs(deltaX - deltaY);

        return diagonalMovements * Parameters.DIAGONAL_TIME + adjacentMovements * Parameters.ADJACENT_TIME;
    }
}
