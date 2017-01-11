package at.vintagestory.modelcreator.gui.right;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.model.Element;

public class ElementTree
{
	public JTree jtree;
	public DefaultMutableTreeNode rootNode;
	public DefaultTreeModel treeModel;
	private Toolkit toolkit = Toolkit.getDefaultToolkit();
		
	public ElementTree() {
		rootNode = new DefaultMutableTreeNode("Root");
		treeModel = new DefaultTreeModel(rootNode);
        jtree = new JTree(treeModel);
        jtree.setEditable(true);
        jtree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jtree.setShowsRootHandles(true);
        jtree.setCellRenderer(new ElementTreeCellRenderer());
        
		jtree.addTreeSelectionListener(new TreeSelectionListener()
		{
			@Override
			public void valueChanged(TreeSelectionEvent e)
			{
				ModelCreator.currentProject.SelectedElement = getSelectedElement();
				ModelCreator.updateValues();
			}
		});
		
	}
	

	public void clearElements()
	{
		jtree.clearSelection();
		rootNode.removeAllChildren();
		jtree.removeAll();
		jtree.updateUI();
	}

	
	public Element getSelectedElement() {
		TreePath currentSelection = jtree.getSelectionPath();
        if (currentSelection != null) {
        	Object userObj = ((DefaultMutableTreeNode)currentSelection.getLastPathComponent()).getUserObject(); 
        	if (userObj instanceof Element) {
        		return (Element)userObj;
        	}
        }
        return null;
	}
	
	
	public void SelectElement(Element elem) {
		jtree.clearSelection();
		
		Enumeration<DefaultMutableTreeNode> enumer = rootNode.breadthFirstEnumeration();
		while (enumer.hasMoreElements()) {
			DefaultMutableTreeNode node = enumer.nextElement();
			if (node.getUserObject().equals(elem)) {
				jtree.setSelectionPath(new TreePath(node.getPath()));
				break;
			}
		}
	}
	
	public void selectElementByOpenGLName(int opengglname) {
		jtree.clearSelection();
		
		Enumeration<DefaultMutableTreeNode> enumer = rootNode.breadthFirstEnumeration();
		
		while (enumer.hasMoreElements()) {
			DefaultMutableTreeNode node = enumer.nextElement();
			
			if (node.getUserObject() instanceof Element) {
				if (((Element)node.getUserObject()).openGlName == opengglname) {
					jtree.setSelectionPath(new TreePath(node.getPath()));
					return;
				}
			}
		}
	}
	
	
	
    /** Remove the currently selected node. */
    public boolean removeCurrentElement() {
        TreePath currentSelection = jtree.getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)(currentSelection.getLastPathComponent());
            
            MutableTreeNode parent = (MutableTreeNode)(currentNode.getParent());
            
            if (parent != null) {
                treeModel.removeNodeFromParent(currentNode);
                
                Element elem = (Element)currentNode.getUserObject();
                if (elem.ParentElement != null) {
                	elem.ParentElement.ChildElements.remove(elem);
                	elem.ParentElement = null;	
                }
                
                return true;
            }
            return true;
        } 

        // Either there was no selection, or the root was selected.
        toolkit.beep();
        
        return false;
    }

    public DefaultMutableTreeNode addElementAsSibling(Element child) {
        DefaultMutableTreeNode parentNode = null;
        
        TreePath parentPath = jtree.getSelectionPath();

        if (parentPath == null) {
            parentNode = rootNode;
        } else {
            parentNode = (DefaultMutableTreeNode) ((DefaultMutableTreeNode)(parentPath.getLastPathComponent())).getParent();
        }

        DefaultMutableTreeNode node =  addElement(parentNode, child, true);
        SelectElement(child);
        return node;
    }

    
    /** Add child to the currently selected node. */
    public DefaultMutableTreeNode addElementAsChild(Element child) {
        DefaultMutableTreeNode parentNode = null;
        
        TreePath parentPath = jtree.getSelectionPath();

        if (parentPath == null) {
            parentNode = rootNode;            
        } else {
            parentNode = (DefaultMutableTreeNode)(parentPath.getLastPathComponent());
        }

        DefaultMutableTreeNode node =  addElement(parentNode, child, true);
        SelectElement(child);
        return node;
    }
    
    public void addRootElement(Element elem)
	{
		addElement(rootNode, elem, true);
	}
    
    

    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Element child) {
        return addElement(parent, child, false);
    }
    
    public DefaultMutableTreeNode addElement(DefaultMutableTreeNode parent, Element elem,  boolean shouldBeVisible) {
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(elem);

        if (parent == null) {
            parent = rootNode;
        }
        
        if (parent.getUserObject() instanceof Element) {
        	Element parentElem = (Element)parent.getUserObject();
        	
        	elem.ParentElement = parentElem;
        	if (!parentElem.ChildElements.contains(elem)) {
        		parentElem.ChildElements.add(elem);	
        	}
        }
                
        // It is key to invoke this on the TreeModel, and NOT DefaultMutableTreeNode
        treeModel.insertNodeInto(childNode, parent, parent.getChildCount());

        //Make sure the user can see the lovely new node.
        if (shouldBeVisible) {
            jtree.scrollPathToVisible(new TreePath(childNode.getPath()));
        }
        
        for (Element child : elem.ChildElements) {
        	addElement(childNode, child, shouldBeVisible);
        }
        
        return childNode;
    }


	public void updateUI()
	{
		jtree.updateUI();
		
	}


	


}
