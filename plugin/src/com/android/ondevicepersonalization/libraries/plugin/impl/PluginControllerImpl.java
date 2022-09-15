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

package com.android.ondevicepersonalization.libraries.plugin.impl;

import android.content.Context;
import android.os.PersistableBundle;
import android.os.RemoteException;

import com.android.ondevicepersonalization.libraries.plugin.FailureType;
import com.android.ondevicepersonalization.libraries.plugin.PluginCallback;
import com.android.ondevicepersonalization.libraries.plugin.PluginController;
import com.android.ondevicepersonalization.libraries.plugin.PluginInfo;
import com.android.ondevicepersonalization.libraries.plugin.PluginState;
import com.android.ondevicepersonalization.libraries.plugin.PluginStateCallback;
import com.android.ondevicepersonalization.libraries.plugin.internal.CallbackConverter;
import com.android.ondevicepersonalization.libraries.plugin.internal.IPluginCallback;
import com.android.ondevicepersonalization.libraries.plugin.internal.IPluginExecutorService;
import com.android.ondevicepersonalization.libraries.plugin.internal.IPluginStateCallback;
import com.android.ondevicepersonalization.libraries.plugin.internal.PluginExecutorServiceProvider;
import com.android.ondevicepersonalization.libraries.plugin.internal.PluginInfoInternal;
import com.android.ondevicepersonalization.libraries.plugin.internal.PluginLoader;

import com.google.common.util.concurrent.SettableFuture;

/**
 * Implementation of {@link PluginController} that executes {@link Plugin} implementations in the
 * {@link IPluginExecutorService} provided by the passed in {@link PluginExecutorServiceProvider}.
 */
public class PluginControllerImpl implements PluginController {
    private final PluginInfo mInfo;
    private final Context mContext;
    private final PluginExecutorServiceProvider mPluginExecutorServiceProvider;

    public PluginControllerImpl(
            Context context,
            PluginExecutorServiceProvider pluginExecutorServiceProvider,
            PluginInfo info) {
        this.mContext = context;
        this.mInfo = info;
        this.mPluginExecutorServiceProvider = pluginExecutorServiceProvider;
    }

    @Override
    public void load(PluginCallback callback) throws RemoteException {
        IPluginCallback parcelablePluginCallback = CallbackConverter.toIPluginCallback(callback);
        PluginInfoInternal.Builder infoBuilder = PluginInfoInternal.builder();
        infoBuilder.setTaskName(mInfo.taskName());
        infoBuilder.setEntryPointClassName(mInfo.entryPointClassName());

        PluginLoader.PluginTask task =
                infoInternal ->
                        mPluginExecutorServiceProvider
                                .getExecutorService()
                                .load(infoInternal, parcelablePluginCallback);
        SettableFuture<Boolean> serviceReadiness =
                mPluginExecutorServiceProvider.getExecutorServiceReadiness();

        if (!PluginLoader.prepareThenRun(
                mContext,
                serviceReadiness,
                "PluginExecutorService",
                infoBuilder,
                mInfo.archives(),
                task)) {
            callback.onFailure(FailureType.ERROR_LOADING_PLUGIN);
        }
    }

    @Override
    public void unload(PluginCallback callback) throws RemoteException {
        IPluginExecutorService pluginExecutorService =
                mPluginExecutorServiceProvider.getExecutorService();
        if (pluginExecutorService == null) {
            callback.onFailure(FailureType.ERROR_UNLOADING_PLUGIN);
            return;
        }

        IPluginCallback parcelablePluginCallback = CallbackConverter.toIPluginCallback(callback);
        try {
            pluginExecutorService.unload(mInfo.taskName(), parcelablePluginCallback);
        } catch (RemoteException e) {
            // This callback call may throw RemoteException, which we pass on.
            callback.onFailure(FailureType.ERROR_UNLOADING_PLUGIN);
        }
    }

    @Override
    public void execute(PersistableBundle input, PluginCallback callback) throws RemoteException {
        IPluginExecutorService pluginExecutorService =
                mPluginExecutorServiceProvider.getExecutorService();
        if (pluginExecutorService == null) {
            callback.onFailure(FailureType.ERROR_EXECUTING_PLUGIN);
            return;
        }

        IPluginCallback parcelablePluginCallback = CallbackConverter.toIPluginCallback(callback);
        try {
            pluginExecutorService.execute(mInfo.taskName(), input, parcelablePluginCallback);
        } catch (RemoteException e) {
            // This callback call may throw RemoteException, which we pass on.
            callback.onFailure(FailureType.ERROR_EXECUTING_PLUGIN);
        }
    }

    @Override
    public void checkPluginState(PluginStateCallback callback) {
        IPluginExecutorService pluginExecutorService =
                mPluginExecutorServiceProvider.getExecutorService();
        if (pluginExecutorService == null) {
            callback.onState(PluginState.STATE_NO_SERVICE);
            return;
        }
        IPluginStateCallback parcelableStateCallback =
                CallbackConverter.toIPluginStateCallback(callback);
        try {
            pluginExecutorService.checkPluginState(mInfo.taskName(), parcelableStateCallback);
        } catch (RemoteException e) {
            callback.onState(PluginState.STATE_EXCEPTION_THROWN);
        }
    }

    @Override
    public String getName() {
        return mInfo.taskName();
    }
}