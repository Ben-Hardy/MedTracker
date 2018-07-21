package ben.medtracker;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;

import ben.medtracker.data.MedicationLogDatabase;
import ben.medtracker.data.MedicationLogEntry;

public class ViewLogListActivity extends AppCompatActivity implements EntryAdapter.ItemClickListener {

    // Logging tag
    private static final String TAG = ViewLogListActivity.class.getSimpleName();

    private MedicationLogDatabase logDb;
    private EntryAdapter logAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_log_list);
        logAdapter = new EntryAdapter(ViewLogListActivity.this, this);
        logDb = MedicationLogDatabase.getDatabase(getApplicationContext());
        initializeUI();


        Button refreshButton = findViewById(R.id.refresh_activity);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });

    }
    public void initializeUI() {
        RecyclerView recyclerView = findViewById(R.id.log_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        LogViewModel viewModel = ViewModelProviders.of(ViewLogListActivity.this).get(LogViewModel.class);
        viewModel.getLogs().observe(this, new Observer<List<MedicationLogEntry>>() {
            @Override
            public void onChanged(@Nullable List<MedicationLogEntry> medicationLogEntries) {
                logAdapter.setLogEntries(medicationLogEntries);
                logAdapter.notifyDataSetChanged();
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        recyclerView.getLayoutParams().height = (Integer) (displayMetrics.heightPixels * 70)/100;

        recyclerView.setAdapter(logAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(decoration);

        Button goHomeButton = findViewById(R.id.entrylist_go_home_button);
        goHomeButton.setOnClickListener(goHomeButtonClickListener());



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

    @Override
    public void onItemClickListener(int itemId) {
        Log.d(TAG, "" + itemId);
    }
}