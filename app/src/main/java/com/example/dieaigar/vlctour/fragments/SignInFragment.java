package com.example.dieaigar.vlctour.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dieaigar.vlctour.MainActivity;
import com.example.dieaigar.vlctour.R;
import com.example.dieaigar.vlctour.pojos.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Arch on 4/23/2016.
 */
public class SignInFragment extends Fragment{

    private EditText etUsername;
    private EditText etPassword;

    private String username;
    private String password;

    public SignInFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_signin, container, false);
        getActivity().setTitle(getString(R.string.sign_in_title));

        etUsername = (EditText) rootView.findViewById(R.id.etUsernameLogin);
        etPassword = (EditText) rootView.findViewById(R.id.etPasswordLogin);

        Button button = (Button) rootView.findViewById(R.id.btLogin);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                loginPressed();
            }
        });

        TextView textView = (TextView) rootView.findViewById(R.id.tvToSignUp);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToSignUp();
            }
        });

        return rootView;

    }

    public void loginPressed(){
        
        username = etUsername.getText().toString();
        password = etPassword.getText().toString();
        
        if (username.equals("") || password.equals(""))
            Toast.makeText(getActivity(), R.string.missing_fields, Toast.LENGTH_SHORT).show();
        
        else{
            Toast.makeText(getActivity(), R.string.login_in, Toast.LENGTH_SHORT).show();
            User user = new User(username, "", password);
            new LoginAsyncTask().execute(user);
        }
        

    }

    private void displayResult(boolean valid){
        if(valid){
            Toast.makeText(getActivity(), R.string.logged_in, Toast.LENGTH_SHORT).show();
            ((MainActivity)getActivity()).login(username, "");
            getActivity().onBackPressed();
            InputMethodManager inputManager = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

        else
            Toast.makeText(getActivity(), R.string.invalid_username_password, Toast.LENGTH_SHORT).show();
    }



    public void switchToSignUp(){
        Fragment fragment = new SignUpFragment();

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

    }

    private class LoginAsyncTask extends AsyncTask<User, Void, Boolean> {

        @Override
        protected Boolean doInBackground(User... params) {
            boolean confirmation = false;

            if(isNetworkConnected()){
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http");
                builder.authority("84.126.81.68");
                builder.appendPath("login");
                builder.appendQueryParameter("user", params[0].getUsername());
                builder.appendQueryParameter("pass", params[0].getPassword());
                try {
                    URL url = new URL(builder.build().toString());
                    Log.d("WS DEBUG", "Request: " + url.toString());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    Log.d("WS DEBUG", "Response code: " + connection.getResponseCode());
                    BufferedReader reader = null;

                    if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder sBuilder = new StringBuilder();
                        String s;
                        while ((s = reader.readLine()) != null) {
                            sBuilder.append(s);
                        }

                        Log.d("WS DEBUG", "Response message: " + sBuilder.toString());
                        JSONObject jsonObj = new JSONObject(sBuilder.toString());

                        confirmation = Boolean.parseBoolean(jsonObj.get("valid").toString());
                        Log.d("WS DEBUG", "Validation:"  + confirmation);
                    }




                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return confirmation;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            displayResult(aBoolean);


        }

        public boolean isNetworkConnected() {
            // Get a reference to the ConnectivityManager
            ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
            // Get information about the default active data network
            NetworkInfo info = manager.getActiveNetworkInfo();
            // There will be connectivity when there is a default connected network
            return ((info != null) && (info.isConnected()));
        }
    }

}
