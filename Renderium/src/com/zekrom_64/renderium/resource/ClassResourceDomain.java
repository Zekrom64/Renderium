package com.zekrom_64.renderium.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;

import com.zekrom_64.renderium.util.threading.StampedRWLock;

/** A resource domain that retrieves resources through a {@link ClassLoader}.
 * 
 * @author Zekrom_64
 *
 */
public class ClassResourceDomain extends ResourceDomain {

	private final ClassLoader loader;
	
	private final Set<String> extantResources = new HashSet<>();
	private final StampedRWLock rwextant = new StampedRWLock();
	
	/** Creates a new classloader-based resource domain.
	 * 
	 * @param name Domain name
	 * @param loader Class loader to use
	 */
	public ClassResourceDomain(@NonNull String name, @NonNull ClassLoader loader) {
		super(name);
		this.loader = loader;
	}
	
	@Override
	public boolean exists(@NonNull String path) {
		try(var rlock = rwextant.read()) {
			if (extantResources.contains(path)) return true;

			InputStream stream = loader.getResourceAsStream(path);
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) { }

				try(var wlock = rlock.upgrade()) {
					extantResources.add(path);
				}
				return true;
			} else return false;
		}
	}

	@Override
	public @NonNull InputStream open(@NonNull String path) throws IOException {
		InputStream stream = loader.getResourceAsStream(path);
		if (stream == null) throw new IOException("No such resource \"" + path + "\" exists in class domain \"" + name + "\"");
		return stream;
	}

}
