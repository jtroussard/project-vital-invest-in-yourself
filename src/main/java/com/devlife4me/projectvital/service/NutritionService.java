package com.devlife4me.projectvital.service;

import com.devlife4me.projectvital.model.entity.Meal;
import com.devlife4me.projectvital.model.entity.MealItem;
import org.springframework.stereotype.Service;

@Service
public class NutritionService {

    public void calculateTotals(Meal meal) {
        float calories = 0f;
        float protein = 0f;
        float carbs = 0f;
        float fat = 0f;

        for (MealItem item : meal.getItems()) {
            float qty = item.getQuantity() != null ? item.getQuantity() : 1f;
            calories += (item.getCalories() != null ? item.getCalories() : 0) * qty;
            protein += (item.getProtein() != null ? item.getProtein() : 0) * qty;
            carbs += (item.getCarbs() != null ? item.getCarbs() : 0) * qty;
            fat += (item.getFat() != null ? item.getFat() : 0) * qty;
        }

        meal.setTotalCalories(calories);
        meal.setTotalProtein(protein);
        meal.setTotalCarbs(carbs);
        meal.setTotalFat(fat);
    }
}
