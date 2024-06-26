package at.vintagestory.modelcreator;

import java.util.ArrayList;

import at.vintagestory.modelcreator.gui.right.ElementTree;
import at.vintagestory.modelcreator.gui.right.RightPanel;
import at.vintagestory.modelcreator.model.Animation;


public class ProjectChangeHistory
{
	int maxHistoryStates = 100;
	
	// 0 = latest state
	// 1 = 1 undo
	// etc.
	int currentHistoryState = 0;
	
	public ArrayList<Project> ProjectSnapshots = new ArrayList<Project>();
	
	
	
	public boolean CanUndo() {
		return currentHistoryState < ProjectSnapshots.size() - 1;
	}
	
	public boolean CanRedo() {
		return currentHistoryState > 0;
	}
	
	public boolean Undo() {
		if (currentHistoryState + 1 >= ProjectSnapshots.size()) return false;
		
		currentHistoryState++;
		ApplyState();
		return true;
	}
	
	
	public boolean Redo() {
		if (currentHistoryState <= 0) return false;
		
		currentHistoryState--;
		ApplyState();
		return true;
	}
	
	
	void ApplyState() {
		
		ModelCreator.ignoreDidModify++;
		
		Project oldProject = ModelCreator.currentProject;
		
		ModelCreator.currentProject = ProjectSnapshots.get(currentHistoryState).clone();
		ModelCreator.currentProject.tree = oldProject.tree;
		if (oldProject.SelectedElement != null) {
			ModelCreator.currentProject.SelectedElement = ModelCreator.currentProject.findElement(oldProject.SelectedElement.getName());	
		}
		
		ModelCreator.currentProject.LoadIntoEditor(ModelCreator.rightTopPanel);

		if (oldProject.SelectedAnimation != null) {
			ModelCreator.currentProject.SelectedAnimation = ModelCreator.currentProject.findAnimation(oldProject.SelectedAnimation.getName());	
		}
		if (ModelCreator.currentProject.SelectedAnimation != null && oldProject.SelectedAnimation != null) {
			Animation anim = ModelCreator.currentProject.SelectedAnimation;
			anim.currentFrame = oldProject.SelectedAnimation.currentFrame;
		}
		
		
		ModelCreator.reloadStepparentRelationShips();	
		
		ModelCreator.updateValues(null);
		ModelCreator.currentProject.tree.jtree.updateUI();
		
		if (ModelCreator.rightTopPanel != null) {
			ElementTree tree = ((RightPanel)ModelCreator.rightTopPanel).tree;			
			if (ModelCreator.currentProject.collapsedPaths != null) tree.loadCollapsedPaths(ModelCreator.currentProject.collapsedPaths);
		}
		
		
		ModelCreator.ignoreDidModify--;
	}
	
	
	boolean multiChangeState = false;
	boolean didAttemptAdd = false;
	public void beginMultichangeHistoryState() {
		if (!multiChangeState) {
			multiChangeState = true;
			didAttemptAdd = false;
		}
	}
	
	public void endMultichangeHistoryState(Project project) {
		multiChangeState = false;
		if (didAttemptAdd) {
			addHistoryState(project);
		}
		didAttemptAdd = false;
	}
	
	
	public void addHistoryState(Project project) {
		if (multiChangeState) {
			didAttemptAdd = true;
			return;
		}
		
		while (currentHistoryState > 0) {
			ProjectSnapshots.remove(0);
			currentHistoryState--;
		}
		
		ProjectSnapshots.add(0, project.clone());
		
		if (ProjectSnapshots.size() > maxHistoryStates) {
			ProjectSnapshots.remove(ProjectSnapshots.size() - 1);
		}
	}

	public void clear()
	{
		ProjectSnapshots.clear();
		currentHistoryState = 0;
	}
	
	public void didSave() {
		for (int i = 1; i < ProjectSnapshots.size(); i++) {
			ProjectSnapshots.get(i).needsSaving = true;
		}
		
		ProjectSnapshots.get(0).needsSaving = false;
	}
	
}
