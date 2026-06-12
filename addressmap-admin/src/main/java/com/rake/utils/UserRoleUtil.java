package com.rake.utils;

import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.rake.common.core.domain.model.LoginUser;
import org.springframework.security.core.Authentication;
import java.util.List;

import java.util.Collection;

@Service
public class UserRoleUtil {

    public static Collection<? extends GrantedAuthority> getCurrentUserRoles() {
        // 获取当前用户的认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            // 获取用户的角色信息
            return authentication.getAuthorities();
        }

        return null;
    }


    public static List<String> getRoleListName(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        List<String> roleNames = null;
        if (principal instanceof LoginUser) {
            LoginUser loginUser = (LoginUser) principal;

            // 获取角色名称列表
            roleNames = loginUser.getRoleNames();

            // 打印角色信息
            roleNames.forEach(roleName -> {
                System.out.println("角色名称: " + roleName);
            });
        }

        return roleNames;
    }
}
