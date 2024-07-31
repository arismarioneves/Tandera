package com.mari05lim.tandera.views.custom_views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mari05lim.tandera.R;
import com.mari05lim.tandera.util.UiUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * DEV Mari05liM
 */
public class MovieContextMenu extends LinearLayout {
    private static final int CONTEXT_MENU_WIDTH = UiUtils.getInstance().dpToPx(240);

    private int menuItem = -1;

    private OnMovieContextMenuItemClickListener onItemClickListener;

    public MovieContextMenu(Context context) {
        super(context);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.movies_popup_menu, this, true);
        setBackgroundResource(R.drawable.bg_container_shadow);
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(CONTEXT_MENU_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void bindToItem(int menuItem) {
        this.menuItem = menuItem;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.bind(this);
    }

    public void dismiss() {
        ((ViewGroup) getParent()).removeView(MovieContextMenu.this);
    }

    @OnClick(R.id.button_share)
    public void onShareMovieClick() {
        if (onItemClickListener != null) {
            onItemClickListener.onMovieContextMenuClick(menuItem, R.id.button_share);
        }
    }

    @OnClick(R.id.button_trailer)
    public void onSharePhotoClick() {
        if (onItemClickListener != null) {
            onItemClickListener.onMovieContextMenuClick(menuItem, R.id.button_trailer);
        }
    }

    @OnClick(R.id.button_web_search)
    public void onCopyShareUrlClick() {
        if (onItemClickListener != null) {
            onItemClickListener.onMovieContextMenuClick(menuItem, R.id.button_web_search);
        }
    }

    @OnClick(R.id.button_cancel)
    public void onCancelClick() {
        if (onItemClickListener != null) {
            onItemClickListener.onMovieContextMenuClick(menuItem, R.id.button_cancel);
        }
    }

    public void setOnMovieMenuItemClickListener(OnMovieContextMenuItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnMovieContextMenuItemClickListener {

        void onMovieContextMenuClick(int menuItem, int id);

    }

}