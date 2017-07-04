package hu.ektf.iot.openbiomapsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import hu.ektf.iot.openbiomapsapp.adapter.DividerItemDecoration;
import hu.ektf.iot.openbiomapsapp.adapter.FormListAdapter;
import hu.ektf.iot.openbiomapsapp.object.Form;
import hu.ektf.iot.openbiomapsapp.repo.ObmRepository;
import hu.ektf.iot.openbiomapsapp.repo.ObmRepositoryImpl;
import hu.ektf.iot.openbiomapsapp.view.ItemClickSupport;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class FormListActivity extends AppCompatActivity {

    private ObmRepository repo = new ObmRepositoryImpl();

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private FormListAdapter adapter = new FormListAdapter();

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

        repo.loadFormList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        refreshLayout.setRefreshing(false);
                    }
                })
                .subscribe(new Action1<List<Form>>() {
                    @Override
                    public void call(List<Form> forms) {
                        adapter.setForms(forms);
                    }
                });
    }
}
