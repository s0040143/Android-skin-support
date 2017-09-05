package skin.support.widget;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Shader;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableWrapper;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ProgressBar;

import skin.support.R;
import skin.support.SkinCompatManager;
import skin.support.content.res.SkinCompatResources;
import skin.support.content.res.SkinCompatTypedValue;

/**
 * Created by ximsfei on 2017/1/20.
 */

public class SkinCompatProgressBarHelper extends SkinCompatHelper {

    private static final int[] TINT_ATTRS = {
            android.R.attr.indeterminateDrawable,
            android.R.attr.progressDrawable
    };

    private final ProgressBar mView;

    private Bitmap mSampleTile;
    private SkinCompatTypedValue mIndeterminateDrawableTypedValue = new SkinCompatTypedValue();
    private SkinCompatTypedValue mProgressDrawableTypedValue = new SkinCompatTypedValue();
    private SkinCompatTypedValue mIndeterminateTintTypedValue = new SkinCompatTypedValue();

    SkinCompatProgressBarHelper(ProgressBar view) {
        mView = view;
    }

    void loadFromAttributes(AttributeSet attrs, int defStyleAttr) {
        SkinCompatTypedValue.getValue(attrs, TINT_ATTRS, 0, mIndeterminateDrawableTypedValue);
        SkinCompatTypedValue.getValue(attrs, TINT_ATTRS, 1, mProgressDrawableTypedValue);
        TypedArray a;
        if (SkinCompatManager.getInstance().isCompatibleMode()) {
            a = mView.getContext().obtainStyledAttributes(attrs, TINT_ATTRS, defStyleAttr, 0);
            if (!mIndeterminateDrawableTypedValue.isTypeRes() && a.hasValue(0)) {
                mIndeterminateDrawableTypedValue.type = SkinCompatTypedValue.TYPE_RESOURCES;
                mIndeterminateDrawableTypedValue.data = a.getResourceId(0, INVALID_ID);
            }
            if (!mProgressDrawableTypedValue.isTypeRes() && a.hasValue(1)) {
                mProgressDrawableTypedValue.type = SkinCompatTypedValue.TYPE_RESOURCES;
                mProgressDrawableTypedValue.data = a.getResourceId(1, INVALID_ID);
            }
            a.recycle();
        }
        if (Build.VERSION.SDK_INT > 21) {
            SkinCompatTypedValue.getValue(attrs, new int[]{android.R.attr.indeterminateTint}, 0, mIndeterminateTintTypedValue);
            if (SkinCompatManager.getInstance().isCompatibleMode()
                    && !mIndeterminateTintTypedValue.isTypeRes()) {
                a = mView.getContext().obtainStyledAttributes(attrs, new int[]{android.R.attr.indeterminateTint}, defStyleAttr, 0);
                if (a.hasValue(0)) {
                    mIndeterminateTintTypedValue.type = SkinCompatTypedValue.TYPE_RESOURCES;
                    mIndeterminateTintTypedValue.data = a.getResourceId(0, INVALID_ID);
                }
                a.recycle();
            }
        }
        applySkin();
    }

