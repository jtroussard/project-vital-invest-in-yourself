package com.devlife4me.projectvital.service;

import com.devlife4me.projectvital.model.entity.Meal;
import com.devlife4me.projectvital.model.entity.MealItem;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class NutritionServiceTest {

    private final NutritionService nutritionService = new NutritionService();

    @Test
    void calculateTotals_SumsItemsCorrectly() {
        MealItem item1 = MealItem.builder()
                .calories(100f)
                .protein(10f)
                .carbs(20f)
                .fat(5f)
                .quantity(2f)
                .build();

        MealItem item2 = MealItem.builder()
                .calories(50f)
                .protein(5f)
                .carbs(10f)
                .fat(2.5f)
                .quantity(1f)
                .build();

        Meal meal = Meal.builder()
                .items(Arrays.asList(item1, item2))
                .build();

        nutritionService.calculateTotals(meal);

        assertEquals(250f, meal.getTotalCalories());
        assertEquals(25f, meal.getTotalProtein());
        assertEquals(50f, meal.getTotalCarbs());
        assertEquals(12.5f, meal.getTotalFat());
    }

    @Test
    void calculateTotals_HandlesNulls() {
        MealItem item = new MealItem();
        Meal meal = Meal.builder().items(Arrays.asList(item)).build();

        nutritionService.calculateTotals(meal);

        assertEquals(0f, meal.getTotalCalories());
        assertEquals(0f, meal.getTotalProtein());
        assertEquals(0f, meal.getTotalFat());
    }
}
