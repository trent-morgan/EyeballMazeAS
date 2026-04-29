package nz.ac.ara.tpm.eyeballmazeas.model;

public class EyeballHolder implements IEyeballHolder {
	private int eyeballRow;
	private int eyeballColumn;
	private Direction eyeballDirection;
	
	@Override
	public void addEyeball(int row, int column, Direction direction) {
		this.eyeballRow = row;
		this.eyeballColumn = column;
		this.eyeballDirection = direction;
	}

	@Override
	public int getEyeballRow() {
		return this.eyeballRow;
	}

	@Override
	public int getEyeballColumn() {
		return this.eyeballColumn;
	}

	@Override
	public Direction getEyeballDirection() {
		return this.eyeballDirection;
	}

}
