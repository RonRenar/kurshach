package com.example.kurshach666;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity3 extends AppCompatActivity {
Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Button button = findViewById(R.id.orders);

        // Находим все ImageButton
        ImageButton[] imageButtons = new ImageButton[]{
                findViewById(R.id.imageButton1),
                findViewById(R.id.imageButton2),
                findViewById(R.id.imageButton3),
                findViewById(R.id.imageButton4),
                findViewById(R.id.imageButton5),
                findViewById(R.id.imageButton6),
                findViewById(R.id.imageButton7),
                findViewById(R.id.imageButton8),
                findViewById(R.id.imageButton9),
                findViewById(R.id.imageButton10)
        };

        // Устанавливаем обработчик клика для каждой кнопки
        for (int i = 0; i < imageButtons.length; i++) {
            final int buttonNumber = i + 1;
            imageButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Переходим на активити 4 и передаем номер кнопки
                    Intent intent = new Intent(MainActivity3.this, MainActivity4.class);
                    intent.putExtra("buttonNumber", buttonNumber);
                    Log.d("MainActivity3", "Button Number: " + buttonNumber); // Добавляем логирование
                    startActivity(intent);
                }
            });


        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Создаем интент для перехода на MainActivity5
                Intent intent = new Intent(MainActivity3.this, MainActivity5.class);
                // Запускаем MainActivity5
                startActivity(intent);
            }
        });


    }
}
