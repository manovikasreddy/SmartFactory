package reports;

public class LargeReportIndexer {

    public static int[] createLargeReportIndex(
            String text) {

        /*
         * Large-report indexing.
         *
         * The project interface is kept separate so
         * the factory application does not expose
         * algorithm names to users.
         */

        return ReportIndexer.createIndex(text);
    }
}