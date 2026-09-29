package com.example.forfinal.dto;

import com.example.forfinal.entity.Address;

public class AddressResponse {
    private Long id;
    private String fullName;
    private String phone;
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private boolean isDefault;

    public AddressResponse() {}

    public AddressResponse(Address a) {
        this.id = a.getId();
        this.fullName = a.getFullName();
        this.phone = a.getPhone();
        this.street = a.getStreet();
        this.city = a.getCity();
        this.state = a.getState();
        this.zipCode = a.getZipCode();
        this.country = a.getCountry();
        this.isDefault = a.isDefault();
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getZipCode() { return zipCode; }
    public String getCountry() { return country; }
    public boolean isDefault() { return isDefault; }
}
