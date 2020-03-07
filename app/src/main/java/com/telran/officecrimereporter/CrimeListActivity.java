package com.telran.officecrimereporter;

import android.content.Intent;
import android.util.Log;

import androidx.fragment.app.Fragment;

public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks{
    private Crime crimeOnDetail;
    @Override
    public Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId(){
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        } else {
            if (crime != null) {
                crimeOnDetail = crime;
                Fragment newDetail = CrimeFragment.newInstance(crime.getId());
                getSupportFragmentManager().beginTransaction().replace(R.id.detail_fragment_container, newDetail).commit();
            }else {
                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
            }
        }
    }

    @Override
    public void onDeletedFromList(Crime crime, Crime prev) {
//        Log.d("MY_TAG", "onDeletedFromList: " + crime);
//        Log.d("MY_TAG", "onDeletedFromList: " + prev);
//        Log.d("MY_TAG", "onDeletedFromList: " + crimeOnDetail);

        if (crimeOnDetail!= null && crime.getId().equals(crimeOnDetail.getId())){
            onCrimeSelected(prev);
        }
    }


    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment fragment = (CrimeListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        fragment.updateUI();
    }

    @Override
    public void onCrimeDeleted(Crime crime) {
        onCrimeSelected(crime);
        onCrimeUpdated(crime);
    }
}
