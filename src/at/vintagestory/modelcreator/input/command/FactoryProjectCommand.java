package at.vintagestory.modelcreator.input.command;

import at.vintagestory.modelcreator.ModelCreator;

public class FactoryProjectCommand
{
	public ProjectCommand CreateRandomizeTextureCommand() {
		return new CommandInvokeLater(new CommandRandomizeTexture());
	}
	
	public ProjectCommand CreateReloadTextureCommand() {
		return new CommandInvokeLater(new CommandReloadTexture());
	}
	
	public ProjectCommand CreateToggleTextureCommand() {
		return new CommandInvokeLater(new CommandToggleTexture());
	}
	
	public ProjectCommand CreateRedoCommand() {
		return new CommandInvokeLater(new CommandRedo());
	}
	
	public ProjectCommand CreateUndoCommand() {
		return new CommandInvokeLater(new CommandUndo());
	}
	
	public ProjectCommand CreateSaveCommand(ModelCreator creator) {
		return new CommandInvokeLater(new CommandSave(creator));
	}
	
	public ProjectCommand CreateMoveSelectedElementCommandUp(ModelCreator creator) {
		return CreateMoveSelectedElementCommand(creator, new double[][] {
			{ 0,  1,  0},
			{ 0,  1,  0},
			{ 0,  1,  0},
			{ 0,  1,  0}
		});
	}
	
	public ProjectCommand CreateMoveSelectedElementCommandForward(ModelCreator creator) {
		return CreateMoveSelectedElementCommand(creator, new double[][] {
			{ 0,  0,  1},
			{-1,  0,  0},
			{ 0,  0, -1},
			{ 1,  0,  0}
		});
	}
	
	public ProjectCommand CreateMoveSelectedElementCommandRight(ModelCreator creator) {
		return CreateMoveSelectedElementCommand(creator, new double[][] {
			{-1,  0,  0},
			{ 0,  0, -1},
			{ 1,  0,  0},
			{ 0,  0,  1}
		});
	}
	
	public ProjectCommand CreateMoveSelectedElementCommandBackward(ModelCreator creator) {
		return CreateMoveSelectedElementCommand(creator, new double[][] {
			{ 0,  0, -1},
			{ 1,  0,  0},
			{ 0,  0,  1},
			{-1,  0,  0}
		});
	}
	
	public ProjectCommand CreateMoveSelectedElementCommandLeft(ModelCreator creator) {
		return CreateMoveSelectedElementCommand(creator, new double[][] {
			{ 1,  0,  0},
			{ 0,  0,  1},
			{-1,  0,  0},
			{ 0,  0, -1}
		});
	}
	
	public ProjectCommand CreateMoveSelectedElementCommandDown(ModelCreator creator) {
		return CreateMoveSelectedElementCommand(creator, new double[][] {
			{ 0, -1,  0},
			{ 0, -1,  0},
			{ 0, -1,  0},
			{ 0, -1,  0}
		});
	}
	
	private ProjectCommand CreateMoveSelectedElementCommand(ModelCreator creator, double[][] matrix) {
		return new CommandInvokeLater(new CommandMoveSelectedElement(creator, matrix));
	}
}
