package com.example.chatpost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.example.chatpost.Adapters.ChatsAdapter;
import com.example.chatpost.Models.MessagesModel;
import com.example.chatpost.databinding.ActivityGroupChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class GroupChatActivity extends AppCompatActivity {

    ActivityGroupChatBinding binding;

    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        binding.backToAllUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        // now we have to implement group chat features..

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // now in these groups we have to send or push the data..

        final String senderId = auth.getUid();

        ArrayList<MessagesModel> messagesModels = new ArrayList<>();

        final ChatsAdapter chatsAdapter = new ChatsAdapter(messagesModels , this);
        binding.chatsRecyclerView.setAdapter(chatsAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatsRecyclerView.setLayoutManager(layoutManager);

        binding.userName.setText("Friends groups");

        // now we have to update the recycler view...

        database.getReference().child("group chats")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // now we have to add the data to list..
                        messagesModels.clear();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren())
                        {
                            MessagesModel messagesModel = dataSnapshot.getValue(MessagesModel.class);
                            messagesModels.add(messagesModel);
                        }
                        chatsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




        binding.sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = binding.evWritemessage.getText().toString(); // this is the message that we have to sent to firebase..

                if(message.length() > 0) {
                    MessagesModel messagesModel = new MessagesModel(senderId, message);
                    messagesModel.setTimestamp(new Date().getTime());
                    messagesModel.setAndroid_reaction(-1);

                    binding.evWritemessage.setText(""); // make the edit text box empty..

                    database.getReference().child("group chats")
                            .push()
                            .setValue(messagesModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });

                }
            }
        });

    }
}