package com.wintermute.applicationcreator.data.sort;

import com.wintermute.applicationcreator.model.complex.ElementWithTimePeriod;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Comparator for data containing time period. Compares reverse so the newest elements are on top.
 *
 * @author wintermute
 */
public class DateComparator implements Comparator<ElementWithTimePeriod>
{
    public int compare(ElementWithTimePeriod p, ElementWithTimePeriod q)
    {
        if (getDate(p.getFrom()).before(getDate(q.getFrom())))
        {
            return 1;
        } else if (getDate(p.getFrom()).after(getDate(q.getFrom())))
        {
            return -1;
        } else
        {
            return 0;
        }
    }

    Date getDate(String date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
        Date result;
        try
        {
            result = sdf.parse(date);
        } catch (ParseException e)
        {
            result = null;
            e.printStackTrace();
        }
        return result;
    }
}

