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
package com.b2international.commons.csv;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.google.common.base.Function;

public class ReflectiveBeanAdapter<T> implements Function<List<String>, T>{

	private final Class<T> beanClass;
	private final PropertyDescriptor[] descriptors;

	public ReflectiveBeanAdapter(Class<T> beanClass, String... properties) throws IntrospectionException {

		this.beanClass = beanClass;
		this.descriptors = new PropertyDescriptor[properties.length];
		
		BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
		PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
		
		outer: for( int i = 0; i < properties.length; i++ ) {
			
			if(properties[i] == null) {
				this.descriptors[i] = null;
				continue outer;
			}
			
			for(int j = 0; j < descriptors.length; j++) {
				if(properties[i].equals(descriptors[j].getName())) {
					this.descriptors[i] = descriptors[j];
					continue outer;
				}
			}
			
			throw new IllegalArgumentException(String.format("Property %s not found on class %s", properties[i], beanClass));
		}
	}
	
	public T apply(List<String> line) {
		
		try {
			T bean = beanClass.newInstance();
			
			for( int i = 0; i < descriptors.length; i++ ) {
				if(descriptors[i] != null ) {
					descriptors[i].getWriteMethod().invoke(bean, line.get(i));
				}
			}
			
			return bean;
			
		} catch (InstantiationException e) {
			throw new RuntimeException("Error while creating new bean instance", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Error while setting bean property", e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Error while setting bean property", e);
		}
	}
}