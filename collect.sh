#!/bin/bash
#创建文件夹
mkdir "bin"
#删除bin文件夹里面的txt文件
rm -rf ./bin/*.txt

for module in 'app' 'Httplibrary' ;
do
    #把文件的内容输出到bin/mapping.txt文件
    echo `cat $module/build/outputs/mapping/Release/mapping.txt` >> bin/mapping.txt
    echo `cat $module/build/outputs/mapping/Release/seeds.txt`  >> bin/seeds.txt
    echo `cat $module/build/outputs/mapping/Release/usage.txt`  >> bin/usage.txt
done

#考备文件
cp libs_symbols/libflutter.so bin/

#查找并拷贝到bin文件中
#-exec是将结果逐条传递给后面的命令，后面的命令逐条执行。
find ./app/build/outputs/apk/ -name *.apk -exec cp {} ./bin \;

#如果var为空或者未设定，返回word，var不变
isAARUsed=${isAARUsed:-true}

if [ $isAARUsed = false ]; then
    cp src/main/jniLibs/libSDK.so bin/libSDK-debug.so
fi






