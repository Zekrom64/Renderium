package com.zekrom_64.renderium.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class Resources {

	public static String readResourceText(String resname) throws IOException {
		StringBuilder sb = new StringBuilder();
		char[] buffer = new char[2048];
		try(InputStream is = Resources.class.getResourceAsStream(resname)) {
			Reader reader = new InputStreamReader(is);
			int n;
			while((n = reader.read(buffer)) != -1) sb.append(buffer, 0, n);
		}
		return sb.toString();
	}
	
	public static byte[] readResourceBytes(String resname) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		try(InputStream is = Resources.class.getResourceAsStream(resname)) {
			int n;
			while((n = is.read(buffer)) != -1) baos.write(buffer, 0, n);
		}
		return baos.toByteArray();
	}
	
}
