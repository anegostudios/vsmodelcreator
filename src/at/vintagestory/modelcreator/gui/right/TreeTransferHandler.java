package at.vintagestory.modelcreator.gui.right;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.util.GameMath;
import at.vintagestory.modelcreator.util.Mat4f;
import at.vintagestory.modelcreator.util.QUtil;

class TreeTransferHandler extends TransferHandler {
	private static final long serialVersionUID = 1L;
	DataFlavor nodesFlavor;
    DataFlavor[] flavors = new DataFlavor[1];
    DefaultMutableTreeNode[] nodesToRemove;
    ElementTree elemTree;
    
    public TreeTransferHandler(ElementTree elemTree) {
    	this.elemTree = elemTree;
    			
        try {
            String mimeType = DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + javax.swing.tree.DefaultMutableTreeNode[].class.getName() + "\"";
            nodesFlavor = new DataFlavor(mimeType);
            flavors[0] = nodesFlavor;
        } catch(ClassNotFoundException e) {
            System.out.println("ClassNotFound: " + e.getMessage());
        }
    }
  
    public boolean canImport(TransferHandler.TransferSupport support) {
        if(!support.isDrop()) {
            return false;
        }
        support.setShowDropLocation(true);
        if(!support.isDataFlavorSupported(nodesFlavor)) {
            return false;
        }
        
        // Do not allow a drop on the drag source selections.
        JTree.DropLocation dl = (JTree.DropLocation)support.getDropLocation();
        JTree tree = (JTree)support.getComponent();
        int dropRow = tree.getRowForPath(dl.getPath());
        int[] selRows = tree.getSelectionRows();
        for (int i = 0; i < selRows.length; i++) {
            if (selRows[i] == dropRow) {
                return false;
            }
        }
        // Do not allow MOVE-action drops if trying to drop into their own children
        TreePath dragpath = tree.getPathForRow(selRows[0]);
        if (dragpath.isDescendant(dl.getPath())) {
        	return false;
        }
        
        return true;
    }
  
    
    protected Transferable createTransferable(JComponent c) {
        JTree tree = (JTree)c;
        TreePath[] paths = tree.getSelectionPaths();
        if(paths != null) {
            // Make up a node array of copies for transfer and
            // another for/of the nodes that will be removed in
            // exportDone after a successful drop.
            List<DefaultMutableTreeNode> copies = new ArrayList<DefaultMutableTreeNode>();
            List<DefaultMutableTreeNode> toRemove = new ArrayList<DefaultMutableTreeNode>();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)paths[0].getLastPathComponent();
            DefaultMutableTreeNode copy = deepcopy(node);
            copies.add(copy);
            toRemove.add(node);
            
            for (int i = 1; i < paths.length; i++) {
                DefaultMutableTreeNode next = (DefaultMutableTreeNode)paths[i].getLastPathComponent();
                // Do not allow higher level nodes to be added to list.
                if(next.getLevel() < node.getLevel()) {
                    break;
                } else if(next.getLevel() > node.getLevel()) {  // child node
                    copy.add(deepcopy(next));
                    // node already contains child
                } else {                                        // sibling
                    copies.add(deepcopy(next));
                    toRemove.add(next);
                }
            }
            
            DefaultMutableTreeNode[] nodes = copies.toArray(new DefaultMutableTreeNode[copies.size()]);
            nodesToRemove = toRemove.toArray(new DefaultMutableTreeNode[toRemove.size()]);
            return new NodesTransferable(nodes);
        }
        
