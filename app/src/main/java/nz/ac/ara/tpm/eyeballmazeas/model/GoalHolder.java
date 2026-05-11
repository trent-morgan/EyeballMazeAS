package nz.ac.ara.tpm.eyeballmazeas.model;

import java.util.ArrayList;
import java.util.List;

public class GoalHolder implements IGoalHolder {
	private List<Position> goals = new ArrayList<>();
	private List<Position> completedGoals = new ArrayList<>();

	//NEW ARRAY TO HOLD LIST OF INITIAL GOALS
	private List<Position> initialGoals = new ArrayList<>();
	
	@Override
	public void addGoal(int row, int column) {
		Position goal = new Position(row, column);
		if (!goals.contains(goal)) {
		    goals.add(goal);
			initialGoals.add(new Position(row, column));
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

    //NEW METHOD TO RESET GOALS & CHECK GOAL
    public void resetGoals() {
        goals.clear();
        completedGoals.clear();
        for (Position p : initialGoals) {
            goals.add(new Position(p.getRow(), p.getColumn()));
        }
    }

    public void checkGoal(int row, int column) {
		Position goal = new Position(row, column);
		if (goals.contains(goal)) {
			completedGoals.add(goal);
			goals.remove(goal);
		}
	}

}
 