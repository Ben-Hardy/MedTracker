package ben.medtracker;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ben.medtracker.data.MedicationDatabase;
import ben.medtracker.data.MedicationEntry;

public class UpdateMedicationActivity extends AppCompatActivity {

    // Variables used for logging and activity transitions
    private static final String TAG = UpdateMedicationActivity.class.getSimpleName();
    public static final String EXTRA_MED_ID = "extramedid";
    private static final int DEFAULT_MED_ID = -1;
    private static final String INSTANCE_MED_ID = "instancemedid";

    // UI elements
    TextView medicationNameTextView;
    EditText dailyReqEditText;
    EditText docNotesEditText;

    CheckBox mondayCheckBox;
    CheckBox tuesdayCheckBox;
    CheckBox wednesdayCheckBox;
    CheckBox thursdayCheckBox;
    CheckBox fridayCheckBox;
    CheckBox saturdayCheckBox;
    CheckBox sundayCheckBox;

    Button updateMedicationButton;
    Button cancelUpdateButton;

    Intent intent;

    // member variables that are needed to run the operations on the database
    private MedicationDatabase medDb;
    private int medId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_medication);

        initializeUI();

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_MED_ID))
            medId = savedInstanceState.getInt(INSTANCE_MED_ID);

        medDb = MedicationDatabase.getDatabase(getApplicationContext());
        intent = getIntent();

        // edit texts and checkboxes can only be filled in after the database is retrieved!
        fillInEditTexts();

        cancelUpdateButton.setOnClickListener(onUpdateCancelButtonListener());
        updateMedicationButton.setOnClickListener(onUpdateButtonClickedListener());
    }

    public void initializeUI() {
        medicationNameTextView = findViewById(R.id.update_name_tv);
        dailyReqEditText = findViewById(R.id.update_daily_req_et);
        docNotesEditText = findViewById(R.id.update_doc_notes_et);

        mondayCheckBox = findViewById(R.id.update_monday_cb);
        tuesdayCheckBox = findViewById(R.id.update_tuesday_cb);
        wednesdayCheckBox = findViewById(R.id.update_wednesday_cb);
        thursdayCheckBox = findViewById(R.id.update_thursday_cb);
        fridayCheckBox = findViewById(R.id.update_friday_cb);
        saturdayCheckBox = findViewById(R.id._update_saturday_cb);
        sundayCheckBox = findViewById(R.id.sunday_cb);

        updateMedicationButton = findViewById(R.id.update_med_button);
        cancelUpdateButton = findViewById(R.id.cancel_update_button);
    }

    /*
    Populated the UI elements of the Update Activity with the information previously provided by
    the user
     */
    public void fillInEditTexts() {
        if (intent != null && intent.hasExtra(EXTRA_MED_ID)) {
            medId = intent.getIntExtra(EXTRA_MED_ID, DEFAULT_MED_ID);

            ViewMedicationViewModelFactory factory = new ViewMedicationViewModelFactory(medDb, medId);

            final ViewMedicationViewModel medicationViewModel = ViewModelProviders.of(this, factory)
                    .get(ViewMedicationViewModel.class);

            medicationViewModel.getMedication().observe(this, new Observer<MedicationEntry>() {
                @Override
                public void onChanged(@Nullable MedicationEntry medicationEntry) {
                    medicationViewModel.getMedication().removeObserver(this);

                    medicationNameTextView.setText(medicationEntry.getMedicationName());

                    if (medicationEntry.getDailyFrequency().equals(getString(R.string.as_needed))) {
                        dailyReqEditText.setText("0");
                    } else {
                        dailyReqEditText.setText(medicationEntry.getDailyFrequency());
                    }

                    if (medicationEntry.getWeeklyFrequency().equals(getString(R.string.daily))) {
                        mondayCheckBox.setChecked(true);
                        tuesdayCheckBox.setChecked(true);
                        wednesdayCheckBox.setChecked(true);
                        thursdayCheckBox.setChecked(true);
                        fridayCheckBox.setChecked(true);
                        saturdayCheckBox.setChecked(true);
                        sundayCheckBox.setChecked(true);
                    } else if (medicationEntry.getWeeklyFrequency().equals(getString(R.string.as_needed))) {
                        // This is explicitly here and empty so that when we decode the day codes it won't falsely
                        // check off a day
                    } else {
                        String entryWeeklyFreqString = medicationEntry.getWeeklyFrequency();

                        if (entryWeeklyFreqString.contains("M")) mondayCheckBox.setChecked(true);
                        if (entryWeeklyFreqString.contains("T")) tuesdayCheckBox.setChecked(true);
                        if (entryWeeklyFreqString.contains("W")) wednesdayCheckBox.setChecked(true);
                        if (entryWeeklyFreqString.contains("t")) thursdayCheckBox.setChecked(true);
                        if (entryWeeklyFreqString.contains("F")) fridayCheckBox.setChecked(true);
                        if (entryWeeklyFreqString.contains("S")) saturdayCheckBox.setChecked(true);
                        if (entryWeeklyFreqString.contains("s")) sundayCheckBox.setChecked(true);
                    }

                    docNotesEditText.setText(medicationEntry.getDocNotes());
                }
            });
        }
    }

    /*
    The Listener for the back button. Simply uses the system back button since that is the simplest
    way of going back.
    Returns a listener for the back button
     */
    public View.OnClickListener onUpdateCancelButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        };
    }

    /*
    Listener for the Update Medication button. Confirms that the input is valid,
     then creates the data for a database entry and safely puts it in the database.
     */
    public View.OnClickListener onUpdateButtonClickedListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dailyReqEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter a daily frequency!", Toast.LENGTH_LONG).show();
                } else {
                    int dailyFreqNum = -1;
                    String dailyFreq = "";

                    try {
                        dailyFreqNum = Integer.parseInt(dailyReqEditText.getText().toString());
                    } catch (Exception e) {}

                    // Verifies the daily frequency amount is a positive integer
                    if (dailyFreqNum < 0) {
                        Toast.makeText(getApplicationContext(), "Daily Frequency must be a positive number!", Toast.LENGTH_LONG).show();
                    } else {
                        String medName = medicationNameTextView.getText().toString();
                        String docNotes = docNotesEditText.getText().toString();

                        if (dailyFreqNum == 0) {
                            dailyFreq = getString(R.string.as_needed);
                        } else {
                            dailyFreq = "" + dailyFreqNum;
                        }

                        StringBuilder sb = new StringBuilder();
                        String schedule;

                        // Collects data from the checkboxes and encodes it for the database
                        if (mondayCheckBox.isChecked() && tuesdayCheckBox.isChecked()
                                && wednesdayCheckBox.isChecked() && thursdayCheckBox.isChecked()
                                && fridayCheckBox.isChecked() && saturdayCheckBox.isChecked()
                                && sundayCheckBox.isChecked())
                            schedule = getString(R.string.daily);
                        else {
                            if (mondayCheckBox.isChecked()) sb.append("M");
                            if (tuesdayCheckBox.isChecked()) sb.append("T");
                            if (wednesdayCheckBox.isChecked()) sb.append("W");
                            if (thursdayCheckBox.isChecked()) sb.append("t");
                            if (fridayCheckBox.isChecked()) sb.append("F");
                            if (saturdayCheckBox.isChecked()) sb.append("S");
                            if (sundayCheckBox.isChecked()) sb.append("s");

                            if (sb.toString().isEmpty())
                                schedule = getString(R.string.as_needed);
                            else schedule = sb.toString();
                        }

                        if (docNotes.isEmpty())
                            docNotes = "No notes given.";

                        // have to use the constructor with the id parameter or else the Room update
                        // query will not work correctly
                        final MedicationEntry medicationEntry = new MedicationEntry(medId, medName,
                                dailyFreq, schedule, docNotes);

                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                medDb.medicationDao().updateMedication(medicationEntry);
                                Log.d(TAG, "Medication updated!");
                                finish();
                            }

                        });

                        // Explicitly go back to ViewMedicationActivity since simply going back
                        // can create a navigation loop
                        Intent backToViewActivity = new Intent(UpdateMedicationActivity.this,
                                ViewMedicationActivity.class);
                        backToViewActivity.putExtra(EXTRA_MED_ID, medId);
                        startActivity(backToViewActivity);
                    }
                }
            }
        };

    }
}
