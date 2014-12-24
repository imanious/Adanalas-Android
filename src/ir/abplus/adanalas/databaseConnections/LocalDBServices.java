package ir.abplus.adanalas.databaseConnections;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.fourmob.datetimepicker.date.PersianCalendar;
import ir.abplus.adanalas.Libraries.*;
import ir.abplus.adanalas.Timeline.FilterMenuAdapter;
import ir.abplus.adanalas.Timeline.TimelineActivity;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;

//import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Keyvan Sasani on 11/26/2014.
 */
public class LocalDBServices {
    public static TransactoinDatabaseHelper trHelper;
    public static SQLiteDatabase db;
    public static String where;
    public static Cursor cursor;


    public static void addNewTransaction(Context context,String dateTime, double amountValue, boolean isExpense, String defaultAccount, int category_index,ArrayList<String> selectedTags,String description) {
//        trHelper= new TransactoinDatabaseHelper(context);
        trHelper= TransactoinDatabaseHelper.getInstance(context);
        db = trHelper.getWritableDatabase(TransactoinDatabaseHelper.DATABASE_ENCRYPT_KEY);
        ContentValues values = new ContentValues();
        String transactionId=java.util.UUID.randomUUID().toString();
        values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_DATE_TIME, dateTime);
        values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_AMOUNT, Currency.allToRial(amountValue));
        values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_IS_EXPENSE, isExpense);
        values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_ACCOUNT_NAME,defaultAccount);
        values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_CATEGORY, category_index);
        values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_DESCRIPTION, description);
        values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_TRANSACTION_ID,transactionId);

        db.insert(TransactionsContract.TransactionEntry.TABLE_NAME, null, values);

        if(selectedTags!=null){
            for(String tag: selectedTags)
            {
                values = new ContentValues();
                values.put(TransactionsContract.TagsEntry.COLUMN_NAME_TRANSACTION_ID, transactionId);
                values.put(TransactionsContract.TagsEntry.COLUMN_NAME_TAG, tag);
                db.insert(TransactionsContract.TagsEntry.TABLE_NAME, null, values);
            }
        }
    }
    public static void addJsonTransaction(Context context,String transactionId,String dateTime, double amountValue, boolean isExpense, String defaultAccount, int category_index,ArrayList<String> selectedTags,String description) {
        trHelper= TransactoinDatabaseHelper.getInstance(context);
        db = trHelper.getWritableDatabase(TransactoinDatabaseHelper.DATABASE_ENCRYPT_KEY);
        ContentValues values = new ContentValues();
        values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_DATE_TIME, dateTime);
        values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_AMOUNT, amountValue);
        values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_IS_EXPENSE, isExpense);
        values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_ACCOUNT_NAME,defaultAccount);
        values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_CATEGORY, category_index);
        values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_DESCRIPTION, description);
        values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_TRANSACTION_ID, transactionId);

        db.insert(TransactionsContract.TransactionEntry.TABLE_NAME, null, values);

        if(selectedTags!=null){
            for(String tag: selectedTags)
            {
                values = new ContentValues();
                values.put(TransactionsContract.TagsEntry.COLUMN_NAME_TRANSACTION_ID, transactionId);
                values.put(TransactionsContract.TagsEntry.COLUMN_NAME_TAG, tag);
                db.insert(TransactionsContract.TagsEntry.TABLE_NAME, null, values);
            }
        }
    }
    public static void addTestAccounts(Context context){
        trHelper= TransactoinDatabaseHelper.getInstance(context);
        SQLiteDatabase db = trHelper.getWritableDatabase(TransactoinDatabaseHelper.DATABASE_ENCRYPT_KEY);
        ContentValues values = new ContentValues();
        values.put(TransactionsContract.Accounts.COLUMN_NAME_Account_Name,"نقدی 1");
        values.put(TransactionsContract.Accounts.COLUMN_NAME_Account_Type, 1);
        db.insertOrThrow(TransactionsContract.Accounts.TABLE_NAME, null, values);


        values = new ContentValues();
        values.put(TransactionsContract.Accounts.COLUMN_NAME_Account_Name,"نقدی 2");
        values.put(TransactionsContract.Accounts.COLUMN_NAME_Account_Type,1);
        db.insert(TransactionsContract.Accounts.TABLE_NAME,null,values);

        values = new ContentValues();
        values.put(TransactionsContract.Accounts.COLUMN_NAME_Account_Name,"کوتاه مدت آینده");
        values.put(TransactionsContract.Accounts.COLUMN_NAME_Account_Type,0);
        db.insert(TransactionsContract.Accounts.TABLE_NAME,null,values);
    }
    public static void addJsonAccounts(Context context,Account account){
        //todo account name & account id should be standard -> account name is like shown and id is what we have in json file
        trHelper= TransactoinDatabaseHelper.getInstance(context);
        SQLiteDatabase db = trHelper.getWritableDatabase(TransactoinDatabaseHelper.DATABASE_ENCRYPT_KEY);
        ContentValues values = new ContentValues();
        for(int i=0;i<account.getDeposits().size();i++){
            values.put(TransactionsContract.Accounts.COLUMN_NAME_Account_Name,account.getDeposits().get(i).getCode());
            if(account.getDeposits().get(i).getType().equals("Handy"))
                values.put(TransactionsContract.Accounts.COLUMN_NAME_Account_Type,1 );
            else
                values.put(TransactionsContract.Accounts.COLUMN_NAME_Account_Type,0 );
            db.insertOrThrow(TransactionsContract.Accounts.TABLE_NAME, null, values);
        }

    }
    public static Cursor getAccountList(Context context){
        trHelper= TransactoinDatabaseHelper.getInstance(context);
        db = trHelper.getReadableDatabase(TransactoinDatabaseHelper.DATABASE_ENCRYPT_KEY);
        String query="select *"+
                " from "+ TransactionsContract.Accounts.TABLE_NAME;

        cursor=db.rawQuery(query,null);

        return cursor;
    }
    public static Cursor getHandyAccountList(Context context){
        trHelper= TransactoinDatabaseHelper.getInstance(context);
        SQLiteDatabase db = trHelper.getWritableDatabase(TransactoinDatabaseHelper.DATABASE_ENCRYPT_KEY);
        String query="select "+ TransactionsContract.Accounts.COLUMN_NAME_Account_Name+
                " from "+ TransactionsContract.Accounts.TABLE_NAME+
                " where "+ TransactionsContract.Accounts.COLUMN_NAME_Account_Type +
                " = 1";
        cursor=db.rawQuery(query,null);
        return cursor;
    }
    //todo make it better
    public static Cursor getTransactionsForBarChart(String query){
//        trHelper= TransactoinDatabaseHelper.getInstance(context);
        db = trHelper.getReadableDatabase(TransactoinDatabaseHelper.DATABASE_ENCRYPT_KEY);
        cursor=db.rawQuery(query,null);
        return cursor;
    }
    public static Cursor getTransactionsFromDBGroupedBYCategory(String where){
//        trHelper= TransactoinDatabaseHelper.getInstance(context);
        db = trHelper.getReadableDatabase(TransactoinDatabaseHelper.DATABASE_ENCRYPT_KEY);

        String query="SELECT "+TransactionsContract.TransactionEntry.COLUMN_NAME_CATEGORY
                +" as A, SUM("+TransactionsContract.TransactionEntry.COLUMN_NAME_AMOUNT
                +") as B, "+TransactionsContract.TransactionEntry.COLUMN_NAME_IS_EXPENSE
                +" as C , COUNT (*) AS D "
                +"FROM "+ TransactionsContract.TransactionEntry.TABLE_NAME+ " WHERE "
                +where+" GROUP BY "
                +"A, C ORDER BY "
                +TransactionsContract.TransactionEntry.COLUMN_NAME_AMOUNT+" DESC";
        cursor = db.rawQuery(query, null);
        return cursor;

    }
    public static Cursor getTransactionsFromDB(Context context, boolean[] expenseSelection, boolean[] incomeSelection,int selectedTime,FilterMenuAdapter accountMenuAdapter,PersianCalendar filterdate){

        trHelper= TransactoinDatabaseHelper.getInstance(context);
//        trHelper= new TransactoinDatabaseHelper(context);
        db = trHelper.getReadableDatabase(TransactoinDatabaseHelper.DATABASE_ENCRYPT_KEY);
        PersianCalendar filterDate = filterdate;


        where = "(";
        where += "("+ TransactionsContract.TransactionEntry.COLUMN_NAME_IS_EXPENSE + "=" + 1 + ") and (";
        for(int i = 0; i < Category.EXPENSE_SIZE; i++)
        {
            if(expenseSelection[i])
            {
                where += TransactionsContract.TransactionEntry.COLUMN_NAME_CATEGORY + "=" + i + " or ";
            }
        }
        if(where.substring(where.length()-4, where.length()).equals(" or "))
        {
            where = where.substring(0, where.length()-4);
            where += ")";
        }
        else
        {
            where += "1=0)";
        }

        where += " or ("+ TransactionsContract.TransactionEntry.COLUMN_NAME_IS_EXPENSE + "=" + 0 + ") and (";
        for(int i = 0; i < Category.INCOME_SIZE; i++)
        {
            if(incomeSelection[i])
            {
                where += TransactionsContract.TransactionEntry.COLUMN_NAME_CATEGORY + "=" + i + " or ";
            }
        }
        if(where.substring(where.length()-4, where.length()).equals(" or "))
        {
            where = where.substring(0, where.length()-4);
            where += ")";
        }
        else
        {
            where += "1=0)";
        }

        where += ")";

        String startDate = "";
        String endDate = "";
        //TODO set cal to a custom date
//			filterDate = new PersianCalendar();
        PersianDate date = null;
        Time time = null;
        int tmp;
        switch(selectedTime)
        {
            case TimelineActivity.DAILY:
                date = new PersianDate((short)filterDate.get(PersianCalendar.DAY_OF_MONTH),
                        (short)filterDate.get(PersianCalendar.MONTH),
                        (short)filterDate.get(PersianCalendar.YEAR), "");
                time = new Time((short)0, (short)0);
                startDate = date.getSTDString()+time.getSTDString();
                time = new Time((short)99, (short)99);
                endDate = date.getSTDString()+time.getSTDString();
                break;
            case TimelineActivity.WEEKLY:
                int amount = -filterDate.get(PersianCalendar.DAY_OF_WEEK);
                if(amount == -7)
                    amount = 0;

                filterDate.add(PersianCalendar.DATE, amount);
                date = new PersianDate((short)filterDate.get(PersianCalendar.DAY_OF_MONTH),
                        (short)filterDate.get(PersianCalendar.MONTH),
                        (short)filterDate.get(PersianCalendar.YEAR), "");
                time = new Time((short)0, (short)0);
                startDate = date.getSTDString()+time.getSTDString();

                filterDate.add(PersianCalendar.DATE, +6);
                date = new PersianDate((short)filterDate.get(PersianCalendar.DAY_OF_MONTH),
                        (short)filterDate.get(PersianCalendar.MONTH),
                        (short)filterDate.get(PersianCalendar.YEAR), "");
                time = new Time((short)99, (short)99);
                endDate = date.getSTDString()+time.getSTDString();
                filterDate.add(PersianCalendar.DATE, -6-amount);
                break;
            case TimelineActivity.MONTHLY:
                tmp = filterDate.get(PersianCalendar.DAY_OF_MONTH);
                filterDate.set(PersianCalendar.DAY_OF_MONTH, 1);
                date = new PersianDate((short)filterDate.get(PersianCalendar.DAY_OF_MONTH),
                        (short)filterDate.get(PersianCalendar.MONTH),
                        (short)filterDate.get(PersianCalendar.YEAR), "");
                time = new Time((short)0, (short)0);
                startDate = date.getSTDString()+time.getSTDString();

                filterDate.set(PersianCalendar.DAY_OF_MONTH,
                        PersianCalendar.jalaliDaysInMonth[filterDate.get(PersianCalendar.MONTH)]);
                date = new PersianDate((short)filterDate.get(PersianCalendar.DAY_OF_MONTH),
                        (short)filterDate.get(PersianCalendar.MONTH),
                        (short)filterDate.get(PersianCalendar.YEAR), "");
                time = new Time((short)99, (short)99);
                endDate = date.getSTDString()+time.getSTDString();
                filterDate.set(PersianCalendar.DAY_OF_MONTH, tmp);
                break;
            case TimelineActivity.YEARLY:
                tmp = filterDate.get(PersianCalendar.DAY_OF_YEAR);
                filterDate.set(PersianCalendar.DAY_OF_YEAR, 1);
                date = new PersianDate((short)filterDate.get(PersianCalendar.DAY_OF_MONTH),
                        (short)filterDate.get(PersianCalendar.MONTH),
                        (short)filterDate.get(PersianCalendar.YEAR), "");
                time = new Time((short)0, (short)0);
                startDate = date.getSTDString()+time.getSTDString();

                filterDate.set(PersianCalendar.DAY_OF_YEAR,
                        PersianCalendar.isLeapYear(filterDate.get(PersianCalendar.YEAR))? 366: 365);
                date = new PersianDate((short)filterDate.get(PersianCalendar.DAY_OF_MONTH),
                        (short)filterDate.get(PersianCalendar.MONTH),
                        (short)filterDate.get(PersianCalendar.YEAR), "");
                time = new Time((short)99, (short)99);
                endDate = date.getSTDString()+time.getSTDString();
                filterDate.set(PersianCalendar.DAY_OF_YEAR, tmp);
                break;
        }


        where += " and "+ TransactionsContract.TransactionEntry.COLUMN_NAME_DATE_TIME+">="+startDate;
        where += " and "+ TransactionsContract.TransactionEntry.COLUMN_NAME_DATE_TIME+"<="+endDate;


        ArrayList<String> ar=new ArrayList<String>();
        for (int i=0;i<accountMenuAdapter.getAccountsSelection().length;i++){
            if(accountMenuAdapter.getAccountsSelection()[i])
                ar.add(accountMenuAdapter.getSelectedAccountString(i));
        }

//        System.arraycopy(accountMenuAdapter.getAccountsSelection(),0,accountSelection,0,accountMenuAdapter.getAccountsSelection().length);
//        accountSelection=accountMenuAdapter.getAccountsSelection();

        where += " and (";
        for(int i = 0; i < ar.size(); i++)
        {
            where += "( "+ TransactionsContract.TransactionEntry.COLUMN_NAME_ACCOUNT_NAME+ " = '" + ar.get(i) +"' ) "+ " or ";
        }
        if(where.substring(where.length()-4, where.length()).equals(" or "))
        {
            where = where.substring(0, where.length()-4);
            where += ")";
        }
        else
        {
            where += "1=0)";
        }



        String query = "SELECT " + TransactionsContract.TransactionEntry.TABLE_NAME+"."+ TransactionsContract.TransactionEntry._ID+
                " , "+ TransactionsContract.TransactionEntry.TABLE_NAME+"."+ TransactionsContract.TransactionEntry.COLUMN_NAME_TRANSACTION_ID+
                " , "+ TransactionsContract.TransactionEntry.COLUMN_NAME_DATE_TIME+
                " , "+ TransactionsContract.TransactionEntry.COLUMN_NAME_AMOUNT+
                " , "+ TransactionsContract.TransactionEntry.COLUMN_NAME_IS_EXPENSE+
                " , "+ TransactionsContract.TransactionEntry.COLUMN_NAME_DESCRIPTION+
                " , "+ TransactionsContract.TransactionEntry.COLUMN_NAME_CATEGORY+
                " , "+ TransactionsContract.TransactionEntry.COLUMN_NAME_ACCOUNT_NAME+
                " , "+ TransactionsContract.TagsEntry.COLUMN_NAME_TAG+
                " FROM "+ TransactionsContract.TransactionEntry.TABLE_NAME+
                " LEFT JOIN "+ TransactionsContract.TagsEntry.TABLE_NAME+
                " ON "+TransactionsContract.TransactionEntry.TABLE_NAME+"."+ TransactionsContract.TransactionEntry.COLUMN_NAME_TRANSACTION_ID+
                "="+ TransactionsContract.TagsEntry.TABLE_NAME+"."+
                TransactionsContract.TagsEntry.COLUMN_NAME_TRANSACTION_ID+
                " WHERE "+where+
                " ORDER BY "+ TransactionsContract.TransactionEntry.COLUMN_NAME_DATE_TIME+" DESC";
        cursor = db.rawQuery(query, null);
//        db.close();
        return cursor;
    }
    public static Cursor getTotalExpense(Context context){
//        trHelper= new TransactoinDatabaseHelper(context);
        trHelper= TransactoinDatabaseHelper.getInstance(context);
        db = trHelper.getReadableDatabase(TransactoinDatabaseHelper.DATABASE_ENCRYPT_KEY);
        String colName = "totalExpense";
        String query = "SELECT SUM("+ TransactionsContract.TransactionEntry.COLUMN_NAME_AMOUNT+") AS "+colName+
                " FROM "+ TransactionsContract.TransactionEntry.TABLE_NAME+
                " WHERE "+ TransactionsContract.TransactionEntry.COLUMN_NAME_IS_EXPENSE+"=1 and " + where;
        cursor = db.rawQuery(query, null);
//        db.close();
        return cursor;
    }
    public static Cursor getTotalIncome(Context context){
        trHelper= TransactoinDatabaseHelper.getInstance(context);
        db = trHelper.getReadableDatabase(TransactoinDatabaseHelper.DATABASE_ENCRYPT_KEY);
        String colName = "totalIncome";
        String query = "SELECT SUM("+TransactionsContract.TransactionEntry.COLUMN_NAME_AMOUNT+") AS "+colName+
                " FROM "+TransactionsContract.TransactionEntry.TABLE_NAME+
                " WHERE "+TransactionsContract.TransactionEntry.COLUMN_NAME_IS_EXPENSE+"=0 and" + where;
        cursor = db.rawQuery(query, null);
        return cursor;
    }
    public static Cursor getTransactionFromID(String id){
        String query = "SELECT *" +
                " FROM "+ TransactionsContract.TransactionEntry.TABLE_NAME +
                " WHERE "+ TransactionsContract.TransactionEntry.COLUMN_NAME_TRANSACTION_ID + " ='" + id+"'";
        cursor = db.rawQuery(query, null);
//        db.close();
        return cursor;
    }
    public static Cursor getTagsFromID(String id){
        String query = "SELECT "+ TransactionsContract.TagsEntry.COLUMN_NAME_TAG +
                " FROM " + TransactionsContract.TagsEntry.TABLE_NAME +
                " WHERE "+ TransactionsContract.TagsEntry.COLUMN_NAME_TRANSACTION_ID + "='" + id+"'";
        cursor = db.rawQuery(query, null);
//        db.close();
        return cursor;
    }
    public static void editHandyTransaction(Context context, String dateTime, double amountValue, boolean isExpense, String defaultAccount, int category_index, String id, ArrayList<String> selectedTags,String description) {
        trHelper= TransactoinDatabaseHelper.getInstance(context);
        db = trHelper.getWritableDatabase(TransactoinDatabaseHelper.DATABASE_ENCRYPT_KEY);
        String selection = TransactionsContract.TransactionEntry.COLUMN_NAME_TRANSACTION_ID + " LIKE ?";

        String[] selectionArgs = { id };
        ContentValues values = new ContentValues();

        values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_AMOUNT, Currency.allToRial(amountValue));
        values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_IS_EXPENSE, isExpense);
        values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_CATEGORY, category_index);

        if(dateTime!=null)
            values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_DATE_TIME, dateTime);
        if(defaultAccount!=null)
            values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_ACCOUNT_NAME,defaultAccount);
        if(description!=null)
            values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_DESCRIPTION, description);
        db.update(TransactionsContract.TransactionEntry.TABLE_NAME,values,selection,selectionArgs);



        //TODO distinct
        if(selectedTags!=null) {
            String selectionTags = TransactionsContract.TagsEntry.COLUMN_NAME_TRANSACTION_ID + " LIKE ?";
            String[] selectionArgsTags = { id };
            db.delete(TransactionsContract.TagsEntry.TABLE_NAME, selectionTags, selectionArgsTags);

            for (String tag : selectedTags) {
                values = new ContentValues();
                values.put(TransactionsContract.TagsEntry.COLUMN_NAME_TRANSACTION_ID, id);
                values.put(TransactionsContract.TagsEntry.COLUMN_NAME_TAG, tag);
                db.insert(TransactionsContract.TagsEntry.TABLE_NAME, null, values);
            }
        }
