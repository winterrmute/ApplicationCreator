package com.wintermute.applicationcreator.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Skill extends ElementWithRating
{
    /**
     * Creates an instance.
     *
     * @param category to group it by value.
     */
    public Skill(String category)
    {
        this.setCategory(category);
    }
}
