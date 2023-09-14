package pnu.cohang.cardiacrenderer.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pnu.cohang.cardiacrenderer.model.dto.CardiacSegmentation;
import pnu.cohang.cardiacrenderer.repository.mapper.CardiacSegmentationMapper;

import java.util.List;

@Repository
@Slf4j
@MapperScan("pnu.cohang.cardiacrenderer.repository.mapper")
@AllArgsConstructor
public class CardiacSegmentationRepository {
    private CardiacSegmentationMapper cardiacSegmentationMapper;

    public List<CardiacSegmentation> getDataList() {
        return cardiacSegmentationMapper.getDataList();
    }

    public CardiacSegmentation getDataFromName(String patientName) {
        return cardiacSegmentationMapper.getDataFromName(patientName);
    }

    public void insertData(CardiacSegmentation data) {
        cardiacSegmentationMapper.insertData(data);
    }

    public void updateData(CardiacSegmentation data) {
        cardiacSegmentationMapper.updateData(data);
    }
}
