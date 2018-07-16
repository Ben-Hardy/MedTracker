package ben.medtracker;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import ben.medtracker.data.MedicationDatabase;
import ben.medtracker.data.MedicationEntry;

public class AddMedicationViewModel extends ViewModel {

    private LiveData<MedicationEntry> medicationEntryLiveData;

    public AddMedicationViewModel(MedicationDatabase database, int medId) {
        medicationEntryLiveData = database.medicationDao().loadMedicationById(medId);
    }

    public LiveData<MedicationEntry> getMedication() {
        return medicationEntryLiveData;
    }
}
