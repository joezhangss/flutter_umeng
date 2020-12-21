//
//  FlutterUmengPushHandler.m
//  flutter_umeng
//
//  Created by dengxiao on 2019/8/16.
//

#import "FlutterUmengPushHandler.h"
#import <UserNotifications/UserNotifications.h>
#import <UMPush/UMessage.h>
#import <UMCommon/UMCommon.h>
#import <UMAnalytics/MobClick.h>
#import "FlutterUmengPushHandler.h"

@interface FlutterUmengPushHandler() <UNUserNotificationCenterDelegate>

@property (nonatomic, strong) FlutterMethodChannel *channel;

@end

@implementation FlutterUmengPushHandler

- (instancetype) initWithRegistrar:(NSObject<FlutterPluginRegistrar> *)registrar  methodChannel:(FlutterMethodChannel *)flutterMethodChannel
{
    self = [super init];
    if(self)
    {
        _channel = flutterMethodChannel;
        if (@available(iOS 10.0, *)) {
            [UNUserNotificationCenter currentNotificationCenter].delegate=self;
        } else {
            // Fallback on earlier versions
        }
    }
    return self;
}

//iOS10以下使用这两个方法接收通知，
- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler
{
    [UMessage setAutoAlert:NO];
    if([[[UIDevice currentDevice] systemVersion]intValue] < 10){
        [UMessage didReceiveRemoteNotification:userInfo];
        
        //    self.userInfo = userInfo;
        //    //定制自定的的弹出框
        //    if([UIApplication sharedApplication].applicationState == UIApplicationStateActive)
        //    {
        //        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"标题"
        //                                                            message:@"Test On ApplicationStateActive"
        //                                                           delegate:self
        //                                                  cancelButtonTitle:@"确定"
        //                                                  otherButtonTitles:nil];
        //
        //        [alertView show];
        //
        //    }
        completionHandler(UIBackgroundFetchResultNewData);
    }
}


//iOS10新增：处理前台收到通知的代理方法
- (void)userNotificationCenter:(UNUserNotificationCenter *)center willPresentNotification:(UNNotification *)notification withCompletionHandler:(void (^)(UNNotificationPresentationOptions))completionHandler
{
    NSDictionary * userInfo = notification.request.content.userInfo;
    if([notification.request.trigger isKindOfClass:[UNPushNotificationTrigger class]]) {
        
        //应用处于前台时的远程推送接受
        NSLog(@"2222userInfo==%@",userInfo[@"aps"][@"alert"][@"body"]);
        //        [[NSUserDefaults standardUserDefaults] setObject:userInfo forKey:@"testBuffer"];
        NSString *resultMsg = userInfo[@"aps"][@"alert"][@"body"];
        [self callBackFlutterWithMessage: resultMsg];
        //关闭U-Push自带的弹出框
        [UMessage setAutoAlert:NO];
        //必须加这句代码
        [UMessage didReceiveRemoteNotification:userInfo];
        NSLog(@"//应用处于前台时的远程推送接受");
        //        [flutterViewController.navigationController pushViewController:flutterViewController animated:YES];
        //        [self.navigationController pushViewController:flutterViewController animated:YES];
    }else{
        //应用处于前台时的本地推送接受
        NSLog(@"//应用处于前台时的本地推送接受");
    }
    //当应用处于前台时提示设置，需要哪个可以设置哪一个
    completionHandler(UNNotificationPresentationOptionSound|UNNotificationPresentationOptionBadge|UNNotificationPresentationOptionAlert);
}

//iOS10新增：处理后台点击通知的代理方法
-(void)userNotificationCenter:(UNUserNotificationCenter *)center didReceiveNotificationResponse:(UNNotificationResponse *)response withCompletionHandler:(void (^)(void))completionHandler{
    NSDictionary * userInfo = response.notification.request.content.userInfo;
    NSLog(@"body==%@, userInfo==%@",userInfo[@"aps"][@"alert"][@"body"],userInfo);
    if([response.notification.request.trigger isKindOfClass:[UNPushNotificationTrigger class]]) {
        //应用处于后台时的远程推送接受
        
        //必须加这句代码
        [UMessage didReceiveRemoteNotification:userInfo];
        NSString *resultMsg = userInfo[@"aps"][@"alert"][@"body"];
        [self callBackFlutterWithMessage: resultMsg];
        
    }else{
        //应用处于后台时的本地推送接受
    }
}

///ios原生调flutter 回传消息
- (void)callBackFlutterWithMessage:(NSString *)msg
{
    NSLog(@"ios 这里要回传。。%@",msg);
    __weak __typeof(self) weakself = self;
    //flutter端通过通道调用原生方法时会进入以下回调
    [self.channel setMethodCallHandler:^(FlutterMethodCall * _Nonnull call, FlutterResult  _Nonnull result) {
        NSLog(@"call.method==%@",call.method);
        //call的属性method是flutter调用原生方法的方法名，我们进行字符串判断然后写入不同的逻辑
        if ([call.method isEqualToString:@"callNativeMethond"]) {
            
            //flutter传给原生的参数
            id para = call.arguments;
            
            NSLog(@"flutter传给原生的参数：%@", para);
            
            //获取一个字符串
            //            NSString *nativeFinalStr = [weakself getString];
            result([NSString stringWithFormat:@"戳中通知消息了。。。%@",msg]);
            //            if (nativeFinalStr!=nil) {
            //                NSLog(@"ios开始回传。。数据是：%@", nativeFinalStr);
            //                //把获取到的字符串传值给flutter
            //                result(nativeFinalStr);
            //            }else{
            //                NSLog(@"ios开始回传。。数据为空。。");
            //                //异常(比如改方法是调用原生的getString获取一个字符串，但是返回的是nil(空值)，这显然是不对的，就可以向flutter抛出异常 进入catch处理)
            //                result([FlutterError errorWithCode:@"001" message:[NSString stringWithFormat:@"进入异常处理"] details:@"进入flutter的trycatch方法的catch方法"]);
            //            }
        }else{
            //调用的方法原生没有对应的处理  抛出未实现的异常
            result(FlutterMethodNotImplemented);
        }
    }];
    NSLog(@"结束回调。。。。");
}


@end
