package com.easemob.easeui.utils;

import java.io.File;
import java.io.FileInputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easemob.easeui.R;
import com.easemob.easeui.controller.EaseUI;
import com.easemob.easeui.controller.EaseUI.EaseUserProfileProvider;
import com.easemob.easeui.domain.EaseUser;

public class EaseUserUtils {
    
    static EaseUserProfileProvider userProvider;
    
    static {
        userProvider = EaseUI.getInstance().getUserProfileProvider();
    }
    
    /**
     * 根据username获取相应user
     * @param username
     * @return
     */
    public static EaseUser getUserInfo(String username){
        if(userProvider != null)
            return userProvider.getUser(username);
        
        return null;
    }

	public static Bitmap getLoacalBitmap(String filename) {
		File file = null;
		FileInputStream fos = null;
		try {
		 	file = new File(filename);

			if(!file.exists()){
				return null;
			}
			
			fos= new FileInputStream(filename);

			return BitmapFactory.decodeStream(fos);         

           } catch (Exception e) {
              e.printStackTrace();
              return null;
         }
    }
    
    /**
     * 设置用户头像
     * @param username
     */

	public static Bitmap decodeFile(String filename,int req_Height,int req_Width){
		File file = null;
		FileInputStream fos = null;
		
		try {
			file = new File(filename);

			if(!file.exists()){
				return null;
			}
			
			//decode image size
			BitmapFactory.Options o1 = new BitmapFactory.Options();
			o1.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(file),null,o1);


			//Find the correct scale value. It should be the power of 2.
			int width_tmp = o1.outWidth;
			int height_tmp = o1.outHeight;
			int scale = 1;

			if(width_tmp > req_Width || height_tmp > req_Height)
			{
				int heightRatio = Math.round((float) height_tmp / (float) req_Height);
				int widthRatio = Math.round((float) width_tmp / (float) req_Width);
				scale = heightRatio < widthRatio ? heightRatio : widthRatio;
			}

			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			o2.inScaled = false;
			return BitmapFactory.decodeFile(file.getAbsolutePath(),o2);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
    		}
	
	}

	public static boolean setAvatarView(String filename, ImageView imageView){
		Bitmap bp = null;
		bp = decodeFile(filename,43,43);

		if(bp!=null){
			imageView.setImageBitmap(bp);
			return true;
		}else{
			return false;
		}
	}

    public static void setUserAvatar(Context context, String username, ImageView imageView){
    	EaseUser user = getUserInfo(username);
        if(user != null && user.getAvatar() != null){
            try {
                //int avatarResId = Integer.parseInt(user.getAvatar());
                //Glide.with(context).load(avatarResId).into(imageView);
                if(!setAvatarView(user.getAvatar(),imageView)){
			Glide.with(context).load(user.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ease_default_avatar).into(imageView);
		   }
            } catch (Exception e) {
                //正常的string路径
                Glide.with(context).load(user.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ease_default_avatar).into(imageView);
            }
        }else{
            Glide.with(context).load(R.drawable.ease_default_avatar).into(imageView);
        }
    }
    
    /**
     * 设置用户昵称
     */
    public static void setUserNick(String username,TextView textView){
        if(textView != null){
        	EaseUser user = getUserInfo(username);
        	if(user != null && user.getNick() != null){
        		textView.setText(user.getNick());
        	}else{
        		textView.setText(username);
        	}
        }
    }
    
}
