package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DataHandlerImpl implements DataHandler {

    private static boolean LOAD_DATA = true;
    private static boolean SAVE_DATA = true;

    public static void setLoadAndSaveDataDisabled() {
        LOAD_DATA = false;
        SAVE_DATA = false;
    }

    private static DataHandlerImpl INSTANCE;

    public static DataHandlerImpl getInstance() throws IOException, ClassNotFoundException {
        if (INSTANCE == null) {
            INSTANCE = new DataHandlerImpl();
        }
        return INSTANCE;
    }

    private String lastSaleDate;

    private final Map<String, String> goodsByCategory;

    private final AllSalesData allSalesData;

    private DataHandlerImpl() throws IOException, ClassNotFoundException {
        this.goodsByCategory = DataManagement.loadCategories();
        if (DataManagement.existSavedData() && LOAD_DATA) {
            this.allSalesData = (AllSalesData) DataManagement.loadAllDataFromBinFile();
        } else {
            this.allSalesData = new AllSalesData();
            allSalesData.maxCategory = new HashMap<>();
            allSalesData.yearlySales = new HashMap<>();
            allSalesData.monthlySales = new HashMap<>();
            allSalesData.dailySales = new HashMap<>();
        }
    }

    @Override
    public void addSale(String newSaleForAdd) throws IOException {
        CategorySalesData categorySalesData = salesRecordConversion(newSaleForAdd);

        var category = categorySalesData.category;
        var date = categorySalesData.date;
        var saleSum = categorySalesData.sum;
        lastSaleDate = date;
        int currentSales = allSalesData.maxCategory.getOrDefault(category, 0);
        allSalesData.maxCategory.put(category, currentSales + categorySalesData.sum);

        Map<String, Integer> localMap;
        var year = date.substring(0, 4);
        localMap = allSalesData.yearlySales.getOrDefault(year, new HashMap<>());

        currentSales = localMap.getOrDefault(category, 0);
        localMap.put(category, currentSales + saleSum);

        allSalesData.yearlySales.put(year, localMap);
        var month = date.substring(0, 7);
        localMap = allSalesData.monthlySales.getOrDefault(month, new HashMap<>());

        currentSales = localMap.getOrDefault(category, 0);
        localMap.put(category, currentSales + saleSum);

        allSalesData.monthlySales.put(month, localMap);
        localMap = allSalesData.dailySales.getOrDefault(date, new HashMap<>());

        currentSales = localMap.getOrDefault(category, 0);
        localMap.put(category, currentSales + saleSum);

        allSalesData.dailySales.put(date, localMap);

        if (SAVE_DATA) {
            DataManagement.saveAllDataToBinFile(allSalesData);
        }
    }

    private CategorySalesData salesRecordConversion(String saleRecordJSON) {
        var gson = new Gson();
        var salesRecord = gson.fromJson(saleRecordJSON, SalesRecordFromJSON.class);

        var saleCategory = goodsByCategory.get(salesRecord.title);
        if (saleCategory == null) {
            saleCategory = "другое";
        }

        var date = salesRecord.date;
        var saleSum = salesRecord.sum;
        return new CategorySalesData(saleCategory, date, saleSum);
    }

    @Override
    public String generateAnalysisResults() {

        String key = getEntryWithMaximumSales(allSalesData.maxCategory);
        var maxInCategory = new SaleDataForOutput(key, allSalesData.maxCategory.get(key));
        var result = new AllAnalysisResults();
        result.maxCategory = maxInCategory;

        Map<String, Integer> localMap;
        var year = lastSaleDate.substring(0, 4);

        localMap = allSalesData.yearlySales.get(year);
        key = getEntryWithMaximumSales(localMap);

        result.maxYearCategory = new SaleDataForOutput(key, localMap.get(key));
        var month = lastSaleDate.substring(0, 7);

        localMap = allSalesData.monthlySales.get(month);
        key = getEntryWithMaximumSales(localMap);

        result.maxMonthCategory = new SaleDataForOutput(key, localMap.get(key));
        var date = lastSaleDate;

        localMap = allSalesData.dailySales.get(date);
        key = getEntryWithMaximumSales(localMap);

        result.maxDayCategory = new SaleDataForOutput(key, localMap.get(key));
        var gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(result);
    }

    private String getEntryWithMaximumSales(Map<String, Integer> salesMap) {
        int maxSale = -1;
        String key = null;
        for (Map.Entry<String, Integer> entryMap : salesMap.entrySet()) {
            if (entryMap.getValue() > maxSale) {
                key = entryMap.getKey();
                maxSale = entryMap.getValue();
            }
        }
        return key;
    }

    private class SalesRecordFromJSON {
        String title;
        String date;
        int sum;
    }

    private class CategorySalesData {
        String category;
        String date;
        int sum;

        public CategorySalesData(String category, String date, int sum) {
            this.category = category;
            this.date = date;
            this.sum = sum;
        }
    }

    private static class AllSalesData implements Serializable {

        private static final long serialVersionUID = 3L;
        Map<String, Integer> maxCategory;
        Map<String, Map<String, Integer>> yearlySales;
        Map<String, Map<String, Integer>> monthlySales;
        Map<String, Map<String, Integer>> dailySales;
    }

    private class AllAnalysisResults {
        SaleDataForOutput maxCategory;
        SaleDataForOutput maxYearCategory;
        SaleDataForOutput maxMonthCategory;
        SaleDataForOutput maxDayCategory;
    }

    private class SaleDataForOutput {
        String category;
        int sum;

        public SaleDataForOutput(String category, int sum) {
            this.category = category;
            this.sum = sum;
        }
    }
}
