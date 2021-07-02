package com.example.magazyn;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PermissionRecycleView {
    private Context mContext;
    private PermissionAdapter mPermissionAdapter;
    public void setConfig(RecyclerView recyclerView, Context context, List<Workers>workers, List<String>keys){
        mContext = context;
        mPermissionAdapter = new PermissionAdapter(workers,keys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mPermissionAdapter);
    }

    class PermissionItemView extends RecyclerView.ViewHolder {
        private TextView mEmail;
        private Switch mAdd;
        private Switch mStock;
        private Switch mCollect;
        private Switch mLocation;

        private String key, creatorUid, worker;

        public PermissionItemView(ViewGroup parent){
            super(LayoutInflater.from(mContext).inflate(R.layout.permission_item,parent,false));

            mEmail = (TextView) itemView.findViewById(R.id.emailText);
            mAdd = (Switch) itemView.findViewById(R.id.switchAddProduct);
            mStock = (Switch) itemView.findViewById(R.id.switchStockStatus);
            mCollect = (Switch) itemView.findViewById(R.id.switchCollect);
            mLocation = (Switch) itemView.findViewById(R.id.switchLocation);
            mAdd.setClickable(false);
            mStock.setClickable(false);
            mCollect.setClickable(false);
            mLocation.setClickable(false);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String addStatus = "false", stockStatus ="false", collectStatus ="false", locationStatus="false";
                    Intent intent = new Intent(mContext, PermissionDetailsActivity.class);
                    intent.putExtra("key",key);
                    intent.putExtra("email",mEmail.getText().toString());
                    if(mAdd.isChecked()){
                        addStatus = "true";
                    }
                    intent.putExtra("add", addStatus);
                    if(mStock.isChecked()){
                        stockStatus = "true";
                    }
                    intent.putExtra("stock", stockStatus);
                    if(mCollect.isChecked()){
                        collectStatus = "true";
                    }
                    intent.putExtra("collect", collectStatus);
                    if(mLocation.isChecked()){
                        locationStatus = "true";
                    }
                    intent.putExtra("location", locationStatus);

                    intent.putExtra("worker", worker);
                    intent.putExtra("creatorUid", creatorUid);

                    mContext.startActivity(intent);
                }
            });
        }

        public void bind(Workers workers, String key){
            mEmail.setText(workers.getEmail());
            String add, stock, collect,location;
            add = workers.getPermissionAdd();
            if(add.equals("true")){
                mAdd.setChecked(true);
            }else{
                mAdd.setChecked(false);
            }
            stock = workers.getPermissionStockStatus();
            if(stock.equals("true")){
                mStock.setChecked(true);
            }else{
                mStock.setChecked(false);
            }
            collect = workers.getPermissionCollect();
            if(collect.equals("true")){
                mCollect.setChecked(true);
            }else{
                mCollect.setChecked(false);
            }
            location = workers.getPermissionLocation();
            if(location.equals("true")){
                mLocation.setChecked(true);
            }else{
                mLocation.setChecked(false);
            }

            worker = workers.getWorker();
            creatorUid = workers.getCreatorUid();


            this.key = key;
        }
    }
    class PermissionAdapter extends RecyclerView.Adapter<PermissionItemView>{
        private List<Workers> mWorkersList;
        private List<String> mKeys;

        public PermissionAdapter(List<Workers> mWorkersList, List<String> mKeys) {
            this.mWorkersList = mWorkersList;
            this.mKeys = mKeys;
        }

        @NonNull
        @Override
        public PermissionItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PermissionItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull PermissionItemView holder, int position) {
            holder.bind(mWorkersList.get(position), mKeys.get(position));
        }

        @Override
        public int getItemCount() {
            return mWorkersList.size();
        }
    }
}
