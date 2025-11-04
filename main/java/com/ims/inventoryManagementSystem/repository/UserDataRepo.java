package com.ims.inventoryManagementSystem.repository;

import com.ims.inventoryManagementSystem.entity.UserData;
import org.springframework.data.repository.CrudRepository;

public interface UserDataRepo  extends CrudRepository<UserData, Integer> {
    UserData findByEmail (String email);
}
