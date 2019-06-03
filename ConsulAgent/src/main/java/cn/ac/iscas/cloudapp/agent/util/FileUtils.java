package cn.ac.iscas.cloudapp.agent.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class FileUtils {
	
	private static SimpleFileVisitor<Path> deleteVisitor = new SimpleFileVisitor<Path>(){
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			Files.delete(file);
			return FileVisitResult.CONTINUE;
		}
		
		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
			Files.delete(dir);
			return FileVisitResult.CONTINUE;
		}
	};
	
	public static void deleteFiles(Path targetPath) throws IOException{
		Files.walkFileTree(targetPath, deleteVisitor);
	}
}
