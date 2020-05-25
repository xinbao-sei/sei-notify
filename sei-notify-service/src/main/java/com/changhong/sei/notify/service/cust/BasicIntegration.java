package com.changhong.sei.notify.service.cust;

import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.dto.serach.PageResult;
import com.changhong.sei.core.dto.serach.Search;
import com.changhong.sei.notify.dto.OrganizationDto;
import com.changhong.sei.notify.dto.UserNotifyInfo;
import com.changhong.sei.notify.dto.AccountResponse;

import java.util.List;

/**
 * 实现功能：sei基础应用(basic)集成
 *
 * @author 马超(Vision.Mac)
 * @version 1.0.00  2020-05-25 06:37
 */
public interface BasicIntegration {
    /**
     * 分页查询业务实体
     *
     * @param search 查询参数
     * @return 分页查询结果
     */
    ResultData<PageResult<AccountResponse>> findAccountByPage(Search search);

    /**
     * 获取用户的组织机构代码清单
     *
     * @param userId 用户Id
     * @return 组织机构代码清单
     */
    ResultData<List<String>> getEmployeeOrgCodes(String userId);

    /**
     * 获取用户的岗位代码清单
     *
     * @param userId 用户Id
     * @return 岗位代码清单
     */
    ResultData<List<String>> getEmployeePositionCodes(String userId);

    /**
     * 获取当前用户有权限的树形组织实体清单
     *
     * @param featureCode 功能项代码
     * @return 有权限的树形组织实体清单
     */
    ResultData<List<OrganizationDto>> getUserAuthorizedTreeEntities(String featureCode);

    /**
     * 通过用户Id清单获取用户信息
     *
     * @param userIds 用户Id清单
     * @return 用户信息
     */
    ResultData<List<UserNotifyInfo>> findNotifyInfoByUserIds(List<String> userIds);

}