package nz.ac.ara.tpm.eyeballmazeas.model;

import java.util.Objects;

public class Position {
    private final int row;
    private final int column;
    
    public Position(int row, int column) {
        this.row = row;
        this.column = column; 
    }
    
    public int getRow() {
    	return this.row;
    }
    
    public int getColumn() {
    	return this.column;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        
        if (o == null || getClass() != o.getClass()) return false;
        
        Position position = (Position) o;
        return row == position.row && column == position.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
}
