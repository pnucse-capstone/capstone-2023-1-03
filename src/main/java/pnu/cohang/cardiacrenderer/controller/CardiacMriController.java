package pnu.cohang.cardiacrenderer.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pnu.cohang.cardiacrenderer.service.ClassificationService;
import pnu.cohang.cardiacrenderer.service.FeatureExtractionService;
import pnu.cohang.cardiacrenderer.service.FileService;
import pnu.cohang.cardiacrenderer.service.SegmentationService;

import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/api/v1/cardiac")
public class CardiacMriController {
    @Autowired
    public SegmentationService segmentationService;

    @Autowired
    public FeatureExtractionService featureExtractionService;

    @Autowired
    public ClassificationService classificationService;

    @Autowired
    public FileService fileService;

    @PostMapping("/upload")
    public void uploadMri(@RequestPart MultipartFile patientZip) throws IOException {
        fileService.uploadFile(patientZip);

        fileService.unzipFile(patientZip.getOriginalFilename());
    }

    @PostMapping("/segmentation")
    public void segmentation() {
        segmentationService.segmentation();
    }

    @PostMapping("/feature-extraction")
    public void featureExtraction() {
        featureExtractionService.featureExtraction();
    }

    @PostMapping("/classification")
    public void classification() {
        classificationService.classification();
    }
}
