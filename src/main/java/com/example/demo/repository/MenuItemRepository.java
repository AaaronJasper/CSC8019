package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
}

