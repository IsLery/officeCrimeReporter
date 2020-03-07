package com.telran.officecrimereporter;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.security.Permission;
import java.security.Permissions;
import java.util.Date;
import java.util.List;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 */
public class CrimeFragment extends Fragment {
    private static final int REQUEST_READ_CONTACTS = 100;
    public static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_ZOOM = "DialogZoom" ;
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_PHOTO = 3;
    public static final int REQUEST_ZOOM = 4;


    private EditText titleTxt;
    private Button dateBtn;
    private Button timeBtn;
    private Button suspectBtn;
    private Button reportBtn;
    private ImageButton photoBtn;
    private ImageView photo;
    private ImageButton callBtn;
    private CheckBox isSolved;
    private CheckBox needCops;

    private Crime crime;
    private UUID crimeId;
    private long suspectId;
    private File photoFile;

    private Callbacks callbacks;





    public CrimeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        crime = CrimeLab.get(requireContext()).getCrime(crimeId);
        photoFile = CrimeLab.get(requireContext()).getPhotoFile(crime);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_crime, container, false);

        titleTxt = view.findViewById(R.id.crime_title);
        titleTxt.setText(crime.getTitle());
        titleTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            crime.setTitle(s.toString());
            updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        dateBtn = view.findViewById(R.id.crime_dateBtn);
        updateDate();
        dateBtn.setOnClickListener(v ->{
            FragmentManager manager = getFragmentManager();
            DatePickerFragment dialog = DatePickerFragment.newInstance(crime.getDate());
            dialog.setTargetFragment(CrimeFragment.this,REQUEST_DATE);
            dialog.show(manager,DIALOG_DATE);
        });
        timeBtn = view.findViewById(R.id.crime_timeBtn);
        timeBtn.setText(crime.getFormattedTime());
        timeBtn.setOnClickListener(v -> {
            TimePickerFragment timeDialog = TimePickerFragment.newInstance(crime.getDate());
            timeDialog.setTargetFragment(CrimeFragment.this,REQUEST_TIME);
            timeDialog.show(getFragmentManager(),DIALOG_TIME);
        });
        isSolved = view.findViewById(R.id.crime_solved);
        isSolved.setChecked(crime.isSolved());
        isSolved.setOnCheckedChangeListener((buttonView, isChecked) -> {
            crime.setSolved(isChecked);
            updateCrime();});
        needCops = view.findViewById(R.id.cops_needed);
        needCops.setChecked(crime.isRequiresPolice());
        needCops.setOnCheckedChangeListener((v,isChecked) -> {
            crime.setRequiresPolice(isChecked);
            updateCrime();});
        suspectBtn = view.findViewById(R.id.crime_suspectBtn);
        if (crime.getSuspect() != null){
            suspectBtn.setText(crime.getSuspect());
        }
        reportBtn = view.findViewById(R.id.crime_reportBtn);
        reportBtn.setOnClickListener(v ->{
            Intent intent = ShareCompat.IntentBuilder.from(requireActivity()).setType("text/plain")
                    .setText(getCrimeReport())
                    .setSubject(getString(R.string.crime_report_subject))
                    .setChooserTitle(getString(R.string.send_report))
                    .createChooserIntent();
//            Intent intent = new Intent(Intent.ACTION_SEND);
//            intent.setType("text/plain");
//            intent.putExtra(Intent.EXTRA_TEXT,getCrimeReport());
//            intent.putExtra(Intent.EXTRA_SUBJECT,R.string.crime_report_subject);
//            intent = Intent.createChooser(intent, getString(R.string.send_report));
            startActivity(intent);
        });
        final Intent pickSuspect = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

    //   pickSuspect.addCategory(Intent.CATEGORY_HOME); - проверка, что фильтр по приложениям с нужным action работает

        PackageManager packageManager = requireContext().getPackageManager();
        if (packageManager.resolveActivity(pickSuspect,PackageManager.MATCH_DEFAULT_ONLY) == null){
            suspectBtn.setEnabled(false);
        }
        suspectBtn.setOnClickListener(v -> {
            startActivityForResult(pickSuspect,REQUEST_CONTACT);

        });
        callBtn = view.findViewById(R.id.call_suspectBtn);
        if (crime.getSuspect() == null){
            callBtn.setVisibility(View.GONE);
        }
            callBtn.setOnClickListener(v->{
               // String phone = suspectPhone.replaceAll("[^0-9]","");
                Intent intPhoneNumber = new Intent(Intent.ACTION_DIAL);
                intPhoneNumber.setData(Uri.parse("tel:"+crime.getSusPhone()));
                startActivity(intPhoneNumber);
            });

        photoBtn = view.findViewById(R.id.photo_btn);
        photo = view.findViewById(R.id.crime_photo);
        updatePhotoView();
        final Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (packageManager.resolveActivity(camIntent,PackageManager.MATCH_DEFAULT_ONLY) == null && photoFile != null){
            photoBtn.setEnabled(false);
        }
        photoBtn.setOnClickListener(v -> {
            Uri uri = FileProvider.getUriForFile(getActivity(),"com.telran.officecrimereporter.fileprovider"
            ,photoFile);

            camIntent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
            List<ResolveInfo> cameraActivities = packageManager.queryIntentActivities(camIntent,PackageManager.MATCH_DEFAULT_ONLY);

            for (ResolveInfo r: cameraActivities) {
                getActivity().grantUriPermission(r.activityInfo.packageName,uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            startActivityForResult(camIntent,REQUEST_PHOTO);
        });
        photo.setOnClickListener(v ->{
            if (!photoFile.exists() || photoFile == null){
                return;
            }
            PhotoZoomFragment fragment = PhotoZoomFragment.newInstance(photoFile.getPath());
            fragment.show(getFragmentManager(),DIALOG_ZOOM);
        });


        return view;
    }

    public static CrimeFragment newInstance(UUID id){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID,id);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public void returnResult(){
        Log.d("MY_TAG", "returnResult: "+crimeId.toString());
        getActivity().setResult(Activity.RESULT_OK,new Intent().putExtra(ARG_CRIME_ID,crimeId));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         if(resultCode == Activity.RESULT_OK){
             switch (requestCode){
                 case REQUEST_DATE:
                     if (data != null) {
                         crime.setDate((Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE));
                         updateDate();
                         updateCrime();
                     }
             break;
                 case REQUEST_TIME:
                     if (data != null) {
                         Date timedate = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_CR_TIME);
                         crime.setDate(timedate);
                         timeBtn.setText(crime.getFormattedTime());
                         updateCrime();
                     }
             break;
                 case  REQUEST_CONTACT:
                     if (data != null) {
                         Uri contactUri = data.getData();
                         String[] queryFields = new String[]{
                                 ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID};
                         Cursor cursor = getActivity().getContentResolver().query(contactUri,
                                 queryFields, null, null, null);
                         try {
                             if (cursor.getCount() == 0) {
                                 return;
                             }
                             cursor.moveToFirst();
                             String suspect = cursor.getString(0);

                             suspectId = cursor.getLong(1);
                             callBtn.setVisibility(View.VISIBLE);
                             crime.setSuspect(suspect);
                             suspectBtn.setText(suspect);
                             getPhone();
                         } finally {
                             cursor.close();
                         }
                         updateCrime();
                     }
                 break;
                 case REQUEST_PHOTO:
                     Uri uri = FileProvider.getUriForFile(getActivity(),"com.telran.officecrimereporter.fileprovider",photoFile);
                     getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                     updatePhotoView();
                     updateCrime();
                     break;
             }
         }


    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_single,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_crime:
                List<Crime> list = CrimeLab.get(requireContext()).getCrimes();
                int pos = list.indexOf(crime);
                CrimeLab.get(requireContext()).deleteCrime(crime);
                if (--pos < 0){
                    callbacks.onCrimeDeleted(null);
                }else {
                    callbacks.onCrimeDeleted(list.get(pos));
                }
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(requireContext()).updateCrime(crime);
    }

    private void updateDate() {
        dateBtn.setText(crime.getFormattedDate("EEE, MMM d, yyyy"));
    }

    private String  getCrimeReport(){
        String solved = null;
        if (crime.isSolved()){
            solved = getString(R.string.crime_report_solved);
        }else {
            solved = getString(R.string.crime_report_unsolved);
        }
        String cops = null;
        if (crime.isRequiresPolice()){
            cops = getString(R.string.crime_report_cops);
        }else {
            cops = getString(R.string.crime_report_no_cops);
        }
        String suspect = null;
        if (crime.getSuspect() == null){
            suspect = getString(R.string.crime_report_no_suspect);
        }else {
            suspect = getString(R.string.crime_report_suspect,crime.getSuspect());
        }
        String report = getString(R.string.crime_report,crime.getTitle(),
                crime.getFormattedDate("EEE, MMM dd"),
                crime.getFormattedTime(),
                solved,
                suspect,
                cops);

        return report;
    }

    public void getPhone(){
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
           requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},REQUEST_READ_CONTACTS);
        }
        Uri phoneURI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = {ContactsContract.CommonDataKinds.Phone._ID,ContactsContract.CommonDataKinds.Phone.NUMBER};
        String selectionClause = ContactsContract.CommonDataKinds.Phone._ID + " = ?";
        String[] selArgs = new String[]{suspectId+""};

        Cursor cursor = getActivity().getContentResolver().query(phoneURI,
                projection,
                selectionClause,
                selArgs,null);
        try {
            if (cursor.getCount() == 0){
                return;
            }
            cursor.moveToFirst();
            crime.setSusPhone(cursor.getString(1));


        }finally {
            cursor.close();
        }
    }

    public void updatePhotoView(){
        if (photoFile == null || !photoFile.exists()){
            photo.setImageDrawable(null);
        }else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(getActivity(),photoFile.getPath());
            photo.setImageBitmap(bitmap);
        }
    }

    public interface Callbacks{
        void onCrimeUpdated(Crime crime);
        void onCrimeDeleted(Crime crime);
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

    private void updateCrime(){
        CrimeLab.get(requireContext()).updateCrime(crime);
        callbacks.onCrimeUpdated(crime);
    }
}
