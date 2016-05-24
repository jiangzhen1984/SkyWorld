package com.netease.nim.uikit.common.ui.imageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;

import com.netease.nim.uikit.ImageLoaderKit;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.R;
import com.netease.nimlib.sdk.nos.model.NosThumbParam;
import com.netease.nimlib.sdk.nos.util.NosThumbImageUtil;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.imageaware.NonViewAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by huangjun on 2015/11/13.
 */
public class HeadImageView extends CircleImageView {

    public static final int DEFAULT_AVATAR_THUMB_SIZE = (int) NimUIKit.getContext().getResources().getDimension(R.dimen.avatar_max_size);
    public static final int DEFAULT_AVATAR_NOTIFICATION_ICON_SIZE = (int) NimUIKit.getContext().getResources().getDimension(R.dimen.avatar_notification_size);

    private DisplayImageOptions options = createImageOptions();

    private static final DisplayImageOptions createImageOptions() {
        int defaultIcon = NimUIKit.getUserInfoProvider().getDefaultIconResId();
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(defaultIcon)
                .showImageOnFail(defaultIcon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    public HeadImageView(Context context) {
        super(context);
    }

    public HeadImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeadImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * �����û�ͷ��Ĭ�ϴ�С������ͼ��
     *
     * @param account
     */
    public void loadBuddyAvatar(String account) {
        loadBuddyAvatar(account, DEFAULT_AVATAR_THUMB_SIZE);
    }

    /**
     * �����û�ͷ��ԭͼ��
     *
     * @param account
     */
    public void loadBuddyOriginalAvatar(String account) {
        loadBuddyAvatar(account, 0);
    }

    /**
     * �����û�ͷ��ָ�����Դ�С��
     *
     * @param account
     * @param thumbSize ����ͼ�Ŀ���
     */
    private void loadBuddyAvatar(final String account, final int thumbSize) {
        // ����ʾĬ��ͷ��
        setImageResource(NimUIKit.getUserInfoProvider().getDefaultIconResId());

        // �ж��Ƿ���ҪImageLoader����
        final UserInfoProvider.UserInfo userInfo = NimUIKit.getUserInfoProvider().getUserInfo(account);
        boolean needLoad = userInfo != null && ImageLoaderKit.isImageUriValid(userInfo.getAvatar());

        // ImageLoader�첽����
        if (needLoad) {
            setTag(account); // ���ViewHolder��������
            /**
             * ��ʹ�����������ƴ洢�����������������ͼƬ��ѹ���ߴ磬��������URL
             * ���ͼƬ��Դ�Ƿ����������ƴ洢���벻Ҫʹ��NosThumbImageUtil
             */
            final String thumbUrl = makeAvatarThumbNosUrl(userInfo.getAvatar(), thumbSize);

            // �첽��cache or NOS����ͼƬ
            ImageLoader.getInstance().displayImage(thumbUrl, new NonViewAware(new ImageSize(thumbSize, thumbSize),
                    ViewScaleType.CROP), options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (getTag() != null && getTag().equals(account)) {
                        setImageBitmap(loadedImage);
                    }
                }
            });
        } else {
            setTag(null);
        }
    }

    public void loadTeamIcon(String tid) {
        Bitmap bitmap = NimUIKit.getUserInfoProvider().getTeamIcon(tid);
        setImageBitmap(bitmap);
    }

    /**
     * ���ViewHolder��������
     */
    public void resetImageView() {
        setImageBitmap(null);
    }

    /**
     * ����ͷ������ͼNOS URL��ַ������ImageLoader�����key��
     */
    private static String makeAvatarThumbNosUrl(final String url, final int thumbSize) {
         /*SAMC_BEGIN()*/
         //return thumbSize > 0 ? NosThumbImageUtil.makeImageThumbUrl(url, NosThumbParam.ThumbType.Crop, thumbSize, thumbSize) : url;
         return url;
         /*SAMC_BEGIN()*/
    }

    public static String getAvatarCacheKey(final String url) {
        return makeAvatarThumbNosUrl(url, DEFAULT_AVATAR_THUMB_SIZE);
    }
}
