package com.example.writediary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class NoteDatabase {
    private static final String TAG="NoteDatabase";

    private static NoteDatabase database;

    public static String TABLE_NOTE="NOTE";
    public static int DATABASE_VERSION=1;

    private DatabaseHelper dbhelper;
    private SQLiteDatabase db;
    private Context context;

    public NoteDatabase(Context context) {
        this.context = context;
    }
    public static NoteDatabase getInstance(Context context){
        if(database==null){
            database=new NoteDatabase(context);
        }
        return database;
    }
    public boolean open(){
        dbhelper=new DatabaseHelper(context);
        db=dbhelper.getWritableDatabase();

        return true;
    }
    public void close(){
        db.close();
        database=null;
    }
    public Cursor rawQuery(String sql){
        Cursor cursor=null;
        try{
            cursor=db.rawQuery(sql,null);
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("rawQuery() 에서 예외 발생");
        }
        return cursor;
    }
    public boolean execSQL(String sql){
        try{
            db.execSQL(sql);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private class DatabaseHelper extends SQLiteOpenHelper{
        public DatabaseHelper(@Nullable Context context) {
            super(context, AppConstants.DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase DB){

            try{
                DB.execSQL("drop table if exists "+TABLE_NOTE);
            } catch (Exception e){
                System.out.println("onCreate()에서 droptable이 실패함");
            }
            String CREATE_SQL="create table "+TABLE_NOTE+"( _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, WEATHER TEXT DEFAULT'', " +
                    " ADDRESS TEXT DEFAULT '', " +
                    " LOCATION_X TEXT DEFAULT ''," +
                    " LOCATION_Y TEXT DEFAULT ''," +
                    " CONTENTS TEXT DEFAULT ''," +
                    " TITLE TEXT DEFAULT '', "+
                    " MOOD TEXT, " +
                    " PICTURE TEXT DEFAULT ''," +
                    " CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    " MODIFY_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP )";
            try{
                DB.execSQL(CREATE_SQL);
            }catch (Exception e){
                System.out.println("onCreate()에서 create table이 실패함");
            }

            String CREATE_INDEX_SQL="create index "+TABLE_NOTE+"_IDX ON "+TABLE_NOTE+"(" +
                    "CREATE_DATE" +
                    ")";
            try{
                DB.execSQL(CREATE_INDEX_SQL);
            }catch (Exception e){
                System.out.println("onCreate()에서 create index가 실패함");
            }

        }
        public void onOpen(SQLiteDatabase DB){
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        }
    }

}
