package jkml.service;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Dummy app class that disables Cassandra auto-config and excludes packages
 * requiring Cassandra
 * <p>
 * See: https://stackoverflow.com/questions/50992933
 */
@SpringBootApplication(scanBasePackageClasses = { DummyService.class })
public class ScanServicePackageOnlyTestApp {
}
