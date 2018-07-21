package ben.medtracker;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import ben.medtracker.data.MedicationDatabase;
import ben.medtracker.data.MedicationEntry;

/*
The ViewModel for our MainActivity. Since we need a list of all medications, we use this to
access them. Since it is LiveData, we are required to use an observer to safely access this data
 */
public class MedicationViewModel extends AndroidViewModel {

    private LiveData<List<MedicationEntry>> medications;

    public MedicationViewModel(@NonNull Application application) {
        super(application);
        MedicationDatabase database = MedicationDatabase.getDatabase(this.getApplication());
        medications = database.medicationDao().loadAllMedications();
    }

    public LiveData<List<MedicationEntry>> getMedications() {
        return medications;
    }
}
