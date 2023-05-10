package org.example;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DataManagement {
    private static final String DATA_FILE = "data.bin";

    static Map<String, String> loadCategories() throws IOException {
        var file = new File("categories.tsv");
        Map<String, String> goodsByCategory = new HashMap<>();
        try (var fis = new FileInputStream(file)) {
            var scanner = new Scanner(fis);
            while (scanner.hasNextLine()) {
                var s = scanner.nextLine();
                var productAndCategory = s.split("\t");
                goodsByCategory.put(productAndCategory[0], productAndCategory[1]);
            }
        }
        return goodsByCategory;
    }

    public static void saveAllDataToBinFile(Object allSalesData) throws IOException {
        File file = new File(DATA_FILE);
        try (var fos = new FileOutputStream(file);
             var oos = new ObjectOutputStream(fos)) {
            oos.writeObject(allSalesData);
        }
    }

    public static Object loadAllDataFromBinFile() throws IOException, ClassNotFoundException {
        File file = new File(DATA_FILE);
        try (var fis = new FileInputStream(file);
             var ois = new ObjectInputStream(fis)) {
            return ois.readObject();
        }
    }

    public static boolean existSavedData() {
        File file = new File(DATA_FILE);
        return file.exists();
    }
}
