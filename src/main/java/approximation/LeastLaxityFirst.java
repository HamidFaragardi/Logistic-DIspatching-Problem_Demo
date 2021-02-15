package approximation;

import base.*;

import java.util.LinkedHashMap;
import java.util.stream.IntStream;

public class LeastLaxityFirst {

    private Position[] usersPosition = new Position[Main.USER_COUNT];                // Index: 0:k-1
    private Position[] driversPosition = new Position[Main.DRIVER_COUNT];            // Index: 0:j-1
    private Position[] restaurantsPosition = new Position[Main.USER_COUNT];          // Index: 0:i-1
    private int[] userExpectations = new int[Main.USER_COUNT];         // Index: 0:k-1
    private int[] userFoodPreparation = new int[Main.USER_COUNT];      // Index: 0:k-1
    private int[] userRestaurantIndex = new int[Main.USER_COUNT];      // Index: 0:k-1, values: 0:i-1

    private int[][] ORIGINAL_DTR = new int[Main.DRIVER_COUNT][Main.RESTAURANT_COUNT];
    private int[][] DTR = new int[Main.DRIVER_COUNT][Main.RESTAURANT_COUNT];
    private int[][] RTU = new int[Main.RESTAURANT_COUNT][Main.USER_COUNT];
    private int[] laxity = new int[Main.USER_COUNT];
    private int[] sortedUserIndexBasedOnLaxity = new int[Main.USER_COUNT]; // Each item of this array is an index of the user (items are between 0:k-1)
    private int[] userDelays = new int[Main.USER_COUNT];

    private LinkedHashMap<Integer, Integer> userToDriver = new LinkedHashMap<>(); // Which user is assigned to which driver



    public LeastLaxityFirst(
            Position[] usersPosition,
            Position[] driversPosition,
            Position[] restaurantsPosition,
            int[] userExpectations,
            int[] userFoodPreparation,
            int[] userRestaurantIndex,
            int[][] DTR,
            int[][] RTU
    ) {
        this.usersPosition = usersPosition;
        this.driversPosition = driversPosition;
        this.restaurantsPosition = restaurantsPosition;
        this.userExpectations = userExpectations;
        this.userFoodPreparation = userFoodPreparation;
        this.userRestaurantIndex = userRestaurantIndex;
        this.ORIGINAL_DTR = DTR;
        this.DTR = Utils.arrayCopy(DTR);
        this.RTU = RTU;
    }

    public SolutionModel execute() {
        calculateLaxity();
        sortUsersBasedOnLaxity();
        iterate();
        printResults();
        return (new SolutionModel(userDelays.clone(), (LinkedHashMap) (userToDriver).clone()));
    }

    private void printResults() {
        System.out.println("---------- Least Laxity First ---------\n");
        System.out.println("Users and drivers assignments:");
        userToDriver.forEach((userIndex, driverIndex) -> System.out.printf("User %d is assigned to driver %d.\n", userIndex + 1, driverIndex + 1));

        System.out.println("\nTotal delay is: " + new SolutionModel(userDelays, userToDriver).calculateSolutionCost());
    }

    private void sortUsersBasedOnLaxity() {
        sortedUserIndexBasedOnLaxity = IntStream.range(0, laxity.length)
                .boxed().sorted((i, j) -> new Integer(laxity[i]).compareTo(laxity[j]))
                .mapToInt(ele -> ele).toArray();
    }

    private void calculateLaxity() {
        for (int k = 0; k < Main.USER_COUNT; k++) {
            laxity[k] = userExpectations[k] - (userFoodPreparation[k] + RTU[userRestaurantIndex[k]][k]);
        }
    }


    private void iterate() {
        // For each order
        for (int k = 0; k < Main.USER_COUNT; k++) {
            int currentSortedUserIndex = sortedUserIndexBasedOnLaxity[k];
            int restaurantIndex = userRestaurantIndex[currentSortedUserIndex];

            // Find the closest rider to the restaurant of the customer according to DTR
            int closestDriverToRestaurantOfCurrentUser = Utils.findClosestDriverToARestaurant(DTR, restaurantIndex);

            // Assign the rider to the order
            userToDriver.put(currentSortedUserIndex, closestDriverToRestaurantOfCurrentUser);

            // Update the time distance matrix DTR
            int deliveryTimeToCurrentUser =
                    Math.max(
                            DTR[closestDriverToRestaurantOfCurrentUser][restaurantIndex],
                            userFoodPreparation[currentSortedUserIndex]
                    ) + RTU[restaurantIndex][currentSortedUserIndex];

            for (int i = 0; i < Main.RESTAURANT_COUNT; i++) {
                DTR[closestDriverToRestaurantOfCurrentUser][i] = RTU[i][currentSortedUserIndex] + deliveryTimeToCurrentUser;
            }

            // Calculate the delay of this order
            userDelays[currentSortedUserIndex] = Math.max(0, deliveryTimeToCurrentUser - userExpectations[currentSortedUserIndex]);
        }
    }


}
