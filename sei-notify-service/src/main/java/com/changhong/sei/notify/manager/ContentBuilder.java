package com.changhong.sei.notify.manager;

import com.changhong.sei.notify.dto.MessageContent;
import com.changhong.sei.notify.entity.ContentTemplate;
import com.changhong.sei.notify.service.ContentTemplateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * <strong>实现功能:</strong>
 * <p>消息内容生成器</p>
 *
 * @author 王锦光 wangj
 * @version 1.0.1 2017-11-23 19:38
 */
@Component
public class ContentBuilder {
    @Autowired
    private ContentTemplateService contentTemplateService;
    private SpringTemplateEngine templateEngine;

    /**
     * 获取模板引擎
     *
     * @return 模板引擎
     */
    private SpringTemplateEngine getTemplateEngine() {
        if (null == templateEngine) {
            templateEngine = new SpringTemplateEngine();
            StringTemplateResolver templateResolver = new StringTemplateResolver();
            templateResolver.setTemplateMode(TemplateMode.HTML);
            templateEngine.setTemplateResolver(templateResolver);
        }
        return templateEngine;
    }

    /**
     * 生成消息内容
     *
     * @param content 内容
     */
    public void build(MessageContent content) {
        if (Objects.isNull(content)) {
            return;
        }
        String templateCode = content.getContentTemplateCode();
        if (StringUtils.isNotBlank(templateCode)) {
            Map<String, Object> templateParams = content.getContentTemplateParams();
            //获取模板内容
            ContentTemplate template = contentTemplateService.findByCode(templateCode);
            if (Objects.nonNull(template) && StringUtils.isNotBlank(template.getContent())) {
                String templateContent = getContent(template, templateParams);
                content.setContent(templateContent);
            }
        }
    }

    /**
     * 通过模板生成内容
     *
     * @param template       模板
     * @param templateParams 参数
     * @return 内容
     */
    public String getContent(ContentTemplate template, Map<String, Object> templateParams) {
        //通过模板生成内容
        final Context ctx = new Context(Locale.getDefault());
        if (templateParams != null && !templateParams.isEmpty()) {
            templateParams.forEach(ctx::setVariable);
        }
        return getTemplateEngine().process(template.getContent(), ctx);
    }
}
