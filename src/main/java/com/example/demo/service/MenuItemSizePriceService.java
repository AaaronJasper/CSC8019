package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.dto.MenuItemSizePriceRequest;
import com.example.demo.dto.MenuItemSizePriceUpdateRequest;
import com.example.demo.entity.MenuItem;
import com.example.demo.entity.MenuItemSizePrice;
import com.example.demo.exception.MenuItemNotFoundException;
import com.example.demo.exception.MenuItemSizePriceNotFoundException;
import com.example.demo.exception.NoFieldsProvidedException;
import com.example.demo.repository.MenuItemRepository;
import com.example.demo.repository.MenuItemSizePriceRepository;

@Service
public class MenuItemSizePriceService {

    private final MenuItemRepository menuItemRepository;
    private final MenuItemSizePriceRepository sizePriceRepository;

    public MenuItemSizePriceService(MenuItemRepository menuItemRepository,
                                    MenuItemSizePriceRepository sizePriceRepository) {
        this.menuItemRepository = menuItemRepository;
        this.sizePriceRepository = sizePriceRepository;
    }

    public MenuItemSizePrice createSizePrice(MenuItemSizePriceRequest request) {
        MenuItem menuItem = menuItemRepository.findById(request.getMenuItemId())
                .orElseThrow(() -> new MenuItemNotFoundException(request.getMenuItemId()));

        MenuItemSizePrice sizePrice = new MenuItemSizePrice(menuItem, request.getSize(), request.getPrice());
        menuItem.getSizePrices().add(sizePrice);
        // cascade persist on parent should save both, but explicitly save sizePrice for clarity
        return sizePriceRepository.save(sizePrice);
    }

    public MenuItemSizePrice updateSizePrice(Long id, MenuItemSizePriceUpdateRequest request) {
        MenuItemSizePrice existing = sizePriceRepository.findById(id)
                .orElseThrow(() -> new MenuItemSizePriceNotFoundException(id));

        boolean updated = false;

        if (request.getSize() != null) {
            existing.setSize(request.getSize());
            updated = true;
        }

        if (request.getPrice() != null) {
            existing.setPrice(request.getPrice());
            updated = true;
        }

        if (!updated) {
            throw new NoFieldsProvidedException("At least one field must be provided for update (size/price)");
        }

        return sizePriceRepository.save(existing);
    }
}
