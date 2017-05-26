package com.luckybuy.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.luckybuy.BaseActivity;
import com.luckybuy.R;
import com.luckybuy.model.VerifyCodeModel;
import com.luckybuy.network.ParseData;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import java.util.Map;

/**
 * Created by zhiPeng.S on 2016/7/29.
 */
@ContentView(R.layout.activity_password)
public class UpdatePwd extends BaseActivity{

    private VerifyCodeModel model;
    private boolean isResetPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            model = (VerifyCodeModel) bundle.getSerializable("bundle");
            isResetPwd = true;
        }


        title_tv.setText(R.string.title_retrieve_pwd);
        if(isResetPwd){
            title_tv.setText(R.string.title_reset_pwd);
            pwd_et1.setHint(R.string.pwd_input_new_hint);
            pwd_et1.setInputType(EditorInfo.TYPE_CLASS_NUMBER|EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD);
            pwd_et2.setHint(R.string.pwd_confirm_hint);
            code_btn.setVisibility(View.GONE);
            commit_btn.setText(R.string.determine);
        }

    }



    @ViewInject(R.id.title_activity)
    private TextView title_tv;

    @ViewInject(R.id.pwd_et1)
    private EditText pwd_et1;

    @ViewInject(R.id.pwd_et2)
    private EditText pwd_et2;

    @ViewInject(R.id.pwd_verify_code_btn)
    private Button code_btn;

    @ViewInject(R.id.pwd_commit_btn)
    private Button commit_btn;

    @Event({R.id.back_iv,R.id.pwd_commit_btn,R.id.pwd_verify_code_btn})
    private void viewClick(View view){
        switch (view.getId()){
            case R.id.back_iv:
                this.finish();
                break;
            case R.id.pwd_commit_btn:
                if(isResetPwd)
                    checkPwdInfo();
                else
                    checkVerifyInfo();
                break;
            case R.id.pwd_verify_code_btn:
                String mobile = pwd_et1.getText().toString();
                if(mobile.length() != 10 && mobile.length() != 11)  {
                    Utility.toastShow(x.app(),R.string.phone_length_error);
                    return;
                }
                code_btn.setEnabled(false);
                getVerifyCode(mobile);
                Utility.VerifyCountDownTimer timer = new Utility.VerifyCountDownTimer(60*1000,1000);
                timer.setButton(code_btn);
                timer.start();
                break;
        }
    }


    private void checkVerifyInfo(){
        if(model == null || !model.getReturncode().toLowerCase().equals("success")) return;
        String verifyCode_user = pwd_et2.getText().toString();
        String verifyCode_true = model.getVerifycode();
        if(verifyCode_user.equals(verifyCode_true)){
            Bundle bundle = new Bundle();
            bundle.putSerializable("bundle",model);
            Intent intent = new Intent(this,UpdatePwd.class);
            intent.putExtras(bundle);
            startActivityForResult(intent,Constant.REQUEST_CODE);
        }else {
            Utility.toastShow(x.app(),R.string.verifyCode_error);
        }
    }

    private void checkPwdInfo(){
        String pwd1 = pwd_et1.getText().toString();
        String pwd2 = pwd_et2.getText().toString();
        if(pwd1.length() < 6 || pwd1.length() > 12) {
            Utility.toastShow(x.app(),R.string.pwd_input_tip);
            return;
        }
        if(pwd1.equals(pwd2)){
            String mobile = model.getMobile();
            String verifycode = model.getVerifycode();
            updatePwdInfo(mobile, pwd1 ,verifycode);
        }else {
            Utility.toastShow(x.app(),R.string.pwd_disagree);
        }
    }

    private void updatePwdInfo(String mobile, String password, String verifycode){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Page/Ucenter/PSWModify.ashx");
        params.addBodyParameter("mobile", mobile);
        params.addBodyParameter("password", password);
        params.addBodyParameter("verifycode", verifycode);
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String returnCode = jsonObject.getString("returncode");
                    if(returnCode.toLowerCase().equals("success")){
                        Utility.toastShow(x.app(),"Reset Password Success");
                        UpdatePwd.this.setResult(Constant.RESULT_CODE);
                        UpdatePwd.this.finish();
                    }else if(returnCode.toLowerCase().equals("error")){
                        String errorInfo = jsonObject.getString("error");
                        Utility.toastShow(x.app(),errorInfo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    private void getVerifyCode(String mobile){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Page/Ucenter/PSWVerify.ashx");
        params.addQueryStringParameter("mobile", mobile);
        params.addQueryStringParameter("verifytype", "password");
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Map<String, Object> resultMap = ParseData.parseVerifyCodeInfo(result);
                if (!result.isEmpty()) {
                    model = (VerifyCodeModel) resultMap.get(Constant.AWARD_LIST);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case Constant.RESULT_CODE:
                finish();
                break;
        }
    }
}
