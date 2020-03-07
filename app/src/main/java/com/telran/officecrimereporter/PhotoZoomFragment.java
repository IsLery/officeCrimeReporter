package com.telran.officecrimereporter;


import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.io.File;

public class PhotoZoomFragment extends AppCompatDialogFragment {
    private ImageView zoomedPhoto;
    public static final String PHOTO_PATH = "image_path";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        View v = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_photo_zoom,null);
        zoomedPhoto = v.findViewById(R.id.zoomed_photo);
        String path = getArguments().getString(PHOTO_PATH);
        Bitmap bitmap = PictureUtils.getScaledBitmap(requireActivity(),path);
        zoomedPhoto.setImageBitmap(bitmap);
        return new AlertDialog.Builder(requireContext()).setView(v).create();
    }

    public static PhotoZoomFragment newInstance(String path){
        Bundle args = new Bundle();
        args.putString(PHOTO_PATH,path);
        PhotoZoomFragment fragment = new PhotoZoomFragment();
        fragment.setArguments(args);
        return fragment;
    }
}


