package pnu.cohang.cardiacrenderer.controller;

import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pnu.cohang.cardiacrenderer.model.dto.CardiacSegmentation;
import pnu.cohang.cardiacrenderer.service.CardiacDataService;

import java.io.IOException;
import java.util.List;

@RequestMapping("/api/v1/data")
@RestController
@AllArgsConstructor
public class MriDataController {
    private CardiacDataService cardiacDataService;

    @GetMapping
    public List<CardiacSegmentation> getData() {
        return cardiacDataService.getData();
    }
}
