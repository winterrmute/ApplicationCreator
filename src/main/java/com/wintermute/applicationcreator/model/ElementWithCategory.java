package com.wintermute.applicationcreator.model;

import lombok.Data;

/**
 * Describes data entries which have fixed category.
 *
 * @author wintermute
 */
@Data
public abstract class ElementWithCategory
{
    private String title;
    private String category;
}
