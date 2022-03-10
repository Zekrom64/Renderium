package com.zekrom_64.renderium.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.NonNull;

/** A resource location identifies a resource that can be loaded from a resource domain. Locations
 * are represented as strings of the format <tt>&lt;domain&gt;:/path/to/resource</tt>. If the path
 * is prefixed by a '/' it is an 'absolute' path within the resource domain, and if not it is automatically
 * prefixed with the path <tt>/assets/&lt;domain name&gt;/</tt>. If the path ends with a '/' it is
 * considered a directory instead of a resource file.
 * 
 * @author Zekrom_64
 *
 */
public class ResourceLocation {

	private static final Pattern ILLEGAL_CHARS = Pattern.compile("[<>\"\\\\\\|]");
	private static final Predicate<String> IS_ILLEGAL = ILLEGAL_CHARS.asMatchPredicate();
	
	/** Tests if the given string is a valid path.
	 * 
	 * @param path Path to test
	 * @return If the path is valid
	 */
	public static boolean isValidPath(@NonNull String path) {
		return !IS_ILLEGAL.test(path);
	}
	
	/** Tests if the given string is a directory path
	 * 
	 * @param path Path to test
	 * @return If the path is valid
	 */
	public static boolean isDirectoryPath(@NonNull String path) {
		return path.charAt(path.length() - 1) == '/';
	}
	
	/** Gets the name of a resource from a path string.
	 * 
	 * @param path Path to use
	 * @return Name of resource at the path
	 */
	@SuppressWarnings("null")
	public static @NonNull String getName(@NonNull String path) {
		int spos = path.length() - 1;
		if (isDirectoryPath(path)) spos--;
		while(spos >= 0) {
			if (path.charAt(spos) == '/') break;
			spos--;
		}
		if (spos < 0) return "";
		return path.substring(spos + 1);
	}
	
	/** The resource domain this location specifies. */
	public final @NonNull ResourceDomain domain;
	/** The path within the resource domain. */
	public final @NonNull String path;
	/** If the specified resource is a directory. */
	public final boolean isDirectory;
	/** If the specified resource is a file. */
	public final boolean isFile;
	/** The name of the resource specified by the path. */
	public final @NonNull String name;
	
	/** Creates a new resource location from a resource domain and a path within that domain.
	 * 
	 * @param domain The domain of the resource
	 * @param path The path within the domain
	 */
	public ResourceLocation(@NonNull ResourceDomain domain, @NonNull String path) {
		this.domain = domain;
		this.path = domain.resolvePath(path);
		
		if (!isValidPath(this.path)) throw new IllegalArgumentException("Illegal path \"" + this.path + "\"");
		this.isDirectory = isDirectoryPath(this.path);
		this.isFile = !this.isDirectory;
		this.name = getName(this.path);
	}

	/** Creates a new resource location from a resource domain and a path within that domain.
	 * 
	 * @param domain The name of the domain of the resource
	 * @param path The path within the domain
	 */
	public ResourceLocation(@NonNull String domain, @NonNull String path) {
		this(ResourceDomain.resolveDomain(domain), path);
	}
	
	/** Creates a new resource location from a string describing the path. If no domain is
	 * specified with this path the implied domain is used.
	 * 
	 * @param path Resource path
	 */
	@SuppressWarnings("null")
	public ResourceLocation(@NonNull String path) {
		int i = path.indexOf(':');
		if (i < 0) {
			this.domain = ResourceDomain.getImpliedDomain();
			this.path = path;
		} else {
			this.domain = ResourceDomain.resolveDomain(path.substring(0, i));
			this.path = domain.resolvePath(path.substring(i + 1));
		}
		
		if (!isValidPath(this.path)) throw new IllegalArgumentException("Illegal path \"" + this.path + "\"");
		this.isDirectory = isDirectoryPath(this.path);
		this.isFile = !this.isDirectory;
		this.name = getName(this.path);
	}
	
	/** Creates a new resource location using an existing resource location as the parent and appending
	 * a sub-path within that directory.
	 * 
	 * @param parent Parent resource
	 * @param subpath Subpath within parent
	 */
	public ResourceLocation(@NonNull ResourceLocation parent, @NonNull String subpath) {
		if (!parent.isDirectory) throw new IllegalArgumentException("Parent resource location must be a directory");
		this.domain = parent.domain;
		this.path = domain.resolvePath(parent.path + "/" + subpath);
		
		if (!isValidPath(this.path)) throw new IllegalArgumentException("Illegal path \"" + this.path + "\"");
		this.isDirectory = isDirectoryPath(this.path);
		this.isFile = !this.isDirectory;
		this.name = getName(this.path);
	}
	
	/** Gets the parent location of this resource. The parent of a file or directory will
	 * always itself be a directory.
	 * 
	 * @return Parent resource
	 */
	@SuppressWarnings("null")
	public @NonNull ResourceLocation parent() {
		int spos = path.length() - 1;
		if (isDirectory) spos--;
		while(spos >= 0) {
			if (path.charAt(spos) == '/') break;
			spos--;
		}
		if (spos < 0) throw new IllegalStateException("Cannot get parent of a root resource location");
		return new ResourceLocation(domain, path.substring(0, spos + 1));
	}
	
	/** Gets the "extension" of this resource from its name. If the resource
	 * ends with text separated by a period, this text is the extension. Otherwise
	 * the extension is an empty string. Note that this excludes filenames that
	 * have a single period at the start.
	 * 
	 * @return
	 */
	@SuppressWarnings("null")
	public @NonNull String extension() {
		int i = name.lastIndexOf('.');
		if (i <= 0) return "";
		else return name.substring(i + 1);
	}
	
	@Override
	public int hashCode() {
		return path.hashCode() ^ (domain.hashCode() << 8);
	}
	
	/** Tests if two resource locations are the same.
	 * 
	 * @param loc The resource location to compare to
	 * @return If the resource locations are the same
	 */
	public boolean equals(@NonNull ResourceLocation loc) {
		return domain == loc.domain && path.equals(loc.path);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null) return false;
		if (obj instanceof ResourceLocation loc) return equals((ResourceLocation)obj);
		return false;
	}

	@Override
	public String toString() {
		return domain + ":" + path;
	}
	
	/** Tests if the resource identified by this location actually exists.
	 * 
	 * @return If the resource exists
	 */
	public boolean exists() {
		return domain.exists(path);
	}
	
	/** Attempts to open the resource identified by this location.
	 * 
	 * @return Stream reading this resource
	 * @throws IOException If an exception occurs opening the resource
	 */
	public @NonNull InputStream open() throws IOException {
		if (isDirectory) throw new IOException("Cannot open a directory as a resource");
		return domain.open(path);
	}
	
	/** Reads the contents of this resource as text.
	 * 
	 * @return The text of the resource
	 * @throws IOException If an exception occurs opening or reading the resource
	 */
	@SuppressWarnings("null")
	public @NonNull String readText() throws IOException {
		StringBuilder sb = new StringBuilder();
		char[] buffer = new char[2048];
		try(InputStream is = open()) {
			Reader reader = new InputStreamReader(is);
			int n;
			while((n = reader.read(buffer)) != -1) sb.append(buffer, 0, n);
		}
		return sb.toString();
	}
	
	/** Reads the contents of this resource as a series of bytes.
	 * 
	 * @return The bytes of the resource
	 * @throws IOException If an exception occurs opening or reading the resource
	 */
	public byte[] readBytes() throws IOException {
		try(InputStream is = open()) {
			return is.readAllBytes();
		}
	}
	
}
