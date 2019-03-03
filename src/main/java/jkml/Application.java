package jkml;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import jkml.data.User;
import jkml.data.UserRepository;

@SpringBootApplication
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);

		UserRepository repo = ctx.getBean(UserRepository.class);
		log.info("Create a single user...");
		repo.deleteAll();
		repo.save(new User(UUID.randomUUID(), "Bob", "Smith"));
		log.info("Listing all users:");
		repo.findAll().iterator().forEachRemaining(c -> {
			log.info(c.toString());
		});

		SpringApplication.exit(ctx);
	}

}
