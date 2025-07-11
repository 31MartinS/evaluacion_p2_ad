package ms_health_analyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MsHealthAnalyzerApplication {
	public static void main(String[] args) {
		SpringApplication.run(MsHealthAnalyzerApplication.class, args);
	}
}
