package com.app2m.samples;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

/**
 * Created by CongHao on 2017/10/2.
 * E-mail: hao.cong@app2m.com
 */

public class ItemVM extends BaseObservable {
    private String str;
    public ItemVM(String str) {
        this.str = str;
    }

    @Bindable
    public String getStr() {
        return str;
    }
}
