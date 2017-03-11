package com.udacity.stockhawk.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Kasim Kovacevic1 on 3/10/17.
 */
public class DateUtil {

    public static final String DATE_FORMAT_DD_MM_YYYY = "dd/MM/yyyy";


    /**
     * Format date in provided format
     *
     * @param date   represent instance of {@link Date} for formatting
     * @param format represent pattern for formatting
     * @return formatted date in {@link String}
     */
    public static String getFormattedDate(Date date, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }


}
