package ben.medtracker.data;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "medication")
public class MedicationEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;



    private String medicationName;

    /*
    The number of times in which the medication needs to be taken on a daily basis
     */
    @ColumnInfo(name = "daily_frequency")
    private int dailyFrequency;

    /*
    A string representing which days in which the medication needs to be taken
    Will be of the format "MTWtFSs" where each letter represents a day of the week.
     */
    @ColumnInfo(name = "weekly_frequency")
    private String weeklyFrequency;

    /*
    The notes from on the bottle provided by the doctor or pharmacist.
     */
    @ColumnInfo(name = "doc_notes")
    private String docNotes;


    /*
    This string will store the daily entries for a medication.
    It will store the date and whether the medication was taken on that date on each line of the
    string.
     */
    @ColumnInfo(name = "daily_entries")
    private String dailyEntries;

    @Ignore
    public MedicationEntry(String medicationName, int dailyFrequency, String weeklyFrequency,
                           String docNotes) {
        this.medicationName = medicationName;
        this.dailyFrequency = dailyFrequency;
        this.weeklyFrequency = weeklyFrequency;
        this.docNotes = docNotes;
    }

    public MedicationEntry(int id, String medicationName, int dailyFrequency, String weeklyFrequency,
                           String docNotes) {
        this.id = id;
        this.medicationName = medicationName;
        this.dailyFrequency = dailyFrequency;
        this.weeklyFrequency = weeklyFrequency;
        this.docNotes = docNotes;
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

    public int getDailyFrequency() {
        return dailyFrequency;
    }

    public void setDailyFrequency(int dailyFrequency) {
        this.dailyFrequency = dailyFrequency;
    }

    public String getWeeklyFrequency() {
        return weeklyFrequency;
    }

    public void setWeeklyFrequency(String weeklyFrequency) {
        this.weeklyFrequency = weeklyFrequency;
    }

    public String getDocNotes() {
        return docNotes;
    }

    public void setDocNotes(String docNotes) {
        this.docNotes = docNotes;
    }

    public String getDailyEntries() {
        return dailyEntries;
    }

    public void setDailyEntries(String dailyEntries) {
        this.dailyEntries = dailyEntries;
    }


}
