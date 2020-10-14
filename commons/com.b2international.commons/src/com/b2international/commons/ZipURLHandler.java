/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.commons;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * An URLStreamHandler to make zip URL's. Gives support for the openStream operation on the URL
 * 
 * 
 */
public class ZipURLHandler extends URLStreamHandler {
	
	private final String fileName;
	
	private final String entryName;
	
	public ZipURLHandler(String fileName, String entryName) {
		this.entryName = entryName;
		this.fileName = fileName;
	}

	/*
	 * (non-Javadoc)
	 * @see java.net.URLStreamHandler#openConnection(java.net.URL)
	 */
	@Override
	protected URLConnection openConnection(URL url) throws IOException {
		return new ZipURLConnection(url, fileName, entryName);
	}

	/**
	 * Static method to get URL from a zip file and entry path
	 * 
	 * @param fileName zip file path. This should be absolute.
	 * @param entryName entry name path. From the root of the zip file
	 * 
	 * @return {@link URL} The URL to the specified entry inside of the zip archive
	 */
	public static URL createURL(String fileName, String entryName) {
		try {
			return new URL("zip", "", -1, fileName + "!" + entryName, new ZipURLHandler(fileName, entryName));
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Unable to create URL instance from passed in file and entry. File: " + fileName + " | Entry: " + entryName, e);
		}
	}
	
	/**
	 * Static method to get URL from a zip file and entry path
	 * 
	 * @param file zip file.
	 * @param entryName entry name path. From the root of the zip file
	 * 
	 * @return {@link URL} The URL to the specified entry inside of the zip archive
	 */
	public static URL createURL(File file, String entryName) {
		return createURL(file.getPath(), entryName);
	}
	
	/**
	 * Static method to get URL from a zip file and entry path
	 * 
	 * @param uri zip file URI.
	 * @param entryName entry name path. From the root of the zip file
	 * 
	 * @return {@link URL} The URL to the specified entry inside of the zip archive
	 */
	public static URL createURL(URI uri, String entryName) {
		return createURL(new File(uri), entryName);
	}
	
	
	/**
	 * Inner URLConnection class to support the inputStream
	 * 
	 *
	 */
	private class ZipURLConnection extends URLConnection{
		
		private final String fileName;
		
		private final String entryName;
		
		protected ZipURLConnection(URL url,String fileName, String entryName) {
			super(url);
			this.fileName = fileName;
			this.entryName = entryName;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.net.URLConnection#connect()
		 */
		@Override
		public void connect() throws IOException {
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.net.URLConnection#getInputStream()
		 */
		@Override
		public InputStream getInputStream() throws IOException {
			ZipFile file = new ZipFile(fileName);
			ZipEntry entry = file.getEntry(entryName);
			return file.getInputStream(entry);
		}
		
	}

}