package pnu.cohang.cardiacrenderer.repository.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pnu.cohang.cardiacrenderer.model.dto.CardiacSegmentation;

import java.util.List;

@Mapper
public interface CardiacSegmentationMapper {
    List<CardiacSegmentation> getDataList();

    CardiacSegmentation getDataFromName(String patientName);

    void insertData(@Param("data") CardiacSegmentation data);

    void updateData(@Param("data") CardiacSegmentation data);
}
