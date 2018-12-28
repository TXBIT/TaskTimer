package android.demo.tasktimer;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
                                                              CursorRecyclerViewAdapter.OnTaskClickListener {
    private static final String TAG = "MainActivityFragment";
    // a loader manager can manage several loaders, as a result, each loader needs a unique number to identify
    public static final int LOADER_ID = 0;

    //adapter reference
    private CursorRecyclerViewAdapter mAdapter;

    public MainActivityFragment() {
        Log.d(TAG, "MainActivityFragment: constructor called");

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: starts");
        super.onActivityCreated(savedInstanceState);

        // Activities containing this fragment must implement its callbacks.
        Activity activity = getActivity();
        if (!(activity instanceof CursorRecyclerViewAdapter.OnTaskClickListener)) {
            throw new ClassCastException(activity.getClass().getSimpleName()
                    + " must implement CursorRecyclerViewAdapter.OnTaskClickListener interface");
        }

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
    public void onEditClick(Task task) {
        Log.d(TAG, "onEditClick: called");
        CursorRecyclerViewAdapter.OnTaskClickListener listener = (CursorRecyclerViewAdapter.OnTaskClickListener)getActivity();
        if (listener != null) {
            listener.onEditClick(task);
        }
    }

    @Override
    public void onDeleteClick(Task task) {
        Log.d(TAG, "onDeleteClick: called");
        CursorRecyclerViewAdapter.OnTaskClickListener listener = (CursorRecyclerViewAdapter.OnTaskClickListener)getActivity();
        if (listener != null) {
            listener.onDeleteClick(task);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.task_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // we haven't got any data for the adapter yet so we will initialize it with null
        // that will cause it to return a view containing the instructions
        // which is what we expect when the app starts up with no task records

        if (mAdapter == null) {
            mAdapter = new CursorRecyclerViewAdapter(null, this);
        }
//        else {
//            mAdapter.setListener((CursorRecyclerViewAdapter.OnTaskClickListener)getActivity());
//        }
        recyclerView.setAdapter(mAdapter);

        Log.d(TAG, "onCreateView: returning");

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: called");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
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
        // COLLATE NOCASE: ignore case
        // <order by> Tasks.SortOrder, Tasks.Name COLLATE NOCASE
        String sortOrder = TasksContract.Columns.TASKS_SORTORDER + "," + TasksContract.Columns.TASKS_NAME + " COLLATE NOCASE";

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
    // we get the data back when the loader calls the onLoadFinished method so this is where we provide the data to the adapter

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        // loop through the cursor and log the data
        Log.d(TAG, "Entering onLoadFinished: ");
        mAdapter.swapCursor(data);

        int count = mAdapter.getItemCount();


        Log.d(TAG, "onLoadFinished: count is " + count);
    }

    // onLoaderReset is called when a previously created loader is being reset and thus making its data unavailable
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: starts");
        // the adapter no longer holds a reference to the cursor and the loader is free to close it
        mAdapter.swapCursor(null);
    }
}
