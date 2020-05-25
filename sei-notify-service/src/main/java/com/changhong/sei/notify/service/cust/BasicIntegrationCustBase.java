package com.changhong.sei.notify.service.cust;

import com.changhong.sei.apitemplate.ApiTemplate;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.dto.serach.PageResult;
import com.changhong.sei.core.dto.serach.Search;
import com.changhong.sei.notify.dto.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
public class BasicIntegrationCustBase implements BasicIntegration {
    @Value("${sei.auth.server-name:sei-auth}")
    private String authServiceName;

    @Value("${sei.basic.server-name:sei-basic}")
    private String basicServiceName;

    @Autowired
    private ApiTemplate apiTemplate;

    /**
     * 分页查询业务实体
     *
     * @param search 查询参数
     * @return 分页查询结果
     */
    @Override
    public ResultData<PageResult<AccountResponse>> findAccountByPage(Search search) {
        return apiTemplate.postByAppModuleCode(authServiceName, "/account/findByPage", new ParameterizedTypeReference<ResultData<PageResult<AccountResponse>>>() {
        }, search);
//        return accountClient.findByPage(search);
    }

    /**
     * 获取用户的组织机构代码清单
     *
     * @param userId 用户Id
     * @return 组织机构代码清单
     */
    @Override
    public ResultData<List<String>> getEmployeeOrgCodes(String userId) {
        Map<String, String> params = new HashMap<>();
        params.put("userId", userId);
        return apiTemplate.getByAppModuleCode(basicServiceName, "/employee/getEmployeeOrgCodes", new ParameterizedTypeReference<ResultData<List<String>>>() {
        }, params);
//        return employeeClient.getEmployeeOrgCodes(userId);
    }

    /**
     * 获取用户的岗位代码清单
     *
     * @param userId 用户Id
     * @return 岗位代码清单
     */
    @Override
    public ResultData<List<String>> getEmployeePositionCodes(String userId) {
        Map<String, String> params = new HashMap<>();
        params.put("userId", userId);
        return apiTemplate.getByAppModuleCode(basicServiceName, "/employee/getEmployeePositionCodes", new ParameterizedTypeReference<ResultData<List<String>>>() {
        }, params);
//        return employeeClient.getEmployeePositionCodes(userId);
    }

    /**
     * 获取当前用户有权限的树形组织实体清单
     *
     * @param featureCode 功能项代码
     * @return 有权限的树形组织实体清单
     */
    @Override
    public ResultData<List<OrganizationDto>> getUserAuthorizedTreeEntities(String featureCode) {
        Map<String, String> params = new HashMap<>();
        params.put("featureCode", featureCode);
        return apiTemplate.getByAppModuleCode(basicServiceName, "/organization/getUserAuthorizedTreeEntities", new ParameterizedTypeReference<ResultData<List<OrganizationDto>>>() {
        }, params);
//        return organizationClient.getUserAuthorizedTreeEntities(featureCode);
    }

    /**
     * 通过用户Id清单获取用户信息
     *
     * @param userIds 用户Id清单
     * @return 用户信息
     */
    @Override
    public ResultData<List<UserNotifyInfo>> findNotifyInfoByUserIds(List<String> userIds) {
        return apiTemplate.postByAppModuleCode(basicServiceName, "/userProfile/findNotifyInfoByUserIds", new ParameterizedTypeReference<ResultData<List<UserNotifyInfo>>>() {
        }, userIds);
//        return userNotifyInfoClient.findNotifyInfoByUserIds(userIds);
    }

    /**
     * 根据岗位的id获取已分配的员工
     * @param positionId 岗位id
     * @return  员工列表
     */
    @Override
    public ResultData<List<EmployeeDto>> getEmployeesByPositionId(String positionId) {
        Map<String, String> params = new HashMap<>();
        params.put("positionId", positionId);
        return apiTemplate.getByAppModuleCode(basicServiceName, "/position/listAllAssignedEmployeesByPositionId", new ParameterizedTypeReference<ResultData<List<EmployeeDto>>>() {
        }, params);
    }

    /**
     * 分页查询岗位实体
     *
     * @param search 查询参数
     * @return 分页查询结果
     */
    @Override
    public ResultData<PageResult<PositionDto>> findPositionByPage(Search search) {
        return apiTemplate.postByAppModuleCode(basicServiceName, "/position/findByPage", new ParameterizedTypeReference<ResultData<PageResult<PositionDto>>>() {
        }, search);
    }

    /**
     * 根据功能角色的id获取已分配的用户
     *
     * @param featureRoleId 功能角色的id
     * @return 用户清单
     */
    @Override
    public ResultData<List<UserDto>> getUserByFeatureRole(String featureRoleId) {
        Map<String, String> params = new HashMap<>();
        params.put("featureRoleId", featureRoleId);
        return apiTemplate.getByAppModuleCode(basicServiceName, "/featureRole/getAssignedEmployeesByFeatureRole", new ParameterizedTypeReference<ResultData<List<UserDto>>>() {
        }, params);
    }

}
