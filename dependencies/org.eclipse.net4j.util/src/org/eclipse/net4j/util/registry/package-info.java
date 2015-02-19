/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */

/**
 * Interfaces and classes for general purpose registries.
 * A registry is like a <code>Map</code> but has some additional features:
 * <ul>
 *   <li>It provides a descriptor framework for registry elements that 
 *   	  are to be instantiated lazily 
 *   <li>It provides a notification framework that enables clients to 
 *       react on events (including the resolution of a lazy descriptor)
 * <ul>
 */
package org.eclipse.net4j.util.registry;
