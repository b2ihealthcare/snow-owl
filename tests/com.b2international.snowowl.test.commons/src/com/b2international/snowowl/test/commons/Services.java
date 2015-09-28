/*******************************************************************************
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 *******************************************************************************/
package com.b2international.snowowl.test.commons;

import com.b2international.snowowl.core.ApplicationContext;

/**
 * @since 3.3
 */
public class Services {

	private Services() {
	}

	/**
	 * Returns a must have service from the {@link ApplicationContext}.
	 * 
	 * @param type
	 * @return
	 */
	public static <T> T service(Class<T> type) {
		return ApplicationContext.getInstance().getServiceChecked(type);
	}

}
