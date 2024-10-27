package com.example.lab5_20180951;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "calorias.db";
    private static final String TABLE_NAME = "comidas";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "NOMBRE";
    private static final String COL_3 = "CALORIAS";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NOMBRE TEXT, CALORIAS INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String nombre, int calorias) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, nombre);
        contentValues.put(COL_3, calorias);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public int getTotalCalorias() {
        int total = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(CALORIAS) FROM comidas", null);
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }


    public void resetearCalorias() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM comidas"); // Borra todas las entradas de la tabla de comidas
    }

    public void restarCalorias(int calorias) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO comidas (NOMBRE, CALORIAS) VALUES ('Actividad Física', -" + calorias + ")");
    }

}

