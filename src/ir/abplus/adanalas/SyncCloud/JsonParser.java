package ir.abplus.adanalas.SyncCloud;

import android.annotation.SuppressLint;
import android.util.Log;
import ir.abplus.adanalas.Libraries.Account;
import ir.abplus.adanalas.Libraries.Category;
import ir.abplus.adanalas.Libraries.Deposit;
import ir.abplus.adanalas.Timeline.TimelineItem2;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Keyvan Sasani on 12/19/2014.
 */
public class JsonParser {

    private String TransUrlString = null;
    private String AccountUrlString = null;

    private ArrayList<TimelineItem2> transItems;
    private Account userAccount;

    public Account getUserAccount() {
        return userAccount;
    }

    public ArrayList<TimelineItem2> getTransItems() {
        return transItems;
    }

    public volatile boolean parsingComplete = true;
    public JsonParser(String transUrlString,String accountUrlString){
        this.TransUrlString = transUrlString;
        this.AccountUrlString=accountUrlString;

    }

    public String getTransUrlString() {
        return TransUrlString;
    }

    @SuppressLint("NewApi")
    public void readAndParseTransactionJSON(String in) {
        try {
            transItems=new ArrayList<TimelineItem2>();
            String transactionID;
            boolean isExpense=false;
            double amount;
//            PersianDate date;
//            Time time;
            int categoryID;

            ArrayList<String> tags;

            String description="";
            String accountName="";

            String detail="";
            String operation="";
            Boolean handy=false;
            Boolean hidden=false;

            String type;
            String dateString="";
            String categoryString;
            int year;
            int month ;
            String rest;
//            PersianCalendar tmpCal;


            JSONArray transArray=new JSONArray(in);
            for(int i=0;i<transArray.length();i++){
                tags=new ArrayList<String>();
                JSONObject transaction= (JSONObject)transArray.get(i);
                transactionID=transaction.getString("id");
                type=transaction.getString("type");
                if(type.equals("c"))
                    isExpense=false;
                else if(type.equals("d"))
                    isExpense=true;
                else
                    Log.e("bug","transaction type does not set!!!");
                amount=transaction.getDouble("amount");
                dateString=transaction.getString("date");
                year= Integer.parseInt(dateString.substring(0, 4));
                month= Integer.parseInt(dateString.substring(4, 6));
                rest= dateString.substring(6, 12);
//                hour= Integer.parseInt(dateString.substring(8, 10));
//                minute = Integer.parseInt(dateString.substring(10, 12));
                month=month-1;
                if(month<10)
                    dateString=year+"0"+month+rest;
                else
                    dateString=year+""+month+rest;
//                tmpCal = new PersianCalendar(year, month, day);
//                date = new PersianDate((short)day, (short)(month+1), (short)year, PersianCalendar.weekdayFullNames[tmpCal.get(PersianCalendar.DAY_OF_WEEK)]);
//                time = new Time((short)hour, (short)minute);

                categoryString=transaction.getString("category");
                if(isExpense)
                    categoryID=Category.getExpenseCategoryID(categoryString);
                else
                    categoryID=Category.getIncomeCategoryID(categoryString);

                JSONArray tagsArray=transaction.getJSONArray("tags");
                for(int j = 0; j < tagsArray.length(); j++){
                    tags.add(tagsArray.optString(j));
                }
                if(transaction.has("description"))
                description=transaction.getString("description");
                accountName=transaction.getString("deposit");
                if(transaction.has("detail"))
                detail=transaction.getString("detail");
                if(transaction.has("operation"))
                operation=transaction.getString("operation");
                if(transaction.has("handy"))
                handy=transaction.getBoolean("handy");
                hidden=transaction.getBoolean("hidden");

                TimelineItem2 item=new TimelineItem2(transactionID,isExpense,amount,dateString,categoryID,tags,description,accountName,detail,operation,handy,hidden,null);
                transItems.add(item);
            }



        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    public void readAndParseAccountJSON(String in) {
         String created;
         String email;
         String modified;
         String filterConfig;
         String budget;
         String firstName;
         String lastName;
         String id;
         ArrayList<String> tags;
         ArrayList<Deposit> deposits;

        try {
          JSONObject me=new JSONObject(in);
            created=me.getString("created");
            email=me.getString("email");
            modified=me.getString("modified");
            filterConfig=me.getString("filterConfig");
            budget=me.getString("budget");
            firstName=me.getString("firstName");
            lastName=me.getString("lastName");
            id=me.getString("id");

            JSONArray tagsArray=me.getJSONArray("tags");

            tags=new ArrayList<String>();
            for(int j = 0; j < tagsArray.length(); j++){
                tags.add(tagsArray.optString(j));
            }
            JSONArray depositsArray=me.getJSONArray("deposits");

            deposits=new ArrayList<Deposit>();

             String code;
             String type;
             boolean shared=false;
            for(int j = 0; j < depositsArray.length(); j++){
                JSONObject tmpJson=(JSONObject)depositsArray.get(j);
                code=tmpJson.getString("code");
                type=tmpJson.getString("type");
                if(tmpJson.has("shared"))
                shared=tmpJson.getBoolean("shared");
                Deposit tmpDeposit=new Deposit(code,shared,type);
                deposits.add(tmpDeposit);
            }
            userAccount=new Account(created,email,modified,filterConfig,budget,firstName,lastName,id,tags,deposits);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void fetchJSON(){
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    URL url = new URL(TransUrlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    // Starts the query
                    conn.connect();
                    InputStream stream = conn.getInputStream();

                    String data = convertStreamToString(stream);

                    readAndParseTransactionJSON(data);
                    stream.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public String getAccountUrlString() {
        return AccountUrlString;
    }
}
