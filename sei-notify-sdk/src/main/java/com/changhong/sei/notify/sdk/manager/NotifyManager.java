package com.changhong.sei.notify.sdk.manager;

import com.changhong.sei.apitemplate.ApiTemplate;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.notify.dto.EmailMessage;
import com.changhong.sei.notify.dto.NotifyMessage;
import com.changhong.sei.notify.dto.NotifyType;
import com.changhong.sei.notify.dto.SmsMessage;
import com.changhong.sei.notify.sdk.client.BasicClient;
import com.changhong.sei.notify.sdk.client.NotifyClient;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 实现功能：
 *
 * @author 马超(Vision.Mac)
 * @version 1.0.00  2020-04-20 22:42
 */
public class NotifyManager/* implements ApplicationContextAware*/ {

    // private final ApiTemplate apiTemplate;
    private final NotifyClient notifyClient;
    private final BasicClient basicClient;

    public NotifyManager(NotifyClient notifyClient, BasicClient basicClient) {
        this.notifyClient = notifyClient;
        this.basicClient = basicClient;
    }

    // private ApplicationContext context;
    //
    // @Override
    // public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    //     this.context = applicationContext;
    // }

    // private String getNotifyServiceUrl() {
    //     String host = context.getEnvironment().getProperty("sei.notify.service.url");
    //     if (StringUtils.isBlank(host)) {
    //         throw new IllegalArgumentException("消息服务地址没有配置[sei.notify.service.url]");
    //     }
    //     return host;
    // }

    // private String getBasicServiceUrl() {
    //     String host = context.getEnvironment().getProperty("sei.basic.service.url");
    //     if (StringUtils.isBlank(host)) {
    //         throw new IllegalArgumentException("消息服务地址没有配置[sei.notify.service.url]");
    //     }
    //     return host;
    // }

    /**
     * 发送平台消息通知
     * 目前支持: NotifyType.SEI_BULLETIN - 通告, NotifyType.EMAIL - 邮件, NotifyType.SMS - 短信, NotifyType.SEI_REMIND - 站内提醒(默认)
     *
     * @param message 发送的通知消息
     */
    public ResultData<String> send(NotifyMessage message) {
        if (StringUtils.isBlank(message.getContent()) && StringUtils.isBlank(message.getContentTemplateCode())) {
            return ResultData.fail("消息内容不能为空.");
        }

        List<NotifyType> notifyTypes = message.getNotifyTypes();
        if (Objects.isNull(notifyTypes)) {
            notifyTypes = new ArrayList<>();
            notifyTypes.add(NotifyType.SEI_REMIND);
        }

        for (NotifyType notifyType : notifyTypes) {
            // 只有通告,可以没有接受人
            if (NotifyType.SEI_BULLETIN != notifyType && CollectionUtils.isEmpty(message.getReceiverIds())) {
                return ResultData.fail("接收人不能为空.");
            }
        }

        message.setNotifyTypes(notifyTypes);

        ResultData<String> resultData = notifyClient.send(message);
        // resultData = apiTemplate.postByUrl(getNotifyServiceUrl() + "/notify/send",
        //         new ParameterizedTypeReference<ResultData<String>>() {
        //         }, message);
        return resultData;
    }

    /**
     * 发送平台短信通知
     */
    public ResultData<String> sendSms(SmsMessage message) {
        if (CollectionUtils.isEmpty(message.getPhoneNums())) {
            return ResultData.fail("接收人不能为空.");
        }

        if (StringUtils.isBlank(message.getContent()) && StringUtils.isBlank(message.getContentTemplateCode())) {
            return ResultData.fail("消息内容不能为空.");
        }

        ResultData<String> resultData = notifyClient.sendSms(message);
        // resultData = apiTemplate.postByUrl(getNotifyServiceUrl() + "/notify/sendSms",
        //         new ParameterizedTypeReference<ResultData<String>>() {
        //         }, message);
        return resultData;
    }

    /**
     * 发送一封电子邮件
     */
    public ResultData<String> sendEmail(EmailMessage message) {
        if (CollectionUtils.isEmpty(message.getReceivers())) {
            return ResultData.fail("接收人不能为空.");
        }

        if (StringUtils.isBlank(message.getContent()) && StringUtils.isBlank(message.getContentTemplateCode())) {
            return ResultData.fail("消息内容不能为空.");
        }

        ResultData<String> resultData = notifyClient.sendEmail(message);
        // resultData = apiTemplate.postByUrl(getNotifyServiceUrl() + "/notify/sendEmail",
        //         new ParameterizedTypeReference<ResultData<String>>() {
        //         }, message);
        return resultData;
    }

    /**
     * 按群组获取接收者
     */
    public ResultData<List<String>> getReceiverIdsByGroup(String groupCode) {
        // Map<String, String> params = new HashMap<>();
        // params.put("groupCode", groupCode);
        // return apiTemplate.getByUrl(getNotifyServiceUrl() + "/group/getUserIdsByGroup", new ParameterizedTypeReference<ResultData<List<String>>>() {
        // }, params);
        return notifyClient.getUserIdsByGroup(groupCode);
    }

    /**
     * 按岗位获取接收者
     */
    public ResultData<List<String>> getReceiverIdsByPosition(String orgCode, String positionCode) {
        // Map<String, String> params = new HashMap<>();
        // params.put("orgCode", orgCode);
        // params.put("positionCodes", positionCode);
        // return apiTemplate.getByUrl(getBasicServiceUrl() + "/position/getUserIdsByOrgCodePositionCode", new ParameterizedTypeReference<ResultData<List<String>>>() {
        // }, params);
        return basicClient.getUserIdsByOrgCodePositionCode(orgCode, positionCode);
    }

    /**
     * 按角色获取接收者
     */
    public ResultData<List<String>> getReceiverIdsByRole(String featureRoleCode) {
        // Map<String, String> params = new HashMap<>();
        // params.put("featureRoleCodes", featureRoleCode);
        // return apiTemplate.getByUrl(getBasicServiceUrl() + "/featureRole/getUserIdsByFeatureRole", new ParameterizedTypeReference<ResultData<List<String>>>() {
        // }, params);
        return basicClient.getUserIdsByFeatureRole(featureRoleCode);
    }
}
