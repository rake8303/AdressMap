package com.rake.utils;

import com.rake.common.core.domain.entity.SysRole;
import com.rake.common.core.domain.model.LoginUser;
import com.rake.common.utils.StringUtils;
import java.util.ArrayList;
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
    public static final String ODA = "織田家";
    public static final String TOYOTOMI = "豊臣家";
    public static final String TOKUGAWA = "徳川家";
    public static final String TAKEDA = "武田家";
    public static final String UESUGI = "上杉家";

    private static final List<String> ORDERED_AGENT_NAMES = Collections.unmodifiableList(Arrays.asList(
            ODA, TOYOTOMI, TOKUGAWA, TAKEDA, UESUGI));

    private static final Set<String> AGENT_NAMES = new HashSet<String>(ORDERED_AGENT_NAMES);

    private static final Set<String> ALL_DATA_USERS = new HashSet<String>(
            Arrays.asList("user1", "user2"));

    private static final Set<String> ALL_DATA_ROLE_KEYS = new HashSet<String>(
            Arrays.asList("admin", "common", "readonly"));

    private static final Map<String, String> AGENT_NAME_ALIASES = new HashMap<String, String>();

    static
    {
        registerAlias("XSOL", ODA);
        registerAlias("DMM", TOYOTOMI);
        registerAlias("WWB", TOKUGAWA);
        registerAlias("高島", TAKEDA);
        registerAlias("韓華", UESUGI);
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

    public static String getCurrentBusinessFlow(Authentication authentication)
    {
        List<String> businessFlows = getBusinessFlowNames(authentication);
        return businessFlows.isEmpty() ? "" : businessFlows.get(0);
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

    public static List<String> sortBusinessFlows(Collection<String> flowNames)
    {
        if (flowNames == null || flowNames.isEmpty())
        {
            return Collections.emptyList();
        }

        Set<String> normalizedNames = new HashSet<String>();
        for (String flowName : flowNames)
        {
            String normalized = normalizeBusinessFlowName(flowName);
            if (AGENT_NAMES.contains(normalized))
            {
                normalizedNames.add(normalized);
            }
        }

        List<String> ordered = new ArrayList<String>();
        for (String orderedName : ORDERED_AGENT_NAMES)
        {
            if (normalizedNames.remove(orderedName))
            {
                ordered.add(orderedName);
            }
        }

        List<String> remaining = new ArrayList<String>(normalizedNames);
        Collections.sort(remaining);
        ordered.addAll(remaining);
        return ordered;
    }

    public static List<String> keepPrimaryBusinessFlow(Collection<String> flowNames)
    {
        List<String> ordered = sortBusinessFlows(flowNames);
        if (ordered.isEmpty())
        {
            return Collections.emptyList();
        }
        return Collections.singletonList(ordered.get(0));
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
