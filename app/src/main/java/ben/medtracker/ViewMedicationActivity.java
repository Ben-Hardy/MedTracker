package ben.medtracker;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ben.medtracker.data.MedicationDatabase;
import ben.medtracker.data.MedicationEntry;

import static java.lang.String.format;

public class ViewMedicationActivity extends AppCompatActivity {

    public static final String EXTRA_MED_ID = "extramedid";
    private static final int DEFAULT_MED_ID = -1;
    private static final String INSTANCE_MED_ID = "instancemedid";
    public static final String EXTRA_MED_NAME = "extramedname";

    private static final String TAG = ViewMedicationActivity.class.getSimpleName();

    // UI elements
    Button addLogEntryButton;
    Button updateMedicationButton;
    Button deleteMedicationButton;
    Button goBackButton;

    TextView medNameTextView;
    TextView dailyFreqTextView;
    TextView weeklyFreqTextView;
    TextView docNotesTextView;

    // Member variables
    private int medId;
    private MedicationDatabase medDb;

    /*
    This medication entry is created to simplify deletion should the user decide to do so.
    It requires a tiny bit more memory but saves us from having to do a second database access
    just to get one item that will be getting deleted.
    */
    private MedicationEntry entryToDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_medication);

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_MED_ID))
            medId = savedInstanceState.getInt(INSTANCE_MED_ID);

        medDb = MedicationDatabase.getDatabase(getApplicationContext());

        initializeUI();
    }

    /*
    Used for what it says, initializing the UI.
     */
    public void initializeUI() {
        medNameTextView = findViewById(R.id.view_med_name_tv);
        dailyFreqTextView = findViewById(R.id.view_daily_freq_tv);
        weeklyFreqTextView = findViewById(R.id.view_weekly_freq_tv);
        docNotesTextView = findViewById(R.id.view_doc_notes_tv);

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(EXTRA_MED_ID)) {
            medId = intent.getIntExtra(EXTRA_MED_ID, DEFAULT_MED_ID);

            /*
            Since we are using LiveData with this app, we are required to use an observer to\
            fetch the data for the MedicationEntry being viewed. We also will use this access to
            store the entry in case we want to delete it rather than using two LiveData accesses.
            */
            ViewMedicationViewModelFactory factory = new ViewMedicationViewModelFactory(medDb, medId);
            final ViewMedicationViewModel medicationViewModel = ViewModelProviders.of(this, factory)
                    .get(ViewMedicationViewModel.class);
            medicationViewModel.getMedication().observe(this, new Observer<MedicationEntry>() {
                @Override
                public void onChanged(@Nullable MedicationEntry medicationEntry) {
                    medicationViewModel.getMedication().removeObserver(this);
                    medNameTextView.setText(medicationEntry.getMedicationName());

                    StringBuilder sb = new StringBuilder();

                    sb.append("This medication is to be taken ");

                    if (medicationEntry.getDailyFrequency().equals(getString(R.string.as_needed))) {
                        sb.append("as needed.");
                    } else {
                        sb.append(medicationEntry.getDailyFrequency());
                        sb.append(" time(s) per day.");
                    }
                    dailyFreqTextView.setText(sb.toString());

                    sb = new StringBuilder();
                    String weeklyFreqCode = medicationEntry.getWeeklyFrequency();

                    if (weeklyFreqCode.equals(getString(R.string.daily))) {
                        sb.append("This medication is to be taken daily.");
                    } else if (weeklyFreqCode.equals(getString(R.string.as_needed))) {
                        sb.append("This medication is to be taken as needed.");
                    } else {
                        sb.append("This medication is to be taken every ");

                        for (int i = 0; i < weeklyFreqCode.length(); i++) {
                            String day = "";
                            char code = weeklyFreqCode.toCharArray()[i];
                            if (code == 'M')
                                day = "Monday";
                            else if (code == 'T')
                                day = "Tuesday";
                            else if (code == 'W')
                                day = "Wednesday";
                            else if (code == 't')
                                day = "Thursday";
                            else if (code == 'F')
                                day = "Friday";
                            else if (code == 'S')
                                day = "Saturday";
                            else if (code == 's')
                                day = "Sunday";

                            if (i == 0 && weeklyFreqCode.length() == 1) {
                                sb.append(day);
                                sb.append(".");
                            }
                            else if (i == 0)
                                sb.append(day);
                            else if (weeklyFreqCode.length() > 1 && i == weeklyFreqCode.length() - 1) {
                                sb.append(", and ");
                                sb.append(day);
                                sb.append(".");
                            } else {
                                sb.append(", ");
                                sb.append(day);
                            }
                        }
                    }
                    weeklyFreqTextView.setText(sb.toString());
                    docNotesTextView.setText(medicationEntry.getDocNotes());
                    entryToDelete = medicationEntry;
                }
            });
        }
        addLogEntryButton = findViewById(R.id.view_add_log_entry_button);
        addLogEntryButton.setOnClickListener(onAddEntryButtonClickedListener());

        updateMedicationButton = findViewById(R.id.view_update_medication_button);
        updateMedicationButton.setOnClickListener(onUpdateMedicationButtonClickedListener());

        deleteMedicationButton = findViewById(R.id.view_delete_medication_button);
        deleteMedicationButton.setOnClickListener(onDeleteButtonClickedListener());

        goBackButton = findViewById(R.id.view_go_back_button);
        goBackButton.setOnClickListener(onGoBackButtonClickedListener());
    }

    public View.OnClickListener onAddEntryButtonClickedListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addLogIntent = new Intent(ViewMedicationActivity.this, AddLogEntryActivity.class);
                addLogIntent.putExtra(EXTRA_MED_NAME,medNameTextView.getText().toString());
                startActivity(addLogIntent);
            }
        };
    }

    /*
    The Listener for the UpdateMedication Button. Moves the user to the UpdateMedicationActivity.
     */
    public View.OnClickListener onUpdateMedicationButtonClickedListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewMedicationActivity.this, UpdateMedicationActivity.class);
                intent.putExtra(EXTRA_MED_ID, medId);
                startActivity(intent);
            }
        };
    }

    /*
    The Listener for the Delete Button. Confirms with the user that they wish to delete the entry
    and then safely deletes it using a separate thread.
     */
    public View.OnClickListener onDeleteButtonClickedListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewMedicationActivity.this);
                builder.setMessage(R.string.medication_delete_prompt)
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        medDb.medicationDao().deleteMedication(entryToDelete);
                                    }
                                });
                                Intent mainIntent = new Intent(ViewMedicationActivity.this, ViewMedicationListActivity.class);
                                startActivity(mainIntent);
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
        };
    }


    /*
    The Listener for the Go Back Button. Explicitly goes back to the MainActivity so as to avoid
    awkward situations using the system back button stack.
     */
    public View.OnClickListener onGoBackButtonClickedListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(ViewMedicationActivity.this, ViewMedicationListActivity.class);
                startActivity(mainIntent);
            }
        };
    }
}