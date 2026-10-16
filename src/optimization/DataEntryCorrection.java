package optimization;

public class DataEntryCorrection {

    public static int difference(
            String a, String b) {

        int n = a.length();
        int m = b.length();

        int[][] dp =
                new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++)
            dp[i][0] = i;

        for (int j = 0; j <= m; j++)
            dp[0][j] = j;

        for (int i = 1; i <= n; i++) {

            for (int j = 1; j <= m; j++) {

                int cost =
                        a.charAt(i - 1)
                        == b.charAt(j - 1)
                        ? 0 : 1;

                dp[i][j] = Math.min(
                        Math.min(
                                dp[i - 1][j] + 1,
                                dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost);
            }
        }

        return dp[n][m];
    }

    public static String suggest(
            String entered,
            String[] validEntries) {

        String best = null;
        int bestDistance =
                Integer.MAX_VALUE;

        for (String entry : validEntries) {

            int distance =
                    difference(
                            entered.toLowerCase(),
                            entry.toLowerCase());

            if (distance < bestDistance) {

                bestDistance = distance;
                best = entry;
            }
        }

        return best;
    }
}