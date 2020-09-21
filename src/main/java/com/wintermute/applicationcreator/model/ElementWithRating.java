package com.wintermute.applicationcreator.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents sortable data by its rating points.
 *
 * @author wintermute
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class ElementWithRating extends ElementWithCategory
{
    private int rating;
}
