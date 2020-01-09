package com.changhong.sei.notify.dto;

import com.chonghong.sei.annotation.Remark;

/**
 * *************************************************************************************************
 * <p/>
 * 实现功能：消息通知类型枚举
 * <p>
 * ------------------------------------------------------------------------------------------------
 * 版本          变更时间             变更人                     变更原因
 * ------------------------------------------------------------------------------------------------
 * 1.0.00      2017-04-14 19:42      王锦光(wangj)                新建
 * <p/>
 * *************************************************************************************************
 */
public enum NotifyType {
    /**
     * 电子邮件
     */
    @Remark("电子邮件")
    Email,

    /**
     * 手机短信
     */
    @Remark("手机短信")
    Sms;
}
