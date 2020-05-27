package com.changhong.sei.notify.sdk;

import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.test.BaseUnitTest;
import com.changhong.sei.notify.dto.NotifyMessage;
import com.changhong.sei.notify.dto.NotifyType;
import com.changhong.sei.notify.sdk.manager.NotifyManager;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 实现功能：
 *
 * @author 马超(Vision.Mac)
 * @version 1.0.00  2020-05-25 00:30
 */
public class NotifyManagerTest extends BaseUnitTest {

    @Autowired
    private NotifyManager notifyManager;

    @Test
    public void send() {
        NotifyMessage message = new NotifyMessage();
        message.setSubject("测试");
        message.setContent("测试内容");

        message.addReceiverId("B54E8964-D14D-11E8-A64B-0242C0A8441B");
        message.addNotifyType(NotifyType.SEI_REMIND);

        ResultData<String> result = notifyManager.send(message);
        System.out.println(result);
    }

    @Test
    public void getReceiverIdsByGroup() {
        ResultData<List<String>> result = notifyManager.getReceiverIdsByGroup("user");
        System.out.println(result);
    }
}