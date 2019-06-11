package com.macro.mall.tiny.controller;

import com.macro.mall.tiny.common.api.CommonPage;
import com.macro.mall.tiny.common.api.CommonResult;
import com.macro.mall.tiny.dto.UmsAdminLoginParam;
import com.macro.mall.tiny.dto.UmsAdminParam;
import com.macro.mall.tiny.mbg.model.UmsAdmin;
import com.macro.mall.tiny.mbg.model.UmsRole;
import com.macro.mall.tiny.service.UmsAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@Api(tags = "UmsAdminController", description = "后台用户管理")
@RestController
@RequestMapping("/admin")
public class UmAdminController {

    @Autowired
    private UmsAdminService adminService;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    /**
     * 用户注册接口
     *
     * @param umsAdminParam
     * @param result
     * @return
     */
    @ApiOperation(value = "用户注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public CommonResult<UmsAdmin> register(@RequestBody UmsAdminParam umsAdminParam, BindingResult result) {
        UmsAdmin umsAdmin = adminService.register(umsAdminParam);
        if (umsAdmin == null) {
            CommonResult.failed();
        }
        return CommonResult.success(umsAdmin);
    }

    /**
     * 用户登录并且返回JWT的接口
     *
     * @param umsAdminLoginParam
     * @param result
     * @return
     */
    @ApiOperation(value = "登录以后返回token")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public CommonResult login(@RequestBody UmsAdminLoginParam umsAdminLoginParam,
                              BindingResult result) {
        //登录成功由Springsecurity帮我们生成token
        String token = adminService.login(umsAdminLoginParam.getUsername(), umsAdminLoginParam.getPassword());
        if (token == null) {
            //返回的是参数验证失败的代码
            return CommonResult.validateFailed("用户名或密码错误");
        }
        HashMap<String, String> tokenMap = new HashMap<>();
        //这里新建一个Map封装我们的Token和tokenHead
        tokenMap.put("token", token);
        //tokenHead是在yaml中注入进来的
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }

    /**
     * 只能续期,不能无效化之前的Token
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "刷新Token")
    @RequestMapping(value = "/refreshToken", method = RequestMethod.POST)
    public CommonResult refreshToken(HttpServletRequest request) {
        //从请求头Authorization中获取JWT的内容
        //后端自定义请求头会有跨域问题,springsecurity可以解决
        String token = request.getHeader(tokenHeader);
        String refreshToken = adminService.refreshToken(token);
        //如果不能刷新Token
        if (refreshToken == null) {
            return CommonResult.failed("无法更新Token");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", refreshToken);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }

    /**
     * 通过security框架获取用户信息
     * 我们在jwt拦截器中已经通过jwt将用户信息给了security,
     * 现在security将其封装到了一个沙箱中,而实体就是principal
     * 我们在Controller层传入Principal会自动帮我们传入带壳的用户信息
     * 或者从jwt的Authentication认证信息中获取也行
     *
     * @param principal
     * @return
     */
    @ApiOperation(value = "获取当前登录用户信息")
    @GetMapping("/info")
    public CommonResult getAdminInfo(Principal principal) {
        String username = principal.getName();
        UmsAdmin umsadmin = adminService.getAdminByUsername(username);
        HashMap<String, Object> data = new HashMap<>();
        data.put("username", umsadmin.getUsername());
        data.put("rols", new String[]{"TEST"});
        data.put("icon", umsadmin.getIcon());
        return CommonResult.success(data);
    }

    /**
     * security框架的登出是由框架实现的,当前端点击退出就可以销毁
     *
     * @return
     */
    @ApiOperation(value = "登出功能")
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public CommonResult logout() {
        return CommonResult.success(null);
    }

    /**
     * 根据姓名或昵称,模糊分页查询获取用户列表
     *
     * @param name
     * @param pageSize
     * @param pageNum
     * @return
     */
    public CommonResult<CommonPage<UmsAdmin>> list(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<UmsAdmin> list = adminService.list(name, pageSize, pageNum);
        //给list,就能封装成分页对象
        return CommonResult.success(CommonPage.restPage(list));
    }


    /**
     * 根据id查询用户的全部信息
     *
     * @param id
     * @return
     */
    @ApiOperation("获取指定用户信息")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public CommonResult<UmsAdmin> getItem(@PathVariable Long id) {
        UmsAdmin admin = adminService.getItem(id);
        return CommonResult.success(admin);
    }

    /**
     * 根据用户id,(非全)更新用户信息(直接更新的策略)
     *
     * @param id
     * @param admin
     * @return
     */
    @ApiOperation("修改指定用户信息")
    @PostMapping("/update/{id}")
    public CommonResult update(@PathVariable Long id,
                               @RequestBody UmsAdmin admin
    ) {
        //判断更新后是否成功
        int count = adminService.update(id, admin);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed("更新用户信息失败");
    }

    /**
     * 删除用户指定信息
     *
     * @param id
     * @return
     */
    @ApiOperation("删除用户指定信息")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult delete(@PathVariable Long id) {
        int count = adminService.delete(id);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }


    /**
     * 根据adminId和roleIds修改用户的一个或多个角色,在用户角色中间表中(先删除,后添加的策略)
     * 注意:使用声明式事务的方法里不能抓异常,否则,事务无效,而且只能抛出RunTimeException
     * 这里使用先删除后添加的原因是关系映射表中用户可能有多个角色,无法确认具体更新哪里角色,所以无法使用update直接更新
     * @param adminId
     * @param roleIds
     * @return
     */
    @ApiOperation("给用户分配角色")
    @RequestMapping(value = "/role/update", method = RequestMethod.POST)
    public CommonResult updateRole(
            @RequestParam("adminId") Long adminId,
            @RequestParam("roleIds") List<Long> roleIds
    ) {
        //判断
        int count = adminService.updateRole(adminId, roleIds);
        //只有用户真的修改了角色,才会返回大于0,执行失败回滚,或者用户没有修改角色都是小于0
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed("修改用户角色失败");
    }

    /**
     * 根据用户id获取用户的所有角色,多表连接,左联,自己写Dao接口
     * @param adminId
     * @return
     */
    @ApiOperation("获取指定用户的角色")
    @RequestMapping(value = "/role/{adminId}",method = RequestMethod.GET)
    public CommonResult<List<UmsRole>> getRoleList(@PathVariable Long adminId){
        List<UmsRole> roleList = adminService.getRoleList(adminId);
        return CommonResult.success(roleList);
    }

    /**
     * 给指定用户更新自定义+-权限(角色和权限映射表不用更新,那个是自带的暂时,,什么角色有什么权限什么的)
     * @param adminId
     * @param permissionIds
     * @return
     */
    @ApiOperation("给用户分配+-权限")
    @RequestMapping(value = "/permission/update", method = RequestMethod.POST)
    public CommonResult updatePermission(
            @RequestParam Long adminId,
            @RequestParam("permissionIds") List<Long> permissionIds
    ){
                adminService.
    }

}
