package com.ocado.basket;



import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        String pathToConfigFile = "/home/sit3kk/DeliveryOptimizer/DeliveryOptimizer/src/main/resources/config.json";


        List<String> items = Arrays.asList(
                "Fond - Chocolate", "Chocolate - Unsweetened", "Nut - Almond, Blanched, Whole", "Haggis", "Mushroom - Porcini Frozen", "Cake - Miini Cheesecake Cherry", "Sauce - Mint", "Longan", "Bag Clear 10 Lb", "Nantucket - Pomegranate Pear", "Puree - Strawberry", "Numi - Assorted Teas", "Apples - Spartan", "Garlic - Peeled", "Cabbage - Nappa", "Bagel - Whole White Sesame", "Tea - Apple Green Tea"
        );


        BasketSplitter splitter = new BasketSplitter(pathToConfigFile);


        Map<String, List<String>> splitResult = splitter.split(items);


        for (String key : splitResult.keySet()) {
            System.out.print(key + " : ");


            for (String value : splitResult.get(key)) {
                System.out.print(value + ",");
            }

            System.out.println();
        }





    }
}
