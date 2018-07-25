package ben.medtracker;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import java.util.List;

import ben.medtracker.data.MedicationDatabase;
import ben.medtracker.data.MedicationEntry;

public class ViewMedicationListActivity extends AppCompatActivity implements MedAdapter.ItemClickListener {

    // Logging tag
    private static final String TAG = ViewMedicationListActivity.class.getSimpleName();
    public static final String EXTRA_MED_NAME = "extramedname";

    private MedicationDatabase medDb;
    private MedAdapter medAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_medication_list);

        initializeUI();

        medDb = MedicationDatabase.getDatabase(getApplicationContext());
    }

    public void initializeUI() {
        RecyclerView recyclerView = findViewById(R.id.med_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        medAdapter = new MedAdapter(this, this);
        recyclerView.setAdapter(medAdapter);
        new ItemTouchHelper(swipeItemTouchHelper()).attachToRecyclerView(recyclerView);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(decoration);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        recyclerView.getLayoutParams().height = (Integer) (displayMetrics.heightPixels * 60)/100;

        Button addMedicationButton = findViewById(R.id.add_med_button_main);
        addMedicationButton.setOnClickListener(addMedicationButtonClickListener());

        Button goHomeButton = findViewById(R.id.medlist_go_home_button);
        goHomeButton.setOnClickListener(goHomeButtonClickListener());

        MedicationViewModel viewModel = ViewModelProviders.of(this).get(MedicationViewModel.class);
        viewModel.getMedications().observe(this, new Observer<List<MedicationEntry>>() {
            @Override
            public void onChanged(@Nullable List<MedicationEntry> medicationEntries) {
                medAdapter.setMedEntries(medicationEntries);
            }
        });
    }

    public ItemTouchHelper.SimpleCallback swipeItemTouchHelper() {
        return new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ViewMedicationListActivity.this);
                builder.setMessage(R.string.add_entry_prompt)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent addEntryIntent = new Intent(ViewMedicationListActivity.this,
                                        AddLogEntryActivity.class);
                                addEntryIntent.putExtra(EXTRA_MED_NAME, medAdapter.getMedEntries().get(viewHolder.getAdapterPosition()).getMedicationName());
                                startActivity(addEntryIntent);
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                medAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        medAdapter.notifyDataSetChanged();
    }

    public View.OnClickListener goHomeButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goHomeIntent = new Intent(ViewMedicationListActivity.this, MainActivity.class);
                startActivity(goHomeIntent);
            }
        };
    }

    public View.OnClickListener addMedicationButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addMedicationIntent = new Intent(ViewMedicationListActivity.this, AddMedicationActivity.class);
                startActivity(addMedicationIntent);
            }
        };
    }

    @Override
    public void onItemClickListener(int itemClicked) {
        Intent viewMedIntent = new Intent(ViewMedicationListActivity.this, ViewMedicationActivity.class);
        viewMedIntent.putExtra(ViewMedicationActivity.EXTRA_MED_ID, itemClicked);
        startActivity(viewMedIntent);
    }
}
