package com.schaffer.base.ui.dialog;

import android.animation.Animator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.schaffer.base.R;
import com.schaffer.base.common.base.BaseDialog;

public class LoadDialog extends BaseDialog {


    protected View rootView;
    protected LottieAnimationView mLavLottie;
    protected TextView mTvText;

    public LoadDialog(Context context) {
        super(context);
        view = View.inflate(context, R.layout.dialog_load, null);
        setLayoutConfig(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true, false);
        initView(view);
    }


    @Override
    protected void initView(View view) {
        super.initView(view);
        mLavLottie = (LottieAnimationView) rootView.findViewById(R.id.dialog_load_lav_lottie);
        mTvText = (TextView) rootView.findViewById(R.id.dialog_load_tv_text);
        mLavLottie.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        mLavLottie.setAnimationFromJson("material_wave_loading.json");
        mLavLottie.loop(true);
    }

    @Override
    public void show() {
        super.show();
        if (mLavLottie != null) {
            mLavLottie.playAnimation();
        }
    }

    @Override
    public void dismiss() {
        if (mLavLottie != null) {
            mLavLottie.pauseAnimation();
        }
        super.dismiss();
    }

    @Override
    public void cancel() {
        if (mLavLottie != null) {
            mLavLottie.pauseAnimation();
        }
        super.cancel();
    }
}
