package caimi.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
	
	private static final Logger logger = LoggerFactory.getLogger(Application.class);
	
	public static void main(String[] args) {
		
		logger.info("caimi web is startting...");
		
		SpringApplication.run(Application.class, args);
		initServices();
	}

	private static void initServices() {
		
	}
}
