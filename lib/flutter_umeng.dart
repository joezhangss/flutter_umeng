import 'dart:async';

import 'package:flutter/services.dart';

typedef OnPushClick = void Function(String name,dynamic arguments);

class FlutterUmeng {
  static const _AID = "AID";
  static const _ATYPE = "AType";

  static const MethodChannel _channel = const MethodChannel('flutter_umeng');

  //设置友盟 标签
  static Future<bool> setUMTags(List<String> tags) async {
     bool b=await _channel.invokeMethod("setUMTags", tags);
     print("返回数据：$b");
     return b;
  }

  //删除友盟标签
  static Future<void> deleteUMTags(List<String> tags) async {
    await _channel.invokeMethod("deleteUMTags", tags);
  }

  //增加友盟别名
  static Future<void> addAlias(String aId, String aType) async {
    await _channel.invokeMethod("addAlias", {_AID: aId, _ATYPE: aType});
  }

  //重置友盟别名
  static Future<void> setAlias(String aId, String aType) async {
    await _channel.invokeMethod("setAlias", {_AID: aId, _ATYPE: aType});
  }

  //删除友盟 别名
  static Future<void> deleteAlias(String aId, String aType) async {
    await _channel.invokeMethod("deleteAlias", {_AID: aId, _ATYPE: aType});
  }


  //友盟统计
  //初始化友盟统计
  static Future<void> initAnalytics(String key){
    Map<String, dynamic> args = {"key": key};
    print("初始化统计：${args}");
    _channel.invokeMethod("initAnalytics", args);
    return new Future.value(true);
  }

//  //统计开始
//  static Future<void> beginLogPageView(String pageName){
//    _channel.invokeMethod("beginPageView", {"name":pageName});
//  }
//
//  //结束统计
//  static Future<void> endLogPageView(String pageName){
//    _channel.invokeMethod("endPageView", {"name": pageName});
//  }

  //用于ios删除通知缓存，解决再次进入时重复获取推送消息
  static Future<void> removeBuffer() async{
    _channel.invokeMethod("removeBuffer");
  }

  //通知原生 Flutter插件加载完成
//  ValueChanged clickCallBack;
  static Future<void> ready() async{
    _channel.invokeMethod("ready");
  }

  static void onPushClick(OnPushClick onPushClick){
    _channel.setMethodCallHandler((handler) async {
      print("收到的数据："+handler.arguments);
      if (handler.method == "onPushClick") {
        print("收到的数据："+handler.arguments.toString());
        onPushClick(handler.method,handler.arguments);
      }

    });



  }



  
}

