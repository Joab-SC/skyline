package com.uniquindio.skyline.domain.valueObject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRoleTest {

    @Test
    void airlineRolesBelongToAnAirline() {
        assertTrue(UserRole.AIRLINE_ADMIN.belongsToAirline());
        assertTrue(UserRole.AIRLINE_SELLER.belongsToAirline());
    }

    @Test
    void adminAndCustomerDoNotBelongToAnAirline() {
        assertFalse(UserRole.ADMIN.belongsToAirline());
        assertFalse(UserRole.CUSTOMER.belongsToAirline());
    }
}
