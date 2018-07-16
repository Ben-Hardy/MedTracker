package ben.medtracker;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import ben.medtracker.data.MedicationDatabase;
import ben.medtracker.data.MedicationEntry;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<MedicationEntry>> medications;

    public MainViewModel(@NonNull Application application) {
        super(application);
        MedicationDatabase database = MedicationDatabase.getDatabase(this.getApplication());
        medications = database.medicationDao().loadAllMedications();
    }

    public LiveData<List<MedicationEntry>> getMedications() {
        return medications;
    }
}
