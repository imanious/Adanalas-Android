package ir.abplus.adanalas.Setting;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import com.fourmob.datetimepicker.date.PersianCalendar;
import ir.abplus.adanalas.Charts.ChartActivity;
import ir.abplus.adanalas.Libraries.*;
import ir.abplus.adanalas.R;
import ir.abplus.adanalas.SyncCloud.ConnectionManager;
import ir.abplus.adanalas.SyncCloud.Declaration;
import ir.abplus.adanalas.SyncCloud.JsonParser;
import ir.abplus.adanalas.Timeline.TimelineItem2;
import ir.abplus.adanalas.Uncategoried.UncategoriedActivity;
import ir.abplus.adanalas.databaseConnections.LocalDBServices;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Keyvan Sasani on 9/3/2014.
 */
public class SettingActivity extends Activity {
    Button commitButton;
    Button generateButton;
    Button accountButton;
    Button tomanButton;
    Button toasandButton;
    Button rialButton;
    Button getAllTransButton;
    Button clearDBButton;
    Button signOutButton;
    Button doDeclareButton;
    Spinner accountSpinner;
    TransactoinDatabaseHelper trHelper;
    public static String defaultAccount;
    static ArrayList<String> accountsList;
    ArrayList<TimelineItem2> listItems=new ArrayList<TimelineItem2>();
    public static int LOGOUT_CODE=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        commitButton=(Button)findViewById(R.id.commit_button);
        generateButton=(Button)findViewById(R.id.generate_button);
        accountButton=(Button)findViewById(R.id.test_account_button);
        accountSpinner=(Spinner) findViewById(R.id.account_spinner);
        tomanButton=(Button)findViewById(R.id.toman_button);
        toasandButton=(Button)findViewById(R.id.tousandtoman_button);
        rialButton=(Button)findViewById(R.id.rial_button);
        clearDBButton =(Button)findViewById(R.id.clearDB);
        getAllTransButton=(Button)findViewById(R.id.syncAllTrans);
        signOutButton=(Button)findViewById(R.id.signout_button);
        doDeclareButton=(Button)findViewById(R.id.declare_button);



