package com.rake.utils;

import com.rake.common.core.domain.entity.SysRole;
import com.rake.common.core.domain.model.LoginUser;
import com.rake.common.utils.SecurityUtils;
import com.rake.common.utils.StringUtils;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.security.core.Authentication;

public class AgentRoleUtil
{
    private static final Set<String> AGENT_NAMES = new HashSet<String>(
            Arrays.asList("織田家", "豊臣家", "徳川家", "武田家", "上杉家"));

    private static final Set<String> ALL_DATA_USERS = new HashSet<String>(
            Arrays.asList("user1", "user2"));

    private static final Set<String> ALL_DATA_ROLE_KEYS = new HashSet<String>(
            Arrays.asList("admin", "common", "readonly"));

    private static final Map<String, String> AGENT_NAME_ALIASES = new HashMap<String, String>();

    static
    {
        registerAlias("XSOL", "織田家");
        registerAlias("DMM", "豊臣家");
        registerAlias("WWB", "徳川家");
        registerAlias("高島", "武田家");
        registerAlias("韓華", "上杉家");
        for (String agentName : AGENT_NAMES)
        {
            registerAlias(agentName, agentName);
        }
    }

    private AgentRoleUtil()
    {
    }

    public static boolean canViewAllData(Authentication authentication)
    {
        LoginUser loginUser = getLoginUser(authentication);
        if (loginUser == null || loginUser.getUser() == null)
        {
            return false;
        }
        if (ALL_DATA_USERS.contains(StringUtils.trim(loginUser.getUsername())))
        {
            return true;
        }
        if (loginUser.getUser().isAdmin())
        {
            return true;
        }
        List<SysRole> roles = loginUser.getRoles();
        if (roles == null || roles.isEmpty())
        {
            return false;
        }
        return roles.stream().anyMatch(role -> role != null
                && (role.isAdmin() || ALL_DATA_ROLE_KEYS.contains(StringUtils.trim(role.getRoleKey()))));
    }

    public static List<String> getBusinessFlowNames(Authentication authentication)
    {
        if (canViewAllData(authentication))
        {
            return Collections.emptyList();
        }
        LoginUser loginUser = getLoginUser(authentication);
        if (loginUser == null || loginUser.getUser() == null)
        {
            return Collections.emptyList();
        }
        List<SysRole> roles = loginUser.getRoles();
        if (roles == null || roles.isEmpty())
        {
            return Collections.emptyList();
        }
        for (SysRole role : roles)
        {
            if (role == null)
            {
                continue;
            }
            for (String flowName : Arrays.asList(role.getRoleName(), role.getRoleKey()))
            {
                List<String> resolvedBusinessFlows = resolveBusinessFlowNames(flowName);
                if (!resolvedBusinessFlows.isEmpty())
                {
                    return Collections.singletonList(resolvedBusinessFlows.get(0));
                }
            }
        }
        return Collections.emptyList();
    }

    public static List<String> getAgentRoleNames(Authentication authentication)
    {
        return getBusinessFlowNames(authentication);
    }

    public static boolean hasOutletAccess(Authentication authentication, Collection<String> outletAgents)
    {
        if (canViewAllData(authentication))
        {
            return true;
        }
        List<String> businessFlows = getBusinessFlowNames(authentication);
        if (businessFlows.isEmpty() || outletAgents == null || outletAgents.isEmpty())
        {
            return false;
        }
        for (String flow : businessFlows)
        {
            if (outletAgents.contains(flow))
            {
                return true;
            }
        }
        return false;
    }

    public static String normalizeBusinessFlowName(String flowName)
    {
        if (flowName == null)
        {
            return "";
        }
        String trimmed = StringUtils.trim(flowName);
        String normalized = AGENT_NAME_ALIASES.get(trimmed);
        return normalized != null ? normalized : trimmed;
    }

    private static List<String> resolveBusinessFlowNames(String flowName)
    {
        if (StringUtils.isBlank(flowName))
        {
            return Collections.emptyList();
        }
        String trimmed = StringUtils.trim(flowName);
        String normalized = normalizeBusinessFlowName(trimmed);
        if (AGENT_NAMES.contains(normalized))
        {
            if (normalized.equals(trimmed))
            {
                return Collections.singletonList(normalized);
            }
            return Arrays.asList(normalized, trimmed);
        }
        if (AGENT_NAME_ALIASES.containsKey(trimmed))
        {
            return Collections.singletonList(trimmed);
        }
        return Collections.emptyList();
    }

    private static LoginUser getLoginUser(Authentication authentication)
    {
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser))
        {
            return null;
        }
        return (LoginUser) authentication.getPrincipal();
    }

    private static void registerAlias(String source, String target)
    {
        AGENT_NAME_ALIASES.put(source, target);
    }
}
