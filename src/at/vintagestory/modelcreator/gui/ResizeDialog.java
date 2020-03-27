package at.vintagestory.modelcreator.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.model.Animation;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.model.KeyFrameElement;
import at.vintagestory.modelcreator.model.Keyframe;

public class ResizeDialog
{
	public static void show(JFrame parent)
	{
		JDialog dialog = new JDialog(parent, "Resize Element", false);
		
		JPanel container = new JPanel(new BorderLayout(20, 10));
		container.setBorder(new EmptyBorder(10, 10, 0, 10));
		
		
		JPanel panelRow1 = new JPanel(new GridLayout(6, 1, 5, 0));
		JLabel label = new JLabel("Size Multiplier");
		label.setPreferredSize(new Dimension(200, 20));
		panelRow1.add(label);
		
		label = new JLabel("Will be applied to the selected element tree");
		label.setPreferredSize(new Dimension(250, 20));
		panelRow1.add(label);
		
		
		JTextField scaleTextField = new JTextField();
		scaleTextField.setPreferredSize(new Dimension(50, 20));
		scaleTextField.setText("1");
		panelRow1.add(scaleTextField);

		JCheckBox btnScaleUv = new JCheckBox("Scale UV");
		btnScaleUv.setToolTipText("If off, it will not rescale the UV, but will cause wonky behavior if UVs are moved");
		btnScaleUv.setSelected(true);
		panelRow1.add(btnScaleUv);

		label = new JLabel("");
		label.setPreferredSize(new Dimension(250, 20));
		panelRow1.add(label);
		
		container.add(panelRow1, BorderLayout.CENTER);
		
		
				
		JButton btnSubmit = new JButton("Apply");
		btnSubmit.setIcon(Icons.disk);
		
		panelRow1.add(btnSubmit);
		
		btnSubmit.addActionListener(a ->
		{
			try {
				ModelCreator.changeHistory.beginMultichangeHistoryState();
				
				Element elem = ModelCreator.currentProject.SelectedElement;
				float size = Float.parseFloat(scaleTextField.getText());
				boolean scaleUV =  btnScaleUv.isSelected();
				
				Resize(elem, size, 0, scaleUV);
				
				ModelCreator.changeHistory.endMultichangeHistoryState(ModelCreator.currentProject);
				
				dialog.dispose();
			} catch (Exception e) {
				
			}
		});
		
		
		dialog.setResizable(false);
		
		dialog.add(container);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		dialog.requestFocusInWindow();
	}

	
	private static void Resize(Element elem, float size, int depth, boolean scaleUV)
	{
		if (elem == null) return;
		
		
		for (Animation anim : ModelCreator.currentProject.Animations) {
			for (Keyframe keyf : anim.keyframes) {
				KeyFrameElement keyfelem = keyf.GetKeyFrameElement(elem);
				if (keyfelem != null) {
					keyfelem.scaleAll(size);
				}
			}
		}
		
		elem.scaleAll(size, scaleUV);
		
		for (Element childElem : elem.ChildElements) {
			Resize(childElem, size, depth+1, scaleUV);
		}
		
		ModelCreator.DidModify();
		ModelCreator.updateValues(null);
	}
}
