package com.rake.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;

public class AgentRoleUtil
{
    private static final Set<String> AGENT_NAMES = new HashSet<String>(
            Arrays.asList("織田家", "豊臣家", "徳川家", "武田家", "上杉家"));

    private AgentRoleUtil()
    {
    }

    public static List<String> getAgentRoleNames(Authentication authentication)
    {
        List<String> roleNames = UserRoleUtil.getRoleListName(authentication);
        if (roleNames == null)
        {
            return Collections.emptyList();
        }
        return roleNames.stream()
                .filter(AGENT_NAMES::contains)
                .collect(Collectors.toList());
    }
}
