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

package android.ondevicepersonalization;

import android.annotation.NonNull;
import android.annotation.Nullable;
import android.ondevicepersonalization.rtb.BidRequest;
import android.ondevicepersonalization.rtb.BidResponse;

import java.util.List;

/**
 * Container for per-request state and APIs for {@link Exchange} and {@link Bidder} code to
 * interact with the OnDevicePersonalization sandbox or with each other.
 *
 * @hide
 */
public interface OnDevicePersonalizationContext {
    /**
     * Sends the response to be rendered on the calling app.
     * @param result The result of an exchange request. If null, no content will be rendered.
     */
    void sendExchangeResponse(@Nullable ExchangeResult result);

    /**
     * Creates a {@link BidRequest} to be sent to bidders.
     * @param bidRequest A bid request.
     * @param bidderPackageNames A list of bidders.
     */
    void sendBidRequests(
            @NonNull BidRequest bidRequest,
            @NonNull List<String> bidderPackageNames);

    /**
     * Sends the bid response from the bidder in response to an exchange request.
     * @param bidResponse The Bid Response from a bidder.
     */
    void sendBidResponse(@Nullable BidResponse bidResponse);
}