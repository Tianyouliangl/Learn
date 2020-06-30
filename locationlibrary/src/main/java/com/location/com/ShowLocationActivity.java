package com.location.com;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.TextView;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
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
import com.zyq.easypermission.EasyPermission;
import com.zyq.easypermission.EasyPermissionResult;

import java.io.Serializable;
import java.util.List;

public class ShowLocationActivity extends AppCompatActivity {

    private MapView mMapView;
    private MyLocationStyle myLocationStyle;
    private AMap mAMap;
    private UiSettings mSettings;
    private float local_zoom = 17;
    // 116.419949  39.899498
    private double latitude = 0;  // 纬度
    private double longitude = 0; //经度
    private LocationBody bean;
    private TextView tv_address_title;
    private TextView tv_address_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_location);
        getPersission(savedInstanceState);

    }

    private void initView(Bundle savedInstanceState) {
        mMapView = findViewById(R.id.mapView);
        tv_address_title = findViewById(R.id.tv_address_title);
        tv_address_msg = findViewById(R.id.tv_address_msg);
        mMapView.onCreate(savedInstanceState);
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }
        mSettings = mAMap.getUiSettings();
    }

    /**
     * 判断是否有相关权限
     *
     * @param savedInstanceState
     */
    private void getPersission(Bundle savedInstanceState) {
        Boolean location = EasyPermission.build().hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        Boolean read = EasyPermission.build().hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        Boolean write = EasyPermission.build().hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (location && read && write) {
            initView(savedInstanceState);
            initData();
            moveCamera();
        } else {
            arvixe(savedInstanceState);
        }

    }

    private void moveCamera() {
        LatLng latLng = new LatLng(latitude,longitude);
        initAMap(latLng);
        drawMarkers(latLng);
    }

    //移动到指定经纬度
    private void initAMap(LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition(latLng, local_zoom, 0, 30);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mAMap.moveCamera(cameraUpdate);
    }
    //画定位标记图
    public void drawMarkers(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(bean.getTitle());
        markerOptions.snippet(bean.getContent());
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.location));
        markerOptions.icon(bitmapDescriptor);
        markerOptions.visible(true);
        Marker marker = mAMap.addMarker(markerOptions);
        marker.showInfoWindow();
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
                .mPerms(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .mResult(new EasyPermissionResult() {
                    @Override
                    public void onPermissionsAccess(int requestCode) {
                        super.onPermissionsAccess(requestCode);
                        initView(savedInstanceState);
                        initData();
                        moveCamera();
                    }

                    @Override
                    public void onPermissionsDismiss(int requestCode, @NonNull List<String> permissions) {
                        super.onPermissionsDismiss(requestCode, permissions);
                        finish();
                    }
                }).requestPermission();
    }


    private void initData() {
        Bundle bundle = getIntent().getBundleExtra("bundle");
        bean = (LocationBody) bundle.getSerializable("bean");
        latitude = bean.getLatitude();
        longitude = bean.getLongitude();
        tv_address_title.setText(bean.getTitle());
        tv_address_msg.setText(bean.getContent());
        myLocationStyle = new MyLocationStyle();
        //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW);
        // myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.showMyLocation(false);
        //设置定位蓝点的Style
        mAMap.setMyLocationStyle(myLocationStyle);
        // 可触发定位并显示当前位置
        mAMap.setMyLocationEnabled(true);
        //开启以中心点进行手势操作
        mSettings.setGestureScaleByMapCenter(false);
        // 设置是否显示放大缩放按钮
        mSettings.setZoomControlsEnabled(false);
        // 指南针用于向 App 端用户展示地图方向，默认不显示
        mSettings.setCompassEnabled(false);
        // 定位按钮
        mSettings.setMyLocationButtonEnabled(false);
        // 比例尺
        mSettings.setScaleControlsEnabled(false);
        // 地图logo位置
        mSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
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
//        mlocationClient.stopLocation();
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
}
