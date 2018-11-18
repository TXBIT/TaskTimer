package android.demo.tasktimer;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.security.InvalidParameterException;

/**
 * A placeholder fragment containing a simple view.
 */
// the fragment must implement the loader manager's loader callbacks interface
// so that it gets notified when things happened
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "MainActivityFragment";
    // a loader manager can manage several loaders, as a result, each loader needs a unique number to identify
    public static final int LOADER_ID = 0;


    public MainActivityFragment() {
        Log.d(TAG, "MainActivityFragment: constructor called");

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: starts");
        super.onActivityCreated(savedInstanceState);

        // deprecated: getLoaderManager.initLoader(LOADER_ID, null, this);
        // get an instance of loader manager and call initLoader
        // tell the manager which loader we are initializing by passing LOADER_ID
        // if there are more than one loader
        // the LOADER_ID decides which one the events will be responding to
        // the third argument is a LoaderManager.loader callbacks object that tells the manager
        // which object will be handling the onCreate, onLoadFinished and onLoaderReset calls
        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @NonNull
    @Override
    // the loader manager will call these methods
    // when it wants to notify the fragment of important events about the loader
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {
        Log.d(TAG, "onCreateLoader: starts with id " + id);
        // a string array to hold the columns that we want to fetch from database
        String[] projection = {TasksContract.Columns._ID, TasksContract.Columns.TASKS_NAME,
                TasksContract.Columns.TASKS_DESCRIPTION, TasksContract.Columns.TASKS_SORTORDER};
        // specify a sort order
        // sorting according to the sort order column
        // and then alphabetically by name for the tasks with same sort order
        String sortOrder = TasksContract.Columns.TASKS_SORTORDER + "," + TasksContract.Columns.TASKS_NAME;
        // add additional cases if there are more than one loader
        switch (id) {
            case LOADER_ID:
                // passing a query to the cursor loader
                // which runs the query on a background thread
                // 1st arg: the activity that this fragment is attached to
                // 2nd arg: the uri of the tasks table
                // 3rd arg: the list of the columns that we want
                // 4th and 5th args: selection and selection args
                // 6th arg: sort order column
                return new CursorLoader(getActivity(),
                        TasksContract.CONTENT_URI,
                        projection,
                        null,
                        null,
                        sortOrder);
            default:
                throw new InvalidParameterException(TAG + ".onCreateLoader called with invalid loader id " + id);
        }
    }

    // when the cursor retrieved all the data, it lets the loader manager know
    // the loader manager then calls onLoadFinished method and passes the cursor
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Entering onLoadFinished: ");
        // loop through the cursor and log the data
        // the cursor is not closed because it belongs to the cursor loader
        // if it is closed, the cursor loader won't get notification that the data has changed
        int count = -1;
        if (data != null) {
            while (data.moveToNext()) {
                for (int i = 0; i < data.getColumnCount(); i++) {
                    Log.d(TAG, "onLoadFinished: " + data.getColumnName(i) + ": " + data.getString(i));
                }
                Log.d(TAG, "onLoadFinished: ===========================");
            }
            count = data.getCount();
        }
        Log.d(TAG, "onLoadFinished: count is " + count);
    }

    // onLoaderReset is called when a previously created loader is being reset and thus making its data unavailable
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: starts");
    }
}
