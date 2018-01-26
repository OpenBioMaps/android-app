package hu.ektf.iot.openbiomapsapp.view.recyclerview;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerViewAdapter<T, V extends View> extends RecyclerView.Adapter<ViewWrapper<V>> {

    protected List<T> items = new ArrayList<>();

    @Override
    public int getItemCount() {
        return items.size();
    }

    public T getItemAtPosition(int position) {
        return items.get(position);
    }

    @Override
    public final ViewWrapper<V> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewWrapper<>(onCreateItemView(parent, viewType));
    }

    protected abstract V onCreateItemView(ViewGroup parent, int viewType);

    @CallSuper
    public void swapItems(List<T> newItems) {
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(getDiffCallback(
                this.items,
                newItems
        ), true);

        this.items.clear();

        if (newItems != null) {
            this.items.addAll(newItems);
        }

        result.dispatchUpdatesTo(this);
    }

    // TODO DiffUtil does not work with FormControl lists. Investigate why?
    public void setItems(List<T> newItems) {
        this.items.clear();

        if (newItems != null) {
            this.items.addAll(newItems);
        }

        notifyDataSetChanged();
    }

    public void addItem(T item, int position) {
        items.add(position, item);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    // default implem
    @SuppressWarnings("unchecked")
    @NonNull
    public DiffCallback getDiffCallback(List<T> oldList, List<T> newList) {
        return new DiffCallback(oldList, newList);
    }

    public static class DiffCallback<T> extends DiffUtil.Callback {
        protected final List<T> oldList;
        protected final List<T> newList;

        public DiffCallback(List<T> oldList, List<T> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList == null ? 0 : oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList == null ? 0 : newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }

}
