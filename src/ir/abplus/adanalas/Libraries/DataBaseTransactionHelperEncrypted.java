package ir.abplus.adanalas.Libraries;

import android.content.Context;
import android.util.Log;
import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;

/**
 * Created by Keyvan Sasani on 12/9/2014.
 */
public class DataBaseTransactionHelperEncrypted  {

    public static final String DATABASE_ENCRYPT_KEY = "adanalas-password";
    private static final String DATABASE_NAME = "adanalas_transactions.db";
    Context context;
    private static DataBaseTransactionHelperEncrypted  mInstance = null;

    public DataBaseTransactionHelperEncrypted(Context context) {
//        super();
        this.context = context;
        InitializeSQLCipher(context);
    }


    private void InitializeSQLCipher(Context context) {
        SQLiteDatabase.loadLibs(context);
        File databaseFile = context.getDatabasePath(DATABASE_NAME);
        databaseFile.mkdirs();
        databaseFile.delete();
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, DATABASE_ENCRYPT_KEY, null);
        onCreate(database);
    }

    public void onCreate(SQLiteDatabase db)
    {
        InitializeSQLCipher(context);
        Log.e("db action", "onCreate called");
        db.execSQL(TransactionsContract.SQL_CREATE_TRANSACTIONS);
        db.execSQL(TransactionsContract.SQL_CREATE_TAGS);
        db.execSQL(TransactionsContract.SQL_CREATE_ACCOUNTS);
        db.execSQL(TransactionsContract.SQL_CREATE_TOKENS);
    }

    public static DataBaseTransactionHelperEncrypted getInstance(Context ctx) {
        /**
         * use the application context as suggested by CommonsWare.
         * this will ensure that you dont accidentally leak an Activitys
         * context (see this article for more information:
         * http://android-developers.blogspot.nl/2009/01/avoiding-memory-leaks.html)
         */
        if (mInstance == null) {
            mInstance = new DataBaseTransactionHelperEncrypted(ctx);
        }
        return mInstance;
    }
}
