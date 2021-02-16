package a_star;

import base.Main;
import base.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;

public class TreeNode implements Comparable<TreeNode> {

    private int delay;
    private int[][] DTR;
    private final LinkedHashMap<Integer, Integer> assignmentMap = new LinkedHashMap<>();

    public TreeNode(int delay, int[][] DTR, LinkedHashMap<Integer, Integer> assignmentMap) {
        this.delay = delay;
        this.DTR = Utils.arrayCopy(DTR);
        this.assignmentMap.putAll(assignmentMap);
    }

    public int getDelay() {
        return delay;
    }

    public int getCost() {
        return delay;
    }

    public int[][] getDTR() {
        return DTR;
    }

    public LinkedHashMap<Integer, Integer> getAssignmentMap() {
        return assignmentMap;
    }

    @Override
    public int compareTo(@NotNull TreeNode otherNode) {
        if (otherNode.delay > this.delay) {
            return -1;
        } else if (otherNode.delay < this.delay) {
            return 1;
        }
        return 0;
    }

    public boolean isSolutionComplete() {
        return assignmentMap != null &&
                assignmentMap.size() == Main.USER_COUNT &&
                assignmentMap.entrySet().stream().filter(entry -> entry.getValue() == null).count() == 0;
    }
}
