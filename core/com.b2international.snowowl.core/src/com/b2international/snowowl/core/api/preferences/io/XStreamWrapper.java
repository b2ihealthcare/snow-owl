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
package com.b2international.snowowl.core.api.preferences.io;

import java.io.File;
import java.io.OutputStream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;

/**
 *
 */
public final class XStreamWrapper {

	private final XStream xStream;
	
	public final String toXML(Object object) {
		return xStream.toXML(object);
	}
	
	public final void toXML(Object object, OutputStream outputStream) {
		xStream.toXML(object, outputStream);
	}
	
	@SuppressWarnings("unchecked")
	public final <T> T fromXML(String xml) {
		return (T) xStream.fromXML(xml);
	}
	
	@SuppressWarnings("unchecked")
	public final <T> T fromXML(File xmlFile) {
		return (T) xStream.fromXML(xmlFile);
	}
	
	public XStreamWrapper(ClassLoader classLoader) {
		xStream = new XStream(new Xpp3Driver());
		xStream.setClassLoader(classLoader);
	}
	
	public XStreamWrapper(Class<?> clazz) {
		xStream = new XStream(new Xpp3Driver());
		xStream.setClassLoader(clazz.getClassLoader());
	}
	
	public XStreamWrapper(Object classLoaderObject) {
		xStream = new XStream(new Xpp3Driver());
		xStream.setClassLoader(classLoaderObject.getClass().getClassLoader());
	}
	
	public XStreamWrapper setMode(int mode) {
		xStream.setMode(mode);
		return this;
	}
	
	
	public XStreamWrapper processAnnotations(Class<?> clazz){
		xStream.processAnnotations(clazz);
		return this;
	}
	
	/**
	 * By using this, we can switch on xStream's ability of automatic check for annotations.
	 * Please beware of it's pitfalls, read {@link http://xstream.codehaus.org/annotations-tutorial.html#AutoDetect} before you use.
	 * @param classLoaderObject
	 * @param autoDetectAnnotations
	 */
	public XStreamWrapper(Object classLoaderObject, boolean autoDetectAnnotations) {
		this(classLoaderObject);
		xStream.autodetectAnnotations(autoDetectAnnotations);
	}
}