package android.demo.tasktimer;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

class CursorRecyclerViewAdapter extends RecyclerView.Adapter<CursorRecyclerViewAdapter.TaskViewHolder> {
    // should use the shortcut to create TAG to ensure that the TAG string will be less than 23 characters
    private static final String TAG = "CursorRecyclerViewAdapt";
    // the field that holds the cursor
    private Cursor mCursor;
    private OnTaskClickListener mListener;

    interface OnTaskClickListener {
        void onEditClick(@NonNull Task task);

        void onDeleteClick(@NonNull Task task);

        void onTaskLongClick(@NonNull Task task);


    }

    public CursorRecyclerViewAdapter(Cursor cursor, OnTaskClickListener listener) {
        Log.d(TAG, "CursorRecyclerViewAdapter: Constructor called");
        mCursor = cursor;
        mListener = listener;
    }


    // onCreateViewHolder is called when the RecyclerView needs a new View to display
    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.task_list_items, viewGroup, false);
        return new TaskViewHolder(view);
    }

    // onBindViewHolder is called when the RecyclerView wants new data to be displayed
    // the RecyclerView first requests a new View and then sends that View back so that we can put data into it
    @Override
    public void onBindViewHolder(TaskViewHolder taskViewHolder, int position) {
        Log.d(TAG, "onBindViewHolder: starts");
        // if the cursor is null, display the instructions
        // else display the data
        if ((mCursor == null) || (mCursor.getCount() == 0)) {
            Log.d(TAG, "onBindViewHolder: providing instructions");
            taskViewHolder.name.setText(R.string.instructions_heading);
            taskViewHolder.description.setText(R.string.instructions);
            // View.GONE is similar to View.INVISIBLE but the widget does not take any space in the layout
            // that allows the description TextView widget to fill the entire width of the display
            taskViewHolder.editButton.setVisibility(View.GONE);
            taskViewHolder.deleteButton.setVisibility(View.GONE);
        } else {
            if (!mCursor.moveToPosition(position)) {
                throw new IllegalStateException("Couldn't move cursor to position" + position);

            }
            final Task task = new Task(mCursor.getLong(mCursor.getColumnIndex(TasksContract.Columns._ID)),
                    mCursor.getString(mCursor.getColumnIndex(TasksContract.Columns.TASKS_NAME)),
                    mCursor.getString(mCursor.getColumnIndex(TasksContract.Columns.TASKS_DESCRIPTION)),
                    mCursor.getInt(mCursor.getColumnIndex(TasksContract.Columns.TASKS_SORTORDER)));

//            final Task task = null;
            taskViewHolder.name.setText(task.getName());
            taskViewHolder.description.setText(task.getDescription());
            taskViewHolder.editButton.setVisibility(View.VISIBLE);
            taskViewHolder.deleteButton.setVisibility(View.VISIBLE);
            View.OnClickListener buttonlistener = new View.OnClickListener() {
                // onclickListener will call the appropriate methods in our mListener object when one of the buttons is clicked.
//                the mListener could be null, so we should check that before attempting to call one of the methods
//                the button calls back this cursor recycler view adapter class when the button is tapped
//                and this class will then call back the activity or the fragment passing the task that needs to be edited or deleted
//                so the activity or the fragment can take care of editing or deleting the task.
//                the CursorRecyclerAdapter is created by our MainActivityFragment, and MainActivityFragment also owns the layout that's display our RecyclerView
//                However, the Fragment does not know anything about the AddEditActivityFragment that we'll be using to edit the Task details.
//                Fragments are managed by Activities, and a Fragment can always get a reference to the Activity that it's attached to.
//                Getting Fragments to create other Fragments is not a good practice.
//                So the editing and deleting will be initiated by MainActivity.
//                If main activity is going to be called back when the button is tapped, it needs to implement the onTaskClickListener interface
//
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: starts");
                    switch (view.getId()) {
                        case R.id.tli_edit:
                            if (mListener != null) {
                                mListener.onEditClick(task);
                            }
                            break;

                        case R.id.tli_delete:
                            if (mListener != null) {
                                mListener.onDeleteClick(task);
                            }
                            break;

                        default:
                            Log.d(TAG, "onClick: found unexpected id");
                    }


                }
            };

            View.OnLongClickListener buttonLongListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.d(TAG, "onLongClick: starts");
                    if(mListener != null){
                        mListener.onTaskLongClick(task);
                        return true;
                    }
                    return false;
                }
            };

            taskViewHolder.editButton.setOnClickListener(buttonlistener);
            taskViewHolder.deleteButton.setOnClickListener(buttonlistener);
            taskViewHolder.itemView.setOnLongClickListener(buttonLongListener);
        }
    }

    // the RecyclerView uses getItemCount to know how many items there are to display
    // if there are no items, we will send back a View to display the instructions
    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: starts");
        if ((mCursor == null) || (mCursor.getCount()) == 0) {
            return 1; //fib, because we populate a single ViewHolder with instructions
        } else {
            return mCursor.getCount();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.
     * The returned old Cursor is <em>not</em> closed.
     * <p>
     * This is a boilerplate code. It should be called whenever the cursor that the adapters using is changed.
     * e.g. It will be called in the MainActivityFragment when we provide a valid cursor for the first time and again when the loader resets
     *
     * @param newCursor The new cursor to be used
     * @return Returns the previously set Cursor, or null if there wasn't one.
     * If the given new Cursor is the same instance as the previously set Cursor, null is also returned.
     */
    Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }

        int numItems = getItemCount();

        final Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if (newCursor != null) {
            //notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            // notify the observers about the lack of a data set
            notifyItemRangeRemoved(0, numItems);
        }
        return oldCursor;
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
//        private static final String TAG = "TaskViewHolder";

        TextView name;
        TextView description;
        ImageButton editButton;
        ImageButton deleteButton;
        View itemView;


        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            this.name = itemView.findViewById(R.id.tli_name);
            this.description = itemView.findViewById(R.id.tli_description);
            this.editButton = itemView.findViewById(R.id.tli_edit);
            this.deleteButton = itemView.findViewById(R.id.tli_delete);
            this.itemView = itemView;

        }
    }
}
