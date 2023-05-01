package vsb.fei.voctionary;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VoctionaryApplication {

	static Logger logger = LoggerFactory.getLogger(VoctionaryApplication.class);
	
	public static void main(String[] args) throws IOException, InterruptedException {
		SpringApplication.run(VoctionaryApplication.class, args); 
	}

}
