class Player {
    private int xCoordinate = 9 * PacMan.CHUNK_RATIO;
    private int yCoordinate = 11 * PacMan.CHUNK_RATIO;
    private Direction direction = Direction.NONE;
    private Direction nextDirection = Direction.NONE;
    private Direction lastDirection = Direction.LEFT;
    private int points;

    void changeDirection(Direction newDirection) {
        if (isDirectionSwitchPossible(newDirection)) {
            lastDirection = direction;
            direction = newDirection;
            nextDirection = Direction.NONE;
        } else {
            nextDirection = newDirection;
        }
    }

    private boolean isDirectionSwitchPossible(Direction newDirection) {
        switch (newDirection) {
            case LEFT:
            case RIGHT:
            case UP:
            case DOWN:
                Square[] moveFront = new Square[PacMan.CHUNK_RATIO];
                setMoveFront(newDirection, moveFront);
                boolean[] isClear = new boolean[PacMan.CHUNK_RATIO];
                for (int i = 0; i < PacMan.CHUNK_RATIO; i++) {
                    isClear[i] = (moveFront[i] == Square.CLEAR);
                }
                for (boolean i : isClear) {
                    if (!i) {
                        return false;
                    }
                }
                return true;
            case NONE:
                return false;
            default:
                System.err.println("Error in Player.isDirectionSwitchPossible: hit default.");
                return false;
        }
    }

    void firstPaint() {
        if (direction == Direction.NONE) {
            move();
        } else {
            System.err.println("Error in Player.firstPaint: should only be called when there is no direction.");
        }
    }

    void attemptMove() {
        if (isMovePossible() && !isDirectionSwitchPossible(nextDirection)) {
            move();
        } else if (nextDirection == Direction.NONE) {
            lastDirection = direction;
            direction = Direction.NONE;
        } else {
            changeDirection(nextDirection);
        }
    }

    private boolean isMovePossible() {
        Square[] moveFront = new Square[PacMan.CHUNK_RATIO];
        setMoveFront(direction, moveFront);
        boolean isClear = true;
        boolean skipPoint = false;
        for (Square i : moveFront) {
            switch (i) {
                case WALL:
                    isClear = false;
                    break;
                case CLEAR:
                    // Do Nothing.
                    break;
                case JUMP:
                    if (direction == Direction.LEFT) {
                        xCoordinate = PacMan.HORIZONTAL_SQUARES - PacMan.CHUNK_RATIO - 1;
                        resetChunk(1);
                    } else {
                        xCoordinate = 1;
                        resetChunk(PacMan.HORIZONTAL_SQUARES - PacMan.CHUNK_RATIO - 1);
                    }
                    break;
                case POINT:
                    if (!skipPoint) {
                        skipPoint = true;
                        eat(i);
                    }
                    break;
                case BALL:
                    if (!skipPoint) {
                        skipPoint = true;
                        eat(i);
                        // TODO: ghost scared code
                    }
                    break;
                case RED:
                case CYAN:
                case PINK:
                case ORANGE:
                    PacMan.gameLose();
                    break;
                case SCARED_RED:
                    // TODO: ghost dies
                    break;
                case SCARED_CYAN:
                    // TODO: ghost dies
                    break;
                case SCARED_PINK:
                    // TODO: ghost dies
                    break;
                case SCARED_ORANGE:
                    // TODO: ghost dies
                    break;
                default:
                    System.err.println("Error in Player.isMovePossible: hit default.");
                    break;
            }
        }
        return isClear;
    }

    private void setMoveFront(Direction moveDirection, Square[] moveFront) {
        switch (moveDirection) {
            case LEFT:
                for (int i = 0; i < PacMan.CHUNK_RATIO; i++) {
                    moveFront[i] = PacMan.board[yCoordinate + i][xCoordinate - 1];
                }
                break;
            case RIGHT:
                for (int i = 0; i < PacMan.CHUNK_RATIO; i++) {
                    moveFront[i] = PacMan.board[yCoordinate + i][xCoordinate + PacMan.CHUNK_RATIO];
                }
                break;
            case UP:
                for (int i = 0; i < PacMan.CHUNK_RATIO; i++) {
                    moveFront[i] = PacMan.board[yCoordinate - 1][xCoordinate + i];
                }
                break;
            case DOWN:
                for (int i = 0; i < PacMan.CHUNK_RATIO; i++) {
                    moveFront[i] = PacMan.board[yCoordinate + PacMan.CHUNK_RATIO][xCoordinate + i];
                }
                break;
            default:
                if (moveDirection != Direction.NONE) {
                    System.err.println("Error in Player.setMoveFront: default hit with no direction.");
                }
                for (int i = 0; i < PacMan.CHUNK_RATIO; i++) {
                    moveFront[i] = Square.CLEAR;
                }
                break;
        }
    }

