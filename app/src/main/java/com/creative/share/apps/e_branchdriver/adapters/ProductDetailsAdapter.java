package com.creative.share.apps.e_branchdriver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.creative.share.apps.e_branchdriver.R;
import com.creative.share.apps.e_branchdriver.databinding.OrderDetailsRowBinding;
import com.creative.share.apps.e_branchdriver.models.OrderModel;

import java.util.List;

public class ProductDetailsAdapter extends RecyclerView.Adapter<ProductDetailsAdapter.MyHolder> {

    private List<OrderModel.OrderDetails> list;
    private Context context;


    public ProductDetailsAdapter(List<OrderModel.OrderDetails> list, Context context) {

        this.list = list;
        this.context = context;

    }

    @NonNull
    @Override
    public ProductDetailsAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        OrderDetailsRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.order_details_row,parent,false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull final ProductDetailsAdapter.MyHolder holder, int position) {

        OrderModel.OrderDetails model = list.get(position);
        holder.binding.setModel(model);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
       private OrderDetailsRowBinding binding;
        public MyHolder(OrderDetailsRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

    }




}
