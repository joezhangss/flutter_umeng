package ujk.com.flutter_umeng;

import android.text.TextUtils;

import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.common.inter.ITagManager;
import com.umeng.message.tag.TagManager;

import java.util.List;

import io.flutter.Log;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * FlutterUmengPlugin
 */
public class FlutterUmengPlugin implements MethodCallHandler {
    /**
     * Plugin registration.
     */
    private static Registrar mRegistrar;
     static MethodChannel channel;
    private final String TAG = "FlutterUmengPlugin";

    public static void registerWith(Registrar registrar) {
        mRegistrar = registrar;
        channel = new MethodChannel(registrar.messenger(), "flutter_umeng");
        channel.setMethodCallHandler(new FlutterUmengPlugin());

    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {

        PushAgent mPushAgent = PushAgent.getInstance(mRegistrar.context());
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
                Log.e(TAG, "设置标签:" + b);
                result.success(b);
            }
        }, tags.toArray(new String[0]));
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
}
