package com.gu.sinahomepage;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gu.sinahomepage.view.content.MyRecyclerView;
import com.gu.sinahomepage.view.bottom.horizontalscroll.ViewPager;
import com.gu.sinahomepage.view.tab.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewActivity extends AppCompatActivity {
  private static final String[] titles = {"第一页", "第二页", "第三页"};

  List<String> list;
  MyRecyclerView rv1, rv2, rv3;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    setContentView(R.layout.activity_recycler_view);
    list = new ArrayList<>();
    for (int i = 0; i < 8; i++) {
      list.add("text " + i);
    }
    TabLayout tabLayout = findViewById(R.id.tab);
    tabLayout.createContentByTitles(titles).combine();
    ViewPager horizontalScrollView = findViewById(R.id.horizontalScrollView);
    horizontalScrollView.bindTab(tabLayout);
    rv1 = findViewById(R.id.rv1);
    rv2 = findViewById(R.id.rv2);
    rv3 = findViewById(R.id.rv3);

    rv1.setLayoutManager(new LinearLayoutManager(this));
    rv2.setLayoutManager(new LinearLayoutManager(this));
    rv3.setLayoutManager(new LinearLayoutManager(this));

    rv1.setAdapter(new DataAdapter(this));
    rv2.setAdapter(new DataAdapter(this));
    rv3.setAdapter(new DataAdapter(this));
  }

  class DataAdapter extends RecyclerView.Adapter {
    LayoutInflater inflater;

    DataAdapter(Context context) {
      super();
      inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      return new ViewHolder(inflater.inflate(R.layout.recyclerview_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
      ((TextView) holder.itemView).setText(list.get(position));
    }

    @Override
    public int getItemCount() {
      return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
      ViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                Log.e("TAG", "onClick: " + getAdapterPosition());
              }
            });
      }
    }
  }
}
