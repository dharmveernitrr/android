package com.example.chatpost.Fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatpost.Adapters.UsersAdapter;
import com.example.chatpost.Models.Users;
import com.example.chatpost.databinding.FragmentChatsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatsFragment extends Fragment implements LifecycleObserver {


    FragmentChatsBinding binding;
    FirebaseDatabase database;
    ArrayList<Users> arrayList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(inflater,container,false);

        database = FirebaseDatabase.getInstance();
        // now we have to perform all our work here..

        UsersAdapter adapter = new UsersAdapter(getContext(),arrayList);

        binding.rvUsersContactList.setAdapter(adapter);

        binding.rvUsersContactList.showShimmerAdapter();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rvUsersContactList.setLayoutManager(layoutManager);
        binding.rvUsersContactList.setHasFixedSize(true);

        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if(!(Objects.equals(FirebaseAuth.getInstance().getUid(), dataSnapshot.getKey()))) {
                        Users users = dataSnapshot.getValue(Users.class);
                        users.setUserId(dataSnapshot.getKey());
                        arrayList.add(users);
                    }
                }
                binding.rvUsersContactList.hideShimmerAdapter();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return binding.getRoot();
    }

}