package at.vintagestory.modelcreator.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import at.vintagestory.modelcreator.ModelCreator;

public class SliderMouseHandler implements MouseListener
{

	@Override
	public void mouseClicked(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		ModelCreator.changeHistory.beginMultichangeHistoryState();
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		ModelCreator.changeHistory.endMultichangeHistoryState(ModelCreator.currentProject);
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub

	}

}
