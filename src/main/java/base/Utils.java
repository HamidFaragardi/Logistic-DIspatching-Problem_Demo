package base;

import java.util.Arrays;
import java.util.stream.IntStream;

public class Utils {

    public static int calculateSolutionCost(
            SolutionModel solution,
            int[][] originalDTR,
            int[][] RTU,
            int[] userExpectations,
            int[] userRestaurantIndex
    ) {
        int[][] DTR = arrayCopy(originalDTR);
        int[] userDelays = new int[userExpectations.length];
        solution.getUserToDriver().entrySet().forEach(userToDriverEntry -> {
            int userIndex = userToDriverEntry.getKey();
            int driverIndex = userToDriverEntry.getValue();
            int restaurantIndex = userRestaurantIndex[userIndex];

            int deliveryTimeToCurrentUser = DTR[driverIndex][restaurantIndex] + RTU[restaurantIndex][userIndex];

            for (int i = 0; i < Main.RESTAURANT_COUNT; i++) {
                DTR[driverIndex][i] = DTR[driverIndex][i] + deliveryTimeToCurrentUser;
            }

            userDelays[userIndex] = Math.max(0, deliveryTimeToCurrentUser - userExpectations[userIndex]);

        });

        return Arrays.stream(userDelays).sum();
    }

    public static int[][] arrayCopy(int[][] array) {
        return Arrays.stream(array).map((ts) -> ts.clone()).toArray(a -> array.clone());
    }

    public static int[] getColumn(int[][] matrix, int column) {
        return IntStream.range(0, matrix.length).map(i -> matrix[i][column]).toArray();
    }

    public static int findClosestDriverToARestaurant(int [][] DTR, int restaurantIndex) {
        // Just to make sure the 2D array never changes unintentionally
        int[][] clonedDTR = Utils.arrayCopy(DTR);
        int[] driverDistancesToRestaurantOfCurrentUser = getColumn(clonedDTR, restaurantIndex);
        int closestDriverToRestaurantOfCurrentUser = IntStream.range(0, driverDistancesToRestaurantOfCurrentUser.length).boxed()
                .sorted((i, j) -> new Integer(driverDistancesToRestaurantOfCurrentUser[i]).compareTo(driverDistancesToRestaurantOfCurrentUser[j]))
                .mapToInt(ele -> ele)
                .toArray()[0];
        return closestDriverToRestaurantOfCurrentUser;
    }
}
