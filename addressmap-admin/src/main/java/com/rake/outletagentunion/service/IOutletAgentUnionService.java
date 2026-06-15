package com.rake.outletagentunion.service;
import java.util.List;
import com.rake.outletagentunion.domain.OutletAgentUnion;

    public interface IOutletAgentUnionService {
        List<OutletAgentUnion> selectOutletAgentUnionList();
        List<OutletAgentUnion> selectOutletAgentUnionListByBusinessflow(List<String> businessflows);
    }


