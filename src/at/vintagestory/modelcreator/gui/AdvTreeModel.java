package at.vintagestory.modelcreator.gui;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public class AdvTreeModel extends DefaultTreeModel {
	private static final long serialVersionUID = 1L;
	
	protected boolean pointsVisible;

	  public AdvTreeModel(TreeNode root) {
	    this(root, false);
	  }

	  public AdvTreeModel(TreeNode root, boolean asksAllowsChildren) {
	    this(root, false, false);
	  }

	  public AdvTreeModel(TreeNode root, boolean asksAllowsChildren, boolean filterIsActive) {
	    super(root, asksAllowsChildren);
	    this.pointsVisible = filterIsActive;
	  }

	  public void setPointsVisible(boolean newValue) {
	    pointsVisible = newValue;
	  }

	  public boolean arePointsVisible() {
	    return pointsVisible;
	  }

	  public Object getChild(Object parent, int index) {
	    if (pointsVisible) {
	      if (parent instanceof AdvTreeNode) {
	        return ((AdvTreeNode) parent).getChildAt(index, pointsVisible);
	      }
	    }
	    return ((TreeNode) parent).getChildAt(index);
	  }

	  public int getChildCount(Object parent) {
	    if (pointsVisible) {
	      if (parent instanceof AdvTreeNode) {
	        return ((AdvTreeNode) parent).getChildCount(pointsVisible);
	      }
	    }
	    return ((TreeNode) parent).getChildCount();
	  }

	}