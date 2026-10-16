package factory;

import java.util.*;

public class MaintenancePatternFinder {

    public static List<Integer> findPattern(
            String text, String pattern) {

        List<Integer> result = new ArrayList<>();

        String combined =
                pattern + "$" + text;

        int n = combined.length();

        int[] z = new int[n];

        int left = 0;
        int right = 0;

        for (int i = 1; i < n; i++) {

            if (i <= right)
                z[i] = Math.min(
                        right - i + 1,
                        z[i - left]);

            while (i + z[i] < n &&
                    combined.charAt(z[i])
                    == combined.charAt(i + z[i])) {

                z[i]++;
            }

            if (i + z[i] - 1 > right) {
                left = i;
                right = i + z[i] - 1;
            }

            if (z[i] == pattern.length()) {
                result.add(
                        i - pattern.length() - 1);
            }
        }

        return result;
    }
}