        addAccountsToList();
        ArrayAdapter dataAdapter=new ArrayAdapter(this,android.R.layout.simple_spinner_item,accountsList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountSpinner.setAdapter(dataAdapter);

        accountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                defaultAccount=((String)accountSpinner.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        commitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                try {
                    commitData();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
//
        generateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                int n = (int) (Math.random()*10) + 1;
                PersianDate date = new PersianDate((short)(Math.random()*29+1), (short)(Math.random()*4), (short)(1393+Math.random()*2), PersianCalendar.weekdayFullNames[(int)(1+Math.random()*6)]);
                for(int i = 0; i < n; i++)
                {
                    Random r = new Random();
                    int Low = 100;
                    int High = 1000;
                    int R = r.nextInt(High-Low) + Low;

                    R*=100;
//                    double amount=Double.parseDouble(String.format("%." + 2 + "f", Math.random()*100));
                    double amount=(double)R;
                    Time time = new Time((short)(Math.random()*24), (short)(Math.random()*60));
                    //todo make random item
//                    TimelineItem2 t = new TimelineItem2("0", Math.random()<0.2? false: true, amount, date, time, (int)(Math.random()*11),null,false,"",(String)accountSpinner.getSelectedItem());
//                    listItems.add(t);
                }
            }
        });

        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTestAccountToDb();
            }
        });

        rialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Currency.setCurrency(Currency.RIAL);
            }
        });

        tomanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Currency.setCurrency(Currency.TOMAN);
            }
        });

        toasandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Currency.setCurrency(Currency.THOUSAND_TOMAN);
            }
        });

        doDeclareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Declaration(SettingActivity.this);
            }
        });

        clearDBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            LocalDBServices.clearTable(SettingActivity.this,TransactionsContract.TransactionEntry.TABLE_NAME);
            LocalDBServices.clearTable(SettingActivity.this,TransactionsContract.Accounts.TABLE_NAME);
            LocalDBServices.clearTable(SettingActivity.this,TransactionsContract.Tokens.TABLE_NAME);
            LocalDBServices.clearTable(SettingActivity.this,TransactionsContract.DeletedTransactions.TABLE_NAME);
            LocalDBServices.clearTable(SettingActivity.this,TransactionsContract.SyncLogData.TABLE_NAME);
            LocalDBServices.clearTable(SettingActivity.this,TransactionsContract.TagsEntry.TABLE_NAME);
            }
        });

        getAllTransButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {

                    JsonParser jsonParser=JsonParser.getInstance();
//                    Account account=jsonParser.getUserAccount();

                    String accountIn=jsonParser.getAccountInfo();
                    jsonParser.readAndParseAccountJSON(accountIn);
                    Account account=jsonParser.getUserAccount();
                    LocalDBServices.addJsonAccounts(getBaseContext(),account);


                    PersianCalendar calendar = new PersianCalendar();
                    int selectedDay = calendar.get(PersianCalendar.DAY_OF_MONTH);
                    int selectedMonth = calendar.get(PersianCalendar.MONTH);
                    int selectedYear = calendar.get(PersianCalendar.YEAR);
                    int selectedWeekday = calendar.get(PersianCalendar.DAY_OF_WEEK);

                    PersianDate date = new PersianDate((short)selectedDay, (short)selectedMonth, (short)selectedYear, PersianCalendar.weekdayFullNames[selectedWeekday]);

                    int monthInt=Integer.parseInt(date.getSTDString().substring(4,6))+1;
                        String dateString=date.getSTDString().substring(0,4)+monthInt+date.getSTDString().substring(6,8);
                        Log.e("debug","last sync date is set to"+dateString);
                        LocalDBServices.updateSyncTime(getBaseContext(),dateString);


                    String transExpenseIn=jsonParser.getAllTransaction(account, "d", "0");
                    jsonParser.readAndParseTransactionJSON(transExpenseIn);
                    ArrayList <TimelineItem2> t2=jsonParser.getTransItems();
                    for(int i=0;i<t2.size();i++){
                        LocalDBServices.addJsonTransactionForce(getBaseContext(), t2.get(i).getTransactionID(), t2.get(i).getDateString(), t2.get(i).getAmount(), t2.get(i).isExpence(), t2.get(i).getAccountName(), t2.get(i).getCategoryID(), t2.get(i).getTags(), t2.get(i).getDescription());
                    }
                    if(t2.size()==100){
                        int j=1;
                        transExpenseIn=jsonParser.getAllTransaction(account, "d", j * 100 + "");
                        jsonParser.readAndParseTransactionJSON(transExpenseIn);
                        t2=jsonParser.getTransItems();
                        for(int i=0;i<t2.size();i++){
                            LocalDBServices.addJsonTransactionForce(getBaseContext(), t2.get(i).getTransactionID(), t2.get(i).getDateString(), t2.get(i).getAmount(), t2.get(i).isExpence(), t2.get(i).getAccountName(), t2.get(i).getCategoryID(), t2.get(i).getTags(), t2.get(i).getDescription());
                        }
                    }

                    String transIncomeIn=jsonParser.getAllTransaction(account, "c", "0");
                    jsonParser.readAndParseTransactionJSON(transIncomeIn);
                    t2=jsonParser.getTransItems();
                    for(int i=0;i<t2.size();i++){
                        LocalDBServices.addJsonTransactionForce(getBaseContext(), t2.get(i).getTransactionID(), t2.get(i).getDateString(), t2.get(i).getAmount(), t2.get(i).isExpence(), t2.get(i).getAccountName(), t2.get(i).getCategoryID(), t2.get(i).getTags(), t2.get(i).getDescription());
                    }
                    if(t2.size()==100){
                        int j=1;
                        transExpenseIn=jsonParser.getAllTransaction(account, "c", j * 100 + "");
                        jsonParser.readAndParseTransactionJSON(transExpenseIn);
                        t2=jsonParser.getTransItems();
                        for(int i=0;i<t2.size();i++){
                            LocalDBServices.addJsonTransactionForce(getBaseContext(), t2.get(i).getTransactionID(), t2.get(i).getDateString(), t2.get(i).getAmount(), t2.get(i).isExpence(), t2.get(i).getAccountName(), t2.get(i).getCategoryID(), t2.get(i).getTags(), t2.get(i).getDescription());
                        }
                    }
                }
                catch (Exception e){
                    Log.e("debug","there is a problem on posting cookie, should try login again");
                    LocalDBServices.invalidTokens(getBaseContext());
                    ConnectionManager.pfmCookie="";
                    ConnectionManager.pfmToken="";
                    setResult(LOGOUT_CODE);
                    finish();
                }

            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectionManager.pfmCookie="";
                ConnectionManager.pfmToken="";
                setResult(LOGOUT_CODE);
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_setting, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
//        System.out.println(item.toString());
        Intent intent = null;
        boolean doNothig=false;
        if(item.toString().equals(getResources().getString(R.string.action_timeline))){
//            intent=new Intent(UncategoriedActivity.this, UncategoriedActivity.class);
        }
        else if(item.toString().equals(getResources().getString(R.string.action_uncategorized))){
            intent=new Intent(SettingActivity.this, UncategoriedActivity.class);
        }
        else if(item.toString().equals(getResources().getString(R.string.action_charts))){
            intent=new Intent(SettingActivity.this, ChartActivity.class);
        }
        else if(item.toString().equals(getResources().getString(R.string.action_settings))){
            doNothig=true;
        }
        if(!doNothig){
            if(intent!=null){
                finish();
                startActivity(intent);

            }
            else finish();}
        overridePendingTransition(0,0);
        return super.onOptionsItemSelected(item);
    }
    private void commitData() throws ParseException
    {

//            SQLiteDatabase db = trHelper.getWritableDatabase();

//            trHelper.onUpgrade(db, 1, 2);
            for(TimelineItem2 t: listItems)
            {
//                PersianDate date = t.date;
//                Time time = t.time;
//                String dateTime = date.getSTDString()+time.getSTDString();

                ContentValues values = new ContentValues();
                values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_DATE_TIME, t.getDateString());
                values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_AMOUNT, t.getAmount());
                values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_IS_EXPENSE, t.isExpence());
                values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_CATEGORY, t.getCategoryID());
                values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_ACCOUNT_NAME,t.getAccountName());

