package com.udacity.stockhawk.data;

import java.util.Date;

/**
 * @author Kasim Kovacevic on 3/10/17.
 */
public class QuoteHistoryModel {

    private Date date;
    private Float value;


    public QuoteHistoryModel(String date, String value) {
        this.date = new Date(Long.valueOf(date));
        this.value = Float.valueOf(value);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }
}
