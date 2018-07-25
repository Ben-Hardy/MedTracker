package ben.medtracker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ben.medtracker.data.MedicationEntry;

/*
Adapter class for RecyclerViews since we will be using those to show the medications in the database
 */
public class MedAdapter extends RecyclerView.Adapter<MedAdapter.MedViewHolder> {

    private List<MedicationEntry> mMedEntries;
    private Context context;
    final private ItemClickListener mItemClickListener;

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    public MedAdapter(Context context, ItemClickListener listener) {
        this.context = context;
        mItemClickListener = listener;
    }


    /*
    Inflates the view that the adapter's data will occupy and returns it so the Activity can use it.
     */
    @NonNull
    @Override
    public MedAdapter.MedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.med_rv_layout, parent, false);
        return new MedViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MedAdapter.MedViewHolder holder, int position) {
        MedicationEntry entry = mMedEntries.get(position);
        String name = entry.getMedicationName();
        holder.medNameView.setText(name);
        holder.medWeekFreqView.setText(context.getString(R.string.weekly_schedule) + entry.getWeeklyFrequency());
        holder.medDailyFreqView.setText(context.getString(R.string.num_per_day) + entry.getDailyFrequency());
    }

    @Override
    public int getItemCount() {
        if (mMedEntries == null)
            return 0;
        else return mMedEntries.size();
    }

    /*
    Returns list of medication entries. Used primarily to populate the recyclerview
     */
    public List<MedicationEntry> getMedEntries() {
        return mMedEntries;
    }

    public void setMedEntries(List<MedicationEntry> medEntries) {
        mMedEntries = medEntries;
        notifyDataSetChanged();
    }

    /*
    The ViewHolder for the Adapter. Creates a ViewHolder for each medication and sets an
    OnClickListener for each entry.
     */
    class MedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView medNameView;
        TextView medWeekFreqView;
        TextView medDailyFreqView;

        public MedViewHolder (View medView) {
            super(medView);
            medNameView = medView.findViewById(R.id.medName);
            medWeekFreqView = medView.findViewById(R.id.med_weekly_freq);
            medDailyFreqView = medView.findViewById(R.id.med_view_daily_freq);
            medView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId = mMedEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }

    /*
    This function is used to simplify getting medications from the adapter since the id will not
    necessarily match the index of the adapter the medication is stored in
     */
    public MedicationEntry getEntryById (int id) {
        for (MedicationEntry entry : mMedEntries) {
            if (entry.getId() == id)
                return entry;
        }
        return null;
    }

}
