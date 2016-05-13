package com.netease.nim.demo.avchat.constant;

/**
 * ����״̬,��������ˢ�½���
 * Created by hzxuwen on 2015/4/27.
 */
public enum CallStateEnum {
    INVALID(-1), //��Ч��״̬,��״̬���޽�����ʾ

    VIDEO(0), //���ڽ�����Ƶͨ��(������)
    OUTGOING_VIDEO_CALLING(2), //���������Ƶͨ��
    INCOMING_VIDEO_CALLING(4),
    OUTGOING_AUDIO_TO_VIDEO(6), //����ѷ���������л�����Ƶ������
    VIDEO_CONNECTING(8), //��Ƶͨ��������
    VIDEO_OFF(10), // �Է��ر�����ͷ

    AUDIO(1), //���ڽ�������ͨ��(������)
    OUTGOING_AUDIO_CALLING(3), //�����������ͨ��
    INCOMING_AUDIO_CALLING(5), //���Ժ��ѵ���Ƶͨ��������ͨ������
    INCOMING_AUDIO_TO_VIDEO(7), //��Ƶ�л�Ϊ��Ƶ������
    AUDIO_CONNECTING(9); //����ͨ��������

    private int value;

    CallStateEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static boolean isVideoMode(CallStateEnum value) {
        return value.getValue() % 2 == 0;
    }

    public static boolean isAudioMode(CallStateEnum value) {
        return value.getValue() % 2 == 1;
    }

    public static CallStateEnum getCallStateEnum(int value) {
        for (CallStateEnum e : values()) {
            if (e.getValue() == value) {
                return e;
            }
        }

        return INVALID;
    }
}
