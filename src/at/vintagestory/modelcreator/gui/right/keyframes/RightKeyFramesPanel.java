package at.vintagestory.modelcreator.gui.right.keyframes;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.Project;
import at.vintagestory.modelcreator.interfaces.IValueUpdater;
import at.vintagestory.modelcreator.model.Animation;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.model.AnimFrameElement;

public class RightKeyFramesPanel extends JPanel implements IValueUpdater
{
	private static final long serialVersionUID = 1L;
	
	private ElementKeyFrameOffsetPanel panelPosition;
	private ElementKeyFrameRotationPanel panelRotation;
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
			ensureAnimationExists();
			
			ModelCreator.changeHistory.beginMultichangeHistoryState();
			
			Element elem = ModelCreator.rightTopPanel.getCurrentElement();
			AnimFrameElement keyFrameElem = ModelCreator.currentProject.SelectedAnimation.ToggleRotation(elem, btnRot.isSelected());
			
			if ((a.getModifiers() & ActionEvent.SHIFT_MASK) == 1) {
				Animation anim = ModelCreator.currentProject.SelectedAnimation;
				int currentFrame = anim.currentFrame;
				
				AnimFrameElement frameElem = anim.GetOrCreateKeyFrameElement(currentFrame, elem);
				keyFrameElem.setRotationX(frameElem.getRotationX());
				keyFrameElem.setRotationY(frameElem.getRotationY());
				keyFrameElem.setRotationZ(frameElem.getRotationZ());
			}			
			
			ModelCreator.updateValues(btnRot);
			ModelCreator.changeHistory.endMultichangeHistoryState(ModelCreator.currentProject);
			
			copyFrameElemToBackdrop(elem);
		});
		btnContainer.add(btnRot);
		
		
		btnPos = new JToggleButton("Position");
		btnPos.setFont(defaultFont);
		btnPos.addActionListener(a ->
		{
			ensureAnimationExists();
			
			ModelCreator.changeHistory.beginMultichangeHistoryState();
			
			Element elem = ModelCreator.rightTopPanel.getCurrentElement();
			AnimFrameElement keyFrameElem = ModelCreator.currentProject.SelectedAnimation.TogglePosition(ModelCreator.rightTopPanel.getCurrentElement(), btnPos.isSelected());
			
			if ((a.getModifiers() & ActionEvent.SHIFT_MASK) == 1) {
				Animation anim = ModelCreator.currentProject.SelectedAnimation;
				int currentFrame = anim.currentFrame;
				
				AnimFrameElement frameElem = anim.GetOrCreateKeyFrameElement(currentFrame, elem);
				
				keyFrameElem.setOffsetX(frameElem.getOffsetX());
				keyFrameElem.setOffsetY(frameElem.getOffsetY());
				keyFrameElem.setOffsetZ(frameElem.getOffsetZ());
			}
			
			ModelCreator.updateValues(btnPos);
			ModelCreator.changeHistory.endMultichangeHistoryState(ModelCreator.currentProject);
			
			copyFrameElemToBackdrop(elem);
		});
		
		btnContainer.add(btnPos);
		
		
		
		btnStretch = new JToggleButton("Stretch");
		btnStretch.setFont(defaultFont);
		btnStretch.addActionListener(a ->
		{
			ensureAnimationExists();
			
			ModelCreator.changeHistory.beginMultichangeHistoryState();
			
			Element elem = ModelCreator.rightTopPanel.getCurrentElement();
			AnimFrameElement keyFrameElem = ModelCreator.currentProject.SelectedAnimation.ToggleStretch(ModelCreator.rightTopPanel.getCurrentElement(), btnStretch.isSelected());
			
			if ((a.getModifiers() & ActionEvent.SHIFT_MASK) == 1) {
				Animation anim = ModelCreator.currentProject.SelectedAnimation;
				int currentFrame = anim.currentFrame;
				
				AnimFrameElement frameElem = anim.GetOrCreateKeyFrameElement(currentFrame, elem);
				
				keyFrameElem.setStretchX(frameElem.getStretchX());
				keyFrameElem.setStretchY(frameElem.getStretchY());
				keyFrameElem.setStretchZ(frameElem.getStretchZ());
			}
			
			ModelCreator.updateValues(btnStretch);
			ModelCreator.changeHistory.endMultichangeHistoryState(ModelCreator.currentProject);
			
			copyFrameElemToBackdrop(elem);
		});
		
		
		btnContainer.add(btnStretch);
		
		
		
		add(btnContainer);
		add(panelRotation);
		add(panelPosition);
		//add(panelSize);	
		
		updateValues(null);
	}
	


	public void copyFrameElemToBackdrop(Element elem)
	{
		if (ModelCreator.currentBackdropProject == null) return;
		
		Animation bdanim = ModelCreator.currentBackdropProject.SelectedAnimation;
		if (bdanim == null) return;
		
		Animation anim = ModelCreator.currentProject.SelectedAnimation;
		AnimFrameElement mainFrameElem = anim.GetOrCreateKeyFrameElement(anim.currentFrame, elem);
		
		AnimFrameElement backFrameElem = bdanim.GetOrCreateKeyFrameElement(anim.currentFrame, elem);
		backFrameElem.setFrom(mainFrameElem);		
	}

	public void ensureAnimationExists()
	{
		if (ModelCreator.backdropAnimationsMode && ModelCreator.currentBackdropProject != null && ModelCreator.currentBackdropProject.SelectedAnimation != null && ModelCreator.currentProject.SelectedAnimation == null) {
			Animation anim = ModelCreator.currentBackdropProject.SelectedAnimation;
			
			Project curProj = ModelCreator.currentProject; 
			curProj.SelectedAnimation = curProj.findAnimation(anim.getCode());
			if (curProj.SelectedAnimation == null) {
				curProj.Animations.add(curProj.SelectedAnimation = new Animation());
				
				curProj.SelectedAnimation.setCode(anim.getCode());
				curProj.SelectedAnimation.setName(anim.getName());
				curProj.SelectedAnimation.SetQuantityFrames(anim.GetQuantityFrames());
			}
		}
		
	}

	@Override
	public void updateValues(JComponent byGuiElem)
	{
		updateFrame(byGuiElem);
	}
	
	public void updateFrame(JComponent byGuiElem) {
		if (ModelCreator.rightTopPanel == null) return;
		Element elem = ModelCreator.rightTopPanel.getCurrentElement();
		
		Project project = ModelCreator.CurrentAnimProject();
		
		boolean enabled = project.SelectedAnimation != null && elem != null && project.GetFrameCount() > 0;
		if (ModelCreator.AnimationPlaying()) return;
		
		ensureAnimationExists();
		
		Animation anim = ModelCreator.currentProject.SelectedAnimation;
		AnimFrameElement keyframeElem = enabled ? anim.GetKeyFrameElement(elem, anim.currentFrame) : null;
		
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
	

	public AnimFrameElement getCurrentElement()
	{
		if (ModelCreator.rightTopPanel == null) return null;
		
		ensureAnimationExists();
		Project project = ModelCreator.currentProject;
		
		Element elem = ModelCreator.rightTopPanel.getCurrentElement();
		if (elem == null || project.SelectedAnimation == null) return null;
		
		Animation anim = ModelCreator.currentProject.SelectedAnimation;
		return anim.GetOrCreateKeyFrameElement(anim.currentFrame, elem);
	}

}
