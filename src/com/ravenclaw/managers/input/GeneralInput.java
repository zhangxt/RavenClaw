/*
 * Copyright (c) 2009-2012 jMonkeyEngine & Corvus Corax
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine', 'Corvus Corax', 'Raven Claw' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.ravenclaw.managers.input;

import java.text.DecimalFormat;

import org.lwjgl.input.Mouse;

import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.scene.Spatial;
import com.ravenclaw.managers.SelectionManager;
import com.ravenclaw.utils.Utils;

import corvus.corax.processing.annotation.Inject;

/**
 * @author Vlad
 */
public final class GeneralInput extends RavenClawInput implements ActionListener, AnalogListener {

	@Inject
	private SelectionManager selectionManager;
	
	/* (non-Javadoc)
	 * @see com.ravenclaw.managers.input.RavenClawInput#registerInputImpl()
	 */
	@Override
	protected void registerInputImpl() {
		addInput("click", this, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		
		addInput("moveX+", this, new MouseAxisTrigger(MouseInput.AXIS_X, false));
		addInput("moveX-", this, new MouseAxisTrigger(MouseInput.AXIS_X, true));
		addInput("moveY+", this, new MouseAxisTrigger(MouseInput.AXIS_Y, false));
		addInput("moveY-", this, new MouseAxisTrigger(MouseInput.AXIS_Y, true));
	}

	/* (non-Javadoc)
	 * @see com.jme3.input.controls.ActionListener#onAction(java.lang.String, boolean, float)
	 */
	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		switch (name) {
			case "click":
				CollisionResults rz = Utils.pick(claw.getAppplication().getRootNode());
				if(rz.size() > 0) {
					Spatial target = rz.getClosestCollision().getGeometry();
					
					if(target != null)
						selectionManager.select(target);
					
				}
				
				break;
			default:
				System.out.println("Unhandled: "+name);
				break;
		}
	}

	DecimalFormat frm = new DecimalFormat("#.###");
	/* (non-Javadoc)
	 * @see com.jme3.input.controls.AnalogListener#onAnalog(java.lang.String, float, float)
	 */
	@Override
	public void onAnalog(String name, float value, float tpf) {

		switch (name) {
			default:
				System.out.println("Unhandled: "+name+" value = "+frm.format(value) + " X["+Mouse.getX()+"]Y["+Mouse.getY()+"]");
				break;
		}
	}
}
