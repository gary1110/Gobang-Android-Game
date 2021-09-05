package com.lrogzin.memo.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lrogzin.memo.Bean.KoahiPaiHangBean;
import com.lrogzin.memo.Bean.NoteBean;

import java.util.ArrayList;
import java.util.List;

/**
 .
 */

public class KaoshiDao {
    Context context;
    noteDBHelper dbHelper;

    public KaoshiDao(Context context) {
        this.context = context;
        dbHelper = new noteDBHelper(context, "kaoshi.db", null, 1);
    }

    public void insertNote(KoahiPaiHangBean bean){

        SQLiteDatabase sqLiteDatabase= dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("kaoshi_tittle",bean.getKaoshi_tittle());
        cv.put("kaoshi_content",bean.getKaoshi_tittle());
        cv.put("kaoshi_cj",bean.getScore());
        cv.put("note_mark",bean.getShijian());
        sqLiteDatabase.insert("kaoshi_data",null,cv);
    }

    public int DeleteNote(int id){
        SQLiteDatabase sqLiteDatabase= dbHelper.getWritableDatabase();
        int ret=0;
        ret=sqLiteDatabase.delete("note_data","note_id=?",new String[]{id + ""});
        return ret;
    }

    public  Cursor getAllData(String note_owner){
        SQLiteDatabase sqLiteDatabase= dbHelper.getWritableDatabase();
        String sql="select * from kaoshi_data where note_owner=?";
        return sqLiteDatabase.rawQuery(sql,new String[]{note_owner});
    }



    public List<KoahiPaiHangBean> queryAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        List<KoahiPaiHangBean> noteList = new ArrayList<>();
        KoahiPaiHangBean note;
        String sql ;
        Cursor cursor = null;
        sql = "select * from kaoshi_data order by kaoshi_cj desc";
        cursor = db.rawQuery(sql,new String[]{});
        while (cursor.moveToNext()) {
            note = new KoahiPaiHangBean();
            note.setKaoshi_tittle(cursor.getString(cursor.getColumnIndex("kaoshi_tittle")));
            note.setShijian(cursor.getString(cursor.getColumnIndex("note_mark")));
            note.setScore(cursor.getString(cursor.getColumnIndex("kaoshi_cj")));
            noteList.add(note);
            }

        if (cursor != null) {
            cursor.close();
        }
        if (db != null) {
            db.close();
        }

        return noteList;
    }

}
