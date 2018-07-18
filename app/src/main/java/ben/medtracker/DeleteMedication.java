package ben.medtracker;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.List;

import ben.medtracker.data.MedicationDatabase;
import ben.medtracker.data.MedicationEntry;

public class DeleteMedication extends AppCompatActivity implements MedAdapter.ItemClickListener {

    MedAdapter adapter;
    RecyclerView recyclerView;

    MedicationDatabase medDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_medication);

        medDb = MedicationDatabase.getDatabase(getApplicationContext());

        adapter = new MedAdapter(this,this);

        recyclerView = findViewById(R.id.delete_med_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(decoration);

        final MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMedications().observe(this, new Observer<List<MedicationEntry>>() {
            @Override
            public void onChanged(@Nullable List<MedicationEntry> medicationEntries) {
                viewModel.getMedications().removeObserver(this);
                adapter.setMedEntries(medicationEntries);
            }
        });

    }

    @Override
    public void onItemClickListener(final int itemId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.medication_delete_prompt)
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        MedicationEntry entryToDelete = adapter.getEntryById(itemId);
                        medDb.medicationDao().deleteMedication(entryToDelete);
                    }
                });
                onBackPressed();
            }
        })
        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
