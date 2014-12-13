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
//        database.execSQL("create table t1(a, b)");
//        database.execSQL("insert into t1(a, b) values(?, ?)", new Object[]{"one for the money",
//                "two for the show"});
    }

    public void onCreate(SQLiteDatabase db)
    {
        InitializeSQLCipher(context);
        Log.e("db action", "onCreate called");
        db.execSQL(TransactionsContract.SQL_CREATE_TRANSACTIONS);
        db.execSQL(TransactionsContract.SQL_CREATE_TAGS);
        db.execSQL(TransactionsContract.SQL_CREATE_ACCOUNTS);
    }

}
