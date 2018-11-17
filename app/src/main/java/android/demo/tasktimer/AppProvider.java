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
    static final String CONTENT_AUTHORITY = "android.demo.tasktimer.provider";
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    private static final String TAG = "AppProvider";
    private static final int TASKS = 100;
    private static final int TASKS_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final int TIMINGS = 200;
    private static final int TIMINGS_ID = 201;
    private static final int TASK_DURATIONS = 400;

//    private static final int TASK_TIMINGS = 300;
//    private static final int TASK_TIMINGS_ID = 301;
    private static final int TASK_DURATIONS_ID = 401;
    private AppDatabase mOpenHelper;

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
        // we use a uri matcher to decide what uri has been passed then return the appropriate mime type
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TASKS:
                return TasksContract.CONTENT_TYPE;

            case TASKS_ID:
                return TasksContract.CONTENT_ITEM_TYPE;

//            case TIMINGS:
//                return TimingsContract.Timings.CONTENT_TYPE;
//
//            case TIMINGS_ID:
//                return TimingsContract.Timings.CONTENT_ITEM_TYPE;
//
//            case TASK_DURATIONS:
//                return DurationsContract.TaskDurations.CONTENT_TYPE;
//
//            case TASK_DURATIONS_ID:
//                return DurationsContract.TaskDurations.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("unknown uri: " + uri);
        }
    }

    // ContentValues is used to store a set of values that the ContentResolver can process
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "entering insert, called with uri: " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "match is: " + match);

        final SQLiteDatabase db;

        Uri returnUri;
        long recordId;

        switch (match) {
            case TASKS:
                // we call getWritableDatabase after we check that the uri is valid
                db = mOpenHelper.getWritableDatabase();

                // the insertion is done by the db.insert method
                // we give it the name of the table to insert into and a list of values to insert
                // whatever the ContentValues object is, we just pass it onto the database insert method
                // because of that, we do not need to call any of its methods
                // our table does not accept a completely null record since the name column is set to be not null
                // therefore we can ignore the second parameter and pass a null there

                recordId = db.insert(TasksContract.TABLE_NAME, null, values);

                // the db.insert method returns the ID of the new row (recordId)
                // if recordId is greater than 0, we append it to the uri using the TasksContract.buildTaskUri method
                // if recordId is -1, the insertion failed, we raise an SQLException
                if (recordId >= 0) {
                    returnUri = TasksContract.buildTaskUri(recordId);
                } else {
                    throw new android.database.SQLException("failed to insert into " + uri.toString());
                }

                break;

            case TIMINGS:
//                // we call getWritableDatabase after we check that the uri is valid
//                db = mOpenHelper.getWritableDatabase();
//
//                recordId = db.insert(TimingsContract.Timings.buildTimingUri(recordId));
//
//                if (recordId >= 0) {
//                    returnUri = TimingsContract.Timings.buildTimingsUri(recordId)
//                } else {
//                    throw new android.database.SQLException("failed to insert into " + uri.toString());
//                }
//
//                break;

            default:
                throw new IllegalArgumentException("unknown uri: " + uri);
        }

        Log.d(TAG, "exiting insert, returning: " + returnUri);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "delete called with uri: " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "match is " + match);

        final SQLiteDatabase db;
        int count;

        String selectionCriteria;

        switch (match) {
            case TASKS:
                db = mOpenHelper.getWritableDatabase();
                count = db.delete(TasksContract.TABLE_NAME, selection, selectionArgs);
                break;

            case TASKS_ID:
                db = mOpenHelper.getWritableDatabase();
                long taskId = TasksContract.getTaskId(uri);
                selectionCriteria = TasksContract.Columns._ID + " = " + taskId;

                if ((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.delete(TasksContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;

//            case TIMINGS:
//                db = mOpenHelper.getWritableDatabase();
//                count = db.delete(TimingsContract.TABLE_NAME, selection, selectionArgs);
//                break;
//
//            case TIMINGS_ID:
//                db = mOpenHelper.getWritableDatabase();
//                long timingsId = TimingsContract.getTaskId(uri);
//                selectionCriteria = TimingsContract.Columns._ID + " = " + timingsId;
//
//                if ((selection != null) && (selection.length() > 0)) {
//                    selectionCriteria += " AND (" + selection + ")";
//                }
//                count = db.delete(TimingsContract.TABLE_NAME, selectionCriteria, selectionArgs);
//                break;

            default:
                throw new IllegalArgumentException("unknown uri: " + uri);
        }

        Log.d(TAG, "exiting delete, returning " + count);

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(TAG, "update called with uri: " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "match is " + match);

        final SQLiteDatabase db;
        int count;

        String selectionCriteria;

        switch (match) {
            case TASKS:
                db = mOpenHelper.getWritableDatabase();
                count = db.update(TasksContract.TABLE_NAME, values, selection, selectionArgs);
                break;

            case TASKS_ID:
                db = mOpenHelper.getWritableDatabase();
                long taskId = TasksContract.getTaskId(uri);
                selectionCriteria = TasksContract.Columns._ID + " = " + taskId;

                if ((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.update(TasksContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;

//            case TIMINGS:
//                db = mOpenHelper.getWritableDatabase();
//                count = db.update(TimingsContract.TABLE_NAME, values, selection, selectionArgs);
//                break;
//
//            case TIMINGS_ID:
//                db = mOpenHelper.getWritableDatabase();
//                long timingsId = TimingsContract.getTaskId(uri);
//                selectionCriteria = TimingsContract.Columns._ID + " = " + timingsId;
//
//                if ((selection != null) && (selection.length() > 0)) {
//                    selectionCriteria += " AND (" + selection + ")";
//                }
//                count = db.update(TimingsContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
//                break;

            default:
                throw new IllegalArgumentException("unknown uri: " + uri);
        }

        Log.d(TAG, "exiting update, returning " + count);

        return count;
    }
}
