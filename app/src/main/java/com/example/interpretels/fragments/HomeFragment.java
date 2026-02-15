package com.example.interpretels.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.interpretels.R;
import com.example.interpretels.activities.CameraActivity;
import com.google.android.material.button.MaterialButton;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Conectar botón Traducir
        MaterialButton btnTranslate = view.findViewById(R.id.btnTranslate);
        btnTranslate.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CameraActivity.class);
            startActivity(intent);
        });
    }
}