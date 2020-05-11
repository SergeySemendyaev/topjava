package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO return filtered list with excess. Implement by cycles
        List<UserMealWithExcess> result = new ArrayList<>();
        Map<LocalDate, Integer> dailyCalorage = new HashMap<>();
        Map<LocalDate, List<UserMealWithExcess>> mealMap = new HashMap<>();

        for (UserMeal meal : meals) {
            LocalDateTime dateTime = meal.getDateTime();
            LocalDate localDate = dateTime.toLocalDate();
            int calorage = meal.getCalories();
            if (dailyCalorage.containsKey(localDate)) {
                calorage += dailyCalorage.get(localDate);
            }

            dailyCalorage.put(localDate, calorage);
            LocalTime localTime = dateTime.toLocalTime();
            if (localTime.isAfter(startTime) && localTime.isBefore(endTime)) {
                if (!mealMap.containsKey(localDate)) {
                    mealMap.put(localDate, new ArrayList<>());
                }
                mealMap.get(localDate).add(new UserMealWithExcess(dateTime, meal.getDescription(), meal.getCalories(), true));
            }
        }

        for (Map.Entry<LocalDate, Integer> pair : dailyCalorage.entrySet()) {
            Integer value = pair.getValue();
            if (value > caloriesPerDay) {
                result.addAll(mealMap.get(pair.getKey()));
            }
        }

        return result;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO Implement by streams
        final Map<LocalDate, Integer> dailyCalorage = new HashMap<>();
        meals.forEach(meal ->  {
            int calories = meal.getCalories();
            LocalDate localDate = meal.getDateTime().toLocalDate();
            if (dailyCalorage.containsKey(localDate)) {
                calories += dailyCalorage.get(localDate);
            }
            dailyCalorage.put(localDate, calories);
        });

        final List<UserMealWithExcess> result = new ArrayList<>();

        meals.stream().filter(meal -> meal.getDateTime().toLocalTime().isAfter(startTime) &&
                                meal.getDateTime().toLocalTime().isBefore(endTime) &&
                                dailyCalorage.get(meal.getDateTime().toLocalDate()) > caloriesPerDay)
                .collect(Collectors.toList())
                .forEach(element -> result.add(new UserMealWithExcess(element.getDateTime()
                        , element.getDescription()
                        , element.getCalories()
                        , true)));
        return result;
    }
}
