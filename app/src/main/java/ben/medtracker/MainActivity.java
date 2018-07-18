package ben.medtracker;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import ben.medtracker.data.MedicationDatabase;
import ben.medtracker.data.MedicationEntry;

import static android.icu.lang.UCharacter.DecompositionType.CANONICAL;
import static android.icu.lang.UCharacter.DecompositionType.VERTICAL;

public class MainActivity extends AppCompatActivity implements MedAdapter.ItemClickListener {

    // Logging tag
    private static final String TAG = MainActivity.class.getSimpleName();

    // members we will use on the main activity screen

    // This RecyclerView will display all the medications in the database;
    private RecyclerView recyclerView;

    private MedicationDatabase medDb;
    private MedAdapter medAdapter;
    private Button addMedicationButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.med_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        medAdapter = new MedAdapter(this, this);
        recyclerView.setAdapter(medAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(decoration);

        addMedicationButton = findViewById(R.id.add_med_button_main);

        addMedicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addMedicationIntent = new Intent(MainActivity.this, AddMedicationActivity.class);
                startActivity(addMedicationIntent);
            }
        });

        medDb = MedicationDatabase.getDatabase(getApplicationContext());

        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMedications().observe(this, new Observer<List<MedicationEntry>>() {
            @Override
            public void onChanged(@Nullable List<MedicationEntry> medicationEntries) {
                medAdapter.setMedEntries(medicationEntries);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            Intent deleteMedicationIntent = new Intent(MainActivity.this, DeleteMedication.class);
            startActivity(deleteMedicationIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClickListener(int itemClicked) {
        Intent viewMedIntent = new Intent(MainActivity.this, ViewMedicationActivity.class);
        viewMedIntent.putExtra(ViewMedicationActivity.EXTRA_MED_ID, itemClicked);
        startActivity(viewMedIntent);
    }
}
