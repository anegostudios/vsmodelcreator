package at.vintagestory.modelcreator.gui;

import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import at.vintagestory.modelcreator.enums.EnumNodeType;

public class AdvTreeNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 1L;
	public EnumNodeType nodeType;

	  public AdvTreeNode() {
	    this(null);
	  }

	  public AdvTreeNode(Object userObject) {
	    this(userObject, true, EnumNodeType.Cube);
	  }

	  public AdvTreeNode(Object userObject, boolean allowsChildren, EnumNodeType nodeType) {
	    super(userObject, allowsChildren);
	    this.nodeType = nodeType;
	  }

	  public TreeNode getChildAt(int index, boolean pointsVisible) {
	    if (pointsVisible) {
	      return super.getChildAt(index);
	    }
	    
	    if (children == null) {
	      throw new ArrayIndexOutOfBoundsException("node has no children");
	    }

	    int realIndex = -1;
	    int visibleIndex = -1;
	    Enumeration e = children.elements();
	    while (e.hasMoreElements()) {
	      AdvTreeNode node = (AdvTreeNode) e.nextElement();
	      if (node.nodeType == EnumNodeType.Cube) {
	        visibleIndex++;
	      }
	      realIndex++;
	      if (visibleIndex == index) {
	        return (TreeNode) children.elementAt(realIndex);
	      }
	    }

	    throw new ArrayIndexOutOfBoundsException("index unmatched");
	    //return (TreeNode)children.elementAt(index);
	  }

	  public int getChildCount(boolean filterIsActive) {
	    if (!filterIsActive) {
	      return super.getChildCount();
	    }
	    if (children == null) {
	      return 0;
	    }

	    int count = 0;
	    Enumeration e = children.elements();
	    while (e.hasMoreElements()) {
	      AdvTreeNode node = (AdvTreeNode) e.nextElement();
	      if (node.nodeType == EnumNodeType.Cube) {
	        count++;
	      }
	    }

	    return count;
	  }

	  

	}
