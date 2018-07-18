package ben.medtracker.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;


/*
    Based on the Udacity Android lessons singleton database class available at:
    https://classroom.udacity.com/courses/ud851 in the Android Architecture Components Lesson
    Thanks to them for teaching me how to make all this stuff!
 */

@Database(entities = {MedicationEntry.class}, version = 2, exportSchema = false)
public abstract class MedicationDatabase extends RoomDatabase{

    private static final String LOG_TAG = MedicationDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "medication";

    private static MedicationDatabase medicationDatabase;

    public abstract MedicationDao medicationDao();

    public static MedicationDatabase getDatabase(Context context) {
        if (medicationDatabase == null) {
            synchronized (LOCK) {
                medicationDatabase = Room.databaseBuilder(context.getApplicationContext(),
                        MedicationDatabase.class, MedicationDatabase.DATABASE_NAME)

                        .build();
                //Log.d(LOG_TAG, "Medication database created!");
            }
        }
        Log.d(LOG_TAG, "Fetching the database!");
        return medicationDatabase;
    }

}
