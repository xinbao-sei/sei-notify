package com.changhong.sei.notify.sdk.manager;

import com.changhong.sei.notify.sdk.common.HttpClientResult;
import com.changhong.sei.notify.sdk.common.HttpClientUtils;
import com.changhong.sei.notify.sdk.dto.NotifyMessage;
import com.changhong.sei.notify.sdk.dto.NotifyType;
import com.ecmp.util.JsonUtils;
import com.ecmp.vo.ResponseData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.*;

/**
 * *************************************************************************************************
 * <p/>
 * 实现功能：文档管理器
 * <p>
 * ------------------------------------------------------------------------------------------------
 * 版本          变更时间             变更人                     变更原因
 * ------------------------------------------------------------------------------------------------
 * 1.0.00      2017-07-11 16:47      王锦光(wangj)                新建
 * <p/>
 * *************************************************************************************************
 */
@SuppressWarnings("rawtypes")
public class NotifyManager implements ApplicationContextAware {
    private static final Logger LOG = LoggerFactory.getLogger(NotifyManager.class);

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    private String getNotifyServiceUrl() {
        return context.getEnvironment().getProperty("sei.notify.service.url", "http://10.4.208.86:20001/sei-notify");
    }

    private String getBasicServiceUrl() {
        return context.getEnvironment().getProperty("sei.basic.service.url", "http://10.4.208.86:20004/sei-basic");
    }

    private Map<String, String> getHeaders() {
        Map<String, String> header = new HashMap<>();
        // 设置请求头
//		header.put("Cookie", "");
//		header.put("Connection", "keep-alive");
        header.put("Accept", "application/json");
        header.put("Content-Type", "application/json;charset=UTF-8");
//		header.put("Accept-Language", "zh-CN,zh;q=0.9");
//		header.put("Accept-Encoding", "gzip, deflate, br");
//		header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");

        return header;
    }

    /**
     * 发送平台消息通知
     * 目前支持: NotifyType.EMAIL - 邮件, NotifyType.SMS - 短信, NotifyType.SEI_REMIND - 站内提醒(默认)
     *
     * @param message 发送的通知消息
     */
    public ResponseData<String> send(NotifyMessage message) {
        if (CollectionUtils.isEmpty(message.getReceiverIds())) {
            return ResponseData.operationFailure("接收人不能为空.");
        }

        if (StringUtils.isBlank(message.getContent()) && StringUtils.isBlank(message.getContentTemplateCode())) {
            return ResponseData.operationFailure("消息内容不能为空.");
        }

        List<NotifyType> notifyTypes = message.getNotifyTypes();
        if (Objects.isNull(notifyTypes)) {
            notifyTypes = new ArrayList<>();
            notifyTypes.add(NotifyType.SEI_REMIND);
        }
        message.setNotifyTypes(notifyTypes);

        ResponseData<String> resultData;
        HttpClientResult result;
        try {
            result = HttpClientUtils.doPostJson(getNotifyServiceUrl() + "/notify/send", JsonUtils.toJson(message));
            resultData = JsonUtils.fromJson(result.getContent(), ResponseData.class);
        } catch (Exception e) {
            LOG.error("发送平台消息通知异常", e);
            resultData = ResponseData.operationFailure("发送平台消息通知异常");
        }

        return resultData;
    }

    /**
     * 按群组获取接收者
     */
    public ResponseData<List<String>> getReceiverIdsByGroup(String groupCode) {
        Map<String, String> params = new HashMap<>();
        params.put("groupCode", groupCode);

        ResponseData<List<String>> resultData;
        HttpClientResult result;
        try {
            result = HttpClientUtils.doGet(getNotifyServiceUrl() + "/group/getUserIdsByGroup", getHeaders(), params);
            resultData = JsonUtils.fromJson(result.getContent(), ResponseData.class);
        } catch (Exception e) {
            LOG.error("发送平台消息通知异常", e);
            resultData = ResponseData.operationFailure("发送平台消息通知异常");
        }
        return resultData;
    }

    /**
     * 按岗位获取接收者
     */
    public ResponseData<List<String>> getReceiverIdsByPosition(String orgCode, String positionCode) {
        Map<String, String> params = new HashMap<>();
        params.put("orgCode", orgCode);
        params.put("positionCode", positionCode);

        ResponseData<List<String>> resultData;
        HttpClientResult result;
        try {
            result = HttpClientUtils.doGet(getBasicServiceUrl() + "/position/getUserIdsByOrgCodePositionCode", getHeaders(), params);
            resultData = JsonUtils.fromJson(result.getContent(), ResponseData.class);
        } catch (Exception e) {
            LOG.error("发送平台消息通知异常", e);
            resultData = ResponseData.operationFailure("发送平台消息通知异常");
        }
        return resultData;
    }

    /**
     * 按角色获取接收者
     */
    public ResponseData<List<String>> getReceiverIdsByRole(String featureRoleCode) {
        Map<String, String> params = new HashMap<>();
        params.put("featureRoleCodes", featureRoleCode);

        ResponseData<List<String>> resultData;
        HttpClientResult result;
        try {
            result = HttpClientUtils.doGet(getBasicServiceUrl() + "/featureRole/getUserIdsByFeatureRole", getHeaders(), params);
            resultData = JsonUtils.fromJson(result.getContent(), ResponseData.class);
        } catch (Exception e) {
            LOG.error("发送平台消息通知异常", e);
            resultData = ResponseData.operationFailure("发送平台消息通知异常");
        }
        return resultData;
    }
}