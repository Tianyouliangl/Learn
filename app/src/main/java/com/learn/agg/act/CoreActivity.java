package com.learn.agg.act;


import android.graphics.Bitmap;

import com.google.zxing.Result;
import com.google.zxing.client.result.AddressBookParsedResult;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.google.zxing.client.result.URIParsedResult;
import com.learn.agg.R;
import com.learn.agg.base.BaseActivity;
import com.mylhyl.zxing.scanner.OnScannerCompletionListener;
import com.mylhyl.zxing.scanner.ScannerView;


public class CoreActivity extends BaseActivity implements OnScannerCompletionListener {

    private ScannerView scanner_view;

    @Override
    protected Boolean isRequestMission() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_core;
    }

    @Override
    protected void initView() {
        super.initView();
        scanner_view = findViewById(R.id.scanner_view);
    }

    @Override
    protected void initData() {
        super.initData();
        scanner_view.setOnScannerCompletionListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        scanner_view.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanner_view.onPause();
    }

    @Override
    public void OnScannerCompletion(Result rawResult, ParsedResult parsedResult, Bitmap barcode) {
        ParsedResultType type = parsedResult.getType();
        switch (type) {
            case ADDRESSBOOK:
                AddressBookParsedResult addressBook = (AddressBookParsedResult) parsedResult;
//                bundle.putSerializable(Intents.Scan.RESULT, new AddressBookResult(addressBook));
                break;
            case URI:
                URIParsedResult uriParsedResult = (URIParsedResult) parsedResult;
//                bundle.putString(Intents.Scan.RESULT, uriParsedResult.getURI());
                break;
            case TEXT:
//                bundle.putString(Intents.Scan.RESULT, rawResult.getText());
                break;
        }
    }
}
