package android.demo.tasktimer;

import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AppDialog extends AppCompatDialogFragment {
    public static final String TAG = "AppDialog";
    public static final String DIALOG_ID = "id";
    public static final String DIALOG_MESSAGE = "message";
    public static final String DIALOG_POSITIVE_RID = "positive_rid";
    public static final String DIALOG_NEGATIVE_RID = "negative_rid";

    /**
     * The dialog's callback interface to notify of user selected results (deletion confirmed, etc)
     */
    interface DialogEvents {
        void onPositiveDialogResult(int dialogId, Bundle args);

        void onNegativeDialogResult(int dialogId, Bundle args);

        void onDialogCancelled(int dialogId);

    }

    private DialogEvents mDialogEvents;

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach: Entering onAttach, activity is " + context.toString());
        super.onAttach(context);


//        Activities containing this fragment must implement its callback
        if (!(context instanceof DialogEvents)) {
            throw new ClassCastException(context.toString() + " must implement AppDialog.DialogEvents interface");

        }
        mDialogEvents = (DialogEvents) context;

    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: Entering....");
        super.onDetach();

//        Reset the active callback interface, because we don't have an activity any longer
        mDialogEvents = null;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog: starts");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final Bundle arguments = getArguments();
        final int dialogId;
        String messageString;
        // we are retrieving int values, resource IDs for the positive and negative button captions
        // we should store all strings that users will see in string resources
        // so consequently we are passing the string resource IDs in our bundle, instead of the string themselves
        int positiveStringId;
        int negativeStringId;
// we have created the constants that we use as keys for retrieving the values from the bundle
        // as long as there is a bundle (arguments != null), we can retrieve the values

        if (arguments != null) {
            dialogId = arguments.getInt(DIALOG_ID); // return 0 if DIALOG_ID is missing
            messageString = arguments.getString(DIALOG_MESSAGE); // return null if DIALOG_MESSAGE is missing
            // the positive and negative buttons IDs are optional but the dialog ID and message aren't
            // we should check and raise an exception if the ID and the message are not provided in the bundle
            if (dialogId == 0 || messageString == null) {
                throw new IllegalArgumentException("DIALOG_ID and/or DIALOG_MESSAGE not present in the bundle");

            }
            // the text for the buttons are made optional
            // if text is not provided for the two buttons, they are default to showing OK and Cancel
            positiveStringId = arguments.getInt(DIALOG_POSITIVE_RID);


            if (positiveStringId == 0) {
                positiveStringId = R.string.ok;

            }
            negativeStringId = arguments.getInt(DIALOG_NEGATIVE_RID);
            if (negativeStringId == 0) {
                negativeStringId = R.string.cancel;

            }
        } else {
            // our dialog fragment relies on being provided with at least a message to display
            // and our callback interface requires a dialog id
            // so if the arguments are null, we are going to raise an exception for the above reasons
            throw new IllegalArgumentException("Must pass DIALOG_ID and DIALOG_NESSAGE in the bundle");

        }

// the setMessage, setPositiveButton, setNegativeButton methods can accept either string or int arguments
        // we do not pass the resource IDs for the messageString
        // because that may have to be built up by combining a resource string with some other values such as the task name or task ID
        // our dialog does not know anything about tasks
        // so we have to let the calling program extract the string resource and then add any extra values that it needs
        builder.setMessage(messageString)
                .setPositiveButton(positiveStringId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

//                        callback positive result method
                        if (mDialogEvents != null) {
                            mDialogEvents.onPositiveDialogResult(dialogId, arguments);

                        }

                    }
                })
                .setNegativeButton(negativeStringId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        callback negative result method
                        if (mDialogEvents != null) {
                            mDialogEvents.onNegativeDialogResult(dialogId, arguments);
                        }

                    }
                });

        return builder.create();
    }


    // if a user dismisses the dialog by using the Back button
    // neither of our callback methods will be called
    // instead, Android will call the onCancel method in the DialogFragment class,
    // and it also calls the onDismiss method
    // as a result, we have to override onCancel and onDismiss

    // we removed the super.onCancel(dialog) but we left the super.onDismiss(dialog) in the overridden methods
    // super.onCancel(dialog) does not actually do anything
    // if we remove super.onDismiss(dialog), the dialog may reappear when the device is rotated
    @Override
    public void onCancel(DialogInterface dialog) {

        Log.d(TAG, "onCancel: called");
        // we callback our listener using its onDialogCancelled method
        // we don't pass the arguments bundle back from the onDialogCancelled method because it will not be useful
        // the bundle is still available using the getArguments method
        // we don't check if the DIALOG_ID is present in the bundle nor check if there is a bundle
        // because they were checked when our dialog fragment was created
        // if the bundle doesn't exist or doesn't contain an ID
        // the program will crash before we can get to this point in the code
        if (mDialogEvents != null) {
            int dialogId = getArguments().getInt(DIALOG_ID);
            mDialogEvents.onDialogCancelled(dialogId);
        }
    }

    // onDismiss is called when the dialog's dismissed, even if it was dismissed as a result of being cancelled
    // onCancel is only called when the dialog is cancelled by tapping the cancel button or tapping outside the dialog or using the back button


    // Fragment Lifecycle callback events - added for practice
    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        Log.d(TAG, "onInflate: called");
        super.onInflate(context, attrs, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: called");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.d(TAG, "onHiddenChanged: called");
        super.onHiddenChanged(hidden);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: called");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: called");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: called");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        Log.d(TAG, "onViewStateRestored: called");
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: called");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: called");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: called");
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: called");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop: called");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView: called");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: called");
        super.onDestroy();
    }

}