    /**
     * Converts a drawable to a tiled version of itself. It will recursively
     * traverse layer and state list drawables.
     */
    private Drawable tileify(Drawable drawable, boolean clip) {
        if (drawable instanceof DrawableWrapper) {
            Drawable inner = ((DrawableWrapper) drawable).getWrappedDrawable();
            if (inner != null) {
                inner = tileify(inner, clip);
                ((DrawableWrapper) drawable).setWrappedDrawable(inner);
            }
        } else if (drawable instanceof LayerDrawable) {
            LayerDrawable background = (LayerDrawable) drawable;
            final int N = background.getNumberOfLayers();
            Drawable[] outDrawables = new Drawable[N];

            for (int i = 0; i < N; i++) {
                int id = background.getId(i);
                outDrawables[i] = tileify(background.getDrawable(i),
                        (id == android.R.id.progress || id == android.R.id.secondaryProgress));
            }
            LayerDrawable newBg = new LayerDrawable(outDrawables);

            for (int i = 0; i < N; i++) {
                newBg.setId(i, background.getId(i));
            }

            return newBg;

        } else if (drawable instanceof BitmapDrawable) {
            final BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            final Bitmap tileBitmap = bitmapDrawable.getBitmap();
            if (mSampleTile == null) {
                mSampleTile = tileBitmap;
            }

            final ShapeDrawable shapeDrawable = new ShapeDrawable(getDrawableShape());
            final BitmapShader bitmapShader = new BitmapShader(tileBitmap,
                    Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
            shapeDrawable.getPaint().setShader(bitmapShader);
            shapeDrawable.getPaint().setColorFilter(bitmapDrawable.getPaint().getColorFilter());
            return (clip) ? new ClipDrawable(shapeDrawable, Gravity.LEFT,
                    ClipDrawable.HORIZONTAL) : shapeDrawable;
        }

        return drawable;
    }

    /**
     * Convert a AnimationDrawable for use as a barberpole animation.
     * Each frame of the animation is wrapped in a ClipDrawable and
     * given a tiling BitmapShader.
     */
    private Drawable tileifyIndeterminate(Drawable drawable) {
        if (drawable instanceof AnimationDrawable) {
            AnimationDrawable background = (AnimationDrawable) drawable;
            final int N = background.getNumberOfFrames();
            AnimationDrawable newBg = new AnimationDrawable();
            newBg.setOneShot(background.isOneShot());

            for (int i = 0; i < N; i++) {
                Drawable frame = tileify(background.getFrame(i), true);
                frame.setLevel(10000);
                newBg.addFrame(frame, background.getDuration(i));
            }
            newBg.setLevel(10000);
            drawable = newBg;
        }
        return drawable;
    }

    private Shape getDrawableShape() {
        final float[] roundedCorners = new float[]{5, 5, 5, 5, 5, 5, 5, 5};
        return new RoundRectShape(roundedCorners, null, null);
    }

    @Override
    public void applySkin() {
        if (!mIndeterminateDrawableTypedValue.isDataInvalid()) {
            if (mIndeterminateDrawableTypedValue.isTypeAttr()) {
                TypedArray a = SkinCompatResources.getInstance().obtainStyledAttributes(
                        mView.getContext(), new int[]{mIndeterminateDrawableTypedValue.data});
                Drawable drawable = a.getDrawable(0);
                if (drawable != null) {
                    drawable.setBounds(mView.getIndeterminateDrawable().getBounds());
                    mView.setIndeterminateDrawable(tileifyIndeterminate(drawable));
                }
                a.recycle();
            } else if (mIndeterminateDrawableTypedValue.isTypeRes()) {
                Drawable drawable = SkinCompatResources.getInstance().getDrawable(mIndeterminateDrawableTypedValue.data);
                drawable.setBounds(mView.getIndeterminateDrawable().getBounds());
                mView.setIndeterminateDrawable(tileifyIndeterminate(drawable));
            }
        }

        if (!mProgressDrawableTypedValue.isDataInvalid()) {
            if (mProgressDrawableTypedValue.isTypeAttr()) {
                TypedArray a = SkinCompatResources.getInstance().obtainStyledAttributes(
                        mView.getContext(), new int[]{mProgressDrawableTypedValue.data});
                mView.setProgressDrawable(tileify(a.getDrawable(0), false));
            } else if (mProgressDrawableTypedValue.isTypeRes()) {
                Drawable drawable = SkinCompatResources.getInstance().getDrawable(mProgressDrawableTypedValue.data);
                mView.setProgressDrawable(tileify(drawable, false));
            }
        }
        if (Build.VERSION.SDK_INT > 21) {
            if (!mIndeterminateTintTypedValue.isDataInvalid()) {
                if (mIndeterminateTintTypedValue.isTypeAttr()) {
                    TypedArray a = SkinCompatResources.getInstance().obtainStyledAttributes(
                            mView.getContext(), new int[]{mIndeterminateTintTypedValue.data});
                    mView.setIndeterminateTintList(a.getColorStateList(0));
                } else if (mIndeterminateTintTypedValue.isTypeRes()) {
                    mView.setIndeterminateTintList(SkinCompatResources.getInstance().getColorStateList(mIndeterminateTintTypedValue.data));
                }
            }
        }
    }

    private int checkProgressDrawableResId(int mProgressDrawableResId) {
        if (mProgressDrawableResId == R.drawable.abc_ratingbar_material) {
            return INVALID_ID;
        }
        return checkResourceId(mProgressDrawableResId);
    }
}
