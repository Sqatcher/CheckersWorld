package checkers.checkersapp;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.util.Duration;

import java.util.Scanner;

public class App extends Application {

    private static final int FRAME_WIDTH  = 640;
    private static final int FRAME_HEIGHT = 640;
    private static final int EPS_X = 80;
    private static final int EPS_Y = 80;
    private static final int ROW = 8;
    private static final int COL = 8;
    double boxX = (FRAME_WIDTH - EPS_X) * 1.0 / COL;
    double boxY = (FRAME_HEIGHT - EPS_Y) * 1.0 / ROW;
    private static final double STARTING_X = 3*EPS_X/4.0;
    private static final double STARTING_Y = 3*EPS_Y/4.0;
    private static final double DRAW_BOXX_EPS = 2;
    private static final double DRAW_BOXY_EPS = 2;
    private static final Paint BACKGROUND_COLOUR = Color.LIGHTSTEELBLUE;

    GraphicsContext gc;
    Canvas canvas;

    char currentPlayer = 'b';
    private char[][] board = new char[ROW][COL];
    private int[] marked = {0, 0, 0, 0};
    private boolean isLocked = false;

    public static void main(String[] args)
    {
        launch(args);
    }

    public void start(Stage primaryStage)
    {
        AnchorPane root = new AnchorPane();

        canvas = new Canvas(FRAME_WIDTH + EPS_X/2.0, FRAME_HEIGHT + EPS_Y/2.0);
        canvas.setOnMousePressed(this::mouse);
        gc = canvas.getGraphicsContext2D();

        root.getChildren().add(canvas);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Checkers");
        primaryStage.setScene( scene );
        primaryStage.setWidth(FRAME_WIDTH + EPS_X - 26);
        primaryStage.setHeight(FRAME_HEIGHT + EPS_Y - 3);
        primaryStage.show();

        gc.setFill(BACKGROUND_COLOUR);
        gc.fillRect(0,0,FRAME_WIDTH + EPS_X/2.0, FRAME_HEIGHT + EPS_Y/2.0);
        //play();
        CheckersLogic.initBoard(board);
        //draw board
        drawBoard();
    }

