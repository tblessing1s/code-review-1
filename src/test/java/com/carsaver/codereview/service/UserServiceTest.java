package com.carsaver.codereview.service;

import com.carsaver.codereview.model.User;
import com.carsaver.codereview.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private EmailService mockEmailService;

    @Mock
    private ZipCodeLookupService mockZipCodeLookupService;

    @InjectMocks
    private UserService userService;


    @Test
    void findAll_shouldReturnListOfUsers() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setEmail("email@email.com");
        user.setZipCode("12345");
        user.setCity("City");

        List<User> expected = singletonList(user);

        when(mockUserRepository.findAllByOrderByIdAsc()).thenReturn(expected);

        List<User> result = userService.findAll();

        assertEquals(expected, result);
    }

    @Test
    void findById_shouldReturnUser_whenUserExist() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setEmail("email@email.com");
        user.setZipCode("12345");
        user.setCity("City");

        Optional<User> expected = of(user);

        when(mockUserRepository.findById(user.getId())).thenReturn(of(user));

        Optional<User> result = userService.findById(user.getId());

        assertEquals(expected, result);
    }

    @Test
    public void createUser_shouldCallSave() throws Exception {
        User user = new User();
        user.setEmail("email@email.com");
        user.setEnabled(false);

        when(mockUserRepository.findUserByEmail(user.getEmail())).thenReturn(null);
        when(mockUserRepository.save(user)).thenReturn(user);

        userService.createUser(user);

        verify(mockUserRepository).save(user);
    }

    @Test
    public void createUser_shouldSave_whenUsersEmailDoesNotExist() throws Exception {
        User user = new User();
        user.setEmail("bob@email.com");
        user.setFirstName("first");
        user.setLastName("last");
        user.setCity("city");
        user.setZipCode("zipCode");

        when(mockUserRepository.findUserByEmail(user.getEmail())).thenReturn(null);
        when(mockUserRepository.save(user)).thenReturn(user);

        userService.createUser(user);

        verify(mockUserRepository).save(user);
    }

    @Test
    public void createUser_shouldNotSave_whenUsersEmailDoesExist() {
        User user = new User();
        user.setEmail("email@email.com");
        user.setFirstName("first");
        user.setLastName("last");
        user.setCity("city");
        user.setZipCode("zipCode");

        User expected = new User();
        user.setId(1L);
        user.setEmail("email@email.com");
        user.setFirstName("first");
        user.setLastName("last");
        user.setCity("city");
        user.setZipCode("zipCode");

        when(mockUserRepository.findUserByEmail(user.getEmail())).thenReturn(expected);

        assertThrows(Exception.class, () -> userService.createUser(user), "User already exist");

        verify(mockUserRepository).findUserByEmail(user.getEmail());
    }

    @Test
    public void createUser_shouldEnableUser_whenEmailContainsAtTestEmail() throws Exception {
        User user = new User();
        user.setFirstName("first");
        user.setLastName("last");
        user.setEmail("email@test.com");
        user.setEnabled(false);


        when(mockUserRepository.findUserByEmail(user.getEmail())).thenReturn(null);
        when(mockUserRepository.save(user)).thenReturn(user);

        userService.createUser(user);


        assertEquals(
                false,
                user.getEnabled()
        );
    }

    @Test
    public void createUser_shouldNotEnableUser_whenEmailDoesNotContainAtTestEmail() throws Exception {
        User user = new User();
        user.setEmail("email@gmail.com");
        user.setEnabled(false);


        when(mockUserRepository.findUserByEmail(user.getEmail())).thenReturn(null);
        when(mockUserRepository.save(user)).thenReturn(user);

        userService.createUser(user);

        assertEquals(true, user.getEnabled());
    }

    @Test
    public void createUser_shouldSendConfirmation_whenUserIsEnabled() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setFirstName("First");
        user.setLastName("Last");
        user.setEmail("email@email.com");
        user.setCity("city");
        user.setZipCode("zipCode");

        when(mockUserRepository.findUserByEmail(user.getEmail())).thenReturn(null);
        when(mockUserRepository.save(user)).thenReturn(user);

        userService.createUser(user);

        verify(mockEmailService).sendConfirmation(user.getEmail());
    }

    @Test
    public void createUser_shouldNotSendConfirmation_whenUserIsNotEnabled() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setFirstName("First");
        user.setLastName("Last");
        user.setEmail("email@test.com");
        user.setCity("city");
        user.setZipCode("zipCode");
        user.setEnabled(false);

        when(mockUserRepository.findUserByEmail(user.getEmail())).thenReturn(null);
        when(mockUserRepository.save(user)).thenReturn(user);

        userService.createUser(user);

        verifyNoInteractions(mockEmailService);
    }

    @Test
    public void updateUserInformation_shouldUpdateUser_whenGivenZipcodeAndCity() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("First");
        user.setLastName("Last");
        user.setEmail("email@test.com");
        user.setCity("city");
        user.setZipCode("zipCode");
        user.setEnabled(false);

        User expected = new User();
        expected.setId(1L);
        expected.setFirstName("First");
        expected.setLastName("Last");
        expected.setEmail("email@test.com");
        expected.setCity("fakeCity");
        expected.setZipCode("12345");
        expected.setEnabled(false);

        when(mockUserRepository.save(user)).thenReturn(expected);

        Map<String, String> fieldUpdate = new HashMap<String, String>();
        fieldUpdate.put("zipCode", "12345");
        fieldUpdate.put("city", "fakeCity");

        userService.updateUserInformation(user, fieldUpdate);

        verify(mockUserRepository).save(user);
        assertEquals(expected, user);
    }

    @Test
    public void updateUserInformation_shouldUpdateUser_whenGivenZipcode() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("First");
        user.setLastName("Last");
        user.setEmail("email@test.com");
        user.setCity("city");
        user.setZipCode("zipCode");
        user.setEnabled(false);

        User expected = new User();
        expected.setId(1L);
        expected.setFirstName("First");
        expected.setLastName("Last");
        expected.setEmail("email@test.com");
        expected.setCity("fakeCity");
        expected.setZipCode("12345");
        expected.setEnabled(false);

        when(mockZipCodeLookupService.lookupCityByZip("12345")).thenReturn("fakeCity");
        when(mockUserRepository.save(user)).thenReturn(expected);

        Map<String, String> fieldUpdate = new HashMap<String, String>();
        fieldUpdate.put("zipCode", "12345");

        userService.updateUserInformation(user, fieldUpdate);

        verify(mockUserRepository).save(user);
        assertEquals(expected, user);
    }

    @Test
    public void updateUserInformation_shouldUpdateUserAndSendConfirmation_whenGivenEmail() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("First");
        user.setLastName("Last");
        user.setEmail("email@test.com");
        user.setCity("city");
        user.setZipCode("zipCode");
        user.setEnabled(false);

        User expected = new User();
        expected.setId(1L);
        expected.setFirstName("First");
        expected.setLastName("Last");
        expected.setEmail("bob@test.com");
        expected.setCity("city");
        expected.setZipCode("zipCode");
        expected.setEnabled(false);

        when(mockUserRepository.save(user)).thenReturn(expected);

        Map<String, String> fieldUpdate = new HashMap<String, String>();
        fieldUpdate.put("email", "bob@test.com");

        userService.updateUserInformation(user, fieldUpdate);

        verify(mockUserRepository).save(user);
        verify(mockEmailService).sendConfirmation(user.getEmail());
        assertEquals(expected, user);
    }

    @Test
    public void deleteById_shouldDeleteUser_whenCalledWithId() {
        userService.deleteById(1L);

        verify(mockUserRepository).deleteById(1L);
    }
}