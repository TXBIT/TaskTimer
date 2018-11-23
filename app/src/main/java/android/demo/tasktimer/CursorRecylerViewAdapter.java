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
        if ((mCursor == null) || (mCursor.getCount() == 0)) {
            Log.d(TAG, "onBindViewHolder: providing instructions");
            taskViewHolder.name.setText("Instructions");
            taskViewHolder.description.setText("Use the add button (+) in the toolbar above to create new tasks." +
                    "\n\nTasks with lower sort order will be placed higher up the list." +
                    "Tasks with the same sort order will be sorted alphabetically" +
                    "\n\nTapping a task will start the timer for that task (and will stop the timer for any previous task that was being timed." +
                    "\n\nEach task has Edit and Delete buttons if you want to change the details or remove the task.");
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

    @Override
    public int getItemCount() {
        return 0;
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
