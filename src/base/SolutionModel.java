package base;

import java.util.Arrays;
import java.util.LinkedHashMap;

public class SolutionModel {
    private int[] userDelays;
    private LinkedHashMap<Integer, Integer> userToDriver = new LinkedHashMap<>();

    public SolutionModel(int[] userDelays, LinkedHashMap<Integer, Integer> userToDriver) {
        this.userDelays = userDelays;
        this.userToDriver = userToDriver;
    }

    public int[] getUserDelays() {
        return userDelays;
    }

    public LinkedHashMap<Integer, Integer> getUserToDriver() {
        return userToDriver;
    }


    public int calculateSolutionCost() {
        return Arrays.stream(userDelays).sum();
    }

}
