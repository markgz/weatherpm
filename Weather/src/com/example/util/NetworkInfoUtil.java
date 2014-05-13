package com.example.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkInfoUtil {

	public static String getNetWorkType(Context context) {
		
		String result = "";

		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		
		if(networkInfo == null){
			
			result = "NULL";
			
		}else{
			
			result = networkInfo.getTypeName();
		}
		
		return result;
	}
}
