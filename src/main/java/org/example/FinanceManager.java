package org.example;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

public class FinanceManager implements Serializable {
    protected Map<String, Map<String, Long>> spendDayCategory = new HashMap<>();
    protected Map<String, Map<String, Long>> spendMonthCategory = new HashMap<>();
    protected Map<String, Map<String, Long>> spendYearCategory = new HashMap<>();
    protected Map<String, Long> spendEntirePeriod = new HashMap<>();
    protected String year;
    protected String month;
    protected String day;

    public FinanceManager() {}

    public Map<String, Long> getSpendEntirePeriod() {
        return spendEntirePeriod;
    }
    public void saveBin(File file) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(this);
        }
    }
    public static FinanceManager loadFromBin(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (FinanceManager) in.readObject();
        }
    }
    public Map<String, Long> maxCategoryOfPeriod(Map<String, Map<String, Long>> spendCategoryOfPeriod,
                                                 String period) {
        Map<String, Long> subMap = new HashMap<>();
        for (Map.Entry<String, Map<String, Long>> kv : spendCategoryOfPeriod.entrySet()) {
            for (String key : kv.getValue().keySet()) {
                if (key.equals(period)) {
                    subMap.put(kv.getKey(), kv.getValue().get(key));
                    break;
                }
            }
        }
        return getMaxOfMap(subMap);
    }
    public Map<String, Long> getMaxOfMap(Map<String, Long> subMap) {
        Map<String, Long> maxOfMap = new HashMap<>();

        TreeSet<Long> longTreeSet = new TreeSet<>(subMap.values());
        Long maxSum = longTreeSet.last();
        for (Map.Entry<String, Long> kv : subMap.entrySet()) {
            if (kv.getValue().equals(maxSum)) {
                maxOfMap.put(kv.getKey(), kv.getValue());
                break;
            }
        }
        return maxOfMap;
    }

    public void addBuy(Object[] valuesBuy) {
        String category = (String) valuesBuy[0];
        String date = (String) valuesBuy[1];
        Long sum = (Long) valuesBuy[2];
        year = date.substring(0, 4);
        month = date.substring(0, 7);
        day = date;

        addSpend(spendDayCategory, category, day, sum);
        addSpend(spendMonthCategory, category, month, sum);
        addSpend(spendYearCategory, category, year, sum);

        addSpendEntire(category, sum);
    }

    public void addSpendEntire(String category, Long sum) {
        if (spendEntirePeriod.containsKey(category)) {
            sum += spendEntirePeriod.get(category);
        }
        spendEntirePeriod.put(category, sum);
    }
    private void addSpend(Map<String, Map<String, Long>> spendMap, String category,
                          String date, Long sum) {
        if (spendMap.containsKey(category)) {
            Map<String, Long> map = spendMap.get(category);
            if (map.containsKey(date)) {
                sum += map.get(date);
            }
            map.put(date, sum);
            spendMap.put(category, map);
        }
        spendMap.put(category, new HashMap<>(Map.of(date, sum)));
    }
    public Map<String, Map<String, Long>> maxCategory() {
        Map<String, Map<String, Long>> financeMap = new LinkedHashMap<>();

        Map<String, Long> maxDayCategoryMap = maxCategoryOfPeriod(spendDayCategory, day);
        Map<String, Long> maxMonthCategoryMap = maxCategoryOfPeriod(spendMonthCategory, month);
        Map<String, Long> maxYearCategoryMap = maxCategoryOfPeriod(spendYearCategory, year);
        Map<String, Long> maxEntireCategoryMap = getMaxOfMap(spendEntirePeriod);

        financeMap.put("maxCategory", maxEntireCategoryMap);
        financeMap.put("maxYearCategory", maxYearCategoryMap);
        financeMap.put("maxMonthCategory", maxMonthCategoryMap);
        financeMap.put("maxDayCategory", maxDayCategoryMap);

        return financeMap;
    }
}
