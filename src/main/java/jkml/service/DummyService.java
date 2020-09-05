package jkml.service;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DummyService {

	private final Logger log = LoggerFactory.getLogger(DummyService.class);

	private AtomicBoolean done = new AtomicBoolean(false);

	public boolean isWorkDone() {
		return done.get();
	}

	public void doWork() {
		log.info("Work is done");
		done.set(true);
	}

	public void undoWork() {
		log.info("Work is undone");
		done.set(false);
	}

}
