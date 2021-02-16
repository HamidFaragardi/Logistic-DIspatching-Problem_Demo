package a_star;

import base.*;

import java.util.LinkedHashMap;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;

public class AStar {

    private SolutionModel llfSolution;
    private Position[] usersPosition = new Position[Main.USER_COUNT];                // Index: 0:k-1
    private Position[] driversPosition = new Position[Main.DRIVER_COUNT];            // Index: 0:j-1
    private Position[] restaurantsPosition = new Position[Main.USER_COUNT];          // Index: 0:i-1
    private int[] userExpectations = new int[Main.USER_COUNT];         // Index: 0:k-1
    private int[] userFoodPreparation = new int[Main.USER_COUNT];      // Index: 0:k-1
    private int[] userRestaurantIndex = new int[Main.USER_COUNT];      // Index: 0:k-1, values: 0:i-1
    private int[] userDelays = new int[Main.USER_COUNT];

    private LinkedHashMap<Integer, Integer> userToDriver = new LinkedHashMap<>(); // Which user is assigned to which driver
    private int totalDelay = 0;
    private int[][] DTR = new int[Main.DRIVER_COUNT][Main.RESTAURANT_COUNT];
    private int[][] RTU = new int[Main.RESTAURANT_COUNT][Main.USER_COUNT];

    public AStar(
            SolutionModel llfSolution,
            Position[] usersPosition,
            Position[] driversPosition,
            Position[] restaurantsPosition,
            int[] userExpectations,
            int[] userFoodPreparation,
            int[] userRestaurantIndex,
            int[][] DTR,
            int[][] RTU
    ) {
        this.llfSolution = llfSolution;
        this.usersPosition = usersPosition;
        this.driversPosition = driversPosition;
        this.restaurantsPosition = restaurantsPosition;
        this.userExpectations = userExpectations;
        this.userFoodPreparation = userFoodPreparation;
        this.userRestaurantIndex = userRestaurantIndex;
        this.DTR = Utils.arrayCopy(DTR);
        this.RTU = RTU;
    }

    public LinkedHashMap<Integer, Integer> execute() {
        // Starting point
        int upperBoundCost = llfSolution.calculateSolutionCost();
        LinkedHashMap<Integer, Integer> bestSolutionSoFar = new LinkedHashMap<>(llfSolution.getUserToDriver());

        // Defining and initializing priority queue
        PriorityQueue<TreeNode> priorityQueue = new PriorityQueue<>();
        priorityQueue.add(new TreeNode(0, this.DTR, new LinkedHashMap<>()));

        // Start iterating and traversing solution tree
        while (priorityQueue.peek() != null) {
            TreeNode node = priorityQueue.poll();
            if (node.isSolutionComplete()) {
                // Solution is complete. Check whether it is a better solution in comparison with the existing one or not.
                if (node.getCost() < upperBoundCost) {
                    upperBoundCost = node.getCost();
                    bestSolutionSoFar = new LinkedHashMap<>(node.getAssignmentMap());
                }
            } else {
                for (int k = 0; k < Main.USER_COUNT; k++) {
                    if (node.getAssignmentMap().containsKey(new Integer(k))) {
                        // This user has been already served.
                        continue;
                    }

                    int restaurantIndex = userRestaurantIndex[k];

                    for (int j = 0; j < Main.DRIVER_COUNT; j++) {
                        int deliveryTimeToCurrentUser = Math.max(node.getDTR()[j][restaurantIndex], userFoodPreparation[k]) + RTU[restaurantIndex][k];
                        int estimatedDelayForOtherUsers = delayEstimationForOtherUsersHeuristic(node);

                        int currentUserDelay = Math.max(0, deliveryTimeToCurrentUser - userExpectations[k]);

                        if (node.getCost() + currentUserDelay + estimatedDelayForOtherUsers < upperBoundCost) {
                            int[][] newNodeDTR = Utils.arrayCopy(node.getDTR());
                            // Update DTR
                            for (int i = 0; i < Main.RESTAURANT_COUNT; i++) {
                                newNodeDTR[j][i] = RTU[i][k] + deliveryTimeToCurrentUser;
                            }
                            LinkedHashMap<Integer, Integer> newNodeAssignmentMap = new LinkedHashMap<>(node.getAssignmentMap());
                            newNodeAssignmentMap.put(k, j);
                            TreeNode newNode = new TreeNode(node.getCost() + currentUserDelay + estimatedDelayForOtherUsers, newNodeDTR, newNodeAssignmentMap);
                            priorityQueue.add(newNode);
                        }
                    }
                }
            }
        }

        userToDriver = bestSolutionSoFar;
        totalDelay = upperBoundCost;

        printResults();

        return userToDriver;
    }

    public int delayEstimationForOtherUsersHeuristic(TreeNode node) {
        int heuristicDelayForOtherUsers = 0;

        Set<Integer> assignedUsers = node.getAssignmentMap().entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        for (int k = 0; k < Main.USER_COUNT; k++) {
            if (!assignedUsers.contains(k)) {
                int restaurantIndex = userRestaurantIndex[k];
                int closestDriverToRestaurantOfCurrentUser = Utils.findClosestDriverToARestaurant(DTR, restaurantIndex);
                heuristicDelayForOtherUsers += RTU[userRestaurantIndex[k]][k] + DTR[closestDriverToRestaurantOfCurrentUser][restaurantIndex];
            }
        }
        return heuristicDelayForOtherUsers;
    }

    private void printResults() {
        System.out.println("\n\n---------- A* ---------\n");
        System.out.println("Users and drivers assignments:");
        userToDriver.forEach((userIndex, driverIndex) -> System.out.printf("User %d is assigned to driver %d.\n", userIndex + 1, driverIndex + 1));

        System.out.println("\nTotal delay is: " + totalDelay);
    }
}
