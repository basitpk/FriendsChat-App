package project.basit.friendschat;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private static int SIGN_IN_REQUEST_CODE=1;
    private ArrayList<String> groups,creaters;
    RelativeLayout activity_main;
    private static DatabaseReference mref;
    private PopupWindow popw;
    private Button exit;
    private EditText user,pass,creater;
    private ListView list;
    private View focusview=null;
    private boolean cancel=false;
    private String username;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.signout:
                AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
               Snackbar.make(activity_main,"Successfully Signed Out!",Snackbar.LENGTH_SHORT).show();
                 finish();
             }
         });
                break;
            case R.id.addgroup:  initiatepopup();
                                  break;
            case R.id.find:  findgrouppop();
                               break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==SIGN_IN_REQUEST_CODE)
        {
            if(resultCode==RESULT_OK)
            {
                Snackbar.make(activity_main,"Successfully logged in",Snackbar.LENGTH_SHORT).show();
                username=FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
                displayUserList();
            }
            else {
                Snackbar.make(activity_main,"Sorry could not log you in",Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        groups=new ArrayList<>();
        creaters=new ArrayList<>();

        mref=FirebaseDatabase.getInstance().getReference().child("groups");
        activity_main=(RelativeLayout)findViewById(R.id.main);
        list=(ListView)findViewById(R.id.grouplist);
        prefs=getSharedPreferences("MyGroups",Context.MODE_PRIVATE);
        editor=prefs.edit();

        int count=prefs.getInt("count",0);
        if(count>0) {
            for (int j = 0; j < count; j++)
            {
                groups.add(prefs.getString("group"+j,""));
                creaters.add(prefs.getString("creater"+j,""));
            }
        }

        ChatGroupList adapter=new ChatGroupList(groups,creaters,this);
        list.setAdapter(adapter);

        if(FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),SIGN_IN_REQUEST_CODE);
        }
        else
        {
            Snackbar.make(activity_main,"Welcome "+FirebaseAuth.getInstance().getCurrentUser().getEmail(),Snackbar.LENGTH_SHORT).show();
            username=FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
            displayUserList();
        }
    }
    private void displayUserList() {

        ChatGroupList adapter=new ChatGroupList(groups,creaters,this);
        list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent=new Intent(MainActivity.this,ChatsActivity.class);
                    if (view == null) {
                        LayoutInflater i=(LayoutInflater)MainActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
                        view = i.inflate(R.layout.group_list_item, null);
                    }

                    TextView group = (TextView) view.findViewById(R.id.group_name);
                    TextView creater=(TextView)view.findViewById(R.id.groupcreater);
                    intent.putExtra("groupname", group.getText().toString());
                    intent.putExtra("creater",creater.getText().toString());
                    startActivity(intent);
                }
            });
    }

    private void initiatepopup()
   {
       LayoutInflater inflater=(LayoutInflater)MainActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
       View view=inflater.inflate(R.layout.screen_popup_addgroup,(ViewGroup)findViewById(R.id.popup));

       popw=new PopupWindow(view,LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,true);
       popw.showAtLocation(view, Gravity.BOTTOM,0,0);
       Button addg=(Button)view.findViewById(R.id.addgroup);
       exit=(Button)view.findViewById(R.id.exit);
       user=(EditText)view.findViewById(R.id.groupname);
       pass=(EditText)view.findViewById(R.id.grouppass);
       user.setError(null);
       pass.setError(null);

       addg.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                       mref.child(username).child(user.getText().toString()).setValue(pass.getText().toString());
                       groups.add(user.getText().toString());
                       creaters.add(username);

                       editor.putString("group"+(groups.size()-1),user.getText().toString());
                       editor.putString("creater"+(groups.size()-1),username);
                       editor.putInt("count",groups.size());
                       editor.commit();
                       popw.dismiss();
           }

       });

       exit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               popw.dismiss();
           }
       });
       displayUserList();
   }

    private void findgrouppop() {
        LayoutInflater inflater=(LayoutInflater)MainActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.screen_popup_find,(ViewGroup)findViewById(R.id.popup));
        popw=new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,true);
        popw.showAtLocation(view,Gravity.BOTTOM,0,0);

        Button find=(Button)view.findViewById(R.id.findgroup);
        exit=(Button)view.findViewById(R.id.exit);
        user=(EditText)view.findViewById(R.id.groupname);
        pass=(EditText)view.findViewById(R.id.grouppass);
        creater=(EditText)view.findViewById(R.id.creater);

        user.setError(null);
        pass.setError(null);
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            focusview=null;
            cancel=false;
             if(TextUtils.isEmpty(user.getText().toString())) {
                    user.setError("empty");
                    focusview=user;
                    cancel = true;
                }

           else if(TextUtils.isEmpty(pass.getText().toString())) {
                 pass.setError("empty");
                 focusview=pass;
                 cancel = true;
             }
              if(cancel){
                  focusview.requestFocus();
              }

             else
                 {
                     mref.child(creater.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild(user.getText().toString())) {
                                String snapshot = dataSnapshot.child(user.getText().toString()).getValue(String.class);
                                if (snapshot.equals(pass.getText().toString())) {
                                    Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                    groups.add(user.getText().toString());
                                    creaters.add(creater.getText().toString());

                                    editor.putString("group"+(groups.size()-1),user.getText().toString());
                                    editor.putString("creater"+(groups.size()-1),creater.getText().toString());
                                    editor.putInt("count",groups.size());
                                    editor.commit();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Failure ", Toast.LENGTH_SHORT).show();
                            }
                            popw.dismiss();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(MainActivity.this, "Failure due to " + databaseError, Toast.LENGTH_SHORT).show();
                            popw.dismiss();
                        }
                    });

                }
            }
        });
        if (cancel){
            focusview.requestFocus();
        }
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popw.dismiss();
            }
        });
     displayUserList();
    }
}
