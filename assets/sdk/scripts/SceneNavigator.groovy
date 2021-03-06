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
package sdk.scripts;

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.event.ActionEvent

import javax.swing.*
import javax.swing.SwingConstants
import javax.swing.border.BevelBorder
import javax.swing.event.TreeExpansionEvent
import javax.swing.event.TreeWillExpandListener
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.ExpandVetoException

import com.jme3.math.ColorRGBA
import com.jme3.scene.Node
import com.jme3.scene.Spatial
import com.ravenclaw.RavenClaw
import com.ravenclaw.swing.*
import com.ravenclaw.utils.Screen

import corvus.corax.Corax
import corvus.corax.event.listeners.CoraxListener;

/**
 * @author Seth
 */
public class SceneNavigator implements TreeWillExpandListener, CoraxListener {

	private final JTree tree;
	
	public SceneNavigator() {
		
		def claw = Corax.getInstance(RavenClaw.class).getAppplication();
		def frame = Corax.getInstance(RavenClaw.class).getFrame();
		def panel = new JPanel(new BorderLayout());
		
		tree = new JTree();
		def scroll = new JScrollPane(tree);
		
		tree.setCellRenderer(new SceneNodeTreeRenderer());
		
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(claw.getRootNode());
		
		DefaultTreeModel model = new DefaultTreeModel(rootNode);
		
		//tree.addTreeSelectionListener(this);
		//tree.addTreeExpansionListener((TreeExpansionListener)this);
		//model.addTreeModelListener(this);
		
		parseLights(claw.getRootNode(), rootNode, model);
		parseControls(claw.getRootNode(), rootNode, model);
		
		for(Spatial spat : claw.getRootNode().getChildren()) {
			parseNodes(spat, rootNode, model);
		}
		
		tree.setModel(model);

		tree.setBackground(Color.lightGray);
		panel.add(scroll, BorderLayout.CENTER);
		
		def sizes = new Dimension(0, 18);
		def bar = new JPanel();
		bar.setLayout(new BoxLayout(bar, BoxLayout.X_AXIS));
		bar.setPreferredSize(sizes);
		bar.setMaximumSize(sizes);
		
		def refresh = new JButton("Refresh");
		refresh.setPreferredSize(new Dimension(26, 10));
		refresh.addActionListener(new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				rootNode = new DefaultMutableTreeNode(claw.getRootNode());
				model = new DefaultTreeModel(rootNode);

				parseLights(claw.getRootNode(), rootNode, model);
				parseControls(claw.getRootNode(), rootNode, model);
				
				for(Spatial spat : claw.getRootNode().getChildren()) {
					parseNodes(spat, rootNode, model);
				}
				
				tree.setModel(model);
			}
		});
		bar.add(refresh);
		
		panel.add(bar, BorderLayout.NORTH);
		
		JPanel statusPanel = new JPanel();
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		panel.add(statusPanel, BorderLayout.SOUTH);
		statusPanel.setPreferredSize(new Dimension(0, 16));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
		JLabel statusLabel = new JLabel("status");
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusPanel.add(statusLabel);
		
		ContentPanel content = frame.getContentPane();
		def bottomSplit = content.bottom;
		
		bottomSplit.add(panel, JSplitPane.LEFT);
	}

	/* (non-Javadoc)
	 * @see corvus.corax.event.listeners.CoraxAbstractListener#enabled()
	 */
	@Override
	public void enabled() { }

	/* (non-Javadoc)
	 * @see corvus.corax.event.listeners.CoraxAbstractListener#disabled()
	 */
	@Override
	public void disabled() { }

	/* (non-Javadoc)
	 * @see corvus.corax.event.listeners.CoraxListener#onEvent(int, corvus.corax.tools.MelloriObjectBuffer)
	 */
	@Override
	public Object onEvent(int key, MelloriObjectBuffer buff) {
		switch(key) {
		}
		return null;
	}


	public void parseNodes(Spatial spat, DefaultMutableTreeNode parent, DefaultTreeModel model) {

		def rez = new DefaultMutableTreeNode(spat); 
		model.insertNodeInto(rez, parent, parent.getChildCount());
		
		parseLights(spat, rez, model);
		parseControls(spat, rez, model);
		
		if(spat instanceof com.jme3.scene.Node && spat.getChildren().size() > 0)
		{
			def fat = (com.jme3.scene.Node) spat;
			
			for(Spatial child : fat.getChildren()) {
				parseNodes(child, rez, model);
			}
			
		}
	}
	
	public void parseLights(Spatial owner, DefaultMutableTreeNode parent, DefaultTreeModel model ) {
		
		def list = owner.getLocalLightList().list;
		
		for(light in list) {
			if(light == null)
				continue;
			model.insertNodeInto(new DefaultMutableTreeNode(light), parent, parent.getChildCount());
		}
	}

	public void parseControls(Spatial owner, DefaultMutableTreeNode parent, DefaultTreeModel model ) {
		
		def list = owner.controls;
		
		for(control in list) {
			if(control == null)
				continue;
			model.insertNodeInto(new DefaultMutableTreeNode(control), parent, parent.getChildCount());
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.TreeWillExpandListener#treeWillExpand(javax.swing.event.TreeExpansionEvent)
	 */
	@Override
	public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException
	{
		System.out.println(event);
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.TreeWillExpandListener#treeWillCollapse(javax.swing.event.TreeExpansionEvent)
	 */
	@Override
	public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException
	{
	}

}
