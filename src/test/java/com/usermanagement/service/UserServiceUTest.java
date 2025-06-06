package com.usermanagement.service;

import com.usermanagement.model.User;
import com.usermanagement.model.UserRole;
import com.usermanagement.repository.UserRepository;
import com.usermanagement.shared.exception.PhoneNumberAlreadyExistException;
import com.usermanagement.shared.exception.UserAlreadyExistsException;
import com.usermanagement.shared.exception.UserNotFoundException;
import com.usermanagement.web.dto.UpdateRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceUTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;


    @Test
    void shouldReturnAllUsersWhenNoSearchNameProvided() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .phoneNumber("1234567890")
                .email("john.doe@example.com")
                .role(UserRole.USER)
                .password("password")
                .createdAt(LocalDateTime.now())
                .build();

        Mockito.when(userRepository.findAllByOrderByLastNameAscDateOfBirthAsc())
                .thenReturn(List.of(user));

        List<User> result = userService.getAllUsers(null);

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).containsExactly(user);
    }

    @Test
    void shouldReturnUsersThatMatchSearchName() {
        String searchName = "John";

        User user = User.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .phoneNumber("1234567890")
                .email("john.doe@example.com")
                .role(UserRole.USER)
                .password("password")
                .createdAt(LocalDateTime.now())
                .build();

        Mockito.when(userRepository.searchUsers(searchName))
                .thenReturn(List.of(user));

        List<User> result = userService.getAllUsers(searchName);

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).containsExactly(user);
    }

    @Test
    void shouldThrowExceptionWhenNoUsersFound() {
        Mockito.when(userRepository.findAllByOrderByLastNameAscDateOfBirthAsc())
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> userService.getAllUsers(null))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("No users found.");
    }

    @Test
    void shouldThrowExceptionWhenNoUsersMatchSearchName() {
        String searchName = "NonExistentName";

        Mockito.when(userRepository.searchUsers(searchName))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> userService.getAllUsers(searchName))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("No users found.");
    }

    @Test
    void updateUser_ValidUpdate_ShouldUpdateUser() {
        UUID userId = UUID.randomUUID();
        UpdateRequest updateRequest = UpdateRequest.builder()
                .firstName("UpdatedFirstName")
                .lastName("UpdatedLastName")
                .email("updatedemail@example.com")
                .phoneNumber("1234567890")
                .password("UpdatedPass123")
                .build();

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("existingemail@example.com");

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.findByPhoneNumber(updateRequest.getPhoneNumber())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByEmail(updateRequest.getEmail())).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(updateRequest.getPassword())).thenReturn("encodedPassword");

        userService.updateUser(userId, updateRequest);

        Mockito.verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_UserNotFound_ShouldThrowException() {
        UUID userId = UUID.randomUUID();
        UpdateRequest updateRequest = UpdateRequest.builder().firstName("TestName").build();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, updateRequest));
    }

    @Test
    void updateUser_PhoneNumberAlreadyExists_ShouldThrowException() {
        UUID userId = UUID.randomUUID();
        UpdateRequest updateRequest = UpdateRequest.builder().phoneNumber("1234567890").build();

        User existingUser = new User();
        existingUser.setId(userId);

        User userWithSamePhoneNumber = new User();
        userWithSamePhoneNumber.setId(UUID.randomUUID());

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.findByPhoneNumber(updateRequest.getPhoneNumber()))
                .thenReturn(Optional.of(userWithSamePhoneNumber));

        assertThrows(PhoneNumberAlreadyExistException.class, () -> userService.updateUser(userId, updateRequest));
    }

    @Test
    void updateUser_EmailAlreadyExists_ShouldThrowException() {
        UUID userId = UUID.randomUUID();
        UpdateRequest updateRequest = UpdateRequest.builder().email("testemail@example.com").build();

        User existingUser = new User();
        existingUser.setId(userId);

        User userWithSameEmail = new User();
        userWithSameEmail.setId(UUID.randomUUID());

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.findByEmail(updateRequest.getEmail()))
                .thenReturn(Optional.of(userWithSameEmail));

        assertThrows(UserAlreadyExistsException.class, () -> userService.updateUser(userId, updateRequest));
    }

    @Test
    void deleteUser_userExists_deletesSuccessfully() {
        UUID existingUserId = UUID.randomUUID();
        User user = new User();
        user.setId(existingUserId);
        user.setEmail("test@example.com");

        Mockito.when(userRepository.findById(existingUserId)).thenReturn(Optional.of(user));

        userService.deleteUser(existingUserId);

        Mockito.verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_userDoesNotExist_throwsException() {
        UUID nonExistentUserId = UUID.randomUUID();

        Mockito.when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(nonExistentUserId));
    }

    @Test
    void testSaveUserSuccessfully() {
        // Arrange
        User user = User.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .email("johndoe@example.com")
                .password("securePassword")
                .phoneNumber("1234567890")
                .role(UserRole.USER)
                .build();

        Mockito.when(userRepository.save(user)).thenReturn(user);

        // Act
        userService.saveUser(user);

        // Assert
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    void updateUser_ShouldUpdateAllFieldsSuccessfully() {
        UUID userId = UUID.randomUUID();
        UpdateRequest updateRequest = UpdateRequest.builder()
                .firstName("UpdatedFirst")
                .lastName("UpdatedLast")
                .phoneNumber("1234567890")
                .email("updated@example.com")
                .password("NewPassword123")
                .build();

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setPhoneNumber("0987654321");
        existingUser.setEmail("old@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByPhoneNumber(updateRequest.getPhoneNumber())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(updateRequest.getEmail())).thenReturn(Optional.empty());

        userService.updateUser(userId, updateRequest);

        verify(userRepository).save(existingUser);
    }

    @Test
    void updateUser_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();
        UpdateRequest updateRequest = UpdateRequest.builder().build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, updateRequest));
    }

    @Test
    void updateUser_ShouldThrowPhoneNumberAlreadyExistException_WhenPhoneNumberExists() {
        UUID userId = UUID.randomUUID();
        UpdateRequest updateRequest = UpdateRequest.builder()
                .phoneNumber("1234567890")
                .build();

        User existingUser = new User();
        existingUser.setId(userId);

        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByPhoneNumber(updateRequest.getPhoneNumber())).thenReturn(Optional.of(anotherUser));

        assertThrows(PhoneNumberAlreadyExistException.class, () -> userService.updateUser(userId, updateRequest));
    }

    @Test
    void updateUser_ShouldThrowUserAlreadyExistsException_WhenEmailExists() {
        UUID userId = UUID.randomUUID();
        UpdateRequest updateRequest = UpdateRequest.builder()
                .email("existing@example.com")
                .build();

        User existingUser = new User();
        existingUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(updateRequest.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () -> userService.updateUser(userId, updateRequest));
    }
}