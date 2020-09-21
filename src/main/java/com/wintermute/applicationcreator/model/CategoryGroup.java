package com.wintermute.applicationcreator.model;

/**
 * Represents group of elements with category.
 *
 * @author wintermute
 */
public class CategoryGroup extends ElementWithRating
{
    /**
     * Creates an instance.
     *
     * @param category to group it by.
     */
    public CategoryGroup(String category, String title, int rating)
    {
        this.setTitle(title);
        this.setCategory(category);
        this.setRating(rating);
    }
}
