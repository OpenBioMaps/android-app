package hu.ektf.iot.openbiomapsapp.screen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import hu.ektf.iot.openbiomapsapp.R;
import hu.ektf.iot.openbiomapsapp.model.Form;
import hu.ektf.iot.openbiomapsapp.view.adapter.FormAdapter;
import hu.ektf.iot.openbiomapsapp.view.recyclerview.DividerItemDecoration;
import hu.ektf.iot.openbiomapsapp.view.recyclerview.ItemClickSupport;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class FormListActivity extends BaseActivity {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private FormAdapter adapter = new FormAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_list);

        refreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.list);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        ItemClickSupport.addTo(recyclerView)
                .setOnItemClickListener((recyclerView, position, view) -> {
                    Form form = adapter.getItemAtPosition(position);

                    Intent intent = new Intent(FormListActivity.this, FormActivity.class);
                    intent.putExtra(FormActivity.EXTRA_FORM_ID, form.getId());
                    startActivity(intent);
                });

        refreshLayout.setOnRefreshListener(this::loadForms);
        loadForms();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.form_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                .doOnTerminate(() -> refreshLayout.setRefreshing(false))
                .subscribe(forms -> adapter.swapItems(forms), Timber::e);
    }

    private void logout() {
        repo.logout()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Intent intent = new Intent(FormListActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });
    }
}
