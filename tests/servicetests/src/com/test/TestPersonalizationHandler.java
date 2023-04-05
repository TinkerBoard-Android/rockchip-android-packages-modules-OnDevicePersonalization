/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.test;

import android.annotation.NonNull;
import android.ondevicepersonalization.DownloadInput;
import android.ondevicepersonalization.DownloadResult;
import android.ondevicepersonalization.EventMetricsInput;
import android.ondevicepersonalization.EventMetricsResult;
import android.ondevicepersonalization.Metrics;
import android.ondevicepersonalization.OnDevicePersonalizationContext;
import android.ondevicepersonalization.PersonalizationHandler;
import android.ondevicepersonalization.RenderContentInput;
import android.ondevicepersonalization.RenderContentResult;
import android.ondevicepersonalization.ScoredBid;
import android.ondevicepersonalization.SelectContentInput;
import android.ondevicepersonalization.SelectContentResult;
import android.ondevicepersonalization.SlotResult;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

// TODO(b/249345663) Move this class and related manifest to separate APK for more realistic testing
public class TestPersonalizationHandler implements PersonalizationHandler {
    public final String TAG = "TestPersonalizationHandler";

    @Override
    public void onDownload(DownloadInput input, OnDevicePersonalizationContext odpContext,
            Consumer<DownloadResult> consumer) {
        try {
            Log.d(TAG, "Starting filterData.");
            Log.d(TAG, "Data: " + input.getData());

            Log.d(TAG, "Existing keyExtra: "
                    + Arrays.toString(odpContext.getRemoteData().get("keyExtra")));
            Log.d(TAG, "Existing keySet: " + odpContext.getRemoteData().keySet());

            List<String> keysToRetain =
                    getFilteredKeys(input.getData());
            keysToRetain.add("keyExtra");
            // Get the keys to keep from the downloaded data
            DownloadResult downloadResult =
                    new DownloadResult.Builder()
                            .setKeysToRetain(keysToRetain)
                            .build();
            consumer.accept(downloadResult);
        } catch (Exception e) {
            Log.e(TAG, "Error occurred in onDownload", e);
        }
    }

    @Override public void selectContent(
            @NonNull SelectContentInput input,
            @NonNull OnDevicePersonalizationContext odpContext,
            @NonNull Consumer<SelectContentResult> consumer
    ) {
        Log.d(TAG, "onAppRequest() started.");
        SelectContentResult result = new SelectContentResult.Builder()
                .addSlotResults(new SlotResult.Builder()
                        .setSlotId("slot_id")
                        .addWinningBids(
                            new ScoredBid.Builder()
                            .setBidId("bid1").setPrice(5.0).setScore(1.0).build())
                        .build())
                .build();
        consumer.accept(result);
    }

    @Override public void renderContent(
            @NonNull RenderContentInput input,
            @NonNull OnDevicePersonalizationContext odpContext,
            @NonNull Consumer<RenderContentResult> consumer
    ) {
        Log.d(TAG, "renderContent() started.");
        RenderContentResult result =
                new RenderContentResult.Builder()
                .setContent("<p>RenderResult: " + String.join(",", input.getBidIds()) + "<p>")
                .build();
        consumer.accept(result);
    }

    public void computeEventMetrics(
            @NonNull EventMetricsInput input,
            @NonNull OnDevicePersonalizationContext odpContext,
            @NonNull Consumer<EventMetricsResult> consumer
    ) {
        int intValue = 0;
        double floatValue = 0.0;
        if (input.getEventParams() != null) {
            intValue = input.getEventParams().getInt("a");
            floatValue = input.getEventParams().getDouble("b");
        }
        EventMetricsResult result =
                new EventMetricsResult.Builder()
                    .setMetrics(
                            new Metrics.Builder()
                                .setLongValues(intValue)
                                .setDoubleValues(floatValue)
                                .build())
                    .build();
        Log.d(TAG, "computeEventMetrics() result: " + result.toString());
        consumer.accept(result);
    }

    private List<String> getFilteredKeys(Map<String, byte[]> data) {
        Set<String> filteredKeys = data.keySet();
        filteredKeys.remove("key3");
        return new ArrayList<>(filteredKeys);
    }
}
