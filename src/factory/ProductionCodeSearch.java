package factory;

public class ProductionCodeSearch {

    public static boolean search(
            String text, String pattern) {

        if (pattern.length() > text.length())
            return false;

        int base = 256;
        int prime = 101;

        int m = pattern.length();
        int n = text.length();

        int high = 1;

        for (int i = 0; i < m - 1; i++)
            high = (high * base) % prime;

        int patternHash = 0;
        int textHash = 0;

        for (int i = 0; i < m; i++) {

            patternHash =
                    (base * patternHash
                            + pattern.charAt(i))
                            % prime;

            textHash =
                    (base * textHash
                            + text.charAt(i))
                            % prime;
        }

        for (int i = 0; i <= n - m; i++) {

            if (patternHash == textHash) {

                boolean same = true;

                for (int j = 0; j < m; j++) {

                    if (text.charAt(i + j)
                            != pattern.charAt(j)) {

                        same = false;
                        break;
                    }
                }

                if (same)
                    return true;
            }

            if (i < n - m) {

                textHash =
                        (base *
                        (textHash -
                        text.charAt(i) * high)
                        + text.charAt(i + m))
                        % prime;

                if (textHash < 0)
                    textHash += prime;
            }
        }

        return false;
    }
}