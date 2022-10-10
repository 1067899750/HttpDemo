#!/bin/bash

adb devices | while read line
do
    if [ ! "$line" = "" ] && [ `echo $line | awk '{print $2}'` = "device" ]; then
        device=`echo $line | awk '{print $1}'`
        echo "adb -s $device"
       	#安装APP
	      adb -s $device install -r -t app/build/outputs/apk/debug/app-debug.apk
	      #启动Activity
	      adb -s $device shell am start -n com.example.httpdemo/com.example.httpdemo.MainActivity
    fi
done











