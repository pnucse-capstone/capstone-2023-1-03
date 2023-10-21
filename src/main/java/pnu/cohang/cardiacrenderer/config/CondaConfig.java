package pnu.cohang.cardiacrenderer.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CondaConfig {
    @Value("${python.lib.path}")
    private String pythonLibPath;
    @Bean
    public void CondaConfig() throws IOException {
        log.info("conda activate classifierVenv");

//        Runtime.getRuntime().exec("bash " + pythonLibPath + "/run_in_conda.sh");

        log.info("conda activate finished");
    }
}
