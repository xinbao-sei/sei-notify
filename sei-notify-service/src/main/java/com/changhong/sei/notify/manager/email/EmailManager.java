package com.changhong.sei.notify.manager.email;

import com.changhong.sei.core.context.ContextUtil;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.log.LogUtil;
import com.changhong.sei.edm.dto.DocumentResponse;
import com.changhong.sei.edm.sdk.DocumentManager;
import com.changhong.sei.notify.dto.*;
import com.changhong.sei.notify.entity.MessageHistory;
import com.changhong.sei.notify.manager.NotifyManager;
import com.changhong.sei.notify.service.MessageHistoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * *************************************************************************************************
 * <p/>
 * 实现功能：邮件消息的处理类
 * <p>
 * ------------------------------------------------------------------------------------------------
 * 版本          变更时间             变更人                     变更原因
 * ------------------------------------------------------------------------------------------------
 * 1.0.00      2017-04-17 8:58      王锦光(wangj)                新建
 * <p/>
 * *************************************************************************************************
 */
public class EmailManager implements NotifyManager {
    @Value("${notify.mail.default-sender}")
    private String defaultSender;
    @Value("${spring.mail.username}")
    private String senderUsername;
    @Value("${spring.mail.password}")
    private String senderPassword;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private MessageHistoryService historyService;
    @Autowired
    private DocumentManager documentManager;

    /**
     * 初始化发送邮件的消息内容
     *
     * @param message 邮件消息
     */
    private void initMessage(EmailMessage message) {
        EmailAccount sender = message.getSender();
        if (sender == null) {
            sender = new EmailAccount(defaultSender, senderUsername);
            message.setSender(sender);
        } else {
            //将实际发送人姓名追加输出
            String content = message.getContent();
//            content += String.format("</br><p>发送人：%s</p><p>发送人邮箱：%s</p>", sender.getName(),
//                    sender.getAddress());
            message.setContent(content);
        }
    }

    /**
     * 发送简单邮件
     *
     * @param message 邮件信息
     */
    public void send(EmailMessage message) {
        //检查邮件消息
        if (Objects.isNull(message) || CollectionUtils.isEmpty(message.getReceivers())) {
            return;
        }

        boolean success = Boolean.TRUE;
        String log = "success";
        String content = message.getContent();
        MessageHistory history;
        List<MessageHistory> histories = new ArrayList<>();

        //初始化消息
        initMessage(message);
        Set<String> docIds = null;
        try {
            //构造MimeMessage并设置相关属性值,MimeMessage就是实际的电子邮件对象.
            MimeMessage msg = mailSender.createMimeMessage();
            //设置发件人
            msg.setFrom(new InternetAddress(senderUsername, defaultSender));
            // 设置收件人,为数组,可输入多个地址.
            List<InternetAddress> to = new ArrayList<>();
            for (EmailAccount account : message.getReceivers()) {
                if (Objects.nonNull(account)
                        && StringUtils.isNotBlank(account.getAddress())
                        && StringUtils.isNotBlank(account.getName())) {
                    history = new MessageHistory();
                    history.setCategory(NotifyType.EMAIL);
                    history.setSubject(message.getSubject());
                    history.setTargetType(TargetType.PERSONAL);
                    history.setTargetValue(account.getAddress());
                    history.setTargetName(account.getName());

                    try {
                        to.add(new InternetAddress(account.getAddress(), account.getName()));
                    } catch (UnsupportedEncodingException e) {
                        history.setSendStatus(Boolean.FALSE);
                        history.setSendLog(e.getMessage());
                    }

                    histories.add(history);
                }
            }
            // 设置抄送人,为数组,可输入多个地址.
            List<InternetAddress> cc = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(message.getCcList())) {
                for (EmailAccount account : message.getCcList()) {
                    if (Objects.nonNull(account)
                            && StringUtils.isNotBlank(account.getAddress())
                            && StringUtils.isNotBlank(account.getName())) {
                        history = new MessageHistory();
                        history.setCategory(NotifyType.EMAIL);
                        history.setSubject(ContextUtil.getMessage("00001", message.getSubject()));
                        history.setTargetType(TargetType.PERSONAL);
                        history.setTargetValue(account.getAddress());
                        history.setTargetName(account.getName());

                        try {
                            cc.add(new InternetAddress(account.getAddress(), account.getName()));
                        } catch (UnsupportedEncodingException e) {
                            history.setSendStatus(Boolean.FALSE);
                            history.setSendLog(e.getMessage());
                        }

                        histories.add(history);
                    }
                }
            }
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            // 设置收件人
            helper.setTo(to.toArray(new InternetAddress[0]));
            // 设置抄送人
            if (CollectionUtils.isNotEmpty(cc)) {
                helper.setCc(cc.toArray(new InternetAddress[0]));
            }
            //ContentBody.RecipientType==>TO(主要接收人),CC(抄送),BCC(密件抄送);
            //设置邮件主题,如果不是UTF-8就要转换下
            helper.setSubject(message.getSubject());
            //设置邮件内容
            helper.setText(content, true);
            //发送时间
            helper.setSentDate(new Date());

            docIds = message.getDocIds();
            //验证文件数据是否为空
            if (CollectionUtils.isNotEmpty(docIds)) {
                InputStreamSource source;
                DocumentResponse docResponse;
                for (String docId : docIds) {
                    if (StringUtils.isNotBlank(docId)) {
                        docResponse = documentManager.getDocument(docId, false);
                        if (Objects.nonNull(docResponse) && Objects.nonNull(docResponse.getData())) {
                            source = new ByteArrayResource(docResponse.getData());
                            //添加附件
                            helper.addAttachment(docResponse.getFileName(), source);
                        } else {
                            LogUtil.error("[{}]附件不存在.", docId);
                        }
                    }
                }
            }
            //发送邮件,使用如下方法!
            mailSender.send(msg);
        } catch (Exception e) {
            // 记录异常日志
            LogUtil.error("发送邮件失败！", e);
            success = Boolean.FALSE;
            log = e.getMessage();
        }

        try {
            historyService.recordHistory(histories, content, success, log, docIds);
        } catch (Exception e) {
            LogUtil.error("记录消息历史异常", e);
        }
    }

    /**
     * 发送消息通知
     *
     * @param message 消息
     */
    @Override
    public ResultData<String> send(SendMessage message) {
        String subject = message.getSubject();
        String content = message.getContent();
        UserNotifyInfo sender = message.getSender();
        List<UserNotifyInfo> receivers = message.getReceivers();
        List<UserNotifyInfo> ccList = message.getCcList();
        // 构造邮件消息
        EmailMessage emailMsg = new EmailMessage();
        if (Objects.nonNull(sender) && StringUtils.isNotBlank(sender.getEmail())) {
            emailMsg.setSender(new EmailAccount(sender.getUserName(), sender.getEmail()));
        }
        emailMsg.setSubject(subject);
        emailMsg.setContent(content);
        List<EmailAccount> emailAccounts = new ArrayList<>();
        receivers.forEach((r) -> emailAccounts.add(new EmailAccount(r.getUserName(), r.getEmail())));
        emailMsg.setReceivers(emailAccounts);
        List<EmailAccount> emailCcAccounts = new ArrayList<>();
        ccList.forEach((r) -> emailCcAccounts.add(new EmailAccount(r.getUserName(), r.getEmail())));
        emailMsg.setCcList(emailCcAccounts);
        emailMsg.setDocIds(message.getDocIds());
        // 发送邮件
        send(emailMsg);

        return ResultData.success("OK");
    }
}

