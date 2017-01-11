package at.vintagestory.modelcreator.gui.right.keyframes;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.gui.right.element.ElementPositionPanel;
import at.vintagestory.modelcreator.gui.right.element.ElementRotationPanel;
import at.vintagestory.modelcreator.interfaces.IBasicElementManager;
import at.vintagestory.modelcreator.interfaces.IValueUpdater;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.model.KeyFrameElement;

public class RightKeyFramesPanel extends JPanel implements IValueUpdater, IBasicElementManager
{
	private static final long serialVersionUID = 1L;
	
	
	private ElementPositionPanel panelPosition;
	private ElementRotationPanel panelRotation;
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
		panelPosition = new ElementPositionPanel(this);
		panelRotation = new ElementRotationPanel(this);
		//panelSize = new ElementSizePanel(manager);
	}


	private void addComponents()
	{
		JPanel btnContainer = new JPanel(new GridLayout(1, 3, 4, 0));
		btnContainer.setPreferredSize(new Dimension(196, 30));
		
		Font defaultFont = new Font("SansSerif", Font.BOLD, 12);
		
		btnPos = new JToggleButton("Position");
		btnPos.setFont(defaultFont);
		btnPos.addActionListener(a ->
		{
			ModelCreator.currentProject.SelectedAnimation.TogglePosition(ModelCreator.manager.getCurrentElement(), btnPos.isSelected());
			ModelCreator.updateValues();
		});
		btnContainer.add(btnPos);
		
		
		btnRot = new JToggleButton("Rotation");
		btnRot.setFont(defaultFont);
		btnRot.addActionListener(a ->
		{
			ModelCreator.currentProject.SelectedAnimation.ToggleRotation(ModelCreator.manager.getCurrentElement(), btnRot.isSelected());
			ModelCreator.updateValues();
		});
		btnContainer.add(btnRot);
		
		
		btnStretch = new JToggleButton("Stretch");
		btnStretch.setFont(defaultFont);
		btnStretch.addActionListener(a ->
		{
			ModelCreator.currentProject.SelectedAnimation.ToggleStretch(ModelCreator.manager.getCurrentElement(), btnStretch.isSelected());
			ModelCreator.updateValues();
		});
		btnContainer.add(btnStretch);
		
		
		
		add(btnContainer);
		add(panelRotation);
		add(panelPosition);
		//add(panelSize);	
		
		updateValues();
	}
	


	@Override
	public void updateValues()
	{
		updateFrame();
	}
	
	public void updateFrame() {
		if (ModelCreator.manager == null) return;
		Element elem = ModelCreator.manager.getCurrentElement();
		
		boolean enabled = ModelCreator.currentProject.SelectedAnimation != null && elem != null && ModelCreator.currentProject.GetFrameCount() > 0;
		if (ModelCreator.currentProject.PlayAnimation) return;
		
		
		KeyFrameElement keyframeElem = enabled ? ModelCreator.currentProject.SelectedAnimation.GetKeyFrameElement(elem) : null;
		
		panelPosition.enabled = enabled && keyframeElem != null && keyframeElem.PositionSet;
		panelRotation.enabled = enabled && keyframeElem != null && keyframeElem.RotationSet;
		
		btnPos.setSelected(panelPosition.enabled);
		btnRot.setSelected(panelRotation.enabled);
		btnStretch.setSelected(enabled && keyframeElem != null && keyframeElem.StretchSet);
		
	
		panelRotation.toggleFields(keyframeElem != null ? getCurrentElement() : null);
		panelPosition.toggleFields(keyframeElem != null ? getCurrentElement() : null);
		
		
		btnPos.setEnabled(enabled);
		btnRot.setEnabled(enabled);
		btnStretch.setEnabled(enabled);		
	}
	

	@Override
	public Element getCurrentElement()
	{
		if (ModelCreator.manager==null) return null;
		
		Element elem = ModelCreator.manager.getCurrentElement();
		if (elem == null || ModelCreator.currentProject.SelectedAnimation == null) return null;
		
		return ModelCreator.currentProject.SelectedAnimation.GetOrCreateKeyFrameElement(elem);
	}

}
