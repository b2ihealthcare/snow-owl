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
package com.b2international.snowowl.snomed.mrcm.core.widget.bean;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import com.b2international.snowowl.snomed.mrcm.core.widget.model.WidgetModel;

/**
 * Represents a leaf widget bean stored in a container bean. This class provides reasonable defaults for "clone",
 * "retire" and "clone and retire" actions:
 * <ul>
 * <li>"clone" adds a {@link #replicate() replicated} instance in which the state fields are populated exactly as the
 * original fields, and the replica shares the same model with this instance (providing the same set of options for the
 * user)
 * <li>"clone" is active if the model allows {@link WidgetModel#isMultiple() multiple instances} and this instance is
 * completely populated
 * <li>"retire" removes this instance from the parent's element list
 * <li>"retire" is active if the model is either optional or not the last instance of its kind, but fully populated in
 * both cases
 * <li>"clone and retire" acts as a sequential application of the previous two actions, leaving the same number of
 * elements in the parent
 * <li>"clone and retire" is active if this instance was released (unreleased items can be edited in place).
 * </ul>
 * 
 */
public abstract class LeafWidgetBean extends ModeledWidgetBean implements Serializable {

	private static final long serialVersionUID = -5105404981700845578L;

	//TODO: act also if the number of siblings are reached the limit received from MRCM
	private final class ActionEnablingListener implements PropertyChangeListener, Serializable {

		private static final long serialVersionUID = -6824425058863289326L;

		@Override
		public void propertyChange(final PropertyChangeEvent evt) {

			final boolean newCloneActionEnabled = isPopulated() && canBeCloned();
			setCloneActionEnabled(newCloneActionEnabled);

			final boolean newRetireActionEnabled = isPopulated() && canBeRetired();
			setRetireActionEnabled(newRetireActionEnabled);
			
			final boolean newCloneAndRetireActionEnabled = canBeClonedAndRetired();
			setCloneAndRetireActionEnabled(newCloneAndRetireActionEnabled);
		}

	}

	protected transient final PropertyChangeListener actionEnablingListener = new ActionEnablingListener();
	
	private ContainerWidgetBean parent;
	private boolean cloneActionEnabled;
	private boolean retireActionEnabled;
	private boolean cloneAndRetireActionEnabled;
	private boolean propagationEnabled = false; // propagation disabled by default
	private boolean released;
	
	/**
	 * Default constructor for serialization.
	 */
	protected LeafWidgetBean() {
		super();
	}
	
	protected LeafWidgetBean(final WidgetModel model, final boolean released) {
		super(model);
		this.released = released;
	}

	public void setParent(ContainerWidgetBean parent) {
		
		// Deregister from old parent, remove our listener as well
		if (null != this.parent) {
			this.parent.removePropertyChangeListener(ContainerWidgetBean.PROP_ELEMENTS, actionEnablingListener);
		}
		
		// Register for property changes on new parent and ourselves
		if (null != parent) {
			parent.addPropertyChangeListener(ContainerWidgetBean.PROP_ELEMENTS, actionEnablingListener);
			actionEnablingListener.propertyChange(null);
		}
		
		this.parent = parent;
	}
	
	public ContainerWidgetBean getParent() {
		return parent;
	}
	
	@Override
	public LeafWidgetBean onCloneAction() {
		final LeafWidgetBean replicate = replicate();
		parent.add(replicate, this);
		return replicate;
	}

	@Override
	public void onRetireAction() {
		parent.remove(this);
	}

	@Override
	public boolean isCloneActionEnabled() {
		return cloneActionEnabled;
	}
	
	@Override
	public boolean isRetireActionEnabled() {
		return retireActionEnabled;
	}
	
	@Override
	public boolean isCloneAndRetireActionEnabled() {
		return cloneAndRetireActionEnabled;
	}
	
	/**
	 * Returns {@code true} if the wrapped component is released. Otherwise returns {@code false}. 
	 * @return {@code true} if the represented component is released. Otherwise {@code false}.
	 */
	public boolean isReleased() {
		return released;
	}

	protected abstract boolean isPopulated();

	private void setCloneActionEnabled(final boolean newCloneActionEnabled) {
		final boolean oldCloneActionEnabled = this.cloneActionEnabled;
		cloneActionEnabled = newCloneActionEnabled;
		firePropertyChange(PROP_CLONE_ACTION_ENABLED, oldCloneActionEnabled, newCloneActionEnabled);
	}

	private void setRetireActionEnabled(final boolean newRetireActionEnabled) {
		final boolean oldRetireActionEnabled = this.retireActionEnabled;
		retireActionEnabled = newRetireActionEnabled;
		firePropertyChange(PROP_RETIRE_ACTION_ENABLED, oldRetireActionEnabled, newRetireActionEnabled);
	}

	private void setCloneAndRetireActionEnabled(final boolean newCloneAndRetireActionEnabled) {
		final boolean oldCloneAndRetireActionEnabled = this.cloneAndRetireActionEnabled;
		cloneAndRetireActionEnabled = newCloneAndRetireActionEnabled;
		firePropertyChange(PROP_CLONE_AND_RETIRE_ACTION_ENABLED, oldCloneAndRetireActionEnabled, newCloneAndRetireActionEnabled);
	}
	
	protected boolean canBeCloned() {
		
		if (isInfrastructure()) {
			return false;
		}
		
		return isMultiple();
	}

	protected boolean canBeRetired() {
		
		if (isInfrastructure()) {
			return false;
		}
		
		if (!isRequired()) {
			return true;
		}
			
		return hasParent() && !parent.isLastInstance(LeafWidgetBean.this); // either optional or not the last instance of its kind
	}
	
	protected boolean canBeClonedAndRetired() {
		return isReleased();
	}

	protected boolean hasParent() {
		return null != parent;
	}

	/**
	 * @return a replica instance (connected to the same model, and its state reinitialized)
	 */
	protected abstract LeafWidgetBean replicate();

	public boolean isPropagationEnabled() {
		return propagationEnabled;
	}

	public void setPropagationEnabled(boolean propagationEnabled) {
		this.propagationEnabled = propagationEnabled;
	}
}