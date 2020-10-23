package com.example.installer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.example.installer.R;
import com.example.installer.databinding.ViewFilterTabItemBinding;


/**
 * Implementation of App Widget functionality.
 */
public class FilterTabItemView extends RelativeLayout {
    private ViewFilterTabItemBinding binding;
    private boolean isHighlight;

    public FilterTabItemView(Context context) {
        super(context);
        initView();
    }

    public FilterTabItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public FilterTabItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        binding = ViewFilterTabItemBinding.inflate(LayoutInflater.from(this.getContext()), this,true);
    }

    public void setTitle(String title) {
        binding.tvFilterTitle.setText(title);
    }

    public boolean isHighlight() {
        return isHighlight;
    }

    public void setHighlight(boolean highlight) {
        isHighlight = highlight;
        if (isHighlight) {
            binding.tvFilterTitle.setTextColor(getResources().getColor(R.color.titleTextSelectedColor));
            binding.tvFilterTitle.getCompoundDrawables()[2].setLevel(1);
        } else {
            binding.tvFilterTitle.setTextColor(getResources().getColor(R.color.titleTextDefaultColor));
            binding.tvFilterTitle.getCompoundDrawables()[2].setLevel(0);
        }
    }
}
