package jkml.service;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Dummy app class that scans the service package only, thus avoiding creation
 * of any bean that requires Cassandra.
 * <p>
 * See: https://stackoverflow.com/questions/50992933
 */
@SpringBootApplication(scanBasePackageClasses = { DummyService.class })
public class ScanServicePackageOnlyTestApp {
}
