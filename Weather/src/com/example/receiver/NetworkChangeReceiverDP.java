package com.example.receiver;

import com.example.util.NetworkInfoUtil;
import com.example.weather.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.util.Log;
import android.widget.Toast;

public class NetworkChangeReceiverDP extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		String typeName = NetworkInfoUtil.getNetWorkType(context);
		Log.i("TAG", "typeName: " + typeName);
		Log.i("TAG", "Intent.Action: " + intent.getAction() +" intent.extra: "+intent.getExtras().toString());
		Toast.makeText(context, "当前网络：" + (typeName.equals("NULL")?"已断开":typeName), Toast.LENGTH_SHORT).show();
		
		if("mobile".equals(typeName)||"WIFI".equals(typeName)){
			
			/*Intent intent2 = new Intent(context,MainActivity.class);
			intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent2);*/
		}
	}
	
}
