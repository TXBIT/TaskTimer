package android.demo.tasktimer;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddEditActivityFragment extends Fragment {
    private static final String TAG = "AddEditActivityFragment";

    public AddEditActivityFragment() {
        Log.d(TAG, "AddEditActivityFragment: constructor called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        return inflater.inflate(R.layout.fragment_add_edit, container, false);
    }
}
