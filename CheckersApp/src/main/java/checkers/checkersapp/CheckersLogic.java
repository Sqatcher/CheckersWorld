package checkers.checkersapp;

import java.util.Objects;
import java.util.Scanner;

public class CheckersLogic
{
    //Initialize the board
    public static void initBoard(char[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {

                //fill b
                if (i < 3 && (j + i) % 2 == 1) {
                    board[i][j] = 'b';
                    continue;
                }

                //fill w
                if (4 < i && (j + i) % 2 == 1) {
                    board[i][j] = 'w';
                    continue;
                }

                board[i][j] = ' ';
            }
        }
    }

    //Printing the board
    public static void printBoard(char[][] board) {
        System.out.println("    1   2   3   4   5   6   7   8");
        System.out.println("  _________________________________");

        for (int i = 0; i < board.length; i++) {
            System.out.print(i + 1 + " |");
            for (int j = 0; j < board.length; j++) {
                System.out.print(" " + board[i][j] + " |");
            }
            System.out.println();
            //System.out.println("_____________");
            System.out.println("  _________________________________");
        }

    }

    static int movePossible(char currentPlayer, char[][] board, int x0, int y0, int x, int y) // -1 = false, 1 = move by 1, 2 = move by 2
    {
        // is the piece mine - true
        // is the move in range of board
        char currentPiece = board[x0 - 1][y0 - 1];
        if (x < 1 || x > 8 || y < 1 || y > 8) {
            return -1;
        }

        //is the move forward - only for normal pieces
        if (currentPlayer == 'b' && currentPiece == 'b') {
            if (x0 - x >= 0) {
                return -1;
            }
        }
        if (currentPlayer == 'w' && currentPiece == 'w') {
            if (x0 - x <= 0) {
                return -1;
            }
        }
        //is the move diagonal and the range is 1 or 2
        int dx = Math.abs(x0 - x);
        int dy = Math.abs(y0 - y);

        if (dx != dy || dx == 0 || dx > 2) {
            return -1;
        }
        // if the move is just by 1 => the tile has to be empty
        if (dx == 1) {
            if (board[x - 1][y - 1] == ' ') {
                return 1;
            }
            return -1;
        }
        // the move is by 2
        // the tile has to be empty and the tile in between is enemy
        char enemyPlayer = (currentPlayer == 'b') ? 'w' : 'b';
        char enemyKing = Character.toUpperCase(enemyPlayer);
        if (board[x - 1][y - 1] == ' ') {
            int ex = (x - x0) / 2 + x0;
            int ey = (y - y0) / 2 + y0;
            if (board[ex - 1][ey - 1] == enemyPlayer || board[ex - 1][ey - 1] == enemyKing) {
                return 2;
            }
        }
        return -1;
    }

