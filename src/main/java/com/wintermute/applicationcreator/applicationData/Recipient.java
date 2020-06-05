package com.wintermute.applicationcreator.applicationData;

import lombok.Data;

@Data
public class Recipient
{
    private String company;
    private String contactPerson;
    private Contact contact;
}
