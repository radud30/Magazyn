package com.example.magazyn;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecycleViewConfig {
    private long mLastClickTime = 0;
    private Context mContext;
    private ProductAdapter mProductAdapter;

    public void setConfig(RecyclerView recyclerView, Context context, List<Products> productsList, List<String> keys){
        mContext = context;
        mProductAdapter = new ProductAdapter(productsList,keys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mProductAdapter);
    }

    class ProduktyItemView extends RecyclerView.ViewHolder{
        private TextView textViewBarcode, textViewName, textViewQuantity;
        private String key;

        public ProduktyItemView(ViewGroup parent){
            super(LayoutInflater.from(mContext).inflate(R.layout.product_list_item,parent,false));
            textViewBarcode = (TextView) itemView.findViewById(R.id.textView_kod);
            textViewName = (TextView) itemView.findViewById(R.id.textView_nazwa);
            textViewQuantity = (TextView) itemView.findViewById(R.id.textView_ilosc);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                    mLastClickTime = SystemClock.elapsedRealtime();

                    Intent intent = new Intent(mContext, ProductDetailsActivity.class);
                    intent.putExtra("key",key);
                    intent.putExtra("barcode", textViewBarcode.getText().toString());
                    intent.putExtra("name", textViewName.getText().toString());
                    intent.putExtra("quantity", textViewQuantity.getText().toString());

                    mContext.startActivity(intent);
                }
            });
        }
        public void bind(Products products, String key){
            textViewBarcode.setText(products.getBarcode());
            textViewName.setText(products.getProductName());
            textViewQuantity.setText(products.getQuantity());
            this.key = key;

        }
    }
    class ProductAdapter extends RecyclerView.Adapter<ProduktyItemView>{
        private List<Products> mProductsList;
        private List<String> mKeys;

        public ProductAdapter(List<Products> mProductsList, List<String> mKeys) {
            this.mProductsList = mProductsList;
            this.mKeys = mKeys;
        }

        @NonNull
        @Override
        public ProduktyItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ProduktyItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ProduktyItemView holder, int position) {
            holder.bind(mProductsList.get(position), mKeys.get(position));
        }

        @Override
        public int getItemCount() {
            return mProductsList.size();
        }
    }
}
