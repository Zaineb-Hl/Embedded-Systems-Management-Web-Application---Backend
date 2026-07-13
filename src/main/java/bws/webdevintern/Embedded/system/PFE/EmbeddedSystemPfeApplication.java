package bws.webdevintern.Embedded.system.PFE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Active le job SensorDataPersistenceJob (@Scheduled)

public class EmbeddedSystemPfeApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmbeddedSystemPfeApplication.class, args);
	}

}
