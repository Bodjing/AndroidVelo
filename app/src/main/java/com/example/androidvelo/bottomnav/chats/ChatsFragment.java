package com.example.androidvelo.bottomnav.chats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.androidvelo.R;
import com.example.androidvelo.bottomnav.new_chat.NewChatFragment;
import com.example.androidvelo.chats.Chat;
import com.example.androidvelo.chats.ChatsAdapter;
import com.example.androidvelo.databinding.FragmentChatsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ChatsFragment extends Fragment {
    private FragmentChatsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);

        loadChats(); // Загрузка чатов

        // Обработчик нажатия на кнопку "Новый чат"
        binding.newChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Открываем фрагмент для создания нового чата
                openNewChatFragment();
            }
        });

        return binding.getRoot();
    }


    private void loadChats(){
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    // Проверяем наличие чатов у пользователя
                    if (snapshot.child("Users").child(uid).hasChild("chats")) {
                        ArrayList<Chat> chats = new ArrayList<>();

                        // Получаем список идентификаторов чатов пользователя
                        String chatsStr = Objects.requireNonNull(snapshot.child("Users").child(uid).child("chats").getValue()).toString();
                        String[] chatsIds = chatsStr.split(",");

                        // Проходимся по каждому идентификатору чата
                        for (String chatId : chatsIds){
                            // Получаем данные о чате
                            DataSnapshot chatsSnapshot = snapshot.child("Chats").child(chatId);
                            String userId1 =Objects.requireNonNull(chatsSnapshot.child("user1").getValue()).toString();
                            String userId2 =Objects.requireNonNull(chatsSnapshot.child("user2").getValue()).toString();

                            // Определяем идентификатор пользователя, с которым ведется чат
                            String chatUserId = (uid.equals(userId1)) ? userId2 : userId1;
                            // Получаем имя пользователя, с которым ведется чат
                            String chatName = Objects.requireNonNull(snapshot.child("Users").child(chatUserId).child("username").getValue()).toString();

                            // Создаем объект Chat и добавляем его в список чатов
                            Chat chat = new Chat(chatId, chatName, userId1, userId2);
                            chats.add(chat);
                        }

                        // Устанавливаем адаптер RecyclerView для отображения чатов
                        binding.chatsRv.setLayoutManager(new LinearLayoutManager(getContext()));
                        binding.chatsRv.setAdapter(new ChatsAdapter(chats));
                    } else {
                        // Если у пользователя нет чатов, показываем сообщение
                        if (getContext() != null)
                            Toast.makeText(getContext(), "Нет существующих чатов", Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    // В случае ошибки при загрузке чатов показываем сообщение об ошибке
                    if (getContext() != null)
                        Toast.makeText(getContext(), "Произошла ошибка при загрузке чатов", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // В случае ошибки при получении данных из базы данных показываем сообщение об ошибке
                if (getContext() != null)
                    Toast.makeText(getContext(), "Failed to get user chats", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openNewChatFragment() {
        // Открываем фрагмент для создания нового чата
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new NewChatFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
