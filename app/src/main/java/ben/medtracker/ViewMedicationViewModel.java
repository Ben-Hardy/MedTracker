package ben.medtracker;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import ben.medtracker.data.MedicationDatabase;
import ben.medtracker.data.MedicationEntry;

/*
A ViewModel for our education. Gets the medication with id medId from the database and
creates a livedata instance of it so it can be accessed safely with an observer
 */
public class ViewMedicationViewModel extends ViewModel {

    private LiveData<MedicationEntry> medicationEntryLiveData;

    public ViewMedicationViewModel(MedicationDatabase database, int medId) {
        medicationEntryLiveData = database.medicationDao().loadMedicationById(medId);
    }

    public LiveData<MedicationEntry> getMedication() {
        return medicationEntryLiveData;
    }
}
