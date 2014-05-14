package com.example.weather;

import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.example.absdemo.R;
import com.example.util.DB;

/**
 * @author Mark
 * 
 */
public class AddressActivity extends SherlockActivity {
	private TextView provinceTV;
	private TextView cityTV;

	private ListView listView;
	private SimpleAdapter adapter;
	private List<Map<String, String>> list;

	private int state = 0;
	private String city = "";
	private String id = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.address);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setLogo(R.drawable.icon);
		getSupportActionBar().setTitle("City");
		getSupportActionBar().setBackgroundDrawable(
				this.getResources().getDrawable(R.drawable.action_bar_bg));

		state = 0;
		provinceTV = (TextView) findViewById(R.id.provinceText);
		cityTV = (TextView) findViewById(R.id.cityText);
		listView = (ListView) findViewById(R.id.addresslist);

		list = DB.getProvince();
		String[] from = new String[] { "address" };
		int[] to = new int[] { android.R.id.text1 };

		adapter = new SimpleAdapter(this, list,
				android.R.layout.simple_list_item_1, from, to);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String temp = list.get(arg2).get("address");
				switch (state) {
				case 0:
					getCity(temp);
					provinceTV.setText(temp + ">>");
					city = temp;
					state = 1;
					break;
				case 1:
					getCountry(temp);
					cityTV.setText(temp + ">>");
					state = 2;
					break;
				case 2:
					getId(temp);
					Intent intent = new Intent();
					intent.putExtra("id", id);
					intent.putExtra("address", temp);
					setResult(1, intent);
					finish();
					break;
				default:
					break;
				}
			}
		});
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
	}

	private void getProvince() {
		list.clear();
		list.addAll(DB.getProvince());
		adapter.notifyDataSetChanged();
	}

	private void getCity(String province) {
		list.clear();
		list.addAll(DB.getCity(province));
		adapter.notifyDataSetChanged();
	}

	private void getCountry(String city) {
		list.clear();
		list.addAll(DB.getCountry(city));
		adapter.notifyDataSetChanged();
	}

	private void getId(String country) {
		id = DB.getAddressId(country);
	}

	@Override
	public void onBackPressed() {
		switch (state) {
		case 0:
			finish();
			break;
		case 1:
			getProvince();
			provinceTV.setText("");
			cityTV.setText("");
			state = 0;
			break;
		case 2:
			getCity(city);
			provinceTV.setText(city + ">>");
			cityTV.setText("");
			state = 1;
			break;
		default:
			break;
		}
		// super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "delete")
				.setIcon(R.drawable.checkbox_checked)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;
		case 1:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
