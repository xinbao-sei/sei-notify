package com.changhong.sei.notify.apitest;

import com.changhong.com.sei.core.test.BaseUnitTest;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.util.JsonUtils;
import com.changhong.sei.notify.api.ContentTemplateService;
import com.changhong.sei.notify.dto.ContentTemplateDto;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * <strong>实现功能:</strong>
 * <p></p>
 *
 * @author 王锦光 wangj
 * @version 1.0.1 2020-01-14 9:25
 */
public class ContentTemplateApiTest extends BaseUnitTest {
    @Autowired
    private ContentTemplateService contentTemplateService;

    @Test
    public void findByCode(){
        String code = "EMAIL_TEMPLATE_REGIST";
        ResultData<ContentTemplateDto> resultData = contentTemplateService.findByCode(code);
        System.out.println(JsonUtils.toJson(resultData));
        Assert.assertTrue(resultData.isSuccessful());
    }

    @Test
    public void findOne(){
        String id = "27B77612-3090-11EA-A16E-080058000005";
        ResultData<ContentTemplateDto> resultData = contentTemplateService.findOne(id);
        System.out.println(JsonUtils.toJson(resultData));
        Assert.assertTrue(resultData.isSuccessful());
    }

    @Test
    public void findAll(){
        ResultData<List<ContentTemplateDto>> resultData = contentTemplateService.findAll();
        System.out.println(JsonUtils.toJson(resultData));
        Assert.assertTrue(resultData.isSuccessful());
    }
}