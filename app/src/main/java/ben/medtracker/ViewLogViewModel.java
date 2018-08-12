package ben.medtracker;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import ben.medtracker.data.MedicationLogDatabase;
import ben.medtracker.data.MedicationLogEntry;

/*
Creates a log data view model and populates it with data for the given logId from the given database
 */
public class ViewLogViewModel extends ViewModel {

    private LiveData<MedicationLogEntry> medicationLogEntryLiveData;

    public ViewLogViewModel(MedicationLogDatabase database, int logId) {
        medicationLogEntryLiveData = database.logDao().loadLogById(logId);
    }

    public LiveData<MedicationLogEntry> getLogEntry() {
        return medicationLogEntryLiveData;
    }
}
