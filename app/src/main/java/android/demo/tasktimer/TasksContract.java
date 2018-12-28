package android.demo.tasktimer;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static android.demo.tasktimer.AppProvider.CONTENT_AUTHORITY;
import static android.demo.tasktimer.AppProvider.CONTENT_AUTHORITY_URI;

public class TasksContract {
    // store the table name
    static final String TABLE_NAME = "Tasks";
    /**
     * The uri to access the Tasks table
     * <p>
     * CONTENT_URI can be used by external classes
     * including different apps that use our content provider to refer to our Tasks table
     */
    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);
    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;

    // append the given Id to the end of the path
    public static Uri buildTaskUri(long taskId) {
        return ContentUris.withAppendedId(CONTENT_URI, taskId);
    }

    // extract the ID from a uri
    public static long getTaskId(Uri uri) {
        // convert the last path segment to a long
        return ContentUris.parseId(uri);
    }

    // store the columns
    public static class Columns {
        public static final String _ID = BaseColumns._ID;
        public static final String TASKS_NAME = "Name";
        public static final String TASKS_DESCRIPTION = "Description";
        public static final String TASKS_SORTORDER = "SortOrder";

        private Columns() {
            // private constructor to prevent instantiation
        }
    }

}
