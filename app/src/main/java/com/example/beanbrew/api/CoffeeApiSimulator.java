package com.example.beanbrew.api;

import android.os.Handler;
import com.example.beanbrew.models.Coffee;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CoffeeApiSimulator {

    public interface CoffeeApiCallback {
        void onSuccess(List<Coffee> coffeeList);
        void onError(String errorMessage);
    }

    public void getCoffeeDeals(final CoffeeApiCallback callback) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    String jsonResponse = getHardcodedJson();
                    List<Coffee> coffeeList = parseJsonResponse(jsonResponse);
                    callback.onSuccess(coffeeList);
                } catch (JSONException e) {
                    callback.onError("Failed to parse API response");
                }
            }
        }, 1500);
    }

    private String getHardcodedJson() {
        return "{\"deals\":[" +
                "{\"id\":101,\"name\":\"Caramel Macchiato ✨ RECOMMENDED\",\"price\":5.99,\"category\":\"Special\",\"description\":\"Rich espresso with vanilla syrup and caramel drizzle - Customer Favorite!\"}," +
                "{\"id\":102,\"name\":\"Iced Matcha Latte ✨ TRENDING\",\"price\":6.49,\"category\":\"Iced\",\"description\":\"Premium matcha powder with creamy milk over ice - Refreshing Summer Drink!\"}," +
                "{\"id\":103,\"name\":\"Vanilla Cold Brew ✨ DEAL\",\"price\":4.99,\"category\":\"Iced\",\"description\":\"Smooth cold brew with vanilla sweet cream - 20% OFF Limited Time!\"}," +
                "{\"id\":104,\"name\":\"Pumpkin Spice Latte\",\"price\":6.99,\"category\":\"Hot\",\"description\":\"Seasonal favorite with pumpkin and spices - Perfect for cold days!\"}," +
                "{\"id\":105,\"name\":\"Mocha Frappuccino ✨ SPECIAL\",\"price\":5.79,\"category\":\"Special\",\"description\":\"Chocolatey blended beverage with whipped cream - Instagram Worthy!\"}," +
                "{\"id\":106,\"name\":\"Hazelnut Cappuccino ✨ NEW\",\"price\":5.49,\"category\":\"Hot\",\"description\":\"Freshly brewed cappuccino with hazelnut syrup - New Arrival!\"}" +
                "]}";
    }

    private List<Coffee> parseJsonResponse(String jsonResponse) throws JSONException {
        List<Coffee> coffeeList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray coffeeArray = jsonObject.getJSONArray("deals"); // Changed from "coffees" to "deals"

        for (int i = 0; i < coffeeArray.length(); i++) {
            JSONObject coffeeJson = coffeeArray.getJSONObject(i);
            Coffee coffee = new Coffee(
                    coffeeJson.getInt("id"),
                    coffeeJson.getString("name"),
                    coffeeJson.getDouble("price"),
                    coffeeJson.getString("category"),
                    coffeeJson.getString("description")
            );
            coffeeList.add(coffee);
        }

        return coffeeList;
    }

    private void addRandomDiscounts(List<Coffee> coffeeList) {
        Random random = new Random();
        int discountIndex = random.nextInt(coffeeList.size());
        Coffee discountedCoffee = coffeeList.get(discountIndex);
        
        double originalPrice = discountedCoffee.getPrice();
        double discountedPrice = originalPrice * 0.8;
        discountedCoffee.setPrice(discountedPrice);
        discountedCoffee.setName(discountedCoffee.getName() + " ✨ DEAL");
    }
}