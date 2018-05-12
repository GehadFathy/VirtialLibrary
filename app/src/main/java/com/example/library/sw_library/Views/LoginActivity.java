package com.example.library.sw_library.Views;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.library.sw_library.Database.DBHelper;
import com.example.library.sw_library.R;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {

    private Button loginButton;
    private EditText emailView;
    private EditText pwdView;
    private DBHelper dbManager;
    private LoginActivity.UserLoginTask mAuthTask = null;

    public boolean isActive = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.e("LOGIN", "onCreate: ");

        dbManager = DBHelper.getInstance(this);
        emailView = findViewById(R.id.email);
        pwdView = findViewById(R.id.pwd);

        loginButton = findViewById(R.id.signin_button);
        Log.e("LOGIN", "sign in button: "+ loginButton);
        loginButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {

                // Reset errors.
                emailView.setError(null);
                pwdView.setError(null);

                String mail = emailView.getText().toString();
                String pwd = pwdView.getText().toString();

                boolean cancel = false;
                View focusView = null;

                // Check for a valid email address.
                if (TextUtils.isEmpty(mail)) {
                    emailView.setError(getString(R.string.error_field_required));
                    focusView = emailView;
                    cancel = true;
                } else if (!isEmailValid(mail)) {
                    emailView.setError(getString(R.string.error_invalid_email));
                    focusView = emailView;
                    cancel = true;
                }

                // Check for a valid password, if the user entered one.
                if (!cancel && TextUtils.isEmpty(pwd)) {
                    pwdView.setError(getString(R.string.error_field_required));
                    focusView = pwdView;
                    cancel = true;
                }

                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    // Show a progress spinner, and kick off a background task to
                    // perform the user login attempt.
                    mAuthTask = new LoginActivity.UserLoginTask( mail, pwd);
                    mAuthTask.execute((Void) null);
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        isActive = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive = false;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Pair<Boolean, String>> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask( String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Pair<Boolean, String> doInBackground(Void... params) {
            return  new Pair(isPasswordValid(mEmail, mPassword), mEmail);
        }

        @Override
        protected void onPostExecute(final Pair<Boolean, String>  success) {
            mAuthTask = null;
//            showProgress(false);

            if(!success.first) {
                Toast.makeText(LoginActivity.this,"LOG IN FAILED",Toast.LENGTH_SHORT).show();
            }

            Intent intent=new Intent(LoginActivity.this,CategoryViewActivity.class);
            intent.putExtra("admin",success.first);
            intent.putExtra("name",success.second.split("@")[0]);
            startActivity(intent);
            finish();
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
//            showProgress(false);
        }
    }

//    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
//        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
//        ArrayAdapter<String> adapter =
//                new ArrayAdapter<>(LoginActivity.this,
//                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
//
//        emailView.setAdapter(adapter);
//    }

    public boolean isEmailValid(String email) {
        return dbManager.validMail(email);
    }

    public boolean isPasswordValid(String mail, String password) {
        return dbManager.validPW(mail, password);
    }

}

