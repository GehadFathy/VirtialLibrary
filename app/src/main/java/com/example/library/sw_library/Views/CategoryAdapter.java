package com.example.library.sw_library.Views;
import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.library.sw_library.R;

import java.util.List;

/*Category adapter*/
public class CategoryAdapter extends RecyclerView.Adapter {
    private OnItemClickListener onClickListener;
    private Activity activity;
    private List<String> categories;

    /*constructor*/
    public CategoryAdapter(RecyclerView recyclerView, List<String> categories, Activity activity) {
        this.categories = categories;
        this.activity = activity;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    public void setOnClickListener(OnItemClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_recycler_view_row, parent, false);
        return new UserViewHolder(view);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String category = categories.get(position);
        UserViewHolder userViewHolder = (UserViewHolder) holder;
        userViewHolder.name.setText(category);
        userViewHolder.bind(categories.get(position), onClickListener);
    }
    @Override
    public int getItemCount() {
        return categories == null ? 0 : categories.size();
    }
    /*View holder*/
    private class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public View view ;
        public UserViewHolder(View view) {
            super(view);
            this.view = view;
            name = (TextView) view.findViewById(R.id.cat_name);
        }
        public void bind(final String category, final OnItemClickListener listener) {
            name.setText(category);
            view.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(category);
                }
            });
        }
    }
}