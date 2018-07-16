package ben.medtracker;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
    private static final int DEFAULT_DB_ID = -1;

    private static final String TAG = ViewMedicationActivity.class.getSimpleName();

    Button goBackButton;
    TextView medNameTextView;
    TextView dailyFreqTextView;
    TextView weeklyFreqTextView;
    TextView docNotesTextView;

    private MedicationDatabase medDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_medication);

        medDb = MedicationDatabase.getDatabase(getApplicationContext());

        assert medDb != null;

        Log.d(TAG, "Successfully retrieved DB");

        final int id = getIntent().getIntExtra(EXTRA_MED_ID, DEFAULT_DB_ID);


        Log.d(TAG, "ID retrieved was: " + id);
        final LiveData<List<MedicationEntry>> medications = medDb.medicationDao().loadAllMedications();
        assert medications != null;


        medNameTextView = findViewById(R.id.view_med_name_tv);
        dailyFreqTextView = findViewById(R.id.view_daily_freq_tv);
        weeklyFreqTextView = findViewById(R.id.view_weekly_freq_tv);
        docNotesTextView = findViewById(R.id.view_doc_notes_tv);

        medications.observe(this, new Observer<List<MedicationEntry>>() {
            @Override
            public void onChanged(@Nullable List<MedicationEntry> medicationEntries) {
                if (medicationEntries != null || !medicationEntries.isEmpty()) {
                    MedicationEntry medicationEntry = medicationEntries.get(id - 1);
                    medNameTextView.setText(medicationEntry.getMedicationName());
                    dailyFreqTextView.setText(format("%s%d%s", getString(R.string.daily_freq_label),
                            medicationEntry.getDailyFrequency(), getString(R.string.daily_freq_end_label)));
                    weeklyFreqTextView.setText(medicationEntry.getWeeklyFrequency());

                    if (medicationEntry.getDocNotes().isEmpty()) {
                        docNotesTextView.setText(R.string.no_doc_notes_label);
                    } else {
                        docNotesTextView.setText(String.format("%s%s", getString(R.string.doc_notes_label),
                                medicationEntry.getDocNotes()));
                    }
                }
            }
        });


        //if (medicationEntry.getDocNotes().isEmpty()) {
       //     docNotesTextView.setText(R.string.no_doc_notes_label);
       // } else {
      //      docNotesTextView.setText(String.format("%s%s", getString(R.string.doc_notes_label), medicationEntry.getDocNotes()));
       // }



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
