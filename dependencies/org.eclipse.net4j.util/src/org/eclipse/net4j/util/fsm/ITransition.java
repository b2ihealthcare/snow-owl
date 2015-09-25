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
package org.eclipse.net4j.util.fsm;

/**
 * Encpsulates the logic to be executed when an <i>event</i> arrives for a <i>subject</i> in a particular <i>state</i>.
 * 
 * @author Eike Stepper
 * @apiviz.landmark
 */
public interface ITransition<STATE extends Enum<?>, EVENT extends Enum<?>, SUBJECT, DATA>
{
  public void execute(SUBJECT subject, STATE state, EVENT event, DATA data);
}
