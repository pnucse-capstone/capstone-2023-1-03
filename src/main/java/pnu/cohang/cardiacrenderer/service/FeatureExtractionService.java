package pnu.cohang.cardiacrenderer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import pnu.cohang.cardiacrenderer.model.dto.CardiacSegmentation;
import pnu.cohang.cardiacrenderer.repository.CardiacSegmentationRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Controller
@Slf4j
public class FeatureExtractionService {
    @Value("${python.lib.path}")
    private String pythonLibPath;

    private final CardiacSegmentationRepository segmentationRepository;

    public FeatureExtractionService(CardiacSegmentationRepository segmentationRepository) {
        this.segmentationRepository = segmentationRepository;
    }

    public void featureExtraction(String patientNumber) {
        try {
            String featureExtractionPath = pythonLibPath + "/step2-feature-extraction/feature_extraction.py";
            log.info("feature-extraction ... featureExtractionPath : {}", featureExtractionPath);

            Runtime runtime = Runtime.getRuntime();

            Process process = runtime.exec("python " + featureExtractionPath + " " + patientNumber, null, new File(pythonLibPath + "/step2-feature-extraction"));
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

    public void addResultToDB(String patientNumber) throws IOException {
        String resultCsvPath = pythonLibPath + "/step2-feature-extraction-res/" + patientNumber + "_to_db.csv";

        String result = Files.readString(Paths.get(resultCsvPath));

        String dataRow = result.split("\n")[1];

        String[] data =  dataRow.split(",");

        CardiacSegmentation cardiacSegmentation = new CardiacSegmentation();

        cardiacSegmentation.setName(patientNumber);
        cardiacSegmentation.setED_vol_LV(Float.parseFloat(data[1]));
        cardiacSegmentation.setED_vol_RV(Float.parseFloat(data[2]));
        cardiacSegmentation.setEF_LV(Float.parseFloat(data[3]));
        cardiacSegmentation.setEF_RV(Float.parseFloat(data[4]));
        cardiacSegmentation.setED_max_MTH(Float.parseFloat(data[5]));

        if(segmentationRepository.getDataFromName(patientNumber) == null) {
            log.info("insertData : {}", cardiacSegmentation);
            segmentationRepository.insertData(cardiacSegmentation);
        } else {
            log.info("updateData : {}", cardiacSegmentation);
            segmentationRepository.updateData(cardiacSegmentation);
        }
    }

    public void setDB() throws IOException {
        String resultCsvPath = pythonLibPath + "/step2-feature-extraction-train-res (1).csv";

        String result = Files.readString(Paths.get(resultCsvPath));

        String[] dataRows = result.split("\n");

        for(int i = 0; i < dataRows.length; i++) {
            if(i == 0)
                continue;

            String[] data =  dataRows[i].split(",");

            CardiacSegmentation cardiacSegmentation = new CardiacSegmentation();

            cardiacSegmentation.setName(data[0]);
            cardiacSegmentation.setED_vol_LV(Float.parseFloat(data[1]));
            cardiacSegmentation.setED_vol_RV(Float.parseFloat(data[2]));
            cardiacSegmentation.setEF_LV(Float.parseFloat(data[3]));
            cardiacSegmentation.setEF_RV(Float.parseFloat(data[4]));
            cardiacSegmentation.setED_max_MTH(Float.parseFloat(data[5]));
            cardiacSegmentation.setDiseaseGroup(data[6]);

            if(segmentationRepository.getDataFromName(data[0]) == null) {
                log.info("insertData : {}", cardiacSegmentation);
                segmentationRepository.insertData(cardiacSegmentation);
            } else {
                log.info("updateData : {}", cardiacSegmentation);
                segmentationRepository.updateData(cardiacSegmentation);
            }
        }


    }
}
