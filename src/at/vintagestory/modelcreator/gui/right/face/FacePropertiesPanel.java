package at.vintagestory.modelcreator.gui.right.face;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.Start;
import at.vintagestory.modelcreator.enums.BlockFacing;
import at.vintagestory.modelcreator.gui.ComponentUtil;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.interfaces.IValueUpdater;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.model.Face;
import at.vintagestory.modelcreator.util.AwtUtil;
import at.vintagestory.modelcreator.util.Mat4f;
import at.vintagestory.modelcreator.util.Vec3f;

public class FacePropertiesPanel extends JPanel implements IValueUpdater
{
	private static final long serialVersionUID = 1L;

	private IElementManager manager;

	private JPanel horizontalBox;
	private JRadioButton boxEnabled;
	private JRadioButton boxAutoUV;
	private JRadioButton boxSnapUv;
	private JTextField glowValue;
	private JTextField windData;
	
	JComboBox<String> bla = new JComboBox<String>();
	@SuppressWarnings("unchecked")
	private JComboBox<String>[] windModeList = (JComboBox<String>[]) Array.newInstance(bla.getClass(), 4);

	private JComboBox<String> reflectiveMode;
	
	public FacePropertiesPanel(IElementManager manager)
	{
		this.manager = manager;
		setLayout(new BorderLayout(0, 5));
		setBorder(BorderFactory.createTitledBorder(Start.Border, "<html><b>Properties</b></html>"));
		setMaximumSize(new Dimension(250, 500));
		initComponents();
		addComponents();
	}

	public void initComponents()
	{
		horizontalBox = new JPanel(new GridLayout(0, 1));
		
		boxEnabled = ComponentUtil.createRadioButton("Enabled","<html>Determines if face should be rendered<br>Default: On</html>");
		boxEnabled.addActionListener(e ->
		{
			ModelCreator.changeHistory.beginMultichangeHistoryState();
			
			if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1) {
				Element elem = manager.getCurrentElement();
				for (int i = 0; i < elem.getAllFaces().length; i++) {
					Face face = elem.getAllFaces()[i];
					face.setEnabled(boxEnabled.isSelected());
				}
				
			} else {
				manager.getCurrentElement().getSelectedFace().setEnabled(boxEnabled.isSelected());
			}
			
			ModelCreator.changeHistory.endMultichangeHistoryState(ModelCreator.currentProject);
		});
		
