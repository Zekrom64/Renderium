package com.zekrom_64.renderium.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.jdt.annotation.NonNull;

/** A zip resource domain provides resources from a ZIP file (or compatible
 * formats such as a JAR).
 * 
 * @author Zekrom_64
 *
 */
public class ZipResourceDomain extends ResourceDomain {

	private final ZipFile zipFile;
	
	/** Creates a new zip resource domain with the given name from a zip file.
	 * 
	 * @param name Domain name
	 * @param file Zip file
	 * @throws IOException If an exception occurs when opening the zip file
	 */
	public ZipResourceDomain(@NonNull String name, @NonNull File file) throws IOException {
		super(name);
		zipFile = new ZipFile(file);
	}

	@Override
	public boolean exists(@NonNull String path) {
		return zipFile.getEntry(path) != null;
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull InputStream open(@NonNull String path) throws IOException {
		ZipEntry entry = zipFile.getEntry(path);
		if (entry == null) throw new IOException("No such resource \"" + path + "\" exists in zip domain \"" + name + "\"");
		return zipFile.getInputStream(entry);
	}

	@Override
	public void close() throws IOException {
		zipFile.close();
	}

}
