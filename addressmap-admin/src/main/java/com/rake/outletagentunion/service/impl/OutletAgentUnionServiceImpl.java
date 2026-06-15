
package com.rake.outletagentunion.service.impl;

import com.rake.outletagentunion.domain.OutletAgentUnion;
import com.rake.outletagentunion.mapper.OutletAgentUnionMapper;
import com.rake.outletagentunion.service.IOutletAgentUnionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Collections;
import com.rake.common.utils.StringUtils;

@Service
public class OutletAgentUnionServiceImpl implements IOutletAgentUnionService {
    @Autowired
    private OutletAgentUnionMapper outletAgentUnionMapper;

    @Override
    public List<OutletAgentUnion> selectOutletAgentUnionList() {
        return outletAgentUnionMapper.selectOutletAgentUnionList();
    }
    @Override
    public List<OutletAgentUnion> selectOutletAgentUnionListByBusinessflow(List<String> businessflows) {
        if (StringUtils.isEmpty(businessflows)) {
            return Collections.emptyList();
        }
        return outletAgentUnionMapper.selectOutletAgentUnionListByBusinessflow(businessflows);
    }
}
