package com.example.receiver;

import com.example.util.NetworkInfoUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		String typeName = NetworkInfoUtil.getNetWorkType(context);
		Log.i("TAG", "typeName: " + typeName);
		Toast.makeText(context, "当前网络：" + typeName, Toast.LENGTH_SHORT).show();
	}

}
