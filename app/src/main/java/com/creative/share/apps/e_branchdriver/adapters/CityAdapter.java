package com.creative.share.apps.e_branchdriver.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.creative.share.apps.e_branchdriver.R;
import com.creative.share.apps.e_branchdriver.databinding.CityRowBinding;
import com.creative.share.apps.e_branchdriver.models.CityDataModel;

import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class CityAdapter extends BaseAdapter {

    private Context context;
    private List<CityDataModel.CityModel> list;
    private String lang;

    public CityAdapter(Context context, List<CityDataModel.CityModel> list) {
        this.context = context;
        this.list = list;
        Paper.init(context);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        @SuppressLint("ViewHolder") CityRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.city_row,viewGroup,false);
        CityDataModel.CityModel cityModel = list.get(i);
        binding.setLang(lang);
        binding.setModel(cityModel);
        return binding.getRoot();
    }
}
