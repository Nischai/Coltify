package in.coltify.minievent;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddEventActivity extends AppCompatActivity {

    @BindView(R.id.edt_date) public EditText date;
    @BindView(R.id.edt_time) public EditText time;
    @BindView(R.id.edt_event_name) public EditText eventName;
    @BindView(R.id.edt_location) public EditText location;
    @BindView(R.id.edt_mobile_number) public EditText mobileNumber;
    @BindView(R.id.edt_description) public EditText description;
    @BindView(R.id.imb_poster) public ImageButton poster;

    private Uri posterUri = null;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;

    public static final String T = "TIME", D ="DATE";
    final Calendar cal = Calendar.getInstance();
    private static final int GALLERY_REQUEST = 1;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        ButterKnife.bind(this);

        date = (EditText)findViewById(R.id.edt_date);
        time = (EditText)findViewById(R.id.edt_time);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Events");
        progressDialog = new ProgressDialog(this);

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                cal.set(Calendar.YEAR, i);
                cal.set(Calendar.MONTH, i1);
                cal.set(Calendar.DAY_OF_MONTH, i2);
                updateView(D);
            }
        };

        final TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener(){

            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                cal.set(Calendar.HOUR,i);
                cal.set(Calendar.MINUTE,i1);
                updateView(T);
            }
        };

        date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    new DatePickerDialog(AddEventActivity.this,dateSetListener,
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        time.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    new TimePickerDialog(AddEventActivity.this,
                            timeSetListener,
                            cal.get(Calendar.HOUR),
                            cal.get(Calendar.MINUTE),
                            false).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            posterUri = data.getData();
            poster.setImageURI(posterUri);
        }else
            Toast.makeText(AddEventActivity.this,"Failed",Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.imb_poster)
    public void onPosterImageClick(){

        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery,GALLERY_REQUEST);
    }

    @OnClick(R.id.btn_create_event)
    public void onCreateEventClick(){
        progressDialog.setMessage("Loading");
        progressDialog.show();
        if (!validateEventForm())
            return ;
        StorageReference filePath = mStorageRef.child("Event_Posters").child(posterUri.getLastPathSegment());
        final DatabaseReference newEvent = mDatabaseRef.push();
        final FirebaseUser user = mAuth.getCurrentUser();
        filePath.putFile(posterUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri downloadUri = taskSnapshot.getDownloadUrl();

                newEvent.child("Name").setValue(eventName.getText().toString().trim());
                newEvent.child("Location").setValue(location.getText().toString().trim());
                newEvent.child("Date").setValue(date.getText().toString().trim());
                newEvent.child("Time").setValue(time.getText().toString().trim());
                newEvent.child("Mobile").setValue(mobileNumber.getText().toString().trim());
                newEvent.child("Description").setValue(description.getText().toString().trim());
                newEvent.child("PosterUri").setValue(downloadUri.toString());
                newEvent.child("Uid").setValue(user.getUid());

                startActivity(new Intent(AddEventActivity.this,EventListActivity.class));
                progressDialog.dismiss();
            }
        });
    }

    private void updateView(String s) {

        if (s.equals(T)){
            String format = "hh:mm aaa";
            SimpleDateFormat timeFormat = new SimpleDateFormat(format,Locale.US);
            time.setText(timeFormat.format(cal.getTime()));

        }else{
            String format = "dd/mm/yyyy";
            SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
            date.setText(dateFormat.format(cal.getTime()));
        }
    }

    public boolean validateEventForm(){
        boolean flag = true;
        if (TextUtils.isEmpty( eventName.getText().toString())){
            eventName.setError("Required Field");
            flag = false;
        }else if(TextUtils.isEmpty( location.getText().toString())){
            location.setError("Required Field");
            flag = false;
        }else if (TextUtils.isEmpty( date.getText().toString())){
            date.setError("Required Field");
            flag = false;
        }else if (TextUtils.isEmpty( time.getText().toString())){
            time.setError("Required Field");
            flag = false;
        }else if (TextUtils.isEmpty( mobileNumber.getText().toString()) ||
                mobileNumber.getText().toString().length() < 10){
            mobileNumber.setError("Enter a valid mobile number");
            flag = false;
        }else if (TextUtils.isEmpty( description.getText().toString())){
            description.setError("Required Field");
            flag = false;
        }else if (cal.after(date.getText().toString())){
            eventName.setError("Invalid Date");
            flag = false;
        }else if (posterUri == null){
            Toast.makeText(AddEventActivity.this,"Image Required",Toast.LENGTH_SHORT).show();
            flag = false;
        }

        return flag;
    }
}
