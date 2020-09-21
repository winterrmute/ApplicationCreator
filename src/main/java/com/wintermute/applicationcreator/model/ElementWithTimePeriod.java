package com.wintermute.applicationcreator.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents elements which have fixed start and end date.
 *
 * @author wintermute h
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class ElementWithTimePeriod extends ElementWithCategory
{
    private String from;
    private String until;
}
