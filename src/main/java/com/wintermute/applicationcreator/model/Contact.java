package com.wintermute.applicationcreator.model;

import lombok.Data;

@Data
public class Contact
{
    private String address;
    private String city;
    private String zipCode;
    private String phoneNumber;
    private String email;
    private String website;

    public String getCityWithZipcode(){
        return this.getZipCode() + " " + this.getCity();
    }
}
