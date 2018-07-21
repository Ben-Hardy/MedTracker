package ben.medtracker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ben.medtracker.data.MedicationLogEntry;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.EntryViewHolder> {

    private static final String DATE_FORMAT = "dd/MM/yyy";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    private List<MedicationLogEntry> logEntries;
    private Context context;
    final private LogAdapter.ItemClickListener itemClickListener;

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    public LogAdapter(Context context, ItemClickListener listener) {
        this.context = context;
        itemClickListener = listener;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.entry_rv_layout, parent,
                false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        MedicationLogEntry logEntry = logEntries.get(position);
        holder.medNameTextView.setText(logEntry.getMedicationName());
        holder.logDateTextView.setText(logEntry.getDateTaken());
        holder.logTimeTextView.setText(logEntry.getTimeTaken());
    }

    @Override
    public int getItemCount() {
        if (logEntries == null)
            return 0;
        else return logEntries.size();
    }

    public List<MedicationLogEntry> getLogEntries() {
        return logEntries;
    }

    public void setLogEntries(List<MedicationLogEntry> logEntries) {
        this.logEntries = logEntries;
        notifyDataSetChanged();
    }

    public class EntryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView medNameTextView;
        TextView logDateTextView;
        TextView logTimeTextView;

        public EntryViewHolder(View view) {
            super(view);
            medNameTextView = view.findViewById(R.id.log_med_name);
            logDateTextView = view.findViewById(R.id.log_date);
            logTimeTextView = view.findViewById(R.id.log_time);
            view.setOnClickListener(this);

        }
        @Override
        public void onClick(View view) {
            int elementId = logEntries.get(getAdapterPosition()).getId();
            itemClickListener.onItemClickListener(elementId);
        }
    }

    public MedicationLogEntry getEntryById (int id) {
        for (MedicationLogEntry entry : logEntries) {
            if (entry.getId() == id)
                return entry;
        }
        return null;
    }
}
