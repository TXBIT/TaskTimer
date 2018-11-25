package android.demo.tasktimer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddEditActivityFragment extends Fragment {
    private static final String TAG = "AddEditActivityFragment";

    // keeps track of whether the fragments being used to add or edit
    public enum FragmentEditMode {
        EDIT, ADD
    }

    private FragmentEditMode mMode;

    // get references to all widgets to attach a listener to the button and get the text type into the edit texts
    private EditText mNameTextView;
    private EditText mDescriptionTextView;
    private EditText mSortOrderTextView;
    private Button mSaveButton;
    private OnSaveClicked mSaveListener = null;

    interface OnSaveClicked {
        void onSaveClicked();
    }

    public AddEditActivityFragment() {
        Log.d(TAG, "AddEditActivityFragment: constructor called");
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach: starts");
        super.onAttach(context);
        // activities contain this fragment must implement its callbacks
        Activity activity = getActivity();
        if (!(activity instanceof OnSaveClicked)) {
            throw new ClassCastException(activity.getClass().getSimpleName()
                    + " must implement ActivityFragment.OnSaveClicked interface");
        }
        mSaveListener = (OnSaveClicked) activity;

    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: starts");
        super.onDetach();
        mSaveListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        // variable to store the inflated view
        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);
        // extract the references to the various widgets on the screen and save those in the fields
        mNameTextView = (EditText) view.findViewById(R.id.addedit_name);
        mDescriptionTextView = (EditText) view.findViewById(R.id.addedit_description);
        mSortOrderTextView = (EditText) view.findViewById(R.id.addedit_sortorder);
        mSaveButton = (Button) view.findViewById(R.id.addedit_save);

        // the line to be changed
        // arguments bundle is returned by getExtras
        // that is calling on the intent (getIntent)
        // that started the activity (getActivity)

//        Bundle arguments = getActivity().getIntent().getExtras();


        // we are still retrieving the bundle
        // but we are getting it from the arguments
        // that were set before the fragment was added
        // using the setArguments() method
        Bundle arguments = getArguments();
        // the task to be retrieved

        final Task task;

        // check if the bundle is empty
        // when MainActivity wants to add a new task, it doesn't provide any extras to the intent, in this case arguments will be null
        // even if there are arguments, there's no guarantee that they contain a task so we used the getSerializable method and pass the Task.class.getSimpleName as the key
        // if that doesn't return null, we have a task to edit, we can then initialize the contents of the edit text widgets with that task details
        // if the task was null, we didn't get a task to edit, we just set the mode to ADD
        // the task is made final because we are referring to it in the button onClickListener and an inner class can only access final variables of its enclosing class
        // we have to initialize it to null  otherwise there will be a path through the code that could result in it no been initialized and lead to error

        if (arguments != null) {
            Log.d(TAG, "onCreateView: retrieving task details.");

            task = (Task) arguments.getSerializable(Task.class.getSimpleName());
            // check ig the task is null
            if (task != null) {
                //retrieved a task
                Log.d(TAG, "onCreateView: Task details found, editing...");
                // get values out of the task object that came in by the bundle
                // set the various text properties and set the mode
                mNameTextView.setText(task.getName());
                mDescriptionTextView.setText(task.getDescription());
                mSortOrderTextView.setText(Integer.toString(task.getSortOrder()));
                mMode = FragmentEditMode.EDIT;

            } else {
                //No task, so we must be adding a new task, and not editing an existing one
                mMode = FragmentEditMode.ADD;
            }
        } else {
            task = null;
            Log.d(TAG, "onCreateView: No arguments, adding a new record");
            mMode = FragmentEditMode.ADD;

        }
        // the values in the EditTexts are checked to make sure they changed
        // if the users click the save button without making any changes to the data
        // then there's no point accessing the database to update a record with the same values
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // update the database if at least one field has changed.
                // - There's no need to hit the database unless this has happened.
                int so; //to save repeated conversions to int.
                if (mSortOrderTextView.length() > 0) {
                    // since we use the numeric value of the sort order three times
                    // it is converted to an int to avoid duplicating the conversion code
                    so = Integer.parseInt(mSortOrderTextView.getText().toString());

                } else {
                    so = 0;
                }
                // reference to the content resolver
                // in an activity, we just call the getContentResolver method
                // but in a fragment, we have to get a reference to the activity that the fragments attached to first
                // and then call getContentResolver on that activity reference
                ContentResolver contentResolver = getActivity().getContentResolver();
                ContentValues values = new ContentValues();

                switch (mMode) {
                    case EDIT:
                        // check each edit text value against the original task object that was passed in the bundle
                        // if the value has changed, the new value is added to the values ContentValues
                        // once all the fields have been checked, if there is anything in value
                        // then the data is saved by calling the update method of the contentResolver
                        if (!mNameTextView.getText().toString().equals(task.getName())) {
                            values.put(TasksContract.Columns.TASKS_NAME, mNameTextView.getText().toString());
                        }
                        if (!mDescriptionTextView.getText().toString().equals(task.getDescription())) {
                            values.put(TasksContract.Columns.TASKS_DESCRIPTION, mDescriptionTextView.getText().toString());
                        }
                        if (so != task.getSortOrder()) {
                            values.put(TasksContract.Columns.TASKS_SORTORDER, so);
                        }
                        // if one of the three fields has been changed
                        if (values.size() != 0) {
                            Log.d(TAG, "onClick: updating task");
                            contentResolver.update(TasksContract.buildTaskUri(task.getid()), values, null, null);
                        }
                        break;
                    case ADD:
                        // in the case of adding a new record
                        // we just make sure that the task field name is not blank
                        // as this is the only column that requires a value on the table
                        if (mNameTextView.length() > 0) {
                            Log.d(TAG, "onClick: adding new task");
                            values.put(TasksContract.Columns.TASKS_NAME, mNameTextView.getText().toString());
                            values.put(TasksContract.Columns.TASKS_DESCRIPTION, mDescriptionTextView.getText().toString());
                            values.put(TasksContract.Columns.TASKS_SORTORDER, so);
                            contentResolver.insert(TasksContract.CONTENT_URI, values);
                        }
                        break;
                }
                Log.d(TAG, "onClick: Done editing");
                // check if mSaveListener is null before attempting to call the onSaveClicked method
                // so the app won't crash if there's no activity associated with the fragment
                if(mSaveListener != null){
                    mSaveListener.onSaveClicked();
                }
            }
        });
        Log.d(TAG, "onCreateView: Exiting...");
        return view;
    }
}

