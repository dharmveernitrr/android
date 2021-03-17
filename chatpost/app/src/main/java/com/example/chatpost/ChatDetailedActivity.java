package com.example.chatpost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.chatpost.Adapters.ChatsAdapter;
import com.example.chatpost.Fragments.CallsFragment;
import com.example.chatpost.Fragments.ChatsFragment;
import com.example.chatpost.Models.MessagesModel;
import com.example.chatpost.databinding.ActivityChatDetailedBinding;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class ChatDetailedActivity extends AppCompatActivity {

    ActivityChatDetailedBinding binding;
    String senderId , receiveId;
    ProgressDialog dialog;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // here we have to store the pos in the shared preferences..
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("lastPos" , lastPosition);
        editor.apply();
    }

    FirebaseDatabase database;
    String senderRoom;
    String receiverRoom;
    FirebaseAuth auth;
    FirebaseStorage storage;
    String user_offline_online_status = "abc";
    int lastPosition = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        dialog = new ProgressDialog(this);
        dialog.setTitle("Uploading..");
        dialog.setMessage("uploading image..");
        //Toast.makeText(this, "chats opened..", Toast.LENGTH_SHORT).show();



        getSupportActionBar().hide();


        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        // now we have to take the data from the intent..

        senderId = auth.getUid();

        //Toast.makeText(this, senderId.toString(), Toast.LENGTH_SHORT).show();
        receiveId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String profilePicture = getIntent().getStringExtra("userProfile");
        String user_offline_online_status = getIntent().getStringExtra("user_offline_online_status");

        binding.userState.setText(user_offline_online_status);




        binding.userName.setText(userName);
        Picasso.get().load(profilePicture).placeholder(R.drawable.profile).into(binding.chatsProfileImage);

        binding.backToAllUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });



        senderRoom = senderId + receiveId;
        receiverRoom = receiveId + senderId;

        final ArrayList<MessagesModel> messagesModels = new ArrayList<>();
        final ChatsAdapter chatsAdapter = new ChatsAdapter(messagesModels , this , receiverRoom , senderRoom);


        // now we have to attach this adapter in the recycler view..

        binding.chatsRecyclerView.setAdapter(chatsAdapter);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatsRecyclerView.setLayoutManager(layoutManager);
        binding.chatsRecyclerView.setHasFixedSize(true);

        //layoutManager.setReverseLayout(true);
        // here get the pos of the scrolling..



        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        lastPosition = sharedPreferences.getInt("lastPos" , 1);
        if(lastPosition < 0)
            lastPosition = 0;

        binding.chatsRecyclerView.smoothScrollToPosition(lastPosition);



        //Toast.makeText(this, Integer.toString(lastPosition), Toast.LENGTH_SHORT).show();

        binding.chatsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                lastPosition = layoutManager.findFirstVisibleItemPosition();
            }
        });



        //Toast.makeText(this, receiverRoom, Toast.LENGTH_SHORT).show();

        database.getReference().child("chats")
                .child(receiverRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messagesModels.clear();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren())
                        {
                            MessagesModel model = dataSnapshot.getValue(MessagesModel.class);
                            model.setMessageId(dataSnapshot.getKey());
                            messagesModels.add(model);
                        }
                        chatsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // set the visibility of the send button..


        // now we have to fill the data to message models..

        // now we have to set and store this data to realtime firebase..
        // this will occur when the button is clicked..

        binding.sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // now we have to store the data to database..
                String message = binding.evWritemessage.getText().toString(); // this is the message that we typed..

                if (message.length() > 0) {
                    final MessagesModel newMessage = new MessagesModel(senderId, message);
                    //final MessagesModel messageOfReceiver = new MessagesModel(receiveId , message);
                    newMessage.setTimestamp(new Date().getTime());

                    newMessage.setAndroid_reaction(-1);

                    //messageOfReceiver.setTimestamp(new Date().getTime());

                    binding.evWritemessage.setText(""); // after the button is clicked.. set it's data to null..

                    String randomKey = database.getReference().push().getKey();

                    database.getReference().child("chats").
                            child(senderRoom).
                            child(randomKey).
                            setValue(newMessage).
                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    database.getReference().child("chats")
                                            .child(receiverRoom)
                                            .child(randomKey)
                                            .setValue(newMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    });
                                }
                            });
                }
            }
        });

        Handler handler = new Handler();
        binding.evWritemessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                HashMap<String, Object> hashMap =  new HashMap<>();
                hashMap.put("user_offline_online_status" , "Typing...");

                database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                        .updateChildren(hashMap);

                handler.removeCallbacksAndMessages(null);

                handler.postDelayed(typingStopped , 1000);
            }

            Runnable typingStopped = new Runnable() {
                @Override
                public void run() {
                    HashMap<String, Object> hashMap =  new HashMap<>();
                    hashMap.put("user_offline_online_status" , "Online");

                    database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                            .updateChildren(hashMap);
                }
            };

        });

        binding.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // now  we have to call the intent from here to store all the data to the users..
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,44);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dialog.show();

        // here we'll get the url of the image and we have to store it to the database and then have to show to the user..

        // we have to store this image to the storge file called chat_photos and then have to redirect that url to firebase..
        if(data != null && data.getData() != null && requestCode == 44)
        {
            Uri sFile = data.getData();


            String randomkey = database.getReference().push().getKey();

            final StorageReference storageReference = storage.getReference().child("chat_photots").child(randomkey);

            storageReference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //  now we have to add the data to the all the chats..

                            String imagePath = uri.toString();
                            dialog.dismiss();

                            String message = "photo"; // this is the message that we typed..

                            if (message.length() > 0) {
                                final MessagesModel newMessage = new MessagesModel(senderId, message);
                                //final MessagesModel messageOfReceiver = new MessagesModel(receiveId , message);
                                newMessage.setTimestamp(new Date().getTime());

                                newMessage.setAndroid_reaction(-1);
                                newMessage.setImageUrl(imagePath);

                                //messageOfReceiver.setTimestamp(new Date().getTime());

                                binding.evWritemessage.setText(""); // after the button is clicked.. set it's data to null..

                                String randomKey = database.getReference().push().getKey();

                                database.getReference().child("chats").
                                        child(senderRoom).
                                        child(randomKey).
                                        setValue(newMessage).
                                        addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                database.getReference().child("chats")
                                                        .child(receiverRoom)
                                                        .child(randomKey)
                                                        .setValue(newMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                    }
                                                });
                                            }
                                        });
                            }



                        }
                    });
                }
            });

        }

    }
}