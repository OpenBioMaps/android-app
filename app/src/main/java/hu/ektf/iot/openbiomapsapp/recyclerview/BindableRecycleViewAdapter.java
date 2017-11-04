package hu.ektf.iot.openbiomapsapp.recyclerview;

import android.view.View;

public abstract class BindableRecycleViewAdapter<T, V extends View & BindableRecycleViewAdapter.Bindable<T>>
        extends BaseRecyclerViewAdapter<T, V> {

    @Override
    public void onBindViewHolder(ViewWrapper<V> holder, int position) {
        holder.getView().bind(getItemAtPosition(position));
    }

    public interface Bindable<T> {

        void bind(T item);
    }
}
