package android.demo.tasktimer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

//                If main activity is going to be called back when the edit or delete button is tapped, it needs to implement the onTaskClickListener interface in CursorRecylerViewAdapter class
public class MainActivity extends AppCompatActivity implements CursorRecylerViewAdapter.OnTaskClickListener,
                                                               AddEditActivityFragment.OnSaveClicked,
                                                               AppDialog.DialogEvents {

    private static final String TAG = "MainActivity";

    //whether or not this activity is in 2-pane mode
//    i.e running in landscape in tablet
    private boolean mTwoPane = false;


    public static final int DIALOG_ID_DELETE = 1;
public static final int DIALOG_ID_CANCEL_EDIT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // if the layout contains a view with the id task_details_container, set mTwoPane to true
        if (findViewById(R.id.task_details_container) != null) {
//             the details container will be present only in the large-screen layouts (res/values-land and res/values-sw600dp).
//             if this view is present, then the activity should be in two-pane mode.
            mTwoPane = true;
        }

    }

    @Override
    public void onSaveClicked() {
        Log.d(TAG, "onSaveClicked: starts");
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.task_details_container);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            //            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.remove(fragment);
//            fragmentTransaction.commit();
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

    // when the delete button is tapped
//    the onClick listener attached to the button in the CursorRecyclerViewAdapter class calles MainActivity's onDeleteClick
//    we can request confirmation before the content resolver's delete mothod is called
    @Override
    public void onDeleteClick(Task task) {
        Log.d(TAG, "onDeleteClick: starts");
        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();
//        passing the key value pairs into the bundle
        args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_DELETE);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.deldiag_message, task.getId(), task.getName()));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.deldiag_positive_caption);

        args.putLong("TaskId", task.getId());

        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);

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

//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


            // the first parameter is the id for the FrameLayout task_details_container that we are going to put the fragment into
// add => replace because of overlapping
//            fragmentTransaction.replace(R.id.task_details_container, fragment);
//            fragmentTransaction.commit();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.task_details_container, fragment)
                    .commit();
//            getSupportFragmentManager().beginTransaction().replace(R.id.task_details_container, fragment).commit();


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

    @Override
    public void onPositiveDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onPositiveDialogResult: called");
        switch (dialogId){
            case DIALOG_ID_DELETE:
                Long taskId = args.getLong("TaskId");
                // the BuildConfig.DEBUG that Google suggested we use is a system-wide constant that the compiler can use
                // the assert error code will be removed when the released version of the app is compiled
                if(BuildConfig.DEBUG && taskId == 0) throw  new AssertionError("Task id is zero");
                getContentResolver().delete(TasksContract.buildTaskUri(taskId), null, null);
                break;
            case DIALOG_ID_CANCEL_EDIT:
                //no action required
                break;
        }
    }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onNegativeDialogResult: called");
        switch (dialogId){
            case DIALOG_ID_DELETE:
                // no action required
                break;
            case DIALOG_ID_CANCEL_EDIT:
                finish();
                break;
        }

    }
    
    @Override
    public void onDialogCancelled(int dialogId) {
        Log.d(TAG, "onDialogCancelled: called");
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called");
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddEditActivityFragment fragment = (AddEditActivityFragment) fragmentManager.findFragmentById(R.id.task_details_container);
        if((fragment == null)|| fragment.canClose()){
            super.onBackPressed();
        } else {
//             show dialogue to get confirmation to quite editing
            AppDialog dialog = new AppDialog();
            Bundle args = new Bundle();
            args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_CANCEL_EDIT);
            args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.cancelEditDiag_message));
            args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.cancelEditDiag_positive_caption);
            args.putInt(AppDialog.DIALOG_NEGATIVE_RID, R.string.cancelEditDiag_negative_caption);

            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), null);
        }

    }
}
