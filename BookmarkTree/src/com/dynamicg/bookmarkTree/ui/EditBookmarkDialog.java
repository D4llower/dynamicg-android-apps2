package com.dynamicg.bookmarkTree.ui;

import java.util.ArrayList;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.R;
import com.dynamicg.bookmarkTree.data.writehandler.BookmarkDeletionHandler;
import com.dynamicg.bookmarkTree.data.writehandler.BookmarkUpdateHandler;
import com.dynamicg.bookmarkTree.data.writer.BookmarkWriter;
import com.dynamicg.bookmarkTree.model.Bookmark;
import com.dynamicg.bookmarkTree.model.BrowserBookmarkBean;
import com.dynamicg.bookmarkTree.model.FolderBean;
import com.dynamicg.bookmarkTree.util.CommonDialogHelper;
import com.dynamicg.bookmarkTree.util.DialogButtonPanelWrapper;
import com.dynamicg.common.main.Logger;
import com.dynamicg.common.main.StringUtil;
import com.dynamicg.common.ui.SimpleAlertDialog;

public class EditBookmarkDialog extends Dialog {

	private static final Logger log = new Logger(EditBookmarkDialog.class);
	public static final BrowserBookmarkBean NEW_BOOKMARK = new BrowserBookmarkBean(-1, "", "", null);
	
	private final BookmarkTreeContext ctx;
	private final Bookmark bookmark;
	private final boolean forCreateBookmark;
	
	private EditText newNodeTitleItem;
	private Spinner parentFolderSpinner;
	private EditText addToNewFolderItem;
	private EditText urlItem;

	public EditBookmarkDialog(BookmarkTreeContext ctx, Bookmark bookmark) {
		super(ctx.activity);
		this.ctx = ctx;
		this.bookmark = bookmark;
		this.forCreateBookmark = bookmark==NEW_BOOKMARK;
		CommonDialogHelper.expandContent(this, R.layout.edit_body);
		this.show();
	}

