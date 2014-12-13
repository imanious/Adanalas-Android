package ir.abplus.adanalas.Libraries;

import java.util.Date;

public class PersianDate
{
	private static final char separator = '/';//'\u066b';
	public static final String DATE_FORMAT = "yyyyMMdd";
	String weekday;
	short day;
	short month;
	short year;
	
	public PersianDate(short day, short month, short year, String weekday)
	{
		this.day = day;
		this.month = month;
		this.year = year;
		this.weekday = weekday;
	}
	
	public static PersianDate convertToPersianDate(Date d)
	{
		return null;
	}
	
	@Override
	public String toString()
	{
		return weekday+"  "+year+separator+month+separator+day;
	}
	
	public String getDateString()
	{
		if(month < 10 && day < 10)
			return year+"-0"+month+"-0"+day;
		if(month < 10)
			return year+"-0"+month+"-"+day;
		if(day < 10)
			return year+"-"+month+"-0"+day;
		return year+"-"+month+"-"+day;
	}

	public String getSTDString()
	{
		if(month < 10 && day < 10)
			return year+"0"+month+"0"+day;
		if(month < 10)
			return year+"0"+month+""+day;
		if(day < 10)
			return year+""+month+"0"+day;
		return year+""+month+""+day;
	}
}
