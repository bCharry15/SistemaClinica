package co.edu.unicauca.piedraazul.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para los enums UserRole y UserStatus.
 */
class UserEnumsTest {

    // Prueba 16: UserRole tiene exactamente dos valores
    @Test
    void test16_userRoleHasTwoValues() {
        UserRole[] roles = UserRole.values();
        assertEquals(2, roles.length);
    }

    // Prueba 17: UserRole contiene ADMIN y USER
    @Test
    void test17_userRoleContainsAdminAndUser() {
        assertNotNull(UserRole.valueOf("ADMIN"));
        assertNotNull(UserRole.valueOf("USER"));
    }

    // Prueba 18: UserStatus tiene exactamente dos valores
    @Test
    void test18_userStatusHasTwoValues() {
        UserStatus[] statuses = UserStatus.values();
        assertEquals(2, statuses.length);
    }

    // Prueba 19: UserStatus contiene ACTIVE e INACTIVE
    @Test
    void test19_userStatusContainsActiveAndInactive() {
        assertNotNull(UserStatus.valueOf("ACTIVE"));
        assertNotNull(UserStatus.valueOf("INACTIVE"));
    }

    // Prueba 20: UserRole.ADMIN no es igual a UserRole.USER
    @Test
    void test20_userRoleAdminNotEqualsUser() {
        assertNotEquals(UserRole.ADMIN, UserRole.USER);
    }

    // Prueba 21: UserStatus.ACTIVE no es igual a UserStatus.INACTIVE
    @Test
    void test21_userStatusActiveNotEqualsInactive() {
        assertNotEquals(UserStatus.ACTIVE, UserStatus.INACTIVE);
    }

    // Prueba 22: los valores de enum se pueden comparar con ==
    @Test
    void test22_enumValuesCompareWithEquality() {
        UserRole role = UserRole.ADMIN;
        assertEquals(UserRole.ADMIN, role);
    }

    // Prueba 23: nombre de enum coincide con su representacion en cadena
    @Test
    void test23_enumNameMatchesToString() {
        assertEquals("ADMIN", UserRole.ADMIN.name());
        assertEquals("USER", UserRole.USER.name());
        assertEquals("ACTIVE", UserStatus.ACTIVE.name());
        assertEquals("INACTIVE", UserStatus.INACTIVE.name());
    }
}
