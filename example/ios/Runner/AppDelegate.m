#include "AppDelegate.h"
#include "GeneratedPluginRegistrant.h"

//遵循代理方法
@interface AppDelegate()//<FlutterStreamHandler>

@end

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application
    didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    
  [GeneratedPluginRegistrant registerWithRegistry:self];
  // Override point for customization after application launch.
  return [super application:application didFinishLaunchingWithOptions:launchOptions];
}


@end
