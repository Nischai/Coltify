package in.coltify.minievent;

import android.content.Context;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


public class EventListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mRef;

    FloatingActionButton addEvent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference().child("Events");

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



    public static class EventViewHolder extends RecyclerView.ViewHolder{

        View view;
        ImageButton interest;
        public EventViewHolder(View itemView) {
            super(itemView);
            view = itemView;

            interest = (ImageButton) view.findViewById(R.id.ib_interest);
        }

        public void setName(String name){
            TextView eventName = (TextView) view.findViewById(R.id.tv_event_name);
            eventName.setText(name);
        }
        public void setPoster(Context context, String uri){
            ImageView poster = (ImageView) view.findViewById(R.id.iv_event_poster);
            Picasso.with(context).load(uri).into(poster);
        }
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

        FirebaseRecyclerAdapter<Events,EventViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Events, EventViewHolder>(
                Events.class,
                R.layout.event_row,
                EventViewHolder.class,
                mRef

        ) {
            @Override
            protected void populateViewHolder(EventViewHolder viewHolder, Events model, int position) {
                viewHolder.setName(model.getName());
                viewHolder.setPoster(EventListActivity.this,model.getPosterUri());
                viewHolder.interest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }


}
