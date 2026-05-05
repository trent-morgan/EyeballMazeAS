package nz.ac.ara.tpm.eyeballmazeas.viewmodel;

import androidx.lifecycle.ViewModel; // Check this import!
import nz.ac.ara.tpm.eyeballmazeas.model.Game;

public class GameViewModel extends ViewModel {

    private final Game game;

    //test
    public GameViewModel() {
        game = new Game();
    }

    public Game getGame() {
        return this.game;
    }


}
