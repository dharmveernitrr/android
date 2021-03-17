package com.example.chatpost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.chatpost.Adapters.FragmentsAdapter;
import com.example.chatpost.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements LifecycleObserver {

    FirebaseAuth auth;

    FirebaseDatabase database;
    ActivityMainBinding binding;

    private ViewPager viewPager;
    private TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        binding.viewpager.setAdapter(new FragmentsAdapter(getSupportFragmentManager()));
        binding.tabLayout.setupWithViewPager(binding.viewpager);



    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onAppBackgrounded() {
        HashMap<String, Object> hashMap =  new HashMap<>();
        hashMap.put("user_offline_online_status" , "offline");

        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                .updateChildren(hashMap);

        Log.d("MyApp", "App in background");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onAppForegrounded() {
        HashMap<String, Object> hashMap =  new HashMap<>();
        hashMap.put("user_offline_online_status" , "online");
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                .updateChildren(hashMap);
        Log.d("MyApp", "App in foreground");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch(id)
        {
            case R.id.action_log_out:
                HashMap<String, Object> hashMap =  new HashMap<>();
                hashMap.put("user_offline_online_status" , "offline");

                database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                        .updateChildren(hashMap);
                auth.signOut();

                // and also from here we have to move to sign in activity..
                Intent intent = new Intent(MainActivity.this , SignInActivity.class);
                startActivity(intent);

                finish();
                break;
            case R.id.action_settings:
                // perform and check the settings..
                Intent intent1 = new Intent(MainActivity.this , SettingsActivity.class);
                startActivity(intent1);
                break;
            case R.id.action_room_chat:
                // here we have to open another intent for group chats..
                Intent intent2 = new Intent(MainActivity.this , GroupChatActivity.class);
                startActivity(intent2);

        }
        return super.onOptionsItemSelected(item);
    }
}