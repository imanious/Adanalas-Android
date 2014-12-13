package com.handmark.pulltorefresh.library;

import java.util.HashSet;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import com.handmark.pulltorefresh.library.internal.LoadingLayout;

public class LoadingLayoutProxy implements ILoadingLayout {

	private final HashSet<LoadingLayout> mLoadingLayouts;

	LoadingLayoutProxy() {
		mLoadingLayouts = new HashSet<LoadingLayout>();
	}

	/**
	 * This allows you to add extra LoadingLayout instances to this proxy. This
	 * is only necessary if you keep your own instances, and want to have them
	 * included in any
	 * {@link PullToRefreshBase#createLoadingLayoutProxy(boolean, boolean)
	 * createLoadingLayoutProxy(...)} calls.
	 * 
	 * @param layout - LoadingLayout to have included.
	 */
	public void addLayout(LoadingLayout layout) {
		if (null != layout) {
			mLoadingLayouts.add(layout);
		}
	}

	@Override
	public void setLastUpdatedLabel(CharSequence label) {
		for (LoadingLayout layout : mLoadingLayouts) {
			layout.setLastUpdatedLabel(label);
		}
	}

	@Override
	public void setLoadingDrawable(Drawable drawable) {
		for (LoadingLayout layout : mLoadingLayouts) {
			layout.setLoadingDrawable(drawable);
		}
	}

	@Override
	public void setRefreshingLabel(CharSequence refreshingLabel) {
		for (LoadingLayout layout : mLoadingLayouts) {
			layout.setRefreshingLabel(refreshingLabel);
		}
	}

	@Override
	public void setPullLabelUp(CharSequence label) {
		for (LoadingLayout layout : mLoadingLayouts) {
			layout.setPullLabelUp(label);
		}
	}

	@Override
	public void setPullLabelBottom(CharSequence label) {
		for (LoadingLayout layout : mLoadingLayouts) {
			layout.setPullLabelBottom(label);
		}
	}
	
	@Override
	public void setReleaseLabelUp(CharSequence label) {
		for (LoadingLayout layout : mLoadingLayouts) {
			layout.setReleaseLabelUp(label);
		}
	}

	@Override
	public void setReleaseLabelBottom(CharSequence label) {
		for (LoadingLayout layout : mLoadingLayouts) {
			layout.setReleaseLabelBottom(label);
		}
	}
	
	public void setTextTypeface(Typeface tf) {
		for (LoadingLayout layout : mLoadingLayouts) {
			layout.setTextTypeface(tf);
		}
	}

	@Override
	public void setSubTextTypeface(Typeface tf) {
		for (LoadingLayout layout : mLoadingLayouts) {
			layout.setSubTextTypeface(tf);
		}
	}

	@Override
	public void setTextSize(int unit, float textSize) {
		for (LoadingLayout layout : mLoadingLayouts) {
			layout.setTextSize(unit, textSize);
		}
	}

	@Override
	public void setSubTextSize(int unit, float textSize) {
		for (LoadingLayout layout : mLoadingLayouts) {
			layout.setSubTextSize(unit, textSize);
		}		
	}

	@Override
	public void setSubTextInclueFontPadding(boolean include) {
		for (LoadingLayout layout : mLoadingLayouts) {
			layout.setSubTextInclueFontPadding(include);
		}		
	}
	
	@Override
	public void setTextInclueFontPadding(boolean include) {
		for (LoadingLayout layout : mLoadingLayouts) {
			layout.setTextInclueFontPadding(include);
		}		
	}
}
