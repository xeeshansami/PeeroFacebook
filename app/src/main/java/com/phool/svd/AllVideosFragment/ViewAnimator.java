package com.phool.svd.AllVideosFragment;

import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.ViewPropertyAnimatorUpdateListener;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.Interpolator;

public class ViewAnimator {
    View view;

    public static class AnimatorExecutor {
        final ViewPropertyAnimatorCompat animator;
        Listeners.Cancel cancelListener;
        Listeners.End endListener;
        Listeners.Start startListener;
        Listeners.Update updateListener;
        final ViewAnimator viewAnimator;

        AnimatorExecutor(ViewAnimator viewAnimator) {
            this.animator = ViewCompat.animate(viewAnimator.view);
            this.viewAnimator = viewAnimator;
            this.animator.setListener(new AnimatorListener(this));
        }

        public AnimatorExecutor alpha(float alpha) {
            this.animator.alpha(alpha);
            return this;
        }

        public AnimatorExecutor alpha(float from, float to) {
            this.viewAnimator.alpha(from);
            return alpha(to);
        }

        public AnimatorExecutor scaleX(float scale) {
            this.animator.scaleX(scale);
            return this;
        }

        public AnimatorExecutor scaleX(float from, float to) {
            this.viewAnimator.scaleX(from);
            return scaleX(to);
        }

        public AnimatorExecutor scaleY(float scale) {
            this.animator.scaleY(scale);
            return this;
        }

        public AnimatorExecutor scaleY(float from, float to) {
            this.viewAnimator.scaleY(from);
            return scaleY(to);
        }

        public AnimatorExecutor scale(float scale) {
            this.animator.scaleX(scale);
            this.animator.scaleY(scale);
            return this;
        }

        public AnimatorExecutor scale(float from, float to) {
            this.viewAnimator.scale(from);
            return scale(to);
        }

        public AnimatorExecutor translationX(float translation) {
            this.animator.translationX(translation);
            return this;
        }

        public AnimatorExecutor translationX(float from, float to) {
            this.viewAnimator.translationX(from);
            return translationX(to);
        }

        public AnimatorExecutor translationY(float translation) {
            this.animator.translationY(translation);
            return this;
        }

        public AnimatorExecutor translationY(float from, float to) {
            this.viewAnimator.translationY(from);
            return translationY(to);
        }

        public AnimatorExecutor translation(float translationX, float translationY) {
            this.animator.translationX(translationX);
            this.animator.translationY(translationY);
            return this;
        }

        public AnimatorExecutor rotation(float rotation) {
            this.animator.rotation(rotation);
            return this;
        }

        public AnimatorExecutor duration(long duration) {
            this.animator.setDuration(duration);
            return this;
        }

        public AnimatorExecutor startDelay(long duration) {
            this.animator.setStartDelay(duration);
            return this;
        }

        public AnimatorExecutor interpolator(Interpolator interpolator) {
            this.animator.setInterpolator(interpolator);
            return this;
        }

        public AnimatorExecutor end(Listeners.End listener) {
            this.endListener = listener;
            return this;
        }

        public AnimatorExecutor update(Listeners.Update listener) {
            this.updateListener = listener;
            this.animator.setUpdateListener(new AnimatorUpdate(this));
            return this;
        }

        public AnimatorExecutor start(Listeners.Start listener) {
            this.startListener = listener;
            return this;
        }

        public AnimatorExecutor cancel(Listeners.Cancel listener) {
            this.cancelListener = listener;
            return this;
        }

        public ViewAnimator pullOut() {
            return this.viewAnimator;
        }

        public AnimatorExecutor thenAnimate(View view) {
            AnimatorExecutor animatorExecutor = new ViewAnimator(view).animate();
            animatorExecutor.startDelay(this.animator.getStartDelay() + this.animator.getDuration());
            return animatorExecutor;
        }

        public AnimatorExecutor andAnimate(View view) {
            ViewAnimator viewAnimator = new ViewAnimator(view);
            viewAnimator.animate().startDelay(this.animator.getStartDelay());
            return viewAnimator.animate();
        }
    }

    public static class Listeners {

        public interface Cancel {
            void onCancel();
        }

        public interface End {
            void onEnd();
        }

        public interface Size {
            void onSize(ViewAnimator viewAnimator);
        }

        public interface Start {
            void onStart();
        }

        public interface Update {
            void update();
        }
    }

