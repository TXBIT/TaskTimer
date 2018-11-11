package android.demo.tasktimer;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Provider for the TaskTimer app. This is the only that knows about {@link AppDatabase}
 */

public class AppProvider extends ContentProvider {
    private static final String TAG = "AppProvider";

    private AppDatabase mOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final String CONTENT_AUTHORITY = "android.demo.tasktimer.provider";
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final int TASKS = 100;
    private static final int TASKS_ID = 101;

    private static final int TIMINGS = 200;
    private static final int TIMINGS_ID = 201;

//    private static final int TASK_TIMINGS = 300;
//    private static final int TASK_TIMINGS_ID = 301;

    private static final int TASK_DURATIONS = 400;
    private static final int TASK_DURATIONS_ID = 401;

    private static UriMatcher buildUriMatcher() {
        // if there is no table name in the URI, the matcher will returns NO_MATCH
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        // If the Tasks table is specified without an ID, the matcher will return 100
        // content://android.demo.tasktimer.provider/Tasks
        matcher.addURI(CONTENT_AUTHORITY, TasksContract.TABLE_NAME, TASKS);
        // If an ID is provided, the matcher will return 101
        // content://android.demo.tasktimer.provider/Tasks/1 (ID is 1)
        matcher.addURI(CONTENT_AUTHORITY, TasksContract.TABLE_NAME + "/#", TASKS_ID);

//        matcher.addURI(CONTENT_AUTHORITY, TimingsContract.TABLE_NAME, TIMINGS);
//        matcher.addURI(CONTENT_AUTHORITY, TimingsContract.TABLE_NAME + "/#", TIMINGS_ID);
//
//        matcher.addURI(CONTENT_AUTHORITY, DurationsContract.TABLE_NAME, TASK_DURATIONS);
//        matcher.addURI(CONTENT_AUTHORITY, DurationsContract.TABLE_NAME + "/#", TASK_DURATIONS_ID);

        return matcher;
    }

    // get an instance of the database and store in the mOpenHelper field
    @Override
    public boolean onCreate() {
        mOpenHelper = AppDatabase.getInstance(getContext());
        return true;
    }

    // we use a UriMatcher to determine what kind of uri that has been given
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query: called with URI " + uri);
        // we use a UriMatcher to analyze the given uri
        // the value of "match" helps us determine which table we should use
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "query: match is " + match);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // we use the switch statement to choose different blocks of code depending on the result of matching the uri
        // if the uri contains a table name, the query will be a basic sql select statement with the table name
        // all records will be returned in this case
        // when the uri matches TASKS, TIMINGS, TASK_DURATIONS
        // we call the setTables method to tell the query builder which table we want to query from
        // if the uri contains an ID
        // the matcher will return TASKS_ID, TIMINGS_ID, TASK_DURATIONS_ID
        // in this case, we call the get...Id method in the contract class that will extract the ID from the uri
        // after that, we call the queryBuilder's appendWhere method adding a where clause to our query
        switch (match) {
            case TASKS:
                // setTables sets the list of tables to query
                // multiple tables can be specified to perform a join
                // for example:
                // setTables("foo, bar")
                // setTables("foo LEFT OUTER JOIN bar ON(foo.id=bar.id)")
                queryBuilder.setTables(TasksContract.TABLE_NAME);
                break;

            case TASKS_ID:
                queryBuilder.setTables(TasksContract.TABLE_NAME);
                long taskId = TasksContract.getTaskId(uri);
                // build a where statement
                queryBuilder.appendWhere(TasksContract.Columns._ID + " = " + taskId);
                break;

//            case TIMINGS:
//                queryBuilder.setTables(TimingsContract.TABLE_NAME);
//                break;
//
//            case TIMINGS_ID:
//                queryBuilder.setTables(TimingsContract.TABLE_NAME);
//                long timingId = TimingsContract.getTimingId(uri);
//                // build a where statement
//                queryBuilder.appendWhere(TimingsContract.Columns._ID + " = " + timingId);
//                break;
//
//            case TASK_DURATIONS:
//                queryBuilder.setTables(DurationsContract.TABLE_NAME);
//                break;
//
//            case TASK_DURATIONS_ID:
//                queryBuilder.setTables(DurationsContract.TABLE_NAME);
//                long durationId = DurationsContract.getDurationId(uri);
//                // build a where statement
//                queryBuilder.appendWhere(DurationsContract.Columns._ID + " = " + durationId);
//                break;

            // if the uri is not matched, we will raise an IllegalArgumentException
            default:
                throw new IllegalArgumentException("unknown URI " + uri);
        }

        // if the uri is valid and matches one of our values
        // we use the AppDatabase instance stored in mOpenHelper to return a readable database object
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        // these parameters have been passed to the query method
//        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
