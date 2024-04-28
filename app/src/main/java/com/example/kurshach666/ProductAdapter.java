package com.example.kurshach666;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<Product> productList;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    private Context context;
    private boolean showBuyButton; // Флаг для отображения кнопки "Купить"

    public ProductAdapter(Context context, List<Product> productList,boolean showBuyButton) {
        this.context = context;
        this.productList = productList;
        this.showBuyButton = showBuyButton;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.textViewName.setText(product.getName());
        holder.textViewPrice.setText("Цена:" + product.getPrice() + " рублей");

        // Load image using Glide library
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imageView);

        if (showBuyButton) {
            holder.buyButton.setVisibility(View.VISIBLE);
        } else {
            holder.buyButton.setVisibility(View.GONE);
        }

        holder.buyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                    String uid = currentUser.getUid();
                    // Теперь у вас есть идентификатор пользователя (UID)


                DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child(uid);
                String orderId = orderRef.push().getKey();

                // Создаем объект для нового заказа
                Map<String, Object> orderData = new HashMap<>();
                orderData.put("name", product.getName());
                orderData.put("price", product.getPrice());
                orderData.put("pic", product.getImageUrl());


                orderRef.child(orderId).setValue(orderData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Оповещаем пользователя о добавлении продукта в корзину (опционально)
                                Toast.makeText(context, "Продукт добавлен в корзину", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Обработка ошибок, если таковые имеются
                                Toast.makeText(context, "Ошибка при добавлении продукта в корзину", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewName;
        TextView textViewPrice;
        Button buyButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewName = itemView.findViewById(R.id.nameTextView);
            textViewPrice = itemView.findViewById(R.id.priceTextView);
            buyButton = itemView.findViewById(R.id.buyButton);
        }
    }
    public int calculateTotalPrice() {
        int totalPrice = 0;
        for (Product product : productList) {
            totalPrice += (product.getPrice());
        }
        return totalPrice;
    }

    // Метод для отправки уведомления

}