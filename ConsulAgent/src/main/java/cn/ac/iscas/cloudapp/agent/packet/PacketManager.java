package cn.ac.iscas.cloudapp.agent.packet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import cn.ac.iscas.cloudapp.agent.util.FileUtils;
import cn.ac.iscas.cloudapp.agent.util.YamlUtils;
import cn.ac.iscas.cloudeploy.v2.packet.Packet;
import cn.ac.iscas.cloudeploy.v2.packet.Packet.PacketBuilder;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

@Component
public class PacketManager {
	private static final Logger logger = LoggerFactory.getLogger(PacketManager.class);
	private static final String SPECIFICFILENAME = "specificFile.yaml";
	private static final String SCRIPTSFOLDER = "scripts";
	private static final String PACKETSPECIFIC = "packet.yaml";
	
	private Path dirPath;
	@Autowired
	public PacketManager(@Value("#{configs['packet_path']}") String path){
		dirPath = Paths.get(path);
	}
	
	private Optional<Path> downloadPacket(String urlString) throws MalformedURLException, IOException{
		URL url = new URL(urlString);
		Path targetDir = Files.createTempDirectory(dirPath, "tmpPackets");
		Path target = Files.createTempFile(targetDir, "packet_", ".zip");
		Files.copy(url.openStream(), target,StandardCopyOption.REPLACE_EXISTING);
		logger.info("successfully download packet form " + urlString + " to path: " + target);
		return Optional.of(target);
	}
	
	/**
	 * 
	 * @param zipFilePath
	 * @param applicationName
	 * @throws IOException
	 * @throws ZipException
	 */
	private Optional<Path> unzipPacketFile(Path zipFilePath) throws IOException, ZipException{
		Path tempUnzipPath = Files.createTempDirectory(zipFilePath.getParent(), "unzip_");
		
		ZipFile zFile = new ZipFile(zipFilePath.toFile());
		if(!zFile.isValidZipFile()){
			throw new ZipException("packet file: "+ zipFilePath +" is not valid zip file");
		}
		zFile.extractAll(tempUnzipPath.toString());
		Path packetSpePath = tempUnzipPath.resolve(PACKETSPECIFIC);
		Packet packet = YamlUtils.loadAs(packetSpePath, Packet.class);
		if(packet == null){
			throw new IOException("can't find packet specific file");
		}
		Path targetDirPath = dirPath.resolve(packet.getAppName());
		if(Files.exists(targetDirPath)){
			FileUtils.deleteFiles(targetDirPath);
			logger.info("clean directory: " + targetDirPath);
		}
		Files.move(tempUnzipPath, targetDirPath,StandardCopyOption.REPLACE_EXISTING);
		logger.info("successfully extract packet file to {}", targetDirPath);
		return Optional.of(targetDirPath);
	}
	/**
	 * download packet of this application
	 * @param urlString
	 * @param applicationName
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ZipException
	 */
	public Packet getPacketFromURL(String urlString) throws MalformedURLException, IOException, ZipException{
		Optional<Path> packetFile = downloadPacket(urlString);
		Optional<Path> unzipPacketPath = unzipPacketFile(packetFile.get());
		PacketBuilder builder = Packet.builder();
		builder.packet(unzipPacketPath.get().toString())
			.specificEntityFile(unzipPacketPath.get().resolve(SPECIFICFILENAME).toString())
			.appName(unzipPacketPath.get().getFileName().toString());
		return builder.build();
	}
	/**
	 * delete packet of this application
	 * @param applicationName
	 * @throws IOException
	 */
	public void deletePacket(String applicationName) throws IOException{
		Path targetPath = dirPath.resolve(applicationName);
		FileUtils.deleteFiles(targetPath);
		logger.info("successfully delete file: {}", targetPath);
		targetPath = dirPath.resolve(applicationName + ".zip");
		FileUtils.deleteFiles(targetPath);
		logger.info("successfully delete file: {}", targetPath);
	}
	
	/**
	 * using specificEntity file in packet to create a specificEntity object
	 * @param applicationName
	 * @return
	 * @throws IOException
	 */
	public SpecificEntity findSpecifiEntity(String applicationName) throws IOException{
		Preconditions.checkArgument(!Strings.isNullOrEmpty(applicationName), "applicationName can't be null or empty");
		Path targetPath = dirPath.resolve(applicationName).resolve(SPECIFICFILENAME);
		Preconditions.checkArgument(Files.exists(targetPath), "application specificFile not exist:" + targetPath);
		return YamlUtils.loadAs(targetPath, SpecificEntity.class);
	}
	
	/**
	 * may be import files doesn't exist, 
	 * @param packet
	 * @return path or null
	 */
	public Optional<Path> findImportFiles(Packet packet) {
		Path targetPath = Paths.get(packet.getPacket()).resolve("imports");
		if(Files.exists(targetPath)) return Optional.of(targetPath);
		return Optional.absent();
	}

	public Path getAbsolutePathOfVMInterface(Packet packet, String path) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(path), "vminterface's path can't be null or empty, can't change a null or empty path to absolute path");
		Preconditions.checkNotNull(packet, "packet can't be null");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(packet.getPacket()), "packet's path can't be null or empty");
		
		Path targetPath = Paths.get(packet.getPacket()).resolve(path);
		return targetPath;
	}
}

