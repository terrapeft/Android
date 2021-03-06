package smf.local.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import smf.local.CustomTypes.ArrayListEx;
import smf.local.CustomTypes.ContactModel;
import smf.local.Files.FileModel;
import smf.local.Messages.MessageModel;
import smf.local.SmsReceiver;
import smf.local.Traces.TraceModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {
    public static SQLiteDatabase myDb;
    private static Context context;

    public static final String DB_NAME = "SmartFilterDB";

    public static final String FILE_TABLE = "filedt";
    public static final String SPAM_TABLE = "spamdt";
    public static final String TRACE_TABLE = "tracedt";

    public static final String CODE_COL = "code";
    public static final String SENDER_COL = "sender";
    public static final String BODY_COL = "body";
    public static final String DATE_COL = "date";
    public static final String DESCR_COL = "description";
    public static final String NAME_COL = "name";
    public static final String BLOCK_FLAG = "block";
    public static final String LOVE_FLAG = "love";
    public static final String SILENT_FLAG = "silent";
    public static final String CONTACT_COL = "isContact";

    public static void clean() {
        DbHelper.context = null;
        myDb = null;
    }

    public static void updateDB() {
        Log.d("spam", "Add new_flag.");
        myDb.execSQL("alter table " + SPAM_TABLE + " add column new_flag int default 1");
    }

    public static void Initialize(Context context) {
        if (myDb != null) return;

        DbHelper.context = context;
        myDb = new DbHelper(context).getWritableDatabase();
        //updateDB();
    }

    DbHelper(Context context) {
        super(context, DB_NAME, null, 2);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + SPAM_TABLE + " (" +
                "id integer primary key autoincrement, " +
                "sender text not null, " +
                "body text not null, " +
                "date text, " +
                "new_flag int default 1);");

        db.execSQL("create table if not exists " + FILE_TABLE + " (" +
                "id integer primary key autoincrement, " +
                BLOCK_FLAG + " int default 0, " +
                LOVE_FLAG + " int default 0, " +
                SILENT_FLAG + " int default 0, " +
                CONTACT_COL + " int, " +
                NAME_COL + " text, " +
                CODE_COL + " text unique collate nocase);");

        db.execSQL("create table if not exists " + TRACE_TABLE + " (" +
                "id integer primary key autoincrement, " +
                DESCR_COL + " text);");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("DbHelper", "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");

        db.execSQL("DROP TABLE IF EXISTS " + SPAM_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + FILE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TRACE_TABLE);

        onCreate(db);
    }

    public static List<FileModel> getFileList() {
        ArrayListEx<FileModel> arr = new ArrayListEx<FileModel>();

        try {
            Cursor cursor = myDb.rawQuery("select id, block, love, silent, isContact, name, code from " + FILE_TABLE + " order by name", null);

            while (cursor.moveToNext()) {
                arr.add(new FileModel(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor.getString(5), cursor.getString(6)));
            }
        } catch (Exception ex) {
            ErrorHelper.ShowError("getFileList() exception (" + getTimeStamp() + "): ", ex);
        }

        return arr;
    }

    public static void updateFileList(ArrayListEx<FileModel> arr) {
        try {
            String sql = "UPDATE OR IGNORE " + FILE_TABLE + " SET block = ?, love = ?, silent = ?, isContact = ?, name = ?, code = ?";
            sql += " where id = ?";

            myDb.beginTransaction();
            SQLiteStatement insert = myDb.compileStatement(sql);

            for (FileModel m : arr) {
                insert.bindLong(1, m.getBlockIt() ? 1 : 0);
                insert.bindLong(2, m.getLoveIt() ? 1 : 0);
                insert.bindLong(3, m.getMuteIt() ? 1 : 0);
                insert.bindLong(4, m.getIsContact() ? 1 : 0);
                insert.bindString(5, m.getName());
                insert.bindString(6, m.getNumber());
                insert.bindLong(7, m.getId());
                insert.execute();
            }
            myDb.setTransactionSuccessful();
        } catch (Exception ex) {
            ErrorHelper.ShowError("setFileList() exception (" + getTimeStamp() + "): ", ex);
        } finally {
            myDb.endTransaction();
        }
    }

    public static void insertFileList(ArrayListEx<ContactModel> arr) {
        try {
            String sql = "INSERT OR IGNORE INTO " + FILE_TABLE + " (isContact, name, code) values (?, ?, ?)";

            myDb.beginTransaction();
            SQLiteStatement insert = myDb.compileStatement(sql);

            for (ContactModel m : arr) {
                insert.bindLong(1, m.isContact() ? 1 : 0);
                insert.bindString(2, m.getName());
                insert.bindString(3, m.getNumber());
                insert.execute();
            }
            myDb.setTransactionSuccessful();
        } catch (Exception ex) {
            ErrorHelper.ShowError("setFileList(first) exception (" + getTimeStamp() + "): ", ex);
        } finally {
            myDb.endTransaction();
        }
    }


    public static List<TraceModel> getTraces() {
        ArrayList<TraceModel> arr = new ArrayList<TraceModel>();

        try {
            Cursor cursor = myDb.rawQuery("select id, description from " + TRACE_TABLE + " order by id desc", null);

            while (cursor.moveToNext()) {
                arr.add(new TraceModel(cursor.getInt(0), cursor.getString(1)));
            }
        } catch (Exception ex) {
            ErrorHelper.ShowError("getTraces() exception (" + getTimeStamp() + "): ", ex);
        }
        return arr;
    }

    public static int getSpamCount() {
        try {
            final String sql = "select count (*) from " + SPAM_TABLE;
            return (int) DatabaseUtils.longForQuery(myDb, sql, null);
        } catch (Exception ex) {
            ErrorHelper.ShowError("getSpamCount() exception (" + getTimeStamp() + "): ", ex);
        }
        return -1;
    }

    public static int getNewSpamCount() {
        try {
            final String sql = "select count (*) from " + SPAM_TABLE + " where new_flag = 1";
            int cnt = (int) DatabaseUtils.longForQuery(myDb, sql, null);
            return cnt < 0 ? 0 : cnt;
        } catch (Exception ex) {
            ErrorHelper.ShowError("getNewSpamCount() exception (" + getTimeStamp() + "): ", ex);
        }
        return -1;
    }

    public static List<MessageModel> getMessages(String sender) {
        ArrayList<MessageModel> arr = new ArrayList<MessageModel>();

        try {
            Cursor cursor = myDb.rawQuery("select id, sender, body, date from " + SPAM_TABLE + " where sender = '" +
                    sender +
                    "' order by id desc", null);

            while (cursor.moveToNext()) {
                arr.add(new MessageModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)));
            }
        } catch (Exception ex) {
            ErrorHelper.ShowError("getMessages() exception (" + getTimeStamp() + "): ", ex);
        }
        return arr;
    }

    public static List<TraceModel> getLogs() {
        ArrayList<TraceModel> arr = new ArrayList<TraceModel>();

        try {
            Cursor cursor = myDb.rawQuery("select id, " + DESCR_COL + " from " + TRACE_TABLE + " order by id desc", null);

            while (cursor.moveToNext()) {
                arr.add(new TraceModel(cursor.getInt(0), cursor.getString(1)));
            }
        } catch (Exception ex) {
            ErrorHelper.ShowError("getLogs() exception (" + getTimeStamp() + "): ", ex);
        }
        return arr;
    }

    public static List<MessageModel> getMessages() {
        ArrayList<MessageModel> arr = new ArrayList<MessageModel>();

        try {
            Cursor cursor = myDb.rawQuery("select id, sender, body, date from " + SPAM_TABLE + " order by id desc", null);

            while (cursor.moveToNext()) {
                arr.add(new MessageModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)));
            }
        } catch (Exception ex) {
            ErrorHelper.ShowError("getMessages() exception (" + getTimeStamp() + "): ", ex);
        }
        return arr;
    }
    
    /*
    // spam methods
    */

    public static void deleteFromBlackList(Integer id) {
        try {
            myDb.delete(FILE_TABLE, "id = ?", new String[]{id.toString()});
        } catch (Exception ex) {
            ErrorHelper.ShowError("deleteFromBlackList() exception (" + getTimeStamp() + "): ", ex);
        }
    }

    public static void upsertInBlackList(String sender, Boolean blockIt) {
        try {
            myDb.execSQL(
                    "insert or replace into " + DbHelper.FILE_TABLE + " (" +
                            BLOCK_FLAG + ", " +
                            LOVE_FLAG + ", " +
                            SILENT_FLAG + ", " +
                            CONTACT_COL + ", " +
                            NAME_COL + ", " +
                            CODE_COL +
                            ") VALUES (" +
                            (blockIt ? 1 : 0) + ", " +
                            "coalesce((select " + LOVE_FLAG + " from " + FILE_TABLE + " where code = '" + sender + "'), 0)" + ", " +
                            "coalesce((select " + SILENT_FLAG + " from " + FILE_TABLE + " where code = '" + sender + "'), 0)" + ", " +
                            "coalesce((select " + CONTACT_COL + " from " + FILE_TABLE + " where code = '" + sender + "'), 0)" + ", " +
                            "coalesce((select " + NAME_COL + " from " + FILE_TABLE + " where code = '" + sender + "'),'')" + ", " +
                            "'" + sender + "')"
            );
        } catch (Exception ex) {
            ErrorHelper.ShowError("upsertInBlackList() exception (" + getTimeStamp() + "): ", ex);
        }
    }


    public static void markMessagesAsRead() {
        try {
            myDb.execSQL("update " + DbHelper.SPAM_TABLE + " set new_flag = 0 where new_flag = 1");
        } catch (Exception ex) {
            ErrorHelper.ShowError("markMessagesAsRead() exception (" + getTimeStamp() + "): ", ex);
        }
    }

    public static void addTrace(String description) {
        if (!SmsReceiver.userPrefs.isDoLogging())
            return;

        //Toast.makeText(context, description, Toast.LENGTH_LONG).show();

        try {
            Log.d("trace", description);

            ContentValues blValues = new ContentValues();
            blValues.put(DbHelper.DESCR_COL, description);

            List<ContentValues> list = new ArrayList<ContentValues>();
            list.add(blValues);

            DbHelper.insertIntoTable(DbHelper.TRACE_TABLE, list);
        } catch (Exception ex) {
            ErrorHelper.ShowError("addTrace() exception (" + getTimeStamp() + "): ", ex);
        }
    }

    public static void deleteSpamMessage(Integer id) {
        try {
            myDb.delete(SPAM_TABLE, "id = ?", new String[]{id.toString()});
        } catch (Exception ex) {
            ErrorHelper.ShowError("deleteSpamMessage() exception (" + getTimeStamp() + "): ", ex);
        }
    }

    public static void deleteSpamMessages(String tableName, String sender) {
        try {
            myDb.delete(tableName, SENDER_COL + " = ?", new String[]{sender});
        } catch (Exception ex) {
            ErrorHelper.ShowError("deleteSpamMessages(tableName, sender) exception (" + getTimeStamp() + "): ", ex);
        }
    }

    /*
    // generic methods
    */

    public static void deleteFromTable(String tableName) {
        try {
            myDb.delete(tableName, null, null);
        } catch (Exception ex) {
            ErrorHelper.ShowError("deleteFromTable(tableName) exception (" + getTimeStamp() + "): ", ex);
        }
    }

    /**
     * @param blTable
     * @param list
     */
    public static void deleteFromTable(String blTable, List<ContentValues> list) {
        try {
            // open the database
            myDb.beginTransaction();

            StringBuilder whereClause = new StringBuilder();

            for (ContentValues v : list) {
                for (java.util.Map.Entry<String, Object> pare : v.valueSet()) {
                    whereClause
                            .append(pare.getKey())
                            .append(" = '")
                            .append(pare.getValue())
                            .append("'");
                }
            }

            for (ContentValues aList : list) {
                myDb.delete(blTable, whereClause.toString(), null);
            }

            myDb.setTransactionSuccessful();
        } catch (Exception ex) {
            ErrorHelper.ShowError("deleteFromTable(blTable, list) exception (" + getTimeStamp() + "): ", ex);
        } finally {
            myDb.endTransaction();
        }

    }

    public static void insertIntoTable(String tableName, List<ContentValues> values) {
        try {
            // open the database
            myDb.beginTransaction();

            for (ContentValues value : values) {
                myDb.insert(tableName, null, value);
            }

            myDb.setTransactionSuccessful();
        } catch (Exception ex) {
            ErrorHelper.ShowError("insertIntoTable() exception (" + getTimeStamp() + "): ", ex);
        } finally {
            myDb.endTransaction();
        }
    }

    public static long getTimeStampMillis() {
        return new Date().getTime();
    }

    public static String getTimeStamp() {
        return getTimeStamp("yyyy-MM-dd HH:mm");
    }

    public static String getTimeStamp(Long milliseconds) {
        return getTimeStamp("yyyy-MM-dd HH:mm", milliseconds);
    }

    public static String getTimeStamp(String format) {
        return new SimpleDateFormat(format).format(new Date());
    }

    public static String getTimeStamp(String format, Long milliseconds) {
        return new SimpleDateFormat(format).format(new Date(milliseconds));
    }

}
