package id.ac.binus.boarderlognightmonitoring.litehelper;

/**
 * Created by CT on 27-Apr-17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LiteHelper {
    private static final String DATABASE_NAME = "LogDB";
    private static final String TAG = "DBAdapter";
    private static final int DATABASE_VERSION = 1;

    //Database table name
    private static final String TABLE_NAME_BOARDER = "MsActiveBoarder";
    private static final String TABLE_NAME_REASON = "MsReason";
    private static final String TABLE_NAME_ADMIN = "MsAdmin";
    private static final String TABLE_NAME_LOG = "TrBoarderLogNightMonitoring";
    private static final String TABLE_NAME_SYNC = "TrLastSyncDate";

    //Database table column name
    public static final String KEY_BOARDER_REGISTRATIONID = "RegistrationID";
    public static final String KEY_BOARDER_BINUSIANID = "BinusianID";
    public static final String KEY_BOARDER_NIM = "NIM";
    public static final String KEY_BOARDER_BOARDERNAME = "BoarderName";
    public static final String KEY_BOARDER_CARDID = "CardID";
    public static final String KEY_BOARDER_PHOTO = "Photo";
    public static final String KEY_REASON_REASONID = "ReasonID";
    public static final String KEY_REASON_REASONNAME = "ReasonName";
    public static final String KEY_ADMIN_CONFIGID = "ConfigID";
    public static final String KEY_ADMIN_CONFIGNAME = "ConfigName";
    public static final String KEY_ADMIN_VALUE = "Value";
    public static final String KEY_LOG_LOGID = "BoarderLogNightMonitoringID";
    public static final String KEY_LOG_REGISTRATIONID = "RegistrationID";
    public static final String KEY_LOG_CHECKOUTDATE = "CheckOutDate";
    public static final String KEY_LOG_CHECKINDATE = "CheckInDate";
    public static final String KEY_LOG_REASONNAME = "ReasonName";
    public static final String KEY_SYNC_ID = "LastSyncID";
    public static final String KEY_SYNC_LASTSYNCDATE = "LastSyncDate";

    //Create table query
    private static final String TABLE_CREATE_BOARDER =
            "create table "+ TABLE_NAME_BOARDER +" ("+ KEY_BOARDER_REGISTRATIONID +" varchar(50) primary key, "
                    + KEY_BOARDER_BINUSIANID +" varchar(50) not null, "+ KEY_BOARDER_NIM + " varchar(50) not null, "
                    + KEY_BOARDER_BOARDERNAME +" varchar(100) not null, " + KEY_BOARDER_CARDID + " varchar(50) not null, "
                    + KEY_BOARDER_PHOTO+" BLOB);";
    private static final String TABLE_CREATE_REASON =
            "create table "+TABLE_NAME_REASON+" ("+KEY_REASON_REASONID+" int primary key, "
                    + KEY_REASON_REASONNAME+" varchar(100) not null);";
    private static final String TABLE_CREATE_ADMIN =
            "create table "+ TABLE_NAME_ADMIN +" ("+ KEY_ADMIN_CONFIGID +" int primary key, "
                    + KEY_ADMIN_CONFIGNAME +" varchar(50) not null, "+ KEY_ADMIN_VALUE +" varchar(50) not null);";
    private static final String TABLE_CREATE_LOG =
            "create table "+ TABLE_NAME_LOG +" ("+ KEY_LOG_LOGID +" varchar(50) primary key, "
                    + KEY_LOG_REGISTRATIONID +" varchar(50) not null,"+ KEY_LOG_CHECKOUTDATE +" varchar(50), "
                    + KEY_LOG_CHECKINDATE +" varchar(50), "+ KEY_LOG_REASONNAME +" varchar(100) not null);";
    private static final String TABLE_CREATE_SYNC =
            "create table "+ TABLE_NAME_SYNC +" ("+ KEY_SYNC_ID +" int primary key, "
                    + KEY_SYNC_LASTSYNCDATE +" varchar(50) );";

    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    String genID;

    public LiteHelper(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try {
                db.execSQL(TABLE_CREATE_BOARDER);
                db.execSQL(TABLE_CREATE_REASON);
                db.execSQL(TABLE_CREATE_ADMIN);
                db.execSQL(TABLE_CREATE_LOG);
                db.execSQL(TABLE_CREATE_SYNC);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME_REASON);
            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME_ADMIN);
            onCreate(db);
        }
    }


    //---opens the database---
    public LiteHelper open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }


    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }


    //---insert a contact into the database---
    public long insertActiveBoarder(String registrationID, String binusianID, String NIM, String boarderName, String cardID, String photo)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_BOARDER_REGISTRATIONID, registrationID);
        initialValues.put(KEY_BOARDER_BINUSIANID, binusianID);
        initialValues.put(KEY_BOARDER_NIM, NIM);
        initialValues.put(KEY_BOARDER_BOARDERNAME, boarderName);
        initialValues.put(KEY_BOARDER_CARDID, cardID);
        initialValues.put(KEY_BOARDER_PHOTO, photo);
        return db.insertWithOnConflict(TABLE_NAME_BOARDER, null, initialValues, SQLiteDatabase.CONFLICT_REPLACE);
    }
    public long insertReason(Integer reasonID, String reasonName)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_REASON_REASONID, reasonID);
        initialValues.put(KEY_REASON_REASONNAME, reasonName);
        return db.insertWithOnConflict(TABLE_NAME_REASON, null, initialValues, SQLiteDatabase.CONFLICT_REPLACE);
    }
    public long insertAdmin(Integer configID, String configName, String value)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ADMIN_CONFIGID, configID);
        initialValues.put(KEY_ADMIN_CONFIGNAME, configName);
        initialValues.put(KEY_ADMIN_VALUE, value);
        return db.insertWithOnConflict(TABLE_NAME_ADMIN, null, initialValues, SQLiteDatabase.CONFLICT_REPLACE);
    }
    public long insertLogNight(String registrationID, String tapingTime, String reasonName, String role)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date d = new Date();
        genID = sdf.format(d)+"00000000";
        ContentValues initialValue = new ContentValues();
        initialValue.put(KEY_LOG_LOGID, genID);
        initialValue.put(KEY_LOG_REGISTRATIONID, registrationID);
        if(role.equals("IN"))
        {
            initialValue.put(KEY_LOG_CHECKINDATE, tapingTime);
        }
        else if(role.equals("OUT"))
        {
            initialValue.put(KEY_LOG_CHECKOUTDATE, tapingTime);
        }
        initialValue.put(KEY_LOG_REASONNAME, reasonName);
        return db.insert(TABLE_NAME_LOG, null, initialValue);
    }

    public long insertSyncDate(Integer lastSyncID, String lastSyncDate)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_SYNC_ID, lastSyncID);
        initialValues.put(KEY_SYNC_LASTSYNCDATE, lastSyncDate);
        return db.insert(TABLE_NAME_SYNC, null, initialValues);
    }

    //---deletes a particular contact---
    public boolean deleteActiveBoarder(String registrationID)
    {
        return db.delete(TABLE_NAME_BOARDER, KEY_BOARDER_REGISTRATIONID + "='" + registrationID + "'", null) > 0;
    }
    public boolean deleteBoarderLogReport()
    {
        return db.delete(TABLE_NAME_LOG, null, null) > 0;
    }
    public boolean deleteReasonList()
    {
        return db.delete(TABLE_NAME_REASON, null, null) > 0;
    }

    //---retrieves all the contacts---
    public Cursor getAllActiveBoarder()
    {
        return db.query(TABLE_NAME_BOARDER, new String[] {KEY_BOARDER_REGISTRATIONID, KEY_BOARDER_BINUSIANID, KEY_BOARDER_BOARDERNAME},
                null, null, null, null, null);
    }
    public Cursor getAllReason()
    {
        return db.query(TABLE_NAME_REASON, new String[] {KEY_REASON_REASONID, KEY_REASON_REASONNAME},
                null, null, null, null, null);
    }
    public Cursor getAllAdmin()
    {
        return db.query(TABLE_NAME_ADMIN, new String[] {KEY_ADMIN_CONFIGID, KEY_ADMIN_CONFIGNAME, KEY_ADMIN_VALUE},
                null, null, null, null, null);
    }
    public Cursor getAllLog()
    {
        return db.query(TABLE_NAME_LOG, new String[] {KEY_LOG_LOGID, KEY_LOG_REGISTRATIONID, KEY_LOG_CHECKOUTDATE, KEY_LOG_CHECKINDATE, KEY_LOG_REASONNAME},
                null, null, null, null, null);
    }

    //---get array string

    //---retrieves a particular contact---
    public Cursor getActiveBoarder(String NIM) throws SQLException
    {
        Cursor mCursor =db.query(true, TABLE_NAME_BOARDER, new String[] {KEY_BOARDER_REGISTRATIONID,
                        KEY_BOARDER_BINUSIANID, KEY_BOARDER_NIM, KEY_BOARDER_BOARDERNAME, KEY_BOARDER_CARDID, KEY_BOARDER_PHOTO}, KEY_BOARDER_NIM + "='" + NIM + "'", null,
                null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    public Cursor getActiveBoarderByTagId(String tagID) throws SQLException
    {
        Cursor mCursor =db.query(true, TABLE_NAME_BOARDER, new String[] {KEY_BOARDER_REGISTRATIONID,
                        KEY_BOARDER_BINUSIANID, KEY_BOARDER_NIM, KEY_BOARDER_BOARDERNAME, KEY_BOARDER_CARDID, KEY_BOARDER_PHOTO}, KEY_BOARDER_CARDID + "='" + tagID + "'", null,
                null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    public Cursor getActiveBoarderPhotoByNIM(String NIM) throws SQLException
    {
        Cursor mCursor =db.query(true, TABLE_NAME_BOARDER, new String[] {KEY_BOARDER_PHOTO}, KEY_BOARDER_NIM + "='" + NIM + "'", null,
                null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    public Cursor getLogNightByRegistrationID(String RegistrationID) throws SQLException
    {
        Cursor mCursor =db.query(true, TABLE_NAME_LOG, new String[] {KEY_LOG_LOGID, KEY_LOG_CHECKOUTDATE,
                KEY_LOG_CHECKINDATE, KEY_LOG_REASONNAME}, KEY_LOG_REGISTRATIONID + "='" + RegistrationID + "'", null,
                null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    public Cursor getReason(Integer reasonID) throws SQLException
    {
        Cursor mCursor =db.query(true, TABLE_NAME_REASON, new String[] {KEY_REASON_REASONID,
                        KEY_REASON_REASONNAME}, KEY_REASON_REASONID + "=" + reasonID, null,
                null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    public Cursor getAdmin(Integer configID) throws SQLException
    {
        Cursor mCursor =db.query(true, TABLE_NAME_ADMIN, new String[] {KEY_ADMIN_CONFIGID,
                        KEY_ADMIN_CONFIGNAME, KEY_ADMIN_VALUE}, KEY_ADMIN_CONFIGID + "=" + configID, null,
                null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    public Cursor getLastSyncDate(Integer lastSyncID) throws SQLException
    {
        Cursor mCursor =db.query(true, TABLE_NAME_SYNC, new String[] {KEY_SYNC_LASTSYNCDATE},
                KEY_SYNC_ID + "=" + lastSyncID, null,null,null,null,null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---updates a contact---
    public boolean updateActiveBoarder(String registrationID, String binusianID, String NIM, String boarderName, String cardID, String photo)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_BOARDER_BINUSIANID, binusianID);
        args.put(KEY_BOARDER_NIM, NIM);
        args.put(KEY_BOARDER_BOARDERNAME, boarderName);
        args.put(KEY_BOARDER_CARDID, cardID);
        args.put(KEY_BOARDER_PHOTO, photo);
        return db.update(TABLE_NAME_BOARDER, args, KEY_BOARDER_REGISTRATIONID + "='" + registrationID + "'", null) > 0;
    }
    public boolean updateReason(Integer reasonID, String reasonName)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_REASON_REASONNAME, reasonName);
        return db.update(TABLE_NAME_REASON, args, KEY_REASON_REASONID + "=" + reasonID, null) > 0;
    }
    public boolean updateAdmin(Integer configID, String configName, String value)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_ADMIN_CONFIGNAME, configName);
        args.put(KEY_ADMIN_VALUE, value);
        return db.update(TABLE_NAME_ADMIN, args, KEY_ADMIN_CONFIGID + "=" + configID, null) > 0;
    }
    public boolean updateCheckInLogNight(String logNightID, String checkInDate)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_LOG_CHECKINDATE, checkInDate);
        return db.update(TABLE_NAME_LOG, args, KEY_LOG_LOGID + "='" + logNightID + "'", null) > 0;
    }
    public boolean updateLastSyncDate(Integer lastSyncID, String lastSyncDate)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_SYNC_LASTSYNCDATE, lastSyncDate);
        return db.update(TABLE_NAME_SYNC, args, KEY_SYNC_ID + "=" + lastSyncID , null) > 0;
    }
}
