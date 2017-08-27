package in.coltify.minievent;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.OnClick;

public class EventListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    FloatingActionButton addEvent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        recyclerView = (RecyclerView)findViewById(R.id.rec_event_item);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addEvent = (FloatingActionButton)findViewById(R.id.fbtn_add_event);

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EventListActivity.this,AddEventActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_sign_out){
            mAuth.signOut();
            startActivity(new Intent(EventListActivity.this,LoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onStart(){
        super.onStart();

        if (user == null){
            finish();
            startActivity(new Intent(EventListActivity.this,LoginActivity.class));
        }
    }


}
