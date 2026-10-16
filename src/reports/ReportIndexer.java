package reports;

import java.util.*;

public class ReportIndexer {

    public static int[] createIndex(String text) {

        Integer[] suffixes =
                new Integer[text.length()];

        for (int i = 0; i < text.length(); i++)
            suffixes[i] = i;

        Arrays.sort(
                suffixes,
                (a, b) -> text.substring(a)
                        .compareTo(text.substring(b)));

        int[] result =
                new int[suffixes.length];

        for (int i = 0; i < suffixes.length; i++)
            result[i] = suffixes[i];

        return result;
    }

    public static void displayIndex(String text) {

        int[] index =
                createIndex(text);

        System.out.println(
                "\n--- REPORT INDEX ---");

        for (int position : index) {

            System.out.println(
                    position + " : "
                    + text.substring(position));
        }
    }
}