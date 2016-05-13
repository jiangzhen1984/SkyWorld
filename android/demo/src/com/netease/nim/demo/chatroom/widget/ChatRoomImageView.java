package com.netease.nim.demo.chatroom.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;

import com.netease.nim.uikit.ImageLoaderKit;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.ui.imageview.CircleImageView;
import com.netease.nimlib.sdk.nos.model.NosThumbParam;
import com.netease.nimlib.sdk.nos.util.NosThumbImageUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.imageaware.NonViewAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ChatRoomImageView extends CircleImageView {

    public static final int DEFAULT_THUMB_SIZE = (int) NimUIKit.getContext().getResources().getDimension(R.dimen.avatar_max_size);;

    private DisplayImageOptions options;

    private final DisplayImageOptions createImageOptions() {
        int defaultIcon = NimUIKit.getUserInfoProvider().getDefaultIconResId();
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(defaultIcon)
                .showImageOnFail(defaultIcon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    public ChatRoomImageView(Context context) {
        super(context);
    }

    public ChatRoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatRoomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageViewEx, defStyle, 0);
        a.recycle();

        this.options = createImageOptions();
    }

    public void loadAvatarByUrl(String url) {
        loadAvatar(url, DEFAULT_THUMB_SIZE);
    }

    /**
     * ����ͼƬ
     */
    public void loadAvatar(final String url, final int thumbSize) {
        // ����ʾĬ��ͷ��
        setImageResource(NimUIKit.getUserInfoProvider().getDefaultIconResId());

        // �ж��Ƿ���ҪImageLoader����
        boolean needLoad = ImageLoaderKit.isImageUriValid(url);

        // ImageLoader�첽����
        if (needLoad) {
            setTag(url); // ���ViewHolder��������
            /**
             * ��ʹ�����������ƴ洢�����������������ͼƬ��ѹ���ߴ磬��������URL
             * ���ͼƬ��Դ�Ƿ����������ƴ洢���벻Ҫʹ��NosThumbImageUtil
             */
            final String thumbUrl = thumbSize > 0 ? NosThumbImageUtil.makeImageThumbUrl(url,
                    NosThumbParam.ThumbType.Crop, thumbSize, thumbSize) : url;

            // �첽��cache or NOS����ͼƬ
            ImageLoader.getInstance().displayImage(thumbUrl, new NonViewAware(new ImageSize(thumbSize, thumbSize),
                    ViewScaleType.CROP), options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (getTag() != null && getTag().equals(url)) {
                        setImageBitmap(loadedImage);
                    }
                }
            });
        } else {
            setTag(null);
        }
    }
}
