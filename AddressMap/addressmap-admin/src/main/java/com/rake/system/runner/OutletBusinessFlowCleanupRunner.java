package com.rake.system.runner;

import com.rake.system.mapper.TabOutletMapper;
import com.rake.utils.AgentRoleUtil;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutletBusinessFlowCleanupRunner implements ApplicationRunner
{
    private static final Logger log = LoggerFactory.getLogger(OutletBusinessFlowCleanupRunner.class);

    private final TabOutletMapper tabOutletMapper;

    public OutletBusinessFlowCleanupRunner(TabOutletMapper tabOutletMapper)
    {
        this.tabOutletMapper = tabOutletMapper;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args)
    {
        List<String> outletIds = tabOutletMapper.selectOutletIdsWithMultipleAgents();
        if (outletIds == null || outletIds.isEmpty())
        {
            return;
        }

        int cleanedCount = 0;
        for (String outletId : outletIds)
        {
            List<String> primaryAgent = AgentRoleUtil.keepPrimaryBusinessFlow(
                    tabOutletMapper.selectOutletAgentNames(outletId));
            if (primaryAgent.isEmpty())
            {
                continue;
            }
            tabOutletMapper.deleteOutletAgentsByOutletId(outletId);
            tabOutletMapper.batchOutletAgents(outletId, primaryAgent);
            cleanedCount++;
        }

        if (cleanedCount > 0)
        {
            log.info("Cleaned duplicate outlet business flows for {} outlets", cleanedCount);
        }
    }
}
