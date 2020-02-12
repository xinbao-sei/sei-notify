package com.changhong.sei.notify.config;

import com.changhong.sei.core.context.ContextUtil;
import com.changhong.sei.core.context.SessionUser;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.test.BaseUnitTest;
import com.changhong.sei.core.util.JsonUtils;
import com.changhong.sei.core.utils.ResultDataUtil;
import com.changhong.sei.notify.dto.MessageCategory;
import com.changhong.sei.notify.dto.Priority;
import com.chonghong.sei.util.EnumUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * <strong>实现功能:</strong>
 * <p></p>
 *
 * @author 王锦光 wangj
 * @version 1.0.1 2019-12-31 20:32
 */
public class ContextUtilTest extends BaseUnitTest {

    @Test
    public void getMessage(){
        String message = ContextUtil.getMessage("core_service_00003", "tes-001");
        Assert.assertNotNull(message);
        System.out.println("core_service_00003="+message);
        message = ContextUtil.getMessage("00001");
        Assert.assertNotNull(message);
        System.out.println("00001="+message);
    }

    @Test
    public void getSessionUser(){
        SessionUser sessionUser = ContextUtil.getSessionUser();
        Assert.assertNotNull(sessionUser);
        System.out.println(JsonUtils.toJson(sessionUser));
    }

    @Test
    public void getEnumMap(){
        Map<String, String> map = EnumUtils.getEnumMap(Priority.class);
        System.out.println(JsonUtils.toJson(map));
    }

    @Test
    public void getEnumEntities(){
        ResultData<List<EnumUtils.EnumEntity>> resultData = ResultDataUtil.getEnumEntities(Priority.class);
        System.out.println(JsonUtils.toJson(resultData));
    }
}