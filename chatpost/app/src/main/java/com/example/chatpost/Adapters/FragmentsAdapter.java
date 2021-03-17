package com.example.chatpost.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.chatpost.Fragments.CallsFragment;
import com.example.chatpost.Fragments.ChatsFragment;
import com.example.chatpost.Fragments.StatusFragment;

public class FragmentsAdapter extends FragmentPagerAdapter {
    public FragmentsAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0: return new ChatsFragment();
            case 1: return new StatusFragment();
            case 2: return new CallsFragment();
            default: return new ChatsFragment();
        }
    }

    @Override
    public int getCount() {
        return 3; // total fragments..
    }

    // here we also have to set the title of the fragments..


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        switch (position)
        {
            case 0:
                title = "Chats";
                break;
            case 1:
                title = "Status";
                break;
            case 2:
                title = "Calls";
                break;
            default:title = "Chats";
            break;
        }
        return title;
    }
}
