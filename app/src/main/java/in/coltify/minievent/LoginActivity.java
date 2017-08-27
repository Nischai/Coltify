package in.coltify.minievent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends Activity {

    private FirebaseAuth mAuth;
    @BindView(R.id.edt_login_email) protected EditText userEmail;
    @BindView(R.id.edt_login_password) protected EditText userPassword;
    private ProgressDialog progressDialog;

    private String email,password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

    }


    public void onStart(){
        super.onStart();
        FirebaseUser user =mAuth.getCurrentUser();
        if (user != null){
            finish();
            startActivity(new Intent(LoginActivity.this,EventListActivity.class));
        }
    }


    @OnClick(R.id.btn_login_signin)
    public void startSignInActivity(View view){
        finish();
        startActivity(new Intent(view.getContext(),SignInActivity.class));
    }

    @OnClick(R.id.btn_login)
    public void onLoginClick(){

        email = userEmail.getText().toString().trim();
        password = userPassword.getText().toString().trim();

        if(!validateForm())
            return ;
        progressDialog.setMessage("Loading");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            finish();
                            startActivity(new Intent(LoginActivity.this, EventListActivity.class));
                        } else {
                            makeToast("Login Failed");
                        }

                    }
                });
        progressDialog.dismiss();
    }

    private boolean validateForm() {
        boolean flag = true;

        if (TextUtils.isEmpty(email)){
            userEmail.setError("Required Field");
            flag = false;
        }

        else if (TextUtils.isEmpty(password)){
            userPassword.setError("Required Field");
            flag = false;
        }

        else if(password.length() < 6){
            makeToast("Password must have atleast 6 characters");
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            makeToast("Enter a valid Email Address");
        }

        return flag;
    }

    public void makeToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }
}
