package android.demo.tasktimer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
//                If main activity is going to be called back when the edit or delete button is tapped, it needs to implement the onTaskClickListener interface in CursorRecylerViewAdapter class
public class MainActivity extends AppCompatActivity implements CursorRecylerViewAdapter.OnTaskClickListener {
    private static final String TAG = "MainActivity";

    //whether or not this activity is in 2-pane mode
//    i.e running in landscape in tablet
    private boolean mTwoPane = false;

    private static final String ADD_EDIT_FRAGMENT = "AddEditFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // if the layout contains a view with the id task_details_container, set mTwoPane to true
        if(findViewById(R.id.task_details_container)!= null){
//             the details container will be present only in the large-screen layouts (res/values-land and res/values-sw600dp).
//             if this view is present, then the activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.menumain_addTask:
                taskEditRequest(null);
                break;
            case R.id.menumain_showDurations:
                break;
            case R.id.menumain_settings:
                break;
            case R.id.menumain_showAbout:
                break;
            case R.id.menumain_generate:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEditClick(Task task) {
        taskEditRequest(task);
    }

    @Override
    public void onDeleteClick(Task task) {
        getContentResolver().delete(TasksContract.buildTaskUri(task.getid()), null, null);
    }

    private void taskEditRequest(Task task) {
        Log.d(TAG, "taskEditRequest: starts");
        if (mTwoPane) {
            Log.d(TAG, "taskEditRequest: in two-pane mode (tablet)");
            AddEditActivityFragment fragment = new AddEditActivityFragment();

            Bundle arguments = new Bundle();
            // add the task to a bundle
            // and add the bundle to the fragments arguments
            arguments.putSerializable(Task.class.getSimpleName(), task);
            fragment.setArguments(arguments);


            // get a fragment manager then call beginTransaction method
            // receive a fragment transaction object that is used to perform the wanted operations
            // adding, removing, replacing fragments are done through a fragment transaction
            // fragment transaction queues up all changes then performs them once commit is called
            // by that way, everything seems smooth
            // and users will not see gaps appearing when fragments removed and new ones added

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // the first parameter is the id for the FrameLayout task_details_container that we are going to put the fragment into
// add => replace because of overlapping
            fragmentTransaction.replace(R.id.task_details_container, fragment);
            fragmentTransaction.commit();
        } else { // if the app is running in portrait mode, start the AddEditActivity using an Intent
            Log.d(TAG, "taskEditRequest: in single-pane mode (phone)");
            //in single-pane mode, start the detail activity for the selected item Id.

            Intent detailIntent = new Intent(this, AddEditActivity.class);
            //If taskEditRequest is called with a not null task argument, an existing task is to be edited
//            if the task is null, add a new task
            if (task != null) { //editing a task
//                adding the to be edited task to the intent by calling the putExtra method
//                that's why we had to make the Task class serializable in order to pass it around in an intent
                detailIntent.putExtra(Task.class.getSimpleName(), task);
                startActivity(detailIntent);
            } else { //adding a new task
                startActivity(detailIntent);
            }
        }
    }
}
