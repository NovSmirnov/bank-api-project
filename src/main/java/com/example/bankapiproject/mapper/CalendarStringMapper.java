package com.example.bankapiproject.mapper;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class CalendarStringMapper {

    public static Calendar stringToCalendar(String date) {
        String year = date.substring(0, 4);
        String month = date.substring(5, 7);
        String day = date.substring(8, 10);
        return new GregorianCalendar(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));

    }
}
