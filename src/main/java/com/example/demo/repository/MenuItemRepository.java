package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE MenuItem m SET m.imgUrl = :imgUrl WHERE m.name = :name AND (m.imgUrl IS NULL OR m.imgUrl = '')")
    void patchImgUrlByName(@Param("name") String name, @Param("imgUrl") String imgUrl);
}

