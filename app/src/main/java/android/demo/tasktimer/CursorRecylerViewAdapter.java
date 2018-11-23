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

class CursorRecylerViewAdapter extends RecyclerView.Adapter<CursorRecylerViewAdapter.TaskViewHolder> {
    // should use the shortcut to create TAG to ensure that the TAG string will be less than 23 characters
    private static final String TAG = "CursorRecyclerViewAdapt";
    // the field that holds the cursor
    private Cursor mCursor;

    public CursorRecylerViewAdapter(Cursor cursor) {
        Log.d(TAG, "CursorRecylerViewAdapter: Constructor called");
        this.mCursor = cursor;
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
            taskViewHolder.name.setText(mCursor.getString(mCursor.getColumnIndex(TasksContract.Columns.TASKS_NAME)));
            taskViewHolder.description.setText(mCursor.getString(mCursor.getColumnIndex(TasksContract.Columns.TASKS_DESCRIPTION)));
            taskViewHolder.editButton.setVisibility(View.VISIBLE); //TODO add onCLick listener
            taskViewHolder.deleteButton.setVisibility(View.VISIBLE); //TODO add onClick listener
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
        final Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if (newCursor != null) {
            //notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            // notify the observers about the lack of a data set
            notifyItemRangeRemoved(0, getItemCount());
        }
        return oldCursor;
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "TaskViewHolder";

        TextView name = null;
        TextView description = null;
        ImageButton editButton = null;
        ImageButton deleteButton = null;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "TaskViewHolder: constructor called");

            this.name = (TextView) itemView.findViewById(R.id.tli_name);
            this.description = (TextView) itemView.findViewById(R.id.tli_description);
            this.editButton = (ImageButton) itemView.findViewById(R.id.tli_edit);
            this.deleteButton = (ImageButton) itemView.findViewById(R.id.tli_delete);
        }
    }
}
