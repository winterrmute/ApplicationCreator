package com.wintermute.applicationcreator.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class Career extends WithDate
{
    private String title;
    private String job;
    private String description;
}
