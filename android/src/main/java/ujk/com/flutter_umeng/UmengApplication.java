<<<<<<< HEAD
package ujk.com.flutter_umeng;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.mezu.MeizuRegister;
import org.android.agoo.xiaomi.MiPushRegistar;
import org.json.JSONObject;

import java.util.Map;

import io.flutter.Log;
import io.flutter.app.FlutterApplication;


public class UmengApplication extends FlutterApplication {

    private final String TAG = "Flutter_Umeng";


    static String offLineBody = "";

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);

            String uAppKey = applicationInfo.metaData.getString("UMENG_APPKEY");
            String uPushSecret = applicationInfo.metaData.getString("UMENG_PUSHSECRET");
            String uChannel = applicationInfo.metaData.getString("UMENG_CHANNEL");

            if (TextUtils.isEmpty(uChannel))
                uChannel = "Umeng";

//               UMConfigure.init(this, "5d369f224ca3573181000889", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "accaeef49c3f67c98ccf1b1426218c2d");
            UMConfigure.init(this, uAppKey, uChannel, UMConfigure.DEVICE_TYPE_PHONE, uPushSecret);
            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);

            //绑定华为离线通道
            HuaWeiRegister.register(this);

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
            String xId = applicationInfo.metaData.getString("MI_ID");
            String xKey = applicationInfo.metaData.getString("MI_KEY");


            if(!TextUtils.isEmpty(xId) && xId.startsWith("appid") && !TextUtils.isEmpty(xKey) && xKey.startsWith("appkey")){

                xId = xId.split("=")[1];
                xKey= xKey.split("=")[1];
                Log.e("TAG","XID:"+xId+"---XKEY:"+xKey);
                MiPushRegistar.register(this, xId, xKey);
            }



            //绑定魅族通道
            String mId = applicationInfo.metaData.getString("MZ_ID");
            String mKey = applicationInfo.metaData.getString("MZ_KEY");
            if(!TextUtils.isEmpty(mId) && mId.startsWith("appid") && !TextUtils.isEmpty(mKey) && mKey.startsWith("appkey")){
                mId = mId.split("=")[1];
                mKey= mKey.split("=")[1];
                Log.e("TAG","XID:"+xId+"---XKEY:"+xKey);
                MeizuRegister.register(this, mId, mKey);
            }

            //获取消息推送代理示例
            PushAgent mPushAgent = PushAgent.getInstance(this);
            HuaWeiRegister.register(this);
            mPushAgent.register(new IUmengRegisterCallback() {
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
                    if(!SystemHelper.isRunningForeground(getApplicationContext()))
                        SystemHelper.setTopApp(getApplicationContext());

                    Map<String,String> map = uMessage.extra;
                    map.put("Action",uMessage.custom);

                    FlutterUmengPlugin.channel.invokeMethod("onPushClick", new JSONObject(map).toString());
                }
            });



            Log.e("TAG","当前包名："+getPackageName());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
=======
package ujk.com.flutter_umeng;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.mezu.MeizuRegister;
import org.android.agoo.xiaomi.MiPushRegistar;
import org.json.JSONObject;

import java.util.Map;

import io.flutter.Log;
import io.flutter.app.FlutterApplication;


public class UmengApplication extends FlutterApplication {

    private final String TAG = "Flutter_Umeng";


    static String offLineBody = "";

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);

            String uAppKey = applicationInfo.metaData.getString("UMENG_APPKEY");
            String uPushSecret = applicationInfo.metaData.getString("UMENG_PUSHSECRET");
            String uChannel = applicationInfo.metaData.getString("UMENG_CHANNEL");

            if (TextUtils.isEmpty(uChannel))
                uChannel = "Umeng";

//               UMConfigure.init(this, "5d369f224ca3573181000889", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "accaeef49c3f67c98ccf1b1426218c2d");
            UMConfigure.init(this, uAppKey, uChannel, UMConfigure.DEVICE_TYPE_PHONE, uPushSecret);
            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);

            //绑定华为离线通道
            HuaWeiRegister.register(this);

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
            String xId = applicationInfo.metaData.getString("MI_ID");
            String xKey = applicationInfo.metaData.getString("MI_KEY");


            if(!TextUtils.isEmpty(xId) && xId.startsWith("appid") && !TextUtils.isEmpty(xKey) && xKey.startsWith("appkey")){

                xId = xId.split("=")[1];
                xKey= xKey.split("=")[1];
                Log.e("TAG","XID:"+xId+"---XKEY:"+xKey);
                MiPushRegistar.register(this, xId, xKey);
            }



            //绑定魅族通道
            String mId = applicationInfo.metaData.getString("MZ_ID");
            String mKey = applicationInfo.metaData.getString("MZ_KEY");
            if(!TextUtils.isEmpty(mId) && mId.startsWith("appid") && !TextUtils.isEmpty(mKey) && mKey.startsWith("appkey")){
                mId = mId.split("=")[1];
                mKey= mKey.split("=")[1];
                Log.e("TAG","XID:"+xId+"---XKEY:"+xKey);
                MeizuRegister.register(this, mId, mKey);
            }

            //获取消息推送代理示例
            PushAgent mPushAgent = PushAgent.getInstance(this);
            HuaWeiRegister.register(this);
            mPushAgent.register(new IUmengRegisterCallback() {
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
                    if(!SystemHelper.isRunningForeground(getApplicationContext()))
                        SystemHelper.setTopApp(getApplicationContext());

                    Map<String,String> map = uMessage.extra;
                    map.put("Action",uMessage.custom);

                    FlutterUmengPlugin.channel.invokeMethod("onPushClick", new JSONObject(map).toString());
                }
            });



            Log.e("TAG","当前包名："+getPackageName());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
>>>>>>> db90b428db3f6bcbf68d955c93211bfcacbf99fa
