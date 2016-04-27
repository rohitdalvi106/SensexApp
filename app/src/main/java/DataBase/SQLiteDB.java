package DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.rohitdalvi.sensexapp.MainActivity;

import Model.EventStock;


public class SQLiteDB extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "Stock.db";
    public static final String TABLE_NAME = "entry";
    public static final String NAME = "Name";
    public static final String LASTTRADE = "LastTrade";
    public static final String CURRENCY = "Currency";

    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE "+TABLE_NAME+" ("+NAME+" TEXT, "+LASTTRADE+" VARCHAR(255), "+CURRENCY+" TEXT);";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "+TABLE_NAME;

    public SQLiteDB(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_ENTRIES);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);

    }

    public void addstock(EventStock eventStock){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAME, eventStock.getName());
        values.put(CURRENCY, eventStock.getCurrency());
        values.put(LASTTRADE, eventStock.getLastTradePrice());

        db.insert(TABLE_NAME, null, values);
        db.close();

    }

    public String showDATABASE() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String[] columns = {this.NAME, this.LASTTRADE, this.CURRENCY};
        Cursor cursor = sqLiteDatabase.query(this.TABLE_NAME, columns, null, null, null, null, null);

        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {
            //int cid = cursor.getInt(0);
            String name = cursor.getString(0);
            String Ltrade = cursor.getString(1);
            String currency = cursor.getString(2);

            buffer.append(name+ ",     " + Ltrade+ " "+ currency+ "\n");
        }
        return buffer.toString() ;

    }
}
