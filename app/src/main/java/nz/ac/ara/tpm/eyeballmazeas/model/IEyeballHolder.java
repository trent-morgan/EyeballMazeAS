package nz.ac.ara.tpm.eyeballmazeas.model;

public interface IEyeballHolder {
	public void addEyeball(int row, int column, Direction direction); 
	public int getEyeballRow(); 
	public int getEyeballColumn(); 
	public Direction getEyeballDirection();
}
