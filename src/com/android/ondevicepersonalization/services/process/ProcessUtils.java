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

package com.android.ondevicepersonalization.services.process;

import android.annotation.NonNull;
import android.annotation.Nullable;
import android.content.Context;
import android.ondevicepersonalization.Constants;
import android.ondevicepersonalization.OnDevicePersonalizationException;
import android.os.Bundle;
import android.util.Log;

import androidx.concurrent.futures.CallbackToFutureAdapter;

import com.android.ondevicepersonalization.libraries.plugin.FailureType;
import com.android.ondevicepersonalization.libraries.plugin.PluginCallback;
import com.android.ondevicepersonalization.libraries.plugin.PluginController;
import com.android.ondevicepersonalization.libraries.plugin.PluginInfo;
import com.android.ondevicepersonalization.libraries.plugin.PluginManager;
import com.android.ondevicepersonalization.libraries.plugin.impl.PluginManagerImpl;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Objects;

/** Utilities to support loading and executing plugins. */
public class ProcessUtils {
    private static final String TAG = "ProcessUtils";
    private static final String ENTRY_POINT_CLASS =
            "com.android.ondevicepersonalization.services.process.OnDevicePersonalizationPlugin";

    public static final String PARAM_CLASS_NAME_KEY = "param.classname";
    public static final String PARAM_OPERATION_KEY = "param.operation";
    public static final String PARAM_DATA_ACCESS_BINDER = "param.binder";
    public static final String INPUT_APP_PACKAGE_NAME = "app_package_name";
    public static final String INPUT_APP_PARAMS = "app_params";
    public static final String INPUT_BID_IDS = "bid_ids";
    public static final String INPUT_PARCEL_FD = "parcel_fd";
    public static final String INPUT_SLOT_INFO = "slot_info";
    public static final String OUTPUT_RESULT_KEY = "result";

    public static final int OP_DOWNLOAD_FILTER_HANDLER = 1;
    public static final int OP_APP_REQUEST_HANDLER = 2;
    public static final int OP_RENDER_CONTENT_REQUEST_HANDLER = 3;
    public static final int OP_MAX = 4;  // 1 more than the last defined operation.

    private static PluginManager sPluginManager;

    /** Loads a service in an isolated process */
    @NonNull public static ListenableFuture<IsolatedServiceInfo> loadIsolatedService(
            @NonNull String taskName, @NonNull String packageName,
            @NonNull Context context) {
        try {
        return loadPlugin(createPluginController(
                createPluginId(packageName, taskName), getPluginManager(context), packageName));
        } catch (Exception e) {
            return Futures.immediateFailedFuture(e);
        }
    }

    /** Executes a service loaded in an isolated process */
    @NonNull public static ListenableFuture<Bundle> runIsolatedService(
            @NonNull IsolatedServiceInfo isolatedProcessInfo,
            @NonNull Bundle params) {
        return executePlugin(isolatedProcessInfo.getPluginController(), params);
    }

    @NonNull static PluginManager getPluginManager(@NonNull Context context) {
        synchronized (ProcessUtils.class) {
            if (sPluginManager == null) {
                sPluginManager = new PluginManagerImpl(context);
            }
            return sPluginManager;
        }
    }

    @NonNull static PluginController createPluginController(
            String taskName, @NonNull PluginManager pluginManager, @Nullable String apkName)
            throws Exception {
        PluginInfo info = PluginInfo.createJvmInfo(
                taskName, getArchiveList(apkName), ENTRY_POINT_CLASS);
        return Objects.requireNonNull(pluginManager.createPluginController(info));
    }

    @NonNull static ListenableFuture<IsolatedServiceInfo> loadPlugin(
            @NonNull PluginController pluginController) {
        return CallbackToFutureAdapter.getFuture(
            completer -> {
                try {
                    Log.d(TAG, "loadPlugin");
                    pluginController.load(new PluginCallback() {
                        @Override public void onSuccess(Bundle bundle) {
                            completer.set(new IsolatedServiceInfo(pluginController));
                        }
                        @Override public void onFailure(FailureType failure) {
                            completer.setException(new OnDevicePersonalizationException(
                                    Constants.STATUS_INTERNAL_ERROR,
                                    String.format("loadPlugin failed. %s", failure.toString())));
                        }
                    });
                } catch (Exception e) {
                    completer.setException(e);
                }
                return "loadPlugin";
            }
        );
    }

    @NonNull static ListenableFuture<Bundle> executePlugin(
            @NonNull PluginController pluginController, @NonNull Bundle pluginParams) {
        return CallbackToFutureAdapter.getFuture(
            completer -> {
                try {
                    Log.d(TAG, "executePlugin");
                    pluginController.execute(pluginParams, new PluginCallback() {
                        @Override public void onSuccess(Bundle bundle) {
                            completer.set(bundle);
                        }
                        @Override public void onFailure(FailureType failure) {
                            completer.setException(new OnDevicePersonalizationException(
                                    Constants.STATUS_INTERNAL_ERROR,
                                    String.format("executePlugin failed: %s", failure.toString())));
                        }
                    });
                } catch (Exception e) {
                    completer.setException(e);
                }
                return "executePlugin";
            }
        );
    }

    @NonNull static ImmutableList<PluginInfo.ArchiveInfo> getArchiveList(
            @Nullable String apkName) {
        if (apkName == null) {
            return ImmutableList.of();
        }
        ImmutableList.Builder<PluginInfo.ArchiveInfo> archiveInfoBuilder = ImmutableList.builder();
        archiveInfoBuilder.add(
                PluginInfo.ArchiveInfo.builder().setPackageName(apkName).build());
        return archiveInfoBuilder.build();
    }

    static String createPluginId(String vendorPackageName, String taskName) {
        // TODO(b/249345663) Perform any validation needed on the input.
        return vendorPackageName + "-" + taskName;
    }

    static String getVendorPackageNameFromPluginId(String pluginId) {
        // TODO(b/249345663) Perform any validation needed on the input.
        return pluginId.split("-")[0];
    }

    private ProcessUtils() {}
}