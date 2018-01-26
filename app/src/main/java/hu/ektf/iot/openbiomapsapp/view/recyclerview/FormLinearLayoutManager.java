package hu.ektf.iot.openbiomapsapp.view.recyclerview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import hu.ektf.iot.openbiomapsapp.R;

import static android.support.v7.widget.RecyclerView.NO_POSITION;

public class FormLinearLayoutManager extends LinearLayoutManager {
    private OnDoneClickListener listener;

    public FormLinearLayoutManager(Context context) {
        super(context);
    }

    public FormLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public FormLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setListener(OnDoneClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);

        final int firstVisibleItemPosition = findFirstVisibleItemPosition();
        final int lastItemPos = getChildCount() - 1;

        if (firstVisibleItemPosition == NO_POSITION || lastItemPos < 0) {
            return;
        }

        setDoneAction(lastItemPos);
    }

    private void setDoneAction(int lastItemPos) {
        for (int i = lastItemPos; 0 <= i; i--) {
            View child = getChildAt(i);
            View input = child.findViewById(R.id.input);

            if (input == null || !(input instanceof EditText)) {
                continue;
            }

            ((EditText) input).setImeOptions(EditorInfo.IME_ACTION_DONE);
            ((EditText) input).setOnEditorActionListener((textView, actionId, keyEvent) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (listener != null) {
                        listener.onDoneClick();
                    }

                    return true;
                }

                return false;
            });
            break;
        }
    }

    public interface OnDoneClickListener {

        void onDoneClick();
    }
}
