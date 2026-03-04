package co.edu.unicauca.piedraazul.service;

import co.edu.unicauca.piedraazul.model.User;
import co.edu.unicauca.piedraazul.model.UserRole;
import co.edu.unicauca.piedraazul.model.UserStatus;
import co.edu.unicauca.piedraazul.observer.Observer;
import co.edu.unicauca.piedraazul.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para UserService usando Mockito para aislar
 * las dependencias (UserRepository y BCryptPasswordEncoder).
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    // Observer de prueba que registra mensajes del servicio
    static class CapturingObserver implements Observer {
        private final List<String> messages = new ArrayList<>();

        @Override
        public void update(String message) {
            messages.add(message);
        }

        public List<String> getMessages() {
            return messages;
        }
    }

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private UserService userService;
    private CapturingObserver serviceObserver;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder);
        serviceObserver = new CapturingObserver();
        userService.attach(serviceObserver);
    }

    // Prueba 24: registerUser retorna true cuando el usuario no existe aun
    @Test
    void test24_registerUserReturnsTrueForNewUser() {
        User user = buildUser("nuevo", "pass", UserRole.USER, UserStatus.ACTIVE);
        when(userRepository.findByUsername("nuevo")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");

        boolean result = userService.registerUser(user);

        assertTrue(result);
        verify(userRepository).save(user);
    }

    // Prueba 25: registerUser retorna false cuando el usuario ya existe
    @Test
    void test25_registerUserReturnsFalseForDuplicateUsername() {
        User existing = buildUser("duplicado", "pass", UserRole.USER, UserStatus.ACTIVE);
        when(userRepository.findByUsername("duplicado")).thenReturn(Optional.of(existing));

        User newUser = buildUser("duplicado", "otropass", UserRole.USER, UserStatus.ACTIVE);
        boolean result = userService.registerUser(newUser);

        assertFalse(result);
        verify(userRepository, never()).save(any());
    }

    // Prueba 26: la contraseña se encripta antes de guardar
    @Test
    void test26_passwordIsEncodedBeforeSaving() {
        User user = buildUser("usuario1", "rawPass", UserRole.USER, UserStatus.ACTIVE);
        when(userRepository.findByUsername("usuario1")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("rawPass")).thenReturn("$2a$hashedPass");

        userService.registerUser(user);

        assertEquals("$2a$hashedPass", user.getPassword());
        verify(passwordEncoder).encode("rawPass");
    }

    // Prueba 27: authenticate retorna el User cuando las credenciales son correctas
    @Test
    void test27_authenticateReturnsUserOnCorrectCredentials() {
        User user = buildUser("validuser", "$2a$encodedPwd", UserRole.USER, UserStatus.ACTIVE);
        when(userRepository.findByUsername("validuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rawPwd", "$2a$encodedPwd")).thenReturn(true);

        User result = userService.authenticate("validuser", "rawPwd");

        assertNotNull(result);
        assertEquals("validuser", result.getUsername());
    }

    // Prueba 28: authenticate retorna null cuando la contraseña es incorrecta
    @Test
    void test28_authenticateReturnsNullOnWrongPassword() {
        User user = buildUser("validuser", "$2a$encodedPwd", UserRole.USER, UserStatus.ACTIVE);
        when(userRepository.findByUsername("validuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPwd", "$2a$encodedPwd")).thenReturn(false);

        User result = userService.authenticate("validuser", "wrongPwd");

        assertNull(result);
    }

    // Prueba 29: authenticate retorna null cuando el usuario no existe
    @Test
    void test29_authenticateReturnsNullForUnknownUser() {
        when(userRepository.findByUsername("fantasma")).thenReturn(Optional.empty());

        User result = userService.authenticate("fantasma", "pass");

        assertNull(result);
    }

    // Prueba 30: authenticate notifica login exitoso al observer del servicio
    @Test
    void test30_authenticateNotifiesObserverOnSuccess() {
        User user = buildUser("admin", "$hash", UserRole.ADMIN, UserStatus.ACTIVE);
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pwd", "$hash")).thenReturn(true);

        userService.authenticate("admin", "pwd");

        assertTrue(serviceObserver.getMessages().stream()
                .anyMatch(m -> m.contains("Login exitoso") && m.contains("admin")));
    }

    // Prueba 31: authenticate notifica login fallido al observer del servicio
    @Test
    void test31_authenticateNotifiesObserverOnFailure() {
        when(userRepository.findByUsername("nadie")).thenReturn(Optional.empty());

        userService.authenticate("nadie", "pwd");

        assertTrue(serviceObserver.getMessages().stream()
                .anyMatch(m -> m.contains("Login fallido") && m.contains("nadie")));
    }

    // Prueba 32: registerUser notifica al observer cuando el registro es exitoso
    @Test
    void test32_registerUserNotifiesObserverOnSuccess() {
        User user = buildUser("nuevo2", "pass", UserRole.USER, UserStatus.ACTIVE);
        when(userRepository.findByUsername("nuevo2")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        userService.registerUser(user);

        assertTrue(serviceObserver.getMessages().stream()
                .anyMatch(m -> m.contains("Nuevo usuario registrado") && m.contains("nuevo2")));
    }

    // Prueba 33: registerUser notifica al observer cuando el registro falla por
    // duplicado
    @Test
    void test33_registerUserNotifiesObserverOnDuplicate() {
        User existing = buildUser("dup", "pass", UserRole.USER, UserStatus.ACTIVE);
        when(userRepository.findByUsername("dup")).thenReturn(Optional.of(existing));

        userService.registerUser(buildUser("dup", "pass2", UserRole.USER, UserStatus.ACTIVE));

        assertTrue(serviceObserver.getMessages().stream()
                .anyMatch(m -> m.contains("Registro fallido") && m.contains("dup")));
    }

    // Prueba 34: registerUser llama a save exactamente una vez para un usuario
    // nuevo
    @Test
    void test34_registerUserCallsSaveExactlyOnce() {
        User user = buildUser("onlyone", "pass", UserRole.ADMIN, UserStatus.ACTIVE);
        when(userRepository.findByUsername("onlyone")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("hashed");

        userService.registerUser(user);

        verify(userRepository, times(1)).save(user);
    }

    // Utilidad para construir usuarios de prueba
    private User buildUser(String username, String password, UserRole role, UserStatus status) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        user.setStatus(status);
        return user;
    }
}
