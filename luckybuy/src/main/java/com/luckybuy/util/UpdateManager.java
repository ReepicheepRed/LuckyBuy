package com.luckybuy.util;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.os.Message;
import com.luckybuy.AboutActivity;
import com.luckybuy.R;
import com.luckybuy.model.AppUpdateModel;
import org.xutils.x;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/*
 *@author Eric 
 *@2015-11-7????8:03:31
 */
public class UpdateManager {
	private static UpdateManager manager = null;

	private boolean isMainPage;

	public void setMainPage(boolean mainPage) {
		isMainPage = mainPage;
	}

	private UpdateManager(){}
	public static UpdateManager getInstance(){
		manager = new UpdateManager();
		return manager;
	}
	
	//????????
	public int getVersion(Context context){
		int version = 0;
		try {  
			version = context.getPackageManager().getPackageInfo(  
                    "com.luckybuy", 0).versionCode;
        } catch (Exception e) {  
        	 System.out.println(R.string.obtain_version_exception);
        }  
		return version;
	}
	
	//????????
	public String getVersionName(Context context){
		String versionName = null;
		try {
			versionName = context.getPackageManager().getPackageInfo(
					"com.luckybuy", 0).versionName;
		} catch (Exception e) {
			 System.out.println(R.string.obtain_version_exception);
		}
		return versionName;
	}
	
	//??????????????
	public String getServerVersion(){
		String serverJson = null;
		byte[] buffer = new byte[128];
		
		try {
			URL serverURL = new URL("http://192.168.226.106/ver.aspx");
			HttpURLConnection connect = (HttpURLConnection) serverURL.openConnection();
			BufferedInputStream bis = new BufferedInputStream(connect.getInputStream());
			int n = 0;
			while((n = bis.read(buffer))!= -1){
				serverJson = new String(buffer);
			}
		} catch (Exception e) {
			System.out.println(x.app().getString(R.string.obtain_version_exception)+e);
		}
		
		return serverJson;
	}	
	
	//??????????????????????????
	public boolean compareVersion(Context context, final AppUpdateModel model){
		
		final Context contextTemp = context;
		
		new Thread(){
			public void run() {
				Looper.prepare();
				try {

					int getServerVersion = model.getVersioncode();
					String getServerVersionName = model.getVersionnumber();

					AboutActivity.version = getVersion(x.app());
					AboutActivity.versionName = getVersionName(x.app());

					AboutActivity.serverVersion = getServerVersion;
					AboutActivity.serverVersionName = getServerVersionName;
					
					if(AboutActivity.version < AboutActivity.serverVersion){
						//????????????
			            Builder builder  = new Builder(contextTemp);  
			            builder.setTitle(R.string.version_update) ;
			            builder.setMessage(x.app().getString(R.string.version_update)+AboutActivity.versionName
			            		+"\n"+x.app().getString(R.string.server_version)+AboutActivity.serverVersionName ) ;
			            builder.setPositiveButton(R.string.immediately_update,new DialogInterface.OnClickListener() {
			                   @Override  
			                   public void onClick(DialogInterface dialog, int arg1) { 
//			                       //???????????apk
//			                	   new Thread(){
//			                		   public void run() {
//			                			   Looper.prepare();
//			                			   downloadApkFile(contextTemp,model);
//			                			   Looper.loop();
//			                		   }
//			                	   }.start();
									// go to google play
								   googlePlay(contextTemp);
			                   }  
			               });  
			            builder.setNegativeButton(R.string.next_update, null);
			            builder.show();
					}else{
						if(isMainPage) return;
			            Builder builder  = new Builder(contextTemp);  
			            builder.setTitle(R.string.version_info) ;
			            builder.setMessage(R.string.current_latest_tip) ;
			            builder.setPositiveButton(R.string.determine,null);
			            builder.show();
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println(x.app().getString(R.string.update_thread_exception)+e);
				}
				
				Looper.loop();
			}
			
		}.start();
		
		
		
		
		
		return false;
	}
	
	
	//????apk???
	public void downloadApkFile(Context context, AppUpdateModel model){
		String savePath = Environment.getExternalStorageDirectory()+ context.getString(R.string.app_url_native);
		//String serverFilePath = x.app().getString(R.string.app_url);
		String serverFilePath = model.getDownloadaddress();
		try {
			if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){  
				URL serverURL = new URL(serverFilePath);
				HttpURLConnection connect = (HttpURLConnection) serverURL.openConnection();
				BufferedInputStream bis = new BufferedInputStream(connect.getInputStream());
				File apkfile = new File(savePath);
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(apkfile));
				
				int fileLength = connect.getContentLength();
				int downLength = 0;
				int progress ;
				int n;
				byte[] buffer = new byte[1024];
				while((n=bis.read(buffer, 0, buffer.length))!=-1){
					bos.write(buffer, 0, n);
					downLength +=n;
					progress = (int) (((float) downLength / fileLength) * 100);
					Message msg = new Message();
					msg.arg1 = progress;
					AboutActivity.handler.sendMessage(msg);
					//System.out.println("????"+progress);
				}
				bis.close();
				bos.close();
				connect.disconnect();
	        } 
			
		} catch (Exception e) {
			System.out.println(x.app().getString(R.string.download_error)+e);
		}
		

		/*AlertDialog.Builder builder  = new Builder(context);  
        builder.setTitle("????apk" ) ;  
        builder.setMessage("????????" ) ;  
        builder.setPositiveButton("???",null);  
        builder.show();*/
		
		
		
	}

	private void googlePlay(Context context){
		String serverFilePath = x.app().getString(R.string.app_url);
		Intent intent= new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse(serverFilePath);
		intent.setData(content_url);
		context.startActivity(intent);
	}


}
