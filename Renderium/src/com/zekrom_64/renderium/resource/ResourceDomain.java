package com.zekrom_64.renderium.resource;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.zekrom_64.renderium.util.TypeUtils;
import com.zekrom_64.renderium.util.threading.IRWLock;
import com.zekrom_64.renderium.util.threading.StampedRWLock;

/** <p>A resource domain manages how resources are managed from a particular
 * source. Resource domains are globally accessible and this class provides
 * static methods for managing this.</p>
 * 
 * <p>Resource domains can have a "fallback" domain set that will be used
 * if the named domain is not registered. This process occurs recursively,
 * returning to the null resource domain if none is found.</p>
 * 
 * <p>There is also a thread-local "implied" domain that will be retrieved
 * when no domain is specified when a {@link ResourceLocation} is created.</p>
 * 
 * @author Zekrom_64
 *
 */
public abstract class ResourceDomain implements Closeable {
	
	private static final ThreadLocal<@NonNull ResourceDomain> impliedDomain = ThreadLocal.withInitial(() -> NullResourceDomain.INSTANCE);
	private static final Map<String, ResourceDomain> allDomains = new HashMap<>();
	private static final IRWLock rwdomains = new StampedRWLock();
	private static final Map<String, String> fallbackDomains = new HashMap<>();
	private static final IRWLock rwfallbacks = new StampedRWLock();
	
	static {
		registerDomain(NullResourceDomain.INSTANCE);
		registerDomain(new ClassResourceDomain("renderium", TypeUtils.nonNull(ResourceDomain.class.getClassLoader())));
	}
	
	/** Registers a resource domain.
	 * 
	 * @param domain Domain to register
	 */
	public static void registerDomain(@NonNull ResourceDomain domain) {
		try(var wlock = rwdomains.write()) {
			allDomains.put(domain.name, domain);
		}
	}
	
	/** Gets a registered resource domain by its name.
	 * 
	 * @param name Domain name
	 * @return Registered resource domain, or <b>null</b> if none was found
	 */
	public static @Nullable ResourceDomain getDomain(@NonNull String name) {
		try(var rlock = rwdomains.read()) {
			return allDomains.get(name);
		}
	}
	
	/** Sets the fallback domain for the given domain name.
	 * 
	 * @param domainName Domain name to set fallback for
	 * @param fallbackName Fallback name to set
	 */
	public static void setFallbackDomain(@NonNull String domainName, @NonNull String fallbackName) {
		try(var wlock = rwfallbacks.write()) {
			fallbackDomains.put(domainName, fallbackName);
		}
	}
	
	/** Resolves a domain name to a usable resource domain.
	 * 
	 * @param name Domain name
	 * @return Resource domain
	 */
	public static @NonNull ResourceDomain resolveDomain(@NonNull String name) {
		ResourceDomain domain = null;
		try(var rdomains = rwdomains.read()) {
			do {
				domain = allDomains.get(name);
				if (domain == null) {
					String nextname;
					try(var rfallbacks = rwfallbacks.read()) {
						nextname = fallbackDomains.get(name);
					}
					if (nextname == null) return NullResourceDomain.INSTANCE;
					name = nextname;
				}
			} while(domain == null);
		}
		return domain;
	}
	
	/** Sets the implied resource domain for the current thread.
	 * 
	 * @param domain New implied resource domain
	 */
	public static void setImpliedDomain(@NonNull ResourceDomain domain) {
		impliedDomain.set(domain);
	}
	
	/** Gets the current implied resource domain.
	 * 
	 * @return Implied resource domain
	 */
	@SuppressWarnings("null")
	public static @NonNull ResourceDomain getImpliedDomain() {
		return impliedDomain.get();
	}

	/** The name of the resource domain. */
	public final @NonNull String name;
	
	/** Creates a new resource domain with the given name
	 * 
	 * @param name Domain name
	 */
	protected ResourceDomain(@NonNull String name) {
		this.name = name;
	}
	
	/** Resolves a relative path within this resource domain.
	 * 
	 * @param path Relative path
	 * @return Absolute path
	 */
	protected @NonNull String resolveRelativePath(@NonNull String path) {
		return "/assets/" + name + "/" + path;
	}
	
	/** Resolves a path within this resource domain to a predictable absolute path.
	 * 
	 * @param path Path to resolve
	 * @return Absolute path
	 */
	public @NonNull String resolvePath(@NonNull String path) {
		if (path.isEmpty()) return path;
		if (path.charAt(0) != '/') return resolveRelativePath(path);
		else return path;
	}
	
	/** Tests if a resource exists at the given path in this domain.
	 * 
	 * @param path Absolute path to resource
	 * @return If a resource exists at this path
	 */
	public abstract boolean exists(@NonNull String path);
	
	/** Attempts to open a resource at the given path in this domain.
	 * 
	 * @param path Absolute path to resource
	 * @return Input stream for the resource 
	 * @throws IOException If an exception occurs opening the resource or it does not exist
	 */
	public abstract @NonNull InputStream open(@NonNull String path) throws IOException;

	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public void close() throws IOException { }
	
	static {
		// Close all resource domains
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			for(ResourceDomain domain : allDomains.values()) {
				try {
					domain.close();
				} catch (IOException e) {}
			}
		}));
	}
	
}
