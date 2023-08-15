package pnu.cohang.cardiacrenderer.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pnu.cohang.cardiacrenderer.service.SegmentationService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

@RestController
@Slf4j
@RequestMapping("/api/v1/segmentation")
public class SegmentationController {
    @Autowired
    public SegmentationService segmentationService;

    @PostMapping("/test")
    public void test() {
        segmentationService.segmentation();
    }
}
