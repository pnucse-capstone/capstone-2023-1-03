package pnu.cohang.cardiacrenderer.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pnu.cohang.cardiacrenderer.model.dto.CardiacSegmentation;
import pnu.cohang.cardiacrenderer.repository.CardiacSegmentationRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class CardiacDataService {
    private CardiacSegmentationRepository cardiacSegmentationRepository;

    public List<CardiacSegmentation> getData() {
        return cardiacSegmentationRepository.getDataList();
    }
}
