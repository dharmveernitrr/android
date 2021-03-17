package com.example.chatpost.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatpost.Adapters.StatusAdapter;
import com.example.chatpost.Models.Status;
import com.example.chatpost.Models.UserStatus;
import com.example.chatpost.Models.Users;
import com.example.chatpost.databinding.FragmentStatusBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatusFragment extends Fragment {

    FragmentStatusBinding binding;

    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseStorage storage;

    ProgressDialog dialog;

    Users users; // here we have to take the current user so that we can have the name and the image
    // of the current user..


    public StatusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentStatusBinding.inflate(inflater, container, false);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);

        dialog.setTitle("Uploading...");

        final ArrayList<UserStatus> userStatusList = new ArrayList<>();


        StatusAdapter statusAdapter = new StatusAdapter(getContext() , userStatusList);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rvUsersStatus.setLayoutManager(layoutManager);
        binding.rvUsersStatus.setHasFixedSize(true);

        binding.rvUsersStatus.setAdapter(statusAdapter);
        binding.rvUsersStatus.showShimmerAdapter();


        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        users = snapshot.getValue(Users.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // now here we have to perform our task and have to add the status for the users..

        // now we have to take the list and have to fill it..

        // here lets take the profile image of the user for testing purposes..

        binding.uploadStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // now we have to upload statues to firebase..
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 50);
            }
        });

        // make a list for getting the name of the users..

        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userStatusList.clear();
                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        UserStatus userStatus = new UserStatus();
                        userStatus.setName(dataSnapshot.child("userName").getValue(String.class));
                        userStatus.setProfileImage(dataSnapshot.child("userProfile").getValue(String.class));
                        userStatus.setLastUpdated(dataSnapshot.child("lastUpdated").getValue(Long.class));

                        ArrayList<Status> list = new ArrayList<>();
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.child("statues").getChildren())
                        {
                            Status status = dataSnapshot1.getValue(Status.class);
                            list.add(status);
                        }

                        userStatus.setStatuses(list);
                        userStatusList.add(userStatus);

                    }
                    binding.rvUsersStatus.hideShimmerAdapter();
                    statusAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null && data.getData() != null)
        {
            dialog.show();
            // now we have to update the image to the statues..
            Uri imageUri = data.getData();
            Date date = new Date();
            // now we have to this image to the database..

            String randomKey = database.getReference().push().getKey();
            StorageReference reference = storage.getReference().child("statuesImages").child(randomKey);

            reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            dialog.dismiss();

                            UserStatus userStatus = new UserStatus();
                            userStatus.setName(users.getUserName());
                            userStatus.setProfileImage(users.getProfilePicture());
                            userStatus.setLastUpdated(date.getTime());

                            HashMap<String , Object> hashMap = new HashMap<>();
                            hashMap.put("userName" , userStatus.getName());
                            hashMap.put("userProfile" , userStatus.getProfileImage());
                            hashMap.put("lastUpdated" , userStatus.getLastUpdated());

                            Status status = new Status(uri.toString() , userStatus.getLastUpdated());

                            database.getReference().child("stories")
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .updateChildren(hashMap);

                            database.getReference().child("stories")
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .child("statues")
                                    .push()
                                    .setValue(status);


                        }
                    });
                }
            });

        }

    }
}