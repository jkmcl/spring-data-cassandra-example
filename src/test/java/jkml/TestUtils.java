package jkml;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestUtils {

	public static Path getResourceAsPath(String name) throws URISyntaxException {
		return Paths.get(TestUtils.class.getClassLoader().getResource(name).toURI());
	}

}