    static class AnimatorListener implements ViewPropertyAnimatorListener {
        AnimatorExecutor animatorExecutor;

        public AnimatorListener(AnimatorExecutor animatorExecutor) {
            this.animatorExecutor = animatorExecutor;
        }

        public void onAnimationStart(View view) {
            AnimatorExecutor animatorExecutor = this.animatorExecutor;
            if (animatorExecutor != null && animatorExecutor.startListener != null) {
                animatorExecutor.startListener.onStart();
            }
        }

        public void onAnimationEnd(View view) {
            AnimatorExecutor animatorExecutor = this.animatorExecutor;
            if (animatorExecutor != null && animatorExecutor.endListener != null) {
                animatorExecutor.endListener.onEnd();
            }
        }

        public void onAnimationCancel(View view) {
            AnimatorExecutor animatorExecutor = this.animatorExecutor;
            if (animatorExecutor != null && animatorExecutor.cancelListener != null) {
                animatorExecutor.cancelListener.onCancel();
            }
        }
    }

    static class AnimatorUpdate implements ViewPropertyAnimatorUpdateListener {
        AnimatorExecutor animatorExecutor;

        public AnimatorUpdate(AnimatorExecutor animatorExecutor) {
            this.animatorExecutor = animatorExecutor;
        }

        public void onAnimationUpdate(View view) {
            if (this.animatorExecutor != null && this.animatorExecutor.updateListener != null) {
                this.animatorExecutor.updateListener.update();
            }
        }
    }

    public ViewAnimator(View view) {
        this.view = view;
    }

    public static ViewAnimator putOn(View view) {
        return new ViewAnimator(view);
    }

    public ViewAnimator andPutOn(View view) {
        this.view = view;
        return this;
    }

    public void waitForSize(final Listeners.Size sizeListener) {
        this.view.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
            public boolean onPreDraw() {
                if (ViewAnimator.this.view != null) {
                    ViewAnimator.this.view.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (sizeListener != null) {
                        sizeListener.onSize(ViewAnimator.this);
                    }
                }
                return false;
            }
        });
    }

    public float getY() {
        Rect rect = new Rect();
        this.view.getGlobalVisibleRect(rect);
        return (float) rect.top;
    }

    public float getX() {
        return ViewCompat.getX(this.view);
    }

    public ViewAnimator alpha(float alpha) {
        if (this.view != null) {
            ViewCompat.setAlpha(this.view, alpha);
        }
        return this;
    }

    public ViewAnimator scaleX(float scale) {
        if (this.view != null) {
            ViewCompat.setScaleX(this.view, scale);
        }
        return this;
    }

    public ViewAnimator scaleY(float scale) {
        if (this.view != null) {
            ViewCompat.setScaleY(this.view, scale);
        }
        return this;
    }

    public ViewAnimator scale(float scale) {
        if (this.view != null) {
            ViewCompat.setScaleX(this.view, scale);
            ViewCompat.setScaleY(this.view, scale);
        }
        return this;
    }

    public ViewAnimator translationX(float translation) {
        if (this.view != null) {
            ViewCompat.setTranslationX(this.view, translation);
        }
        return this;
    }

    public ViewAnimator translationY(float translation) {
        if (this.view != null) {
            ViewCompat.setTranslationY(this.view, translation);
        }
        return this;
    }

    public ViewAnimator translation(float translationX, float translationY) {
        if (this.view != null) {
            ViewCompat.setTranslationX(this.view, translationX);
            ViewCompat.setTranslationY(this.view, translationY);
        }
        return this;
    }

    public ViewAnimator pivotX(float percent) {
        if (this.view != null) {
            ViewCompat.setPivotX(this.view, ((float) this.view.getWidth()) * percent);
        }
        return this;
    }

    public ViewAnimator pivotY(float percent) {
        if (this.view != null) {
            ViewCompat.setPivotY(this.view, ((float) this.view.getHeight()) * percent);
        }
        return this;
    }

    public ViewAnimator visible() {
        if (this.view != null) {
            this.view.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public ViewAnimator invisible() {
        if (this.view != null) {
            this.view.setVisibility(View.INVISIBLE);
        }
        return this;
    }

    public ViewAnimator gone() {
        if (this.view != null) {
            this.view.setVisibility(View.GONE);
        }
        return this;
    }

    public AnimatorExecutor animate() {
        return new AnimatorExecutor(this);
    }
}
