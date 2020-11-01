package com.wintermute.applicationcreator.model.complex;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents sortable data by its position number.
 *
 * @author wintermute
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class ElementWithPosition extends ElementWithTitle
{
    private int position;
}
