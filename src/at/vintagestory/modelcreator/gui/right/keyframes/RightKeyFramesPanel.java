package at.vintagestory.modelcreator.gui.right.keyframes;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.interfaces.IValueUpdater;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.model.KeyFrameElement;

public class RightKeyFramesPanel extends JPanel implements IValueUpdater
{
	private static final long serialVersionUID = 1L;
	
	
	private ElementKeyFrameOffsetPanel panelPosition;
	private ElementKeyFrameRotationPanel panelRotation;
	//private ElementSizePanel panelSize;
	
	
	
	JToggleButton btnPos;
	JToggleButton btnRot;
	JToggleButton btnStretch;
	
	public RightKeyFramesPanel()
	{
		initComponents();
		addComponents();
	}

	private void initComponents()
	{
		panelPosition = new ElementKeyFrameOffsetPanel(this);
		panelRotation = new ElementKeyFrameRotationPanel(this);
		//panelSize = new ElementSizePanel(manager);
	}


	private void addComponents()
	{
		JPanel btnContainer = new JPanel(new GridLayout(1, 3, 4, 0));
		btnContainer.setPreferredSize(new Dimension(196, 30));
		
		Font defaultFont = new Font("SansSerif", Font.BOLD, 11);
		

		btnRot = new JToggleButton("Rotation");
		btnRot.setFont(defaultFont);
		btnRot.addActionListener(a ->
		{
			ModelCreator.currentProject.SelectedAnimation.ToggleRotation(ModelCreator.manager.getCurrentElement(), btnRot.isSelected());
			ModelCreator.updateValues(btnRot);
		});
		btnContainer.add(btnRot);
		
		
		btnPos = new JToggleButton("Position");
		btnPos.setFont(defaultFont);
		btnPos.addActionListener(a ->
		{
			ModelCreator.currentProject.SelectedAnimation.TogglePosition(ModelCreator.manager.getCurrentElement(), btnPos.isSelected());
			ModelCreator.updateValues(btnPos);
		});
		btnContainer.add(btnPos);
		
		
		
		btnStretch = new JToggleButton("Stretch");
		btnStretch.setFont(defaultFont);
		btnStretch.addActionListener(a ->
		{
			ModelCreator.currentProject.SelectedAnimation.ToggleStretch(ModelCreator.manager.getCurrentElement(), btnStretch.isSelected());
			ModelCreator.updateValues(btnStretch);
		});
		btnContainer.add(btnStretch);
		
		
		
		add(btnContainer);
		add(panelRotation);
		add(panelPosition);
		//add(panelSize);	
		
		updateValues(null);
	}
	


	@Override
	public void updateValues(JComponent byGuiElem)
	{
		updateFrame(byGuiElem);
	}
	
	public void updateFrame(JComponent byGuiElem) {
		if (ModelCreator.manager == null) return;
		Element elem = ModelCreator.manager.getCurrentElement();
		
		boolean enabled = ModelCreator.currentProject.SelectedAnimation != null && elem != null && ModelCreator.currentProject.GetFrameCount() > 0;
		if (ModelCreator.currentProject.PlayAnimation) return;
		
		
		KeyFrameElement keyframeElem = enabled ? ModelCreator.currentProject.SelectedAnimation.GetKeyFrameElement(elem, ModelCreator.currentProject.SelectedAnimation.currentFrame) : null;
		
		panelPosition.enabled = enabled && keyframeElem != null && keyframeElem.PositionSet;
		panelRotation.enabled = enabled && keyframeElem != null && keyframeElem.RotationSet;
		
		btnPos.setSelected(panelPosition.enabled);
		btnRot.setSelected(panelRotation.enabled);
		btnStretch.setSelected(enabled && keyframeElem != null && keyframeElem.StretchSet);
		
	
		panelRotation.toggleFields(keyframeElem != null ? getCurrentElement() : null, byGuiElem);
		panelPosition.toggleFields(keyframeElem != null ? getCurrentElement() : null, byGuiElem);
		
		
		btnPos.setEnabled(enabled);
		btnRot.setEnabled(enabled);
		btnStretch.setEnabled(enabled);		
	}
	

	public KeyFrameElement getCurrentElement()
	{
		if (ModelCreator.manager == null) return null;
		
		Element elem = ModelCreator.manager.getCurrentElement();
		if (elem == null || ModelCreator.currentProject.SelectedAnimation == null) return null;
		
		return ModelCreator.currentProject.SelectedAnimation.GetKeyFrameElement(elem, ModelCreator.currentProject.SelectedAnimation.currentFrame);
	}

}
