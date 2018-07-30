package ben.medtracker;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ben.medtracker.data.MedicationLogDatabase;
import ben.medtracker.data.MedicationLogEntry;

public class ViewLogEntryActivity extends AppCompatActivity {

    private static final String TAG = ViewLogViewModel.class.getSimpleName();
    private static final String EXTRA_LOG_ID = "extralogid";

    TextView medNameTextView;
    TextView dosesTextView;
    TextView dateTextView;
    TextView timeTextView;
    TextView notesTextView;

    Button deleteButton;
    Button goBackButton;

    MedicationLogDatabase logDb;
    int logId;

    MedicationLogEntry logEntryToDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_log_entry);

        logDb = MedicationLogDatabase.getDatabase(getApplicationContext());

        initializeUI();
    }

    public void initializeUI() {
        medNameTextView = findViewById(R.id.view_entry_med_name_tv);
        dosesTextView = findViewById(R.id.view_entry_doses_tv);
        dateTextView = findViewById(R.id.view_entry_date_tv);
        timeTextView = findViewById(R.id.view_entry_time_tv);
        notesTextView = findViewById(R.id.view_entry_notes_tv);

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(EXTRA_LOG_ID)) {
            logId = intent.getIntExtra(EXTRA_LOG_ID, -1);
            Log.d(TAG, "" + logId);
            ViewLogViewModelFactory factory = new ViewLogViewModelFactory(logDb, logId);
            final ViewLogViewModel logViewModel = ViewModelProviders.of(ViewLogEntryActivity.this, factory)
                    .get(ViewLogViewModel.class);
            logViewModel.getLogEntry().observe(this, new Observer<MedicationLogEntry>() {
                @Override
                public void onChanged(@Nullable MedicationLogEntry logEntry) {
                    logViewModel.getLogEntry().removeObserver(this);
                    medNameTextView.setText(logEntry.getMedicationName());
                    dosesTextView.setText(logEntry.getNumDosesTaken());
                    dateTextView.setText(logEntry.getDateTaken());
                    timeTextView.setText(logEntry.getTimeTaken());
                    notesTextView.setText(logEntry.getNotes());
                    logEntryToDelete = logEntry;
                }
            });
        }

        deleteButton = findViewById(R.id.view_entry_delete_button);
        deleteButton.setOnClickListener(onDeleteButtonClickListener());
        goBackButton = findViewById(R.id.view_entry_go_back_button);
        goBackButton.setOnClickListener(onBackButtonClickListener());
    }

    public View.OnClickListener onDeleteButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewLogEntryActivity.this);
                builder.setMessage(R.string.delete_log_prompt)
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        logDb.logDao().deleteLogEntry(logEntryToDelete);
                                    }
                                });
                                Intent mainIntent = new Intent(ViewLogEntryActivity.this, ViewLogListActivity.class);
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

    public View.OnClickListener onBackButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goBackIntent = new Intent(ViewLogEntryActivity.this,
                        ViewLogListActivity.class);
                startActivity(goBackIntent);
            }
        };
    }

}
