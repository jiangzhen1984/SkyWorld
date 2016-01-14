package com.easemob.easeui.domain;

public class EaseEmojicon {
    public EaseEmojicon(){
    }
    
    /**
     * ���캯��
     * @param icon ��̬ͼƬresource id
     * @param emojiText ����emoji�ı�����
     */
    public EaseEmojicon(int icon, String emojiText){
        this.icon = icon;
        this.emojiText = emojiText;
        this.type = Type.NORMAL;
    }
    
    /**
     * ���캯��
     * @param icon ��̬ͼƬresource id
     * @param emojiText emojiText ����emoji�ı�����
     * @param type ��������
     */
    public EaseEmojicon(int icon, String emojiText, Type type){
        this.icon = icon;
        this.emojiText = emojiText;
        this.type = type;
    }
    
    
    /**
     * Ψһʶ���
     */
    private String identityCode;
    
    /**
     * static icon resource id
     */
    private int icon;
    
    /**
     * dynamic icon resource id
     */
    private int bigIcon;
    
    /**
     * ����emoji�ı�����,�����������ݿ���Ϊnull
     */
    private String emojiText;
    
    /**
     * ��������Ӧ������
     */
    private String name;
    
    /**
     * ��ͨor�����
     */
    private Type type;
    
    /**
     * ���龲̬ͼƬ��ַ
     */
    private String iconPath;
    
    /**
     * �����ͼƬ��ַ
     */
    private String bigIconPath;
    
    
    /**
     * ��ȡ��̬ͼƬ(СͼƬ)��Դid
     * @return
     */
    public int getIcon() {
        return icon;
    }


    /**
     * ���þ�̬ͼƬ��Դid
     * @param icon
     */
    public void setIcon(int icon) {
        this.icon = icon;
    }


    /**
     * ��ȡ��ͼƬ��Դid
     * @return
     */
    public int getBigIcon() {
        return bigIcon;
    }


    /**
     * ���ô�ͼƬ��Դid
     * @return
     */
    public void setBigIcon(int dynamicIcon) {
        this.bigIcon = dynamicIcon;
    }


    /**
     * ��ȡemoji�ı�����
     * @return
     */
    public String getEmojiText() {
        return emojiText;
    }


    /**
     * ����emoji�ı�����
     * @param emojiText
     */
    public void setEmojiText(String emojiText) {
        this.emojiText = emojiText;
    }

    /**
     * ��ȡ��������
     * @return
     */
    public String getName() {
        return name;
    }
    
    /**
     * ���ñ�������
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * ��ȡ��������
     * @return
     */
    public Type getType() {
        return type;
    }


    /**
     * ���ñ�������
     * @param type
     */
    public void setType(Type type) {
        this.type = type;
    }


    /**
     * ��ȡ��̬ͼƬ��ַ
     * @return
     */
    public String getIconPath() {
        return iconPath;
    }


    /**
     * ���þ�̬ͼƬ��ַ
     * @param iconPath
     */
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }


    /**
     * ��ȡ��ͼ(��̬��ַ)��ַ()
     * @return
     */
    public String getBigIconPath() {
        return bigIconPath;
    }


    /**
     * ���ô�ͼ(��̬��ַ)��ַ
     * @param bigIconPath
     */
    public void setBigIconPath(String bigIconPath) {
        this.bigIconPath = bigIconPath;
    }

    /**
     * ��ȡʶ����
     * @return
     */
    public String getIdentityCode() {
        return identityCode;
    }
    
    /**
     * ����ʶ����
     * @param identityId
     */
    public void setIdentityCode(String identityCode) {
        this.identityCode = identityCode;
    }

    public static final String newEmojiText(int codePoint) {
        if (Character.charCount(codePoint) == 1) {
            return String.valueOf(codePoint);
        } else {
            return new String(Character.toChars(codePoint));
        }
    }



    public enum Type{
        /**
         * ��ͨ���飬����һ����������edittext
         */
        NORMAL,
        /**
         * ����飬���֮��ֱ�ӷ���
         */
        BIG_EXPRESSION
    }
}
