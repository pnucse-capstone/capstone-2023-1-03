package pnu.cohang.cardiacrenderer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.io.*;

@Controller
@Slf4j
public class FeatureExtractionService {
    @Value("${python.lib.path}")
    private String pythonLibPath;

    public void featureExtraction() {
        try {
            String featureExtractionPath = pythonLibPath + "/step2-feature-extraction/feature_extraction.py";
            log.info("feature-extraction ... featureExtractionPath : {}", featureExtractionPath);

            Runtime runtime = Runtime.getRuntime();

            Process process = runtime.exec("python " + featureExtractionPath, null, new File(pythonLibPath + "/step2-feature-extraction"));
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
}
