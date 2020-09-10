
package com.android.jwjy.wtjyproduct.event;


import com.android.jwjy.wtjyproduct.entity.ExpressionEntity;

/**
 * Created by asus on 2015/11/24.
 */
public interface OnExpressionListener {
    void OnExpressionSelected(ExpressionEntity entity);
    void OnExpressionRemove();
}
