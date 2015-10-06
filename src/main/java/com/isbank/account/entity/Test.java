package com.isbank.account.entity;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;


public class Test {

	public static void main(String[] args) {
		Timestamp ts=new Timestamp(System.currentTimeMillis());
        Date date=new Date(ts.getTime());
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    cal.add(Calendar.DAY_OF_MONTH, -1);
        ts=new Timestamp(cal.getTimeInMillis());
        date=new Date(ts.getTime());
        
        System.out.println(date);
	}

}
