package com.example.androidvelo;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.androidvelo.bottomnav.chats.ChatsFragment;
import com.example.androidvelo.bottomnav.home.HomeFragment;
import com.example.androidvelo.bottomnav.options.OptionsFragment;
import com.example.androidvelo.bottomnav.profile.ProfileFragment;
import com.example.androidvelo.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            startActivities(new Intent[]{new Intent(MainActivity.this, LoginActivity.class)});
        }

        getSupportFragmentManager().beginTransaction().replace(binding.fragmentContainer.getId(), new HomeFragment()).commit();
        binding.bottomNav.setSelectedItemId(R.id.news_page);

        Map<Integer, Fragment> fragmentMap = new HashMap<>();
        fragmentMap.put(R.id.news_page, new HomeFragment());
        fragmentMap.put(R.id.options, new OptionsFragment());
        fragmentMap.put(R.id.chats, new ChatsFragment());
        //fragmentMap.put(R.id.new_chat, new NewChatFragment());
        fragmentMap.put(R.id.profile, new ProfileFragment());

        binding.bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = fragmentMap.get(item.getItemId());

            getSupportFragmentManager().beginTransaction().replace(binding.fragmentContainer.getId(), fragment).commit();

            return true;
        });
    }
}