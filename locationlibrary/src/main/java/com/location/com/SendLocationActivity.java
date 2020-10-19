package com.location.com;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.TranslateAnimation;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.gyf.barlibrary.BarHide;
import com.gyf.barlibrary.ImmersionBar;
import com.learn.commonalitylibrary.body.LocationBody;
import com.zyq.easypermission.EasyPermission;
import com.zyq.easypermission.EasyPermissionResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SendLocationActivity extends AppCompatActivity implements AMapLocationListener, AMap.OnCameraChangeListener, View.OnClickListener, AddressAdapter.locationInterface {

    private MapView mMapView;
    private AMap mAMap;
    private MyLocationStyle myLocationStyle;
    private UiSettings mSettings; // 管理mapview
    //声明mlocationClient对象
    public AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private MarkerOptions markerOption; // 自定义marker
    private Marker marker; // 标记点
    private double local_lat; // 经度 自己位置
    private double local_lon; // 纬度 自己位置
    private float local_zoom = 17; // 缩放级别 0～20
    private Animation animation;    // 动画
    private RecyclerView mRvAddress; // 周边位置信息
    private Boolean isStart = false; // 判断是否取消动画
    private String cityCode;        // 城市编码
    private int pageSize = 15;     // 周边地址条数
    private int pageNum = 1;     // 周边地址页数
    private AddressAdapter addressAdapter;
    private List<PoiltemBean> poiList = new ArrayList<>();
    private Boolean isSearch = true;
    private ProgressBar pb_bar;
    private Button btn_send;
    private TextView tv_cancel;
    private DialogUtils mDialog;
    public static final String BaseImagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/learn/gaode/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_location);
        initBar();
        getPersission(savedInstanceState);
    }

    /**
     * 判断是否有相关权限
     *
     * @param savedInstanceState
     */
    private void getPersission(Bundle savedInstanceState) {
        if (EasyPermission
                .build()
                .hasPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
            initView(savedInstanceState);
            initData();
        } else {
            arvixe(savedInstanceState);
        }

    }

    /**
     * 动态申请权限
     *
     * @param savedInstanceState
     */
    private void arvixe(final Bundle savedInstanceState) {
        EasyPermission.build()
                .mRequestCode(10010)
                .mContext(this)
                .mPerms(Manifest.permission.ACCESS_FINE_LOCATION)
                .mResult(new EasyPermissionResult() {
                    @Override
                    public void onPermissionsAccess(int requestCode) {
                        super.onPermissionsAccess(requestCode);
                        initView(savedInstanceState);
                        initData();
                    }

                    @Override
                    public void onPermissionsDismiss(int requestCode, @NonNull List<String> permissions) {
                        super.onPermissionsDismiss(requestCode, permissions);
                        finish();
                    }
                }).requestPermission();
    }

    /**
     * 设置地图参数
     */
    private void initData() {
        isShowPbBar(true);
        mDialog = new DialogUtils(this, R.style.CustomDialog);
        addressAdapter = new AddressAdapter(SendLocationActivity.this);
        mRvAddress.setLayoutManager(new LinearLayoutManager(SendLocationActivity.this));
        mRvAddress.setAdapter(addressAdapter);
        addressAdapter.setOnClickItem(this);
        mAMap.setOnCameraChangeListener(this);
        myLocationStyle = new MyLocationStyle();
        //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW);
        // myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.showMyLocation(true);
        // 自定义定位蓝点
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.yuan));
//        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(50, 0, 0, 180));// 设置圆形的填充颜色
        myLocationStyle.strokeWidth(0.0f);// 设置圆形的边框粗细
        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        mAMap.setMyLocationEnabled(true);
        //设置定位蓝点的Style
        mAMap.setMyLocationStyle(myLocationStyle);
        // 可触发定位并显示当前位置
        mAMap.setMyLocationEnabled(true);
        //开启以中心点进行手势操作
        mSettings.setGestureScaleByMapCenter(true);
        // 设置是否显示放大缩放按钮
        mSettings.setZoomControlsEnabled(false);
        // 指南针用于向 App 端用户展示地图方向，默认不显示
        mSettings.setCompassEnabled(false);
        // 定位按钮
        mSettings.setMyLocationButtonEnabled(false);
        // 比例尺
        mSettings.setScaleControlsEnabled(false);
        // 地图logo位置
        // AMapOptions.LOGO_POSITION_BOTTOM_LEFT  左边
        // AMapOptions.LOGO_MARGIN_BOTTOM 底部
        // AMapOptions.LOGO_MARGIN_RIGHT  右边
        // AMapOptions.LOGO_POSITION_BOTTOM_CENTER 底部居中
        // AMapOptions.LOGO_POSITION_BOTTOM_LEFT 左下角
        // AMapOptions.LOGO_POSITION_BOTTOM_RIGHT 右下角
        mSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
        // 开始定位
        startLocation();
    }

    private void showDialog() {
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
    }

    private void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    /**
     * 开始定位 以及设置定位的参数
     */
    private void startLocation() {
        mlocationClient = new AMapLocationClient(getApplicationContext());
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();

        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        // 启动定位
        mlocationClient.startLocation();
        //设置定位监听
        mlocationClient.setLocationListener(this);
    }

    /**
     * 初始化控件
     *
     * @param savedInstanceState
     */
    private void initView(Bundle savedInstanceState) {
        mMapView = findViewById(R.id.mapView);
        ImageView iv_location = findViewById(R.id.iv_location);
        mRvAddress = findViewById(R.id.rv_address);
        pb_bar = findViewById(R.id.pb_bar);
        btn_send = findViewById(R.id.btn_send);
        tv_cancel = findViewById(R.id.tv_cancel);
        mMapView.onCreate(savedInstanceState);
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }
        mSettings = mAMap.getUiSettings();
        iv_location.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
    }

    /**
     * 可设置全屏
     */
    private void initBar() {
        ImmersionBar.with(this)
                .transparentStatusBar()  //透明状态栏，不写默认透明色
                .hideBar(BarHide.FLAG_HIDE_STATUS_BAR)
                .transparentNavigationBar()  //透明导航栏，不写默认黑色(设置此方法，fullScreen()方法自动为 true)
                .transparentBar()             //透明状态栏和导航栏，不写默认状态栏为透明色，导航栏为黑色（设置此方法，fullScreen()方法自动为 true）
                .fullScreen(false)
                .statusBarAlpha(0.3f)  //状态栏透明度，不写默认 0.0f
                .navigationBarAlpha(0.2f)  //导航栏透明度，不写默认 0.0F
                .barAlpha(0.3f)  //状态栏和导航栏透明度，不写默认 0.0f
                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                .init();
    }

    /**
     * 地图生命周期于activity绑定
     */
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mlocationClient.stopLocation();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    // 定位监听回调
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                double latitude = amapLocation.getLatitude();//获取纬度
                double longitude = amapLocation.getLongitude();//获取经度
                local_lat = latitude;
                local_lon = longitude;
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                amapLocation.getCountry();//国家信息
                amapLocation.getProvince();//省信息
                amapLocation.getCity();//城市信息
                amapLocation.getDistrict();//城区信息
                amapLocation.getStreet();//街道信息
                amapLocation.getStreetNum();//街道门牌号信息
                cityCode = amapLocation.getCityCode();//城市编码
                amapLocation.getAdCode();//地区编码
                amapLocation.getAoiName();//获取当前定位点的AOI信息
                mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), local_zoom));
                if (marker == null) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(latitude, longitude));
                    markerOptions.title(amapLocation.getCity());
                    markerOptions.snippet(amapLocation.getAddress());
                    markerOptions.visible(true);
                    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.location));
                    markerOptions.icon(bitmapDescriptor);
                    marker = mAMap.addMarker(markerOptions);
                } else {
                    marker.setPosition(new LatLng(latitude, longitude));
                }
                Log.i("gd", "--------定位code:" + amapLocation.getErrorCode());
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
                finish();
            }
        }
    }

    /**
     * 地图位置改变
     *
     * @param cameraPosition
     */
    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        cancelAnimation();
        LatLng target = cameraPosition.target;
        local_zoom = cameraPosition.zoom;
        marker.setPosition(target);
    }

    /**
     * 取消marker的动画
     */
    private void cancelAnimation() {
        if (animation != null) {
            if (isStart) {
                animation.glAnimation.cancel();
                animation.glAnimation.reset();
            }
        }
    }

    /**
     * 地图改变结束
     *
     * @param cameraPosition
     */
    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        if (isSearch) {
            isShowPbBar(true);
            initNearByParam();
            screenMarkerJump(mAMap, marker);
        }
        isSearch = true;
    }

    /**
     * 添加marker动画
     *
     * @param aMap
     * @param screenMarker
     */
    public void screenMarkerJump(AMap aMap, Marker screenMarker) {
        if (screenMarker != null) {

            final LatLng latLng = screenMarker.getPosition();
            Point point = aMap.getProjection().toScreenLocation(latLng);
            point.y -= dpToPx(20);
            LatLng target = aMap.getProjection()
                    .fromScreenLocation(point);
            //使用TranslateAnimation,填写一个需要移动的目标点
            animation = new TranslateAnimation(target);
            animation.setInterpolator(new Interpolator() {
                @Override
                public float getInterpolation(float input) {
                    // 模拟重加速度的interpolator
                    if (input <= 0.5) {
                        return (float) (0.5f - 2 * (0.5 - input) * (0.5 - input));
                    } else {
                        return (float) (0.5f - Math.sqrt((input - 0.5f) * (1.5f - input)));
                    }
                }
            });
            //整个移动所需要的时间
            animation.setDuration(600);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart() {
                    isStart = true;
                }

                @Override
                public void onAnimationEnd() {
                    isStart = false;
                }
            });
            //设置动画
            screenMarker.setAnimation(animation);
            //开始动画
            screenMarker.startAnimation();
        }
    }

    /**
     * 附近位置相关配置
     */
    private void initNearByParam() {
        if (cityCode != null) {
            PoiSearch.Query query = new PoiSearch.Query("", "", cityCode);
            query.setPageSize(pageSize);
            query.setPageNum(pageNum);
            PoiSearch poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {

                @Override
                public void onPoiSearched(PoiResult poiResult, int rCode) {
                    if (rCode == 1000 && poiResult != null && poiResult.getPois().size() > 0) {
                        ArrayList<PoiItem> pois = poiResult.getPois();
                        poiList.clear();
                        for (int i = 0; i < pois.size(); i++) {
                            PoiItem item = pois.get(i);
                            String title = item.getTitle();
                            String snippet = item.getSnippet();
                            int distance = item.getDistance();
                            String cityCode = item.getCityCode();
                            LatLonPoint latLonPoint = item.getLatLonPoint();
                            String searchAddress = item.getProvinceName() + item.getCityName() + item.getAdName() + snippet;
                            if (i == 0) {
                                poiList.add(new PoiltemBean(cityCode, searchAddress, title, snippet, distance, latLonPoint, true));
                            } else {
                                poiList.add(new PoiltemBean(cityCode, searchAddress, title, snippet, distance, latLonPoint, false));
                            }
                        }
                        addressAdapter.setData(poiList);
                        mRvAddress.scrollToPosition(0);
                        isShowPbBar(false);
                    } else {
                        Log.i("gd", "搜索附近失败");
                    }
                }

                @Override
                public void onPoiItemSearched(PoiItem poiItem, int i) {

                }
            });
            poiSearch.searchPOIAsyn();
            poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(marker.getPosition().latitude, marker.getPosition().longitude), 1000));//设置周边搜索的中心点以及半径
        } else {

        }
    }

    /**
     * 是否显示pb
     *
     * @param b
     */
    private void isShowPbBar(Boolean b) {
        pb_bar.setVisibility(b ? View.VISIBLE : View.GONE);
        mRvAddress.setVisibility(b ? View.GONE : View.VISIBLE);
        btn_send.setEnabled(!b);
    }


    /**
     * dp转px
     *
     * @param dps
     * @return
     */
    int dpToPx(int dps) {
        return Math.round(getResources().getDisplayMetrics().density * dps);
    }

    /**
     * View点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_location) {
            mAMap.animateCamera(CameraUpdateFactory.changeLatLng(new LatLng(local_lat, local_lon)));
        }
        if (v.getId() == R.id.tv_cancel) {
            finish();
        }
        if (v.getId() == R.id.btn_send) {
            showDialog();
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    getMapPng();
                }
            }.start();

        }
    }

    /**
     * item 点击事件
     *
     * @param bean
     * @param position
     */
    @Override
    public void onItemClick(PoiltemBean bean, int position) {
        isSearch = false;
        if (!bean.getChecked()) {
            addressAdapter.checked(position);
            LatLng latLng = new LatLng(bean.getLatLonPoint().getLatitude(), bean.getLatLonPoint().getLongitude());
            mAMap.animateCamera(CameraUpdateFactory.changeLatLng(latLng));
        }
    }

    void getMapPng() {
        mAMap.getMapScreenShot(new AMap.OnMapScreenShotListener() {
            @Override
            public void onMapScreenShot(Bitmap bitmap) {

            }

            @Override
            public void onMapScreenShot(Bitmap bitmap, int status) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                if (null == bitmap) {
                    dismissDialog();
                    Toast.makeText(SendLocationActivity.this,"请重试",Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    File dir = new File(BaseImagePath);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    String path = BaseImagePath + sdf.format(new Date()) + ".png";
                    FileOutputStream fos = new FileOutputStream(path);
                    boolean b = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    try {
                        fos.flush();
                    } catch (IOException e) {
                        path = null;
                        e.printStackTrace();
                    }
                    try {
                        fos.close();
                    } catch (IOException e) {
                        path = null;
                        e.printStackTrace();
                    }
                    StringBuffer buffer = new StringBuffer();
                    if (b) {
                        buffer.append("截屏成功 ");
                    } else {
                        buffer.append("截屏失败 ");
                        path = null;
                    }
                    if (status != 0)
                        buffer.append("地图渲染完成，截屏无网格");
                    else {
                        buffer.append("地图未渲染完成，截屏有网格");
                        path = null;
                    }
                    PoiltemBean bean = addressAdapter.getChecked();
                    if (path != null && bean != null) {
                        LocationBody body = new LocationBody();
                        bean.setMapPng(path);
                        dismissDialog();
                        Log.i("gd", "\n-------" + buffer.toString() + "\n------path:" + path + "\n-----addressTitle:" + bean.getTitle() +
                                "\n-----address:" + bean.getAddress() + "\n-----经度:" + bean.getLatLonPoint().getLongitude() + "\n-----纬度:" +
                                bean.getLatLonPoint().getLatitude());
                        body.setContent(bean.getAddress());
                        body.setTitle(bean.getTitle());
                        body.setLatitude(bean.getLatLonPoint().getLatitude());
                        body.setLongitude(bean.getLatLonPoint().getLongitude());
                        body.setLocation_url(path);
                        body.setUrl("");
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("bean",body);
                        Intent intent = new Intent();
                        intent.putExtra("bundle",bundle);
                        setResult(RESULT_OK, intent);
                        finish();
                    }else {
                        dismissDialog();
                        Toast.makeText(SendLocationActivity.this,"请重试",Toast.LENGTH_SHORT).show();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    dismissDialog();
                    Toast.makeText(SendLocationActivity.this,"请重试",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
