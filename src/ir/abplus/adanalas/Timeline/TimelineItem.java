package ir.abplus.adanalas.Timeline;

import android.widget.LinearLayout;
import ir.abplus.adanalas.Libraries.PersianDate;
import ir.abplus.adanalas.Libraries.Time;

import java.util.ArrayList;

public class TimelineItem
{
	public boolean isExpence;
    boolean isSelected;
	public double amount;
	public PersianDate date;
	public Time time;
	public int categoryID;
	int transactionID;
    ArrayList<String> tags;
    LinearLayout tagLayout;
    String description;
    public String accountName;

	public TimelineItem(int transactionID, boolean isExpence, double amount, PersianDate date, Time time, int categoryID,ArrayList<String> tags,boolean isSelected,String description, String accountName)
	{
		this.transactionID = transactionID;
		this.isExpence = isExpence;
		this.amount = amount;
		this.date = date;
		this.time = time;
		this.categoryID = categoryID;
        this.tags=tags;
        this.isSelected = isSelected;
        this.description=description;
        this.accountName=accountName;
	}
}
