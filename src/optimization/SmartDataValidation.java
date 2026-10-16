package optimization;

public class SmartDataValidation {

    public static int calculate(
            String a,
            String b,
            int insertCost,
            int deleteCost,
            int replaceCost) {

        int n = a.length();
        int m = b.length();

        int[][] dp =
                new int[n + 1][m + 1];

        for (int i = 1; i <= n; i++)
            dp[i][0] =
                    dp[i - 1][0] + deleteCost;

        for (int j = 1; j <= m; j++)
            dp[0][j] =
                    dp[0][j - 1] + insertCost;

        for (int i = 1; i <= n; i++) {

            for (int j = 1; j <= m; j++) {

                int replace =
                        dp[i - 1][j - 1]
                        + (a.charAt(i - 1)
                        == b.charAt(j - 1)
                        ? 0 : replaceCost);

                int insert =
                        dp[i][j - 1] + insertCost;

                int delete =
                        dp[i - 1][j] + deleteCost;

                dp[i][j] =
                        Math.min(
                                replace,
                                Math.min(insert, delete));
            }
        }

        return dp[n][m];
    }
}