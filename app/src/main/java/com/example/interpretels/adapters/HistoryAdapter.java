package com.example.interpretels.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.interpretels.R;
import com.example.interpretels.models.SignHistory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<SignHistory> historyList;
    private Context context;
    private SimpleDateFormat dateFormat;

    public HistoryAdapter(Context context, List<SignHistory> historyList) {
        this.context = context;
        this.historyList = historyList;
        this.dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        SignHistory history = historyList.get(position);

        // Mostrar nombre de la seña
        holder.tvSignName.setText(history.getSignName());

        // Mostrar contador si es mayor a 1
        if (history.getCount() > 1) {
            holder.tvCount.setText("x" + history.getCount());
            holder.tvCount.setVisibility(View.VISIBLE);
        } else {
            holder.tvCount.setVisibility(View.GONE);
        }

        // Mostrar hora
        String time = dateFormat.format(new Date(history.getTimestamp()));
        holder.tvTime.setText(time);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void updateData(List<SignHistory> newHistory) {
        this.historyList = newHistory;
        notifyDataSetChanged();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvSignName;
        TextView tvCount;
        TextView tvTime;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSignName = itemView.findViewById(R.id.tvSignName);
            tvCount = itemView.findViewById(R.id.tvCount);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}