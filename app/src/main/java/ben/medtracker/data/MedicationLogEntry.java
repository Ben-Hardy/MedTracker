package ben.medtracker.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "medication_log")
public class MedicationLogEntry {

    /*
    The identifying key used by the database to find this log entry
     */
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String medicationName;

    @ColumnInfo(name="num_doses_taken")
    private String numDosesTaken;
    @ColumnInfo(name="date_taken")
    private String dateTaken;



    private String timeTaken;

    private String notes;

    @Ignore
    public MedicationLogEntry(String medicationName, String numDosesTaken, String dateTaken,
    String timeTaken, String notes) {
        this.medicationName = medicationName;
        this.numDosesTaken = numDosesTaken;
        this.dateTaken = dateTaken;
        this.timeTaken = timeTaken;
        this.notes = notes;
    }

    public MedicationLogEntry(int id, String medicationName, String numDosesTaken, String dateTaken,
                              String timeTaken, String notes) {
        this.id = id;
        this.medicationName = medicationName;
        this.numDosesTaken = numDosesTaken;
        this.dateTaken = dateTaken;
        this.timeTaken = timeTaken;
        this.notes = notes;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getNumDosesTaken() {
        return numDosesTaken;
    }

    public void setNumDosesTaken(String numDosesTaken) {
        this.numDosesTaken = numDosesTaken;
    }

    public String getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(String dateTaken) {
        this.dateTaken = dateTaken;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }
}
