package com.example.interpretels.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.interpretels.R;
import com.example.interpretels.adapters.HistoryAdapter;
import com.example.interpretels.database.AppDatabase;
import com.example.interpretels.models.SignHistory;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class HistorialFragment extends Fragment {

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private TextView tvEmpty;
    private MaterialButton btnClear;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_historial, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewHistorial);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        btnClear = view.findViewById(R.id.btnClearHistory);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HistoryAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Botón para limpiar historial
        btnClear.setOnClickListener(v -> clearHistory());

        // Cargar historial
        loadHistory();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Recargar cuando vuelve a la pantalla
        loadHistory();
    }

    private void loadHistory() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(getContext());
            List<SignHistory> history = db.signHistoryDao().getAllHistory();

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (history.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        btnClear.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        btnClear.setVisibility(View.VISIBLE);
                        adapter.updateData(history);
                    }
                });
            }
        }).start();
    }

    private void clearHistory() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(getContext());
            db.signHistoryDao().deleteAll();

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    loadHistory();
                });
            }
        }).start();
    }
}