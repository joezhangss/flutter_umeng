package ujk.com.flutter_umeng;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.umeng.message.UmengNotifyClickActivity;

import org.android.agoo.common.AgooConstants;
import org.json.JSONException;
import org.json.JSONObject;

public class MPushActivity extends UmengNotifyClickActivity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
//        setContentView(R.layout.activity_mpush);
        startMainActivity();
    }


    private void startMainActivity() {
        //获取当前Application包名


        Intent startIntent = new Intent(Intent.ACTION_MAIN);
        startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName(getPackageName(), getPackageName()+".MainActivity");
        startIntent.setComponent(cn);
        startActivity(startIntent);
        finish();
    }


    @Override
    public void onMessage(Intent intent) {
        super.onMessage(intent);

        try {
            String body = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
            JSONObject jObj=new JSONObject(body);
            //Log.e("TAG","数据："+body);
            String action=jObj.getJSONObject("body").getString("custom");
            JSONObject mapJson=jObj.getJSONObject("extra").put("Action",action);
            UmengApplication.offLineBody = mapJson.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
