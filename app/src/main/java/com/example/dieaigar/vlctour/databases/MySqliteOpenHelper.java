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

import com.example.dieaigar.vlctour.POI;
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

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE pois (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL, tipo TEXT NOT NULL, latitud DOUBLE NOT NULL, longitud DOUBLE NOT NULL);");
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
                String aux = items[2];
                aux = aux.substring(6);
                //System.out.println(aux);
                aux = aux.substring(0, aux.length()-1);
                //System.out.println(aux);
                String[] coord = aux.split(" ");

                addPOI(items[0], items[1], Double.parseDouble(coord[0]), Double.parseDouble(coord[1]), db);

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

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  }


    //get scores from database
    public ArrayList<POI> getPOIs() {

        ArrayList<POI> result = new ArrayList<>();
        ArrayList<String> item;
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query("pois", new String[]{"nombre", "tipo", "latitud", "longitud"}, null, null, null, null, null);

        while (cursor.moveToNext()) {

            POI p = new POI(0,cursor.getString(0),cursor.getString(1),cursor.getDouble(2), cursor.getDouble(3));
            result.add(p);
        }

        cursor.close();
        database.close();
        return result;
    }

    public void addPOI(String nombre, String tipo, Double latitud, Double longitud) {

        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("tipo", tipo);
        values.put("latitud", latitud);
        values.put("longitud", longitud);
        database.insert("pois", null, values);
        database.close();
    }

    //adding a score to the database
    public void addPOI(String nombre, String tipo, Double latitud, Double longitud, SQLiteDatabase db) {

        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("tipo", tipo);
        values.put("latitud", latitud);
        values.put("longitud", longitud);
        db.insert("pois", null, values);
    }

    public void deleteAllScores() {

        SQLiteDatabase database = getWritableDatabase();
        database.delete("pois", null, null);
        database.close();
    }
}
