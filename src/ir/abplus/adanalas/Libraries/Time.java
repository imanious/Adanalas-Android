package ir.abplus.adanalas.Libraries;

public class Time
{
	public static final String TIME_FORMAT = "hhmm";
	short hour;
	short minute;
	
	public Time(short hour, short minute)
	{
		this.hour = hour;
		this.minute = minute;
	}
	
	@Override
	public String toString()
	{
		return hour+":"+String.format("%02d", minute);
	}

	public String getSTDString()
	{
		if(hour < 10 && minute < 10)
			return "0"+hour+"0"+minute;
		if(hour < 10)
			return "0"+hour+""+minute;
		if(minute < 10)
			return ""+hour+"0"+minute;
		return hour+""+minute;
	}
}
