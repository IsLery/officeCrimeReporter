package com.telran.officecrimereporter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.UUID;


public class CrimeActivity extends SingleFragmentActivity {
    private static final String EXTRA_CRIME_ID =
            "com.bignerdranch.android.criminalintent.crime_id";
    CrimeFragment fragment;

    @Override
    public Fragment createFragment() {
        UUID crimeId =  (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        fragment = CrimeFragment.newInstance(crimeId);
        return fragment;
    }

    public static Intent newIntent(Context context, UUID crimeId) {
        Intent intent = new Intent(context, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);

        return intent;

    }


}
