package com.carsaver.codereview.repository;


import com.carsaver.codereview.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findAllByOrderByIdAsc();
    User findUserByEmail(String email);
}
