package com.netease.nim.demo.common.ui.viewpager;

import android.annotation.SuppressLint;
import android.support.v4.view.ViewPager;
import android.view.View;
/**
 * Viewpager ҳ���л�������ֻ֧��3.0���ϰ汾
 * <p/>
 * [-�ޣ�-1]��ȫ���ɼ�
 * [-1,  0]�Ӳ��ɼ�����ȫ�ɼ�
 * [0,1]����ȫ�ɼ������ɼ�
 * [1,��]��ȫ���ɼ�
 * <p/>
 * Created by doc on 15/1/6.
 */
public class FadeInOutPageTransformer implements ViewPager.PageTransformer {

    @SuppressLint("NewApi")
    @Override
    public void transformPage(View page, float position) {
        if (position < -1) {//ҳ����ȫ���ɼ�
            page.setAlpha(0);
        } else if (position < 0) {
            page.setAlpha(1 + position);
        } else if (position < 1) {
            page.setAlpha(1 - position);
        } else {
            page.setAlpha(0);
        }
    }
}
