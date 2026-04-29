package nz.ac.ara.tpm.eyeballmazeas.model;

public interface ISquareHolder {
	public void addSquare(Square square, int row, int column); 
	public Color getColorAt(int row, int column); 
	public Shape getShapeAt(int row, int column);
}
