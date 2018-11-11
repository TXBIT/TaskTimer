package android.demo.tasktimer;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // We specify the columns we want to see in the query by creating a string array listing them.
        // Next we get a reference to the content resolver and we call its query method
        // Because we use the uri for our tasks table,
        // the content resolver knows that it has to extract the authority from the uri to work out which provider to use
        // and then it passes the entire uri on to our provider to run the query
        // As long as the cursor returned by the query is not null, we will loop through all the rows
        // The code loops through all the columns in the cursor and logs the name and value of each one
        String[] projections = {TasksContract.Columns.TASKS_NAME, TasksContract.Columns.TASKS_DESCRIPTION};
        ContentResolver contentResolver = getContentResolver();
        // sort by name alphabetically
        // Cursor cursor = contentResolver.query(TasksContract.CONTENT_URI, projections, null, null, TasksContract.Columns.TASKS_NAME);
        // sort by sortOrder column
        // Cursor cursor = contentResolver.query(TasksContract.CONTENT_URI, projections, null, null, TasksContract.Columns.TASKS_SORTORDER);

        // return the row with ID 1
        Cursor cursor = contentResolver.query(TasksContract.buildTaskUri(1), projections, null, null, TasksContract.Columns.TASKS_NAME);
        if (cursor != null) {
            Log.d(TAG, "onCreate: number of rows: " + cursor.getCount());
            while (cursor.moveToNext()) {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    Log.d(TAG, "onCreate: " + cursor.getColumnName(i) + ": " + cursor.getString(i));
                }
                Log.d(TAG, "onCreate: =================================");
            }
            cursor.close();;
        }

//        AppDatabase appDatabase = AppDatabase.getInstance(this);
//        final SQLiteDatabase db = appDatabase.getReadableDatabase();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
