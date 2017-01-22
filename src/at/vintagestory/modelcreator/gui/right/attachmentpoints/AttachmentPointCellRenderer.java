package at.vintagestory.modelcreator.gui.right.attachmentpoints;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import at.vintagestory.modelcreator.model.AttachmentPoint;

public class AttachmentPointCellRenderer  extends DefaultListCellRenderer {
	private static final long serialVersionUID = 1L;

	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof AttachmentPoint) {
            setText(((AttachmentPoint)value).getCode());
        }
        return this;
    }
}
