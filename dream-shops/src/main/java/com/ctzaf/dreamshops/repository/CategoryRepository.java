package com.ctzaf.dreamshops.repository;

import com.ctzaf.dreamshops.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);

    boolean existByName(String name);
}
