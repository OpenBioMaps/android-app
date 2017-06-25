package hu.ektf.iot.openbiomapsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import hu.ektf.iot.openbiomapsapp.adapter.DividerItemDecoration;
import hu.ektf.iot.openbiomapsapp.adapter.FormAdapter;
import hu.ektf.iot.openbiomapsapp.object.Form;
import hu.ektf.iot.openbiomapsapp.view.ItemClickSupport;

public class FormListActivity extends AppCompatActivity {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private FormAdapter adapter = new FormAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_list);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) findViewById(R.id.list);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        ItemClickSupport.addTo(recyclerView)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        // TODO Put form id in extra
                        Intent intent = new Intent(FormListActivity.this, FormActivity.class);
                        startActivity(intent);
                    }
                });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadForms();
            }
        });
        loadForms();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ItemClickSupport.removeFrom(recyclerView);
    }

    private void loadForms() {
        if (!refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(true);
        }

        List<Form> forms = new ArrayList<>();
        Form form = new Form();
        form.setId(1);
        form.setVisibility("Test Form");
        forms.add(form);
        form = new Form();
        form.setId(2);
        form.setVisibility("Cool form");
        forms.add(form);
        form = new Form();
        form.setId(3);
        form.setVisibility("The best form evaaar");
        forms.add(form);

        adapter.setForms(forms);
        refreshLayout.setRefreshing(false);
    }
}
