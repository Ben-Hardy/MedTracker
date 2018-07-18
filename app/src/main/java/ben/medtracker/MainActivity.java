package ben.medtracker;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.List;

import ben.medtracker.data.MedicationDatabase;
import ben.medtracker.data.MedicationEntry;

public class MainActivity extends AppCompatActivity implements MedAdapter.ItemClickListener {

    // Logging tag
    private static final String TAG = MainActivity.class.getSimpleName();

    private MedicationDatabase medDb;
    private MedAdapter medAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUI();

        medDb = MedicationDatabase.getDatabase(getApplicationContext());
    }

    public void initializeUI() {
        RecyclerView recyclerView = findViewById(R.id.med_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        medAdapter = new MedAdapter(this, this);
        recyclerView.setAdapter(medAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(decoration);

        Button addMedicationButton = findViewById(R.id.add_med_button_main);
        addMedicationButton.setOnClickListener(onAddMedicationButtonClicked());

        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMedications().observe(this, new Observer<List<MedicationEntry>>() {
            @Override
            public void onChanged(@Nullable List<MedicationEntry> medicationEntries) {
                medAdapter.setMedEntries(medicationEntries);
            }
        });
    }

    public View.OnClickListener onAddMedicationButtonClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addMedicationIntent = new Intent(MainActivity.this, AddMedicationActivity.class);
                startActivity(addMedicationIntent);
            }
        };
    }

    @Override
    public void onItemClickListener(int itemClicked) {
        Intent viewMedIntent = new Intent(MainActivity.this, ViewMedicationActivity.class);
        viewMedIntent.putExtra(ViewMedicationActivity.EXTRA_MED_ID, itemClicked);
        startActivity(viewMedIntent);
    }
}
