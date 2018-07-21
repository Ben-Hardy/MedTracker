package ben.medtracker;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import ben.medtracker.data.MedicationLogDatabase;
import ben.medtracker.data.MedicationLogEntry;

public class LogViewModel extends AndroidViewModel {

    private LiveData<List<MedicationLogEntry>> logs;

    public LogViewModel(@NonNull Application application) {
        super(application);
        MedicationLogDatabase database = MedicationLogDatabase.getDatabase(this.getApplication());
        logs = database.logDao().loadMedicationEntries();
    }

    public LiveData<List<MedicationLogEntry>> getLogs() {
        return logs;
    }
}
