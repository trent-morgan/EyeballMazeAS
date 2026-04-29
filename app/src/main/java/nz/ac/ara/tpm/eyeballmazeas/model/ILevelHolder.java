package nz.ac.ara.tpm.eyeballmazeas.model;

public interface ILevelHolder {
	public void addLevel(int height, int width); 
	public int getLevelWidth(); 
	public int getLevelHeight(); 
	public void setCurrentLevel(int levelNumber); 
	public int getLevelCount(); 
}
