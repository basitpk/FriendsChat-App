package project.basit.friendschat;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static project.basit.friendschat.R.id.creater;


public class ChatsActivity extends AppCompatActivity {

    private DatabaseReference ref;
    private FloatingActionButton fab;
    private FirebaseListAdapter<Chatmessage> adapter;
    RelativeLayout layout;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chats_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.link:
            sendEmail();break;
        }
        return true;
    }

    private void sendEmail() {
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:"));
        intent.setType("text/plain");
        String TO[]={""};
        String CC[]={""};
        intent.putExtra(Intent.EXTRA_EMAIL,TO);
        intent.putExtra(Intent.EXTRA_CC,CC);
        intent.putExtra(Intent.EXTRA_SUBJECT,"YOUR SUBJECT");
        intent.putExtra(Intent.EXTRA_TEXT,"Email message goes here");
        try {
            startActivity(Intent.createChooser(intent,"Send Mail..."));
        }catch (ActivityNotFoundException e)
        {
            Toast.makeText(this,"Cannot Send Mail",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        String group = getIntent().getStringExtra("groupname");
        String creater = getIntent().getStringExtra("creater");
        String myself=FirebaseAuth.getInstance().getCurrentUser().getEmail();
        ref = FirebaseDatabase.getInstance().getReference().child("chats").child(creater).child(group);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = (EditText) findViewById(R.id.input);
                ref.push().setValue(new Chatmessage(input.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                input.setText("");

            }
        });
        displayMessage();
    }

    private void displayMessage() {
        ListView list = (ListView) findViewById(R.id.list_of_message);
        final String myself=FirebaseAuth.getInstance().getCurrentUser().getEmail();
        adapter=new FirebaseListAdapter<Chatmessage>(this,Chatmessage.class,R.layout.list_item,ref.limitToLast(10)) {
            @Override
            protected void populateView(View v, Chatmessage model, int position) {
                TextView text, user, time;

                layout = (RelativeLayout)v.findViewById(R.id.changer);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();

                text = (TextView) v.findViewById(R.id.meesage_text);
                user = (TextView) v.findViewById(R.id.meesage_user);
                time = (TextView) v.findViewById(R.id.meesage_time);

                text.setText(model.getMessage_Text());
                user.setText(model.getMessage_User().split("@")[0]);
                time.setText(DateFormat.format("dd-MM-yy (HH:mm) a", model.getMessage_Time()));
                if(model.getMessage_User().equals(myself)) {
                    layout.setBackgroundResource(R.drawable.round_rect_me);
                    params.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
                }
                else
                {
                    layout.setBackgroundResource(R.drawable.round_rect);
                    params.removeRule(RelativeLayout.ALIGN_PARENT_END);

                }
                layout.setLayoutParams(params);
            }
        };
        list.setAdapter(adapter);
    }
}
