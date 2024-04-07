package com.ocado.basket;

import com.ocado.basket.algorithm.dancingLinks;
import com.ocado.basket.algorithm.listHeader;
import com.ocado.basket.utils.JsonUtils;
import java.util.*;


public class BasketSplitter {

    // A map holding the delivery options for each item.
    private Map<String, List<String>> deliveryOptions;
    private Map<String, Integer> deliveryId;
    private Map<Integer, String> deliverIdRev;


    public BasketSplitter(String absolutePathToConfigFile) {
        // Loading delivery options configuration from a JSON file
        this.deliveryOptions = JsonUtils.readJsonFromFile(absolutePathToConfigFile, Map.class);

        this.deliverIdRev = new HashMap<>();
    }

    public Map<String, List<String>> split(List<String> items) {

        int maxId = 0;
        Map<String, Integer> deliveryId = new HashMap<>();

        for (String item : items) {
            if (!deliveryOptions.containsKey(item)) {
                throw new IllegalArgumentException("Item " + item + " cannot be delivered!");
            }
            List<String> options = deliveryOptions.get(item);

            for (String option : options) {

                if (!deliveryId.containsKey(option)) {
                    deliveryId.put(option, maxId);
                    deliverIdRev.put(maxId, option);
                    maxId += 1;
                }
            }
        }

        byte[][] matrix = new byte[items.size()][maxId];
        for (int i = 0; i < items.size(); i++) {
            String item = items.get(i);

            if (!deliveryOptions.containsKey(item)) {
                throw new IllegalArgumentException("Item " + item + " cannot be delivered!");
            }

            List<String> options = deliveryOptions.get(item);

            for(String option: options) {
                matrix[i][deliveryId.get(option)] = 1;
            }
        }

        Object[] listHeaders = new Integer[maxId];
        for (int i = 0; i < maxId; i++) {
            listHeaders[i] = i;
        }
        ArrayList<Object[]> sol1 = dancingLinks.firstSolution(matrix, listHeaders, 0);

        Map<String, List<String>> outputMap = new HashMap<>();


        for (Object[] solutionRow : sol1) {

            int col = (int) solutionRow[0];

            List<String> newList = new ArrayList<>();
            for(int i = 0 ; i < items.size(); i++)
            {
                if(matrix[i][col] == 1) {
                    newList.add(items.get(i));
                    Arrays.fill(matrix[i], (byte) 0);
                }
            }
            outputMap.put(deliverIdRev.get(col), newList);
        }

        return outputMap;

    }
}
