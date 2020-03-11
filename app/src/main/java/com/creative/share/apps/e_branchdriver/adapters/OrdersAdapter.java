package com.creative.share.apps.e_branchdriver.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.creative.share.apps.e_branchdriver.R;
import com.creative.share.apps.e_branchdriver.activities_fragments.activity_home.fragments.Fragment_Available_Order;
import com.creative.share.apps.e_branchdriver.activities_fragments.activity_home.fragments.Fragment_Delivered_Order;
import com.creative.share.apps.e_branchdriver.activities_fragments.activity_home.fragments.Fragment_Discarded_Order;
import com.creative.share.apps.e_branchdriver.activities_fragments.activity_home.fragments.Fragment_Not_Delivered_Order;
import com.creative.share.apps.e_branchdriver.activities_fragments.activity_home.fragments.Fragment_Pending_Order;
import com.creative.share.apps.e_branchdriver.activities_fragments.activity_home.fragments.Fragment_Stumble_Order;
import com.creative.share.apps.e_branchdriver.databinding.OrderRowBinding;
import com.creative.share.apps.e_branchdriver.databinding.ProgressLoadRowBinding;
import com.creative.share.apps.e_branchdriver.models.OrderModel;

import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class OrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ITEM_DATA = 1;
    private final int ITEM_LOAD = 2;

    private List<OrderModel> list;
    private Context context;
    private Fragment fragment;
    private String lang;
    private Locale locale;

    public OrdersAdapter(List<OrderModel> list, Context context, Fragment fragment) {

        this.list = list;
        this.context = context;
        this.fragment = fragment;
        Paper.init(context);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        locale = Locale.ENGLISH;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ITEM_DATA) {
            OrderRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.order_row,parent,false);
            return new MyHolder(binding);
        } else {
            ProgressLoadRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.progress_load_row,parent,false);
            return new LoadMoreHolder(binding);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MyHolder) {


            final MyHolder myHolder = (MyHolder) holder;
            OrderModel model = list.get(myHolder.getAdapterPosition());

            myHolder.binding.setLang(lang);
            myHolder.binding.setModel(model);
            myHolder.binding.setLocal(locale);

            holder.itemView.setOnClickListener(view ->
                    {

                        if (fragment instanceof Fragment_Available_Order)
                        {
                            Fragment_Available_Order fragment_available_order = (Fragment_Available_Order) fragment;
                            fragment_available_order.setItemData(model,holder.getAdapterPosition());
                        }else if (fragment instanceof Fragment_Discarded_Order)
                        {
                            Fragment_Discarded_Order fragment_discarded_order = (Fragment_Discarded_Order) fragment;
                            fragment_discarded_order.setItemData(model,holder.getAdapterPosition());
                        }else if (fragment instanceof Fragment_Pending_Order)
                        {
                            Fragment_Pending_Order fragment_pending_order = (Fragment_Pending_Order) fragment;
                            fragment_pending_order.setItemData(model,holder.getAdapterPosition());
                        }else if (fragment instanceof Fragment_Not_Delivered_Order)
                        {
                            Fragment_Not_Delivered_Order fragment_not_delivered_order = (Fragment_Not_Delivered_Order) fragment;
                            fragment_not_delivered_order.setItemData(model,holder.getAdapterPosition());
                        }else if (fragment instanceof Fragment_Delivered_Order)
                        {
                            Fragment_Delivered_Order fragment_delivered_order = (Fragment_Delivered_Order) fragment;
                            fragment_delivered_order.setItemData(model,holder.getAdapterPosition());
                        }else if (fragment instanceof Fragment_Stumble_Order)
                        {
                            Fragment_Stumble_Order fragment_stumble_order = (Fragment_Stumble_Order) fragment;
                            fragment_stumble_order.setItemData(model,holder.getAdapterPosition());
                        }

                    }
                    );

        } else {
            LoadMoreHolder loadMoreHolder = (LoadMoreHolder) holder;
            loadMoreHolder.binding.progBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
       private OrderRowBinding binding;
        public MyHolder(OrderRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

    }

    public class LoadMoreHolder extends RecyclerView.ViewHolder {

        private ProgressLoadRowBinding binding;
        public LoadMoreHolder(ProgressLoadRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public int getItemViewType(int position) {
        OrderModel model = list.get(position);
        if (model == null) {
            return ITEM_LOAD;
        } else {
            return ITEM_DATA;

        }



    }
}
