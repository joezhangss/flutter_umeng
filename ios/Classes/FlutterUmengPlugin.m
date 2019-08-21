#import "FlutterUmengPlugin.h"
#import <UserNotifications/UserNotifications.h>
#import <UMPush/UMessage.h>
#import <UMCommon/UMCommon.h>
#import <UMAnalytics/MobClick.h>

@interface FlutterUmengPlugin()<UNUserNotificationCenterDelegate>//FlutterStreamHandler

@property (nonatomic, strong) FlutterMethodChannel *pushChannel;
@end

@implementation FlutterUmengPlugin

+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar
{
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"flutter_umeng"
            binaryMessenger:[registrar messenger]];
    
  FlutterUmengPlugin* instance = [[FlutterUmengPlugin alloc] initWithRegistrar:registrar methodChannel:channel];
//    FlutterUmengPlugin* instance = [[FlutterUmengPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
  [registrar addApplicationDelegate:instance];
  
}

- (instancetype)initWithRegistrar: (NSObject<FlutterPluginRegistrar> *) registrar methodChannel:(FlutterMethodChannel *) flutterMethodChannel
{
    self = [super init];
    self.pushChannel = flutterMethodChannel;
    return self;
}


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{

    // Push功能配置
    UMessageRegisterEntity * entity = [[UMessageRegisterEntity alloc] init];
    entity.types = UMessageAuthorizationOptionBadge|UMessageAuthorizationOptionAlert|UMessageAuthorizationOptionSound;
    //如果你期望使用交互式(只有iOS 8.0及以上有)的通知，请参考下面注释部分的初始化代码
    if (([[[UIDevice currentDevice] systemVersion]intValue] >= 8) && ([[[UIDevice currentDevice] systemVersion]intValue] < 10)) {
        UIMutableUserNotificationAction *action1 = [[UIMutableUserNotificationAction alloc] init];
        action1.identifier = @"action1_identifier";
        action1.title=@"打开应用";
        action1.activationMode = UIUserNotificationActivationModeForeground;//当点击的时候启动程序
        
        UIMutableUserNotificationAction *action2 = [[UIMutableUserNotificationAction alloc] init];  //第二按钮
        action2.identifier = @"action2_identifier";
        action2.title=@"忽略";
        action2.activationMode = UIUserNotificationActivationModeBackground;//当点击的时候不启动程序，在后台处理
        action2.authenticationRequired = YES;//需要解锁才能处理，如果action.activationMode = UIUserNotificationActivationModeForeground;则这个属性被忽略；
        action2.destructive = YES;
        UIMutableUserNotificationCategory *actionCategory1 = [[UIMutableUserNotificationCategory alloc] init];
        actionCategory1.identifier = @"category1";//这组动作的唯一标示
        [actionCategory1 setActions:@[action1,action2] forContext:(UIUserNotificationActionContextDefault)];
        NSSet *categories = [NSSet setWithObjects:actionCategory1, nil];
        entity.categories=categories;
    }
    //如果要在iOS10显示交互式的通知，必须注意实现以下代码
    if (@available(iOS 10.0, *)) {// [[[UIDevice currentDevice] systemVersion]intValue]>=10
        UNNotificationAction *action1_ios10 = [UNNotificationAction actionWithIdentifier:@"action1_identifier" title:@"打开应用" options:UNNotificationActionOptionForeground];
        UNNotificationAction *action2_ios10 = [UNNotificationAction actionWithIdentifier:@"action2_identifier" title:@"忽略" options:UNNotificationActionOptionForeground];
        
        //UNNotificationCategoryOptionNone
        //UNNotificationCategoryOptionCustomDismissAction  清除通知被触发会走通知的代理方法
        //UNNotificationCategoryOptionAllowInCarPlay       适用于行车模式
        UNNotificationCategory *category1_ios10 = [UNNotificationCategory categoryWithIdentifier:@"category1" actions:@[action1_ios10,action2_ios10]   intentIdentifiers:@[] options:UNNotificationCategoryOptionCustomDismissAction];
        NSSet *categories = [NSSet setWithObjects:category1_ios10, nil];
        entity.categories=categories;
        [UNUserNotificationCenter currentNotificationCenter].delegate=self;
    }
    
    [UMessage registerForRemoteNotificationsWithLaunchOptions:launchOptions Entity:entity completionHandler:^(BOOL granted, NSError * _Nullable error) {
        if (granted) {
        }else{
        }
    }];
    
    return YES;
}


- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {

    if([@"initAnalytics" isEqualToString:call.method])
    {
        //初始化友盟统计
        [self init:call result:result];
    }
//    else if ([@"beginPageView" isEqualToString:call.method])
//    {
//        [MobClick beginLogPageView:call.arguments[@"name"]];
//        result(nil);
//    }
//    else if ([@"endPageView" isEqualToString:call.method])
//    {
//        [MobClick endLogPageView:call.arguments[@"name"]];
//        result(nil);
//    }
    else if([@"removeBuffer" isEqualToString:call.method])
    {
        //清除因点击通知而保存的缓存信息
        [[NSUserDefaults standardUserDefaults] removeObjectForKey:@"receiveBuffer"];
    }
    else if([@"ready" isEqualToString:call.method])
    {
        //当离线推送启动app时，ios回传通知信息给flutter，但flutter组件还没初始化完成。所以需要在这里回传消息
        NSString *val = [[NSUserDefaults standardUserDefaults] objectForKey:@"receiveBuffer"];
        
        if(![self isNullObject:val]){
            [self.pushChannel invokeMethod:@"onPushClick" arguments:val];
        }
        
    }

    /************ 别名处理 start **************/
    else if([@"addAlias" isEqualToString:call.method])
    {
        [self addAliasWithPhone:call.arguments[@"AID"] andType:call.arguments[@"AType"]];
    }
    else if([@"setAlias" isEqualToString:call.method])
    {
        [self setAliasWithPhone:call.arguments[@"AID"] andType:call.arguments[@"AType"]];
    }
    else if([@"deleteAlias" isEqualToString:call.method])
    {
        [self deleteAliasWithPhone:call.arguments[@"AID"] andType:call.arguments[@"AType"]];
    }
    /************ 别名处理 end **************/

    /************ 标签处理 start **************/
    else if([@"setUMTags" isEqualToString:call.method])
    {
        [self addTagWithTagName: call.arguments];
    }
    else if([@"deleteUMTags" isEqualToString:call.method])
    {
        [self deleteTagWithTagName:call.arguments];
    }
    /************ 标签处理 end **************/
    else {
        result(FlutterMethodNotImplemented);
    }
}

- (BOOL) isNullObject:(id)object{
    if (object == nil || [object isEqual:[NSNull class]]) {
       return YES;
        
    }else if ([object isKindOfClass:[NSNull class]]){
      if ([object isEqualToString:@""]) {
          return YES;
            
        }else{
            return NO;
            
        }
        
    }else if ([object isKindOfClass:[NSNumber class]]){
        if ([object isEqualToNumber:@0]) {
            return YES;
        }else
        {
            return NO;
        }
        
    }
    return NO;
    
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
//        NSLog(@"2222userInfo==%@",userInfo[@"aps"][@"alert"][@"body"]);
        
        //关闭U-Push自带的弹出框
        [UMessage setAutoAlert:NO];
        //必须加这句代码
        [UMessage didReceiveRemoteNotification:userInfo];
//        NSLog(@"//应用处于前台时的远程推送接受");
    }else{
        //应用处于前台时的本地推送接受
//        NSLog(@"//应用处于前台时的本地推送接受");
    }
    //当应用处于前台时提示设置，需要哪个可以设置哪一个
    completionHandler(UNNotificationPresentationOptionSound|UNNotificationPresentationOptionBadge|UNNotificationPresentationOptionAlert);
}

//iOS10新增：处理后台点击通知的代理方法
-(void)userNotificationCenter:(UNUserNotificationCenter *)center didReceiveNotificationResponse:(UNNotificationResponse *)response withCompletionHandler:(void (^)(void))completionHandler{
    NSDictionary * userInfo = response.notification.request.content.userInfo;
    
    if([response.notification.request.trigger isKindOfClass:[UNPushNotificationTrigger class]]) {
        //应用处于后台时的远程推送接受
        NSLog(@"userInfo==%@",userInfo);
        //必须加这句代码
        [UMessage didReceiveRemoteNotification:userInfo];
        
//        NSString *resultMsg = [NSString stringWithFormat:@"%@,%@",userInfo[@"STORE_ID"],userInfo[@"STORE_NAME"]];//userInfo[@"aps"][@"alert"][@"body"];
//        [self callBackFlutterWithMessage: resultMsg];
//        _eventSink(userInfo[@"aps"][@"alert"][@"body"]);
        NSString *resultMsg = [self convertJSONWithDic:userInfo];
        NSLog(@"resultMsg==%@",resultMsg);
        [self.pushChannel invokeMethod:@"onPushClick" arguments:resultMsg];//回传消息
        [[NSUserDefaults standardUserDefaults] setObject:resultMsg forKey:@"receiveBuffer"];


    }else{
        //应用处于后台时的本地推送接受
    }
}

