# Copyright 2006 The Android Open Source Project

TARGET_BUILD=jni

LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_LDLIBS += -llog

LOCAL_SRC_FILES:= \
        update_version.cpp

LOCAL_SHARED_LIBRARIES := \
	libcutils \
 
ifeq ($(TARGET_ARCH),arm)
LOCAL_SHARED_LIBRARIES += libdl
endif # arm

ifeq ($(TARGET_BUILD),exe)
LOCAL_MODULE:= updateversion
LOCAL_MODULE_TAGS := eng
include $(BUILD_EXECUTABLE)
endif # exe

ifeq ($(TARGET_BUILD),jni)
LOCAL_C_INCLUDES += $(JNI_H_INCLUDE)
LOCAL_CFLAGS := -DJAVA_JNI
LOCAL_MODULE:= libupdateversion
LOCAL_MODULE_TAGS := eng
LOCAL_PRELINK_MODULE := false
include $(BUILD_SHARED_LIBRARY)   #for so file
endif # exe