	public EditBookmarkDialog(BookmarkTreeContext ctx) {
		this(ctx, NEW_BOOKMARK);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		final int alertTitle = forCreateBookmark ? R.string.commonNewBookmark
				: bookmark.isBrowserBookmark() ? R.string.commonEditBookmark : R.string.commonEditFolder;
		setTitle(alertTitle);

		final String bookmarkTitle = bookmark.getDisplayTitle();
		newNodeTitleItem = (EditText)findViewById(R.id.editBookmarkNewTitle);
		newNodeTitleItem.setText(bookmarkTitle);

		if (bookmark.isBrowserBookmark()) {
			urlItem = (EditText)findViewById(R.id.editBookmarkUrl);
			urlItem.setText(bookmark.getUrl());
		}
		else {
			findViewById(R.id.editBookmarkUrlContainer).setVisibility(View.GONE);
		}
		
		parentFolderSpinner = (Spinner)findViewById(R.id.editBookmarkParentFolder);
		prepareParentFolderSpinner(bookmark);

		addToNewFolderItem = (EditText)findViewById(R.id.editBookmarkAddToNewFolder);

		new DialogButtonPanelWrapper(this) {
			@Override
			public void onPositiveButton() {
				saveBookmark(bookmark);
			}
		};
		
		/*
		 * delete
		 */
		boolean showDeletion = !forCreateBookmark && ctx.preferencesWrapper.isShowDeleteIcon();
		if (showDeletion) {
			TextView deleteTitle = (TextView)findViewById(R.id.editBookmarkDeleteText);
			deleteTitle.setText(bookmark.isFolder() ? "Delete Folder" : "Delete Bookmark");
			View deleteIcon = findViewById(R.id.editBookmarkDeleteIcon);
			deleteIcon.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					deleteConfirmation();
				}
			});
		}
		else {
			View view = findViewById(R.id.editBookmarkDeletePanel);
			view.setVisibility(View.INVISIBLE);
			view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,0));
		}
		
	}

	private void prepareParentFolderSpinner(final Bookmark bookmark) {

		/*
		 * .copy the "folders" cache
		 * .remove all folders below the given bookmark (we don't want to move the folder to one of its children)
		 * .add <no folder> to top
		 */
		ArrayList<FolderBean> folders = new ArrayList<FolderBean>(ctx.bookmarkManager.getAllFolders());
		if (bookmark.isFolder()) {
			// this is a folder, so we remove all children from the 'new parent folder' list
			folders.removeAll(bookmark.getTree(Bookmark.TYPE_FOLDER));
			folders.remove(bookmark); // remove 'self' from list
		}
		folders.add(0, FolderBean.ROOT);

		ArrayAdapter<FolderBean> adapter = new ArrayAdapter<FolderBean>(ctx.activity
				, android.R.layout.simple_spinner_item, folders);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		parentFolderSpinner.setAdapter(adapter);

		// sync position
		int pos = bookmark.getParentFolder()!=null ? folders.indexOf(bookmark.getParentFolder()) : -1 ;
		if (log.isDebugEnabled()) {
			log.debug("parent folder Spinner", bookmark.getParentFolder(), pos );
		}
		if (pos>=0) {
			parentFolderSpinner.setSelection(pos);
		}

	}

	private static String getEditTextValue(EditText item) {
		return item!=null ? item.getText().toString().trim() : null;
	}
	
	private void saveBookmark(Bookmark bookmark) {

		String newNodeTitle = getEditTextValue(newNodeTitleItem) ;
		String addToNewFolderTitle = getEditTextValue(addToNewFolderItem);
		String newUrl = getEditTextValue(urlItem);

		if (addToNewFolderTitle!=null && addToNewFolderTitle.length()>0) {
			newNodeTitle = addToNewFolderTitle + ctx.getNodeConcatenation() + newNodeTitle;
		}

		FolderBean newParentFolder = (FolderBean)parentFolderSpinner.getSelectedItem();
		if (newParentFolder==FolderBean.ROOT) {
			newParentFolder=null;
		}

		if (log.isDebugEnabled()) {
			log.debug("saveBookmark", newNodeTitle, newParentFolder, addToNewFolderTitle );
		}
		
		if (bookmark==NEW_BOOKMARK) {
			if (newParentFolder!=null) {
				// prepend the folder path
				newNodeTitle = newParentFolder.getFullTitle() + ctx.getNodeConcatenation() + newNodeTitle;
			}
			new BookmarkWriter(ctx).insert(newNodeTitle, newUrl);
		}
		else {
			BookmarkUpdateHandler upd = new BookmarkUpdateHandler(ctx);
			upd.update ( bookmark, newNodeTitle, newParentFolder, newUrl );
		}

		ctx.reloadAndRefresh();
		this.dismiss();
		
	}

	private String getText(int res) {
		return getContext().getString(res);
	}
	
	private void deleteConfirmation() {

		String alertTitle;
		if (bookmark.isBrowserBookmark()) {
			alertTitle = getText(R.string.actionDeleteBookmark);
		}
		else {
			int num = bookmark.getTree(Bookmark.TYPE_BROWSER_BOOKMARK).size();
			if (num<=1) {
				alertTitle = getText(R.string.actionDeleteFolderOne);
			}
			else {
				alertTitle = StringUtil.replaceFirst ( getText(R.string.actionDeleteFolderMany)
						, "{1}", Integer.toString(num) );
			}
		}

		new SimpleAlertDialog(ctx.activity, alertTitle, R.string.commonOK, R.string.commonCancel) {
			@Override
			public void onPositiveButton() {
				deleteBookmark();
			}
		};
	}
	
	private void deleteBookmark() {
		new BookmarkDeletionHandler(ctx,bookmark);
		ctx.reloadAndRefresh();
		this.dismiss();
	}
	

}
