package com.example.demo.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.entity.MenuItem;
import com.example.demo.repository.MenuItemRepository;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedMenu(MenuItemRepository repo) {
        return args -> {

            if (repo.count() > 0) {
                System.out.println("⚠️ Menu already seeded");
                return;
            }

            List<MenuItem> items = List.of(

                new MenuItem(
                        "Americano",
                        "Classic black coffee",
                        "",
                        new BigDecimal("4.5"),
                        true,
                        "Coffee",
                        100
                ),

                new MenuItem(
                        "Americano with milk",
                        "Americano with a splash of milk",
                        "",
                        new BigDecimal("4.5"),
                        true,
                        "Coffee",
                        100
                ),

                new MenuItem(
                        "Latte",
                        "Milk coffee",
                        "",
                        new BigDecimal("4.6"),
                        true,
                        "Coffee",
                        100
                ),

                new MenuItem(
                        "Cappuccino",
                        "Foamy milk coffee",
                        "",
                        new BigDecimal("4.6"),
                        true,
                        "Coffee",
                        100
                ),

                new MenuItem(
                        "Hot Chocolate",
                        "Rich chocolate drink",
                        "",
                        new BigDecimal("4.4"),
                        true,
                        "Drink",
                        100
                ),

                new MenuItem(
                        "Mocha",
                        "Chocolate coffee",
                        "",
                        new BigDecimal("4.7"),
                        true,
                        "Coffee",
                        100
                ),

                new MenuItem(
                        "Mineral Water",
                        "Still mineral water",
                        "",
                        new BigDecimal("4.0"),
                        true,
                        "Drink",
                        100
                )
            );

            repo.saveAll(items);

            System.out.println("✅ Default menu items inserted!");
        };
    }
}