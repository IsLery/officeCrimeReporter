package com.telran.officecrimereporter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity  extends AppCompatActivity  implements CrimeFragment.Callbacks {
    private static final String EXTRA_CRIME_ID =
            "com.bignerdranch.android.criminalintent.crime_id";
    private ViewPager viewPager;
    private List<Crime> crimes;
    private CrimeFragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);
        UUID firstCrimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        viewPager = findViewById(R.id.crime_view_pager);
        crimes = CrimeLab.get(this).getCrimes();
        Button toFirstBtn = findViewById(R.id.firstBtn);
        Button toLastBtn = findViewById(R.id.lastBtn);

        FragmentManager manager = getSupportFragmentManager();
        viewPager.setAdapter(new FragmentStatePagerAdapter(manager) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                Crime crime = crimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return crimes.size();
            }

            @Override
            public void finishUpdate(@NonNull ViewGroup container) {
                super.finishUpdate(container);
                int pos = viewPager.getCurrentItem();
                if (pos == 0){
                    toFirstBtn.setEnabled(false);
                    toLastBtn.setEnabled(crimes.size()>1);
                }else if (pos == crimes.size()-1){
                    toLastBtn.setEnabled(false);
                    toFirstBtn.setEnabled(crimes.size()>1);
                }else {
                    toFirstBtn.setEnabled(true);
                    toLastBtn.setEnabled(true);
                }

            }
        }) ;

        for (int i = 0; i < crimes.size(); i++) {
            if (crimes.get(i).getId().equals(firstCrimeId)){
                viewPager.setCurrentItem(i);
                break;
            }
        }

        toFirstBtn.setOnClickListener(v ->
        {
            viewPager.setCurrentItem(0);
//            toFirstBtn.setEnabled(false);
        });

        toLastBtn.setOnClickListener(v->
                viewPager.setCurrentItem(crimes.size()-1));
//                toLastBtn.setEnabled(false);

    }

    public static Intent newIntent(Context context, UUID id){
        Intent intent = new Intent(context, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID,id);
        return intent;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("POS", viewPager.getCurrentItem());
        setResult(RESULT_OK,intent);
        super.onBackPressed();


    }

    @Override
    public void onCrimeUpdated(Crime crime) {

    }

    @Override
    public void onCrimeDeleted(Crime crime) {
        finish();
    }
}
