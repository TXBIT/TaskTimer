package android.demo.tasktimer;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class AddEditActivity extends AppCompatActivity {
    private static final String TAG = "AddEditActivity";

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

}
