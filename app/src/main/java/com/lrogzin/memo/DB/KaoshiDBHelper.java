package com.lrogzin.memo.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 .
 */

public class KaoshiDBHelper extends SQLiteOpenHelper {
    public KaoshiDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql="create table if not exists kaoshi_data(" +
                "kaoshi_id integer primary key autoincrement," +
                "kaoshi_tittle varchar,"+
                "kaoshi_content varchar,"+
                "kaoshi_cj integer,"+
                "note_mark varchar,"+
                "note_owner varchar)";
        sqLiteDatabase.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