//                db.insert(TransactionsContract.TransactionEntry.TABLE_NAME, null, values);
                LocalDBServices.addNewTransaction(getBaseContext(),t.getDateString(),t.getAmount(),t.isExpence(),t.getAccountName(),t.getCategoryID(),t.getTags(),t.getDescription());
            }

    }
    private void addTestAccountToDb(){
//        TransactoinDatabaseHelper trHelper=new TransactoinDatabaseHelper(this);
//        SQLiteDatabase db = trHelper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(TransactionsContract.Accounts.COLUMN_NAME_Account_Name,"نقدی 1");
//        values.put(TransactionsContract.Accounts.COLUMN_NAME_Account_Type,1);
//        db.insertOrThrow(TransactionsContract.Accounts.TABLE_NAME, null, values);
//
//
//        values = new ContentValues();
//        values.put(TransactionsContract.Accounts.COLUMN_NAME_Account_Name,"نقدی 2");
//        values.put(TransactionsContract.Accounts.COLUMN_NAME_Account_Type,1);
//        db.insert(TransactionsContract.Accounts.TABLE_NAME,null,values);
//
//        values = new ContentValues();
//        values.put(TransactionsContract.Accounts.COLUMN_NAME_Account_Name,"کوتاه مدت آینده");
//        values.put(TransactionsContract.Accounts.COLUMN_NAME_Account_Type,0);
//        db.insert(TransactionsContract.Accounts.TABLE_NAME,null,values);
        LocalDBServices.addTestAccounts(getBaseContext());
    }

    private void addAccountsToList() {
//        SQLiteDatabase db = trHelper.getReadableDatabase();
//        Cursor c2;
        accountsList=new ArrayList<String>();
//        String query="select "+ TransactionsContract.Accounts.COLUMN_NAME_Account_Name+
//                " from "+ TransactionsContract.Accounts.TABLE_NAME;
//
//        c2=db.rawQuery(query,null);
        Cursor c2=LocalDBServices.getAccountList(this);
        c2.moveToFirst();


        if(c2.getCount() != 0)
        {
            do
            {
                String accountName=c2.getString(c2.getColumnIndexOrThrow(TransactionsContract.Accounts.COLUMN_NAME_Account_Name));
                if(c2!=null){
//                    if(c2.isFirst())

                        accountsList.add(accountName);
//                    else
//                        accountsAndTimeFilter.add(new FilterMenuItem("", true,accountName, false, R.drawable.vaam_raw));
                }
            }while(c2.moveToNext());
        }
     c2.close();
    }

    public static String getAccountType(String accountName,Context context){
//        TransactoinDatabaseHelper trHelper=new TransactoinDatabaseHelper(context);
//        SQLiteDatabase db =trHelper.getReadableDatabase();
        Cursor c2;
        String accountType="-1";
        accountsList=new ArrayList<String>();
//        String query="select "+ TransactionsContract.Accounts.COLUMN_NAME_Account_Name+
//                " , "+TransactionsContract.Accounts.COLUMN_NAME_Account_Type+
//                " from "+ TransactionsContract.Accounts.TABLE_NAME;

        c2=LocalDBServices.getAccountList(context);
//        c2=db.rawQuery(query,null);
        c2.moveToFirst();

        if(c2.getCount() != 0)
        {
            do
            {
                String accountNameDB=c2.getString(c2.getColumnIndexOrThrow(TransactionsContract.Accounts.COLUMN_NAME_Account_Name));
                String accountTypeDB=c2.getString(c2.getColumnIndexOrThrow(TransactionsContract.Accounts.COLUMN_NAME_Account_Type));
                if(c2!=null){
                    if(accountNameDB.equals(accountName))
                    accountType=accountTypeDB;
                }
            }while(c2.moveToNext());
        }
        c2.close();
        if(accountType.equals("-1"))
            Log.e("error","an error happend on finding account type:setting activity.getaccountType");
        return accountType;
    }
}
