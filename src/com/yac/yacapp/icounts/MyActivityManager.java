package com.yac.yacapp.icounts;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;

public class MyActivityManager {

	private List<Activity> activityList = new LinkedList<Activity>();  
	private static MyActivityManager activityManager;
	
	private MyActivityManager(){
	}
	
	public static MyActivityManager getInstance(){
		if(activityManager == null){
			activityManager = new MyActivityManager();
		}
		return activityManager;
	}
	
	public void addActivity(Activity activity){  
        activityList.add(activity);  
    }  
	
	public void removeActivity(Activity activity){  
		if(activityList.contains(activity))
			activityList.remove(activity);  
    }  
	
    public void closeActivities(){
        for(Activity a : activityList){  
            a.finish();  
        }  
    }  
}