    private void resetChunk(int start) {
        for (int vertical = yCoordinate; vertical < yCoordinate + PacMan.CHUNK_RATIO; vertical++) {
            for (int horizontal = start; horizontal < start + PacMan.CHUNK_RATIO; horizontal++) {
                PacMan.board[vertical][horizontal] = Square.CLEAR;
            }
        }
    }

    private void eat(Square delete) {
        final int POINTS_ON_BOARD = 151;
        points++;
        int x = xCoordinate;
        int y = yCoordinate;
        switch (direction) {
            case LEFT:
                x -= PacMan.CHUNK_RATIO;
                break;
            case RIGHT:
                x += PacMan.CHUNK_RATIO;
                break;
            case UP:
                y -= PacMan.CHUNK_RATIO;
                break;
            case DOWN:
                y += PacMan.CHUNK_RATIO;
                break;
        }
        deleteInChunk(delete, x, y);
        if (points >= POINTS_ON_BOARD) {
            PacMan.gameWin();
        }
    }

    private void deleteInChunk(Square delete, int x, int y) {
        for (int vertical = y; vertical < y + PacMan.CHUNK_RATIO; vertical++) {
            for (int horizontal = x; horizontal < x + PacMan.CHUNK_RATIO; horizontal++) {
                if (PacMan.board[vertical][horizontal] == delete) {
                    PacMan.board[vertical][horizontal] = Square.CLEAR;
                }
            }
        }
    }

    private void move() {
        switch (direction) {
            case LEFT:
                xCoordinate--;
                break;
            case RIGHT:
                xCoordinate++;
                break;
            case UP:
                yCoordinate--;
                break;
            case DOWN:
                yCoordinate++;
                break;
        }
        draw();
    }

    private void draw() {
        resetOldGrid();

        final int[][] leftDirectionPacMan =
                {
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0},
                        {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0},
                        {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                        {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                        {0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                        {0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0},
                        {0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                        {0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                        {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                        {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                        {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0},
                        {0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
                };

        final int[][] pacMan = new int[PacMan.CHUNK_RATIO][PacMan.CHUNK_RATIO];

        if (direction == Direction.RIGHT || (direction == Direction.NONE && lastDirection == Direction.RIGHT)) {
            for (int horizontal = 0; horizontal < PacMan.CHUNK_RATIO; horizontal++) {
                for (int vertical = 0; vertical < PacMan.CHUNK_RATIO; vertical++) {
                    pacMan[vertical][horizontal] = leftDirectionPacMan[vertical][PacMan.CHUNK_RATIO - horizontal - 1];
                }
            }
        } else if (direction == Direction.DOWN || (direction == Direction.NONE && lastDirection == Direction.DOWN)) {
            for (int horizontal = 0; horizontal < PacMan.CHUNK_RATIO; horizontal++) {
                for (int vertical = 0; vertical < PacMan.CHUNK_RATIO; vertical++) {
                    pacMan[vertical][horizontal] = leftDirectionPacMan[horizontal][PacMan.CHUNK_RATIO - vertical - 1];
                }
            }
        } else if (direction == Direction.UP || (direction == Direction.NONE && lastDirection == Direction.UP)) {
            for (int horizontal = 0; horizontal < PacMan.CHUNK_RATIO; horizontal++) {
                for (int vertical = 0; vertical < PacMan.CHUNK_RATIO; vertical++) {
                    pacMan[vertical][horizontal] = leftDirectionPacMan[horizontal][vertical];
                }
            }
        } else {
            for (int horizontal = 0; horizontal < PacMan.CHUNK_RATIO; horizontal++) {
                for (int vertical = 0; vertical < PacMan.CHUNK_RATIO; vertical++) {
                    pacMan[vertical][horizontal] = leftDirectionPacMan[vertical][horizontal];
                }
            }
        }

        for (int vertical = yCoordinate; vertical < yCoordinate + PacMan.CHUNK_RATIO; vertical++) {
            for (int horizontal = xCoordinate; horizontal < xCoordinate + PacMan.CHUNK_RATIO; horizontal++) {
                if (pacMan[vertical - yCoordinate][horizontal - xCoordinate] == 1) {
                    PacMan.board[vertical][horizontal] = Square.YELLOW;
                }
            }
        }
    }

    private void resetOldGrid() {
        int oldX = xCoordinate;
        int oldY = yCoordinate;
        switch (direction) {
            case RIGHT:
                oldX = xCoordinate - 1;
                break;
            case LEFT:
                oldX = xCoordinate + 1;
                break;
            case UP:
                oldY = yCoordinate + 1;
                break;
            case DOWN:
                oldY = yCoordinate - 1;
                break;
        }
        for (int vertical = oldY; vertical < oldY + PacMan.CHUNK_RATIO; vertical++) {
            for (int horizontal = oldX; horizontal < oldX + PacMan.CHUNK_RATIO; horizontal++) {
                PacMan.board[vertical][horizontal] = Square.CLEAR;
            }
        }
    }
}