package com.example.kurshach666;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
public class MainActivity4 extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    private TextView nameTextView, priceTextView;
    private ImageView imageView;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Добавляем LayoutManager

        int buttonNumber = getIntent().getIntExtra("buttonNumber", 0); // 0 - значение по умолчанию, если ключ не найден
        String gruppa = ""; // По умолчанию значение пустой строки
        Log.d("MainActivity4", "Button Number: " + buttonNumber);

        switch (buttonNumber) {
            case 1:
                gruppa = "gpu";
                break;
            case 2:
                gruppa = "bp ";
                break;
            case 3:
                gruppa = "Products";
                break;
            case 4:
                gruppa = "Cp";
                break; // Добавлен оператор break
            case 5:
                gruppa = "cooler_cp";
                break;
            case 6:
                gruppa = "ssd";
                break;
            case 7:
                gruppa = "memory";
                break;
            case 8:
                gruppa = "korpus";
                break; // Добавлен оператор break
            case 9:
                gruppa = "monitor";
                break;
            case 10:
                gruppa = "hdd";
                break;
            // Добавьте другие варианты, если необходимо
        }

        // Получаем ссылку на Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference().child(gruppa);

        // Инициализируем адаптер
        ProductAdapter adapter = new ProductAdapter(this, new ArrayList<>(), true);

        // Устанавливаем адаптер для RecyclerView
        recyclerView.setAdapter(adapter);

        // Устанавливаем слушатель для получения данных
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Product> productList = new ArrayList<>();

                // Обходим все дочерние узлы (продукты) в базе данных
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Получаем данные для каждого продукта
                    String name = snapshot.child("name").getValue(String.class);
                    Integer price = snapshot.child("price").getValue(Integer.class);
                    String imageUrl = snapshot.child("pic").getValue(String.class);

                    // Создаем объект Product и добавляем его в список
                    Product product = new Product(name, String.valueOf(price), imageUrl);
                    productList.add(product);
                }

                // Обновляем данные в адаптере
                adapter.setProductList(productList);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибок, если такие имеются
            }
        });
    }
}