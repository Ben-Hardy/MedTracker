package ben.medtracker;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ben.medtracker.data.DateConverter;
import ben.medtracker.data.MedicationLogDatabase;
import ben.medtracker.data.MedicationLogEntry;

public class AddLogEntryActivity extends AppCompatActivity {

    public static final String LOG = AddLogEntryActivity.class.getSimpleName();
    public static final String EXTRA_MED_NAME = "extramedname";

    /*
    The required date formatting information for formatting both the date and timestamps for
    each entry
     */
    private static final String DATE_FORMAT = "MM/dd/yyyy";
    private static final String TIME_FORMAT = "h:mm:ss";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());

    TextView medicationNameTextView;
    EditText doseAmountTextView;
    EditText notesTextView;

    Button addEntryButton;
    Button goBackButton;

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

    /*
    Initializes the UI and sets all necessary click listeners
     */
    public void initializeUI() {
        medicationNameTextView = findViewById(R.id.add_entry_med_name_tv);
        doseAmountTextView = findViewById(R.id.add_entry_num_doses_et);
        notesTextView = findViewById(R.id.add_entry_notes_et);

        intent = getIntent();
        medicationName = intent.getStringExtra(EXTRA_MED_NAME);
        medicationNameTextView.setText(medicationName);

        addEntryButton = findViewById(R.id.add_entry_add_entry_button);
        addEntryButton.setOnClickListener(onAddEntryButtonClickedListener());

        goBackButton = findViewById(R.id.add_entry_go_back_button);
        goBackButton.setOnClickListener(onGoBackButtonClickedListener());
    }

    /*
    Listener for adding a new entry. Makes sure that the amount taken is filled in.
    It takes the current system time as a long and formats it into usable date and time
    strings.
     */
    public View.OnClickListener onAddEntryButtonClickedListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // make sure there is text for the amount taken
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

                    // Convert current system time to a usable format.
                    Date dateTimeStamp = new Date();
                    String date = dateFormat.format(dateTimeStamp);
                    String time = timeFormat.format(dateTimeStamp);

                    Log.d(LOG, "Entry: name: " + medicationNameTextView.getText().toString()
                    + " dosage: " + dosage + " notes: " + notes + " date: " + date);
                    final MedicationLogEntry entry = new MedicationLogEntry(medicationName,
                            dosage, date, time, notes);

                    // add entry safelt to the database
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            logDb.logDao().insertLogEntry(entry);
                        }
                    });
                    logDb.logDao().loadMedicationEntries().observe(AddLogEntryActivity.this, new Observer<List<MedicationLogEntry>>() {
                        @Override
                        public void onChanged(@Nullable List<MedicationLogEntry> medicationLogEntries) {
                            Log.d(LOG, "Number of log entries: " + medicationLogEntries.size());
                        }
                    });

                    Log.d(LOG, "Added new log entry");
                    onBackPressed();
                }
            }
        };
    }

    /*
    Listener that goes back to the log list activity when activated.
     */
    public View.OnClickListener onGoBackButtonClickedListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backToEntriesViewIntent = new Intent(AddLogEntryActivity.this,
                        ViewLogListActivity.class);
                startActivity(backToEntriesViewIntent);
            }
        };
    }

}
