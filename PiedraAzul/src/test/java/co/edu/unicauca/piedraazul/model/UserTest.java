package co.edu.unicauca.piedraazul.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import co.edu.unicauca.piedraazul.observer.Observer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase User (modelo de dominio).
 */
class UserTest {

    // Observer de prueba que registra los mensajes recibidos
    static class TestObserver implements Observer {
        private final List<String> messages = new ArrayList<>();

        @Override
        public void update(String message) {
            messages.add(message);
        }

        public List<String> getMessages() {
            return messages;
        }
    }

    private User user;
    private TestObserver observer;

    @BeforeEach
    void setUp() {
        user = new User();
        observer = new TestObserver();
    }

    // Prueba 7: setUsername guarda el valor correctamente
    @Test
    void test07_setUsernameStoresValue() {
        user.setUsername("juan");
        assertEquals("juan", user.getUsername());
    }

    // Prueba 8: setPassword guarda el valor correctamente
    @Test
    void test08_setPasswordStoresValue() {
        user.setPassword("secreto123");
        assertEquals("secreto123", user.getPassword());
    }

    // Prueba 9: setRole guarda el rol ADMIN correctamente
    @Test
    void test09_setRoleAdminStoresValue() {
        user.setRole(UserRole.ADMIN);
        assertEquals(UserRole.ADMIN, user.getRole());
    }

    // Prueba 10: setRole guarda el rol USER correctamente
    @Test
    void test10_setRoleUserStoresValue() {
        user.setRole(UserRole.USER);
        assertEquals(UserRole.USER, user.getRole());
    }

    // Prueba 11: setStatus notifica a los observers suscritos
    @Test
    void test11_setStatusNotifiesObserver() {
        user.setUsername("carlos");
        user.attach(observer);
        user.setStatus(UserStatus.ACTIVE);

        assertFalse(observer.getMessages().isEmpty(), "El observer deberia haber recibido una notificacion");
        assertTrue(observer.getMessages().get(0).contains("carlos"),
                "El mensaje debe mencionar el username");
        assertTrue(observer.getMessages().get(0).contains("ACTIVE"),
                "El mensaje debe mencionar el nuevo estado");
    }

    // Prueba 12: setStatus con INACTIVE notifica con estado correcto
    @Test
    void test12_setStatusInactiveNotifiesObserver() {
        user.setUsername("pedro");
        user.attach(observer);
        user.setStatus(UserStatus.INACTIVE);

        assertFalse(observer.getMessages().isEmpty());
        assertTrue(observer.getMessages().get(0).contains("INACTIVE"));
    }

    // Prueba 13: getStatus retorna el ultimo estado asignado
    @Test
    void test13_getStatusReturnsLastSetStatus() {
        user.setStatus(UserStatus.ACTIVE);
        assertEquals(UserStatus.ACTIVE, user.getStatus());

        user.setStatus(UserStatus.INACTIVE);
        assertEquals(UserStatus.INACTIVE, user.getStatus());
    }

    // Prueba 14: setId y getId funcionan correctamente
    @Test
    void test14_setAndGetId() {
        user.setId(42L);
        assertEquals(42L, user.getId());
    }

    // Prueba 15: observer no suscrito no recibe notificaciones de setStatus
    @Test
    void test15_unattachedObserverReceivesNoNotification() {
        user.setUsername("ana");
        // NO se hace attach del observer
        user.setStatus(UserStatus.ACTIVE);

        assertTrue(observer.getMessages().isEmpty(),
                "El observer no suscrito no debe recibir mensajes");
    }
}
