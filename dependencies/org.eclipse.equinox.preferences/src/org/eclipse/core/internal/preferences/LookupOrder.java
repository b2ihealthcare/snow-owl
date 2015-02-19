/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.internal.preferences;

/**
 * Object used to store the look-up order for preference
 * scope searching.
 * 
 * @since 3.0
 */
public class LookupOrder {

	private String[] order;

	LookupOrder(String[] order) {
		for (int i = 0; i < order.length; i++)
			if (order[i] == null)
				throw new IllegalArgumentException();
		this.order = order;
	}

	public String[] getOrder() {
		return order;
	}
}
