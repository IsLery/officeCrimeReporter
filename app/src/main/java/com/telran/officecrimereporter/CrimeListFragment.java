package com.telran.officecrimereporter;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class CrimeListFragment extends Fragment {
    private RecyclerView crimesResV;
    private CrimeAdapter crimeAdapter;
    private boolean mSubtitleVisible = false;
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    AlertDialog blankListDial;
    private Callbacks callbacks;

    public CrimeListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        blankListDial = new AlertDialog.Builder(requireContext()).setTitle(R.string.empty_dlg_title)
                .setMessage(R.string.empty_dlg_text)
                .setPositiveButton("Add crime", (dialog, which) -> addNewCrime()).create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (savedInstanceState != null){
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        View v = inflater.inflate(R.layout.fragment_crime_list, container, false);
        crimesResV = v.findViewById(R.id.crimesResV);
        crimesResV.setLayoutManager(new LinearLayoutManager(requireContext()));
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0,ItemTouchHelper.START);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.START){
                    CrimeHolder holder = (CrimeHolder)viewHolder;
                    int pos = holder.getLayoutPosition();
                    Crime prevCrime = null;
                    if (crimeAdapter.getItemCount() > 1){
                    pos = pos > 0 ? pos - 1 : pos+1;
                    prevCrime = crimeAdapter.adCrimes.get(pos);
                    }
                    Log.d("MY_TAG", "onSwiped: "+prevCrime +" "+ pos);
                    callbacks.onDeletedFromList(holder.crime,prevCrime);
                    CrimeLab.get(requireContext()).deleteCrime(holder.crime);

                }
            }
        });
        helper.attachToRecyclerView(crimesResV);
        updateUI();


        return v;
    }

    private void emptyListDialog() {
        if (CrimeLab.get(requireContext()).getCrimes().size() < 1){
            blankListDial.show();
        }
    }

    public void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(requireContext());
        List<Crime> crimes = crimeLab.getCrimes();
        emptyListDialog();
        if (crimeAdapter == null) {
            crimeAdapter = new CrimeAdapter(crimes);
            crimesResV.setAdapter(crimeAdapter);
        }
        else {
            crimeAdapter.setAdCrimes(crimes);
            crimeAdapter.notifyDataSetChanged();
    }
        updateSubtitle();
}

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);
        MenuItem subItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible){
            subItem.setTitle(R.string.hide_subtitle);
        }else {
            subItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_crime:
                addNewCrime();
                updateUI();
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    private void addNewCrime() {
        Crime crime = new Crime();
        CrimeLab.get(requireActivity()).addCrime(crime);

        callbacks.onCrimeSelected(crime);

    }

    public void updateSubtitle(){
        int ct = CrimeLab.get(requireContext()).getCrimes().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural,ct,ct);
        if (!mSubtitleVisible) {
            subtitle = null;
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();


    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE,mSubtitleVisible);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       super.onActivityResult(requestCode, resultCode, data);
        Log.d("MY_TAG", "onActivityResult: ");
//        if (requestCode == CrimeHolder.REQUEST_CRIME){
//            Log.d("MY_TAG", "onActivityResult: is null"+ (data==null));
//            int pos = data.getIntExtra("POS",0);
//            crimeAdapter.notifyItemChanged(pos);
//        }

//             UUID id = (UUID)data.getSerializableExtra(CrimeFragment.ARG_CRIME_ID);
//             int pos = crimeAdapter.getPos(id);
//            Log.d("MY_TAG", "onActivityResult: true");
//             crimeAdapter.notifyItemChanged(pos);
//        }
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final int REQUEST_CRIME = 1;

        private TextView crimeTitle;
        private TextView crimeDate;
        private Crime crime;
        private ImageView crimeSolvedImg;


        public CrimeHolder(@NonNull View itemView) {
            super(itemView);
            crimeTitle = itemView.findViewById(R.id.crime_title);
            crimeDate = itemView.findViewById(R.id.crime_date);
            crimeSolvedImg = itemView.findViewById(R.id.crimeSolvedImg);
            itemView.setOnClickListener(this);
        }


        public void bind(Crime crime){
            this.crime = crime;
            crimeTitle.setText(crime.getTitle());

            crimeDate.setText(crime.getFormattedDate("EEE, MMM d, yyyy"));

            crimeSolvedImg.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View v) {

            // в крайм активити созлан статичный метод
         //   Intent intent = CrimeActivity.newIntent(requireContext(),crime.getId());

//            Intent intent = CrimePagerActivity.newIntent(requireContext(),crime.getId());
//            startActivityForResult(intent,REQUEST_CRIME);
            callbacks.onCrimeSelected(crime);
        }


        public UUID getCrimeId(){
            return crime.getId();
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{



        private List<Crime> adCrimes;
        public static final int NEED_POLICE = 100;
        public static final int NO_COPS = 200;

        CrimeAdapter(List<Crime> adCrimes) {
            this.adCrimes = adCrimes;
        }


        @Override
        public int getItemViewType(int position) {
            if (adCrimes.get(position).isRequiresPolice()){
                return NEED_POLICE;
            }else {
                return NO_COPS;
            }
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == NEED_POLICE) {
               view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.crime_row_police, parent, false);
            }else{
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.crime_row,parent,false);
            }
            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            holder.bind(adCrimes.get(position));

        }

        @Override
        public int getItemCount() {
            return CrimeLab.get(requireContext()).getCrimes().size();
        }


        public void setAdCrimes(List<Crime> adCrimes) {
            this.adCrimes = adCrimes;
        }
    }

    public interface Callbacks{
        void onCrimeSelected(Crime crime);
        void onDeletedFromList(Crime crime,Crime prev);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        callbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }
}
