package ben.medtracker;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;

import ben.medtracker.data.MedicationLogDatabase;

public class ViewLogViewModelFactory  extends ViewModelProvider.NewInstanceFactory  {
    private final MedicationLogDatabase logDb;
    private final int logId;

    public ViewLogViewModelFactory(MedicationLogDatabase database, int logId) {
        this.logDb = database;
        this.logId = logId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new ViewLogViewModel(logDb, logId);
    }

}