		boxAutoUV = ComponentUtil.createRadioButton("Auto Resolution", "<html>Automatically sets the UV end coordinates to fit the desired texture resolution<br>Default: On</html>");
		boxAutoUV.addActionListener(e ->
		{
			boolean on = boxAutoUV.isSelected();
			ModelCreator.changeHistory.beginMultichangeHistoryState();
			
			if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1) {
				Element elem = manager.getCurrentElement();
				for (int i = 0; i < elem.getAllFaces().length; i++) {
					Face face = elem.getAllFaces()[i];
					face.setAutoUVEnabled(on);
					face.updateUV();
				}
				
			} else {
				manager.getCurrentElement().getSelectedFace().setAutoUVEnabled(on);
				manager.getCurrentElement().getSelectedFace().updateUV();

			}
			
			ModelCreator.updateValues(boxAutoUV);
			ModelCreator.changeHistory.endMultichangeHistoryState(ModelCreator.currentProject);
		});
		
		
		boxSnapUv = ComponentUtil.createRadioButton("Snap UV", "<html>Determines if auto-uv should snap the coordinates to pixels on the texture. Disable if your element is very small or want full control over the UV Coordinates<br>Default: On</html>");
		boxSnapUv.addActionListener(e ->
		{
			ModelCreator.changeHistory.beginMultichangeHistoryState();
			
			if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 1) {
				Element elem = manager.getCurrentElement();
				for (int i = 0; i < elem.getAllFaces().length; i++) {
					Face face = elem.getAllFaces()[i];
					face.setSnapUVEnabled(boxSnapUv.isSelected());
				}
				
			} else {
				manager.getCurrentElement().getSelectedFace().setSnapUVEnabled(boxSnapUv.isSelected());
			}
			
			
			manager.getCurrentElement().updateUV();
			ModelCreator.updateValues(boxSnapUv);
			ModelCreator.changeHistory.endMultichangeHistoryState(ModelCreator.currentProject);
		});
		
		glowValue = new JTextField();
		
		
		AwtUtil.addChangeListener(glowValue, e -> {
			try {
				manager.getCurrentElement().getSelectedFace().setGlow(Integer.parseInt(glowValue.getText()));	
			} catch(Exception ex) {
				
			}
		});
				
		horizontalBox.add(boxEnabled);
		horizontalBox.add(boxAutoUV);
		horizontalBox.add(boxSnapUv);
		horizontalBox.add(new JLabel("Glow Level (0..255)"));
		horizontalBox.add(glowValue);
		horizontalBox.add(new JLabel("Reflective Mode"));
		
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		model.addElement("Not reflective");
		model.addElement("Weakly random reflective");
		model.addElement("Weakly reflective");
		model.addElement("Strongly reflective");
		
		reflectiveMode = new JComboBox<String>();
		reflectiveMode.setModel(model);
		reflectiveMode.setToolTipText("Sets the reflectivity of the face.");
		reflectiveMode.addActionListener(e ->
		{
			if (ModelCreator.ignoreValueUpdates) return;
			if (manager.getCurrentElement() != null)
			{
				manager.getCurrentElement().getSelectedFace().setReflectiveMode(reflectiveMode.getSelectedIndex());
				updateValues(reflectiveMode);
			}
		});
		
		horizontalBox.add(reflectiveMode);
		
		for (int i = 0; i < 4; i++) {
			int index=i;
			JComboBox<String> list = windModeList[i] = new JComboBox<String>(); 
			
			list.setToolTipText("Defines the wind sway behavior.");
			DefaultComboBoxModel<String> modelr = windModeList();		
			list.setModel(modelr);
			list.setPreferredSize(new Dimension(190, 25));
			
			list.addActionListener(e -> {
				if (ModelCreator.ignoreValueUpdates) return;
				
				Element elem = manager.getCurrentElement();
				if (elem != null) {
					Face sface = elem.getSelectedFace();
					
					int prevmode = -1;
					if (sface.WindModes != null) prevmode = sface.WindModes[index];
					
					int newmode = list.getSelectedIndex() - 1;
					
					if (sface.WindModes == null) sface.WindModes = new int[] { -1, -1, -1, -1};
					sface.WindModes[index]=newmode;
					
					boolean allFaces = (e.getModifiers() & ActionEvent.SHIFT_MASK) == 1 && (e.getModifiers() & ActionEvent.CTRL_MASK) == 2;  
					
					if (allFaces) {
						for (int k = 0; k < 6; k++) {
							Face f = elem.getAllFaces()[k];
							if (!f.isEnabled()) continue;
							if (f.WindModes == null) f.WindModes = new int[] { -1, -1, -1, -1};
							for (int l = 0; l < 4; l++) {
								f.WindModes[l]=newmode;
							}
						}
					}

					boolean modified = prevmode != newmode;
					
					Face[] faces = new Face[] { sface };
					if (allFaces) faces = elem.getAllFaces();
					
					for (Face face : faces) {					
						int[] windData = getWindData(elem, face);
						if (face.WindData != null || windData[0] != 0 || windData[1] != 0 || windData[2] != 0 || windData[3] != 0) {
							modified |= face.WindData == null || face.WindData[0] != windData[0] || face.WindData[1] != windData[1] || face.WindData[2] != windData[2] || face.WindData[3] != windData[3];
							face.WindData = windData;
						}
					}
					
					
					if (modified) ModelCreator.DidModify();
					
					ModelCreator.updateValues(list);
				}
			});
			list.addMouseListener(new MouseListener()
			{
				
				@Override
				public void mouseReleased(MouseEvent e)
				{
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void mousePressed(MouseEvent e)
				{
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void mouseExited(MouseEvent e)
				{
					Element elem = manager.getCurrentElement();
					if (elem != null) {
						Face face = elem.getSelectedFace();
						if (face != null) {
							face.HoveredVertex = -1;
						}
					}
				}
				
				@Override
				public void mouseEntered(MouseEvent e)
				{
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void mouseClicked(MouseEvent e)
				{
					// TODO Auto-generated method stub
					
				}
			});
			list.addMouseMotionListener(new MouseMotionListener()
			{
				
				@Override
				public void mouseMoved(MouseEvent e)
				{
					Element elem = manager.getCurrentElement();
					if (elem != null) {
						Face face = elem.getSelectedFace();
						if (face != null) {
							face.HoveredVertex = index;
						}
						
					}	
					
				}
				
				@Override
				public void mouseDragged(MouseEvent e)
				{
					
					
				}
			});
			
			
			horizontalBox.add(new JLabel("Wind mode " + (i+1)));
			horizontalBox.add(list);
		}
		
		
		
		windData = new JTextField();
		horizontalBox.add(new JLabel("Wind Data"));
		horizontalBox.add(windData);
		AwtUtil.addChangeListener(windData, e -> {
			if (ModelCreator.ignoreValueUpdates) return;
			
			try {
				String text = windData.getText();
				String[] parts = text.split(",");
				if (parts.length == 4) {
					manager.getCurrentElement().getSelectedFace().WindData = new int[] { Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]) };
					ModelCreator.DidModify();
				}					
			} catch(Exception ex) {
				
			}
		});
	}

	private int[] getWindData(Element elem, Face face)
	{
		ArrayList<Element> elemPath = new ArrayList<Element>();
		elemPath.add(elem);
		Element celem = elem;
		float[] matrix = Mat4f.Create();
		while (celem.ParentElement != null) {
			elemPath.add(celem = celem.ParentElement);
		}
		for (int j = elemPath.size() - 1; j >= 0; j--) {
			elemPath.get(j).ApplyTransform(matrix);
		}
		
		Vec3f sizeXyz = new Vec3f(
            (float)(elem.getWidth()) / 1f,
            (float)(elem.getHeight()) / 1f,
            (float)(elem.getDepth()) / 1f
        );
		Vec3f centerVec = new Vec3f(sizeXyz.X / 2, sizeXyz.Y / 2, sizeXyz.Z / 2);
		
		int[] windData = new int[4];
		
		int coordIndex = BlockFacing.ALLFACES[face.getSide()].GetIndex() * 12;
		
		for (int i = 0; i < 4; i++) {
			
			float x = centerVec.X + sizeXyz.X * Face.CubeVertices[coordIndex + i*3] / 2;
			float y = centerVec.Y + sizeXyz.Y * Face.CubeVertices[coordIndex + i*3 + 1] / 2;
			float z = centerVec.Z + sizeXyz.Z * Face.CubeVertices[coordIndex + i*3 + 2] / 2;
			
			float[] facematrix = Mat4f.Translate(new float[16], matrix, new float[] {0,0,0});
			float[] sdf = Mat4f.MulWithVec4(facematrix, new float[] { x, y, z, 1 });
			float ypos = sdf[1] / 16f;
			
			windData[i] = (int)ypos;
		}
		
		return windData;
	}

	public void addComponents()
	{
		add(horizontalBox, BorderLayout.NORTH);
	}

	@Override
	public void updateValues(JComponent byGuiElem)
	{
		Element cube = manager.getCurrentElement();
		
		boxEnabled.setEnabled(cube != null);
		boxEnabled.setSelected(cube != null);
		boxAutoUV.setEnabled(cube != null);
		boxAutoUV.setSelected(cube != null);
		boxSnapUv.setEnabled(cube != null);
		glowValue.setEnabled(cube != null);
		
		windModeList[0].setEnabled(cube != null);
		windModeList[1].setEnabled(cube != null);
		windModeList[2].setEnabled(cube != null);
		windModeList[3].setEnabled(cube != null);
		
		windData.setEnabled(false);
		
		
		if (cube != null)
		{
			Face face = cube.getSelectedFace();
			
			boxEnabled.setSelected(face.isEnabled());
			boxAutoUV.setSelected(face.isAutoUVEnabled());
			boxSnapUv.setSelected(face.isSnapUvEnabled());
			if (byGuiElem != glowValue) glowValue.setText(""+face.getGlow());
			
			for (int i = 0; i < 4; i++) {
				if (face.WindModes == null) {
					windModeList[i].setSelectedIndex(0);
				} else {				
					windModeList[i].setSelectedIndex(face.WindModes[i] + 1);
				}
			}
			
			if (face.WindData != null) {
				String windDataStr = face.WindData[0]+","+face.WindData[1]+","+face.WindData[2]+","+face.WindData[3];
				if (windData.getText() != windDataStr) {
					windData.setText(windDataStr);
				}
			} else {
				windData.setText("");
			}
		}
	}
	

	private DefaultComboBoxModel<String> windModeList()
	{
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		
		model.addElement("<html><b>Default</b></html>");       // null
		model.addElement("<html><b>NoWind</b></html>");        // 0
		model.addElement("<html><b>WeakWind</b></html>");      // 1
		model.addElement("<html><b>NormalWind</b></html>");    // 2
		model.addElement("<html><b>Leaves</b></html>");        // 3
		model.addElement("<html><b>Bend</b></html>");          // 4
		model.addElement("<html><b>Tallbend</b></html>");      // 5
		model.addElement("<html><b>Water</b></html>");         // 6
		model.addElement("<html><b>ExtraWeakWind</b></html>"); // 7
		model.addElement("<html><b>Fruit</b></html>");         // 8
		model.addElement("<html><b>WeakWindNoBend</b></html>");         // 9
		model.addElement("<html><b>Inverse Bend (Vines)</b></html>");         // 10
		
		model.addElement("<html><b>Waterplant (Seaweed)</b></html>");         // 11
		model.addElement("<html><b>Follow Water waves</b></html>");         // 12
		model.addElement("<html><b>Weak wind + reduced alphatest</b></html>");         // 13
		
		return model;
	}

}
