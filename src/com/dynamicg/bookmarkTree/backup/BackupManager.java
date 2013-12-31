package com.dynamicg.bookmarkTree.backup;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;

import android.content.Context;
import android.text.format.Time;

import com.dynamicg.bookmarkTree.BookmarkTreeContext;
import com.dynamicg.bookmarkTree.R;
import com.dynamicg.bookmarkTree.backup.xml.Tags;
import com.dynamicg.bookmarkTree.backup.xml.XmlReader;
import com.dynamicg.bookmarkTree.backup.xml.XmlSettingsHelper;
import com.dynamicg.bookmarkTree.backup.xml.XmlSettingsHelper.PreferenceEntry;
import com.dynamicg.bookmarkTree.backup.xml.XmlWriter;
import com.dynamicg.bookmarkTree.chrome.ChromeWrapper;
import com.dynamicg.bookmarkTree.data.BrowserBookmarkLoader;
import com.dynamicg.bookmarkTree.model.RawDataBean;
import com.dynamicg.bookmarkTree.util.SimpleProgressDialog;
import com.dynamicg.common.ErrorNotification;
import com.dynamicg.common.Logger;
import com.dynamicg.common.SimpleAlertDialog;
import com.dynamicg.common.StringUtil;
import com.dynamicg.common.SystemUtil;

public class BackupManager {

	private static final Logger log = new Logger(BackupManager.class);

	private static final String FILE_PREFIX = "backup.";
	private static final String FILE_SUFFIX = ".xml";
	private static final String FILE_PATTERN = FILE_PREFIX + "{stamp}" + FILE_SUFFIX;
	private static final String FMT_STAMP = "%Y-%m-%d.%H-%M-%S";

	public static final String GOOGLE_DRIVE_FILE_NAME = "bookmarks.xml.gz";

