package com.dynamicg.bookmarkTree.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dynamicg.bookmarkTree.R;
import com.dynamicg.bookmarkTree.model.Bookmark;
import com.dynamicg.common.main.Logger;

public abstract class RowViewProvider {

	private static final Logger log = new Logger(RowViewProvider.class);
	private static final int childLevelIndention = 32;
	
	public final LayoutInflater inflater;

	public RowViewProvider(LayoutInflater inflater) {
		this.inflater = inflater;
		if (log.isDebugEnabled()) {
			log.info("create RowViewProvider", this);
		}
	}
	
	public abstract View getView(Bookmark bm, View convertView, ViewGroup parent);
	
	public static class ProviderOldStyle extends RowViewProvider {

		public ProviderOldStyle(LayoutInflater inflater) {
			super(inflater);
		}

		private void prepare(View rowview, Bookmark bm) {
			
	        TextView titleCell = (TextView) rowview.findViewById(R.id.bmTitle);
	        titleCell.setText(bm.getDisplayTitle());
	        
	        View indentionCell = rowview.findViewById(R.id.bmIndention);
	    	indentionCell.getLayoutParams().width = bm.hasParentFolder() ? bm.getLevel() * childLevelIndention : 0; 
	    	
	    	if (bm.isBrowserBookmark()) {
		        TextView urlCell = (TextView) rowview.findViewById(R.id.bmUrl);
		        urlCell.setText(bm.getUrl());
	    	}

	        ImageView iconCell = (ImageView) rowview.findViewById(R.id.bmIcon);
			if (bm.isFolder()) {
		        iconCell.setImageResource(bm.isExpanded() ? R.drawable.folder_open : R.drawable.folder_dflt );
			}
			else {
		        iconCell.setImageBitmap(bm.getFavicon());
			}
	        
		}
		
		@Override
		public View getView(Bookmark bm, View convertView, ViewGroup parent) {
	        int resid = bm.isFolder() ? R.layout.list_row_folder : R.layout.list_row_bookmark;
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

		public ProviderModern(LayoutInflater inflater) {
			super(inflater);
		}
		
		private void prepare(ViewHolder holder, Bookmark bm) {
			
			holder.titleCell.setText(bm.getDisplayTitle());
	    	holder.indentionCell.getLayoutParams().width = bm.hasParentFolder() ? bm.getLevel() * childLevelIndention : 0; 
	    	
	    	if (bm.isBrowserBookmark()) {
		        holder.urlCell.setText(bm.getUrl());
		        holder.urlCell.setVisibility(View.VISIBLE);
	    	}
	    	else if (holder.urlCell!=null) {
	    		holder.urlCell.setText(null);
	    		holder.urlCell.setVisibility(View.GONE);
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
		        convertView = inflater.inflate(R.layout.list_row_relative, parent, false);
		        
				holder = new ViewHolder();
				holder.titleCell = (TextView) convertView.findViewById(R.id.bmTitle);
				holder.indentionCell = convertView.findViewById(R.id.bmIndention);
				holder.iconCell = (FaviconImageView) convertView.findViewById(R.id.bmIcon);
				holder.urlCell = (TextView) convertView.findViewById(R.id.bmUrl);
		    	convertView.setTag(holder);
			}
			
	        prepare(holder, bm);
			
			return convertView;
			
		}
		
	}
	
}
