package com.dynamicg.bookmarkTree.backup;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;

import android.content.Context;
import android.text.format.Time;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.data.BrowserBookmarkLoader;
import com.dynamicg.bookmarkTree.model.RawDataBean;
import com.dynamicg.bookmarkTree.util.SimpleProgressDialog;
import com.dynamicg.common.Logger;
import com.dynamicg.common.StringUtil;
import com.dynamicg.common.SystemUtil;

//TODO - validation after backup (?)
public class BackupManager {

	private static final Logger log = new Logger(BackupManager.class);
	
	private static final String FILE_PREFIX = "backup.";
	private static final String FILE_SUFFIX = ".xml";
	private static final String FILE_PATTERN = FILE_PREFIX + "{stamp}" + FILE_SUFFIX;
	private static final String FMT_STAMP = "%Y-%m-%d.%H-%M-%S";
	
	private static String getFilename(Time t) {
		return StringUtil.replaceFirst(FILE_PATTERN, "{stamp}", t.format(FMT_STAMP));
	}
	private static String getFilename() {
		Time t = new Time();
		t.setToNow();
		return getFilename(t);
	}
	
	public static ArrayList<File> getBackupFiles() {
		File dir = SDCardCheck.getBackupDir();
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.startsWith(FILE_PREFIX) && filename.endsWith(FILE_SUFFIX);
			}
		});
		
		if (log.debugEnabled) {
			log.debug("backup list", dir, files!=null?files.length:-1);
		}
		
		if (files==null) {
			return new ArrayList<File>();
		}
		
		// sort a-z
		TreeMap<String, File> sortmap = new TreeMap<String, File>();
		for (File f:files) {
			sortmap.put(f.getName(), f);
		}
		
		// revert
		ArrayList<File> sortdesc = new ArrayList<File>();
		for (File f:sortmap.values()) {
			sortdesc.add(0, f);
		}
		
		return sortdesc;
	}
	
	public static interface BackupEventListener {
		public void backupDone();
		public void restoreDone();
	}
	
	private static final HashSet<String> locktable = new HashSet<String>();
	
	public synchronized static void createBackup(final BookmarkTreeContext ctx, final BackupEventListener backupDoneListener) {
		
		final Context context = ctx.activity;
		
		final File backupdir = new SDCardCheck(context).readyForWrite();
		if (backupdir==null) {
			return; // not ready
		}
		
		final String filename = getFilename();
		synchronized (locktable) {
			if (locktable.contains(filename)) {
				return; // already running. double-click(?)
			}
			locktable.add(filename);
		}
		
		new SimpleProgressDialog(context, Messages.brProgressCreateBackup) {
			
			int numberOfRows;
			
			@Override
			public void backgroundWork() {
				synchronized (locktable) {
					File xmlfileTemp = new File ( backupdir, filename+".tmp" );
					File xmlfileFinal = new File ( backupdir, filename );
					
					ArrayList<RawDataBean> bookmarks = BrowserBookmarkLoader.forBackup(ctx);
					numberOfRows = bookmarks.size();
					try {
						new XmlWriter(xmlfileTemp, bookmarks);
						xmlfileTemp.renameTo(xmlfileFinal);
					}
					catch (RuntimeException e) {
						throw (RuntimeException)e;
					}
					catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
			
			@Override
			public void done() {
				String text = Messages.brHintBackupCreated
				.replace("{1}", filename)
				.replace("{2}", Integer.toString(numberOfRows))
				;
				SystemUtil.toastLong(ctx.activity, text);
				BackupPrefs.registerBackup();
				if (backupDoneListener!=null) {
					// "refresh GUI" or "register" callback
					backupDoneListener.backupDone();
				}
			}
			
			@Override
			public void handleError(Throwable e) {
				super.handleError(e);
			}
			
		};
		
	}
	
	public synchronized static void restore ( final BookmarkTreeContext ctx
			, final File xmlfile
			, final BackupEventListener backupDoneListener
			) 
	{
		
		if (!new SDCardCheck(ctx.activity).readyForRead()) {
			return;
		}
		
		new SimpleProgressDialog(ctx.activity, Messages.brProgressRestoreBookmarks) {
			
			int numberOfRows;
			
			@Override
			public void backgroundWork() {
				try {
					ArrayList<RawDataBean> rows = new XmlReader(xmlfile).read();
					numberOfRows = rows.size();
					RestoreWriter.replaceFull(ctx, rows);
				}
				catch (RuntimeException e) {
					throw (RuntimeException)e;
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			
			@Override
			public void done() {
				String text = StringUtil.textWithParam(Messages.brHintBookmarksRestored, numberOfRows);
				SystemUtil.toastLong(ctx.activity, text);
				backupDoneListener.restoreDone();
			}
			
			@Override
			public void handleError(Throwable e) {
				super.handleError(e);
			}
			
		};
		
	}

	private static void deleteImpl(ArrayList<File> backupFiles) {
		for (File f:backupFiles) {
			f.delete();
		}
	}
	public static void deleteOldFiles() {
		ArrayList<File> backupFiles = getBackupFiles();
		for (File f:backupFiles) {
			f.delete();
		}
	}

	public static void deleteFiles(int what) {
		if (what==BackupRestoreDialog.ACTION_DELETE_ALL) {
			deleteImpl(getBackupFiles());
		}
		else if (what==BackupRestoreDialog.ACTION_DELETE_OLD) {
			ArrayList<File> backupFiles = getBackupFiles();
			ArrayList<File> deletions = new ArrayList<File>();
			
			Time t = new Time();
			t.setToNow();
			t.monthDay = t.monthDay - BackupRestoreDialog.DELETION_DAYS_LIMIT;
			t.normalize(false);
			
			final String fnameStampLimit = getFilename(t);
			int comp;
			for (File f:backupFiles) {
				comp = f.getName().compareTo(fnameStampLimit);
				if (log.debugEnabled) {
					log.debug("check old files", fnameStampLimit, f.getName(), comp, comp<=0?"***":"-");
				}
				if (comp<=0) {
					deletions.add(f);
				}
			}
			deleteImpl(deletions);
		}
	}

}