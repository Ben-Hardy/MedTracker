package ben.medtracker;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import ben.medtracker.data.MedicationLogDatabase;
import ben.medtracker.data.MedicationLogEntry;

public class ViewLogViewModel extends ViewModel {

    private LiveData<MedicationLogEntry> medicationLogEntryLiveData;

    public ViewLogViewModel(MedicationLogDatabase database, int logId) {
        medicationLogEntryLiveData = database.logDao().loadLogById(logId);
    }

    public LiveData<MedicationLogEntry> getLogEntry() {
        return medicationLogEntryLiveData;
    }
}
