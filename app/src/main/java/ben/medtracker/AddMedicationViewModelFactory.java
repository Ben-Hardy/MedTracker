package ben.medtracker;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import ben.medtracker.data.MedicationDatabase;

/*
A factory that creates ViewModels for the medication with id medId. Needed for adding medications
to the database
 */
public class AddMedicationViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final MedicationDatabase medDb;
    private final int medId;

    public AddMedicationViewModelFactory(MedicationDatabase database, int medId) {
        this.medDb = database;
        this.medId = medId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new AddMedicationViewModel(medDb, medId);
    }

}
