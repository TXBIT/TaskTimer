package android.demo.tasktimer;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static android.demo.tasktimer.AppProvider.CONTENT_AUTHORITY;
import static android.demo.tasktimer.AppProvider.CONTENT_AUTHORITY_URI;

public class TimingsContract {
    // store the table name
    static final String TABLE_NAME = "Timings";
    /**
     * The uri to access the Timings table
     * <p>
     * CONTENT_URI can be used by external classes
     * including different apps that use our content provider to refer to our Timings table
     */
    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);
    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;

    // append the given Id to the end of the path
    public static Uri buildTimingUri(long TimingId) {
        return ContentUris.withAppendedId(CONTENT_URI, TimingId);
    }

    // extract the ID from a uri
    public static long getTimingId(Uri uri) {
        // convert the last path segment to a long
        return ContentUris.parseId(uri);
    }

    // store the columns
    public static class Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String TIMINGS_TASK_ID = "TaskId";
        public static final String TIMINGS_START_TIME = "StartTime";
        public static final String TIMINGS_DURATION = "Duration";

        private Columns() {
            // private constructor to prevent instantiation
        }
    }

}
