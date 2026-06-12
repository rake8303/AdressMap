package com.rake.outletagentunion.mapper;

import java.util.List;
import com.rake.outletagentunion.domain.OutletAgentUnion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OutletAgentUnionMapper {
    List<OutletAgentUnion> selectOutletAgentUnionList();
    List<OutletAgentUnion> selectOutletAgentUnionListByBusinessflow(@Param("businessflows") List<String> businessflows);
}

