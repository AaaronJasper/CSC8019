package com.example.demo.service;

import com.example.demo.dto.MenuItemUpdateRequest;
import com.example.demo.dto.MenuItemRequest;
import com.example.demo.entity.MenuItem;
import com.example.demo.exception.MenuItemNotFoundException;
import com.example.demo.exception.MenuItemsNotFoundException;
import com.example.demo.exception.NoFieldsProvidedException;
import com.example.demo.repository.MenuItemRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MenuService {

    private final MenuItemRepository menuItemRepository;

    public MenuService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    /**
     * Updates only fields that are non-null in {@code request}.
     *
     * If no updatable fields are provided (all are null), throws {@link NoFieldsProvidedException}.
     *
     * @throws MenuItemNotFoundException if the id does not exist
     * @throws NoFieldsProvidedException if the request contains no fields to update
     */
    public MenuItem updateMenuItem(Long id, MenuItemUpdateRequest request) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new MenuItemNotFoundException(id));

        boolean updated = false;

        if (request.getName() != null) {
            item.setName(request.getName());
            updated = true;
        }
        if (request.getDescription() != null) {
            item.setDescription(request.getDescription());
            updated = true;
        }
        if (request.getImgUrl() != null) {
            item.setImgUrl(request.getImgUrl());
            updated = true;
        }
        if (request.getRating() != null) {
            item.setRating(request.getRating());
            updated = true;
        }
        if (request.getCategory() != null) {
            item.setCategory(request.getCategory());
            updated = true;
        }
        if (request.getItemCount() != null) {
            item.setItemCount(request.getItemCount());
            updated = true;
        }

        if (!updated) {
            throw new NoFieldsProvidedException(
                    "At least one field must be provided for update (name/description/imgUrl/rating/category/itemCount)");
        }

        return menuItemRepository.save(item);
    }

    /**
     * Fetch all menu items.
     *
     * @throws MenuItemsNotFoundException if no menu items exist
     */
    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> items = menuItemRepository.findAll();
        if (items.isEmpty()) {
            throw new MenuItemsNotFoundException();
        }
        return items;
    }

    /**
     * Fetch a menu item by id.
     *
     * @throws MenuItemNotFoundException if the id does not exist
     */
    public MenuItem getMenuItemById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new MenuItemNotFoundException(id));
    }

    /**
     * Soft-delete a menu item by setting {@code isAvailable=false}.
     *
     * @throws MenuItemNotFoundException if the id does not exist
     */
    public String softDeleteMenuItem(Long id) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new MenuItemNotFoundException(id));

        item.setIsAvailable(false);
        menuItemRepository.save(item);
        return "MenuItem with id " + id + " has been soft-deleted (isAvailable=false)";
    }

    /**
     * Creates a new {@link MenuItem} from {@code request}.
     *
     * Bean Validation is handled on the controller side via {@code @Valid}.
     * This method always sets {@code isAvailable=true} for new menu items.
     */
    public MenuItem addMenuItem(MenuItemRequest request) {
        MenuItem menuItem = new MenuItem();
        menuItem.setName(request.getName());
        menuItem.setDescription(request.getDescription());
        menuItem.setImgUrl(request.getImgUrl());
        menuItem.setRating(request.getRating());
        menuItem.setCategory(request.getCategory());
        menuItem.setItemCount(request.getItemCount());
        menuItem.setIsAvailable(true);
        return menuItemRepository.save(menuItem);
    }
}

