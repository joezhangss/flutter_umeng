package ujk.com.flutter_umeng;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.common.inter.ITagManager;
import com.umeng.message.tag.TagManager;

import java.util.List;

//import io.flutter.Log;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;

/**
 * FlutterUmengPlugin
 */
public class FlutterUmengPlugin implements MethodCallHandler, FlutterPlugin, ActivityAware {
    /**
     * Plugin registration.
     */
//    private static Registrar mRegistrar;
     static MethodChannel channel;
     static MethodChannel channelTags;//获取tags的通道
    private final String TAG = "FlutterUmengPlugin";
    private Context applicationContext;
    private Activity activity;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Log.e("TAG", "数据回调" + Thread.currentThread().getName());
                    String result = null;
                    if(msg.obj != null){
                        result = msg.obj.toString();
                    }else{
                        result = "";
                    }
//                    channelTags.invokeMethod("onTag", msg.obj.toString());
                    channelTags.invokeMethod("onTag", result);
//                   channel.invokeMethod("onTag", "111");
                    break;
            }

        }
    };

    public static void registerWith(Registrar registrar) {
//        mRegistrar = registrar;
        channel = new MethodChannel(registrar.messenger(), "flutter_umeng");
        channel.setMethodCallHandler(new FlutterUmengPlugin());

        channelTags = new MethodChannel(registrar.messenger(), "flutter_umeng_tags");
        channelTags.setMethodCallHandler(new FlutterUmengPlugin());
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
//        PushAgent mPushAgent = PushAgent.getInstance(mRegistrar.context());
        PushAgent mPushAgent = PushAgent.getInstance(activity.getBaseContext());
        switch (call.method) {
            case "setUMTags":
                setUMTags(mPushAgent, call, result);
//channel.invokeMethod("test","test");
                result.success(true);
                break;
            case "deleteUMTags":
                deleteUMTags(mPushAgent, call, result);
                break;
            case "addAlias":
                addAlias(mPushAgent, call, result);
                break;
            case "setAlias":
                setAlias(mPushAgent, call, result);
                break;
            case "deleteAlias":
                deleteAlias(mPushAgent, call, result);
                break;
            case "ready":
                offLine();
                break;
            case "getAllTags":
                getAllTags(mPushAgent);

                break;

            case "registerUmengPush":
                //注册友盟
//                Application currentApp = (Application)mRegistrar.context().getApplicationContext();
                Application currentApp = (Application)applicationContext;
                String uAppKey = call.argument("uAppKey");
                String uChannel = call.argument("uChannel");
                String uPushSecret = call.argument("uPushSecret");
                Log.e(TAG, "》》》准备注册友盟:application=" + currentApp );
                SystemHelper.init(currentApp, uAppKey, uChannel, uPushSecret);
                break;
            default:
                result.notImplemented();
                break;

        }


    }

    //添加标签 用于给一类用户推送
    private void setUMTags(PushAgent mPushAgent, MethodCall call, final Result result) {
        List<String> tags = (List<String>) call.arguments;

        mPushAgent.getTagManager().addTags(new TagManager.TCallBack() {
            @Override
            public void onMessage(boolean b, ITagManager.Result result1) {
                Log.e(TAG, "设置标签:" + b + "  result1=="+result1);
                result.success(b);
            }
        }, tags.toArray(new String[0]));
    }

    //获取所有的标签
    private void getAllTags(PushAgent mPushAgent) {
        mPushAgent.getTagManager().getTags(new TagManager.TagListCallBack() {
            @Override
            public void onMessage(boolean b, List<String> list) {
                Log.e("TAG", "获取数据成功" + Thread.currentThread().getName()+"   list=="+list);
                Message message= mHandler.obtainMessage(0,list);
                mHandler.sendMessage(message);
                //channel.invokeMethod("onTag","22222");
            }
        });
    }

    //删除标签
    private void deleteUMTags(PushAgent mPushAgent, MethodCall call, Result result) {

        List<String> tags = (List<String>) call.arguments;

        Log.e("TAG", "传递的数据:" + tags);
        mPushAgent.getTagManager().deleteTags(new TagManager.TCallBack() {
            @Override
            public void onMessage(boolean b, ITagManager.Result result1) {

            }
        }, tags.toArray(new String[0]));
    }

    //增加别名 用于给指定推送
    private void addAlias(PushAgent mPushAgent, MethodCall call, Result result) {

        String aId = call.argument("AID");
        String aType = call.argument("AType");
        mPushAgent.addAlias(aId, aType, new UTrack.ICallBack() {
            @Override
            public void onMessage(boolean b, String s) {

            }
        });
    }

    //别名绑定
    private void setAlias(PushAgent mPushAgent, MethodCall call, Result result) {


        String aId = call.argument("AID");
        String aType = call.argument("AType");
        mPushAgent.setAlias(aId, aType, new UTrack.ICallBack() {
            @Override
            public void onMessage(boolean b, String s) {

            }
        });
    }

    //删除别名
    private void deleteAlias(PushAgent mPushAgent, MethodCall call, Result result) {

        String aId = call.argument("AID");
        String aType = call.argument("AType");
        mPushAgent.deleteAlias(aId, aType, new UTrack.ICallBack() {
            @Override
            public void onMessage(boolean b, String s) {

            }
        });
    }

    //当由离线推送进入
    private void offLine() {
        if(!TextUtils.isEmpty(UmengApplication.offLineBody)){
            channel.invokeMethod("onPushClick",UmengApplication.offLineBody);
        }
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        channel = new MethodChannel(binding.getBinaryMessenger(), "flutter_umeng");
        channel.setMethodCallHandler(this);

        channelTags = new MethodChannel(binding.getBinaryMessenger(), "flutter_umeng_tags");
        channelTags.setMethodCallHandler(this);
        applicationContext = binding.getApplicationContext();
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
        channelTags.setMethodCallHandler(null);
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        onAttachedToActivity(binding);
    }

    @Override
    public void onDetachedFromActivity() {
        activity = null;
    }
}