    static boolean isWon(char enemyPlayer, char[][] board) {
        // see if there are enemy pieces remaining
        char enemyKing = Character.toUpperCase(enemyPlayer);
        int count = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] == enemyPlayer || board[i][j] == enemyKing) {
                    count++;

                    // check if any move is possible
                    int mv = 8;
                    i += 1;
                    j += 1;
                    mv += movePossible(enemyPlayer, board, i, j, i - 1, j - 1);
                    mv += movePossible(enemyPlayer, board, i, j, i - 1, j + 1);
                    mv += movePossible(enemyPlayer, board, i, j, i + 1, j - 1);
                    mv += movePossible(enemyPlayer, board, i, j, i + 1, j + 1);

                    mv += movePossible(enemyPlayer, board, i, j, i - 2, j - 2);
                    mv += movePossible(enemyPlayer, board, i, j, i - 2, j + 2);
                    mv += movePossible(enemyPlayer, board, i, j, i + 2, j - 2);
                    mv += movePossible(enemyPlayer, board, i, j, i + 2, j + 2);

                    i -= 1;
                    j -= 1;
                    if (mv == 0) // no move is possible
                    {
                        count--;  // the piece is there but rendered unable to move
                    }
                }
            }
        }
        if (count == 0) {
            return true;
        }
        return false;
    }

    static boolean canAttack(char currentPlayer, char[][] board) {
        char currentKing = Character.toUpperCase(currentPlayer);
        int count = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] == currentPlayer || board[i][j] == currentKing) {
                    count++;

                    // check if any capture move is possible
                    int mv = 4;
                    i += 1;
                    j += 1;

                    mv += movePossible(currentPlayer, board, i, j, i - 2, j - 2);
                    mv += movePossible(currentPlayer, board, i, j, i - 2, j + 2);
                    mv += movePossible(currentPlayer, board, i, j, i + 2, j - 2);
                    mv += movePossible(currentPlayer, board, i, j, i + 2, j + 2);

                    i -= 1;
                    j -= 1;
                    if (mv == 0) // no move is possible
                    {
                        count--;  // the piece is there but rendered unable to capture
                    }
                }
            }
        }
        if (count == 0) {
            return false;
        }
        return true;
    }

    public static void printMenu() {
        System.out.println("********************************");
        System.out.println("********************************");
        System.out.println("******        MENU        ******");
        System.out.println("********************************");
        System.out.println("********************************");
        System.out.println("                                ");
        System.out.println("Choose your mode:");
        System.out.println("0) Basic");
        System.out.println("1) Intermediate");
        System.out.println("3) Advanced");
    }

    //More functions to be made....
    static char[][] makeMoveBy1(char[][] board, int x0, int y0, int x, int y) {
        board[x - 1][y - 1] = board[x0 - 1][y0 - 1];
        board[x0 - 1][y0 - 1] = ' ';
        if (board[x - 1][y - 1] == 'b' && x == 8 || board[x - 1][y - 1] == 'w' && x == 1) {
            //promote
            board[x - 1][y - 1] = Character.toUpperCase(board[x - 1][y - 1]);
        }
        return board;
    }

    static char[][] makeMoveBy2(char[][] board, int x0, int y0, int x, int y) {
        char[][] boardCopy = makeMoveBy1(board, x0, y0, x, y);
        int ex = (x - x0) / 2 + x0;
        int ey = (y - y0) / 2 + y0;
        boardCopy[ex - 1][ey - 1] = ' ';
        return boardCopy;
    }

    static int[] canPieceAttack(char myColour, char[][] board, int x0, int y0) {
        int[] xy = {0, 0};

        if (movePossible(myColour, board, x0, y0, x0 - 2, y0 - 2) == 2) {
            xy[0] = x0 - 2;
            xy[1] = y0 - 2;
            return xy;
        }

        if (movePossible(myColour, board, x0, y0, x0 - 2, y0 + 2) == 2) {
            xy[0] = x0 - 2;
            xy[1] = y0 + 2;
            return xy;
        }

        if (movePossible(myColour, board, x0, y0, x0 + 2, y0 - 2) == 2) {
            xy[0] = x0 + 2;
            xy[1] = y0 - 2;
            return xy;
        }

        if (movePossible(myColour, board, x0, y0, x0 + 2, y0 + 2) == 2) {
            xy[0] = x0 + 2;
            xy[1] = y0 + 2;
            return xy;
        }

        return xy;
    }

    static class CheckerBot {
        private static double evaluateBoard(char myColour, char[][] board) {
            char enemyColour = (myColour == 'b') ? 'w' : 'b';
            char myKing = Character.toUpperCase(myColour);
            char enemyKing = Character.toUpperCase(enemyColour);
            int myPieces = 0;
            int enemyPieces = 0;

            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board.length; j++) {
                    //pieces
                    if (board[i][j] == myColour) {
                        myPieces++;
                        continue;
                    }
                    if (board[i][j] == enemyColour) {
                        enemyPieces++;
                    }
                    //kings
                    if (board[i][j] == myKing) {
                        myPieces += 3;
                        continue;
                    }
                    if (board[i][j] == enemyKing) {
                        enemyPieces += 3;
                    }
                }
            }

            return myPieces * 1.0 / enemyPieces;
        }

        private static String[] chooseBestMove(char myColour, char[][] board, String[] moves, int i, int j, int r) {
            String[] movesCopy = {"0.0", ""};
            movesCopy[0] = moves[0];
            movesCopy[1] = moves[1];
            char enemyColour = (myColour == 'b') ? 'w' : 'b';
            char[][] boardWork = copyBoard(board);

            String[] res2 = {"0.0", ""};
            int x0 = i + 1;
            int y0 = j + 1;
            int[] xy;
            char[][] board2move = copyBoard(board);
            int attacks = 0;

            while ((xy = canPieceAttack(myColour, board2move, x0, y0))[0] != 0) {
                board2move = makeMoveBy2(board2move, x0, y0, xy[0], xy[1]);
                x0 = xy[0];
                y0 = xy[1];
                res2[1] = res2[1] + "(" + x0 + "," + y0 + ") ";
                attacks++;
            }
            if (attacks > 0) {
                String[] res = getMove(enemyColour, board2move, r + 1);
                board2move = moveByGoodString(enemyColour, board2move, res[1]);
                res2[0] = String.valueOf(evaluateBoard(myColour, board2move));
            }

            if (Double.parseDouble(res2[0]) > Double.parseDouble(moves[0])) {
                movesCopy = res2;
            }


            if (canAttack(myColour, board)) {
                return movesCopy;
            }

            int k, h;
            int[] tabk = {-1, -1, 1, 1};
            int[] tabh = {-1, 1, -1, 1};
            //moves by 1
            for (int y = 0; y < 4; y++) {
                k = i + 1 + tabk[y];
                h = j + 1 + tabh[y];
                if (movePossible(myColour, boardWork, i + 1, j + 1, k, h) == 1) //move is legal, by 1
                {
                    char[][] boardCopy = makeMoveBy1(boardWork, i + 1, j + 1, k, h);
                    String[] res = getMove(enemyColour, boardCopy, r + 1);
                    //move enemy
                    boardCopy = moveByGoodString(enemyColour, boardCopy, res[1]);
                    double score = evaluateBoard(myColour, boardCopy);

                    if (score > Double.parseDouble(movesCopy[0])) {
                        movesCopy[0] = Double.toString(score);
                        movesCopy[1] = "(" + k + "," + h + ") ";
                        boardWork = boardCopy;
                    }

                }
            }
            return movesCopy;
        }

        // moves[0] = "0.0"
        static String[] getMove(char myColour, char[][] board, int r) {
            int MAX_R = 7;
            char enemyColour = (myColour == 'b') ? 'w' : 'b';
            char myKing = Character.toUpperCase(myColour);
            char enemyKing = Character.toUpperCase(enemyColour);
            String[] moves = {"0.0", ""};
            if (isWon(enemyColour, board)) {
                moves[0] = "100.0";
                return moves;
            }
            if (r >= MAX_R) {
                return moves;
            }

            int bestX = 0;
            int bestY = 0;

            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board.length; j++) {
                    if (board[i][j] == myColour || board[i][j] == myKing) {
                        // my piece
                        String[] res = chooseBestMove(myColour, board, moves, i, j, r);

                        if (Double.parseDouble(res[0]) > Double.parseDouble(moves[0])) {
                            moves = res;
                            bestX = i + 1;
                            bestY = j + 1;
                        }

                    }
                }
            }
            moves[1] = "(" + bestX + "," + bestY + ") " + moves[1];
            return moves;
        }
    }

    private static char[][] moveByGoodString(char currentPlayer, char[][] board, String c) {
        if (Objects.equals(c, "")) {
            return board;
        }
        int x0 = c.charAt(1) - '0';
        int y0 = c.charAt(3) - '0';

        int x, y;
        int mv;
        for (int i = 6; i < c.length(); i += 6)  //(x,y)_  == 6 chars
        {
            x = c.charAt(i + 1) - '0';
            y = c.charAt(i + 3) - '0';

            mv = movePossible(currentPlayer, board, x0, y0, x, y);
            if (mv == -1) {
                return null;
            }

            if (mv == 1) {
                board = makeMoveBy1(board, x0, y0, x, y);
                break;
            }
            board = makeMoveBy2(board, x0, y0, x, y);

            x0 = x;
            y0 = y;
        }
        return board;
    }

    public static char[][] copyBoard(char[][] board) {
        char[][] boardCopy = new char[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boardCopy[i][j] = board[i][j];
            }
        }
        return boardCopy;
    }

    public static void main(String[] args) {

        char currentPlayer = 'b'; //  'w' or 'b'
        int gameMode = 0; // 0 basic, 1 intermediate, 3 advanced
        int row = 8;
        int col = 8;
        Scanner input = new Scanner(System.in);
        String c;

        char[][] board = new char[row][col];

        //game menu
        /*
        printMenu();
        System.out.println("Which mode do you choose? [0/1/3]");
        c = input.nextLine();
        gameMode = c.charAt(0) - '0';
        */

        //The game starts!
        initBoard(board);
        printBoard(board);

        for (; ; ) {
            System.out.println("Current player turn: " + currentPlayer + "\nYour move: ");

            if (currentPlayer == 'w') {
                char[][] copiedBoard = copyBoard(board);
                String[] res = CheckerBot.getMove(currentPlayer, copiedBoard, 0);
                c = res[1];
                System.out.println(c);
            } else {
                c = input.nextLine();
            }
            //copy board
            char[][] boardCopy = copyBoard(board);

            //load data
            if (c.length() <= 3) {
                System.out.println("Provided move format is invalid");
                continue;
            }
            int x0 = c.charAt(1) - '0';
            int y0 = c.charAt(3) - '0';

            if (x0 < 1 || x0 > 8 || y0 < 1 || y0 > 8) {
                System.out.println("Provided move format is invalid");
                continue;
            }

            //is the move possible = is the piece mine
            if (Character.toLowerCase(boardCopy[x0 - 1][y0 - 1]) != currentPlayer) {
                System.out.println("Provided move is not viable");
                continue;
            }

            int x, y;
            int mv;
            int flag = 0;
            int didMove = 0;
            for (int i = 6; i < c.length(); i += 6)  //(x,y)_  == 6 chars
            {
                didMove = 1;
                if (c.length() <= i + 3) {
                    flag = 1;
                    System.out.println("Provided move format is invalid");
                    break;
                }
                x = c.charAt(i + 1) - '0';
                y = c.charAt(i + 3) - '0';

                mv = movePossible(currentPlayer, boardCopy, x0, y0, x, y);
                if (mv == -1) {
                    flag = 1;
                    System.out.println("Provided move is not viable");
                    break;
                }

                // if canAttack - attack
                if (canAttack(currentPlayer, boardCopy) && mv == 1) {
                    flag = 1;
                    System.out.println("Provided move is not viable. You have to attack");
                    break;
                }

                if (mv == 1) {
                    boardCopy = makeMoveBy1(boardCopy, x0, y0, x, y);
                    break;
                }
                boardCopy = makeMoveBy2(boardCopy, x0, y0, x, y);

                x0 = x;
                y0 = y;
            }
            // if after all the moves still can attack - force attack
            if (canPieceAttack(currentPlayer, boardCopy, x0, y0)[0] != 0) {
                System.out.println("Provided move is not viable. You have to attack");
                continue;
            }
            if (didMove == 0) {
                System.out.println("Provided move is not viable");
                continue;
            }
            if (flag == 1) {
                continue;
            }
            board = boardCopy;
            printBoard(board);
            char enemyPlayer = (currentPlayer == 'b') ? 'w' : 'b';
            if (isWon(enemyPlayer, board)) {
                System.out.println("Player " + currentPlayer + " wins!");
                break;
            }
            currentPlayer = enemyPlayer;
        }
    }
}
