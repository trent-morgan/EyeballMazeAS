package nz.ac.ara.tpm.eyeballmazeas.model;


public class SquareHolder implements ISquareHolder {
	private final Square[][] squares;
	
	public SquareHolder(int height, int width) {
		squares = new Square[height][width];
	}
	
	@Override
	public void addSquare(Square square, int row, int column) {
		squares[row][column] = square;
	}

	@Override
	public Color getColorAt(int row, int column) {
		Square current = squares[row][column];

		if (current instanceof PlayableSquare p) {
		    return p.color(); 
		} else {
			return Color.BLANK;
		}
	}

	@Override
	public Shape getShapeAt(int row, int column) {
		Square current = squares[row][column];

		if (current instanceof PlayableSquare p) {
		    return p.shape(); 
		} else  {
			return Shape.BLANK;
		}
	}

}
