package aero.cubox.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.TimeZone;

@Slf4j
@SpringBootApplication
public class CuboxAcsApiApplication {

	public static void main(String[] args) throws UnknownHostException {

		SpringApplication app = new SpringApplication(CuboxAcsApiApplication.class);

		ApplicationContext context = app.run(args);

		Environment env = context.getEnvironment();

		log.info("\n----------------------------------------------------------\n\t"
						+ "Application '{}' is running! Access URLs:\n\t"
						+ "Local: \t\thttp://localhost:{}\n\t"
						+ "External: \thttp://{}:{}"
						+ "\n----------------------------------------------------------"
				, env.getProperty("spring.application.name")
				, env.getProperty("server.port")
				, InetAddress.getLocalHost().getHostAddress()
				, env.getProperty("server.port"));
	}

	@PostConstruct
	public void started(){
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

}
