package pnu.cohang.cardiacrenderer.controller;

import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pnu.cohang.cardiacrenderer.model.FileResource;
import pnu.cohang.cardiacrenderer.service.ClassificationService;
import pnu.cohang.cardiacrenderer.service.FeatureExtractionService;
import pnu.cohang.cardiacrenderer.service.FileService;
import pnu.cohang.cardiacrenderer.service.SegmentationService;

import java.io.File;
import java.io.FileNotFoundException;
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
    public ResponseEntity<Resource> uploadMri(@RequestPart MultipartFile patientZip) throws IOException {
        fileService.uploadFile(patientZip);

        fileService.unzipFile(patientZip.getOriginalFilename());

        FileResource resource = fileService.getNiiFile(patientZip.getOriginalFilename().split("\\.")[0]);

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFile().getName());
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        return ResponseEntity.ok()
                .headers(header)
                .contentLength(resource.getFile().length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource.getInputStreamResource());
    }

    @GetMapping("/download/{patientNumber}")
    public ResponseEntity<Resource> download(@PathVariable String patientNumber) throws FileNotFoundException {
        log.info("download mri ... patientNumber : {}", patientNumber);

        FileResource resource = fileService.getNiiFile(patientNumber.split("\\.")[0]);

        HttpHeaders header = new HttpHeaders();
//        header.add(HttpHeaders.CONTENT_ENCODING, "gzip");
        header.add(HttpHeaders.CONNECTION, "keep-alive");
        header.add(HttpHeaders.ACCEPT_RANGES, "bytes");
//        header.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
//        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFile().getName());
//        header.add("Cache-Control", "max-age=600");
//        header.add("Pragma", "no-cache");
//        header.add("Expires", "0");


        return ResponseEntity.ok()
                .headers(header)
                .contentLength(resource.getFile().length())
                .contentType(MediaType.parseMediaType("application/gzip"))
                .body(resource.getInputStreamResource());
    }

    @PostMapping("/segmentation/{patientNumber}")
    public void segmentation(@PathVariable String patientNumber) {
        segmentationService.segmentation(patientNumber);
    }

    @PostMapping("/feature-extraction/{patientNumber}")
    public void featureExtraction(@PathVariable String patientNumber) {
        featureExtractionService.featureExtraction(patientNumber);
    }

    @PostMapping("/classification/{patientNumber}")
    public void classification(@PathVariable String patientNumber) {
        classificationService.classification(patientNumber);
    }
}
