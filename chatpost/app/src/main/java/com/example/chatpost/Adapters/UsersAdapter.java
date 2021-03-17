package com.example.chatpost.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatpost.ChatDetailedActivity;
import com.example.chatpost.Models.Users;
import com.example.chatpost.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder>{

    ArrayList<Users> list;
    Context context;

    public UsersAdapter(Context context , ArrayList<Users> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.users_chat_list_layout , parent , false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // now here we have to attach the data here..
        Users users = list.get(position);
        Picasso.get().load(users.getProfilePicture()).placeholder(R.drawable.profile).into(holder.profile_picture);

        holder.userName.setText(users.getUserName());

        // here we also have to set the last message sent by the sender or the receiver..

        FirebaseDatabase.getInstance().getReference().child("chats")
                .child(FirebaseAuth.getInstance().getUid() + users.getUserId())
                .orderByChild("timestamp")
                .limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren()){
                            for(DataSnapshot dataSnapshot : snapshot.getChildren())
                            {
                                holder.lastMessage.setText(dataSnapshot.child("message").getValue().toString());
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , ChatDetailedActivity.class);
                // now we also have to put extras here to send the data to other activity..

                intent.putExtra("userId" , users.getUserId());
                intent.putExtra("userProfile" , users.getProfilePicture());
                intent.putExtra("userName" , users.getUserName());
                intent.putExtra("user_offline_online_status" ,users.getUser_offline_online_status());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        // in this layout we have three views..

        ImageView profile_picture;
        TextView userName , lastMessage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_picture = (ImageView) itemView.findViewById(R.id.chats_profileImage);
            userName = (TextView) itemView.findViewById(R.id.chats_userName);
            lastMessage = (TextView) itemView.findViewById(R.id.chats_lastMessage);
        }
    }
}
