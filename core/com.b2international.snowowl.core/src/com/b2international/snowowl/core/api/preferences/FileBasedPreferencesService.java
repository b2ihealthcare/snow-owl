/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.api.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.osgi.service.prefs.PreferencesService;

/**
 * 
 * Custom preferences service that stores preferences in the file system.
 * The root preference defines a configuration directory. All sub nodes in the preferences hierarchy 
 * will be saved into a property file in this directory under the name [nodename].properties 
 * 
 *
 */
public class FileBasedPreferencesService implements PreferencesService {
	
	private final FileBasedConfigurationPreferences preferences;

	public FileBasedPreferencesService(Path configDir) {
		this.preferences = new FileBasedConfigurationPreferences(configDir);
	}
	
	@Override
	public Preferences getSystemPreferences() {
		return preferences;
	}

	@Override
	public Preferences getUserPreferences(String name) {
		return preferences;
	}

	@Override
	public String[] getUsers() {
		return new String[0];
	}
	
	/**
	 * 
	 * Custom {@link Preferences} implementation to store configuration preferences in a property file.
	 * Supports only flat structure of preferences. 
	 * 
	 *
	 */
	private static final class FileBasedConfigurationPreferences implements Preferences {
		
		private Properties properties;
		private File file;
		private FileBasedConfigurationPreferences parent;
		private Map<String, FileBasedConfigurationPreferences> children;
		private String name;
		private Path configDir;
		
		private static final String ROOT_SNOWOWL_CONFIG_NODE_NAME = "SNOWOWL_CONFIG_ROOT";
		
		/**
		 * Constructor to create the root preferences node, does not contain preferences, it is the root of the preferences tree
		 * @param configDirectory
		 */
		private FileBasedConfigurationPreferences(Path configDir)  {
			if (!Files.isDirectory(configDir)){
				throw new IllegalArgumentException("Provided directory does not exist or it is not a directory.");
			}
			
			this.configDir = configDir;
			this.name =  ROOT_SNOWOWL_CONFIG_NODE_NAME;
			this.children = new HashMap<String, FileBasedPreferencesService.FileBasedConfigurationPreferences>();			
		}
		
		/**
		 * Constructor to create child preferences nodes.
		 * @param parent teh parent node
		 * @param nodeName the name of the node
		 */
		private FileBasedConfigurationPreferences(FileBasedConfigurationPreferences parent, String nodeName){
			
			if(parent == null){
				throw new IllegalArgumentException("Parent node cannot be null");
			}
			
			if(nodeName.isEmpty()){
				throw new IllegalArgumentException("Node name cannot be empty");
			}
			
			this.parent = parent;
			this.name = nodeName;
			this.file = parent.getConfigDir().resolve(getFilePath(nodeName)).toFile();
						
			try {				
				if(!file.exists()){
					file.createNewFile();
				}
				
				this.properties = new Properties();
			
				properties.load(new FileInputStream(file));
			} catch (IOException e) {
				throw new RuntimeException("Could not load preferences from file: "+file.getAbsolutePath(), e);
			}
		}

		@Override
		public void put(String key, String value) {
			properties.put(key, value);
			
		}

		@Override
		public String get(String key, String def) {
			return properties.getProperty(key, def);
		}

		@Override
		public void remove(String key) {
			properties.remove(key);
		}

		@Override
		public void clear() throws BackingStoreException {
			properties.clear();
		}

		@Override
		public void putInt(String key, int value) {
			properties.put(key, String.valueOf(value));
		}

		@Override
		public int getInt(String key, int def) {
			try {
				return Integer.valueOf((String) properties.get(key));
			} catch (Exception e) {
				return def;
			}
		}

		@Override
		public void putLong(String key, long value) {
			properties.put(key, String.valueOf(value));
		}

		@Override
		public long getLong(String key, long def) {
			try {
				return Long.valueOf((String) properties.get(key));
			} catch (Exception e) {
				return def;
			}
		}

		@Override
		public void putBoolean(String key, boolean value) {
			properties.put(key, String.valueOf(value));
		}

		@Override
		public boolean getBoolean(String key, boolean def) {
			try {
				return Boolean.valueOf((String) properties.get(key));
			} catch (Exception e) {
				return def;
			}
		}

		@Override
		public void putFloat(String key, float value) {
			properties.put(key, String.valueOf(value));
		}

		@Override
		public float getFloat(String key, float def) {
			try {
				return Float.valueOf((String) properties.get(key));
			} catch (Exception e) {
				return 0;
			}
		}

		@Override
		public void putDouble(String key, double value) {
			properties.put(key, String.valueOf(value));
		}

		@Override
		public double getDouble(String key, double def) {
			try {
				return Double.valueOf((String) properties.get(key));
			} catch (Exception e) {
				return 0;
			}
		}

		@Override
		public void putByteArray(String key, byte[] value) {
			properties.put(key, new String(value));
		}

		@Override
		public byte[] getByteArray(String key, byte[] def) {
			try {
				return ((String)properties.get(key)).getBytes();
			} catch (Exception e) {
				return def;
			}
		}

		@Override
		public String[] keys() throws BackingStoreException {
			return properties.keySet().toArray(new String[0]);
		}

		@Override
		public String[] childrenNames() throws BackingStoreException {
			return children.keySet().toArray(new String[children.size()]);
		}

		@Override
		public Preferences parent() {
			return parent;
		}

		@Override
		public Preferences node(String nodeName) {
			
			FileBasedConfigurationPreferences child = children.get(nodeName);
			if (child == null) {
				child = new FileBasedConfigurationPreferences(this, nodeName);
				children.put(nodeName, child);
			}
		
			return child;
		}

		@Override
		public boolean nodeExists(String nodeName) throws BackingStoreException {
			
			if(!isRoot()){
				return false;
			}
			
			return children.containsKey(nodeName);
			
		}

		@Override
		public void removeNode() throws BackingStoreException {
			throw new UnsupportedOperationException(getClass().getName() + " does not support removing of preferences");
		}

		@Override
		public String name() {
			return name;
		}

		@Override
		public String absolutePath() {
			return getConfigDir().resolve(getFilePath(name)).toString();
		}

		@Override
		public void flush() throws BackingStoreException {
			try {
				properties.store(new FileOutputStream(file), null);
			} catch (IOException e) {
				throw new BackingStoreException("Error while flushing",e);
			}
		}

		@Override
		public void sync() throws BackingStoreException {
			flush();
		}
		
		private boolean isRoot() {
			return parent() == null;
		}
		
		public Path getConfigDir() {			
			return isRoot() ? configDir : parent.getConfigDir();
		}
		
		
		private String getFilePath(String nodeName){
			
			if(nodeName.endsWith(".properties")){
				return nodeName;
			}
			
			return nodeName+".properties";
			
		}
	}

}