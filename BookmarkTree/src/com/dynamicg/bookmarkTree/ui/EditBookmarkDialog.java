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
import com.dynamicg.bookmarkTree.data.writer.UpdateBookmarkWriter;
import com.dynamicg.bookmarkTree.model.Bookmark;
import com.dynamicg.bookmarkTree.model.FolderBean;
import com.dynamicg.bookmarkTree.util.CommonDialogHelper;
import com.dynamicg.bookmarkTree.util.DialogButtonPanelWrapper;
import com.dynamicg.common.main.Logger;
import com.dynamicg.common.ui.SimpleAlertDialog;

public class EditBookmarkDialog extends Dialog {

	private static final Logger log = new Logger(EditBookmarkDialog.class);

	private final BookmarkTreeContext ctx;
	private final Bookmark bookmark;
	
	private EditText newNodeTitleItem;
	private Spinner parentFolderSpinner;
	private EditText addToNewFolderItem;

	public EditBookmarkDialog(BookmarkTreeContext ctx, Bookmark bookmark) {
		super(ctx.activity);
		this.ctx = ctx;
		this.bookmark = bookmark;
		CommonDialogHelper.expandContent(this, R.layout.edit_body);
		this.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String alertTitle = bookmark.isFolder() ? "Edit Folder" : "Edit Bookmark";
		setTitle(alertTitle);

		String bookmarkTitle = bookmark.getDisplayTitle();

		newNodeTitleItem = (EditText)findViewById(R.id.editBookmarkNewTitle);
		newNodeTitleItem.setText(bookmarkTitle);

		parentFolderSpinner = (Spinner)findViewById(R.id.editBookmarkParentFolder);
		prepareParentFolderSpinner(bookmark);

		addToNewFolderItem = (EditText)findViewById(R.id.editBookmarkAddToNewFolder);

		new DialogButtonPanelWrapper(this) {
			@Override
			public void onPositiveButton() {
				updateBookmark(bookmark);
			}
		};
		
		/*
		 * delete
		 */
		boolean showDeletion = ctx.preferencesWrapper.isShowDeleteIcon();
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

	private void prepareParentFolderSpinner(Bookmark bookmark) {

		/*
		 * .copy the "folders" cache
		 * .remove all folders below the given bookmark (we don't want to move the folder to one of its children)
		 * .add <no folder> to top
		 */
		ArrayList<FolderBean> folders = new ArrayList<FolderBean>(ctx.bookmarkManager.getAllFolders());
		folders.removeAll(bookmark.getTree(Bookmark.TYPE_FOLDER));
		folders.remove(bookmark); // remove 'self' from list
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

	private void updateBookmark(Bookmark bookmark) {

		String newNodeTitle = newNodeTitleItem.getText().toString();
		String addToNewFolderTitle = addToNewFolderItem.getText().toString();

		if (addToNewFolderTitle!=null && addToNewFolderTitle.trim().length()>0) {
			newNodeTitle = addToNewFolderTitle + ctx.getNodeConcatenation() + newNodeTitle;
		}

		FolderBean newParentFolder = (FolderBean)parentFolderSpinner.getSelectedItem();
		if (newParentFolder==FolderBean.ROOT) {
			newParentFolder=null;
		}

		if (log.isDebugEnabled()) {
			log.debug("updateBookmark", newNodeTitle, newParentFolder, addToNewFolderTitle );
		}
		UpdateBookmarkWriter upd = new UpdateBookmarkWriter(ctx);
		upd.update(bookmark, newNodeTitle, newParentFolder);

		ctx.reloadAndRefresh();
		this.dismiss();
		
	}

	private void deleteConfirmation() {

		String alertTitle;
		if (bookmark.isBrowserBookmark()) {
			alertTitle = "Delete this bookmark?";
		}
		else {
			int num = bookmark.getTree(Bookmark.TYPE_BROWSER_BOOKMARK).size();
			alertTitle = num<=1 ? "Delete this folder?" : "Delete this folder with all "+num+" bookmarks?" ;
		}

		new SimpleAlertDialog(ctx.activity, alertTitle, "OK", "Cancel") {
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
