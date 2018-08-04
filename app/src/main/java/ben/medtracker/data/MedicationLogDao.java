package ben.medtracker.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MedicationLogDao
{
    @Query("SELECT * FROM medication_log")
    LiveData<List<MedicationLogEntry>> loadMedicationEntries();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertLogEntry(MedicationLogEntry logEntry);

    @Delete
    void deleteLogEntry(MedicationLogEntry logEntry);

    @Query("SELECT * FROM medication_log WHERE id = :id")
    LiveData<MedicationLogEntry> loadLogById(int id);

    @Query("DELETE FROM medication_log")
    void clearMedicationLogDatabase();

    @Query("SELECT * FROM medication_log WHERE medicationName = :medicationName")
    LiveData<List<MedicationLogEntry>> getEntriesByMedication(String medicationName);

    @Query("SELECT * FROM medication_log WHERE date_taken = :dateTaken")
    LiveData<List<MedicationLogEntry>> getEntriesByDate(String dateTaken);

    @Query("SELECT DISTINCT medicationName FROM medication_log")
    LiveData<List<String>> getEnteredMedicationNames();

    @Query("SELECT DISTINCT date_taken FROM medication_log")
    LiveData<List<String>> getUniqueLogDates();



}
