package at.vintagestory.modelcreator.gui.right;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.HashSet;

import javax.swing.DropMode;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
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
	
	public HashSet<String> collapsedPaths = new HashSet<String>();
	
	boolean ignoreExpandCollapse;
	
	
		
	public ElementTree() {
		rootNode = new DefaultMutableTreeNode("Root");
		treeModel = new DefaultTreeModel(rootNode);
        jtree = new FixedJTree(treeModel);
        jtree.setEditable(false);
        jtree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jtree.setShowsRootHandles(true);
        jtree.setFocusable(true);
        jtree.setCellRenderer(new ElementTreeCellRenderer());
        jtree.setDragEnabled(true);
        jtree.setDropMode(DropMode.ON_OR_INSERT);
        
		jtree.addTreeSelectionListener(new TreeSelectionListener()
		{
			@Override
			public void valueChanged(TreeSelectionEvent e)
			{
				
				if (!ModelCreator.ignoreValueUpdates) {
					ModelCreator.currentProject.SelectedElement = getSelectedElement();
					if (ModelCreator.currentProject.SelectedElement != null) {
						ModelCreator.currentProject.SelectedElement.elementWasSelected();
					}
					ModelCreator.updateValues(jtree);	
				}
			}	
		});
		
		// does not work, wtf?
		jtree.addKeyListener(new KeyListener()
		{
			
			@Override
			public void keyTyped(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					jtree.setSelectionRow(Math.max(0, jtree.getSelectionRows()[0] - 1));
				}
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					jtree.setSelectionRow(Math.max(0, jtree.getSelectionRows()[0] + 1));
				}
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)jtree.getLastSelectedPathComponent();
					jtree.collapsePath(new TreePath(selectedNode.getPath()));
				}
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)jtree.getLastSelectedPathComponent();
					jtree.expandPath(new TreePath(selectedNode.getPath()));
				}
				
			}
			
			@Override
			public void keyReleased(KeyEvent e)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e)
			{
				
				
				
			}
		});
		
		jtree.addTreeExpansionListener(new TreeExpansionListener()
		{
			
			@Override
			public void treeExpanded(TreeExpansionEvent arg0)
			{
				if (ignoreExpandCollapse) return;
				TreePath path = arg0.getPath();
				collapsedPaths.remove(path.toString());
			}
			
			@Override
			public void treeCollapsed(TreeExpansionEvent arg0)
			{
				if (ignoreExpandCollapse) return;
				
				TreePath path = arg0.getPath();		        
		        collapsedPaths.add(path.toString());
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
					
					Object userObj = ((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject(); 
		        	if (userObj instanceof Element) {
		        		((Element)userObj).setRenderInEditor(!((Element)userObj).getRenderInEditor());
		        		jtree.updateUI();
		        		arg0.consume();
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
	
	public Element getNextSelectedElement() {
		TreePath currentSelection = jtree.getSelectionPath();
		if (currentSelection != null) {
        	DefaultMutableTreeNode node = ((DefaultMutableTreeNode)currentSelection.getLastPathComponent());
        	if (node.getNextSibling() != null) { 
        		return (Element)node.getNextSibling().getUserObject();
        	}
        	if (node.getPreviousSibling() != null) { 
        		return (Element)node.getPreviousSibling().getUserObject();
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
				jtree.scrollPathToVisible(new TreePath(node.getPath()));
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
				Element elem = (Element)node.getUserObject();
				for (int i = 0; i < elem.getAllFaces().length; i++) {
					if (elem.getAllFaces()[i].openGlName == opengglname) {
						jtree.setSelectionPath(new TreePath(node.getPath()));
						return;
					}
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

        DefaultMutableTreeNode node = addElement(parentNode, child, true);
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
        
        ignoreExpandCollapse = true;
                
        // It is key to invoke this on the TreeModel, and NOT DefaultMutableTreeNode
        treeModel.insertNodeInto(childNode, parent, parent.getChildCount());

        //Make sure the user can see the lovely new node.
        if (shouldBeVisible) {
            jtree.scrollPathToVisible(new TreePath(childNode.getPath()));
        }
        
        for (Element child : elem.ChildElements) {
        	addElement(childNode, child, shouldBeVisible);
        }
        
        String path = new TreePath(childNode.getPath()).toString();
        if (collapsedPaths.contains(path)) {
        	jtree.collapsePath(new TreePath(childNode.getPath()));
        } else {
            jtree.expandPath(new TreePath(childNode.getPath()));
        }
        
        ignoreExpandCollapse = false;
        
        
        return childNode;
    }


	public void updateUI()
	{
		jtree.updateUI();
		
	}


	public static class FixedJTree extends JTree {
		
		private static final long serialVersionUID = 1L;
		
		public FixedJTree(TreeModel arg0)
		{
			super(arg0);
		}

		@Override
	    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	        // filter property change of "dropLocation" with newValue==null, 
	        // since this will result in a NPE in BasicTreeUI.getDropLineRect(...)
	        if(newValue!=null || !"dropLocation".equals(propertyName)) {
	            super.firePropertyChange(propertyName, oldValue, newValue);
	        }
	    }
		
	}


}
