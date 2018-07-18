package ben.medtracker.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;


/*
Identical to the Medication Database in implementation. See notes in that file for where
I based this code off of.
 */
@Database(entities = {MedicationLogEntry.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class MedicationLogDatabase extends RoomDatabase{

    private static final String TAG = MedicationLogDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "medication_log";

    private static MedicationLogDatabase logDatabase;

    public abstract MedicationLogDao logDao();

    public static MedicationLogDatabase getDatabase(Context context) {
        if (logDatabase == null) {
            synchronized (LOCK) {
                logDatabase = Room.databaseBuilder(context.getApplicationContext(),
                        MedicationLogDatabase.class, MedicationLogDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(TAG, "Fetching the log database!");
        return logDatabase;
    }
}
