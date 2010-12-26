package com.dynamicg.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class ContextUtil {

	private static final Logger log = new Logger(ContextUtil.class);
	
	private static final String SVN = "svnrevision";
	
	/*
	 * get version and svnrevision from manifest
	 * - the manifest file has to have the svn:keyword set to 'Rev'
	 * - the main activity has to have this entry:
	 *     <meta-data android:name="svnrevision" android:value="$Rev$" />
	 */
    public static String[] getVersion(Context context) {
        
    	String version="-";
    	String svnrevision="-";
    	try {
    		
	        PackageManager manager = context.getPackageManager();
	        PackageInfo info = manager.getPackageInfo ( context.getPackageName()
	        		, PackageManager.GET_ACTIVITIES + PackageManager.GET_META_DATA
	        		);
	        
	        version = info.versionName ;
	        
	        svnrevision = info.activities[0].metaData.getString(SVN);
	        // sample: "$Rev: 301 $"
	        svnrevision = svnrevision.split(" ")[1];
    	} 
    	catch (Exception e) {
    		log.warn("get version/revision", e);
    	}
    	
    	return new String[]{version, svnrevision};
    }

	private static float DENSITY_SCALE = -1;
	
	public static int getScaledSizeInt(Context context, float size) {
		if (DENSITY_SCALE==-1) {
			DENSITY_SCALE = context.getResources().getDisplayMetrics().density;
	    }
		return (int) (size * DENSITY_SCALE) ;
	}
	
}
