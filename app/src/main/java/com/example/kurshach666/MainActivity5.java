package com.example.kurshach666;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;




import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MainActivity5 extends AppCompatActivity {
    private Button checkoutButton;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> cartProductList;
    private  String total;
    private DatabaseReference orderRef;
    private StringBuilder messageBody = new StringBuilder();
    private  String uid;
    private List<Product> formail;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        checkoutButton = findViewById(R.id.checkoutButton);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        cartProductList = new ArrayList<>(); // Инициализируем список продуктов корзины
        uid = currentUser.getUid();
        adapter = new ProductAdapter(this, cartProductList, false);
        recyclerView.setAdapter(adapter);

        // Инициализируем orderRef глобальной переменной
        orderRef = FirebaseDatabase.getInstance().getReference().child(uid);

        // Добавляем слушатель для получения данных из узла "order"
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Очищаем список продуктов корзины перед обновлением
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
                formail = productList;
                adapter.setProductList(productList);
                int totalPrice = adapter.calculateTotalPrice();
                TextView totalPriceTextView = findViewById(R.id.totalPriceTextView);

                totalPriceTextView.setText("Общая цена: " + totalPrice + " руб");
                total = totalPriceTextView.getText().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибок, если такие имеются
            }
        });
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (total.equals("Общая цена: 0 руб")) {
                    showOrderPlacedNotificatio1();
                } else {
                    sendEmail();
                    showOrderPlacedNotification();
                    moveDataToNewGroupAndDelete();


                    Intent intent = new Intent(MainActivity5.this, MainActivity2.class);
                    startActivity(intent);
                }
            }
        });

    }

    private void showOrderPlacedNotification() {
        // Создаем и отображаем Toast с сообщением о том, что заказ оформлен
        Toast.makeText(MainActivity5.this, "Заказ успешно оформлен!", Toast.LENGTH_SHORT).show();
    }
    private void showOrderPlacedNotificatio1() {
        // Создаем и отображаем Toast с сообщением о том, что заказ оформлен
        Toast.makeText(MainActivity5.this, "Корзина пуста!", Toast.LENGTH_SHORT).show();
    }
    private void deleteGroup() {
        // Удаляем группу из базы данных Firebase
        orderRef.removeValue();
    }



    private void moveDataToNewGroupAndDelete() {
        // Получаем сегодняшнюю дату
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String todayDate = dateFormat.format(new Date());
        String orders = "orders" + uid;

        // Создаем новую группу с явно заданным именем (например, сегодняшней датой)
        DatabaseReference newOrderRef = orderRef.getParent().child(orders);

        // Переносим данные из текущей группы в новую с добавлением сегодняшней даты к каждому элементу
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Получаем данные для каждого продукта из текущей группы
                    String name = snapshot.child("name").getValue(String.class);
                    Integer price = snapshot.child("price").getValue(Integer.class);
                    String imageUrl = snapshot.child("pic").getValue(String.class);

                    // Создаем объект Product с сегодняшней датой и добавляем его в новую группу
                    Product product = new Product(name, String.valueOf(price), imageUrl);
                    product.setDate(todayDate); // Устанавливаем сегодняшнюю дату для продукта
                    newOrderRef.child(snapshot.getKey()).setValue(product);
                }

                // Удаляем текущую группу
                orderRef.removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибок, если такие имеются
            }
        });
    }

    private void sendEmail() {
        // Получаем общую цену
        messageBody.setLength(0);


        // Получаем общую цену
        int totalPrice = adapter.calculateTotalPrice();

        // Формируем текст сообщения
        messageBody.append("Товары в заказе:\n");
        for (Product product : formail) {
            messageBody.append("Название: ").append(product.getName()).append("\n");
            messageBody.append("Цена: ").append(product.getPrice()).append(" руб\n");
            messageBody.append("\n");
        }
        messageBody.append("Общая цена: ").append(totalPrice).append(" руб");


        // Создаем отдельный поток для отправки почты
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Настройка свойств для соединения с SMTP сервером
                    Properties props = new Properties();
                    props.put("mail.smtp.host", "smtp.yandex.com");
                    props.put("mail.smtp.socketFactory.port", "465");
                    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                    props.put("mail.smtp.auth", "true");
                    props.put("mail.smtp.port", "465");

                    // Получение сессии
                    Session session = Session.getDefaultInstance(props,
                            new javax.mail.Authenticator() {
                                protected PasswordAuthentication getPasswordAuthentication() {
                                    return new PasswordAuthentication("AndreyKicheev@yandex.ru", "ydyatacvfubgshhn");
                                }
                            });

                    // Отправка сообщения
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress("AndreyKicheev@yandex.ru"));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("ahdrey.kicheev42@gmail.com"));
                    message.setSubject("Заказ с мобильного приложения");
                    message.setText(messageBody.toString());

                    // Отправка сообщения
                    Transport.send(message);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity5.this, "Информация о заказе отправлена вам на почту", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (MessagingException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity5.this, "Ошибка отправки почты", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
}