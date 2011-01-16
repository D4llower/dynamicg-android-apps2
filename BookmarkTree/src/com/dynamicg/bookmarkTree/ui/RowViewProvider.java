package com.dynamicg.bookmarkTree.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dynamicg.bookmarkTree.R;
import com.dynamicg.bookmarkTree.model.Bookmark;
import com.dynamicg.bookmarkTree.prefs.PrefEntryInt;
import com.dynamicg.bookmarkTree.prefs.PreferencesWrapper;
import com.dynamicg.common.Logger;

public abstract class RowViewProvider {

	private static final Logger log = new Logger(RowViewProvider.class);
	private static final int childLevelIndention = 32;
	
	public final LayoutInflater inflater;
	public final boolean compact;
	
	public boolean applyTextColors;

	public RowViewProvider(LayoutInflater inflater, boolean compact) {
		this.inflater = inflater;
		this.compact = compact;
		beforeRedraw();
		
		if (log.debugEnabled) {
			log.debug("create RowViewProvider", this);
		}
	}
	
	public abstract View getView(Bookmark bm, View convertView, ViewGroup parent);
	
	public static class ProviderOldStyle extends RowViewProvider {

		public ProviderOldStyle(LayoutInflater inflater, boolean compact) {
			super(inflater, compact);
		}

		private void prepare(View rowview, Bookmark bm) {
			
	        TextView titleCell = (TextView) rowview.findViewById(R.id.bmTitle);
	        titleCell.setText(bm.getDisplayTitle());
	        if (applyTextColors) {
	        	titleCell.setTextColor(bm.isFolder() ? PreferencesWrapper.colorFolder.value : PreferencesWrapper.colorBookmarkTitle.value );
	        }
	        
	        View indentionCell = rowview.findViewById(R.id.bmIndention);
	    	indentionCell.getLayoutParams().width = bm.hasParentFolder() ? bm.getLevel() * childLevelIndention : 0; 
	    	
	    	if (!compact) {
		    	if (bm.isBrowserBookmark()) {
			        TextView urlCell = (TextView) rowview.findViewById(R.id.bmUrl);
			        urlCell.setText(bm.getUrl());
			        if (applyTextColors) {
			        	urlCell.setTextColor(PreferencesWrapper.colorBookmarkUrl.value);
			        }
		    	}
	    	}

	        ImageView iconCell = (ImageView) rowview.findViewById(R.id.bmIcon);
	    	if (compact && bm.isFolder()) {
		    	((FaviconImageView)iconCell).isFolder = true;
	    	}
			if (bm.isFolder()) {
		        iconCell.setImageResource(bm.isExpanded() ? R.drawable.folder_open : R.drawable.folder_dflt );
			}
			else {
		        iconCell.setImageBitmap(bm.getFavicon());
			}
	        
		}
		
		@Override
		public View getView(Bookmark bm, View convertView, ViewGroup parent) {
	        int resid;
	        if (compact) {
	        	resid = R.layout.list_row_compact;
	        }
	        else {
	        	resid = bm.isFolder() ? R.layout.list15_row_folder : R.layout.list15_row_bookmark;
	        }
	        View rowview = inflater.inflate(resid, null);
	        prepare(rowview, bm);
			return rowview;
		}
		
	}
	
	static class ViewHolder {
		TextView titleCell;
		View indentionCell;
		FaviconImageView iconCell;
		TextView urlCell;
	}

	public static class ProviderModern extends RowViewProvider {

		private final int layoutId;

		public ProviderModern(LayoutInflater inflater, boolean compact) {
			super(inflater, compact);
			this.layoutId = compact ? R.layout.list_row_compact : R.layout.list20_row_relative;
		}
		
		private void prepare(ViewHolder holder, Bookmark bm) {
			
			holder.titleCell.setText(bm.getDisplayTitle());
	        if (applyTextColors) {
	        	holder.titleCell.setTextColor(bm.isFolder() ? PreferencesWrapper.colorFolder.value : PreferencesWrapper.colorBookmarkTitle.value );
	        }
	    	holder.indentionCell.getLayoutParams().width = bm.hasParentFolder() ? bm.getLevel() * childLevelIndention : 0; 
	    	
	    	if (!compact) {
		    	if (bm.isBrowserBookmark()) {
			        holder.urlCell.setText(bm.getUrl());
			        holder.urlCell.setVisibility(View.VISIBLE);
			        if (applyTextColors) {
			        	holder.urlCell.setTextColor(PreferencesWrapper.colorBookmarkUrl.value);
			        }
		    	}
		    	else if (holder.urlCell!=null) {
		    		holder.urlCell.setText(null);
		    		holder.urlCell.setVisibility(View.GONE);
		    	}
	    	}

	    	holder.iconCell.isFolder = bm.isFolder();
			if (bm.isFolder()) {
		        holder.iconCell.setImageResource(bm.isExpanded() ? R.drawable.folder_open : R.drawable.folder_dflt );
			}
			else {
				holder.iconCell.setImageBitmap(bm.getFavicon());
			}
	        
		}
		
		@Override
		public View getView(Bookmark bm, View convertView, ViewGroup parent) {

			ViewHolder holder;
			if (convertView!=null) {
				holder = (ViewHolder)convertView.getTag();
			}
			else {
		        convertView = inflater.inflate(layoutId, parent, false);
		        
				holder = new ViewHolder();
				holder.titleCell = (TextView) convertView.findViewById(R.id.bmTitle);
				holder.indentionCell = convertView.findViewById(R.id.bmIndention);
				holder.iconCell = (FaviconImageView) convertView.findViewById(R.id.bmIcon);
				if (!compact) {
					holder.urlCell = (TextView) convertView.findViewById(R.id.bmUrl);
				}
		    	convertView.setTag(holder);
			}
			
	        prepare(holder, bm);
			
			return convertView;
			
		}
		
	}

	private static boolean nonWhite(PrefEntryInt item) {
		return item.value != Color.WHITE;
	}

	public void beforeRedraw() {
		if ( nonWhite(PreferencesWrapper.colorFolder)
				|| nonWhite(PreferencesWrapper.colorBookmarkTitle)
				|| nonWhite(PreferencesWrapper.colorBookmarkUrl)
		) {
			this.applyTextColors = true;
		}
	}

}
