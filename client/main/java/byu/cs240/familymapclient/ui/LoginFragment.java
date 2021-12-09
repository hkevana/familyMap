package byu.cs240.familymapclient.ui;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import byu.cs240.familymapclient.R;
import byu.cs240.familymapclient.model.DataCache;
import byu.cs240.familymapclient.net.ServerProxy;
import byu.cs240.familymapclient.req.LoginReq;
import byu.cs240.familymapclient.req.DataReq;
import byu.cs240.familymapclient.req.RegisterReq;
import byu.cs240.familymapclient.res.LoginRes;
import byu.cs240.familymapclient.res.DataRes;
import byu.cs240.familymapclient.res.RegisterRes;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";

    private EditText hostNameEditText;
    private EditText portNumEditText;

    private EditText userNameEditText;
    private EditText passwordEditText;
    private EditText emailEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;

    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;

    private Button signInButton;
    private Button registerButton;


    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_login, container, false);

        hostNameEditText = (EditText)v.findViewById(R.id.prompt_hostname);
        hostNameEditText.setOnKeyListener(keyListener);

        portNumEditText = (EditText)v.findViewById(R.id.prompt_port_number);
        portNumEditText.setOnKeyListener(keyListener);

        userNameEditText = (EditText)v.findViewById(R.id.prompt_user_name);
        userNameEditText.setOnKeyListener(keyListener);

        passwordEditText = (EditText)v.findViewById(R.id.prompt_password);
        passwordEditText.setOnKeyListener(keyListener);

        emailEditText = (EditText)v.findViewById(R.id.prompt_email);
        emailEditText.setOnKeyListener(keyListener);

        firstNameEditText = (EditText)v.findViewById(R.id.prompt_first_name);
        firstNameEditText.setOnKeyListener(keyListener);

        lastNameEditText = (EditText)v.findViewById(R.id.prompt_last_name);
        lastNameEditText.setOnKeyListener(keyListener);

        maleRadioButton = (RadioButton)v.findViewById(R.id.radio_male);
        maleRadioButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) { checkBtnEnable(); }
        });

        femaleRadioButton = (RadioButton)v.findViewById(R.id.radio_female);
        femaleRadioButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) { checkBtnEnable(); }
        });

        signInButton = (Button)v.findViewById(R.id.sign_in_button);
        signInButton.setEnabled(false);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { loginUser(); }
        });

        registerButton = (Button)v.findViewById(R.id.register_button);
        registerButton.setEnabled(false);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { registerNewUser(); }
        });

        return v;
    }

    private View.OnKeyListener keyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            checkBtnEnable();
            return false;
        }
    };

    private void checkBtnEnable() {
        Log.d(TAG, "onKey");
        if (!(hostNameEditText.getText().toString().equals("") || portNumEditText.getText().toString().equals("")
                || userNameEditText.getText().toString().equals("") || passwordEditText.getText().toString().equals(""))) {
            signInButton.setEnabled(true);

            if (!(emailEditText.getText().toString().equals("") || firstNameEditText.getText().toString().equals("")
                    || lastNameEditText.getText().toString().equals(""))
                    && (maleRadioButton.isChecked() || femaleRadioButton.isChecked())) {
                registerButton.setEnabled(true);
            } else { registerButton.setEnabled(false); }
        } else {
            registerButton.setEnabled(false);
            signInButton.setEnabled(false);
        }
    }

    private void loginUser() {
        Log.d(TAG, "loginUser()");
        try {
            LoginTask task = new LoginTask();
            task.execute(new LoginReq(userNameEditText.getText().toString(), passwordEditText.getText().toString()));
        } catch(Exception ex) { ex.printStackTrace(); }
    }

    private class LoginTask extends AsyncTask<LoginReq, Void, LoginRes> {

        @Override
        protected LoginRes doInBackground(LoginReq... loginReqs) {
            Log.i(TAG + "::BgTask", loginReqs[0].toString());
            ServerProxy server = ServerProxy.getInstance();
            return server.login(loginReqs[0]);
        }

        @Override
        protected void onPostExecute(LoginRes res) {
            if (res != null) {
                DataReq req =  new DataReq(res.getPersonID(), res.getAuthToken());

                GetDataTask task = new GetDataTask();
                task.execute(req);
            } else {
                makeToast(R.string.invalid_login);
                clearForm();
            }
        }
    }

    private void registerNewUser() {
        Log.d(TAG, "registerNewUser()");
        setConnectionUrl();

        RegisterTask task = new RegisterTask();
        RegisterReq req = getRegReq();

        if (req != null) {
            Log.d(TAG, req.toString());
            task.execute(req);
        }
        else { makeToast(R.string.empty_entry); }
    }

    private class RegisterTask extends AsyncTask<RegisterReq, Void, RegisterRes> {

        @Override
        protected RegisterRes doInBackground(RegisterReq... registerReqs) {
            Log.i(TAG + "::BgTask", registerReqs[0].toString());

            ServerProxy server = ServerProxy.getInstance();
            return server.register(registerReqs[0]);
        }

        @Override
        protected void onPostExecute(RegisterRes res) {
            if (res != null) {
                Log.i(TAG + "::BgTask", res.toString());

                DataReq req = new DataReq(res.getPersonID(), res.getAuthToken());

                GetDataTask task = new GetDataTask();
                task.execute(req);
            } else {
                makeToast(R.string.invalid_credentials);
                clearForm();
            }
        }
    }
    private RegisterReq getRegReq() {
        String username  = userNameEditText.getText().toString();
        String password  = passwordEditText.getText().toString();
        String email     = emailEditText.getText().toString();
        String firstName = firstNameEditText.getText().toString();
        String lastName  = lastNameEditText.getText().toString();
        String gender = null;
        if (maleRadioButton.isChecked()) { gender = "m"; }
        else if (femaleRadioButton.isChecked()) { gender = "f"; }

        // If anything is null, return null
        if (username.equals("") || password.equals("") || email.equals("") || firstName.equals("") || lastName.equals("") || gender == null) {
            return null;
        }
        // Return new user
        return new RegisterReq(username, password, email, firstName, lastName, gender);
    }

    private class GetDataTask extends AsyncTask<DataReq, Void, DataRes> {

        @Override
        protected DataRes doInBackground(DataReq... dataReqs) {
            Log.i(TAG + "::BgTask", dataReqs[0].toString());

            ServerProxy server = ServerProxy.getInstance();
            return server.getData(dataReqs[0]);
        }

        @Override
        protected void onPostExecute(DataRes dataRes) {
            Log.i(TAG + "::post()", dataRes.toString());

            if (dataRes.isSuccess()) {
                storeData(dataRes);

                assert getFragmentManager() != null;
                FragmentTransaction transition = getFragmentManager().beginTransaction();

                MapsFragment mapFrag = new MapsFragment();
                transition.replace(R.id.fragment_container, mapFrag);
                transition.addToBackStack("A");
                transition.commit();
            }
            else { makeToast(R.string.bad_data); }
        }
    }

    private void storeData(DataRes data) {
        DataCache dataStorage = DataCache.getInstance();
        if (dataStorage.storeData(data)) { System.out.println("DATA STORED: " + dataStorage.toString()); }
        else { makeToast(R.string.data_upload_failed); }
    }

    private void clearForm() {
        userNameEditText.setText("");
        passwordEditText.setText("");
    }

    private void makeToast(int message) { Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show(); }

    private void setConnectionUrl() {
        if (!hostNameEditText.getText().toString().equals("")) { ServerProxy.setHostName(hostNameEditText.getText().toString()); }
        if (!portNumEditText.getText().toString().equals("")) { ServerProxy.setPortNumber(portNumEditText.getText().toString()); }
    }
}