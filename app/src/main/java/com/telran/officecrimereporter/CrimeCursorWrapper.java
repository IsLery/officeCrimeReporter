package com.telran.officecrimereporter;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.telran.officecrimereporter.database.CrimeDbSchema;

import java.util.Date;
import java.util.UUID;

public class CrimeCursorWrapper extends CursorWrapper {
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime(){
        String idStr = getString(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.SOLVED));
        int callCops = getInt(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.COPS));
        String suspect = getString(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.SUSPECT));
        String sPhone = getString(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.SUSPECT_PHONE));
        Crime crime = new Crime(UUID.fromString(idStr));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setRequiresPolice(callCops != 0);
        crime.setSuspect(suspect);
        crime.setSusPhone(sPhone);
        return crime;
    }
}
