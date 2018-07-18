package ben.medtracker.data;

/*
Taken from the Udacity todolist tutorial project available at
https://classroom.udacity.com/courses/ud851
 */


import android.arch.persistence.room.TypeConverter;
import java.util.Date;

public class DateConverter {
    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}


