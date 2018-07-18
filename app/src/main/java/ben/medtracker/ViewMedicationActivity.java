package ben.medtracker;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ben.medtracker.data.MedicationDatabase;
import ben.medtracker.data.MedicationEntry;

import static java.lang.String.format;

public class ViewMedicationActivity extends AppCompatActivity {

    public static final String EXTRA_MED_ID = "extramedid";
    private static final int DEFAULT_MED_ID = -1;
    private static final String INSTANCE_MED_ID = "instancemedid";

    private static final String TAG = ViewMedicationActivity.class.getSimpleName();

    Button updateMedicationButton;
    Button goBackButton;

    TextView medNameTextView;
    TextView dailyFreqTextView;
    TextView weeklyFreqTextView;
    TextView docNotesTextView;
    private int medId;

    private MedicationDatabase medDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_medication);

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_MED_ID))
            medId = savedInstanceState.getInt(INSTANCE_MED_ID);

        medDb = MedicationDatabase.getDatabase(getApplicationContext());

        Log.d(TAG, "Successfully retrieved DB");

        medNameTextView = findViewById(R.id.view_med_name_tv);
        dailyFreqTextView = findViewById(R.id.view_daily_freq_tv);
        weeklyFreqTextView = findViewById(R.id.view_weekly_freq_tv);
        docNotesTextView = findViewById(R.id.view_doc_notes_tv);

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(EXTRA_MED_ID)) {
            medId = intent.getIntExtra(EXTRA_MED_ID, DEFAULT_MED_ID);

            AddMedicationViewModelFactory factory = new AddMedicationViewModelFactory(medDb, medId);

            final AddMedicationViewModel medicationViewModel = ViewModelProviders.of(this, factory)
                    .get(AddMedicationViewModel.class);

            medicationViewModel.getMedication().observe(this, new Observer<MedicationEntry>() {
                @Override
                public void onChanged(@Nullable MedicationEntry medicationEntry) {
                    medicationViewModel.getMedication().removeObserver(this);
                    medNameTextView.setText(medicationEntry.getMedicationName());
                    dailyFreqTextView.setText(medicationEntry.getDailyFrequency());
                    weeklyFreqTextView.setText(medicationEntry.getWeeklyFrequency());
                    docNotesTextView.setText(medicationEntry.getDocNotes());
                }
            });
        }

        updateMedicationButton = findViewById(R.id.view_update_medication_button);
        updateMedicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewMedicationActivity.this, UpdateMedicationActivity.class);
                intent.putExtra(EXTRA_MED_ID, medId);
                startActivity(intent);
            }
        });

        goBackButton = findViewById(R.id.view_go_back_button);

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(ViewMedicationActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });

    }
}