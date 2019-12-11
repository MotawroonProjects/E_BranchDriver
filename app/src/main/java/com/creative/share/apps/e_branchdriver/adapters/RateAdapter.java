package com.creative.share.apps.e_branchdriver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.creative.share.apps.e_branchdriver.R;
import com.creative.share.apps.e_branchdriver.databinding.RateRowBinding;
import com.creative.share.apps.e_branchdriver.models.RateModel;

import java.util.List;

public class RateAdapter extends RecyclerView.Adapter<RateAdapter.MyHolder> {

    private List<RateModel.Rate> list;
    private Context context;


    public RateAdapter(List<RateModel.Rate> list, Context context) {

        this.list = list;
        this.context = context;

    }

    @NonNull
    @Override
    public RateAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RateRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rate_row,parent,false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull final RateAdapter.MyHolder holder, int position) {

        RateModel.Rate model = list.get(position);
        holder.binding.setModel(model);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
       private RateRowBinding binding;
        public MyHolder(RateRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

    }




}
