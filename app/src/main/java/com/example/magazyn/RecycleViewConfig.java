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
    private ProduktyAdapter mProduktyAdapter;

    public void setConfig(RecyclerView recyclerView, Context context, List<Produkty> produktyList, List<String> keys){
        mContext = context;
        mProduktyAdapter = new ProduktyAdapter(produktyList,keys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mProduktyAdapter);
    }

    class ProduktyItemView extends RecyclerView.ViewHolder{
        private TextView mKod;
        private TextView mNazwa;
        private TextView mIlosc;

        private String key;

        public ProduktyItemView(ViewGroup parent){
            super(LayoutInflater.from(mContext).inflate(R.layout.produkty_list_item,parent,false));
            mKod = (TextView) itemView.findViewById(R.id.textView_kod);
            mNazwa = (TextView) itemView.findViewById(R.id.textView_nazwa);
            mIlosc = (TextView) itemView.findViewById(R.id.textView_ilosc);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(SystemClock.elapsedRealtime() - mLastClickTime < 1000) return;
                    mLastClickTime = SystemClock.elapsedRealtime();

                    Intent intent = new Intent(mContext, ProduktDetailsActivity.class);
                    intent.putExtra("key",key);
                    intent.putExtra("kod",mKod.getText().toString());
                    intent.putExtra("nazwa",mNazwa.getText().toString());
                    intent.putExtra("ilosc", mIlosc.getText().toString());

                    mContext.startActivity(intent);
                }
            });
        }
        public void bind(Produkty produkty, String key){
            mKod.setText(produkty.getKod());
            mNazwa.setText(produkty.getProduktNazwa());
            mIlosc.setText(produkty.getIlosc());
            this.key = key;

        }
    }
    class ProduktyAdapter extends RecyclerView.Adapter<ProduktyItemView>{
        private List<Produkty> mProduktyList;
        private List<String> mKeys;

        public ProduktyAdapter(List<Produkty> mProduktyList, List<String> mKeys) {
            this.mProduktyList = mProduktyList;
            this.mKeys = mKeys;
        }

        @NonNull
        @Override
        public ProduktyItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ProduktyItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ProduktyItemView holder, int position) {
            holder.bind(mProduktyList.get(position), mKeys.get(position));
        }

        @Override
        public int getItemCount() {
            return mProduktyList.size();
        }
    }
}
