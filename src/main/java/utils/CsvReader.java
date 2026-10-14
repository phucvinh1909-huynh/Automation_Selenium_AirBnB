package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {
    public static List<String[]> readCsvFile(String filePath) throws IOException {
        List<String[]> data = new ArrayList<>();

        try (BufferedReader reader = java.nio.file.Files.newBufferedReader(
                java.nio.file.Path.of(filePath),
                java.nio.charset.StandardCharsets.UTF_8)) {

            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                data.add(line.split(",", -1));
            }
        }

        return data;
    }

    public static Object[][] toDataProviderArray(List<String[]> rows){
        Object[][] data = new Object[rows.size()][];
        for(int i = 0; i < rows.size(); i++){
            data[i] = rows.get(i);
        }
        return  data;
    }
}

