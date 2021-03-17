package com.example.chatpost.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.chatpost.R;
import com.example.chatpost.databinding.FragmentCallsBinding;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CallsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CallsFragment extends Fragment {

    FragmentCallsBinding binding;
    ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCallsBinding.inflate(inflater,container,false);

        dialog = new ProgressDialog(getContext());
        dialog.setTitle("generating code...");

        binding.generateRandomCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.show();
                String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                        + "0123456789"
                        + "abcdefghijklmnopqrstuvxyz" + "!@#$%^&*";

                // create StringBuffer size of AlphaNumericString
                StringBuilder sb = new StringBuilder(10);

                for (int i = 0; i < 10; i++) {

                    // generate a random number between
                    // 0 to AlphaNumericString variable length
                    int index
                            = (int)(AlphaNumericString.length()
                            * Math.random());

                    // add Character one by one in end of sb
                    sb.append(AlphaNumericString
                            .charAt(index));
                }

                if(sb.length() == 10) {
                    binding.evMeetingCode.setText(sb);
                    dialog.hide();
                }
            }
        });


        URL serverUrl;

        try {
            serverUrl = new URL("https://meet.jit.si");

            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(serverUrl)
                    .setWelcomePageEnabled(false)
                    .build();

            JitsiMeet.setDefaultConferenceOptions(options);

        } catch (MalformedURLException e){
            e.printStackTrace();
        }



        binding.join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.evMeetingCode.getText().toString().length() > 6) {
                    JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                            .setRoom(binding.evMeetingCode.getText().toString())
                            .setWelcomePageEnabled(false)
                            .build();

                    JitsiMeetActivity.launch(getContext(), options);
                }else {
                    binding.evMeetingCode.setError("code should be greater than 6 digits..");
                }
            }
        });

        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // now we have the share the code of the meeting to our friends..

                if(binding.evMeetingCode.getText().toString().length() > 6) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, "Meeting code: " + binding.evMeetingCode.getText().toString());
                    intent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(intent, null);
                    startActivity(shareIntent);
                }
                else
                {
                    binding.evMeetingCode.setError("code should be greater than 6 digits..");
                }
            }
        });



        return binding.getRoot();
    }
}