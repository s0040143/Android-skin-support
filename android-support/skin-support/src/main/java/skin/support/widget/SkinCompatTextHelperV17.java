package skin.support.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatDrawableManager;
import android.util.AttributeSet;
import android.widget.TextView;

import skin.support.R;
import skin.support.content.res.SkinCompatResources;
import skin.support.content.res.SkinCompatTypedValue;

/**
 * Created by pengfengwang on 2017/3/8.
 */

@RequiresApi(17)
@TargetApi(17)
public class SkinCompatTextHelperV17 extends SkinCompatTextHelper {
    private SkinCompatTypedValue mDrawableStartTypedValue = new SkinCompatTypedValue();
    private SkinCompatTypedValue mDrawableEndTypedValue = new SkinCompatTypedValue();

    public SkinCompatTextHelperV17(TextView view) {
        super(view);
    }

    @Override
    public void loadFromAttributes(AttributeSet attrs, int defStyleAttr) {
        final Context context = mView.getContext();

        SkinCompatTypedValue.getValue(
                context,
                attrs,
                defStyleAttr,
                R.styleable.SkinCompatTextHelper,
                R.styleable.SkinCompatTextHelper_android_drawableStart,
                mDrawableStartTypedValue);
        SkinCompatTypedValue.getValue(
                context,
                attrs,
                defStyleAttr,
                R.styleable.SkinCompatTextHelper,
                R.styleable.SkinCompatTextHelper_android_drawableEnd,
                mDrawableEndTypedValue);
        super.loadFromAttributes(attrs, defStyleAttr);
    }

    public void onSetCompoundDrawablesRelativeWithIntrinsicBounds(
            @DrawableRes int start, @DrawableRes int top, @DrawableRes int end, @DrawableRes int bottom) {
        mDrawableStartTypedValue.setData(start);
        mDrawableEndTypedValue.setData(end);
        mDrawableTopTypedValue.setData(top);
        mDrawableBottomTypedValue.setData(bottom);
        applyCompoundDrawablesRelativeResource();
    }

    @Override
    protected void applyCompoundDrawablesRelativeResource() {
        Drawable drawableLeft = mDrawableLeftTypedValue.getDrawable();
        Drawable drawableTop = mDrawableTopTypedValue.getDrawable();
        Drawable drawableRight = mDrawableRightTypedValue.getDrawable();
        Drawable drawableBottom = mDrawableBottomTypedValue.getDrawable();
        Drawable drawableStart = mDrawableStartTypedValue.getDrawable();
        Drawable drawableEnd = mDrawableEndTypedValue.getDrawable();

        if (drawableStart == null) {
            drawableStart = drawableLeft;
        }
        if (drawableEnd == null) {
            drawableEnd = drawableRight;
        }
        if (drawableStart != null
                || drawableTop != null
                || drawableEnd != null
                || drawableBottom != null) {
            mView.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, drawableTop, drawableEnd, drawableBottom);
        }
    }

    @Override
    public void applySkin() {
        super.applySkin();
        applyCompoundDrawablesRelativeResource();
    }
}
