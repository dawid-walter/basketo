package com.dwalter.basketo.modules.ordering.domain.model;

public record ShippingAddress(
        String firstName,
        String lastName,
        String addressLine,
        String city,
        String postalCode,
        String country,
        String phone
) {
    public ShippingAddress {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be blank");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name cannot be blank");
        }
        if (addressLine == null || addressLine.isBlank()) {
            throw new IllegalArgumentException("Address line cannot be blank");
        }
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City cannot be blank");
        }
        if (postalCode == null || postalCode.isBlank()) {
            throw new IllegalArgumentException("Postal code cannot be blank");
        }
        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException("Country cannot be blank");
        }
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone cannot be blank");
        }
    }
}
