package optimization;

import java.util.*;

public class MachineSelection {

    public static List<Integer> selectMachines(
            int[] capacity,
            int required) {

        int n = capacity.length;

        int bestMask = 0;
        int bestCapacity = Integer.MAX_VALUE;

        for (int mask = 1;
             mask < (1 << n);
             mask++) {

            int total = 0;

            for (int i = 0; i < n; i++) {

                if ((mask & (1 << i)) != 0)
                    total += capacity[i];
            }

            if (total >= required &&
                    total < bestCapacity) {

                bestCapacity = total;
                bestMask = mask;
            }
        }

        List<Integer> result =
                new ArrayList<>();

        for (int i = 0; i < n; i++) {

            if ((bestMask & (1 << i)) != 0)
                result.add(i);
        }

        return result;
    }
}