package ben.medtracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button todaysSummaryButton;
    Button viewMyMedicationsButton;
    Button viewMyEntriesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUI();
    }

    public void initializeUI() {
        todaysSummaryButton = findViewById(R.id.today_button);
        todaysSummaryButton.setOnClickListener(onSummaryButtonClickListener());
        viewMyMedicationsButton = findViewById(R.id.view_medications_button);
        viewMyMedicationsButton.setOnClickListener(onMyMedicationsButtonClickListener());
        viewMyEntriesButton = findViewById(R.id.view_entries_button);
        viewMyEntriesButton.setOnClickListener(onMyEntriesButtonClickListener());
    }

    public View.OnClickListener onSummaryButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent summaryIntent = new Intent(MainActivity.this, TodaysSummary.class);
                startActivity(summaryIntent);
            }
        };
    }

    public View.OnClickListener onMyMedicationsButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewMedsIntent = new Intent(MainActivity.this, ViewMedicationListActivity.class);
                startActivity(viewMedsIntent);
            }
        };
    }

    public View.OnClickListener onMyEntriesButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewEntriesIntent = new Intent(MainActivity.this, ViewLogListActivity.class);
                startActivity(viewEntriesIntent);
            }
        };
    }
}
