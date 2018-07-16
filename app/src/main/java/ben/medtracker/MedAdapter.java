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
    }

    @Override
    public int getItemCount() {
        if (mMedEntries == null)
            return 0;
        else return mMedEntries.size();
    }

    public List<MedicationEntry> getMedEntries() {
        return mMedEntries;
    }

    public void setMedEntries(List<MedicationEntry> medEntries) {
        mMedEntries = medEntries;
        notifyDataSetChanged();
    }


    class MedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView medNameView;

        public MedViewHolder (View medView) {
            super(medView);
            medNameView = medView.findViewById(R.id.medName);
            medView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId = mMedEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }
}
