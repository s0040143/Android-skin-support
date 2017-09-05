package skin.support.app;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;

import skin.support.SkinCompatManager;
import skin.support.content.res.SkinCompatResources;
import skin.support.observe.SkinObservable;
import skin.support.observe.SkinObserver;
import skin.support.widget.SkinCompatThemeUtils;

import static skin.support.widget.SkinCompatHelper.INVALID_ID;
import static skin.support.widget.SkinCompatHelper.checkResourceId;

/**
 * Created by ximsfei on 17-1-8.
 */
@Deprecated
public class SkinCompatActivity extends AppCompatActivity implements SkinObserver {

    private SkinCompatDelegate mSkinDelegate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory(getLayoutInflater(), getSkinDelegate());
        super.onCreate(savedInstanceState);
        updateStatusBarColor();
        updateWindowBackground();
    }

    @NonNull
    public SkinCompatDelegate getSkinDelegate() {
        if (mSkinDelegate == null) {
            mSkinDelegate = SkinCompatDelegate.create(this);
        }
        return mSkinDelegate;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SkinCompatManager.getInstance().addObserver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinCompatManager.getInstance().deleteObserver(this);
    }

    /**
     * @return true: 打开5.0以上状态栏换肤, false: 关闭5.0以上状态栏换肤;
     */
    protected boolean skinStatusBarColorEnable() {
        return true;
    }

    protected void updateStatusBarColor() {
        if (SkinCompatManager.getInstance().isSkinStatusBarColorEnable()
                && skinStatusBarColorEnable()
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int color = SkinCompatThemeUtils.getStatusBarColor(this);
            if (color != 0) {
                getWindow().setStatusBarColor(color);
            }
            if (SkinCompatManager.getInstance().isCompatibleMode()) {
                int statusBarColorResId = SkinCompatThemeUtils.getStatusBarColorResId(this);
                int colorPrimaryDarkResId = SkinCompatThemeUtils.getColorPrimaryDarkResId(this);
                if (checkResourceId(statusBarColorResId) != INVALID_ID) {
                    getWindow().setStatusBarColor(SkinCompatResources.getInstance().getColor(statusBarColorResId));
                } else if (checkResourceId(colorPrimaryDarkResId) != INVALID_ID) {
                    getWindow().setStatusBarColor(SkinCompatResources.getInstance().getColor(colorPrimaryDarkResId));
                }
            }
        }
    }

    protected void updateWindowBackground() {
        if (!SkinCompatManager.getInstance().isSkinWindowBackgroundEnable()) {
            return;
        }
        Drawable drawable = SkinCompatThemeUtils.getWindowBackgroundDrawable(this);
        if (drawable != null) {
            getWindow().setBackgroundDrawable(drawable);
        }
        if (SkinCompatManager.getInstance().isCompatibleMode()) {
            int windowBackgroundResId = SkinCompatThemeUtils.getWindowBackgroundResId(this);
            if (checkResourceId(windowBackgroundResId) != INVALID_ID) {
                String typeName = getResources().getResourceTypeName(windowBackgroundResId);
                if ("color".equals(typeName)) {
                    getWindow().setBackgroundDrawable(
                            new ColorDrawable(SkinCompatResources.getInstance().getColor(windowBackgroundResId)));
                } else if ("drawable".equals(typeName)) {
                    getWindow().setBackgroundDrawable(
                            SkinCompatResources.getInstance().getDrawable(windowBackgroundResId));
                } else if ("mipmap".equals(typeName)) {
                    getWindow().setBackgroundDrawable(
                            SkinCompatResources.getInstance().getMipmap(windowBackgroundResId));
                }
            }
        }
    }

    @Override
    public void updateSkin(SkinObservable observable, Object o) {
        updateStatusBarColor();
        updateWindowBackground();
        getSkinDelegate().applySkin();
    }
}
