package com.dynamicg.bookmarkTree.ui;

import java.util.ArrayList;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.FolderStateHandler;
import com.dynamicg.bookmarkTree.R;
import com.dynamicg.bookmarkTree.model.Bookmark;
import com.dynamicg.common.main.Logger;
import com.dynamicg.common.ui.SimpleAlertDialog;

public class BookmarkListAdapter extends BaseAdapter {

	private static final Logger log = new Logger(BookmarkListAdapter.class);
	
	private final BookmarkTreeContext ctx;
	private final ListView listview;
	
	private ArrayList<Bookmark> bookmarks;
	private RowViewProvider rowViewProvider;

	public BookmarkListAdapter(BookmarkTreeContext ctx) {
		this.ctx = ctx;
		boolean compact = ctx.preferencesWrapper.isCompact();
		
		if (ctx.preferencesWrapper.isOptimisedLayout()) {
			rowViewProvider = new RowViewProvider.ProviderModern(ctx.getLayoutInflater(), compact);; 
		}
		else {
			rowViewProvider = new RowViewProvider.ProviderOldStyle(ctx.getLayoutInflater(), compact);
		}
		
		this.listview = (ListView)ctx.activity.findViewById(R.id.mainList);
		
		// prepare rounded white bg image
		FaviconImageView.setBackground(ctx.activity.getResources());
		
		updateBookmarkList();
		listview.setAdapter(this);
		
		// do *not* handle the "click" events on the individual view items
		// see http://www.mail-archive.com/android-developers@googlegroups.com/msg28348.html
		
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				itemClicked(position);
			}
		});
		
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				return itemLongClicked(position);
			}
		});
		
	}
	
	public View getView(final int position, View convertView, ViewGroup parent) {
		return rowViewProvider.getView ( bookmarks.get(position), convertView, parent);
		
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public Object getItem(int position) {
		return bookmarks.get(position);
	}
	
	@Override
	public int getCount() {
		return bookmarks.size();
	}
	
	private void itemClicked(int position) {
		Bookmark bm = bookmarks.get(position);
		if (bm==null) {
			log.warn("itemClicked - NULL?", position, bookmarks.size());
		}
		else if (bm.isFolder()) {
			bm.setExpanded(!bm.isExpanded());
			redraw();
			if (ctx.preferencesWrapper.isKeepState()) {
				FolderStateHandler.folderClicked(bm);
			}
		}
		else if (bm.getUrl()!=null) {
			openBookmark(bm.getUrl());
		}
	}
	
	private boolean itemLongClicked(int position) {
		Bookmark bm = bookmarks.get(position);
		if (bm==null) {
			log.warn("itemLongClicked - NULL?", position, bookmarks.size());
			return false;
		}
		else {
			new EditBookmarkDialog(ctx,bm);
			return true;
		}
	}
	
	private void updateBookmarkList() {
		bookmarks = ctx.bookmarkManager.getPresentationList();
	}
	
	// called by click event and via menu actions
	public void redraw() {
		updateBookmarkList();
//		if (rowViewProvider!=null) {
//			rowViewProvider.compact = ctx.preferencesWrapper.isCompact();
//		}
		// => does not work with 2.0++ due to convert view caching
		
		// force repaint
		listview.invalidateViews();
	}
	
	private void openBookmark(final String url) {
		Context context = ctx.activity;
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		try {
			context.startActivity(intent);
		}
		catch (ActivityNotFoundException e){
			new SimpleAlertDialog(context, R.string.hintNoIntent, R.string.commonOK) {
				@Override
				public String getPlainBodyText() {
					return "URL: " + (url.length()==0?"-":url);
				}
			};
		}
	}
	
}