    private void evaluateMove(String c)
    {
        //System.out.println("Current player turn: " + currentPlayer + "\nYour move: ");

        //copy board
        char[][] boardCopy = CheckersLogic.copyBoard(board);

        //load data
        if (c.length() <= 3) {
            System.out.println("Provided move format is invalid");
            return;
        }
        int x0 = c.charAt(1) - '0';
        int y0 = c.charAt(3) - '0';

        if (x0 < 1 || x0 > 8 || y0 < 1 || y0 > 8) {
            System.out.println("Provided move format is invalid");
            return;
        }

        //is the move possible = is the piece mine
        if (Character.toLowerCase(boardCopy[x0 - 1][y0 - 1]) != currentPlayer) {
            System.out.println("Provided move is not viable");
            return;
        }

        int x, y;
        int mv = 0;
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

            mv = CheckersLogic.movePossible(currentPlayer, boardCopy, x0, y0, x, y);
            if (mv == -1) {
                flag = 1;
                System.out.println("Provided move is not viable");
                break;
            }

            // if canAttack - attack
            if (CheckersLogic.canAttack(currentPlayer, boardCopy) && mv == 1) {
                flag = 1;
                System.out.println("Provided move is not viable. You have to attack");
                break;
            }

            if (mv == 1) {
                boardCopy = CheckersLogic.makeMoveBy1(boardCopy, x0, y0, x, y);
                break;
            }
            boardCopy = CheckersLogic.makeMoveBy2(boardCopy, x0, y0, x, y);

            x0 = x;
            y0 = y;
        }
        // if after all the moves still can attack - force attack
        if (mv == 2 && CheckersLogic.canPieceAttack(currentPlayer, boardCopy, x0, y0)[0] != 0) {
            System.out.println("You have to attack more");
            isLocked = true;
        }
        else
        {
            isLocked = false;
        }
        if (didMove == 0) {
            System.out.println("Provided move is not viable");
            return;
        }
        if (flag == 1) {
            return;
        }
        board = boardCopy;
        char enemyPlayer = (currentPlayer == 'b') ? 'w' : 'b';
        if (CheckersLogic.isWon(enemyPlayer, board)) {
            System.out.println("Player " + currentPlayer + " wins!");
            return;
        }
        if (isLocked)
        {
            return;
        }
        currentPlayer = enemyPlayer;
    }

    private void mouse(MouseEvent e)
    {
        int[] xy = getBoardCoords(e.getX(), e.getY());
        if (xy[0] == 0)
        {
            return;
        }

        int flag = markBoard(xy[0], xy[1]);

        if (flag < 2)
        {
            return;
        }

        int mv = CheckersLogic.movePossible(currentPlayer, board, marked[0], marked[1], marked[2], marked[3]);
        if (mv != -1)
        {
            String c = "(" + marked[0] + "," + marked[1] + ") ";
            c += "(" + marked[2] + "," + marked[3] + ") ";
            evaluateMove(c);
            if (isLocked)
            {
                int xx = marked[2];
                int yy = marked[3];
                marked[0] = 0;
                marked[1] = 0;
                marked[2] = 0;
                marked[3] = 0;
                clearBoard();
                drawBoard();
                markBoard(xx, yy);
                return;
            }
        }
        else
        {
            if (isLocked)
            {
                marked[2] = 0;
                marked[3] = 0;
                return;
            }
        }
        marked[0] = 0;
        marked[1] = 0;
        marked[2] = 0;
        marked[3] = 0;
        clearBoard();
        drawBoard();

        if (currentPlayer == 'w')
        {
            try {
                Thread.sleep(500);
            }
            catch (InterruptedException ignored)
            {

            }
            String[] c = CheckersLogic.CheckerBot.getMove('w', board, 0);
            System.out.println(c[1]);
            evaluateMove(c[1]);
            clearBoard();
            drawBoard();
        }
    }

    private void clearBoard()
    {
        gc.setFill(BACKGROUND_COLOUR);
        gc.fillRect(STARTING_X,STARTING_Y, COL*boxX, ROW*boxY);
    }

    private void drawBoard()
    {
        gc.setLineWidth(3);
        gc.setStroke(Color.BLACK);

        //frame
        gc.strokeRect(STARTING_X,STARTING_Y, COL*boxX, ROW*boxY);

        //mesh
        for (int i=0; i<ROW; i++)
        {
            gc.strokeRect(STARTING_X + i*boxX, STARTING_Y, boxX, 8*boxY);
            gc.strokeRect(STARTING_X,STARTING_Y + i*boxY, 8*boxX, boxY);
        }

        //pieces
        for (int i=0; i<COL; ++i)
        {
            for (int j=0; j<ROW; ++j)
            {
                if (board[i][j] != ' ')
                {
                    drawPiece(i+1, j+1, board[i][j]);
                }
            }
        }
    }

    private void drawPiece(int xx, int yy, char colour)
    {
        switch (colour) {
            case 'b', 'B' -> gc.setFill(Color.BLACK);
            case 'w', 'W' -> gc.setFill(Color.WHITESMOKE);
        }
        gc.fillOval(STARTING_X + (yy-1)*boxX + DRAW_BOXX_EPS + 4, STARTING_Y + (xx-1)*boxY + DRAW_BOXY_EPS + 4,
                boxX-2*DRAW_BOXX_EPS - 8, boxY-2*DRAW_BOXY_EPS - 8);
        gc.setLineWidth(1);
        gc.setStroke(Color.SANDYBROWN);
        gc.strokeOval(STARTING_X + (yy-1)*boxX + DRAW_BOXX_EPS + 4, STARTING_Y + (xx-1)*boxY + DRAW_BOXY_EPS + 4,
                boxX-2*DRAW_BOXX_EPS - 8, boxY-2*DRAW_BOXY_EPS - 8);
        // if king
        if (colour == Character.toUpperCase(colour))
        {
            gc.setFill(Color.DARKRED);
            gc.fillRect(STARTING_X + (yy-1)*boxX + DRAW_BOXX_EPS + 20, STARTING_Y + (xx-1)*boxY + DRAW_BOXY_EPS + 20,
                    boxX-2*DRAW_BOXX_EPS - 40, boxY-2*DRAW_BOXY_EPS - 40);
        }
    }

    private int[] getBoardCoords(double xx, double yy)
    {
        int[] res = {0,0};
        if (xx < STARTING_X || yy < STARTING_Y || xx > STARTING_X + COL*boxX || yy > STARTING_Y + ROW*boxY)
        {
            return res;
        }

        for (int i=1; i<=COL; i++)
        {
            if (xx < 3*EPS_X / 4.0 + i*boxX)
            {
                res[1] = i;
                break;
            }
        }
        for (int i=1; i<=ROW; i++)
        {
            if (yy < 3*EPS_Y / 4.0 + i*boxY)
            {
                res[0] = i;
                break;
            }
        }
        return res;
    }

    private int markBoard(int xx, int yy)
    {
        if (marked[0] == 0)
        {
            if (board[xx-1][yy-1] != currentPlayer && board[xx-1][yy-1] != Character.toUpperCase(currentPlayer))
            {
                return 0;
            }
            marked[0] = xx;
            marked[1] = yy;

            gc.setFill(Color.DEEPSKYBLUE);
            gc.fillRect(STARTING_X + (yy-1)*boxX + DRAW_BOXX_EPS, STARTING_Y + (xx-1)*boxY + DRAW_BOXY_EPS,
                    boxX - 2*DRAW_BOXX_EPS, boxY - 2*DRAW_BOXY_EPS);

            drawPiece(xx, yy, board[xx-1][yy-1]);
            return 1;
        }

        marked[2] = xx;
        marked[3] = yy;
        return 2;
    }
}
