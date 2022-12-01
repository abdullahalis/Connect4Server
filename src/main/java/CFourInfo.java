import java.io.Serializable;

public class CFourInfo implements Serializable {
    Boolean twoPlayers;
    int playerNum;
    Boolean turn;
    Boolean won;
    int moveRow;
    int moveCol;
    Boolean gameStarted;


    public CFourInfo() {
        twoPlayers = false;
        int playerNum = -1;
        turn = false;
        won = false;
        moveRow = -1;
        moveCol = -1;
        gameStarted = true;
    }


    public CFourInfo(Boolean twoPlayers, int playerNum, Boolean turn, Boolean won, int moveRow, int moveCol, Boolean gameStarted) {
        this.twoPlayers = twoPlayers;
        this.playerNum = playerNum;
        this.turn = turn;
        this.won = false;
        this.moveRow = moveRow;
        this.moveCol = moveCol;
        this.gameStarted = gameStarted;
    }
}