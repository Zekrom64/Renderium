package com.zekrom_64.renderium.resource;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.jdt.annotation.NonNull;

/** The null resource domain will always have no resources inside it and is the domain
 * of last resort.
 * 
 * @author Zekrom_64
 *
 */
public class NullResourceDomain extends ResourceDomain {

	/** The null resource domain instance. */
	public static final @NonNull NullResourceDomain INSTANCE = new NullResourceDomain();
	
	protected NullResourceDomain() {
		super("null");
	}

	@Override
	public boolean exists(@NonNull String path) {
		return false;
	}

	@Override
	public @NonNull InputStream open(@NonNull String path) throws IOException {
		throw new IOException("Cannot open resource from null domain");
	}

}
