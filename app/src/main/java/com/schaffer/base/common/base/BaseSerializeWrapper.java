package com.schaffer.base.common.base;

import java.io.Serializable;

/**
 * Created by a7352 on 2017/7/18.
 */

public class BaseSerializeWrapper<T> implements Serializable {


    public BaseSerializeWrapper(T t) {
        this.t = t;
    }

    public T t;

}
