package ir.abplus.adanalas.Libraries;

import android.content.Context;
import android.util.Log;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;

public class TransactoinDatabaseHelper extends SQLiteOpenHelper
{
	private static final int DATABASE_VERSION = 1;
	public static final String DATABASE_ENCRYPT_KEY = "adanalas-password";
	private static final String DATABASE_NAME = "adanalas_transactions.db";
    Context context;
    private static TransactoinDatabaseHelper  mInstance = null;


	private TransactoinDatabaseHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
        SQLiteDatabase.loadLibs(context);
        Log.e("db action","constructor called");
	}

    public static TransactoinDatabaseHelper getInstance(Context ctx) {
        /**
         * use the application context as suggested by CommonsWare.
         * this will ensure that you dont accidentally leak an Activitys
         * context (see this article for more information:
         * http://android-developers.blogspot.nl/2009/01/avoiding-memory-leaks.html)
         */
        if (mInstance == null) {
            mInstance = new TransactoinDatabaseHelper(ctx);
        }
        return mInstance;
    }

    @Override
	public void onCreate(SQLiteDatabase db)
	{
        Log.e("db action","onCreate called");
		db.execSQL(TransactionsContract.SQL_CREATE_TRANSACTIONS);
		db.execSQL(TransactionsContract.SQL_CREATE_TAGS);
        db.execSQL(TransactionsContract.SQL_CREATE_ACCOUNTS);
        db.execSQL(TransactionsContract.SQL_CREATE_TOKENS);
        db.execSQL(TransactionsContract.SQL_CREATE_DELETED_TRANSACTIONS);
        db.execSQL(TransactionsContract.SQL_CREATE_SYNC_LOG_DATA);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL(TransactionsContract.SQL_DELETE_TRANSACTIONS);
		db.execSQL(TransactionsContract.SQL_DELETE_TAGS);
		db.execSQL(TransactionsContract.SQL_DELETE_ACCOUNTS);
        db.execSQL(TransactionsContract.SQL_DELETE_TOKENS);
        db.execSQL(TransactionsContract.SQL_DELETE_DELETED_TRANSACTIONS);
        db.execSQL(TransactionsContract.SQL_DELETE_SYNC_LOG_DATA);
		onCreate(db);
	}
}
