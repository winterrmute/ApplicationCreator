package com.wintermute.applicationcreator.applicationData;

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
}
