package com.writercorporation.utils;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class TextChangeListener<T> implements TextWatcher {
    private T target;

    public TextChangeListener(T target) {
        this.target = target;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        this.onTextChanged(target, s);
    }

    public abstract void onTextChanged(T target, Editable s);
}
