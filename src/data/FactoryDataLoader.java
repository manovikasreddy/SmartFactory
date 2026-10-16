package data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.FactoryRecord;

public class FactoryDataLoader {

    public static List<FactoryRecord> loadData(String filePath) {

        List<FactoryRecord> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;

            // Read header
            br.readLine();

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",", -1);

                if (data.length < 20) {
                    continue;
                }

                try {

                    FactoryRecord record = new FactoryRecord(
                            data[0].trim(),                         // record_id
                            data[1].trim(),                         // device_id
                            data[2].trim(),                         // timestamp
                            parseDouble(data[3]),                   // signal_strength
                            parseDouble(data[4]),                   // packet_loss
                            parseDouble(data[5]),                   // temperature
                            parseDouble(data[6]),                   // vibration
                            parseDouble(data[7]),                   // humidity
                            parseDouble(data[8]),                   // pressure
                            parseDouble(data[9]),                   // current
                            parseDouble(data[10]),                  // voltage
                            parseDouble(data[11]),                  // power factor
                            parseDouble(data[12]),                  // power
                            parseDouble(data[13]),                  // energy
                            parseDouble(data[14]),                  // rpm
                            parseDouble(data[15]),                  // tool wear
                            parseDouble(data[16]),                  // operating hours
                            data[17].trim(),                        // load type
                            data[18].trim(),                        // fault flag
                            data[19].trim()                         // failure type
                    );

                    records.add(record);

                } catch (Exception e) {
                    System.out.println("Skipping invalid row: " + line);
                }
            }

        } catch (IOException e) {

            System.out.println();
            System.out.println("ERROR: Cannot read CSV file.");
            System.out.println("Path: " + filePath);
            System.out.println("Reason: " + e.getMessage());
        }

        return records;
    }

    private static double parseDouble(String value) {

        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }

        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}