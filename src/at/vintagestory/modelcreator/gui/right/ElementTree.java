package at.vintagestory.modelcreator.gui.right;

import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import javax.swing.DropMode;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
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
        jtree.setEditable(false);
        jtree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jtree.setShowsRootHandles(true);
        jtree.setCellRenderer(new ElementTreeCellRenderer());
        jtree.setDragEnabled(false);
        jtree.setDropMode(DropMode.ON_OR_INSERT);
        
		jtree.addTreeSelectionListener(new TreeSelectionListener()
		{
			@Override
			public void valueChanged(TreeSelectionEvent e)
			{
				
				if (!ModelCreator.ignoreValueUpdates) {
					ModelCreator.currentProject.SelectedElement = getSelectedElement();
					ModelCreator.updateValues(jtree);	
				}
			}	
		});
		
		jtree.addMouseListener(new MouseListener()
		{
			
			@Override
			public void mouseReleased(MouseEvent arg0)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent arg0)
			{
				TreePath path = jtree.getPathForLocation(arg0.getX(), arg0.getY());
				TreePath pathLeft = jtree.getPathForLocation(arg0.getX() - 17, arg0.getY());
				
				if (path != null && pathLeft == null) {
					arg0.consume();
					
					Object userObj = ((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject(); 
		        	if (userObj instanceof Element) {
		        		((Element)userObj).Render = !((Element)userObj).Render;
		        		jtree.updateUI();
		        	}
				}
				
			}
			
			@Override
			public void mouseExited(MouseEvent arg0)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				// TODO Auto-generated method stub
				
			}
		});
		
		jtree.setTransferHandler(new TreeTransferHandler(this));
		
	}
	

	public void clearElements()
	{
		rootNode.removeAllChildren();
		jtree.removeAll();
		jtree.updateUI();
		jtree.clearSelection();
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
	
	
	public void selectElement(Element elem) {
		jtree.clearSelection();
		
		Enumeration<TreeNode> enumer = rootNode.breadthFirstEnumeration();
		while (enumer.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)enumer.nextElement();
			if (node.getUserObject().equals(elem)) {
				jtree.setSelectionPath(new TreePath(node.getPath()));
				break;
			}
		}
	}
	
	public void selectElementByOpenGLName(int opengglname) {
		jtree.clearSelection();
		
		Enumeration<TreeNode> enumer = rootNode.breadthFirstEnumeration();
		
		while (enumer.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)enumer.nextElement();
			
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

        DefaultMutableTreeNode node = addElement(parentNode, child, true);
        selectElement(child);
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
        selectElement(child);
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
