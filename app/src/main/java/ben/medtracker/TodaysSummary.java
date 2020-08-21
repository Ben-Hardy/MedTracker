package ben.medtracker;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ben.medtracker.data.MedicationDatabase;
import ben.medtracker.data.MedicationEntry;
import ben.medtracker.data.MedicationLogDatabase;
import ben.medtracker.data.MedicationLogEntry;

public class TodaysSummary extends AppCompatActivity {

    private static final String TAG = ViewLogListActivity.class.getSimpleName();

    Button goHomeButton;
    TextView titleTextView;
    TextView dateTextView;
    TextView requiredMedicationTextView;
    TextView medicationTakenTextView;

    private static final String DATE_FORMAT = "MM/dd/yyyy";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    String date;

    private MedicationDatabase medDb;
    private MedicationLogDatabase logDb;

    List<MedicationEntry> medicationList;
    List<MedicationLogEntry> logEntries;

    String dayOfTheWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todays_summary);



        Date dateTimeStamp = new Date();
        date = dateFormat.format(dateTimeStamp);

        medDb = MedicationDatabase.getDatabase(getApplicationContext());
        logDb = MedicationLogDatabase.getDatabase(getApplicationContext());

        setDayOfTheWeek();
        initializeUI();
        createSummary();

    }

    private void setDayOfTheWeek() {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.MONDAY: dayOfTheWeek = "M"; break;
            case Calendar.TUESDAY: dayOfTheWeek = "T"; break;
            case Calendar.WEDNESDAY: dayOfTheWeek = "W"; break;
            case Calendar.THURSDAY: dayOfTheWeek = "t"; break;
            case Calendar.FRIDAY: dayOfTheWeek = "F"; break;
            case Calendar.SATURDAY: dayOfTheWeek = "S"; break;
            case Calendar.SUNDAY: dayOfTheWeek = "s"; break;
            }
    }

    private void createSummary() {
        switch (dayOfTheWeek) {
            case "M":
                titleTextView.setText(R.string.summary_monday);
                break;
            case "T":
                titleTextView.setText(R.string.summary_tuesday);
                break;
            case "W":
                titleTextView.setText(R.string.summary_wednesday);
                break;
            case "t":
                titleTextView.setText(R.string.summary_thursday);
                break;
            case "F":
                titleTextView.setText(R.string.summary_friday);
                break;
            case "S":
                titleTextView.setText(R.string.summary_saturday);
                break;
            case "s":
                titleTextView.setText(R.string.summary_sunday);
                break;
        }

        dateTextView.setText(date);

        LiveData<List<MedicationEntry>> medicationLiveData = medDb.medicationDao().loadAllMedications();

        final ArrayList<MedicationTally> requiredMedicationTally = new ArrayList<>();
        final ArrayList<MedicationTally> medicationTakenTally = new ArrayList<>();




        medicationLiveData.observe(TodaysSummary.this, new Observer<List<MedicationEntry>>() {
            @Override
            public void onChanged(@Nullable List<MedicationEntry> medicationEntries) {
                Log.d(TAG, "Number of medications: " + medicationEntries.size());

                for (MedicationEntry entry: medicationEntries) {
                    // skip if the medication is as needed
                    if (entry.getWeeklyFrequency().equals(getString(R.string.as_needed))) {

                    } else if (entry.getWeeklyFrequency().equals(getString(R.string.daily))) {
                        requiredMedicationTally.add(new MedicationTally(entry.getMedicationName(),
                                Integer.parseInt(entry.getDailyFrequency())));
                        Log.d(TAG, String.format("Medication Name: %s Number Times Required: %s", entry.getMedicationName(),
                                entry.getDailyFrequency()));
                        requiredMedicationTextView.setText(String.format("%s%s x %s\n", String.valueOf(requiredMedicationTextView.getText()), entry.getMedicationName(), entry.getDailyFrequency()));
                    } else {
                        if (entry.getWeeklyFrequency().contains(dayOfTheWeek)) {
                            requiredMedicationTally.add(new MedicationTally(entry.getMedicationName(),
                                    Integer.parseInt(entry.getDailyFrequency())));
                            Log.d(TAG, String.format("Medication Name: %s Number Times Required: %s", entry.getMedicationName(),
                                    entry.getDailyFrequency()));
                            requiredMedicationTextView.setText(String.format("%s%s x %s\n", String.valueOf(requiredMedicationTextView.getText()), entry.getMedicationName(), entry.getDailyFrequency()));
                        }
                    }
                }

                LiveData<List<MedicationLogEntry>> logLiveData = logDb.logDao().getEntriesByDate(date);

                assert logLiveData == null;
                logLiveData.observe(TodaysSummary.this, new Observer<List<MedicationLogEntry>>() {
                    @Override
                    public void onChanged(@Nullable List<MedicationLogEntry> medicationLogEntries) {
                        if (medicationLogEntries != null && !medicationLogEntries.isEmpty()) {
                            for (MedicationLogEntry entry: medicationLogEntries) {
                                boolean found = false;
                                for (MedicationTally tally: medicationTakenTally) {
                                    if (tally.getMedicationName().equals(entry.getMedicationName())) {
                                        tally.incrementNumberTaken();
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found) {
                                    medicationTakenTally.add(new MedicationTally(entry.getMedicationName(), 0, 1));
                                }
                            }
                            for (MedicationTally tally: medicationTakenTally) {
                                medicationTakenTextView.setText(String.format("%s%s x %s\n", String.valueOf(medicationTakenTextView.getText()), tally.getMedicationName(), tally.getNumberTaken()));
                            }
                        } else {
                            medicationTakenTextView.setText(R.string.nothing_taken);
                        }

                    }
                });
                Log.d(TAG, String.format("Total Required Tally Size: %d", requiredMedicationTally.size()));


            }
        });

        for (MedicationTally tally: requiredMedicationTally) {
            Log.d(TAG, String.format("Medication Name: %s, Number Times Required: %d, Number Times Taken: %d", tally.getMedicationName(),
                    tally.getNumberRequired(), tally.getNumberTaken()));
        }


    }

    private void initializeUI() {
        titleTextView = findViewById(R.id.summary_title_textview);
        dateTextView = findViewById(R.id.summary_date);
        requiredMedicationTextView = findViewById(R.id.summary_required_medication_tv);
        medicationTakenTextView = findViewById(R.id.summary_medication_taken_list_tv);

        goHomeButton = findViewById(R.id.summary_home_button);
        goHomeButton.setOnClickListener(onHomeButtonClickListener());
    }

    private View.OnClickListener onHomeButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goBackIntent = new Intent(TodaysSummary.this, MainActivity.class);
                startActivity(goBackIntent);
            }
        };
    }



    private class MedicationTally {
        private String medicationName;
        private int numberRequired;
        private int numberTaken;

        public MedicationTally(String medicationName, int numberRequired) {
            this.medicationName = medicationName;
            this.numberRequired = numberRequired;
            numberTaken = 0;
        }

        public MedicationTally(String medicationName, int numberRequired, int numberTaken) {
            this.medicationName = medicationName;
            this.numberRequired = numberRequired;
            this.numberTaken = numberTaken;
        }

        public String getMedicationName() { return medicationName;}
        public int getNumberRequired() { return numberRequired; }
        public int getNumberTaken() { return numberTaken; }

        public void incrementNumberTaken() {
            numberTaken++;
        }

    }

}
