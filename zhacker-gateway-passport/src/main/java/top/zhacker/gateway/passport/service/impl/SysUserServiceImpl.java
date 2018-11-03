package top.zhacker.gateway.passport.service.impl;

import top.zhacker.gateway.passport.domain.Result;
import top.zhacker.gateway.passport.domain.SysRole;
import top.zhacker.gateway.passport.domain.SysUser;
import top.zhacker.gateway.passport.dto.UserDto;
import top.zhacker.gateway.passport.enums.ResultEnum;
import top.zhacker.gateway.passport.properties.SecurityConstants;
import top.zhacker.gateway.passport.repository.SysRoleRepository;
import top.zhacker.gateway.passport.repository.SysUserRepository;
import top.zhacker.gateway.passport.service.SysUserService;
import top.zhacker.gateway.passport.utils.ResultUtil;
import top.zhacker.gateway.passport.properties.SecurityConstants;
import top.zhacker.gateway.passport.repository.SysRoleRepository;
import top.zhacker.gateway.passport.repository.SysUserRepository;
import top.zhacker.gateway.passport.service.SysUserService;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import freemarker.template.utility.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2018/2/1 0001.
 *
 * @author zlf
 * @email i@merryyou.cn
 * @since 1.0
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private SysUserRepository userRepository;

    @Autowired
    private SysRoleRepository roleRepository;

    @Override
    public SysUser save(SysUser user) {
        user.setDelFlag("0");
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public SysUser findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<UserDto> findAll() {
        List<UserDto> userDtoList = new ArrayList<>();
        UserDto userDto;
        List<SysUser> userList = userRepository.findAll();
        for (SysUser user : userList) {
            userDto = new UserDto();
            BeanUtils.copyProperties(user, userDto);
            userDtoList.add(userDto);
        }
        return userDtoList;
    }

    @Override
    public UserDto findOne(String id) {

        SysUser sysUser = userRepository.findOne(id);
        if (sysUser != null) {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(sysUser, userDto);
            List<SysRole> roleList = sysUser.getRoles();
            String roles = "";
            if (roleList != null && roleList.size() > 0) {
                for (SysRole role : roleList) {
                    roles += role.getId() + ",";
                }
            }
            userDto.setRoles(roles);
            return userDto;
        }
        return null;
    }

    @Override
    public Result save(String data) {
        List<UserDto> userDtoList = new Gson().fromJson(data,
                new TypeToken<List<UserDto>>() {
                }.getType());
        UserDto userDto = userDtoList.get(0);
        String roles = userDto.getRoles();
        if (StringUtils.isEmpty(roles)) return ResultUtil.error(ResultEnum.FAIL.getCode(), "请选择该用户角色！");
        List<SysRole> roleList = new ArrayList<>();
        SysRole sysRole;
        for (String str : roles.split(",")) {
            sysRole = roleRepository.findOne(str);
            roleList.add(sysRole);
        }
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(userDto, sysUser);
        sysUser.setPassword(bCryptPasswordEncoder.encode(SecurityConstants.DEFAULT_PASSWORD));
        sysUser.getRoles().addAll(roleList);
        SysUser user = userRepository.save(sysUser);
        BeanUtils.copyProperties(user, userDto);
        return ResultUtil.success("用户保存成功！");
    }

    @Override
    public Result<String> delUsers(String ids) {
        if (StringUtils.isEmpty(ids)) return ResultUtil.error(ResultEnum.FAIL.getCode(), "请选择要删除的行！");
        String[] userIds = ids.split(",");
        for (String id : userIds) {
            userRepository.delete(id);
        }
        return ResultUtil.success("删除成功！");
    }
}
