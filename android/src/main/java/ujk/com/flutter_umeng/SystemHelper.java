package ujk.com.flutter_umeng;

/**
 * 文件名 SystemHelper
 * 创建者  CT
 * 时 间  2019/8/21 13:31
 * TODO
 */

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.taobao.accs.ACCSClient;
import com.taobao.accs.AccsClientConfig;
import com.taobao.agoo.TaobaoRegister;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.commonsdk.utils.UMUtils;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.api.UPushRegisterCallback;
import com.umeng.message.entity.UMessage;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.mezu.MeizuRegister;
import org.android.agoo.oppo.OppoRegister;
import org.android.agoo.vivo.VivoRegister;
import org.android.agoo.xiaomi.MiPushRegistar;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import io.flutter.Log;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * 系统帮助类
 */
public class SystemHelper {

    private static final String TAG = SystemHelper.class.getSimpleName();

    /**
     * 判断本地是否已经安装好了指定的应用程序包
     *
     * @param packageNameTarget ：待判断的 App 包名，如 微博 com.sina.weibo
     * @return 已安装时返回 true,不存在时返回 false
     */
    public static boolean appIsExist(Context context, String packageNameTarget) {
        if (!"".equals(packageNameTarget.trim())) {
            PackageManager packageManager = context.getPackageManager();
            List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES);
            for (PackageInfo packageInfo : packageInfoList) {
                String packageNameSource = packageInfo.packageName;
                if (packageNameSource.equals(packageNameTarget)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 将本应用置顶到最前端
     * 当本应用位于后台时，则将它切换到最前端
     *
     * @param context
     */
    public static void setTopApp(Context context) {
        if (!isRunningForeground(context)) {
            /**获取ActivityManager*/
            ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);

            /**获得当前运行的task(任务)*/
            List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(100);
            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                /**找到本应用的 task，并将它切换到前台*/
                if (taskInfo.topActivity.getPackageName().equals(context.getPackageName())) {
                    activityManager.moveTaskToFront(taskInfo.id, 0);
                    break;
                }
            }
        }
    }

    /**
     * 判断本应用是否已经位于最前端
     *
     * @param context
     * @return 本应用已经位于最前端时，返回 true；否则返回 false
     */
    public static boolean isRunningForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();
        /**枚举进程*/
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfoList) {
            if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (appProcessInfo.processName.equals(context.getApplicationInfo().processName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 预初始化，已添加子进程中初始化sdk。
     * 使用场景：用户未同意隐私政策协议授权时，延迟初始化
     *
     * @param context 应用上下文
     */
    public static void preInit(Context context, String uAppKey, String uMsgSecret, String uChannel) {
        try {
            android.util.Log.e(TAG, "预注册友盟的方法：preInit>>>>>"  );
            //解决推送消息显示乱码的问题
            AccsClientConfig.Builder builder = new AccsClientConfig.Builder();
            builder.setAppKey("umeng:" + uAppKey);
            builder.setAppSecret(uMsgSecret);
            builder.setTag(AccsClientConfig.DEFAULT_CONFIGTAG);
            ACCSClient.init(context, builder.build());
            TaobaoRegister.setAccsConfigTag(context, AccsClientConfig.DEFAULT_CONFIGTAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
        UMConfigure.preInit(context, uAppKey, uChannel);
        if (!isMainProcess(context)) {
//            init(context);
        }
    }

    /**
     * 初始化。
     * 场景：用户已同意隐私政策协议授权时
     *
     * @param context 应用上下文
     */
    public static void init(Context context, String uAppKey, String uChannel, String uPushSecret) {
        try {

            android.util.Log.e(TAG, "》》》》》》》到友盟注册页面。init方法"  );
            UMConfigure.init(context, uAppKey, uChannel, UMConfigure.DEVICE_TYPE_PHONE, uPushSecret);
            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);

            //绑定华为离线通道
//            HuaWeiRegister.register((Application) context.getApplicationContext());

            /*
            //绑定小米通道
            String xId = applicationInfo.metaData.getString("XIAO_ID");
            String xKey = applicationInfo.metaData.getString("XIAO_KEY");
            MiPushRegistar.register(this, xId, xKey);

            //绑定魅族通道
            String mId = applicationInfo.metaData.getString("MZ_ID");
            String mKey = applicationInfo.metaData.getString("MZ_KEY");
            MeizuRegister.register(this, mId, mKey);*/
            //绑定小米通道
//            String xId = context.getApplicationInfo().metaData.getString("MI_ID");
//            String xKey = context.getApplicationInfo().metaData.getString("MI_KEY");
//
//
//            if(!TextUtils.isEmpty(xId) && xId.startsWith("appid") && !TextUtils.isEmpty(xKey) && xKey.startsWith("appkey")){
//
//                xId = xId.split("=")[1];
//                xKey= xKey.split("=")[1];
//                Log.e("TAG","XID:"+xId+"---XKEY:"+xKey);
//                MiPushRegistar.register((Application) context.getApplicationContext(), xId, xKey);
//            }



            //绑定魅族通道
//            String mId = context.getApplicationInfo().metaData.getString("MZ_ID");
//            String mKey = context.getApplicationInfo().metaData.getString("MZ_KEY");
//            if(!TextUtils.isEmpty(mId) && mId.startsWith("appid") && !TextUtils.isEmpty(mKey) && mKey.startsWith("appkey")){
//                mId = mId.split("=")[1];
//                mKey= mKey.split("=")[1];
//                Log.e("TAG","XID:"+xId+"---XKEY:"+xKey);
//                MeizuRegister.register((Application) context.getApplicationContext(), mId, mKey);
//            }
            SystemHelper.registerDeviceChannel(context);
            //获取消息推送代理示例
            PushAgent mPushAgent = PushAgent.getInstance(context);
            mPushAgent.register(new UPushRegisterCallback() {
                @Override
                public void onSuccess(String s) {
                    Log.e(TAG, "友盟注册成功:" + s);
                }

                @Override
                public void onFailure(String s, String s1) {
                    Log.e(TAG, "友盟注册失败:" + s + "------" + s1);
                }
            });


            //自定义通知点击事件
            mPushAgent.setNotificationClickHandler(new UmengNotificationClickHandler() {
                @Override
                public void dealWithCustomAction(Context context, UMessage uMessage) {
                    Log.e("TAG", "自定义事件 点击事件");
                    if(!SystemHelper.isRunningForeground(context.getApplicationContext()))
                        SystemHelper.setTopApp(context.getApplicationContext());

                    Map<String,String> map = uMessage.extra;
                    map.put("Action",uMessage.custom);

                    FlutterUmengPlugin.channel.invokeMethod("onPushClick", new JSONObject(map).toString());
                }
            });

            Log.e("TAG","当前包名："+context.getPackageName());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册设备推送通道（小米、华为等设备的推送）
     *
     * @param context 应用上下文
     */
    private static void registerDeviceChannel(Context context) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            //小米通道，填写您在小米后台APP对应的xiaomi id和key
            Log.e("TAG","getApplicationInfo："+applicationInfo);
            Log.e("TAG","getApplicationInfo.metaData："+applicationInfo.metaData);
            String xId = applicationInfo.metaData.getString("MI_ID");
            String xKey = applicationInfo.metaData.getString("MI_KEY");
            if(!TextUtils.isEmpty(xId) && xId.startsWith("appid") && !TextUtils.isEmpty(xKey) && xKey.startsWith("appkey") && !xId.equals("你的Id")){

                xId = xId.split("=")[1];
                xKey= xKey.split("=")[1];
                Log.e("TAG","XID:"+xId+"---XKEY:"+xKey);
                MiPushRegistar.register((Application) context.getApplicationContext(), xId, xKey);
            }

            //华为，注意华为通道的初始化参数在minifest中配置
            HuaWeiRegister.register((Application) context.getApplicationContext());

            //魅族，填写您在魅族后台APP对应的app id和key
            String mId = applicationInfo.metaData.getString("MZ_ID");
            String mKey = applicationInfo.metaData.getString("MZ_KEY");
            if(!TextUtils.isEmpty(mId) && mId.startsWith("appid") && !TextUtils.isEmpty(mKey) && mKey.startsWith("appkey") && !mId.equals("你的Id")){
                mId = mId.split("=")[1];
                mKey= mKey.split("=")[1];
                Log.e("TAG","mId:"+mId+"---mKey:"+mKey);
                MeizuRegister.register((Application) context.getApplicationContext(), mId, mKey);
            }

            //OPPO，填写您在OPPO后台APP对应的app key和secret
//        OppoRegister.register(context, PushConstants.OPPO_KEY, PushConstants.OPPO_SECRET);
            String oId = applicationInfo.metaData.getString("OP_ID");
            String oKey = applicationInfo.metaData.getString("OP_KEY");
            if(!TextUtils.isEmpty(oId) && oId.startsWith("appid") && !TextUtils.isEmpty(oKey) && oKey.startsWith("appkey") && !oId.equals("你的Id")){

                oId = oId.split("=")[1];
                oKey= oKey.split("=")[1];
                Log.e("TAG","oId:"+oId+"---oKey:"+oKey);
                OppoRegister.register((Application) context.getApplicationContext(), oId, oKey);
            }

            //vivo，注意vivo通道的初始化参数在minifest中配置
            VivoRegister.register(context);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否运行在主进程
     *
     * @param context 应用上下文
     * @return true: 主进程；false: 子进程
     */
    public static boolean isMainProcess(Context context) {
        return UMUtils.isMainProgress(context);
    }
}