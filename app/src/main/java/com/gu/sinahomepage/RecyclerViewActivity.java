package com.gu.sinahomepage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gu.indicatorwidget.TabLayout;
import com.gu.sinahomepage.view.horizontalscroll.HomePageHorScrollView;
import com.gu.sinahomepage.view.horizontalscroll.content.impl.MyLinearLayoutManager;
import com.gu.sinahomepage.view.horizontalscroll.content.impl.MyRecyclerView;

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
    for (int i = 0; i < 5; i++) {
      list.add("text " + i);
    }
    TabLayout tabLayout = findViewById(R.id.tab);
    tabLayout.createContentByTitles(titles).combine();
    HomePageHorScrollView horizontalScrollView = findViewById(R.id.horizontalScrollView);
    horizontalScrollView.bindTabLayout(tabLayout);
    rv1 = findViewById(R.id.rv1);
    rv2 = findViewById(R.id.rv2);
    rv3 = findViewById(R.id.rv3);
    LinearLayoutManager l1 = new MyLinearLayoutManager(this);
    LinearLayoutManager l2 = new MyLinearLayoutManager(this);
    LinearLayoutManager l3 = new MyLinearLayoutManager(this);

    l1.setOrientation(RecyclerView.VERTICAL);
    l2.setOrientation(RecyclerView.VERTICAL);
    l3.setOrientation(RecyclerView.VERTICAL);

    rv1.setLayoutManager(l1);
    rv2.setLayoutManager(l2);
    rv3.setLayoutManager(l3);

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
      }
    }
  }
}
