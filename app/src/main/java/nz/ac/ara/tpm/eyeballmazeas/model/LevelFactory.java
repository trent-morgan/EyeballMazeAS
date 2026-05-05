package nz.ac.ara.tpm.eyeballmazeas.model;

public class LevelFactory {

    public static void loadLevel(Game game, int levelId) {
        if (levelId == 1) {
            // Define dimensions
            game.addLevel(6, 4);
            game.setCurrentLevel(0);

            game.addSquare(new PlayableSquare(Color.RED, Shape.FLOWER), 0, 2);

            game.addSquare(new PlayableSquare(Color.BLUE, Shape.CROSS), 1, 0);
            game.addSquare(new PlayableSquare(Color.YELLOW, Shape.FLOWER), 1, 0);
            game.addSquare(new PlayableSquare(Color.YELLOW, Shape.DIAMOND), 1, 0);
            game.addSquare(new PlayableSquare(Color.GREEN, Shape.CROSS), 1, 0);

            game.addSquare(new PlayableSquare(Color.GREEN, Shape.FLOWER), 2, 0);
            game.addSquare(new PlayableSquare(Color.RED, Shape.STAR), 2, 0);
            game.addSquare(new PlayableSquare(Color.GREEN, Shape.STAR), 2, 0);
            game.addSquare(new PlayableSquare(Color.YELLOW, Shape.DIAMOND), 2, 0);

            game.addSquare(new PlayableSquare(Color.RED, Shape.FLOWER), 3, 0);
            game.addSquare(new PlayableSquare(Color.BLUE, Shape.FLOWER), 3, 0);
            game.addSquare(new PlayableSquare(Color.RED, Shape.STAR), 3, 0);
            game.addSquare(new PlayableSquare(Color.GREEN, Shape.FLOWER), 3, 0);

            game.addSquare(new PlayableSquare(Color.BLUE, Shape.STAR), 4, 0);
            game.addSquare(new PlayableSquare(Color.RED, Shape.DIAMOND), 4, 0);
            game.addSquare(new PlayableSquare(Color.BLUE, Shape.FLOWER), 4, 0);
            game.addSquare(new PlayableSquare(Color.BLUE, Shape.DIAMOND), 4, 0);

            game.addSquare(new PlayableSquare(Color.BLUE, Shape.DIAMOND), 5, 1);


            game.addGoal(0, 2);

            game.addEyeball(5, 1, Direction.UP);
        }
//        else if (levelId == 2) {
//            // Define a different grid for Level 2...
//            game.addLevel(4, 4);
//            // ...
//        }
    }

    public static void loadAllLevelSkeletons(Game game) {
        loadLevel(game, 1);
    }
}