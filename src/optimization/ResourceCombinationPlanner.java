package optimization;

import java.util.*;

public class ResourceCombinationPlanner {

    public static int minimumCost(
            int[] costs,
            int requiredResources) {

        int n = costs.length;

        int[] dp =
                new int[1 << n];

        Arrays.fill(
                dp,
                Integer.MAX_VALUE);

        dp[0] = 0;

        for (int mask = 0;
             mask < (1 << n);
             mask++) {

            if (dp[mask] ==
                    Integer.MAX_VALUE)
                continue;

            for (int i = 0; i < n; i++) {

                if ((mask & (1 << i)) == 0) {

                    int next =
                            mask | (1 << i);

                    dp[next] =
                            Math.min(
                                    dp[next],
                                    dp[mask]
                                    + costs[i]);
                }
            }
        }

        int answer =
                Integer.MAX_VALUE;

        for (int mask = 0;
             mask < (1 << n);
             mask++) {

            if (Integer.bitCount(mask)
                    == requiredResources) {

                answer =
                        Math.min(
                                answer,
                                dp[mask]);
            }
        }

        return answer;
    }
}