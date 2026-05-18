package nz.ac.ara.tpm.eyeballmazeas.model;

public class Game implements IMoving, IEyeballHolder {
	
	private LevelHolder levelHolder;
	private EyeballHolder eyeballHolder;

	private int moveCount = 0; // Initialize at zero
	
	public Game() {
		this.levelHolder = new LevelHolder();
		this.eyeballHolder = new EyeballHolder();
	}
	
	//LEVELHOLDER ACCESS METHODS
	public void addLevel(int height, int width) {
		this.levelHolder.addLevel(height, width);
	}

	public int getLevelWidth() {
		return this.levelHolder.getLevelWidth();
	}

	
	public int getLevelHeight() {
		return this.levelHolder.getLevelHeight();
	}

	
	public void setCurrentLevel(int levelNumber) {
		this.levelHolder.setCurrentLevel(levelNumber);

	}
	
	public int getLevelCount() {
		return this.levelHolder.getLevelCount();
	}
	
	//LEVEL ACESS METHODS
	public void addGoal(int row, int column) {
	    this.levelHolder.getCurrentLevel().addGoal(row, column);
	}

	public int getGoalCount() {
	    return this.levelHolder.getCurrentLevel().getGoalCount();
	}

	public boolean hasGoalAt(int targetRow, int targetColumn) {
	    return this.levelHolder.getCurrentLevel().hasGoalAt(targetRow, targetColumn);
	}

	public int getCompletedGoalCount() {
	    return this.levelHolder.getCurrentLevel().getCompletedGoalCount();
	}

	public void addSquare(Square square, int row, int column) {
	    this.levelHolder.getCurrentLevel().addSquare(square, row, column);
	}

	public Color getColorAt(int row, int column) {
	    return this.levelHolder.getCurrentLevel().getColorAt(row, column);
	}

	public Shape getShapeAt(int row, int column) {
	    return this.levelHolder.getCurrentLevel().getShapeAt(row, column);
	}
	
	public void addEyeball(int row, int column, Direction direction) {
		if (row < 0 || row > this.getLevelHeight() || column < 0 || column > this.getLevelWidth()) {
	        throw new IllegalArgumentException();
	    }
		this.eyeballHolder.addEyeball(row, column, direction);
	}

	public int getEyeballRow() {
	    return this.eyeballHolder.getEyeballRow();
	}

	public int getEyeballColumn() {
	    return this.eyeballHolder.getEyeballColumn();
	}

	public Direction getEyeballDirection() {
	    return this.eyeballHolder.getEyeballDirection();
	}
	
