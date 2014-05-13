package com.example.weather;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
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
	// 侧边菜单栏
	private MenuDrawer mMenuDrawer;
	private Weather weatherData;
	// ViewPager
	public ViewPager myViewPager;
	private MyPagerAdapter myAdapter;
	private LayoutInflater mInflater;
	private List<View> mListViews;
	private View layout1 = null; // 第一个界面
	private View layout2 = null; // 第二个界面

	// 第一个界面组件
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

	// 第二个界面组件
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

	// 侧面菜单栏地址列表
	private List<Map<String, String>> addressList;
	private SimpleAdapter adapter;
	private ListView menuListView;

	private String id = "101280101"; // 默认广州

	private Animation animation; // 渐变动画
	private LinearLayout layout; // 动画载体

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		new WriteToSD(this); // 将数据库写入SD卡
		new Constants(this);

		initMenu();
		initPage();
		initAnim();
		initWidget();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setLogo(R.drawable.icon);
		getSupportActionBar().setTitle("Weather");
		getSupportActionBar().setBackgroundDrawable(
				this.getResources().getDrawable(R.drawable.base_actionbar_bg));
		setSupportProgressBarIndeterminateVisibility(false); // 加载进度隐藏

	}

	// 初始化侧滑菜单
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
	}

	// 初始化滑动界面
	private void initPage() {
		mListViews = new ArrayList<View>();
		mInflater = getLayoutInflater();
		layout1 = mInflater.inflate(R.layout.activity_main, null);
		layout2 = mInflater.inflate(R.layout.trend, null);
		// 将布局添加进ViewPager
		mListViews.add(layout1);
		mListViews.add(layout2);

		myViewPager = (ViewPager) findViewById(R.id.viewpagerLayout);
		myAdapter = new MyPagerAdapter(mListViews);
		myViewPager.setAdapter(myAdapter);

		myViewPager.setCurrentItem(0); // 初始化当前显示的view
		myViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int arg0) {
				switch (arg0) {
				case 0:
					mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
					break;
				case 1: // 如果不是第一页侧侧边栏滑动无效
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

	// 初始化动画
	private void initAnim() {
		// 城市箭头补间动画
		TextView textView = (TextView) layout1.findViewById(R.id.city);
		AnimationDrawable ad = (AnimationDrawable) textView
				.getCompoundDrawables()[0];
		ad.start();

		// trend arrow
		ImageView imageView = (ImageView) layout1.findViewById(R.id.trendarrow);
		AnimationDrawable treadAD = (AnimationDrawable) imageView
				.getBackground();
		treadAD.start();

		// 透明度变化动画
		animation = new AlphaAnimation(1, 0);
		animation.setDuration(700);
		animation.setRepeatCount(1);
		animation.setRepeatMode(Animation.REVERSE);
	}

	// 初始化控件
	private void initWidget() {
		// 第一页面控件
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

		// 第二页面控件
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
		
		// 点击城市，更换城市天气
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
		// 菜单栏地区列表
		menuListView = (ListView) findViewById(R.id.menuaddresslist);
		addressList = new ArrayList<Map<String, String>>();
		String[] from = new String[] { "address" };
		int[] to = new int[] { android.R.id.text1 };
		adapter = new SimpleAdapter(this, addressList,
				android.R.layout.simple_list_item_single_choice, from, to);
		menuListView.setAdapter(adapter);

		// 菜单栏地区添加按钮
		Button add = (Button) mMenuDrawer.findViewById(R.id.addaddress);
		add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				add();
			}
		});
		// 点击列表地区更新天气
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
		// 长按删除地区
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

		// 初始化载入数据库地区数据并更新
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
	
	public String getDateFormat(Calendar calendar){
		String formatDate = "";
		Date date = calendar.getTime();
		
		SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(R.string.dateformat));
		//new SimpleDateFormat("", new Local);
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
					Toast.makeText(getApplicationContext(), "地区已存在",
							Toast.LENGTH_SHORT).show();
					break;
				}
				addressList.add(map);
				refresh();
				adapter.notifyDataSetChanged();
				menuListView.setItemChecked(menuListView.getCount() - 1, true);

				DB.saveCityAndId(map.get("address"), id); // 保存添加的地区
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
		// 标题栏菜单按钮事件
		switch (item.getItemId()) {
		case android.R.id.home:
			mMenuDrawer.toggleMenu();
			break;
		case 1: // 添加
			add();
			break;
		case 0: // 更新
			refresh();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// 转到地区选择界面
	private void add() {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, AddressActivity.class);
		startActivityForResult(intent, 1);
		overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
	}

	// 更新天气
	private void refresh() {
		setSupportProgressBarIndeterminateVisibility(true);

		// 判断网络状态
		if (!"NULL".equals(NetworkInfoUtil.getNetWorkType(this))) {

			MenuTask task = new MenuTask();
			task.execute(0);
			layout.startAnimation(animation);
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

	/**
	 * 异步查询天气
	 * 
	 * @author Dave
	 * 
	 */
	class MenuTask extends AsyncTask<Integer, Integer, Integer> {
		@Override
		protected Integer doInBackground(Integer... i) {// 处理后台执行的任务，在后台线程执行
			WeatherData data = new WeatherData(MainActivity.this);
			Log.i("TAG", "id: " + id);
			weatherData = data.getData("http://m.weather.com.cn/data/" + id
					+ ".html");
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {// 后台任务执行完之后被调用，在ui线程执行
			// 更新界面控件值
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
			tomorrowTemperature.setText(weatherData.getTomorrowTemperature());
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

			// 完成
			setSupportProgressBarIndeterminateVisibility(false);
		}
	}
}