        return null;
    }
  
    /** Defensive copy used in createTransferable. */
    private DefaultMutableTreeNode deepcopy(DefaultMutableTreeNode node) {
    	DefaultMutableTreeNode clone = (DefaultMutableTreeNode) node.clone();
    	
    	int cnt = node.getChildCount();
    	for (int i = 0; i < cnt; i++) {
    		clone.add(deepcopy((DefaultMutableTreeNode)node.getChildAt(i)));
    	}
    	
    	return clone;
    }
  
    protected void exportDone(JComponent source, Transferable data, int action) {
        if((action & MOVE) == MOVE) {
            JTree tree = (JTree)source;
            DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
            // Remove nodes saved in nodesToRemove in createTransferable.
            for(int i = 0; i < nodesToRemove.length; i++) {
            	if(nodesToRemove[i].getParent() != null)
            		model.removeNodeFromParent(nodesToRemove[i]);
            }
        }
    }
  
    public int getSourceActions(JComponent c) {
        return MOVE;//COPY_OR_MOVE;
    }
  
    public boolean importData(TransferHandler.TransferSupport support) {
        if(!canImport(support)) {
            return false;
        }
        
        // Extract transfer data.
        DefaultMutableTreeNode[] nodes = null;
        try {
            Transferable t = support.getTransferable();
            nodes = (DefaultMutableTreeNode[])t.getTransferData(nodesFlavor);
        } catch(UnsupportedFlavorException ufe) {
            System.out.println("UnsupportedFlavor: " + ufe.getMessage());
        } catch(java.io.IOException ioe) {
            System.out.println("I/O error: " + ioe.getMessage());
        }
        
        // Get drop location info.
        JTree.DropLocation dl = (JTree.DropLocation)support.getDropLocation();
        int childIndex = dl.getChildIndex();
        TreePath dest = dl.getPath();
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode)dest.getLastPathComponent();
        JTree tree = (JTree)support.getComponent();
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        // Configure for drop mode.
        int index = childIndex;    // DropMode.INSERT
        if(childIndex == -1) {     // DropMode.ON
            index = parent.getChildCount();
        }
        
        ModelCreator.changeHistory.beginMultichangeHistoryState();
        
        List<Element> oldParentPath = new ArrayList<Element>();
        List<Element> newParentPath = new ArrayList<Element>();
        
        // Add data to model.
        for(int i = 0; i < nodes.length; i++) {       	
            model.insertNodeInto(nodes[i], parent, index);
            Element ownElem = (Element)nodes[i].getUserObject();
            
            Element oldParent = ownElem.ParentElement; 
            Element newParent = parent.getUserObject() instanceof Element ? (Element) parent.getUserObject() : null;
            
        	int prevIndex = oldParent == null ? ModelCreator.currentProject.rootElements.indexOf(ownElem) : oldParent.ChildElements.indexOf(ownElem);
            
            // Make sure the element is removed everywhere. (Step-parented elements might be a root element *and* have a parent element)
        	oldParentPath = ownElem.GetParentPath();
        	if (oldParent != null) {
        		oldParent.ChildElements.remove(ownElem);
        		oldParent.StepChildElements.remove(ownElem);
        	}
        	ModelCreator.currentProject.rootElements.remove(ownElem);
        	
        	int insertIndex = oldParent == newParent && index >= prevIndex ? index - 1 : index;
            
            if (newParent != null) {
            	newParent.ChildElements.add(insertIndex, ownElem);
                // Must not have a step parent when parented 
	            ownElem.setStepParent(null);                
            } else {
            	ModelCreator.currentProject.rootElements.add(insertIndex, ownElem);
            }
            
            index++;
            
            ownElem.ParentElement = newParent;
            
            newParentPath = ownElem.GetParentPath();
            
            if (ModelCreator.repositionWhenReparented) {
            	applyReparentTransform(ownElem, oldParentPath, newParentPath);
            }
            
            
            tree.setSelectionPath(new TreePath(nodes[i].getPath()));
        }
        
        tree.expandPath(dest);
        
        ModelCreator.updateValues(tree);
        tree.updateUI();
        ModelCreator.DidModify();
        
        ModelCreator.changeHistory.endMultichangeHistoryState(ModelCreator.currentProject);
        
        return true;
    }
   
    
  
    private void applyReparentTransform(Element ownElem, List<Element> oldParentPath, List<Element> newParentPath)
	{
        ModelCreator.ignoreValueUpdates = true;

        float[] matrix = Mat4f.Create();
        
        for (int j = 0; j < newParentPath.size(); j++) {
        	Element p = newParentPath.get(j);
        	p.ApplyTransform(matrix);
        }
        Mat4f.Invert(matrix, matrix);
        
        for (int j = 0; j < oldParentPath.size(); j++) {
        	Element p = oldParentPath.get(j);
        	p.ApplyTransform(matrix);
        }

        
        float[] originpos = Mat4f.MulWithVec4(matrix, new float[] { (float)ownElem.getOriginX(), (float)ownElem.getOriginY(), (float)ownElem.getOriginZ(), 1 });
        
        ownElem.ApplyTransform(matrix);
        double[] angles = QUtil.MatrixToEuler(matrix);

        ownElem.setOriginX(originpos[0]);
        ownElem.setOriginY(originpos[1]);
        ownElem.setOriginZ(originpos[2]);

        ownElem.setRotationX(-angles[0] * GameMath.RAD2DEG);
        ownElem.setRotationY(-angles[1] * GameMath.RAD2DEG);
        ownElem.setRotationZ(-angles[2] * GameMath.RAD2DEG);

        Mat4f.Invert(matrix, matrix);
        ownElem.ApplyTransform(matrix);
        Mat4f.Invert(matrix, matrix);

        float[] startpos = Mat4f.MulWithVec4(matrix, new float[] { (float)ownElem.getStartX(), (float)ownElem.getStartY(), (float)ownElem.getStartZ(), 1 });

    	ownElem.setStartX(startpos[0]);
    	ownElem.setStartY(startpos[1]);
    	ownElem.setStartZ(startpos[2]);

    	ModelCreator.ignoreValueUpdates = false;
	}
    
    
    
    
    
    
	public String toString() {
        return getClass().getName();
    }
  
    public class NodesTransferable implements Transferable {
        DefaultMutableTreeNode[] nodes;
  
        public NodesTransferable(DefaultMutableTreeNode[] nodes) {
            this.nodes = nodes;
         }
  
        public Object getTransferData(DataFlavor flavor)
                                 throws UnsupportedFlavorException {
            if(!isDataFlavorSupported(flavor))
                throw new UnsupportedFlavorException(flavor);
            return nodes;
        }
  
        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }
  
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return nodesFlavor.equals(flavor);
        }
    }
}