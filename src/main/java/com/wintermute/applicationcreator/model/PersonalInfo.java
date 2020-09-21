package com.wintermute.applicationcreator.model;

import lombok.Data;

@Data
public class PersonalInfo
{
    private String firstName;
    private String lastName;
    private String jobTitle;
    private String dateOfBirth;
    private String placeOfBirth;
    private String familyStatus;

    public String getFullName(){
        return this.getFirstName() + " " + this.getLastName();
    }
}
