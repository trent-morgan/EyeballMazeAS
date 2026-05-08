package nz.ac.ara.tpm.eyeballmazeas.model;

import java.util.ArrayList;
import java.util.List;

public class LevelHolder implements ILevelHolder {
	private List<Level> levels = new ArrayList<>();
	private Level currentLevel;

	@Override
	public void addLevel(int height, int width) {
		Level level = new Level(height, width);
		levels.add(level);
		
		currentLevel = level;
	}

	@Override
	public int getLevelWidth() {
		return currentLevel.getLevelWidth();
	}

	@Override
	public int getLevelHeight() {
		return currentLevel.getLevelHeight();
	}

	@Override
	public void setCurrentLevel(int levelNumber) {
		if (levelNumber < 0 || levelNumber >= levels.size()) {
	        throw new IllegalArgumentException();
	    }
		this.currentLevel = levels.get(levelNumber);
	}

	@Override
	public int getLevelCount() {
		return this.levels.size();
	}
	
	public Level getCurrentLevel() {
		return this.currentLevel;
	}
	
	public void checkGoal(int row, int column) {
		this.currentLevel.checkGoal(row, column);
	}

	public void resetGoalsForCurrentLevel() {
		this.currentLevel.resetGoalsForLevel();
	}

}
