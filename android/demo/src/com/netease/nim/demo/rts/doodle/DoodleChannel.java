package com.netease.nim.demo.rts.doodle;

import android.graphics.Color;

import com.netease.nim.demo.rts.doodle.action.Action;

import java.util.ArrayList;
import java.util.List;

/**
 * Ϳѻ��ͨ��������ͨ�������ͨ����
 * <p/>
 * Created by huangjun on 2015/6/29.
 */
class DoodleChannel {
    /**
     * ��ǰ��ѡ�Ļ���
     */
    public int type = 0; // ��ǰ����״����

    public Action action; // ��ǰ����״����

    public int paintColor = Color.BLACK;

    public int paintSize = 5;

    public int lastPaintColor = paintColor; // ��һ��ʹ�õĻ�����ɫ����Ƥ���л�����״ʱ���ָ��ϴε���ɫ��

    public int lastPaintSize = paintSize; // ��һ��ʹ�õĻ��ʴ�ϸ����Ƥ���л�����״ʱ���ָ��ϴεĴ�ϸ��

    /**
     * ��¼������״���б�
     */
    public List<Action> actions = new ArrayList<>();

    /**
     * ���õ�ǰ���ʵ���״
     *
     * @param type
     */
    public void setType(int type) {
        if (this.type == SupportActionType.getInstance().getEraserType()) {
            // ����Ƥ���л���ĳ����״���ָ�������ɫ�����ʴ�ϸ
            this.paintColor = this.lastPaintColor;
            this.paintSize = this.lastPaintSize;
        }

        this.type = type;
    }

    /**
     * ���õ�ǰ����Ϊ��Ƥ��
     */
    public void setEraseType(int bgColor, int size) {
        this.type = SupportActionType.getInstance().getEraserType();
        this.lastPaintColor = this.paintColor; // ���ݵ�ǰ�Ļ�����ɫ
        this.lastPaintSize = this.paintSize; // ���ݵ�ǰ�Ļ��ʴ�ϸ
        this.paintColor = bgColor;
        if (size > 0) {
            paintSize = size;
        }
    }

    /**
     * ���õ�ǰ���ʵ���ɫ
     *
     * @param color
     */
    public void setColor(String color) {
        if (this.type == SupportActionType.getInstance().getEraserType()) {
            // �������ʹ����Ƥ������ô���ܸ��Ļ�����ɫ
            return;
        }

        this.paintColor = Color.parseColor(color);
    }

    /**
     * ���û��ʵĴ�ϸ
     *
     * @param size
     */
    public void setSize(int size) {
        if (size > 0) {
            this.paintSize = size;
        }
    }
}
