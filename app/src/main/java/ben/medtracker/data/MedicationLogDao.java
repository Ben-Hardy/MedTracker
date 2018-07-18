package ben.medtracker.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MedicationLogDao
{
    @Query("SELECT * FROM medication_log")
    LiveData<List<MedicationLogEntry>> loadMedicationEntries();

    @Insert
    void insertLogEntry(MedicationLogEntry logEntry);

    @Delete
    void deleteLogEntry(MedicationLogEntry logEntry);

    @Query ("SELECT * FROM medication_log WHERE id = :id")
    LiveData<MedicationLogEntry> loadLogById(int id);

    @Query("DELETE FROM medication_log")
    void clearMedicationLogDatabase();


}
