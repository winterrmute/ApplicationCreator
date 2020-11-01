package com.wintermute.applicationcreator.model.complex;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents career object. May be education or professional career.
 *
 * @author wintermute
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Career extends ElementWithTimePeriod
{
    private String job;
    private String company;
    private String school;
    private String graduation;
}
