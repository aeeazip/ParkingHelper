package ddwucom.mobile.ma02_20201019;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ParkingDBHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "parking_db";
    public final static String TABLE_NAME = "parking_table";
    public final static String COL_ID = "_id";
    public final static String COL_NAME = "name";
    public final static String COL_ADDRESS = "address";
    public final static String COL_RATING = "rating";
    public final static String COL_IMAGE = "image";
    public final static String COL_MEMO = "memo";

    public ParkingDBHelper(Context context) {
        super (context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL ("create table " + TABLE_NAME + " ( " + COL_ID + " integer primary key autoincrement,"
                + COL_NAME + " TEXT, " + COL_ADDRESS + " TEXT, " + COL_RATING + " TEXT, " + COL_IMAGE + " TEXT, " + COL_MEMO + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + TABLE_NAME);
        onCreate(db);
    }
}
