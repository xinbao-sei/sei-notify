package com.changhong.sei.notify.service.cust;

import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.dto.serach.PageResult;
import com.changhong.sei.core.dto.serach.Search;
import com.changhong.sei.notify.dto.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 根据岗位的id获取已分配的员工
     * @param positionId 岗位id
     * @return  员工列表
     */
    ResultData<List<EmployeeDto>> getEmployeesByPositionId(String positionId);

    /**
     * 分页查询岗位实体
     *
     * @param search 查询参数
     * @return 分页查询结果
     */
    ResultData<PageResult<PositionDto>> findPositionByPage(Search search);

    /**
     * 根据功能角色的id获取已分配的用户
     *
     * @param featureRoleId 功能角色的id
     * @return 用户清单
     */
    ResultData<List<UserDto>> getUserByFeatureRole(String featureRoleId);
}
