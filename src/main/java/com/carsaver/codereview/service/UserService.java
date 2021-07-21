package com.carsaver.codereview.service;

import com.carsaver.codereview.model.User;
import com.carsaver.codereview.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toMap;

@Service
public class UserService {
    private final UserRepository repository;

    private final EmailService emailService;

    private final ZipCodeLookupService zipCodeLookupService;

    @Autowired
    public UserService(UserRepository repository, EmailService emailService, ZipCodeLookupService zipCodeLookupService) {
        this.repository = repository;
        this.emailService = emailService;
        this.zipCodeLookupService = zipCodeLookupService;
    }

    public List<User> findAll() {
        return this.repository.findAllByOrderByIdAsc();
    }

    public Optional<User> findById(Long id) {
        return this.repository.findById(id);
    }

    public User createUser(User user) throws Exception {
        if (!Objects.isNull(this.repository.findUserByEmail(user.getEmail()))) {
            throw new Exception("User already exist");
        }

        if (!user.getEmail().contains("@test.com")) {
            user.setEnabled(true);
        }

        User newUser = this.repository.save(user);

        if (newUser.isEnabled()) {
            emailService.sendConfirmation(newUser.getEmail());
        }
        return newUser;
    }

    public User updateUserInformation(User userFound, Map<String, String> updates) {
        setUserInformation(userFound, updates);
        return this.repository.save(userFound);
    }

    private void setUserInformation(User userFound, Map<String, String> updates) {
        updates.forEach((key, value) -> {
            switch (key) {
                case "zipCode":
                    userFound.setZipCode(value);
                    break;
                case "city":
                    userFound.setCity(value);
                    break;
                case "email":
                    updateEmail(userFound, value);
                    break;
            }
        });

        if((updates.get("city") == null) && (updates.get("zipCode") != null)) {
            userFound.setCity(zipCodeLookupService.lookupCityByZip(updates.get("zipCode")));
        }
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public Map<Long, String> getNames() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .collect(toMap(User::getId, user -> {
                    return user.getFirstName() + ", " + user.getFirstName();
                }));

    }

    private void updateEmail(User userFound, String value) {
        userFound.setEmail(value);
        emailService.sendConfirmation(value);
    }
}
