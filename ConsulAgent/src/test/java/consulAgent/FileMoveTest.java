package consulAgent;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.junit.Test;

public class FileMoveTest {
	@Test
	public void testFilesMoveMethod() throws IOException{
		Path source = Paths.get("src/test/resources/volumes");
		Path target = Paths.get("src/test/resources/volume");
		Files.move(source, target,StandardCopyOption.REPLACE_EXISTING);
	}
}
