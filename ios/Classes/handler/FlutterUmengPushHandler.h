//
//  FlutterUmengPushHandler.h
//  flutter_umeng
//
//  Created by dengxiao on 2019/8/16.
//

#import <Foundation/Foundation.h>
#import <Flutter/Flutter.h>


NS_ASSUME_NONNULL_BEGIN

@interface FlutterUmengPushHandler : NSObject

-(instancetype) initWithRegistrar:(NSObject<FlutterPluginRegistrar> *)registrar  methodChannel:(FlutterMethodChannel *)flutterMethodChannel;

@end

NS_ASSUME_NONNULL_END
