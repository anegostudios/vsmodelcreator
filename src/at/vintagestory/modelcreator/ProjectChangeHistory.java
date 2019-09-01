package at.vintagestory.modelcreator;

import java.util.ArrayList;

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
		
		System.out.println("applied undo, state now at " + currentHistoryState);
		
		return true;
	}
	
	
	public boolean Redo() {
		if (currentHistoryState <= 0) return false;
		
		currentHistoryState--;
		ApplyState();
		
		System.out.println("applied redo, state now at " + currentHistoryState);
		
		return true;
	}
	
	
	void ApplyState() {
		Project oldProject = ModelCreator.currentProject;
		
		ModelCreator.currentProject = ProjectSnapshots.get(currentHistoryState).clone(false);
		ModelCreator.currentProject.tree = oldProject.tree;
		if (oldProject.SelectedElement != null) {
			ModelCreator.currentProject.SelectedElement = ModelCreator.currentProject.findElement(oldProject.SelectedElement.name);	
		}
		
		ModelCreator.currentProject.LoadIntoEditor(ModelCreator.manager);

		if (oldProject.SelectedAnimation != null) {
			ModelCreator.currentProject.SelectedAnimation = ModelCreator.currentProject.findAnimation(oldProject.SelectedAnimation.getName());	
		}
		if (ModelCreator.currentProject.SelectedAnimation != null && oldProject.SelectedAnimation != null) {
			Animation anim = ModelCreator.currentProject.SelectedAnimation;
			anim.currentFrame = oldProject.SelectedAnimation.currentFrame;
		}
		
		

		ModelCreator.updateValues(null);
		ModelCreator.currentProject.tree.jtree.updateUI();
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
		
		ProjectSnapshots.add(0, project.clone(false));
		System.out.println("added history state, states = " + ProjectSnapshots.size());
		
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
