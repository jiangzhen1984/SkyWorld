package com.netease.nim.demo.main.reminder;

public class ReminderSettings {
	/**
	 * �����ʾδ����
	 */
	public static final int MAX_UNREAD_SHOW_NUMBER = 99;

	public static int unreadMessageShowRule(int unread) {
		return Math.min(MAX_UNREAD_SHOW_NUMBER, unread);
	}
}
