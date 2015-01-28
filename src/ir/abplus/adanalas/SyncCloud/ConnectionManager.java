package ir.abplus.adanalas.SyncCloud;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.fourmob.datetimepicker.date.PersianCalendar;
import ir.abplus.adanalas.Libraries.Account;
import ir.abplus.adanalas.Libraries.PersianDate;
import ir.abplus.adanalas.Timeline.TimelineItem2;
import ir.abplus.adanalas.databaseConnections.LocalDBServices;

import java.util.ArrayList;

/**
 * Created by Keyvan Sasani on 12/17/2014.
 */
public class ConnectionManager {
    public static String pfmToken="";
    public static String pfmCookie="";

public boolean doSync(Context context){

    try {
        Log.e("debug", "getNewTransFromServer called");
        JsonParser jsonParser=JsonParser.getInstance();
        String accountIn=jsonParser.getAccountInfo();
        Account account=jsonParser.readAndParseAccountJSON(accountIn);
        LocalDBServices.addJsonAccounts(context, account);

        Log.e("debug", "expense called");
        new GetAllTrans(account,context,"d").execute();
        Log.e("debug", "income called");
        new GetAllTrans(account,context,"c").execute();

        Log.e("debug", "update time called");
        PersianCalendar calendar = new PersianCalendar();
        int selectedDay = calendar.get(PersianCalendar.DAY_OF_MONTH);
        int selectedMonth = calendar.get(PersianCalendar.MONTH);
        int selectedYear = calendar.get(PersianCalendar.YEAR);
        int selectedWeekday = calendar.get(PersianCalendar.DAY_OF_WEEK);

        PersianDate date = new PersianDate((short)selectedDay, (short)selectedMonth, (short)selectedYear, PersianCalendar.weekdayFullNames[selectedWeekday]);
        int monthInt=Integer.parseInt(date.getSTDString().substring(4,6))+1;
        String dateString=date.getSTDString().substring(0,4)+monthInt+date.getSTDString().substring(6,8);
        LocalDBServices.updateSyncTime(context,dateString);
        Log.e("debug","sync time updated "+ LocalDBServices.getSyncTime(context));
    }
    catch (Exception e){
        e.printStackTrace();
        Log.e("debug","there is a problem on posting cookie, should try login again");
        LocalDBServices.invalidTokens(context);
//            ConnectionManager.pfmCookie="";
//            ConnectionManager.pfmToken="";
        return false;
    }
    return true;
}

    private class GetAllTrans extends AsyncTask<Void, Void, Void> {
        Account account;
        Context context;
        String type;
        public GetAllTrans(Account account,Context context,String type){
            this.account=account;
            this.context=context;
            this.type=type;

        }

        @Override
        protected Void doInBackground(Void... voids) {

            JsonParser jsonParser=JsonParser.getInstance();
            String transExpenseIn=jsonParser.getAllTransaction(account,type, "0");
            jsonParser.readAndParseTransactionJSON(transExpenseIn);
            ArrayList<TimelineItem2> t2=jsonParser.getTransItems();
            for(int i=0;i<t2.size();i++){
                LocalDBServices.addJsonTransactionForce(context,t2.get(i).getTransactionID(), t2.get(i).getDateString(), t2.get(i).getAmount(), t2.get(i).isExpence(), t2.get(i).getAccountName(), t2.get(i).getCategoryID(), t2.get(i).getTags(), t2.get(i).getDescription(),t2.get(i).getHandy());
            }
            Log.e("debug", "try to get more on type: "+type);
            if(t2.size()==100){
                int j=1;
                transExpenseIn=jsonParser.getAllTransaction(account, type, j * 100 + "");
                jsonParser.readAndParseTransactionJSON(transExpenseIn);
                t2=jsonParser.getTransItems();
                for(int i=0;i<t2.size();i++){
                    LocalDBServices.addJsonTransactionForce(context,t2.get(i).getTransactionID(), t2.get(i).getDateString(), t2.get(i).getAmount(), t2.get(i).isExpence(), t2.get(i).getAccountName(), t2.get(i).getCategoryID(), t2.get(i).getTags(), t2.get(i).getDescription(),t2.get(i).getHandy());
                }
            }
            Log.e("debug", "thread finished on type: "+type);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}
