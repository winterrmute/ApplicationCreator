package com.wintermute.applicationcreator.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Career extends ElementWithTimePeriod
{
    private String job;
    private String description;
}
