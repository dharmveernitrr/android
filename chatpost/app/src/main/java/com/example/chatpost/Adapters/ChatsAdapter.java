package com.example.chatpost.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatpost.Models.MessagesModel;
import com.example.chatpost.R;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ChatsAdapter extends RecyclerView.Adapter {

    ArrayList<MessagesModel> messagesModels;
    Context context;

    String receiverRoom , senderRoom;



    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;

    int reactions[] = new int[]{
            R.drawable.ic_fb_like,
            R.drawable.ic_fb_love,
            R.drawable.ic_fb_laugh,
            R.drawable.ic_fb_sad,
            R.drawable.ic_fb_angry,
    };


    public ChatsAdapter(ArrayList<MessagesModel> messagesModels, Context context) {
        this.messagesModels = messagesModels;
        this.context = context;
    }

    public ChatsAdapter(ArrayList<MessagesModel> messagesModels, Context context, String receiverRoom , String senderRoom) {
        this.messagesModels = messagesModels;
        //setHasStableIds(true);
        this.context = context;
        this.receiverRoom = receiverRoom;
        this.senderRoom = senderRoom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_sample, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_sample, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        // here we have to check whether which layout we have to use in the chat adapter..
        if (messagesModels.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())) {
            return SENDER_VIEW_TYPE;
        } else {
            return RECEIVER_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessagesModel messagesModel = messagesModels.get(position);
        String str = messagesModel.getMessage();
        int react = -1;
        if (messagesModel.getAndroid_reaction() != null)
            react = messagesModel.getAndroid_reaction();



         holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete message")
                        .setMessage("Are you want to delete this message?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();

                                HashMap<String,Object> hashMap = new HashMap<>();
                                hashMap.put("message" , "this message is deleted");

                                database.getReference().child("chats").child(receiverRoom)
                                        .child(messagesModel.getMessageId())
                                        .setValue(null);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

                return true;
            }
        });

        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, messagesModel.toString(), Toast.LENGTH_SHORT).show();
            }
        });*/


        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();


        // now we have to create a popup..
        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if (holder.getClass() == SenderViewHolder.class) {

            } else {
                if(pos >= 0) {
                    //Toast.makeText(context, "reaction updated..", Toast.LENGTH_SHORT).show();
                    ReceiverViewHolder receiverViewHolder = (ReceiverViewHolder) holder;
                    receiverViewHolder.receiver_reactions.setImageResource(reactions[pos]);
                    receiverViewHolder.receiver_reactions.setVisibility(View.VISIBLE);


                    messagesModel.setAndroid_reaction(pos);
                    FirebaseDatabase.getInstance().getReference()
                            .child("chats")
                            .child(senderRoom)
                            .child(messagesModel.getMessageId()).setValue(messagesModel);

                    FirebaseDatabase.getInstance().getReference()
                            .child("chats")
                            .child(receiverRoom)
                            .child(messagesModel.getMessageId()).setValue(messagesModel);
                }
             }


            return true;
        });


        if (holder.getClass() == SenderViewHolder.class) {

            if(messagesModel.getMessage().equals("photo"))


            if(messagesModel.getAndroid_reaction() >= 0)
            {
                ((SenderViewHolder)holder).sender_reactions.setImageResource(reactions[(int)messagesModel.getAndroid_reaction()]);
                ((SenderViewHolder)holder).sender_reactions.setVisibility(View.VISIBLE);
            } else
            {
                ((SenderViewHolder)holder).sender_reactions.setVisibility(View.GONE);
            }

            if(messagesModel.getTimestamp() > 0) {
                String time = DateUtils.formatDateTime(context, messagesModel.getTimestamp(), DateUtils.FORMAT_SHOW_TIME);
                String date =  DateUtils.formatDateTime(context, messagesModel.getTimestamp(), DateUtils.FORMAT_SHOW_DATE);

                ((SenderViewHolder) holder).senderTime.setText(date + " | " +  time);
            }
            if (messagesModel.getMessage().equals("photo"))
            {
                // here we have to upload the image..
                ((SenderViewHolder)holder).sender_chat_image.setVisibility(View.VISIBLE);
                ((SenderViewHolder)holder).senderMessage.setVisibility(View.GONE);
                Picasso.get().load(messagesModel.getImageUrl()).into(((SenderViewHolder)holder).sender_chat_image);
            }
            else {
                ((SenderViewHolder)holder).sender_chat_image.setVisibility(View.GONE);
                ((SenderViewHolder)holder).senderMessage.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).senderMessage.setText(str);
                /*((SenderViewHolder)holder).itemView.findViewById(R.id.tv_sender_text).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popup.onTouch(v,event);
                        return  true;
                    }
                });*/
            }
            // if(messagesModel.getAndroid_reaction() != null)
            //((SenderViewHolder)holder).sender_reactions.setImageResource(messagesModel.getAndroid_reaction());
        } else {

            /*((ReceiverViewHolder)holder).itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            popup.onTouch(v, event);
                            return true;
                        }
                    });
                    return true;
                }
            });*/

            ((ReceiverViewHolder)holder).itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ((ReceiverViewHolder)holder).itemView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            popup.onTouch(v,event);
                            return true;
                        }
                    });
                    return true;
                }
            });

            if(messagesModel.getTimestamp() > 0) {
                String time = DateUtils.formatDateTime(context, messagesModel.getTimestamp(), DateUtils.FORMAT_SHOW_TIME);
                String date =  DateUtils.formatDateTime(context, messagesModel.getTimestamp(), DateUtils.FORMAT_SHOW_DATE);

                ((ReceiverViewHolder) holder).receiverTime.setText(date + " | " +  time);
            }




            // now we have to check whether this string is valid url or not..

            if(messagesModel.getAndroid_reaction() >= 0)
            {
                ((ReceiverViewHolder)holder).receiver_reactions.setImageResource(reactions[(int)messagesModel.getAndroid_reaction()]);
                ((ReceiverViewHolder)holder).receiver_reactions.setVisibility(View.VISIBLE);
            } else
            {
                ((ReceiverViewHolder)holder).receiver_reactions.setVisibility(View.GONE);
            }

            if (messagesModel.getMessage().equals("photo")) {
                // here we have to upload the image in the adapter..

                ((ReceiverViewHolder)holder).receiver_chat_image.setVisibility(View.VISIBLE);
                ((ReceiverViewHolder)holder).receiverMessage.setVisibility(View.GONE);

                Picasso.get().load(messagesModel.getImageUrl()).into(((ReceiverViewHolder)holder).receiver_chat_image);
            } else {
                ((ReceiverViewHolder)holder).receiver_chat_image.setVisibility(View.GONE);
                ((ReceiverViewHolder)holder).receiverMessage.setVisibility(View.VISIBLE);
                ((ReceiverViewHolder) holder).receiverMessage.setText(str);

            }
        }
    }

    @Override
    public int getItemCount() {
        return messagesModels.size();
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {

        TextView senderMessage;
        TextView senderTime;
        ImageView sender_reactions, sender_chat_image;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessage = (TextView) itemView.findViewById(R.id.tv_sender_text);
            senderTime = (TextView) itemView.findViewById(R.id.tv_sender_time);
            sender_reactions = (ImageView) itemView.findViewById(R.id.sender_reactions);
            sender_chat_image = (ImageView) itemView.findViewById(R.id.tv_sender_image);
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {

        TextView receiverMessage, receiverTime;
        ImageView receiver_reactions;
        ImageView receiver_chat_image;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMessage = (TextView) itemView.findViewById(R.id.tv_receiver_text);
            receiverTime = (TextView) itemView.findViewById(R.id.tv_receiver_time);
            receiver_reactions = (ImageView) itemView.findViewById(R.id.receiver_reactions);
            receiver_chat_image = (ImageView) itemView.findViewById(R.id.tv_receiver_image);
        }
    }
}