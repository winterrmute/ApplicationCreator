package com.wintermute.applicationcreator.applicationData;

import lombok.Data;

@Data
public class Career extends WithDate
{
    private String title;
    private String job;
    private String description;
}
