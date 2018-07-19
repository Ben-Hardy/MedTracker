package ben.medtracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import ben.medtracker.data.DateConverter;
import ben.medtracker.data.MedicationLogDatabase;
import ben.medtracker.data.MedicationLogEntry;

public class AddLogEntryActivity extends AppCompatActivity {

    public static final String LOG = AddLogEntryActivity.class.getSimpleName();
    public static final String EXTRA_MED_NAME = "extramedname";
    private static final String DATE_FORMAT = "dd/MM/yyy";

    TextView medicationNameTextView;
    EditText doseAmountTextView;
    EditText notesTextView;

    Button addEntryButton;

    String medicationName;
    Intent intent;

    MedicationLogDatabase logDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_log_entry);

        initializeUI();

        logDb = MedicationLogDatabase.getDatabase(this);
    }

    public void initializeUI() {
        medicationNameTextView = findViewById(R.id.add_entry_med_name_tv);
        doseAmountTextView = findViewById(R.id.add_entry_num_doses_et);
        notesTextView = findViewById(R.id.add_entry_notes_et);

        intent = getIntent();
        medicationName = intent.getStringExtra(EXTRA_MED_NAME);
        medicationNameTextView.setText(medicationName);

        addEntryButton = findViewById(R.id.add_entry_add_entry_button);
        addEntryButton.setOnClickListener(onAddEntryButtonClickedListener());
    }

    public View.OnClickListener onAddEntryButtonClickedListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (doseAmountTextView.getText().toString().isEmpty()) {
                    Toast.makeText(AddLogEntryActivity.this, R.string.add_entry_toast,
                            Toast.LENGTH_SHORT).show();
                } else {
                    String dosage = doseAmountTextView.getText().toString();
                    String notes;
                    if (notesTextView.getText().toString().isEmpty()) {
                        notes = "No notes added for this entry.";
                    } else {
                        notes = notesTextView.getText().toString();
                    }

                    final MedicationLogEntry entry = new MedicationLogEntry(medicationName, dosage, new Date(), notes);

                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            logDb.logDao().insertLogEntry(entry);
                        }
                    });
                    Log.d(LOG, "Added new log entry");
                    onBackPressed();
                }
            }
        };
    }
}
