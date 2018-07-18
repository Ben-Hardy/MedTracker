package ben.medtracker;

import android.bluetooth.le.AdvertiseData;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.time.Instant;

import ben.medtracker.data.MedicationDatabase;
import ben.medtracker.data.MedicationEntry;

public class AddMedicationActivity extends AppCompatActivity {

    // Variables for inter-Activity communication and logging
    private static final int DEFAULT_ID = -1;
    public static final String SPARE_ID = "extra stuff!";
    public static final String INSTANCE_ID = "Instance!";
    private static final String TAG = AddMedicationActivity.class.getSimpleName();

    // UI Elements
    EditText medNameEditText;
    EditText dailyFrequencyEditText;
    EditText docNotesEditText;

    CheckBox mondayCheckBox;
    CheckBox tuesdayCheckBox;
    CheckBox wednesdayCheckBox;
    CheckBox thursdayCheckBox;
    CheckBox fridayCheckBox;
    CheckBox saturdayCheckBox;
    CheckBox sundayCheckBox;

    Button goHomeButton;
    Button addMedicationButton;

    private MedicationDatabase medDb;

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication);

        initializeUI();

        medDb = MedicationDatabase.getDatabase(getApplicationContext());
    }

    /*
    Initializes UI and sets Listeners
     */
    public void initializeUI() {
        medNameEditText = findViewById(R.id.med_name_et);
        dailyFrequencyEditText = findViewById(R.id.med_daily_req_et);
        docNotesEditText = findViewById(R.id.doc_notes_et);

        mondayCheckBox = findViewById(R.id.monday_cb);
        tuesdayCheckBox = findViewById(R.id.tuesday_cb);
        wednesdayCheckBox = findViewById(R.id.wednesday_cb);
        thursdayCheckBox = findViewById(R.id.thursday_cb);
        fridayCheckBox = findViewById(R.id.friday_cb);
        saturdayCheckBox = findViewById(R.id.saturday_cb);
        sundayCheckBox = findViewById(R.id.sunday_cb);

        addMedicationButton = findViewById(R.id.add_med_button);
        goHomeButton = findViewById(R.id.add_go_home_button);
        addMedicationButton.setOnClickListener(onAddMedicationButtonClicked());
        goHomeButton.setOnClickListener(onGoHomeButtonClicked());
    }

    /*
    Listener for adding a medication. Makes sure the TextFields are correctly filled in, and if they
    are, creates usable data for the database then safely adds it to the database
     */
    public View.OnClickListener onAddMedicationButtonClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (medNameEditText.getText().toString().isEmpty() &&
                        dailyFrequencyEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter a name and daily frequency!", Toast.LENGTH_LONG).show();
                } else if (medNameEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter a name!", Toast.LENGTH_LONG).show();
                } else if (dailyFrequencyEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter a daily frequency!", Toast.LENGTH_LONG).show();
                } else {
                    int dailyFreqNum = -1;
                    String dailyFreq = "";

                    try {
                        dailyFreqNum = Integer.parseInt(dailyFrequencyEditText.getText().toString());
                    } catch (Exception e) {}

                    if (dailyFreqNum < 0) {
                        Toast.makeText(getApplicationContext(), "Daily Frequency must be a positive number!", Toast.LENGTH_LONG).show();
                    } else {
                        String medName = medNameEditText.getText().toString();
                        String docNotes = docNotesEditText.getText().toString();

                        if (dailyFreqNum == 0) {
                            dailyFreq = getString(R.string.as_needed);
                        } else {
                            dailyFreq = "" + dailyFreqNum;
                        }

                        StringBuilder sb = new StringBuilder();
                        String schedule = "";

                        if (mondayCheckBox.isChecked() && tuesdayCheckBox.isChecked()
                                && wednesdayCheckBox.isChecked() && thursdayCheckBox.isChecked()
                                && fridayCheckBox.isChecked() && saturdayCheckBox.isChecked()
                                && sundayCheckBox.isChecked())
                            schedule = "Daily";
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

                        final MedicationEntry medicationEntry = new MedicationEntry(medName, dailyFreq,
                                schedule, docNotes);

                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                medDb.medicationDao().insertMedication(medicationEntry);
                                Log.d(TAG, "Added to DB!");
                                finish();
                            }

                        });

                        Intent backToMainActivity = new Intent(AddMedicationActivity.this, MainActivity.class);
                        startActivity(backToMainActivity);
                    }
                }
            }
        };
    }

    // Explicitly makes the app go back to the MainActivity
    public View.OnClickListener onGoHomeButtonClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnToMainIntent = new Intent(AddMedicationActivity.this, MainActivity.class);
                startActivity(returnToMainIntent);
            }
        };
    }

}
