package com.govt.spm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText etUserId,etPassword;
    private Button btnLogin;
    private SharedPreferences userPref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUserId = (EditText) findViewById(R.id.etUserIdLogin);
        etPassword = (EditText) findViewById(R.id.etPasswordLogin);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        userPref = getSharedPreferences("user",MODE_PRIVATE);
        editor = userPref.edit();

    }
    //Do login when click
    public void doLogin(View view) {
        String userid = etUserId.getText().toString();
        String password = etPassword.getText().toString();

        //start loading
        ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        pd.setMessage("Authenticating...");
        pd.show();

        if(validate()){
            //Here actual login auth - and fetch user role
            //According user role - use intent to move to next activity
            editor.putString("userId",userid);
            editor.putString("role","TPO");
            editor.apply();

            startActivity(new Intent(getBaseContext(),OfficerDashboardActivity.class));
            finish();
        }else{
            pd.dismiss();
        }
    }

    //validating fields
    private boolean validate(){
        String userid = etUserId.getText().toString();
        String password = etPassword.getText().toString();

        if (userid.equals("")) {
            Toast.makeText(this, "Please Enter User Id", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.equals("")) {
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    //For Authentication and fetch user role : ex : student , TPO
    private void Authentication(){

    }

}