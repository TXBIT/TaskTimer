package android.demo.tasktimer;

import android.provider.BaseColumns;

public class TasksContract {
    // store the table name
    static final String TABLE_NAME = "Tasks";

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
