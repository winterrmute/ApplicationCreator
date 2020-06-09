package com.wintermute.applicationcreator.data;

import lombok.Data;

@Data
public class Recipient
{
    private String company;
    private String contactPerson;
    private Contact contact;
}
