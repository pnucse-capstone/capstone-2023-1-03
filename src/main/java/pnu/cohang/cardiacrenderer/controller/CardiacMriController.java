package pnu.cohang.cardiacrenderer.controller;

import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
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
import pnu.cohang.cardiacrenderer.model.dto.CardiacSegmentation;
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
@AllArgsConstructor
public class CardiacMriController {
    public SegmentationService segmentationService;
    public FeatureExtractionService featureExtractionService;
    public ClassificationService classificationService;
    public FileService fileService;

    @PostMapping("/upload")
    public String uploadMri(@RequestPart MultipartFile patientZip) throws IOException {
        log.info("upload zip ... fileName : {}", patientZip.getOriginalFilename());

        fileService.uploadFile(patientZip);

        fileService.unzipFile(patientZip.getOriginalFilename());

        return patientZip.getOriginalFilename().split("\\.")[0];
    }

    @GetMapping("/download-origin/{patientNumber}")
    public ResponseEntity<Resource> downloadOrigin(@PathVariable String patientNumber) throws FileNotFoundException {
        log.info("download mri ... patientNumber : {}", patientNumber);

        FileResource resource = fileService.getNiiOriginFile(patientNumber.split("\\.")[0]);

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONNECTION, "keep-alive");
        header.add(HttpHeaders.ACCEPT_RANGES, "bytes");

        return ResponseEntity.ok()
                .headers(header)
                .contentLength(resource.getFile().length())
                .contentType(MediaType.parseMediaType("application/gzip"))
                .body(resource.getInputStreamResource());
    }

    @GetMapping("/download-segmentation/{patientNumber}/{classNumber}")
    public ResponseEntity<Resource> downloadSegmentation(@PathVariable String patientNumber, @PathVariable String classNumber) throws FileNotFoundException {
        log.info("download segmentation mri ... patientNumber : {}, classNumber : {}", patientNumber, classNumber);

        FileResource resource = fileService.getNiiSegmentationFile(patientNumber.split("\\.")[0], classNumber);

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONNECTION, "keep-alive");
        header.add(HttpHeaders.ACCEPT_RANGES, "bytes");

        return ResponseEntity.ok()
                .headers(header)
                .contentLength(resource.getFile().length())
                .contentType(MediaType.parseMediaType("application/gzip"))
                .body(resource.getInputStreamResource());
    }

    @GetMapping("/download-info/{patientNumber}")
    public ResponseEntity<Resource> downloadSegmentation(@PathVariable String patientNumber) throws FileNotFoundException {
        log.info("download mri info ... patientNumber : {}", patientNumber);

        FileResource resource = fileService.getInfoFile(patientNumber);

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONNECTION, "keep-alive");
        header.add(HttpHeaders.ACCEPT_RANGES, "bytes");

        return ResponseEntity.ok()
                .headers(header)
                .contentLength(resource.getFile().length())
                .contentType(MediaType.parseMediaType("text/plain"))
                .body(resource.getInputStreamResource());
    }

    @PostMapping("/segmentation/{patientNumber}")
    public void segmentation(@PathVariable String patientNumber) {
        segmentationService.segmentation(patientNumber);
        segmentationService.segmentationResultSplit(patientNumber);
    }

    @PostMapping("/feature-extraction/{patientNumber}")
    public void featureExtraction(@PathVariable String patientNumber) throws IOException {
        featureExtractionService.featureExtraction(patientNumber);
        featureExtractionService.addResultToDB(patientNumber);
    }

    @PostMapping("/classification/{patientNumber}")
    public void classification(@PathVariable String patientNumber) throws IOException {
        classificationService.classification(patientNumber);
        classificationService.updateResultToDB(patientNumber);
    }

    @PostMapping("/set-db")
    public void setDB() throws IOException {
        featureExtractionService.setDB();
    }

    @GetMapping("/classification-info/{patientNumber}")
    public CardiacSegmentation getClassificationInfo(@PathVariable String patientNumber) throws IOException {
        return classificationService.getClassificationInfo(patientNumber);
    }
}
