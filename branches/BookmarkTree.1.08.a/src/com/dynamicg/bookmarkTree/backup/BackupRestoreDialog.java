package com.dynamicg.bookmarkTree.backup;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.R;
import com.dynamicg.bookmarkTree.backup.BackupManager.BackupEventListener;
import com.dynamicg.bookmarkTree.util.DialogButtonPanelWrapper;
import com.dynamicg.bookmarkTree.util.DialogHelper;
import com.dynamicg.common.SimpleAlertDialog;
import com.dynamicg.common.StringUtil;

public class BackupRestoreDialog extends Dialog
implements BackupEventListener {

	public static final int DELETION_DAYS_LIMIT = 90;
	
    public static final int ACTION_DELETE_OLD = 1;
    public static final int ACTION_DELETE_ALL = 2;
    
	private final BookmarkTreeContext ctx;
	private final Activity context;

	public BackupRestoreDialog(BookmarkTreeContext ctx) {
		super(ctx.activity);
		this.ctx = ctx;
		this.context = ctx.activity;
		
		DialogHelper.expandContent(this, R.layout.backup_restore_body);
		
		this.show();
		
		// check sd card
		new SDCardCheck(context).checkMountedSdCard();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		setTitle(Messages.brDialogTitle);
		
		Button backup = (Button)findViewById(R.id.brBackup);
		backup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				createBackup();
			}
		});
		
		refreshBackupFilesList();
		
		new DialogButtonPanelWrapper(this, DialogButtonPanelWrapper.TYPE_CLOSE) {
			@Override
			public void onPositiveButton() {
				dismiss();
			}
		};
		
		((TextView)findViewById(R.id.brStorageHint1)).setText(Messages.brStorageHint);
		
		String backupdir = SDCardCheck.getBackupDir().toString();
		((TextView)findViewById(R.id.brStorageHint2)).setText(backupdir);
		
		setupAutoBackup();
		
	}
	
	private void setupAutoBackup() {
		CheckBox autoBackup = (CheckBox)findViewById(R.id.brAutoBackup);
		String autoBackupLabel = Messages.brAutoBackupLabel.replace("{1}", Integer.toString(BackupPrefs.DAYS_BETWEEN)); 
		autoBackup.setText(autoBackupLabel);
		autoBackup.setChecked(BackupPrefs.isAutoBackupEnabled());
		
		autoBackup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				BackupPrefs.writeAutoBackupEnabled(isChecked);
				Toast.makeText(context, "Auto backup "+(isChecked?"enabled":"disabled"), Toast.LENGTH_SHORT).show();
			}
		});
		
	}
	
	private void refreshBackupFilesList() {
		
		final RadioGroup backupListGroup = (RadioGroup)findViewById(R.id.brRestoreList);
		backupListGroup.removeAllViews(); // for repeated calls
		
		final ArrayList<File> backupFiles = BackupManager.getBackupFiles();
		if (backupFiles.size()==0) {
			TextView hint = SimpleAlertDialog.createTextView(context, Messages.brNoFilesForRestore);
			backupListGroup.addView(hint);
		}
		
		RadioButton rb;
		String filename;
		for ( int pos=0 ; pos<backupFiles.size() ; pos++ ) {
			rb = new RadioButton(context);
			filename = backupFiles.get(pos).getName();
			rb.setText(filename);
			rb.setId(pos);
			backupListGroup.addView(rb);
		}
		
		backupListGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId>=0) {
					RadioButton rb = (RadioButton)group.getChildAt(checkedId);
					// "clear()" fires the "changed" event for the current selection
					if (rb.isChecked()) {
						File f = backupFiles.get(checkedId);
						restore(group, f);
					}
				}
			}
		});
	}
	
	private void restore(final RadioGroup group, final File backupFile) {
		new SimpleAlertDialog.OkCancelDialog(context, Messages.brRestoreConfirmation) {
			
			@Override
			public void onPositiveButton() {
				BackupManager.restore(ctx, backupFile, BackupRestoreDialog.this);
			}

			@Override
			public void onNegativeButton() {
				group.clearCheck();
			}

			@Override
			public String getPlainBodyText() {
				return StringUtil.replaceFirst(Messages.brSelectedFileLabel, "{1}", backupFile.getName() );
			}

		};
	}

	private void createBackup() {
		BackupManager.createBackup(ctx, this);
	}
	
	@Override
	public void backupDone() {
		refreshBackupFilesList();
	}

	@Override
	public void restoreDone() {
		// restore ok - refresh, close backup dialog
		ctx.reloadAndRefresh();
		dismiss();
	}
	
	private String getDeletionOldLabel() {
		return Messages.brDeleteOld.replace("{1}", Integer.toString(DELETION_DAYS_LIMIT));
	}
	
	private void deleteConfirmation(final int what) {
		String msg = what==ACTION_DELETE_ALL ? Messages.brDeleteAll
				: what==ACTION_DELETE_OLD ? getDeletionOldLabel()
						: "<undefined>";
		new SimpleAlertDialog.OkCancelDialog(context, msg+"?") {
			@Override
			public void onPositiveButton() {
				BackupManager.deleteFiles(what);
				refreshBackupFilesList();
			}
		};
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, ACTION_DELETE_OLD, 0, getDeletionOldLabel());
		menu.add(0, ACTION_DELETE_ALL, 0, Messages.brDeleteAll);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int id = item.getItemId();
		if (id==ACTION_DELETE_OLD || id==ACTION_DELETE_ALL) {
			deleteConfirmation(id);
		}
		return true;
	}
	
	
	
}
