package com.changhong.sei.notify.service;

import com.changhong.sei.core.dao.BaseEntityDao;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.log.LogUtil;
import com.changhong.sei.core.service.BaseEntityService;
import com.changhong.sei.edm.sdk.DocumentManager;
import com.changhong.sei.notify.dao.MessageHistoryDao;
import com.changhong.sei.notify.entity.ContentBody;
import com.changhong.sei.notify.entity.MessageHistory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;


/**
 * 消息历史(MessageHistory)业务逻辑实现类
 *
 * @author sei
 * @since 2020-06-11 14:36:17
 */
@Service
public class MessageHistoryService extends BaseEntityService<MessageHistory> {
    @Autowired
    private MessageHistoryDao dao;
    @Autowired
    private ContentBodyService contentBodyService;
    @Autowired(required = false)
    private DocumentManager documentManager;

    @Override
    protected BaseEntityDao<MessageHistory> getDao() {
        return dao;
    }

    @Async
    @Transactional(rollbackFor = Exception.class)
    public ResultData<String> recordHistory(List<MessageHistory> histories, String content, boolean success, String log, Set<String> docIds) {
        LogUtil.debug("记录消息历史内容:{0}", content);
        if (CollectionUtils.isEmpty(histories)) {
            // 消息发送历史记录失败,历史记录不能为空
            return ResultData.fail("00021");
        }
        String contentId;
        if (StringUtils.isNotBlank(content)) {
            ContentBody contentBody = new ContentBody(content);
            contentBodyService.save(contentBody);

            contentId = contentBody.getId();
        } else {
            LogUtil.error("记录消息历史失败, 消息内容不能为空.");
            return ResultData.fail("消息内容不存在.");
        }
        LocalDateTime now = LocalDateTime.now();
        for (MessageHistory history : histories) {
            history.setContentId(contentId);
            history.setSendDate(now);
            if (Objects.isNull(history.getSendStatus())) {
                history.setSendStatus(success);
            }
            if (Objects.isNull(history.getSendLog())) {
                history.setSendLog(log);
            }
        }
        dao.save(histories);

        //绑定附件  注意:这里是通过内容id关联附件
        if (CollectionUtils.isNotEmpty(docIds) && Objects.nonNull(documentManager)) {
            documentManager.bindBusinessDocuments(contentId, docIds);
        }

        return ResultData.success("ok");
    }

    /**
     * 用户查看
     *
     * @param id 消息id
     * @return 返回明细
     */
    public ResultData<MessageHistory> detail(String id) {
        if (StringUtils.isNotBlank(id)) {
            MessageHistory message = dao.findOne(id);
            if (Objects.nonNull(message)) {
                String contentId = message.getContentId();
                //加载内容
                if (StringUtils.isNotBlank(contentId)) {
                    ContentBody body = contentBodyService.findOne(contentId);
                    if (Objects.nonNull(body)) {
                        message.setContent(body.getContent());
                    }
                }
                return ResultData.success(message);
            } else {
                return ResultData.fail(id + " - 消息不存在");
            }
        } else {
            return ResultData.fail("参数不能为空!");
        }
    }
}