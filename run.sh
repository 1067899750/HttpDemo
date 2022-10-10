#!/bin/bash

#./run.sh           	fullyBuild 全量编译
#./run.sh           	buildDebugInc 增量编译debug
#./run.sh           	buildReleaseInc 增量编译 release
#./install.sh         	只安装、不编译 debug 版本
#./installReleash.sh  	只安装、不编译 release 版本
if [ ! -f "pluginCompile.properties" ]; then
	#把enablePluginCompile=false输入到文件中
	echo "enablePluginCompile=false">pluginCompile.properties
fi

gradleCmd=gradle
#command命令调用指定的指令并执行，命令执行时不查询shell函数。command命令只能够执行shell内部的命令。
command -v $gradleCmd >/dev/null 2>&1 || { gradleCmd=./gradlew; }
$gradleCmd --version

startTime=$(date +%s)
echo "startTime=$startTime"

function echoHelp(){
	echo ""
	echo "注意：脚本更新 增加多渠道flavor构建 fullyBuild增加第二参数 不带第二参数就是默认 yyb(应用宝渠道)"
   	echo "使用方式: ./run.sh"
	echo "更多gradle的标准参数请执行$gradleCmd -h查看"
   	echo ""
}


function replacePluginCompile(){
    case "$OSTYPE" in
      darwin*)  sed -i "" "s/enablePluginCompile\s*=.*/enablePluginCompile=$1/g" pluginCompile.properties ;;
      *)   sed -i "s/enablePluginCompile\s*=.*/enablePluginCompile=$1/g" pluginCompile.properties ;;
    esac
}


channel=$2
#用于多渠道打包
firstUpChannel=channel
function toFirstLetterUpper(){
	str=$1
	#截取字符串第一个字符
	firstLetter=${str:0:1}
	#从第一个参数后面的字符串
	otherLetter=${str:1}
	#tr 实现大小写转换
	firstLetter=$(echo $firstLetter | tr '[a-z]' '[A-Z]')
	firstUpChannel=$firstLetter$otherLetter
}


#判断脚本的第2个参数是否为空
if [ -z "$2" ];then
	echo "no channel param , default channel will be yyb"
	channel="yyd"
else
	echo "channel param is $2"
    echo "build target is $1"
fi

toFirstLetterUpper $channel
echo "input channel is $channel to toFirstLetterUpper is $firstUpChannel"

#构建 module
function buildPlugins() {
  for module in 'PluginQQFav'  'PluginPhotoplus'  'PluginQZone' 'PluginTroop' 'PluginTroopMemberCard'  'PluginTroopManage'  'PluginQlink' 
    do
      echo "$gradleCmd $module:assemble"
      $gradleCmd $module:assemble
      if [ $? -ne 0 ];then
        echo "$gradleCmd $module:assemble failed"
        exit 0
      fi
    done
}


#安装APP
function installApk(){
	apkPath=$1
	echo "apkPath=$apkPath"
	i=1
	while read line
	do
		echo "$line"
		if [[ ! "$line" = "" ]] && [[ `echo $line | awk '{print $2}'` = "device" ]];then
			device[i]=`echo $line | awk '{print $1}'`
			echo -e "Select number is: \033[31m $i \033[0m ${device[i]}"
			let i+=1;
		fi
	done < <(adb devices)
	
	num=0
	if [[ ${#device[@]} == 1 ]];then
		echo "only one device found"
		num=1
	elif [[ ${#device[@]} == 0 ]];then
		echo "no device found, please connect your device, and try it again"
		return
	else 
		echo -e "please select the device: (input the red color number after \033[47;30m Select number is: \033[0m)"
		read num
	fi
	
	echo "select device is "${device[$num]}
	#安装APP
	adb -s ${device[$num]} install -r -t $apkPath
	#启动Activity
	adb -s ${device[$num]} shell am start -n com.example.httpdemo/com.example.httpdemo.MainActivity
}

if [[ $isMultiChannel == "true" ]];then
    echo "isMultiChannel  = $isMultiChannel "
    channel="all"
fi


if [ "$1" == "-help" ];then
	echoHelp
	
elif [ "$1" == "fullyBuild" ];then	
	replacePluginCompile true
	echo "$gradleCmd clean"
	$gradleCmd clean
	
	if [ $? -ne 0 ];then
		echo "/gradlew clean failed"
		exit 0
	fi
	
	echo ""
	echo "fullyBuild...start"
	
	if [ "all" == "$2" ];then
		echo "assemble all channels"
		$gradleCmd assemble
	elif [ "$isMultiChannel" == "true"];then
		echo "assemble all channels"
		$gradleCmd assemble
	else
		echo "assemble$firstUpChannel"
		$gradleCmd assemble$firstUpChannel
	fi
	
	if [ $? -ne 0 ];then
		echo "$gradleCmd assemble$firstUpChannel failed"
		exit 0
	fi
	
	echo "build all application module finish"
	
elif [[ "$1" == "minBuild" ]];then
	replacePluginCompile true
	echo "build application module app"
	$gradleCmd app:clean
	#用于多渠道打包
	#$gradleCmd app:assemble$firstUpChannel
	$gradleCmd app:assemble
	echo "build application module app finish"
	
elif [[ "$1" == "incBuild" ]];then
	replacePluginCompile true
	echo "build application module app without clean"
	#用于多渠道打包
	#$gradleCmd app:assemble$firstUpChannel
	$gradleCmd app:assemble
    echo "build application module app finish"
	
elif [ "$1" == "buildDebugInc" ];then
	replacePluginCompile false
	echo "buildDebugInc"
	#用于多渠道打包
	#taskName="app:assemble${firstUpChannel}Debug"
	taskName="app:assembleDebug"
	echo "execte task $taskName"
	#执行gradel 进行编译
	$gradleCmd $taskName
	#用于多渠道打包安装
	#installApk AAProject/QQLite/build/outputs/apk/$channel/debug/QQLite-$channel-debug.apk
	installApk app/build/outputs/apk/debug/app-debug.apk
elif [[ "$1" == "buildReleaseInc" ]]
	then
	replacePluginCompile true	
	echo "buildReleaseInc"
	#taskName="app:assemble${firstUpChannel}Release"
	taskName="app:assembleRelease"
	echo "execute task "$taskName
	
	$gradleCmd $taskName
	installApk app/build/outputs/apk/release/app-release-unsigned.apk
	
else
	echoHelp
fi

replacePluginCompile false
endtime=$(date +%s)
echo "$1 spent time "$((endtime-startTime))"s"




