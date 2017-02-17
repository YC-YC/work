# Copyright 2007-2008 The Android Open Source Project

LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := eng
LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_PACKAGE_NAME := UpdateVersion
LOCAL_STATIC_JAVA_LIBRARIES += android-common
LOCAL_JNI_SHARED_LIBRARIES := libupdateversion
include $(BUILD_PACKAGE)
include $(LOCAL_PATH)/jni/Android.mk
