package in.coltify.minievent;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignInActivity extends Activity {

    @BindView(R.id.edt_signin_username) protected EditText userName;
    @BindView(R.id.edt_signin_email) protected EditText userEmail;
    @BindView(R.id.edt_signin_password) protected EditText userPassword;
    @BindView(R.id.btn_signin) protected Button signInUser;
    @BindView(R.id.prog_signin) protected ProgressBar progressBar;

    String email,password,name;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference().child("Users");

    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            finish();
            startActivity(new Intent(SignInActivity.this,EventListActivity.class));
        }
    }

    @OnClick(R.id.btn_signin)
    protected void onSignInClick(){
        signInUser.setClickable(false);

        email = userEmail.getText().toString().trim();
        password = userPassword.getText().toString().trim();
        name = userName.getText().toString().trim();

        if(!validateForm())
            return;
        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            mRef.child(currentUser.getUid()).child("Name").setValue(name);
                            mRef.child(currentUser.getUid()).child("Email").setValue(email);
                            mRef.child(currentUser.getUid()).child("Active").setValue(1);
                            startActivity(new Intent(SignInActivity.this,EventListActivity.class));
                        }else {
                            makeToast("Registration Failed!");
                            signInUser.setClickable(true);
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                });

    }

    private boolean validateForm() {
        boolean flag = true;

        if (TextUtils.isEmpty(name)){
            userName.setError("Required Field");
            flag = false;
        }

        else if (TextUtils.isEmpty(email)){
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
