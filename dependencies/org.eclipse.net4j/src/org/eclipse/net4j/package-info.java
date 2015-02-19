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
 * The Net4j transport layer.
 * <p>
 * <img src="doc-files/architecture.png" title="Diagram Architecture" border="0"/>
 * <p>
 * The five main interfaces of the transport layer are:
 * 	<ul>
 * 		<li>{@link org.eclipse.net4j.buffer.IBuffer}</li>
 * 		<li>{@link org.eclipse.net4j.channel.IChannel}</li>
 * 		<li>{@link org.eclipse.net4j.acceptor.IAcceptor}</li>
 * 		<li>{@link org.eclipse.net4j.connector.IConnector}</li>
 * 		<li>{@link org.eclipse.net4j.protocol.IProtocol}</li>
 * 	</ul>
 */
package org.eclipse.net4j;

