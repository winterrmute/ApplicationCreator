package com.wintermute.applicationcreator.data.sort;

import com.wintermute.applicationcreator.model.ElementWithRating;

import java.util.Comparator;

/**
 * Compares elements containing rating scala.
 *
 * @author wintermute
 */
public class RatingComparator implements Comparator<ElementWithRating>
{
    @Override
    public int compare(ElementWithRating e1, ElementWithRating e2)
    {
        if (e1.getRating() > e2.getRating()) {
            return 1;
        }
        else if (e1.getRating() < e2.getRating()) {
            return -1;
        }
        return 0;
    }
}
