/*
*       Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/

package com.huawei.hmssample.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.location.ActivityIdentification;
import com.huawei.hms.location.ActivityIdentificationData;
import com.huawei.hms.location.ActivityIdentificationService;
import com.huawei.hmssample.R;
import com.huawei.hmssample.RequestPermission;
import com.huawei.hmssample.location.fusedlocation.LocationBaseActivity;
import com.huawei.hmssample.location.fusedlocation.LocationBroadcastReceiver;
import com.huawei.logger.LocationLog;

import java.util.List;

public class ActivityIdentificationActivity extends LocationBaseActivity implements View.OnClickListener {
    public String TAG = "ActivityTransitionUpdate";

    public ActivityIdentificationService activityIdentificationService;

    public static LinearLayout.LayoutParams Type0, Type1, Type2, Type3, Type4, Type5, Type7, Type8;

    public static LinearLayout activityIN_VEHICLE, activityON_BICYCLE, activityON_FOOT, activitySTILL, activityUNKNOWN,
        activityTILTING, activityWALKING, activityRunning;

    private PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition_type);
        activityIdentificationService = ActivityIdentification.getService(this);
        RequestPermission.requestActivityTransitionPermission(this);
        findViewById(R.id.requestActivityTransitionUpdate).setOnClickListener(this);
        findViewById(R.id.removeActivityTransitionUpdate).setOnClickListener(this);
        activityIN_VEHICLE = (LinearLayout) findViewById(R.id.activityIN_VEHICLE);
        Type0 = (LinearLayout.LayoutParams) activityIN_VEHICLE.getLayoutParams();
        activityON_BICYCLE = (LinearLayout) findViewById(R.id.activityON_BICYCLE);
        Type1 = (LinearLayout.LayoutParams) activityON_BICYCLE.getLayoutParams();
        activityON_FOOT = (LinearLayout) findViewById(R.id.activityON_FOOT);
        Type2 = (LinearLayout.LayoutParams) activityON_FOOT.getLayoutParams();
        activitySTILL = (LinearLayout) findViewById(R.id.activitySTILL);
        Type3 = (LinearLayout.LayoutParams) activitySTILL.getLayoutParams();
        activityUNKNOWN = (LinearLayout) findViewById(R.id.activityUNKNOWN);
        Type4 = (LinearLayout.LayoutParams) activityUNKNOWN.getLayoutParams();
        activityTILTING = (LinearLayout) findViewById(R.id.activityTILTING);
        Type5 = (LinearLayout.LayoutParams) activityTILTING.getLayoutParams();
        activityWALKING = (LinearLayout) findViewById(R.id.activityWALKING);
        Type7 = (LinearLayout.LayoutParams) activityWALKING.getLayoutParams();
        activityRunning = (LinearLayout) findViewById(R.id.activityRunning);
        Type8 = (LinearLayout.LayoutParams) activityRunning.getLayoutParams();
        addLogFragment();
        reSet();

    }

    public void requestActivityUpdates(long detectionIntervalMillis) {
        try {
            if(pendingIntent != null){
                removeActivityUpdates();
            }
            pendingIntent = getPendingIntent();
            LocationBroadcastReceiver.addIdentificationListener();
            activityIdentificationService.createActivityIdentificationUpdates(detectionIntervalMillis, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        LocationLog.i(TAG, "createActivityIdentificationUpdates onSuccess");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        LocationLog.e(TAG, "createActivityIdentificationUpdates onFailure:" + e.getMessage());
                    }
                });
        } catch (Exception e) {
            LocationLog.e(TAG, "createActivityIdentificationUpdates exception:" + e.getMessage());
        }
    }

    public void removeActivityUpdates() {
        reSet();
        try {
            LocationBroadcastReceiver.removeIdentificationListener();
            Log.i(TAG, "start to removeActivityUpdates");
            activityIdentificationService.deleteActivityIdentificationUpdates(pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        LocationLog.i(TAG, "deleteActivityIdentificationUpdates onSuccess");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        LocationLog.e(TAG, "deleteActivityIdentificationUpdates onFailure:" + e.getMessage());
                    }
                });
        } catch (Exception e) {
            LocationLog.e(TAG, "removeActivityUpdates exception:" + e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.requestActivityTransitionUpdate:
                    requestActivityUpdates(5000);
                    break;
                case R.id.removeActivityTransitionUpdate:
                    removeActivityUpdates();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            LocationLog.e(TAG, "RequestLocationUpdatesWithCallbackActivity Exception:" + e);
        }
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, LocationBroadcastReceiver.class);
        intent.setAction(LocationBroadcastReceiver.ACTION_PROCESS_LOCATION);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    protected void onDestroy() {
        if(pendingIntent != null) {
            removeActivityUpdates();
        }
        super.onDestroy();
    }

    public static void setData(List<ActivityIdentificationData> list) {
        reSet();
        for (int i = 0; i < list.size(); i++) {
            int type = list.get(i).getIdentificationActivity();
            int value = list.get(i).getPossibility();
            try {
                switch (type) {
                    case 100:
                        Type0.width = Type0.width + value * 6;
                        activityIN_VEHICLE.setLayoutParams(Type0);
                        break;
                    case 101:
                        Type1.width = Type1.width + value * 6;
                        activityON_BICYCLE.setLayoutParams(Type1);
                        break;
                    case 102:
                        Type2.width = Type2.width + value * 6;
                        activityON_FOOT.setLayoutParams(Type2);
                        break;
                    case 103:
                        Type3.width = Type3.width + value * 6;
                        activitySTILL.setLayoutParams(Type3);
                        break;
                    case 104:
                        Type4.width = Type4.width + value * 6;
                        activityUNKNOWN.setLayoutParams(Type4);
                        break;
                    case 105:
                        Type5.width = Type5.width + value * 6;
                        activityTILTING.setLayoutParams(Type5);
                        break;
                    case 107:
                        Type7.width = Type7.width + value * 6;
                        activityWALKING.setLayoutParams(Type7);
                        break;
                    case 108:
                        Type8.width = Type8.width + value * 6;
                        activityRunning.setLayoutParams(Type8);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                LocationLog.e("ActivityTransitionUpdate", "setdata Exception");
            }
        }
    }

    public static void reSet() {
        Type0.width = 100;
        activityIN_VEHICLE.setLayoutParams(Type0);
        Type1.width = 100;
        activityON_BICYCLE.setLayoutParams(Type1);
        Type2.width = 100;
        activityON_FOOT.setLayoutParams(Type2);
        Type3.width = 100;
        activitySTILL.setLayoutParams(Type3);
        Type4.width = 100;
        activityUNKNOWN.setLayoutParams(Type4);
        Type5.width = 100;
        activityTILTING.setLayoutParams(Type5);
        Type7.width = 100;
        activityWALKING.setLayoutParams(Type7);
        Type8.width = 100;
        activityRunning.setLayoutParams(Type8);
    }
}
