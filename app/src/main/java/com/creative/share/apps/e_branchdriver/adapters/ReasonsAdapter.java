package com.creative.share.apps.e_branchdriver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.creative.share.apps.e_branchdriver.R;
import com.creative.share.apps.e_branchdriver.activities_fragments.activity_order_details.fragments.Fragment_Client_Order_Details;
import com.creative.share.apps.e_branchdriver.databinding.ReasonRowBinding;
import com.creative.share.apps.e_branchdriver.models.CancelReasonsDataModel;

import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class ReasonsAdapter extends RecyclerView.Adapter<ReasonsAdapter.MyHolder> {

    private List<CancelReasonsDataModel.CancelModel> list;
    private Context context;
    private String lang;
    private int selected_pos = -1;
    private Fragment_Client_Order_Details fragment;
    public ReasonsAdapter(List<CancelReasonsDataModel.CancelModel> list, Context context,Fragment_Client_Order_Details fragment) {

        this.list = list;
        this.context = context;
        this.fragment = fragment;
        Paper.init(context);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());

    }

    @NonNull
    @Override
    public ReasonsAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ReasonRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.reason_row,parent,false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull final ReasonsAdapter.MyHolder holder, int position) {

        CancelReasonsDataModel.CancelModel model = list.get(position);
        holder.binding.setModel(model);
        holder.binding.setLang(lang);
        if (selected_pos==position)
        {
            holder.binding.rb.setChecked(true);
        }else
            {
                holder.binding.rb.setChecked(false);

            }

        holder.itemView.setOnClickListener(view ->
        {
            int pos = holder.getAdapterPosition();
            CancelReasonsDataModel.CancelModel model2 = list.get(pos);
            fragment.setItemData(model2);
            selected_pos = pos;
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
       private ReasonRowBinding binding;
        public MyHolder(ReasonRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

    }




}
