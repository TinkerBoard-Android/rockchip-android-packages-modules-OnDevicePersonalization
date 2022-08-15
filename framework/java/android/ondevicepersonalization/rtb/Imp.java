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

package android.ondevicepersonalization.rtb;

import android.annotation.Nullable;
import android.os.Parcelable;

import com.android.ondevicepersonalization.internal.util.DataClass;

/**
 * An Imp object in a {@link BidRequest}.
 *
 * @hide
 */
@DataClass(genBuilder = true, genEqualsHashCode = true)
public final class Imp implements Parcelable {
    @Nullable private Banner mBanner = null;



    // Code below generated by codegen v1.0.23.
    //
    // DO NOT MODIFY!
    // CHECKSTYLE:OFF Generated code
    //
    // To regenerate run:
    // $ codegen $ANDROID_BUILD_TOP/packages/modules/OnDevicePersonalization/framework/java/android/ondevicepersonalization/rtb/Imp.java
    //
    // To exclude the generated code from IntelliJ auto-formatting enable (one-time):
    //   Settings > Editor > Code Style > Formatter Control
    //@formatter:off


    @DataClass.Generated.Member
    /* package-private */ Imp(
            @Nullable Banner banner) {
        this.mBanner = banner;

        // onConstructed(); // You can define this method to get a callback
    }

    @DataClass.Generated.Member
    public @Nullable Banner getBanner() {
        return mBanner;
    }

    @Override
    @DataClass.Generated.Member
    public boolean equals(@Nullable Object o) {
        // You can override field equality logic by defining either of the methods like:
        // boolean fieldNameEquals(Imp other) { ... }
        // boolean fieldNameEquals(FieldType otherValue) { ... }

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        @SuppressWarnings("unchecked")
        Imp that = (Imp) o;
        //noinspection PointlessBooleanExpression
        return true
                && java.util.Objects.equals(mBanner, that.mBanner);
    }

    @Override
    @DataClass.Generated.Member
    public int hashCode() {
        // You can override field hashCode logic by defining methods like:
        // int fieldNameHashCode() { ... }

        int _hash = 1;
        _hash = 31 * _hash + java.util.Objects.hashCode(mBanner);
        return _hash;
    }

    @Override
    @DataClass.Generated.Member
    public void writeToParcel(@android.annotation.NonNull android.os.Parcel dest, int flags) {
        // You can override field parcelling by defining methods like:
        // void parcelFieldName(Parcel dest, int flags) { ... }

        byte flg = 0;
        if (mBanner != null) flg |= 0x1;
        dest.writeByte(flg);
        if (mBanner != null) dest.writeTypedObject(mBanner, flags);
    }

    @Override
    @DataClass.Generated.Member
    public int describeContents() { return 0; }

    /** @hide */
    @SuppressWarnings({"unchecked", "RedundantCast"})
    @DataClass.Generated.Member
    /* package-private */ Imp(@android.annotation.NonNull android.os.Parcel in) {
        // You can override field unparcelling by defining methods like:
        // static FieldType unparcelFieldName(Parcel in) { ... }

        byte flg = in.readByte();
        Banner banner = (flg & 0x1) == 0 ? null : (Banner) in.readTypedObject(Banner.CREATOR);

        this.mBanner = banner;

        // onConstructed(); // You can define this method to get a callback
    }

    @DataClass.Generated.Member
    public static final @android.annotation.NonNull Parcelable.Creator<Imp> CREATOR
            = new Parcelable.Creator<Imp>() {
        @Override
        public Imp[] newArray(int size) {
            return new Imp[size];
        }

        @Override
        public Imp createFromParcel(@android.annotation.NonNull android.os.Parcel in) {
            return new Imp(in);
        }
    };

    /**
     * A builder for {@link Imp}
     */
    @SuppressWarnings("WeakerAccess")
    @DataClass.Generated.Member
    public static final class Builder {

        private @Nullable Banner mBanner;

        private long mBuilderFieldsSet = 0L;

        public Builder() {
        }

        @DataClass.Generated.Member
        public @android.annotation.NonNull Builder setBanner(@android.annotation.NonNull Banner value) {
            checkNotUsed();
            mBuilderFieldsSet |= 0x1;
            mBanner = value;
            return this;
        }

        /** Builds the instance. This builder should not be touched after calling this! */
        public @android.annotation.NonNull Imp build() {
            checkNotUsed();
            mBuilderFieldsSet |= 0x2; // Mark builder used

            if ((mBuilderFieldsSet & 0x1) == 0) {
                mBanner = null;
            }
            Imp o = new Imp(
                    mBanner);
            return o;
        }

        private void checkNotUsed() {
            if ((mBuilderFieldsSet & 0x2) != 0) {
                throw new IllegalStateException(
                        "This Builder should not be reused. Use a new Builder instance instead");
            }
        }
    }

    @DataClass.Generated(
            time = 1659465263795L,
            codegenVersion = "1.0.23",
            sourceFile = "packages/modules/OnDevicePersonalization/framework/java/android/ondevicepersonalization/rtb/Imp.java",
            inputSignatures = "private @android.annotation.Nullable android.ondevicepersonalization.rtb.Banner mBanner\nclass Imp extends java.lang.Object implements [android.os.Parcelable]\n@com.android.ondevicepersonalization.internal.util.DataClass(genBuilder=true, genEqualsHashCode=true)")
    @Deprecated
    private void __metadata() {}


    //@formatter:on
    // End of generated code

}
