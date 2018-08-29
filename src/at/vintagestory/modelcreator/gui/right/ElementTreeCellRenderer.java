package at.vintagestory.modelcreator.gui.right;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import at.vintagestory.modelcreator.gui.Icons;
import at.vintagestory.modelcreator.model.Element;

public class ElementTreeCellRenderer extends DefaultTreeCellRenderer 
{
	private static final long serialVersionUID = 1L;
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		super.getTreeCellRendererComponent(tree, value, selected,expanded, leaf, row, hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        
        if (tree.getModel().getRoot().equals(node)) {
            setIcon(null);
        } else {
			Object userObj = node.getUserObject(); 
        	if (userObj instanceof Element) {
        		if(((Element)userObj).Render) {
        			setIcon(Icons.smallcube);
        		} else {
        			setIcon(Icons.smallcubegray);
        		}
        	}

            
        }
        
        return this;
	}

}
