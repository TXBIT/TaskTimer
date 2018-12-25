package android.demo.tasktimer;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

public class AddEditActivity extends AppCompatActivity implements AddEditActivityFragment.OnSaveClicked,
        AppDialog.DialogEvents {
    private static final String TAG = "AddEditActivity";
    public static final int DIALOG_ID_CANCEL_EDIT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        AddEditActivityFragment fragment = new AddEditActivityFragment();

//        Bundle arguments = new Bundle();
        Bundle arguments = getIntent().getExtras();
        // add the task to a bundle
        // and add the bundle to the fragments arguments
//        arguments.putSerializable(Task.class.getSimpleName(), task);
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
        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected: home  button pressed");
                AddEditActivityFragment fragment = (AddEditActivityFragment)
                        getSupportFragmentManager().findFragmentById(R.id.fragment);
                if (fragment.canClose()) {
                    return super.onOptionsItemSelected(item);
                } else {
                    showConfirmationDialog();
                    return true; //indicate we are handling this
                }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onSaveClicked() {
        finish();
    }

    @Override
    public void onPositiveDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onPositiveDialogResult: called");
    }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onNegativeDialogResult: called");
        finish();
    }

    @Override
    public void onDialogCancelled(int dialogId) {
        Log.d(TAG, "onDialogCancelled: called");
    }

    private void showConfirmationDialog() {
        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_CANCEL_EDIT);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.cancelEditDiag_message));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.cancelEditDiag_positive_caption);
        args.putInt(AppDialog.DIALOG_NEGATIVE_RID, R.string.cancelEditDiag_negative_caption);

        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called");
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddEditActivityFragment fragment = (AddEditActivityFragment) fragmentManager.findFragmentById(R.id.fragment);
        if (fragment.canClose()) {
            super.onBackPressed();
        } else {
//             show dialogue to get confirmation to quite editing
            showConfirmationDialog();
        }
    }
}
