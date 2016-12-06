package hfad.com.crusaders;

/**
 * Created by WilliamGay on 11/18/16.
 */
public class PlayMode {



    public int runGame(){
        int success = 0;


        return success;
    }

    public static int[][] indexBoard(int index, int[][] board){
        int indexId = index;
        indexUp(indexId, board, 0);
        indexLeft(indexId, board, 0);
        indexDown(indexId, board, 0);
        indexRight(indexId, board, 0);
        indexCornerUpLeft(indexId, board);
        indexCornerUpRight(indexId, board);
        indexCornerDownRight(indexId, board);
        indexCornerDownLeft(indexId, board);
        return board;
    }

    public static int[][] indexUp(int index, int[][] board, int existingDist){
        int indexId = index-10;
        int distFromKeep = 1;
        while(indexId >= 0){
            board[indexId][1] =distFromKeep + existingDist;
            distFromKeep++;
            indexId = indexId -10;
        }
        return board;
    }

    public static int[][] indexLeft(int index, int[][] board, int existingDist){
        int indexId = index-1;
        int distFromKeep = 1;
        while(indexId%10 < 9 && indexId >= 0){
            board[indexId][1] =distFromKeep + existingDist;
            distFromKeep++;
            indexId--;
        }
        return board;
    }

    public static int[][] indexDown(int index, int[][] board, int existingDist){
        int indexId = index+10;
        int distFromKeep = 1;
        while(indexId < 120){
            board[indexId][1] = distFromKeep + existingDist;
            distFromKeep++;
            indexId = indexId +10;
        }
        return board;
    }

    public static int[][] indexRight(int index, int[][] board, int existingDist){
        int indexId = index+1;
        int distFromKeep = 1;
        while(indexId%10 > 0){
            board[indexId][1] = distFromKeep + existingDist;
            distFromKeep++;
            indexId++;
        }
        return board;
    }

    public static int[][] indexCornerUpLeft(int index, int[][] board){
        int indexId = index-11;
        int distFromKeep = 1;
        while(indexId%10 < 9 && indexId >= 0){
            board[indexId][1] =board[indexId][1]+ distFromKeep;
            indexLeft(indexId, board, distFromKeep);
            indexUp(indexId, board, distFromKeep);
            distFromKeep++;
            indexId = indexId - 11;
        }
        return board;
    }

    public static int[][] indexCornerUpRight(int index, int[][] board){
        int indexId = index-9;
        int distFromKeep = 1;
        while(indexId%10 > 0 && indexId > 0){
            board[indexId][1] =board[indexId][1]+ distFromKeep;
            indexRight(indexId, board, distFromKeep);
            indexUp(indexId, board, distFromKeep);
            distFromKeep++;
            indexId = indexId - 9;
        }
        return board;
    }

    public static int[][] indexCornerDownLeft(int index, int[][] board){
        int indexId = index+9;
        int distFromKeep = 1;
        while(indexId%10 < 9 && indexId < 120){
            board[indexId][1] =board[indexId][1]+ distFromKeep;
            indexLeft(indexId, board, distFromKeep);
            indexDown(indexId, board, distFromKeep);
            distFromKeep++;
            indexId = indexId + 9;
        }
        return board;
    }

    public static int[][] indexCornerDownRight(int index, int[][] board){
        int indexId = index+11;
        int distFromKeep = 1;
        while(indexId%10 > 0 && indexId < 120){
            board[indexId][1] =board[indexId][1]+ distFromKeep;
            indexRight(indexId, board, distFromKeep);
            indexDown(indexId, board, distFromKeep);
            distFromKeep++;
            indexId = indexId + 11;
        }
        return board;
    }

    public static int chooseStart(){
        int randStartPos = (int) Math.floor((Math.random() * 40) +1);
        int[] edgeAndEndPos = new int[21];
        edgeAndEndPos[0] = 19;
        edgeAndEndPos[1] = 20;
        edgeAndEndPos[2] = 29;
        edgeAndEndPos[3] = 30;
        edgeAndEndPos[4] = 39;
        edgeAndEndPos[5] = 40;
        edgeAndEndPos[6] = 49;
        edgeAndEndPos[7] = 50;
        edgeAndEndPos[8] = 59;
        edgeAndEndPos[9] = 60;
        edgeAndEndPos[10] = 69;
        edgeAndEndPos[10] = 70;
        edgeAndEndPos[12] = 79;
        edgeAndEndPos[13] = 80;
        edgeAndEndPos[14] = 89;
        edgeAndEndPos[15] = 90;
        edgeAndEndPos[16] = 99;
        edgeAndEndPos[17] = 100;
        edgeAndEndPos[18] = 109;
        edgeAndEndPos[19] = 110;
        edgeAndEndPos[20] = 119;
        if(randStartPos <= 10) {
            return randStartPos;
        }else if(10 < randStartPos && randStartPos < 30){
            randStartPos = randStartPos - 11;
            randStartPos = edgeAndEndPos[randStartPos];
            return randStartPos;

/*            int x1 = randStartPos%10;
            if(x1%2 == 0) {
                10 + (x1 * 10);
            }
            if(x1%2 == 1) {
                9 + x1 * 10;
            }*/
        }
        return 119;
    }
}