// 字典转json字符串方法
- (NSString *)convertJSONWithDic:(NSDictionary *)dic {
      NSError *err;
      NSData *data = [NSJSONSerialization dataWithJSONObject:dic options:NSJSONWritingPrettyPrinted error:&err];
      if (err) {
          return @"字典转JSON出错";
      }
      return [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
  }



-(NSString *)convertToJsonData:(NSDictionary *)dict

{

    NSError *error;

    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dict options:NSJSONWritingPrettyPrinted error:&error];

    NSString *jsonString;

    if (!jsonData) {

        NSLog(@"%@",error);

    }else{

        jsonString = [[NSString alloc]initWithData:jsonData encoding:NSUTF8StringEncoding];

    }

    NSMutableString *mutStr = [NSMutableString stringWithString:jsonString];

    NSRange range = {0,jsonString.length};

    //去掉字符串中的空格

    [mutStr replaceOccurrencesOfString:@" " withString:@"" options:NSLiteralSearch range:range];

    NSRange range2 = {0,mutStr.length};

    //去掉字符串中的换行符

    [mutStr replaceOccurrencesOfString:@"\n" withString:@"" options:NSLiteralSearch range:range2];

    return mutStr;

}

//添加别名
- (void)addAliasWithPhone:(NSString *)phone andType:(NSString *)aliasType
{
    [UMessage addAlias:phone type:aliasType response:^(id  _Nullable responseObject, NSError * _Nullable error) {
        NSLog(@"responseObject==%@",responseObject);
        NSLog(@"添加别名error==%@",error);
    }];
}

//重置别名
- (void)setAliasWithPhone:(NSString *)phone andType:(NSString *)aliasType
{
    [UMessage setAlias:phone type:aliasType response:^(id  _Nonnull responseObject, NSError * _Nonnull error) {
    }];
}

//删除别名
- (void)deleteAliasWithPhone:(NSString *)phone andType:(NSString *)aliasType
{
    [UMessage removeAlias:phone type:aliasType response:^(id  _Nonnull responseObject, NSError * _Nonnull error) {
    }];
}



- (void)addTagWithTagName:(NSArray *)tagName
{

    [UMessage getTags:^(NSSet * _Nonnull responseTags, NSInteger remain, NSError * _Nullable error) {
        NSLog(@"获取的标签：responseTags==%@, remain==%ld",responseTags,remain);

    }];
        //添加加权标签
    for(int i =0;i<tagName.count;i++){
           NSLog(@"tagName[i]==%@",tagName[i]);
        [UMessage addTags:tagName[i] response:^(id  _Nullable responseObject, NSInteger remain, NSError * _Nullable error) {
            NSLog(@"添加tag responseObject==%@",responseObject);
            NSLog(@"添加tag remain==%ld",remain);
            NSLog(@"添加标签error==%@",error);
        }];
    }
    
}

- (void)deleteTagWithTagName: (NSArray *)tagName
{
    for(int i =0;i<tagName.count;i++){
        [UMessage deleteTags:tagName[i] response:^(id  _Nullable responseObject, NSInteger remain, NSError * _Nullable error) {
            
        }];
    }
}

-(void)getAllWeightedTag
{
    [UMessage getWeightedTags:^(NSDictionary * _Nonnull responseWeightedTags, NSInteger remain, NSError * _Nonnull error) {
//        NSLog(@"获取所有的标签 remain==%ld",remain);
        
    }];
}


//友盟统计
- (void)init:(FlutterMethodCall*)call result:(FlutterResult)result {//

    [UMConfigure initWithAppkey:[NSString stringWithFormat:@"%@",call.arguments[@"key"]] channel:@"App Store"];//[NSString stringWithFormat:@"%@",call.arguments[@"key"]]
    [MobClick setAutoPageEnabled:YES];
//    NSLog(@"传入的key==%@, self.tmpLaunchOptions",call.arguments[@"key"],self.tmpLaunchOptions);
    /*
//    [MobClick setScenarioType:E_UM_NORMAL];
//    [UMConfigure setEncryptEnabled:YES];
    [UMConfigure setLogEnabled:YES];*/
    
}




@end
