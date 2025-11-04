package com.ims.inventoryManagementSystem.repository;

import com.ims.inventoryManagementSystem.entity.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category,Integer> {
    Category getCategoriesByCategoryName (String categoryName);
}
