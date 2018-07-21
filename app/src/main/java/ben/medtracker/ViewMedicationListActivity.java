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
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import java.util.List;

import ben.medtracker.data.MedicationDatabase;
import ben.medtracker.data.MedicationEntry;

public class ViewMedicationListActivity extends AppCompatActivity implements MedAdapter.ItemClickListener {

    // Logging tag
    private static final String TAG = ViewMedicationListActivity.class.getSimpleName();

    private MedicationDatabase medDb;
    private MedAdapter medAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_medication_list);

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

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        recyclerView.getLayoutParams().height = (Integer) (displayMetrics.heightPixels * 70)/100;

        Button addMedicationButton = findViewById(R.id.add_med_button_main);
        addMedicationButton.setOnClickListener(addMedicationButtonClickListener());

        Button goHomeButton = findViewById(R.id.medlist_go_home_button);
        goHomeButton.setOnClickListener(goHomeButtonClickListener());

        MedicationViewModel viewModel = ViewModelProviders.of(this).get(MedicationViewModel.class);
        viewModel.getMedications().observe(this, new Observer<List<MedicationEntry>>() {
            @Override
            public void onChanged(@Nullable List<MedicationEntry> medicationEntries) {
                medAdapter.setMedEntries(medicationEntries);
            }
        });
    }

    public View.OnClickListener goHomeButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goHomeIntent = new Intent(ViewMedicationListActivity.this, MainActivity.class);
                startActivity(goHomeIntent);
            }
        };
    }

    public View.OnClickListener addMedicationButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addMedicationIntent = new Intent(ViewMedicationListActivity.this, AddMedicationActivity.class);
                startActivity(addMedicationIntent);
            }
        };
    }

    @Override
    public void onItemClickListener(int itemClicked) {
        Intent viewMedIntent = new Intent(ViewMedicationListActivity.this, ViewMedicationActivity.class);
        viewMedIntent.putExtra(ViewMedicationActivity.EXTRA_MED_ID, itemClicked);
        startActivity(viewMedIntent);
    }
}