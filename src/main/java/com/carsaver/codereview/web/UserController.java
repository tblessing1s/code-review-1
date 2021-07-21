package com.carsaver.codereview.web;

import com.carsaver.codereview.model.User;
import com.carsaver.codereview.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> findAll() throws RuntimeException{
        List<User> users;
        try {
            users = this.userService.findAll();
        } catch (Exception exception) {
            return new ResponseEntity(exception, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(users, HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id){
        User user;
        try {
            user = this.userService.findById(id).orElseThrow();
        } catch (Exception exception) {
            return new ResponseEntity(exception, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(user, HttpStatus.OK);
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email
    ) {
        User createdUser;

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);

        try {
            createdUser = this.userService.createUser(user);
        } catch (Exception exception) {
            return new ResponseEntity(exception, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(createdUser, HttpStatus.CREATED);
    }

    /**
     * updates user's address
     * @param id - assume valid existing id
     * @param updates - accepts key value pairs for email, zipcode, city
     * @return updated User
     */
    @PatchMapping("/users/{id}")
    public ResponseEntity<User> updateUserInformation(@PathVariable Long id, @RequestBody Map<String, String> updates) throws Exception {
        User user;
        try {
            User userFound = this.userService.findById(id).orElseThrow();
            user = this.userService.updateUserInformation(userFound, updates);
        } catch (Exception exception) {
            return new ResponseEntity(exception, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(user, HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity deleteUser(@PathVariable Long id) {
        this.userService.deleteById(id);
        return new ResponseEntity(null, HttpStatus.NO_CONTENT);
    }
}
