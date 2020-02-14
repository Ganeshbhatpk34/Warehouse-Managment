package com.probatus.rhbus.warehouse;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SQLitedbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "local.db";
    public static final String DATABASE_PATH = "/data/data/com.sv.rhbus.production/databases/";

    Context cont;

    private SQLitedbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        cont = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {;
        executeSQLScript(db, "createsql.sql");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    private static SQLitedbHelper sInstance = null;

    public static synchronized SQLitedbHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SQLitedbHelper(context);
        }
        return sInstance;
    }

    private void executeSQLScript(SQLiteDatabase database, String filename) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte buf[] = new byte[1024];
        int len;
        AssetManager assetManager = cont.getAssets();
        InputStream inputStream = null;

        try {
            inputStream = assetManager.open(filename);
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();

            String[] createScript = outputStream.toString().split("CREATE.TRIGGER.;.END.;\n|;\n");
            for (int i = 0; i < createScript.length; i++) {
                Log.i("SQL", "" + createScript[i]);
                String sqlStatement = createScript[i].trim();
                if (sqlStatement.length() > 0) {
                    database.execSQL(sqlStatement + ";");
                }
            }
        } catch (IOException e) {

        } catch (SQLException e) {

        }

    }

    public boolean checkDatabase() {
        //SQLiteDatabase db=this.getReadableDatabase();
        //String pathName=db.getPath();
        File database = cont.getDatabasePath(DATABASE_NAME);
        if (database.exists()) {
            Cursor c=checkTable("DummyTable");
            if(c!=null) return true;
            else{ cont.deleteDatabase(DATABASE_NAME);
                return false;}
        } else
            return false;
    }
    public Cursor checkTable(String TABLENAME){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                + TABLENAME + "'", null);
        return cursor;
    }

    public class AeSimpleSHA1 {
        private String convertToHex(byte[] data) {
            StringBuilder buf = new StringBuilder();
            for (byte b : data) {
                int halfbyte = (b >>> 4) & 0x0F;
                int two_halfs = 0;
                do {
                    buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                    halfbyte = b & 0x0F;
                } while (two_halfs++ < 1);
            }
            return buf.toString();
        }

        public String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] textBytes = text.getBytes("iso-8859-1");
            md.update(textBytes, 0, textBytes.length);
            byte[] sha1hash = md.digest();
            return convertToHex(sha1hash);
        }
    }

    public String fetchDateTime() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("select datetime('now', 'localtime')", null);
        cur.moveToFirst();
        return cur.getString(0);
    }

    public String fetchTime() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("select time('now', 'localtime')", null);
        cur.moveToFirst();
        return cur.getString(0);
    }

    public String fetchDate() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("select date('now', 'localtime')", null);
        cur.moveToFirst();
        return cur.getString(0);
    }

    public Cursor fetchUSERInfo() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(String.format("select Usename,SerialKey,Config from dummyTable where ID='1'"), null);
        return cursor;
    }


    public void updateUSERInfo(String user,String SerialKey){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(String.format("UPDATE `dummyTable` set Usename=?,SerialKey= ?,LastModifiedOn= datetime('now', 'localtime') where ID='1'"),new String[]{user, SerialKey});
    }


    @Override
    public void onConfigure(SQLiteDatabase db1) {
        super.onConfigure(db1);
        db1.setForeignKeyConstraintsEnabled(true);
    }
}

