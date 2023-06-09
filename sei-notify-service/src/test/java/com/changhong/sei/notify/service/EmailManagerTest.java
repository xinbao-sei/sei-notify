package com.changhong.sei.notify.service;

import com.changhong.sei.core.test.BaseUnitTest;
import com.changhong.sei.notify.dto.EmailAccount;
import com.changhong.sei.notify.dto.EmailMessage;
import com.changhong.sei.notify.manager.email.EmailManager;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <strong>实现功能:</strong>
 * <p></p>
 *
 * @author 王锦光 wangj
 * @version 1.0.1 2020-01-10 9:07
 */
public class EmailManagerTest extends BaseUnitTest {
    @Autowired
    private EmailManager emailService;

    /**
     * 生成消息
     * @return 消息
     */
    private EmailMessage builderMessage(){
        EmailMessage message = new EmailMessage();
        message.setSubject("Test 测试发送邮件");
        message.setSender(new EmailAccount("王锦光","wangjg@changhong.com"));
        message.setContent("Test 测试邮件内容");
        //测试模板
        // message.setContentTemplateCode("EMAIL_TEMPLATE_REGIST");
        Map<String,Object> params = new HashMap<>();
        params.put("userName","宝宝");
        params.put("account","baobao");
        params.put("password","123456");
        // message.setContentTemplateParams(params);
        List<EmailAccount> receivers = new ArrayList<>();
        receivers.add(new EmailAccount("MAC","87540704@qq.com"));
        message.setReceivers(receivers);
        // 设置消息内容;
        // contentBuilder.build(message);
        return message;
    }

    @Test
    public void send() {
        emailService.send(builderMessage());
    }
}