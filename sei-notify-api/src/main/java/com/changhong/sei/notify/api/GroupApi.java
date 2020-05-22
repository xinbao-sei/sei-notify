package com.changhong.sei.notify.api;

import com.changhong.sei.core.api.BaseEntityApi;
import com.changhong.sei.core.api.FindAllApi;
import com.changhong.sei.core.dto.ResultData;
import com.changhong.sei.core.dto.serach.PageResult;
import com.changhong.sei.core.dto.serach.Search;
import com.changhong.sei.notify.dto.GroupDto;
import com.changhong.sei.notify.dto.GroupUserDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * 群组(Group)API
 *
 * @author sei
 * @since 2020-05-22 11:04:28
 */
@Valid
@FeignClient(name = "sei-notify", path = "group")
public interface GroupApi extends BaseEntityApi<GroupDto>, FindAllApi<GroupDto> {

    /**
     * 屏蔽删除接口
     */
    @Override
    ResultData<String> delete(String id);

    /**
     * 冻结群组
     *
     * @param ids 群组id集合
     * @return 操作结果
     */
    @PostMapping(path = "frozen")
    @ApiOperation(value = "冻结群组", notes = "冻结群组")
    ResultData<String> frozen(@RequestBody List<String> ids);

    /**
     * 解冻群组
     *
     * @param ids 群组id集合
     * @return 操作结果
     */
    @PostMapping(path = "unfrozen")
    @ApiOperation(value = "解冻群组", notes = "解冻群组")
    ResultData<String> unfrozen(@RequestBody List<String> ids);

    /**
     * 添加群组用户
     *
     * @param groupUserDtos 群组用户集合
     * @return 操作结果
     */
    @PostMapping(path = "addGroupUser")
    @ApiOperation(value = "添加群组用户", notes = "添加群组用户")
    ResultData<String> addGroupUser(@RequestBody @Valid List<GroupUserDto> groupUserDtos);

    /**
     * 移除群组用户
     *
     * @param groupUserIds 群组用户id集合
     * @return 操作结果
     */
    @PostMapping(path = "removeGroupUser")
    @ApiOperation(value = "移除群组用户", notes = "移除群组用户")
    ResultData<String> removeGroupUser(@RequestBody List<String> groupUserIds);

    /**
     * 获取指定群组用户
     *
     * @param groupId 群组id
     * @return 返回指定群组用户对象
     */
    @GetMapping(path = "getGroupUsers")
    @ApiOperation(value = "获取指定群组用户", notes = "获取指定群组用户")
    ResultData<List<GroupUserDto>> getGroupUsers(@RequestParam("groupId") String groupId);

    /**
     * 获取用户账号分页数据
     */
    @PostMapping(path = "getUserAccounts")
    @ApiOperation(value = "获取用户账号分页数据", notes = "获取用户账号分页数据")
    ResultData<PageResult<GroupUserDto>> getUserAccounts(Search search);
}