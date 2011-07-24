package ch.mieng.meteo.mythenquai.mod;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.webkit.WebView;

public class ChartInterface {

	public String title;
	public String url;
	public Date date;
	private SimpleDateFormat simpleDayFormat = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat simpleMonthFormat = new SimpleDateFormat("yyyyMM");
	private SimpleDateFormat simpleYearFormat = new SimpleDateFormat("yyyy");

	private SimpleDateFormat simpleDayTextFormat = new SimpleDateFormat("dd. MMM yyyy");
	private SimpleDateFormat simpleMonthTextFormat = new SimpleDateFormat("MMM yyyy");
	
	private enum VIEWMODE {DAY, MONTH, YEAR};
	private VIEWMODE viewMode = VIEWMODE.DAY;

	public ChartInterface  (WebView webView, String title, String value, boolean isTiefenbrunnen, Date date) {
		this.title = title;
		this.date = date;
		this.url = "http://mi-eng.appspot.com/meteoreading/"+(isTiefenbrunnen ? "tiefenbrunnen" : "mythenquai")+"/"+ value + "/";
	}
        
    public String getTitle() {
		return title;
    }  
    
    public String getChartDate() {
    	switch(viewMode) {
			case DAY:
				return simpleDayTextFormat.format(date);
			case MONTH:
				return simpleMonthTextFormat.format(date);
			case YEAR:
				return simpleYearFormat.format(date);
		}
    	return "";
    }

    public String getUrl() {
    	switch(viewMode) {
    		case DAY:
    			return url+ simpleDayFormat.format(date);
    		case MONTH:
    			return url+ simpleMonthFormat.format(date);
    		case YEAR:
    			return url+ simpleYearFormat.format(date);
    	}
    	return "";
    }  

    public String getDayUrl() {
    	viewMode = VIEWMODE.DAY;
    	return getUrl();
    }  

    public String getYearUrl() {
    	viewMode = VIEWMODE.YEAR;
    	return getUrl();
    }  

    public String getMonthUrl() {
    	viewMode = VIEWMODE.MONTH;
    	return getUrl();
    }  
    
    private void adjustDate(int offset) {
		Calendar cal = Calendar.getInstance();
    	switch(viewMode) {
		case DAY:
			cal.setTime(date);
			cal.add(Calendar.DAY_OF_YEAR, offset);
			date = cal.getTime();
			break;
		case MONTH:
			cal.setTime(date);
			cal.add(Calendar.MONTH, offset);
			date = cal.getTime();
			break;
		case YEAR:
			cal.setTime(date);
			cal.add(Calendar.YEAR, offset);
			date = cal.getTime();
			break;
    	}    	
    }
    
    public String getPreviousUrl() {
    	adjustDate(-1);
    	return getUrl();
    }  
    
    public String getNextUrl() {
    	adjustDate(+1);
    	return getUrl();
    }  

}
