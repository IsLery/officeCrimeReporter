package com.telran.officecrimereporter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.telran.officecrimereporter.database.CrimeBaseHelper;
import com.telran.officecrimereporter.database.CrimeDbSchema;
import com.telran.officecrimereporter.database.CrimeDbSchema.CrimeTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab crimeLab;

    private Context context;
    private SQLiteDatabase database;


    public static CrimeLab get(Context context){ ;
        if (crimeLab == null){
            crimeLab = new CrimeLab(context);
        }
        return crimeLab;
    }


    private CrimeLab(Context context){
        this.context = context.getApplicationContext();
        database = new CrimeBaseHelper(context)
                .getWritableDatabase();


    }

    public File getPhotoFile(Crime crime){
        File filesDir = context.getFilesDir();
        return new File(filesDir, crime.getPhotoFileName());

    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs){
        Cursor cursor = database.query(CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null);
        return new CrimeCursorWrapper(cursor);
    }

    public Crime getCrime(UUID id){
        CrimeCursorWrapper cursor = queryCrimes(CrimeTable.Cols.UUID + " = ?",
                new String[]{id.toString()});
        try {
            if (cursor.getCount() == 0){
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        }finally {
            cursor.close();
        }
    }

    public List<Crime> getCrimes(){
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null,null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return crimes;
    }

    public void addCrime(Crime c){
        database.insert(CrimeTable.NAME,null,getContentValues(c));
    }

    public void updateCrime(Crime c){
        ContentValues values = getContentValues(c);
        database.update(CrimeTable.NAME,values,CrimeTable.Cols.UUID + " = ?",
                new String[]{c.getId().toString()});
    }
    public void deleteCrime(Crime c){
        database.delete(CrimeTable.NAME,CrimeTable.Cols.UUID + " = ?",
                new String[]{c.getId().toString()});
    }

    public void deleteCrime(UUID id){
        database.delete(CrimeTable.NAME,CrimeTable.Cols.UUID + " = ?",
                new String[]{id.toString()});
    }


    private static ContentValues getContentValues(Crime c){
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID,c.getId().toString());
        values.put(CrimeTable.Cols.TITLE,c.getTitle());
        values.put(CrimeTable.Cols.DATE,c.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED,c.isSolved()? 1 : 0);
        values.put(CrimeTable.Cols.COPS,c.isRequiresPolice()? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT,c.getSuspect());
        values.put(CrimeTable.Cols.SUSPECT_PHONE,c.getSusPhone());
        return values;
    }
}