	private static String getFilename(Time t) {
		return StringUtil.replaceAll(FILE_PATTERN, "{stamp}", t.format(FMT_STAMP));
	}
	private static String getFilename(boolean useGZ) {
		if (useGZ) {
			return GOOGLE_DRIVE_FILE_NAME;
		}
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

		if (log.isDebugEnabled) {
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
		public void backupDone(File backupFile);
		public void restoreDone();
	}

	private static final HashSet<String> locktable = new HashSet<String>();

	public synchronized static void createBackup(final BookmarkTreeContext ctx, final BackupEventListener backupDoneListener) {
		createBackup(ctx, backupDoneListener, false);
	}

	public synchronized static void createBackup(final BookmarkTreeContext ctx, final BackupEventListener backupDoneListener, final boolean forGoogleDrive) {

		final Context context = ctx.activity;
		final boolean useGZ = forGoogleDrive;

		final File backupdir = new SDCardCheck(context).readyForWrite();
		if (backupdir==null) {
			return; // not ready
		}

		final String filename = getFilename(useGZ);
		synchronized (locktable) {
			if (locktable.contains(filename)) {
				return; // already running. double-click(?)
			}
			locktable.add(filename);
		}

		new SimpleProgressDialog(context, R.string.brProgressCreateBackup) {

			int numberOfRows;
			File backupFile;

			@Override
			public void backgroundWork() {
				synchronized (locktable) {
					File xmlfileTemp = new File ( backupdir, filename+".tmp" );
					File xmlfileFinal = new File ( backupdir, filename );

					ArrayList<RawDataBean> bookmarks = BrowserBookmarkLoader.forBackup(ctx);
					numberOfRows = bookmarks.size();
					try {
						new XmlWriter(xmlfileTemp, bookmarks, useGZ);
						if (xmlfileFinal.exists()) {
							xmlfileFinal.delete();
						}

						xmlfileTemp.renameTo(xmlfileFinal);
						backupFile = xmlfileFinal;
					}
					catch (RuntimeException e) {
						throw (RuntimeException)e;
					}
					catch (Exception e) {
						throw new RuntimeException(e);
					}
					finally {
						locktable.remove(filename);
					}
				}
			}

			@Override
			public void done() {
				String text = context.getString(R.string.brHintBackupCreated)
						.replace("{1}", filename)
						.replace("{2}", Integer.toString(numberOfRows))
						;
				if (!forGoogleDrive) {
					SystemUtil.toastShort(ctx.activity, text);
				}
				BackupPrefs.registerBackup();
				if (backupDoneListener!=null) {
					// "refresh GUI" or "register" callback
					backupDoneListener.backupDone(this.backupFile);
				}
			}

			@Override
			public String getErrorTitle(Throwable exception) {
				return "Cannot create backup";
			}

		};

	}

	public static String getProgressMessageText(Context context, int step) {
		return StringUtil.textWithParam(context, R.string.brProgressRestoreBookmarks, step);
	}
	public static void updateProgressMessageText(BookmarkTreeContext ctx, SimpleProgressDialog progress, int step) {
		progress.updateProgressMessage ( BackupManager.getProgressMessageText(ctx.activity,step) );
	}

	public synchronized static void restore ( final BookmarkTreeContext ctx
			, final File xmlfile
			, final BackupEventListener backupDoneListener
			, final int restoreBookmarks
			, final int restoreSettings
			)
	{

		final Context context = ctx.activity;

		if (!new SDCardCheck(context).readyForRead()) {
			return;
		}


		new SimpleProgressDialog(context, getProgressMessageText(context,1) ) {

			int numberOfRows;
			ArrayList<PreferenceEntry> settingsFromXml;
			ArrayList<PreferenceEntry> labelsFromXml;

			@Override
			public void backgroundWork() {
				try {
					updateProgressMessageText(ctx, this, 2);
					XmlReader xmlReader = new XmlReader(xmlfile);
					ArrayList<RawDataBean> rows = xmlReader.read();
					numberOfRows = rows.size();
					settingsFromXml = xmlReader.settings;
					labelsFromXml = xmlReader.labels;

					if (restoreBookmarks!=-1) {
						RestoreWriter.replaceFull(ctx, rows, this);
					}

					if (ChromeWrapper.isKitKat()) {
						ChromeWrapper.getKitKatInstance().resetPrefs();
						int importFileVersion = xmlReader.getImportFileVersion();
						if (importFileVersion<Tags.V19) {
							ChromeWrapper.forceMarkPendingMigration();
						}
					}
				}
				catch (RuntimeException e) {
					throw (RuntimeException)e;
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

			void restoreDone() {
				String text = StringUtil.textWithParam(context, R.string.brHintBookmarksRestored, numberOfRows);
				SystemUtil.toastShort(context, text);
				backupDoneListener.restoreDone();
			}

			void askForSettingsImport() {
				new SimpleAlertDialog(ctx.activity, R.string.confirmImportSettings, R.string.buttonYes, R.string.buttonNo) {
					@Override
					public void onPositiveButton() {
						XmlSettingsHelper.restore(BookmarkTreeContext.settings, settingsFromXml);
						restoreDone();
					}
					@Override
					public void onNegativeButton() {
						restoreDone();
					}
				};
			}

			@Override
			public void done() {
				boolean hasSettings = settingsFromXml!=null && settingsFromXml.size()>0;
				if (ChromeWrapper.isKitKat()) {
					if (restoreSettings==1 && hasSettings) {
						XmlSettingsHelper.restore(BookmarkTreeContext.settings, settingsFromXml);
						if (restoreBookmarks==-1) {
							// only import labels if "boomarks" is OFF
							// (import of bookmarks will assign new bm ids so the old labels are useless)
							XmlSettingsHelper.restore(ChromeWrapper.getKitKatInstance().getSharedPrefs(), labelsFromXml);
						}
					}
					restoreDone();
				}
				else if (hasSettings) {
					askForSettingsImport();
				}
				else {
					restoreDone();
				}
			}

			@Override
			public void handleError(Throwable exception) {
				if (SystemUtil.isInvalidBrowserContentUrl(exception)) {
					ErrorNotification.cannotResolveBookmarks(context, exception);
				}
				else {
					super.handleError(exception);
				}
			}

			@Override
			public String getErrorTitle(Throwable exception) {
				return "Cannot restore";
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
				if (log.isDebugEnabled) {
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
