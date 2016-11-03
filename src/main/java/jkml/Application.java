package jkml;

import java.util.Arrays;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);

		String[] beanNames = ctx.getBeanDefinitionNames();
		Arrays.sort(beanNames);
		log.debug("Beans loaded:");
		for (String beanName : beanNames) {
			log.debug(beanName);
		}
		
		CustomerRepository repo = ctx.getBean(CustomerRepository.class);
		log.info("Create a single customer...");
		repo.deleteAll();
		repo.save(new Customer(UUID.randomUUID(), "Bob", "Smith"));
		log.info("Listing all customers:");
		repo.findAll().iterator().forEachRemaining(c -> {
			log.info(c.toString());
		});
		
		SpringApplication.exit(ctx);
	}

}
