package com.wintermute.applicationcreator.data.sort;

import com.wintermute.applicationcreator.model.complex.ElementWithPosition;

import java.util.Comparator;

/**
 * Compares elements containing rating scala.
 *
 * @author wintermute
 */
public class PositionComparator implements Comparator<ElementWithPosition>
{
    @Override
    public int compare(ElementWithPosition e1, ElementWithPosition e2)
    {
        if (e1.getPosition() > e2.getPosition())
        {
            return 1;
        } else if (e1.getPosition() < e2.getPosition())
        {
            return -1;
        }
        return 0;
    }
}