//        db.close();
    }
    public static void editUnhandyTransaction(Context context,int category_index,String id,ArrayList<String> selectedTags) {
//        trHelper= new TransactoinDatabaseHelper(context);
        trHelper= TransactoinDatabaseHelper.getInstance(context);
        db = trHelper.getWritableDatabase(TransactoinDatabaseHelper.DATABASE_ENCRYPT_KEY);
        String selection = TransactionsContract.TransactionEntry.COLUMN_NAME_TRANSACTION_ID + " LIKE ?";

        String[] selectionArgs = { id };
        ContentValues values = new ContentValues();
        values.put(TransactionsContract.TransactionEntry.COLUMN_NAME_CATEGORY, category_index);
        db.update(TransactionsContract.TransactionEntry.TABLE_NAME,values,selection,selectionArgs);


        String selectionTags = TransactionsContract.TagsEntry.COLUMN_NAME_TRANSACTION_ID + " LIKE ?";
        String[] selectionArgsTags = { id };
        db.delete(TransactionsContract.TagsEntry.TABLE_NAME, selectionTags, selectionArgsTags);

        //TODO distinct
        if(selectedTags!=null) {
            for (String tag : selectedTags) {
                values = new ContentValues();
                values.put(TransactionsContract.TagsEntry.COLUMN_NAME_TRANSACTION_ID, id);
                values.put(TransactionsContract.TagsEntry.COLUMN_NAME_TAG, tag);
                db.insert(TransactionsContract.TagsEntry.TABLE_NAME, null, values);
            }
        }
//        db.close();
    }
    public static boolean deleteTransactionFromDB(Context context,String[] selectionArgs){
        trHelper= TransactoinDatabaseHelper.getInstance(context);
        db = trHelper.getWritableDatabase(TransactoinDatabaseHelper.DATABASE_ENCRYPT_KEY);
        String selection = TransactionsContract.TransactionEntry.COLUMN_NAME_TRANSACTION_ID + " LIKE ?";
        db.delete(TransactionsContract.TransactionEntry.TABLE_NAME,selection,selectionArgs);
//        db.close();
        return true;
    }

}
