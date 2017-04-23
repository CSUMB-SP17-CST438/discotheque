package edu.jocruzcsumb.discotheque;

import android.app.DialogFragment;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

/**
 * Created by Admin on 4/20/2017.
 */


public class CreateFloorDialogFragment extends DialogFragment implements View.OnClickListener, Emitter.Listener {

    private static final String TAG = "FloorDialogFragment";
    private static final String EVENT_CREATE_FLOOR = "create floor";
    private static final String FLOOR_NAME = "floor_name";
    private static final String MEMBER_ID = "member_id";
    private static final String FLOOR_GENRE = "floor_genre";
    private static final String DIALOG_TITLE = "Create Floor";
    private static final String IS_PUBLIC = "is_public";
    private static final String EVENT_ERROR_MESSAGE = "error";
    private static final String EVENT_FLOOR_CREATE = "floor create";
    private EditText editFloorName;
    private Button cancelButton;
    private Button createFloorButton;
    private Spinner splitSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_create_floor, container, false);
        createFloorButton = (Button) rootView.findViewById(R.id.create_floor_button);
        cancelButton = (Button) rootView.findViewById(R.id.cancel_floor_button);
        editFloorName = (EditText) rootView.findViewById(R.id.edit_floor_name);
        splitSpinner = (Spinner) rootView.findViewById(R.id.splitSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.genre_array_list,
                android.R.layout.simple_spinner_item);
        splitSpinner.setAdapter(adapter);
        splitSpinner.setSelection(0);
        createFloorButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        getDialog().setTitle(DIALOG_TITLE);
        Sockets.getSocket().on(EVENT_ERROR_MESSAGE, this);
        Sockets.getSocket().on(EVENT_FLOOR_CREATE, this);

        return rootView;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_floor_button:
                getDialog().dismiss();
                break;
            case R.id.create_floor_button:
                String floorname = editFloorName.getText().toString();
                int position = splitSpinner.getSelectedItemPosition();
                String selectedText = (String) splitSpinner.getSelectedItem();
                Log.d(TAG, String.valueOf(position));
                Log.d(TAG, selectedText);
                if (floorname.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter a floor name", Toast.LENGTH_SHORT).show();
                }
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(FLOOR_NAME, floorname);
                    jsonObject.put(MEMBER_ID, LocalUser.getCurrentUser().getId());
                    Log.d(TAG, String.valueOf(LocalUser.getCurrentUser().getId()));
                    jsonObject.put(FLOOR_GENRE, selectedText);
                    jsonObject.put(IS_PUBLIC, 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Sockets.getSocket().emit(EVENT_CREATE_FLOOR, jsonObject);
                getDialog().dismiss();
                break;
        }
    }

    @Override
    public void call(Object... args) {
        Log.d(TAG, "Received object: " + args[0]);
        JSONObject jsonObject = (JSONObject) args[0];
        try {
            if (jsonObject.get("Floor name is already taken").equals("Floor name is already taken")) {
                Toast.makeText(getActivity(), "Error! Floor name already exists.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Floor was created sucessfully!", Toast.LENGTH_SHORT).show();
            }
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        }
}


