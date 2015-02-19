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
package com.b2international.snowowl.snomed.mrcm.core;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * Utility methods for JAXB marshaling/unmarshaling.
 * 
 */
public class PersistenceUtils {

	@SuppressWarnings("unchecked") 
	public static <T> T unmarshal(Class<T> documentClass, InputStream inputStream)	throws JAXBException {
		String packageName = documentClass.getPackage().getName();
		JAXBContext jc = JAXBContext.newInstance(packageName);
		Unmarshaller u = jc.createUnmarshaller();
		Object unmarshalledObject = u.unmarshal(inputStream);
		if (unmarshalledObject instanceof JAXBElement) {
			JAXBElement<T> doc = (JAXBElement<T>) unmarshalledObject;
			return doc.getValue();
		} else {
			return (T) unmarshalledObject;
		}
	}

	public static <T> void marshal(T document, OutputStream outputStream) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(document.getClass().getPackage().getName());
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(document, outputStream);
	}

}