package com.example.chatpost.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devlomi.circularstatusview.CircularStatusView;
import com.example.chatpost.Fragments.StatusFragment;
import com.example.chatpost.MainActivity;
import com.example.chatpost.Models.Status;
import com.example.chatpost.Models.UserStatus;
import com.example.chatpost.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusViewHolder> {

    Context context;
    ArrayList<UserStatus> userStatuses;



    public StatusAdapter(Context context, ArrayList<UserStatus> userStatuses) {
        this.context = context;
        this.userStatuses = userStatuses;
    }

    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.user_status_layout , parent , false);

        return new StatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder holder, int position) {

        UserStatus userStatus = userStatuses.get(position);


        holder.statusView.setPortionsCount(userStatus.getStatuses().size());

        Status lastStatus = userStatus.getStatuses().get(userStatus.getStatuses().size() - 1);

        Picasso.get().load(lastStatus.getImageUrl()).into(holder.imageView);

        holder.userName.setText(userStatus.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                
                ArrayList<MyStory> myStories = new ArrayList<>();
                for(Status status : userStatus.getStatuses())
                {
                    myStories.add(new MyStory(status.getImageUrl()));
                }

                //Toast.makeText(context, Integer.toString(myStories.size()), Toast.LENGTH_SHORT).show();

                if(myStories.size() > 0) {
                    new StoryView.Builder(((MainActivity) context).getSupportFragmentManager())
                            .setStoriesList(myStories) // Required
                            .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                            .setTitleText(userStatus.getName())// Default is Hidden
                            .setSubtitleText("") // Default is Hidden
                            .setTitleLogoUrl(userStatus.getProfileImage()) // Default is Hidden
                            .setStoryClickListeners(new StoryClickListeners() {
                                @Override
                                public void onDescriptionClickListener(int position) {
                                    //your action
                                }

                                @Override
                                public void onTitleIconClickListener(int position) {
                                    //your action
                                }
                            }) // Optional Listeners
                            .build() // Must be called before calling show method
                            .show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return userStatuses.size();
    }

    public class StatusViewHolder extends RecyclerView.ViewHolder{

        CircularStatusView statusView;
        ImageView imageView;
        TextView userName;
        public StatusViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = (TextView) itemView.findViewById(R.id.statusUserName);
            imageView = (ImageView) itemView.findViewById(R.id.status_image);
            statusView = (CircularStatusView) itemView.findViewById(R.id.circular_status_view);
        }
    }
}
