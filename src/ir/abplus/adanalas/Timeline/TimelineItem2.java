package ir.abplus.adanalas.Timeline;

import android.widget.LinearLayout;
import com.fourmob.datetimepicker.date.PersianCalendar;
import ir.abplus.adanalas.Libraries.PersianDate;
import ir.abplus.adanalas.Libraries.Time;

import java.util.ArrayList;

public class TimelineItem2
{
    private String transactionID;
    private boolean isExpence;
	private Long amount;
//	private PersianDate date;
//	private Time time;
    private String dateString;
	private int categoryID;

    private ArrayList<String> tags;

    private String description;
    private String accountName;

    private String detail="";
    private String operation="";
    private Boolean handy=false;
    private Boolean hidden=false;


    private LinearLayout tagLayout;

    public TimelineItem2(String transactionID, boolean isExpence, Long amount, String dateString, int categoryID, ArrayList<String> tags,  String description, String accountName,String detail,String operation,boolean handy, boolean hidden,LinearLayout tagLayout)
    {
        this.transactionID = transactionID;
        this.isExpence = isExpence;
        this.amount = amount;
//        this.date = date;
//        this.time = time;
        this.dateString=dateString;
        this.categoryID = categoryID;
        this.tags=tags;
        this.description=description;
        this.accountName=accountName;
        this.detail=detail;
        this.operation=operation;
        this.handy=handy;
        this.hidden=hidden;
    }




    public boolean isExpence() {
        return isExpence;
    }

    public Long getAmount() {
        return amount;
    }

    public String getDateString() {
        return dateString;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public String  getTransactionID() {
        return transactionID;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public LinearLayout getTagLayout() {
        return tagLayout;
    }

    public String getDescription() {
        return description;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getDetail() {
        return detail;
    }

    public String getOperation() {
        return operation;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public Boolean getHandy() {
        return handy;
    }

    public String getFormatedDate(){
      int  year = Integer.parseInt(dateString.substring(0, 4));
       int month = Integer.parseInt(dateString.substring(4, 6));
       int day = Integer.parseInt(dateString.substring(6, 8));
        PersianCalendar tmpCal = new PersianCalendar(year, month, day);
        PersianDate date = new PersianDate((short)day, (short)(month+1), (short)year, PersianCalendar.weekdayFullNames[tmpCal.get(PersianCalendar.DAY_OF_WEEK)]);
        return date.toString();
    }
    public String getFormatedTime(){
        int hour = Integer.parseInt(dateString.substring(8, 10));
        int minute = Integer.parseInt(dateString.substring(10, 12));
        Time time = new Time((short)hour, (short)minute);
        return time.toString();
    }

    private String parentId="";
    private String terminal="";
    private String acquirer="";
    private String guild="";
    private String store="";
    private String merchant="";
    private Boolean split=false;
    private String target="";
    private String rule="";
    private String row="";
    private String hint="";
    private String billNumber="";
    private double balance;
    private double latitude;
    private double longitude;

//    private String id="";
//    private String deposit="";
//    private String date="";
//    private String amount="";
//    private String type="";
//    private String category="";
//    private String tags="";

//    private String description="";


}
