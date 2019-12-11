package com.creative.share.apps.e_branchdriver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.creative.share.apps.e_branchdriver.R;
import com.creative.share.apps.e_branchdriver.databinding.ChargeRowBinding;
import com.creative.share.apps.e_branchdriver.models.CopunModel;

import java.util.List;

public class ChargeAdapter extends RecyclerView.Adapter<ChargeAdapter.MyHolder> {

    private List<CopunModel> list;
    private Context context;


    public ChargeAdapter(List<CopunModel> list, Context context) {

        this.list = list;
        this.context = context;

    }

    @NonNull
    @Override
    public ChargeAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ChargeRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.charge_row,parent,false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull final ChargeAdapter.MyHolder holder, int position) {

        CopunModel copunModel = list.get(position);
        holder.binding.setModel(copunModel);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
       private ChargeRowBinding binding;
        public MyHolder(ChargeRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

    }




}
