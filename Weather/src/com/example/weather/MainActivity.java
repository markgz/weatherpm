package com.example.weather;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.example.absdemo.R;
import com.example.entity.Weather;
import com.example.util.Constants;
import com.example.util.DB;
import com.example.util.NetworkInfoUtil;
import com.example.util.WeatherData;
import com.example.util.WeatherPic;
import com.example.util.WriteToSD;
import com.example.view.MyPagerAdapter;
import com.example.view.TrendView;

public class MainActivity extends SherlockActivity {
	private static MenuDrawer mMenuDrawer;
	private Weather weatherData;
	// ViewPager
	public ViewPager myViewPager;
	private MyPagerAdapter myAdapter;
	private LayoutInflater mInflater;
	private List<View> mListViews;
	private View layout1 = null;
	private View layout2 = null;

	private TextView temperature;
	private TextView refreshTime;
	private TextView refreshDate;
	private TextView weather;
	private TextView wind;
	private TextView city;
	private TextView comfortable;
	private TextView tomorrowTemperature;
	private TextView tomorrowWeather;
	private ImageView weatherPic;

	private TrendView view;
	private TextView day1TextView;
	private TextView day2TextView;
	private TextView day3TextView;
	private TextView day4TextView;
	private TextView day5TextView;
	private TextView wea1TextView;
	private TextView wea2TextView;
	private TextView wea3TextView;
	private TextView wea4TextView;
	private TextView wea5TextView;

	private TextView date1TextView;
	private TextView date2TextView;
	private TextView date3TextView;
	private TextView date4TextView;
	private TextView date5TextView;

	private List<Map<String, String>> addressList;
	private SimpleAdapter adapter;
	private ListView menuListView;

	private String id = "101280101";

	private Animation animation;
	private LinearLayout layout;

	private TextView usernameTextView;

	private static SharedPreferences sharedPreferences;

	private TextView developerButton;

	private Button bgSettingButton;

	private static final int IMG_REQUEST_CODE = 100;

	private static final int IMG_MESSAGE_WHAT = 101;

	NetworkChangeReceiver networkChangeReceiver;

	private String imgStoreFolderName = "weather/bgImage";
	
	private TextView pm25Textview;

	public static Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case IMG_MESSAGE_WHAT:

				String imgPath = msg.obj.toString();

				Drawable background = Drawable.createFromPath(imgPath);

				LinearLayout viewpagerLayout = (LinearLayout) mMenuDrawer
						.findViewById(R.id.background_layout);

				// mMenuDrawer.setBackgroundDrawable(background);

				viewpagerLayout.setBackgroundDrawable(background);

				Editor editor = sharedPreferences.edit();
				editor.putBoolean("customBG", true);
				editor.putString("bgImgPath", imgPath);
				editor.commit();

				Log.i("TAG", "set background imgpath: " + imgPath);

				break;

			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		new WriteToSD(this);
		new Constants(this);

		sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE);

		initMenu();
		initPage();
		initAnim();
		initWidget();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setLogo(R.drawable.icon);
		getSupportActionBar().setTitle(
				getResources().getString(R.string.app_name));
		getSupportActionBar().setBackgroundDrawable(
				this.getResources().getDrawable(R.drawable.action_bar_bg));
		setSupportProgressBarIndeterminateVisibility(false);

		registerNetworkChangeReceiver();

	}

	public void registerNetworkChangeReceiver() {

		// create a intentFilter for CONNECTIVITY_CHANGE
		IntentFilter intentFilter = new IntentFilter(
				"android.net.conn.CONNECTIVITY_CHANGE");

		// set a high priority for this intentFilter
		intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		// create new instance of the BroadcasteReceiver's
		// subclass(NetworkChangeReceiver)
		networkChangeReceiver = new NetworkChangeReceiver();
		// register the broadcastReceiver
		registerReceiver(networkChangeReceiver, intentFilter);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onPause();

		// unregister the broadcastReceiver when activity is paused
		if (networkChangeReceiver != null) {

			try {
				unregisterReceiver(networkChangeReceiver);

			} catch (IllegalArgumentException e) {
				Log.e("TAG", "your Recevier is not registed...");
				e.printStackTrace();
			}
		}

	}

	public class NetworkChangeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			String typeName = NetworkInfoUtil.getNetWorkType(context);
			Log.i("TAG", "typeName: " + typeName);
			Log.i("TAG", "Intent.Action: " + intent.getAction()
					+ " intent.extra: " + intent.getExtras().toString());
			Toast.makeText(context,
					"当前网络：" + (typeName.equals("NULL") ? "已断开" : typeName),
					Toast.LENGTH_SHORT).show();

			if ("mobile".equals(typeName) || "WIFI".equals(typeName)) {

				refresh();
			}
		}

	}

	private void initMenu() {
		mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.MENU_DRAG_WINDOW,
				Position.LEFT); // WINDOW
		mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
		mMenuDrawer.setContentView(R.layout.viewpager);
		mMenuDrawer.setMenuView(R.layout.menu);
		mMenuDrawer.setDropShadow(R.drawable.shadow);
		mMenuDrawer.setDropShadowSize((int) getResources().getDimension(
				R.dimen.shadow_width));
		mMenuDrawer.setMaxAnimationDuration(3000);
		mMenuDrawer.setHardwareLayerEnabled(false);
		mMenuDrawer.setMenuSize((int) getResources().getDimension(
				R.dimen.slidingmenu_offset));

		boolean customBG = sharedPreferences.getBoolean("customBG", false);
		if (customBG) {

			String bgImgPath = sharedPreferences.getString("bgImgPath", "");

			Drawable background = Drawable.createFromPath(bgImgPath);

			LinearLayout viewpagerLayout = (LinearLayout) mMenuDrawer
					.findViewById(R.id.background_layout);

			viewpagerLayout.setBackgroundDrawable(background);

		}
	}

	private void initPage() {
		mListViews = new ArrayList<View>();
		mInflater = getLayoutInflater();
		layout1 = mInflater.inflate(R.layout.activity_main, null);
		layout2 = mInflater.inflate(R.layout.trend, null);
		mListViews.add(layout1);
		mListViews.add(layout2);

		myViewPager = (ViewPager) findViewById(R.id.viewpagerLayout);
		myAdapter = new MyPagerAdapter(mListViews);
		myViewPager.setAdapter(myAdapter);

		myViewPager.setCurrentItem(0);
		myViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int arg0) {
				switch (arg0) {
				case 0:
					mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
					break;
				case 1:
					mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_NONE);
					break;
				}
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	private void initAnim() {
		TextView textView = (TextView) layout1.findViewById(R.id.city);
		AnimationDrawable ad = (AnimationDrawable) textView
				.getCompoundDrawables()[0];
		ad.start();

		// trend arrow
		ImageView imageView = (ImageView) layout1.findViewById(R.id.trendarrow);
		AnimationDrawable treadAD = (AnimationDrawable) imageView
				.getBackground();
		treadAD.start();

		animation = new AlphaAnimation(1, 0);
		animation.setDuration(700);
		animation.setRepeatCount(1);
		animation.setRepeatMode(Animation.REVERSE);
	}

	private void initWidget() {
		layout = (LinearLayout) layout1.findViewById(R.id.addresslay);
		temperature = (TextView) layout1.findViewById(R.id.temperature);
		wind = (TextView) layout1.findViewById(R.id.wind);
		refreshDate = (TextView) layout1.findViewById(R.id.refreshDate);
		refreshTime = (TextView) layout1.findViewById(R.id.refreshTime);
		weather = (TextView) layout1.findViewById(R.id.weather);
		city = (TextView) layout1.findViewById(R.id.city);
		comfortable = (TextView) layout1.findViewById(R.id.comfortable);
		tomorrowTemperature = (TextView) layout1
				.findViewById(R.id.tomorrowtemperature);
		tomorrowWeather = (TextView) layout1.findViewById(R.id.tomorroweather);
		weatherPic = (ImageView) layout1.findViewById(R.id.weatherPic);
		
		pm25Textview = (TextView) layout1.findViewById(R.id.pm25_textview);
		
		
		pm25Textview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				String cityPY = DB.getCityPY(id).toLowerCase();
				
				Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://aqicn.org/city/"+cityPY));
				startActivity(intent);
				
			}
		});

		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int screenWidth = displayMetrics.widthPixels;
		int screenHeight = displayMetrics.heightPixels;
		view = (TrendView) layout2.findViewById(R.id.trendView);
		view.setWidthHeight(screenWidth, screenHeight);

		day1TextView = (TextView) layout2.findViewById(R.id.day1);
		day2TextView = (TextView) layout2.findViewById(R.id.day2);
		day3TextView = (TextView) layout2.findViewById(R.id.day3);
		day4TextView = (TextView) layout2.findViewById(R.id.day4);
		day5TextView = (TextView) layout2.findViewById(R.id.day5);
		wea1TextView = (TextView) layout2.findViewById(R.id.weather1);
		wea2TextView = (TextView) layout2.findViewById(R.id.weather2);
		wea3TextView = (TextView) layout2.findViewById(R.id.weather3);
		wea4TextView = (TextView) layout2.findViewById(R.id.weather4);
		wea5TextView = (TextView) layout2.findViewById(R.id.weather5);

		date1TextView = (TextView) layout2.findViewById(R.id.relatedDate1);
		date2TextView = (TextView) layout2.findViewById(R.id.relatedDate2);
		date3TextView = (TextView) layout2.findViewById(R.id.relatedDate3);
		date4TextView = (TextView) layout2.findViewById(R.id.relatedDate4);
		date5TextView = (TextView) layout2.findViewById(R.id.relatedDate5);

		Calendar calendar = Calendar.getInstance();

		day1TextView.setText(getDayOfWeek(calendar));
		date1TextView.setText(getDateFormat(calendar));

		calendar.add(Calendar.DAY_OF_WEEK, 1);
		day2TextView.setText(getDayOfWeek(calendar));
		date2TextView.setText(getDateFormat(calendar));

		calendar.add(Calendar.DAY_OF_WEEK, 1);
		day3TextView.setText(getDayOfWeek(calendar));
		date3TextView.setText(getDateFormat(calendar));

		calendar.add(Calendar.DAY_OF_WEEK, 1);
		day4TextView.setText(getDayOfWeek(calendar));
		date4TextView.setText(getDateFormat(calendar));

		calendar.add(Calendar.DAY_OF_WEEK, 1);
		day5TextView.setText(getDayOfWeek(calendar));
		date5TextView.setText(getDateFormat(calendar));

		usernameTextView = (TextView) findViewById(R.id.smsItemName);

		usernameTextView.setText(sharedPreferences.getString("username",
				"Click me"));

		usernameTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
				builder.setTitle(getResources().getString(
						R.string.usernameDialogTitle));
				final EditText editText = new EditText(MainActivity.this);
				editText.setHint(getResources().getString(
						R.string.usernameDialogTitle));
				builder.setView(editText);
				builder.setPositiveButton(
						getResources().getString(R.string.confirm),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								usernameTextView.setText(editText.getText()
										.toString());
								Editor editor = sharedPreferences.edit();
								editor.putString("username", editText.getText()
										.toString());
								editor.commit();
							}
						});
				builder.setNegativeButton(
						getResources().getString(R.string.cancel),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						});
				builder.setCancelable(false);
				builder.show();
				;

			}
		});

		developerButton = (TextView) findViewById(R.id.developer_button);

		developerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
				builder.setTitle(getResources().getString(
						R.string.developer_info));

				builder.setMessage(getResources().getString(
						R.string.developer_email));
				builder.setPositiveButton(
						getResources().getString(R.string.connect_me),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								Intent emailIntent = new Intent(
										Intent.ACTION_SENDTO,
										Uri.parse("mailto:"
												+ getResources().getString(
														R.string.only_email)));

								emailIntent.putExtra(
										android.content.Intent.EXTRA_SUBJECT,
										"用户反馈");
								emailIntent.putExtra(
										android.content.Intent.EXTRA_TEXT,
										"你好，");

								startActivity(Intent
										.createChooser(
												emailIntent,
												getResources()
														.getString(
																R.string.use_which_one_to_send)));

							}
						});
				builder.setNegativeButton(
						getResources().getString(R.string.cancel),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						});
				builder.setCancelable(false);
				builder.show();
				;

			}
		});

		bgSettingButton = (Button) findViewById(R.id.bg_setting_button);

		bgSettingButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);

				builder.setTitle(getResources().getString(R.string.settingbg));

				builder.setPositiveButton(
						getResources().getString(R.string.customBG),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								Intent pickIntent = new Intent(
										Intent.ACTION_PICK);
								pickIntent.setType("image/*");
								startActivityForResult(pickIntent,
										IMG_REQUEST_CODE);
							}
						});

				builder.setNegativeButton(
						getResources().getString(R.string.defaultBG),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								// String imgPath = msg.obj.toString();

								// Drawable background = getAssets()

								LinearLayout viewpagerLayout = (LinearLayout) mMenuDrawer
										.findViewById(R.id.background_layout);

								// mMenuDrawer.setBackgroundDrawable(background);
								viewpagerLayout
										.setBackgroundResource(R.drawable.bg_girl_first);

								// viewpagerLayout.setBackgroundDrawable(background);

								Editor editor = sharedPreferences.edit();
								editor.putBoolean("customBG", false);
								editor.putString("bgImgPath", "");
								editor.commit();

							}
						});
				builder.setCancelable(false);
				builder.show();

			}
		});

		city.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (menuListView.getCount() == 0) {
					return;
				}
				int index = (menuListView.getCheckedItemPosition() + 1)
						% menuListView.getCount();
				menuListView.setItemChecked(index, true);
				Map<String, String> map = addressList.get(index);
				id = map.get("id");
				refresh();
			}
		});
		menuListView = (ListView) findViewById(R.id.menuaddresslist);
		addressList = new ArrayList<Map<String, String>>();
		String[] from = new String[] { "address" };
		int[] to = new int[] { android.R.id.text1 };
		adapter = new SimpleAdapter(this, addressList,
				R.layout.menu_list_item_layout, from, to);
		menuListView.setAdapter(adapter);

		Button add = (Button) mMenuDrawer.findViewById(R.id.addaddress);
		add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				add();
			}
		});
		menuListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Map<String, String> map = addressList.get(arg2);
				id = map.get("id");
				mMenuDrawer.toggleMenu();
				refresh();
			}
		});
		menuListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				final int index = arg2;
				final MyDialog d = new MyDialog(MainActivity.this, "提示",
						"确认删除？");
				System.out.println(index);
				d.show();
				d.getButton1().setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (menuListView.getCount() <= 1) {
							Toast.makeText(getApplicationContext(),
									"至少要保留一个地区", Toast.LENGTH_SHORT).show();
							return;
						}
						DB.deleteCityAndId(addressList.get(index).get("id"));
						addressList.remove(index);
						// int index2 =
						// (menuListView.getCheckedItemPosition()+1)%menuListView.getCount();
						menuListView.setItemChecked(0, true);
						adapter.notifyDataSetChanged();
						Toast.makeText(getApplicationContext(), "删除成功",
								Toast.LENGTH_SHORT).show();
						d.dismiss();
					}
				});
				return false;
			}
		});

		addressList.clear();
		addressList.addAll(DB.getCityAndId());
		id = addressList.get(0).get("id");
		adapter.notifyDataSetChanged();
		menuListView.setItemChecked(0, true);
		refresh();
	}

	public String getDayOfWeek(Calendar calendar) {
		String day = "";
		int dayConstant = calendar.get(Calendar.DAY_OF_WEEK);
		switch (dayConstant) {
		case 1:

			day = getResources().getString(R.string.sunday);

			break;
		case 2:

			day = getResources().getString(R.string.monday);

			break;
		case 3:

			day = getResources().getString(R.string.tuesday);

			break;
		case 4:

			day = getResources().getString(R.string.wednesday);

			break;
		case 5:

			day = getResources().getString(R.string.thursday);

			break;
		case 6:

			day = getResources().getString(R.string.friday);

			break;
		case 7:

			day = getResources().getString(R.string.saturday);

			break;

		default:
			break;
		}
		return day;
	}

	@SuppressLint("SimpleDateFormat")
	public String getDateFormat(Calendar calendar) {
		String formatDate = "";
		Date date = calendar.getTime();

		SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(
				R.string.dateformat));
		// new SimpleDateFormat("", new Local);
		formatDate = sdf.format(date);

		return formatDate;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			if (data != null) {
				id = data.getExtras().getString("id");
				Map<String, String> map = new HashMap<String, String>();
				map.put("address", data.getExtras().getString("address"));
				map.put("id", id);
				if (addressList.contains(map)) {
					Toast.makeText(getApplicationContext(), "城市已添加",
							Toast.LENGTH_SHORT).show();
					break;
				}
				addressList.add(map);
				refresh();
				adapter.notifyDataSetChanged();
				menuListView.setItemChecked(menuListView.getCount() - 1, true);

				DB.saveCityAndId(map.get("address"), id);
			}
			break;
		case IMG_REQUEST_CODE:

			if (data != null) {

				Uri selectedImg = data.getData();

				String[] filePathColumn = new String[] { MediaStore.Images.Media.DATA };

				Cursor cursor = getContentResolver().query(selectedImg,
						filePathColumn, null, null, null);
				String filePath = "";
				if (cursor.moveToFirst()) {
					filePath = cursor.getString(cursor
							.getColumnIndex(filePathColumn[0]));
					cursor.close();
				}

				// Bitmap yourSelectedImg = BitmapFactory.decodeFile(filePath);
				Log.i("TAG", "filePath: " + filePath);
				// mMenuDrawer.setBackgroundDrawable(Drawable.createFromPath(filePath));

				WriteToSD.writeFileToSD(filePath, imgStoreFolderName, handler);

			} else {

				Log.i("TAG", "onActivityResult data is null ");
			}

			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void finish() {
		super.finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			mMenuDrawer.toggleMenu();
			break;
		case 1:
			add();
			break;
		case 0:
			refresh();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void add() {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, AddressActivity.class);
		startActivityForResult(intent, 1);
		overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
	}

	private void refresh() {
		setSupportProgressBarIndeterminateVisibility(true);

		if (!"NULL".equals(NetworkInfoUtil.getNetWorkType(this))) {

			MenuTask task = new MenuTask();
			task.execute(0);
			layout.startAnimation(animation);
		} else {
			Toast.makeText(this, "请开启网络", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "add")
				.setIcon(R.drawable.ic_search)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		menu.add(0, 0, 0, "Refresh")
				.setIcon(R.drawable.ic_refresh)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}

	public boolean checkNetWork() {

		boolean result = false;

		return result;

	}

	class MenuTask extends AsyncTask<Integer, Integer, Integer> {
		@Override
		protected Integer doInBackground(Integer... i) {
			WeatherData data = new WeatherData(MainActivity.this);
			Log.i("TAG", "id: " + id);
			weatherData = data.getData("http://m.weather.com.cn/atad/" + id
					+ ".html");
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {

			if (weatherData != null) {
				Log.i("TAG", "id: " + id + " city: " + weatherData.getCity());

				city.setText(weatherData.getCity());
				temperature.setText(weatherData.getTodayTemperature());
				if (weatherData.getWeather().get(0).equals("")) {
					weather.setText(weatherData.getTodayWeather());
				} else {
					weather.setText(weatherData.getWeather().get(0));
				}
				wind.setText(weatherData.getWind().get(0));
				comfortable.setText(weatherData.getComfortable());
				tomorrowTemperature.setText(weatherData
						.getTomorrowTemperature());
				tomorrowWeather.setText(weatherData.getTomorrowWeather());
				refreshDate.setText(weatherData.getRefreshDate());
				refreshTime.setText(weatherData.getRefreshTime());
				weatherPic.setImageBitmap(WeatherPic.getPic(
						getApplicationContext(), weatherData.getPicIndex(),
						weatherData.isNight() ? 1 : 0));

				view.setTemperature(weatherData.getMaxlist(),
						weatherData.getMinlist());
				view.setBitmap(weatherData.getTopPic(), weatherData.getLowPic());

				if (!weatherData.getWeather().get(0).equals("")) {
					wea1TextView.setText(weatherData.getWeather().get(0));
				} else {
					wea1TextView.setText(weatherData.getTodayWeather());
				}
				wea2TextView.setText(weatherData.getWeather().get(1));
				wea3TextView.setText(weatherData.getWeather().get(2));
				wea4TextView.setText(weatherData.getWeather().get(3));
				wea5TextView.setText(weatherData.getWeather().get(4));

				setSupportProgressBarIndeterminateVisibility(false);
			}

		}
	}
}
