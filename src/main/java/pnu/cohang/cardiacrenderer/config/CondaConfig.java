package pnu.cohang.cardiacrenderer.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CondaConfig {
    @Bean
    public void CondaConfig() throws IOException {
        log.info("conda activate classifierVenv");

        Runtime.getRuntime().exec("conda activate classifierVenv");

        log.info("conda activate finished");
    }
}
