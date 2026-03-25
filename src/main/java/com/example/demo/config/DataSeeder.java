package com.example.demo.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.entity.MenuItem;
import com.example.demo.entity.MenuItemSizePrice;
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

            MenuItem americano = new MenuItem(
                    "Americano",
                    "Classic black coffee",
                    "",
                    new BigDecimal("4.5"),
                    true,
                    "Coffee",
                    100
            );
            americano.getSizePrices().add(new MenuItemSizePrice(americano, "Regular", new BigDecimal("1.50")));
            americano.getSizePrices().add(new MenuItemSizePrice(americano, "Large", new BigDecimal("2.00")));

            MenuItem americanoMilk = new MenuItem(
                    "Americano with milk",
                    "Americano with a splash of milk",
                    "",
                    new BigDecimal("4.5"),
                    true,
                    "Coffee",
                    100
            );
            americanoMilk.getSizePrices().add(new MenuItemSizePrice(americanoMilk, "Regular", new BigDecimal("2.00")));
            americanoMilk.getSizePrices().add(new MenuItemSizePrice(americanoMilk, "Large", new BigDecimal("2.50")));

            MenuItem latte = new MenuItem(
                    "Latte",
                    "Milk coffee",
                    "",
                    new BigDecimal("4.6"),
                    true,
                    "Coffee",
                    100
            );
            latte.getSizePrices().add(new MenuItemSizePrice(latte, "Regular", new BigDecimal("2.50")));
            latte.getSizePrices().add(new MenuItemSizePrice(latte, "Large", new BigDecimal("3.00")));

            MenuItem cappuccino = new MenuItem(
                    "Cappuccino",
                    "Foamy milk coffee",
                    "",
                    new BigDecimal("4.6"),
                    true,
                    "Coffee",
                    100
            );
            cappuccino.getSizePrices().add(new MenuItemSizePrice(cappuccino, "Regular", new BigDecimal("2.50")));
            cappuccino.getSizePrices().add(new MenuItemSizePrice(cappuccino, "Large", new BigDecimal("3.00")));

            MenuItem hotChocolate = new MenuItem(
                    "Hot Chocolate",
                    "Rich chocolate drink",
                    "",
                    new BigDecimal("4.4"),
                    true,
                    "Drink",
                    100
            );
            hotChocolate.getSizePrices().add(new MenuItemSizePrice(hotChocolate, "Regular", new BigDecimal("2.00")));
            hotChocolate.getSizePrices().add(new MenuItemSizePrice(hotChocolate, "Large", new BigDecimal("2.50")));

            MenuItem mocha = new MenuItem(
                    "Mocha",
                    "Chocolate coffee",
                    "",
                    new BigDecimal("4.7"),
                    true,
                    "Coffee",
                    100
            );
            mocha.getSizePrices().add(new MenuItemSizePrice(mocha, "Regular", new BigDecimal("2.50")));
            mocha.getSizePrices().add(new MenuItemSizePrice(mocha, "Large", new BigDecimal("3.00")));

            MenuItem mineralWater = new MenuItem(
                    "Mineral Water",
                    "Still mineral water",
                    "",
                    new BigDecimal("4.0"),
                    true,
                    "Drink",
                    100
            );
            mineralWater.getSizePrices().add(new MenuItemSizePrice(mineralWater, "Regular", new BigDecimal("1.00")));

            List<MenuItem> items = List.of(
                    americano,
                    americanoMilk,
                    latte,
                    cappuccino,
                    hotChocolate,
                    mocha,
                    mineralWater
            );

            repo.saveAll(items);

            System.out.println("✅ Default menu items inserted!");
        };
    }
}