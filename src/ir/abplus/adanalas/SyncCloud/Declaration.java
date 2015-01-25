package ir.abplus.adanalas.SyncCloud;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import ir.abplus.adanalas.Libraries.Category;
import ir.abplus.adanalas.Libraries.TransactionsContract;
import ir.abplus.adanalas.Timeline.TimelineItem2;
import ir.abplus.adanalas.databaseConnections.LocalDBServices;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Keyvan Sasani on 1/21/2015.
 */
public class Declaration {
    public static String PFM_DECLARATION_ADDRESS="https://pfm.abplus.ir/declarations";
    Context context;

    public Declaration(Context context) {
        String result="";
        this.context=context;
        try {
            JSONArray jsonArray=makeDeclaration(context);
            if(!jsonArray.toString().equals("[]")) {
                result = postDeclaration(makeDeclaration(context));
            }
            else {
                Log.e("debug","There is no data to declare");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("debug", result);
    }
    public void DoDeclaration(Context context){
//        String result=postDeclaration(makeDeclaration(context));
//        Log.e("debug", result);
    }

    public JSONArray makeDeclaration(Context context){
        Cursor cursor= LocalDBServices.getUnsyncedTransactions(context);
        JSONArray transArray=new JSONArray();
        ArrayList<TimelineItem2> items=new ArrayList<TimelineItem2>();
        TimelineItem2 item;
        String id;
        String dateTime;
        long amount;
        boolean isExpense;
        int category_index;
        String descp;
        String accountName;

        String[] selectedTags=new String[0];
        cursor.moveToFirst();
        if (cursor.getCount()>0){
            do{

                id = cursor.getString(cursor.getColumnIndexOrThrow(TransactionsContract.TransactionEntry.COLUMN_NAME_TRANSACTION_ID));
                dateTime = cursor.getString(cursor.getColumnIndexOrThrow(TransactionsContract.TransactionEntry.COLUMN_NAME_DATE_TIME));
                amount = cursor.getLong(cursor.getColumnIndexOrThrow(TransactionsContract.TransactionEntry.COLUMN_NAME_AMOUNT));
                isExpense = cursor.getInt(cursor.getColumnIndexOrThrow(TransactionsContract.TransactionEntry.COLUMN_NAME_IS_EXPENSE))==0? false: true;
                category_index = cursor.getInt(cursor.getColumnIndexOrThrow(TransactionsContract.TransactionEntry.COLUMN_NAME_CATEGORY));
                descp=cursor.getString(cursor.getColumnIndexOrThrow(TransactionsContract.TransactionEntry.COLUMN_NAME_DESCRIPTION));
                accountName=cursor.getString(cursor.getColumnIndexOrThrow(TransactionsContract.TransactionEntry.COLUMN_NAME_ACCOUNT_NAME));

                Cursor c2= LocalDBServices.getTagsFromID(id);
                c2.moveToFirst();

                if(c2.getCount() != 0)
                {
                    int tagLength=c2.getCount();
                    selectedTags=new String[tagLength];
                    for(int i=0;i<tagLength;i++){
                        selectedTags[i]="\""+c2.getString(c2.getColumnIndexOrThrow(TransactionsContract.TagsEntry.COLUMN_NAME_TAG))+"\"";
                        c2.moveToNext();
                    }
                }
                c2.close();

                System.out.println("@@@"+Arrays.toString(selectedTags));
                JSONObject tmpObject=new JSONObject();
                try {
                    tmpObject.put("id",id);
                    tmpObject.put("deposit",accountName);
                    tmpObject.put("date",standardDate(dateTime));
                    tmpObject.put("amount",amount);
                    tmpObject.put("tags",Arrays.toString(selectedTags));
                    if(isExpense){
                        tmpObject.put("category", Category.getExpenseCategoryString(category_index));
                        tmpObject.put("type","d");
                    }
                    else {
                        tmpObject.put("category", Category.getIncomeCategoryString((category_index)));
                        tmpObject.put("type","c");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            transArray.put(tmpObject);
            }while(cursor.moveToNext());
            cursor.close();
        }
        return transArray;
    }
    public JSONArray makeDeclaration2(Context context){
        Cursor cursor= LocalDBServices.getUnsyncedTransactionsAndTags(context);
        JSONArray transArray=new JSONArray();
        ArrayList<TimelineItem2> items=new ArrayList<TimelineItem2>();
        TimelineItem2 item;
        String id;
        String dateTime;
        long amount;
        boolean isExpense;
        int category_index;
        String descp;
        String accountName;

        String[] selectedTags=new String[0];
        cursor.moveToFirst();
        if (cursor.getCount()>0){
            do{

                id = cursor.getString(cursor.getColumnIndexOrThrow(TransactionsContract.TransactionEntry.COLUMN_NAME_TRANSACTION_ID));
                dateTime = cursor.getString(cursor.getColumnIndexOrThrow(TransactionsContract.TransactionEntry.COLUMN_NAME_DATE_TIME));
                amount = cursor.getLong(cursor.getColumnIndexOrThrow(TransactionsContract.TransactionEntry.COLUMN_NAME_AMOUNT));
                isExpense = cursor.getInt(cursor.getColumnIndexOrThrow(TransactionsContract.TransactionEntry.COLUMN_NAME_IS_EXPENSE))==0? false: true;
                category_index = cursor.getInt(cursor.getColumnIndexOrThrow(TransactionsContract.TransactionEntry.COLUMN_NAME_CATEGORY));
                descp=cursor.getString(cursor.getColumnIndexOrThrow(TransactionsContract.TransactionEntry.COLUMN_NAME_DESCRIPTION));
                accountName=cursor.getString(cursor.getColumnIndexOrThrow(TransactionsContract.TransactionEntry.COLUMN_NAME_ACCOUNT_NAME));

                Cursor c2= LocalDBServices.getTagsFromID(id);
                c2.moveToFirst();

                if(c2.getCount() != 0)
                {
                    int tagLength=c2.getCount();
                    selectedTags=new String[tagLength];
                    for(int i=0;i<tagLength;i++){
                        selectedTags[i]="\""+c2.getString(c2.getColumnIndexOrThrow(TransactionsContract.TagsEntry.COLUMN_NAME_TAG))+"\"";
                        c2.moveToNext();
                    }
                }
                c2.close();

                System.out.println("@@@"+Arrays.toString(selectedTags));
                JSONObject tmpObject=new JSONObject();
                try {
                    tmpObject.put("id",id);
                    tmpObject.put("deposit",accountName);
                    tmpObject.put("date",standardDate(dateTime));
                    tmpObject.put("amount",amount);
                    tmpObject.put("tags",Arrays.toString(selectedTags));
                    if(isExpense){
                        tmpObject.put("category", Category.getExpenseCategoryString(category_index));
                        tmpObject.put("type","d");
                    }
                    else {
                        tmpObject.put("category", Category.getIncomeCategoryString((category_index)));
                        tmpObject.put("type","c");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            transArray.put(tmpObject);
            }while(cursor.moveToNext());
            cursor.close();
        }
        return transArray;
    }
    public String postDeclaration(JSONArray jsonArray) throws IOException {

//        String url = "https://selfsolve.apple.com/wcResults.do";
        URL obj = new URL(PFM_DECLARATION_ADDRESS);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-type", "application/json");
        con.setRequestProperty("X-CSRF-Token",ConnectionManager.pfmToken);
        con.setRequestProperty("Accept-Encoding","gzip, deflate");
        con.setRequestProperty("Accept-Language","en-GB,en-US;q=0.8,en;q=0.6,fa;q=0.4");

        con.setRequestProperty("Cookie", "sid=" + ConnectionManager.pfmCookie);

//        String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//        wr.writeBytes(standardJsonObject(jsonArray.toString()));
        wr.write(standardJsonObject(jsonArray.toString()).getBytes("UTF-8"));
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + PFM_DECLARATION_ADDRESS);
        System.out.println("Post parameters : " + jsonArray.toString());
        System.out.println("Post parameters : " + standardJsonObject(jsonArray.toString()));
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());
//        LocalDBServices.setSyncTransactions(context);
//        Cursor cursor= LocalDBServices.getUnsyncedTransactions(context);
//        cursor.moveToFirst();
//        if (cursor.getCount()>0) {
//            do {
//                System.out.println("این مبلغ سینک نشده : " + cursor.getLong(cursor.getColumnIndexOrThrow(TransactionsContract.TransactionEntry.COLUMN_NAME_AMOUNT)));
//            } while (cursor.moveToNext());
//
//        }
//        cursor.close();

        return "null";
    }
    private String standardDate(String date){
        String tmp1=date.substring(0,4);
        String tmp2=date.substring(4,6);
        String tmp3=date.substring(6,14);
        int month=Integer.parseInt(tmp2)+1;
        if(month<10){
            tmp2="0"+month;}
        else{
            tmp2=month+"";
        }
        return tmp1+tmp2+tmp3;
    }
    private String standardJsonObject(String inputString){
        inputString=inputString.replaceAll("tags\":\"","tags\":");
        inputString=inputString.replaceAll("]\",","],");
        inputString=inputString.replaceAll("\\\\","");
        return inputString;
    }

}
