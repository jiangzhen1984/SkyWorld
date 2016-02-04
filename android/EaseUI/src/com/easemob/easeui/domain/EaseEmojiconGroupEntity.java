package com.easemob.easeui.domain;

import java.util.List;

import com.easemob.easeui.domain.EaseEmojicon.Type;

/**
 * һ���������Ӧ��ʵ����
 *
 */
public class EaseEmojiconGroupEntity {
    /**
     * ��������
     */
    private List<EaseEmojicon> emojiconList;
    /**
     * ͼƬ
     */
    private int icon;
    /**
     * ����
     */
    private String name;
    /**
     * ��������
     */
    private EaseEmojicon.Type type;
    
    public EaseEmojiconGroupEntity(){}
    
    public EaseEmojiconGroupEntity(int icon, List<EaseEmojicon> emojiconList){
        this.icon = icon;
        this.emojiconList = emojiconList;
        type = Type.NORMAL;
    }
    
    public EaseEmojiconGroupEntity(int icon, List<EaseEmojicon> emojiconList, EaseEmojicon.Type type){
        this.icon = icon;
        this.emojiconList = emojiconList;
        this.type = type;
    }
    
    public List<EaseEmojicon> getEmojiconList() {
        return emojiconList;
    }
    public void setEmojiconList(List<EaseEmojicon> emojiconList) {
        this.emojiconList = emojiconList;
    }
    public int getIcon() {
        return icon;
    }
    public void setIcon(int icon) {
        this.icon = icon;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public EaseEmojicon.Type getType() {
        return type;
    }

    public void setType(EaseEmojicon.Type type) {
        this.type = type;
    }
    
    
}
