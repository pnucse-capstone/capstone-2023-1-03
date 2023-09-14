package pnu.cohang.cardiacrenderer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pnu.cohang.cardiacrenderer.model.dto.CardiacSegmentation;
import pnu.cohang.cardiacrenderer.repository.CardiacSegmentationRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@Slf4j
public class ClassificationService {
    @Value("${python.lib.path}")
    private String pythonLibPath;

    private final CardiacSegmentationRepository segmentationRepository;

    public ClassificationService(CardiacSegmentationRepository segmentationRepository) {
        this.segmentationRepository = segmentationRepository;
    }

    public void classification(String patientNumber) {
        try {
            String classificationPath = pythonLibPath + "/step3-classification/classification.py";
            log.info("classification ... classificationPath : {}", classificationPath);

            Runtime runtime = Runtime.getRuntime();

            Process process = runtime.exec("python " + classificationPath + " " + patientNumber, null, new File(pythonLibPath + "/step3-classification"));
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;

            while ((line = br.readLine()) != null) {
                log.info(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateResultToDB(String patientNumber) throws IOException {
        String resultCsvPath = pythonLibPath + "/step3-classification-res/" + patientNumber + ".txt";

        String result = Files.readString(Paths.get(resultCsvPath));

        String diseaseGroup = result.split(" ")[1].split("\n")[0];

        CardiacSegmentation data = segmentationRepository.getDataFromName(patientNumber);

        data.setDiseaseGroup(diseaseGroup);

        log.info("update disease_group : {}, Data : {}", diseaseGroup, data);
        segmentationRepository.updateData(data);
    }

    public CardiacSegmentation getClassificationInfo(String patientNumber) {
        return segmentationRepository.getDataFromName(patientNumber);
    }
}