	//MOVE VALIDATING METHODS
	@Override
	public boolean canMoveTo(int destinationRow, int destinationColumn) {
		
		boolean dirOk = this.isDirectionOK(destinationRow, destinationColumn);
		
		if (dirOk) {
			
			boolean freePath = this.hasBlankFreePathTo(destinationRow, destinationColumn);
			
			if (freePath) {
				
				Color destColor = this.getColorAt(destinationRow, destinationColumn);
				Color curColor = this.getColorAt(this.getEyeballRow(), this.getEyeballColumn());
				Shape destShape = this.getShapeAt(destinationRow, destinationColumn);
				Shape curShape = this.getShapeAt(this.getEyeballRow(), this.getEyeballColumn());
				
				if (destColor == curColor || destShape == curShape) {
										
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Message messageIfMovingTo(int destinationRow, int destinationColumn) {
	    
	    if (!this.isDirectionOK(destinationRow, destinationColumn)) {   	
	    	return this.checkDirectionMessage(destinationRow, destinationColumn);
	    }
	    
	    Color destColor = this.getColorAt(destinationRow, destinationColumn);
	    Color curColor = this.getColorAt(this.getEyeballRow(), this.getEyeballColumn());
	    Shape destShape = this.getShapeAt(destinationRow, destinationColumn);
	    Shape curShape = this.getShapeAt(this.getEyeballRow(), this.getEyeballColumn());

	    if (destColor != curColor && destShape != curShape) {
	        return Message.DIFFERENT_SHAPE_OR_COLOR;
	    }

	    return checkMessageForBlankOnPathTo(destinationRow, destinationColumn);
	}

	@Override
	public boolean isDirectionOK(int destinationRow, int destinationColumn) {
		
		if (destinationRow == this.getEyeballRow() && destinationColumn == this.getEyeballColumn()) {
			return false;
		}
		
		if (destinationRow == this.getEyeballRow() || destinationColumn == this.getEyeballColumn()) {
			
			Direction currentDir = this.getEyeballDirection();
	        
	        if (destinationRow < this.getEyeballRow() && currentDir == Direction.DOWN) {
	            return false; 
	        }
	        if (destinationRow > this.getEyeballRow() && currentDir == Direction.UP) {
	            return false; 
	        }
	        if (destinationColumn < this.getEyeballColumn() && currentDir == Direction.RIGHT) {
	            return false; 
	        }
	        if (destinationColumn > this.getEyeballColumn() && currentDir == Direction.LEFT) {
	            return false; 
	        }

	        return true;
		}
		return false;
	}

	@Override
	public Message checkDirectionMessage(int destinationRow, int destinationColumn) {
		if (destinationRow != this.getEyeballRow() && destinationColumn != this.getEyeballColumn()) {
            return Message.MOVING_DIAGONALLY;
        }

        return Message.BACKWARDS_MOVE;
	}

	@Override
	public boolean hasBlankFreePathTo(int destinationRow, int destinationColumn) {
	    if (!this.isDirectionOK(destinationRow, destinationColumn)) {
	        return false;
	    }

	    int curRow = this.getEyeballRow();
	    int curCol = this.getEyeballColumn();

	    int rowStep = Integer.compare(destinationRow, curRow);
	    int colStep = Integer.compare(destinationColumn, curCol);

	    int checkRow = curRow + rowStep;
	    int checkCol = curCol + colStep;

	    while (checkRow != destinationRow || checkCol != destinationColumn) {
	        if (this.getColorAt(checkRow, checkCol) == Color.BLANK) {
	            return false;
	        }

	        checkRow += rowStep;
	        checkCol += colStep;
	    }

	    return true;
	}

	@Override
	public Message checkMessageForBlankOnPathTo(int destinationRow, int destinationColumn) {
	    if (!this.hasBlankFreePathTo(destinationRow, destinationColumn)) {
	        return Message.MOVING_OVER_BLANK;
	    }
	    return Message.OK;
	}

	@Override
	public void moveTo(int destinationRow, int destinationColumn) {

		if (this.canMoveTo(destinationRow, destinationColumn)) {
			this.moveCount ++;

			int oldRow = this.getEyeballRow();
		    int oldCol = this.getEyeballColumn();

		    Direction newDirection;

		    if (destinationRow < oldRow) {
		        newDirection = Direction.UP;
		    } else if (destinationRow > oldRow) {
		        newDirection = Direction.DOWN;
		    } else if (destinationColumn < oldCol) {
		        newDirection = Direction.LEFT;
		    } else {
		        newDirection = Direction.RIGHT;
		    }
		    
		    this.addEyeball(destinationRow, destinationColumn, newDirection);
		    if (this.hasGoalAt(oldRow, oldCol)) {
	            this.addSquare(new BlankSquare(), oldRow, oldCol);
	        }
		    this.checkGoal(destinationRow, destinationColumn);
		}
	}

	//ADDED FOR MOVE COUNT
	public int getMoveCount() {
		return this.moveCount;
	}

	public void resetMoveCount() {
		this.moveCount = 0;
	}

    public void checkGoal(int row, int column) {
        this.levelHolder.checkGoal(row, column);
    }

	public void resetGoalsForCurrentLevel() {
		this.levelHolder.resetGoalsForCurrentLevel();
	}
}
