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

/*
A RecyclerView adapter for viewing medication log entries. It populates the recyclerview
with entry information like the medication name, the date and time taken, and the dosage amount
 */
public class LogAdapter extends RecyclerView.Adapter<LogAdapter.EntryViewHolder> {

    /*
    required formatting string and class for date formatting
     */
    private static final String DATE_FORMAT = "dd/MM/yyy";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    // our member variables
    private List<MedicationLogEntry> logEntries;
    private Context context;
    final private LogAdapter.ItemClickListener itemClickListener;

    // interface so that onItemClickListener has to be implemented
    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    /*
    Constructor class
     */
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

    // Binds the log entry information to the viewHolder for the recyclerview entry
    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        MedicationLogEntry logEntry = logEntries.get(position);
        holder.medNameTextView.setText(logEntry.getMedicationName());
        holder.dosageTextView.setText(logEntry.getNumDosesTaken());
        holder.logDateTextView.setText(logEntry.getDateTaken());
        holder.logTimeTextView.setText(logEntry.getTimeTaken());
        holder.notesTextView.setText(logEntry.getNotes());
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

    /* use this to update log entries. Have to notify that data set has changed or recyclerview
    will not update
    */
    public void setLogEntries(List<MedicationLogEntry> logEntries) {
        this.logEntries = logEntries;
        notifyDataSetChanged();
    }

    /*
    EntryViewHolder class creates the actual view for showing the data. All fields are initialized
    and the ViewHolder has a click listener added to it.
     */
    public class EntryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView medNameTextView;
        TextView dosageTextView;
        TextView logDateTextView;
        TextView logTimeTextView;
        TextView notesTextView;

        public EntryViewHolder(View view) {
            super(view);
            medNameTextView = view.findViewById(R.id.log_med_name);
            dosageTextView = view.findViewById(R.id.log_dosage);
            logDateTextView = view.findViewById(R.id.log_date);
            logTimeTextView = view.findViewById(R.id.log_time);
            notesTextView = view.findViewById(R.id.log_doc_notes);
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
