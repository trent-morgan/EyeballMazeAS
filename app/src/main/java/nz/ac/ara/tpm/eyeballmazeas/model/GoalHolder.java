package nz.ac.ara.tpm.eyeballmazeas.model;

import java.util.ArrayList;
import java.util.List;

public class GoalHolder implements IGoalHolder {
	private List<Position> goals = new ArrayList<>();
	private List<Position> completedGoals = new ArrayList<>();

	
	@Override
	public void addGoal(int row, int column) {
		Position goal = new Position(row, column);
		if (!goals.contains(goal)) {
		    goals.add(goal);
		}
	}

	@Override
	public int getGoalCount() {
		return goals.size();
	}

	@Override
	public boolean hasGoalAt(int targetRow, int targetColumn) {
		return goals.contains(new Position(targetRow, targetColumn));
	}

	@Override
	public int getCompletedGoalCount() {
		return completedGoals.size();
	}
	
	public void checkGoal(int row, int column) {
		Position goal = new Position(row, column);
		if (goals.contains(goal)) {
			completedGoals.add(goal);
			goals.remove(goal);
		}
	}

}
 