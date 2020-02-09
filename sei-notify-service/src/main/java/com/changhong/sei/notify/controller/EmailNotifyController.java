package com.changhong.sei.notify.controller;

import com.changhong.sei.core.mq.MqProducer;
import com.changhong.sei.core.util.JsonUtils;
import com.changhong.sei.notify.api.EmailNotifyApi;
import com.changhong.sei.notify.dto.EmailMessage;
import com.changhong.sei.notify.service.ContentBuilder;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <strong>实现功能:</strong>
 * <p>发送电子邮件的API服务实现</p>
 *
 * @author 王锦光 wangj
 * @version 1.0.1 2020-01-13 14:07
 */
@RestController
@Api(value = "EmailNotifyApi", tags = "发送电子邮件的API服务")
@RequestMapping(path = "emailNotify", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class EmailNotifyController implements EmailNotifyApi {
    public final static String EMAIL_MQ_KEY = "email";
    @Autowired
    private ContentBuilder contentBuilder;
    /**
     * 注入消息队列生产者
     */
    @Autowired
    private MqProducer mqProducer;

    /**
     * 发送一封电子邮件
     *
     * @param emailMessage 电子邮件消息
     */
    @Override
    public void sendEmail(EmailMessage emailMessage) {
        // 通过模板生成内容
        contentBuilder.build(emailMessage);
        // JSON序列化
        String message = JsonUtils.toJson(emailMessage);
        mqProducer.send(EMAIL_MQ_KEY, message);
    }
}
