package ben.medtracker;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.List;

import ben.medtracker.data.MedicationLogDatabase;
import ben.medtracker.data.MedicationLogEntry;

public class ViewLogListActivity extends AppCompatActivity implements LogAdapter.ItemClickListener {

    // Logging tag
    private static final String TAG = ViewLogListActivity.class.getSimpleName();
    private static final String EXTRA_LOG_ID = "extralogid";

    private MedicationLogDatabase logDb;
    private LogAdapter logAdapter;

    String[] uniqueMedNames;
    String[] datesTaken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_log_list);

        initializeUI();

        logDb = MedicationLogDatabase.getDatabase(getApplicationContext());
    }
    public void initializeUI() {
        RecyclerView recyclerView = findViewById(R.id.log_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        logAdapter = new LogAdapter(this, this);
        recyclerView.setAdapter(logAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(decoration);



        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        recyclerView.getLayoutParams().height = (Integer) (displayMetrics.heightPixels * 60)/100;

        LogViewModel viewModel = ViewModelProviders.of(this).get(LogViewModel.class);
        viewModel.getLogs().observe(this, new Observer<List<MedicationLogEntry>>() {
            @Override
            public void onChanged(@Nullable List<MedicationLogEntry> medicationLogEntries) {
                logAdapter.setLogEntries(medicationLogEntries);

                // Also will use this opportunity to populate a few filter options so we can save
                // having to do thread creations for database accesses
                // we have these arrays populated here because they will also get updated if the
                // data changes.
                LiveData<List<String>> medNames = logDb.logDao().getEnteredMedicationNames();
                medNames.observe(ViewLogListActivity.this, new Observer<List<String>>() {
                    @Override
                    public void onChanged(@Nullable List<String> strings) {
                        if (strings.size() > 0) {
                            uniqueMedNames = new String[strings.size()];
                            for (int i = 0; i < strings.size();i++)
                                uniqueMedNames[i] = strings.get(i);
                        }
                    }
                });

                LiveData<List<String>> datesTakenLiveData = logDb.logDao().getUniqueLogDates();
                datesTakenLiveData.observe(ViewLogListActivity.this, new Observer<List<String>>() {
                    @Override
                    public void onChanged(@Nullable List<String> strings) {
                        if (strings.size() > 0) {
                            datesTaken = new String[strings.size()];
                            for (int i = 0; i < strings.size(); i++) {
                                datesTaken[i] = strings.get(i);
                            }
                        }
                    }
                });
            }
        });

        Button goHomeButton = findViewById(R.id.entrylist_go_home_button);
        goHomeButton.setOnClickListener(goHomeButtonClickListener());

        Button filterButton = findViewById(R.id.entrylist_filter_button);
        filterButton.setOnClickListener(filterButtonClickListener());

    }

    public View.OnClickListener goHomeButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goHomeIntent = new Intent(ViewLogListActivity.this, MainActivity.class);
                startActivity(goHomeIntent);
            }
        };
    }

    public View.OnClickListener filterButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewLogListActivity.this);
                builder.setTitle(R.string.filter_prompt)
                        .setPositiveButton(R.string.medication_name_filter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final AlertDialog.Builder medBuilder = new AlertDialog.Builder(ViewLogListActivity.this);
                                medBuilder.setItems(uniqueMedNames, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String selected = uniqueMedNames[i];
                                        LiveData<List<MedicationLogEntry>> entriesByMedName =
                                                logDb.logDao().getEntriesByMedication(selected);

                                        entriesByMedName.observe(ViewLogListActivity.this,
                                                new Observer<List<MedicationLogEntry>>() {
                                                    @Override
                                                    public void onChanged(@Nullable List<MedicationLogEntry> medicationLogEntries) {
                                                        logAdapter.setLogEntries(medicationLogEntries);
                                                    }
                                                });
                                    }
                                });
                                medBuilder.setTitle("Select a Medication");
                                medBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                                medBuilder.create().show();
                            }
                        })
                        .setNegativeButton(R.string.date_taken_filter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final AlertDialog.Builder dateBuilder = new AlertDialog.Builder(ViewLogListActivity.this);
                                dateBuilder.setItems(datesTaken, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String selected = datesTaken[i];
                                        LiveData<List<MedicationLogEntry>> entriesByMedName =
                                                logDb.logDao().getEntriesByDate(selected);

                                        entriesByMedName.observe(ViewLogListActivity.this,
                                                new Observer<List<MedicationLogEntry>>() {
                                                    @Override
                                                    public void onChanged(@Nullable List<MedicationLogEntry> medicationLogEntries) {
                                                        logAdapter.setLogEntries(medicationLogEntries);
                                                    }
                                                });
                                    }
                                });
                                dateBuilder.setTitle("Select a Date");
                                dateBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                                dateBuilder.create().show();
                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });


                AlertDialog dialog = builder.create();
                dialog.show();
            }
        };
    }

    @Override
    public void onItemClickListener(int itemId) {
        Intent viewEntryIntent = new Intent(ViewLogListActivity.this, ViewLogEntryActivity.class);
        viewEntryIntent.putExtra(EXTRA_LOG_ID, itemId);
        startActivity(viewEntryIntent);

    }
}
