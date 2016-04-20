package com.example.dieaigar.vlctour.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.example.dieaigar.vlctour.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.HashMap;

public class MySqliteOpenHelper extends SQLiteOpenHelper {

    //variables
    private static MySqliteOpenHelper instance;
    Context context;


    public synchronized static MySqliteOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MySqliteOpenHelper(context.getApplicationContext());
        }
        return instance;
    }

    public MySqliteOpenHelper(Context context) {
        super(context, "database_file", null, 1);
        this.context=context;
    }


    //onCreate
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE pois (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL, tipo TEXT NOT NULL, coordenadas TEXT NOT NULL);");
            parsecsv(db);
        }catch(SQLException e){e.printStackTrace();}
    }

    private void parsecsv(SQLiteDatabase db){
        InputStream csvFile = context.getResources().openRawResource(R.raw.pois);
        String line = "";
        String cvsSplitBy = ";";
        BufferedReader br = null;
        try {

            br = new BufferedReader(new InputStreamReader(csvFile));
            while ((line = br.readLine()) != null) {

                String[] items = line.split(cvsSplitBy);
                addPOI(items[0], items[1], items[2], db);

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //onUpgrade, not really needed
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  }

    public ArrayList<ArrayList<String>> getPOIs() {

        ArrayList<ArrayList<String>> result = new ArrayList<>();
        ArrayList<String> item;
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query("pois", new String[]{"nombre", "tipo", "coordenadas"}, null, null, null, null, null);

        while (cursor.moveToNext()) {

            item = new ArrayList<String>();
            item.add(cursor.getString(0));
            item.add(cursor.getString(1));
            item.add(cursor.getString(2));
            result.add(item);
        }

        cursor.close();
        database.close();
        return result;
    }

    public void addPOI(String nombre, String tipo, String coordenadas) {

        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("tipo", tipo);
        values.put("coordenadas", coordenadas);
        database.insert("pois", null, values);
        database.close();
    }

    public void addPOI(String nombre, String tipo, String coordenadas, SQLiteDatabase db) {

        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("tipo", tipo);
        values.put("coordenadas", coordenadas);
        db.insert("pois", null, values);
    }

    public void deleteAllPOI() {
        SQLiteDatabase database = getWritableDatabase();
        database.delete("pois", null, null);
        database.close();
    }
}
