package com.wintermute.applicationcreator.model.complex;

import com.wintermute.applicationcreator.model.complex.ElementWithPosition;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents group of elements with category.
 *
 * @author wintermute
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CategoryGroup extends ElementWithPosition
{
    /**
     * Creates an instance.
     */
    public CategoryGroup(String title, int position)
    {
        this.setTitle(title);
        this.setPosition(position);
    }
}