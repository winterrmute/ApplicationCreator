package com.wintermute.applicationcreator.model.complex;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents elements which have fixed start and end date.
 *
 * @author wintermute
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class ElementWithTimePeriod extends ElementWithPosition
{
    private String description;
    private String from;
    private String until;
}
