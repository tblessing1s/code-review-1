package com.carsaver.codereview.web;

import com.carsaver.codereview.model.User;
import com.carsaver.codereview.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService mockUserService;

    @InjectMocks
    private UserController userController;

    @Test
    public void findAll_shouldCallFinalAllFromService() throws RuntimeException {
        userController.findAll();

        verify(mockUserService).findAll();
    }

    @Test
    public void findAll_shouldReturnStatusCodeOk() throws RuntimeException {
        User user = new User();
        user.setId(1L);
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setEmail("email@email.com");
        user.setZipCode("12345");
        user.setCity("City");

        List<User> expected = singletonList(user);

        when(mockUserService.findAll()).thenReturn(expected);

        ResponseEntity<List<User>> result = userController.findAll();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expected, result.getBody());
    }

    @Test
    public void findAll_shouldReturnStatusCodeNotFound() throws RuntimeException{
        String errorMessage = "Missing";

        when(mockUserService.findAll()).thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<List<User>> result = userController.findAll();

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void findById_shouldCallFinalByIdFromService() {
        Long id = 1L;

        userController.findById(id);

        verify(mockUserService).findById(id);
    }

    @Test
    public void findById_shouldReturnStatusCodeOk() throws RuntimeException {
        User expected = new User();
        expected.setId(1L);
        expected.setFirstName("firstName");
        expected.setLastName("lastName");
        expected.setEmail("email@email.com");
        expected.setZipCode("12345");
        expected.setCity("City");


        when(mockUserService.findById(expected.getId())).thenReturn(of(expected));

        ResponseEntity<User> result = userController.findById(expected.getId());

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expected, result.getBody());
    }

    @Test
    public void findById_shouldReturnStatusCodeNotFound() throws RuntimeException{
        String errorMessage = "Missing";

        when(mockUserService.findById(1L)).thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<User> result = userController.findById(1L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void createUser_shouldCallCreateUserFromService() throws Exception {
        String firstName = "firstName";
        String lastName = "lastName";
        String email = "email";

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);


        userController.createUser(firstName, lastName, email);

        verify(mockUserService).createUser(user);
    }

    @Test
    public void createUser_shouldReturnStatusCodeCreated() throws Exception {
        String firstName = "firstName";
        String lastName = "lastName";
        String email = "email@email.com";

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);

        when(mockUserService.createUser(user)).thenReturn(user);

        ResponseEntity<User> result = userController.createUser(firstName, lastName, email);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(user, result.getBody());
    }

    @Test
    public void createUser_shouldReturnStatusCodeBadRequest() throws Exception {
        String firstName = "firstName";
        String lastName = "lastName";
        String email = "email@email.com";

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);

        String errorMessage = "Missing";

        when(mockUserService.createUser(user)).thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<User> result = userController.createUser(firstName, lastName, email);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void updateUserLocation_shouldReturnUser_whenUserExist() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("email@email.com");
        user.setZipCode("12345");
        user.setCity("city");

        when(mockUserService.findById(user.getId())).thenReturn(of(user));

        Map<String, String> fieldUpdate = new HashMap<String, String>();
        fieldUpdate.put("zipCode", "12345");

        ResponseEntity<User> result = userController.updateUserInformation(user.getId(), fieldUpdate);

        verify(mockUserService).updateUserInformation(user, fieldUpdate);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void updateUserLocation_shouldThrowError_whenUserDoesNotExist() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("email@email.com");
        user.setZipCode("12345");
        user.setCity("city");

        String errorMessage = "User does not exist";

        when(mockUserService.findById(user.getId())).thenThrow(new RuntimeException(errorMessage));

        Map<String, String> fieldUpdate = new HashMap<String, String>();
        fieldUpdate.put("zipCode", "12345");

        ResponseEntity<User> result = userController.updateUserInformation(user.getId(), fieldUpdate);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        verify(mockUserService, times(0)).createUser(user);
    }

    @Test
    public void deleteUser_shouldReturn204_whenSuccessfullyDeleted() {
        Long id = 1L;

        ResponseEntity result = userController.deleteUser(id);

        verify(mockUserService).deleteById(id);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
}