package at.vintagestory.modelcreator.gui.right;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import at.vintagestory.modelcreator.gui.Icons;
import at.vintagestory.modelcreator.model.Element;

public class ElementTreeCellRenderer extends DefaultTreeCellRenderer 
{
	private static final long serialVersionUID = 1L;
	public static Map<String, Color> colorConfig = new HashMap<>();

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		Color color = Color.WHITE;
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		Object userObj = node.getUserObject();
		if (userObj instanceof Element) {
			Element element = (Element) userObj;
			color = element.TextColor;
		}
		setTextNonSelectionColor(color);
		setTextSelectionColor(color);
		super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

		if (tree.getModel().getRoot().equals(node)) {
			setIcon(null);
		} else {
			if (userObj instanceof Element) {
				Element element = (Element) userObj;
				if (element.getRenderInEditor()) {
					setIcon(Icons.smallcube);
				} else {
					setIcon(Icons.smallcubegray);
				}
			}
		}

        return this;
	}
}
