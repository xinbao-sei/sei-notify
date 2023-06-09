package com.changhong.sei.notify.manager.miniapp;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.log.LogUtil;
import com.changhong.sei.core.util.JsonUtils;
import com.changhong.sei.notify.dto.*;
import com.changhong.sei.notify.entity.MessageHistory;
import com.changhong.sei.notify.manager.NotifyManager;
import com.changhong.sei.notify.service.MessageHistoryService;
import com.changhong.sei.notify.service.UserMiniAppPushTimeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 实现功能：
 *
 * @author 马超(Vision.Mac)
 * @version 1.0.00  2020-05-24 23:37
 */
public class MiniAppManager implements NotifyManager {

    private static final Logger LOG = LoggerFactory.getLogger(MiniAppManager.class);

    @Autowired(required = false)
    private WxMaService wxService;
    @Autowired
    private UserMiniAppPushTimeService userMiniAppPushTimeService;
    @Autowired
    private MessageHistoryService historyService;

    /**
     * 发送消息通知
     *
     * @param message 发送的消息
     */
    @Override
    public ResultData<String> send(SendMessage message) {
        LOG.info("收到消息[{}],开始执行发送微信小程序提醒消息", message);
        if (Objects.isNull(wxService)) {
            LOG.error("后台未配置微信小程序消息发送功能");
            return ResultData.fail("后台未配置微信小程序消息发送功能");
        }

        boolean success = Boolean.TRUE;
        String log = "success";
        String content = message.getContent();
        MessageHistory history;
        List<MessageHistory> histories = new ArrayList<>();

        //收件人清单
        List<UserNotifyInfo> receivers = message.getReceivers();
        try {
            //格式化模板内容
            MiniAppTemplate template = JsonUtils.fromJson(content, MiniAppTemplate.class);
            message.setSubject(template.getTitle());
            List<MiniAppTemplateParam> params = template.getData();
            StringBuilder sb = new StringBuilder();
            for (MiniAppTemplateParam param : params) {
                sb.append(param.getDesc()).append(":").append(param.getValue()).append("<br>");
            }
            content = sb.toString();
            if (CollectionUtils.isNotEmpty(receivers)) {
                //获得内容
                WxMaSubscribeMessage wxMaSubscribeMessage = JsonUtils.fromJson(message.getContent(), WxMaSubscribeMessage.class);
                //循环推送消息
                for (UserNotifyInfo receiver : receivers) {
                    history = new MessageHistory();
                    history.setCategory(NotifyType.MiniApp);
                    history.setSubject(StringUtils.isNotBlank(message.getSubject()) ? message.getSubject() : StringUtils.left(message.getContent(), 100));
                    history.setTargetType(TargetType.PERSONAL);
                    history.setTargetValue(receiver.getMiniProgramOpenId());
                    history.setTargetName(receiver.getUserName());

                    if (StringUtils.isNotEmpty(receiver.getMiniProgramOpenId())) {
                        wxMaSubscribeMessage.setToUser(receiver.getMiniProgramOpenId());
                        //推送消息
                        wxService.getMsgService().sendSubscribeMsg(wxMaSubscribeMessage);
                        //更新推送次数-1
                        userMiniAppPushTimeService.subtract(receiver, receiver.getMiniProgramOpenId());
                    } else {
                        //TODO 新增查询消息发送记录
                        LOG.error("用户[{}]需要发送微信小程序提醒消息,所绑定的openid为空未发送", receiver.getUserAccount());
                        history.setSendLog("用户[" + receiver.getUserAccount() + "]未绑定的openid");
                        history.setSendStatus(Boolean.FALSE);
                    }

                    histories.add(history);
                }
                LOG.info("微信小程序提醒消息发送成功");
                return ResultData.success("微信小程序提醒消息发送成功");
            } else {
                success = Boolean.FALSE;
                log = "消息接收人不能为空.";

                return ResultData.fail(log);
            }
        } catch (Exception e) {
            success = Boolean.FALSE;
            log = e.getMessage();

            LOG.error("发送微信小程序提醒消息异常", e);
            return ResultData.fail("发送微信小程序提醒消息异常:" + e.getMessage());
        } finally {
            try {
                historyService.recordHistory(histories, content, success, log, null);
            } catch (Exception e) {
                LogUtil.error("记录消息历史异常", e);
            }
        }
    }
}
