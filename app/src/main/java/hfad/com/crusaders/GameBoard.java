package hfad.com.crusaders;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class GameBoard extends Activity {

    int keepYN = 0;
    int wallsYN = 12;
    int troopsYN = 7;
    int keepPos = 0;
    int allSpawned = 0;
    int attackerStartingPos = 279;
    int boardSize = 480;
    int numAttackers = 25;

    //arrays and adapters to keep track of the status of elements across the board
    final int[][] boardStatus = new int[boardSize][3];//[every square][type, distance from keep, health]
    final int[][] attackerStatus = new int[numAttackers][4];//id, health, guesses, dist
    final ArrayList<ImageButton> boardAdapter = new ArrayList<>();//adapter

    final Timer timer = new Timer();
    //final ScheduledExecutorService timer =
            //Executors.newSingleThreadScheduledExecutor();
    final TimerTask spawnAttackTask = new TimerTask() {
        @Override
        public void run() {
            attackerStartingPos = PlayMode.chooseStart();
            if(allSpawned == numAttackers){
                Log.v("WHY", "HERE");
                timer.cancel();
                //timer.purge();
            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boardAdapter.get(attackerStartingPos).setImageResource(R.drawable.crusaders_enemy_troop);
                    }
                });
                attackerStatus[allSpawned][0] = attackerStartingPos;
                attackerStatus[allSpawned][1] = 3;
                attackerStatus[allSpawned][2] = 2;
                attackerStatus[allSpawned][3] = boardStatus[attackerStartingPos][1];
                allSpawned++;
            }
        }
    };

    final TimerTask moveAttackTask = new TimerTask() {
        @Override
        public void run() {
            Log.v("START", "ATTACKING THREAD");
            final Context context = getApplicationContext();
            int totalAttackerHealth = 0;
            for(int i=0;i<numAttackers;i++){
                totalAttackerHealth = totalAttackerHealth + attackerStatus[i][1];
            }
            Log.v("Attk Health", Integer.toString(totalAttackerHealth));
            if(totalAttackerHealth == 0){
                Log.v("GAME", "ENEMYS DEAAD");
                for(int k=0;k<numAttackers;k++){
                    final ImageButton deadTroop = boardAdapter.get(attackerStatus[k][0]);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            deadTroop.setImageResource(R.drawable.crusaders_background);
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Congrats! You defeated all the enemy troops!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                timer.cancel();
                timer.purge();
            }
            else{
                for(int i=0;i<numAttackers;i++){
                    int[] moveOptions = new int[10]; //ids of surrounding blocks
                    int[][] optionsStatus = new int[9][2]; // array of the eight squares arroud i [][type or dist, id of block]
                    int[][] bestMove = new int[5][4]; //[id, dist, type, eligableMoves]

                    moveOptions[0] = (attackerStatus[i][0] - 11);
                    moveOptions[1] = (attackerStatus[i][0] - 10);
                    moveOptions[2] = (attackerStatus[i][0] - 9);
                    moveOptions[3] = (attackerStatus[i][0] - 1);
                    moveOptions[4] = (attackerStatus[i][0] + 1);
                    moveOptions[5] = (attackerStatus[i][0] + 9);
                    moveOptions[6] = (attackerStatus[i][0] + 10);
                    moveOptions[7] = (attackerStatus[i][0] + 11);

                    //survay the squares around
                    for(int r=0;r<8;r++){
                        //Log.v("Array moveOptions", Integer.toString(moveOptions[r]));
                        if(moveOptions[r] < 0 || moveOptions[r] > boardSize - 1 ||
                                Math.abs(attackerStatus[i][0]%10 - moveOptions[r]%10) > 1) { //out of bounds
                            optionsStatus[r][0] = -3;
                            //Log.v("optionsStatus0", Integer.toString(moveOptions[r]));
                        }else if(moveOptions[r] >= 0 && moveOptions[r] < boardSize){ //in bounds
                            if (boardStatus[moveOptions[r]][0] == 1) { //keep
                                optionsStatus[r][0] = 0;
                                optionsStatus[r][1] = moveOptions[r];
                                //Log.v("optionsStatus1", Integer.toString(moveOptions[r]));
                            } else if (boardStatus[moveOptions[r]][0] == 3) { //troop
                                optionsStatus[r][0] = -1;
                                optionsStatus[r][1] = moveOptions[r];
                                //Log.v("optionsStatus2", Integer.toString(moveOptions[r]));
                                boardStatus[moveOptions[r]][1]--;
                            } else if (boardStatus[moveOptions[r]][0] == 2) { //wall
                                optionsStatus[r][0] = -2;
                                optionsStatus[r][1] = moveOptions[r];
                                Log.v("optionsStatus3", Integer.toString(moveOptions[r]));
                            } else { //empty
                                optionsStatus[r][0] = boardStatus[moveOptions[r]][1];
                                optionsStatus[r][1] = moveOptions[r];
                                //Log.v("optionsStatus4", Integer.toString(boardStatus[moveOptions[r]][1]));
                            }
                        }
                    }
                    //Log.v("$$$$$$", "We done here (survaying)");

                    //decide what to do
                    for(int v=0;v<8;v++){
                        //Log.v("Iteration", Integer.toString(optionsStatus[v][0]));
                        //if in contact with the keep
                        if(optionsStatus[v][0] == 0){
                            bestMove[0][1] = -5;
                            bestMove[0][0] = optionsStatus[v][1];
                            //Log.v("Keep contact", Integer.toString(bestMove[0][0]));
                            //if in contact with troop
                        }else if(optionsStatus[v][0] == -1){
                            bestMove[0][1] = -1;
                            bestMove[0][0] = optionsStatus[v][1];
                            //Log.v("Troop contact", Integer.toString(bestMove[0][0]));
                            //if only empty spaces and no clear way foward
                            //if in contact with wall
                        }else if(optionsStatus[v][0] == -2){
                            bestMove[0][1] = -2;
                            bestMove[0][0] = optionsStatus[v][1];
                        }else if(optionsStatus[v][0] > 0){
                            //Log.v("Entered empty space", Integer.toString(bestMove[0][1]));
                            //Log.v("Entered empty space", Integer.toString(optionsStatus[v][0]));
                            if(bestMove[0][1] == 0){
                                bestMove[0][1] = optionsStatus[v][0]; //set dist of best move
                                bestMove[0][0] = optionsStatus[v][1]; //set id of best move
                            }else if(bestMove[0][1] > optionsStatus[v][0]){ //if the cur best move is higher than another option
                                bestMove[0][1] = optionsStatus[v][0]; //set dist of best move
                                bestMove[0][0] = optionsStatus[v][1]; //set id of best move
                                bestMove[0][3] = 0;
                                //Log.v("Usurped", Integer.toString(bestMove[0][0]));
                            }else if(bestMove[0][1] == optionsStatus[v][0]){ //if they have equal distances
                                bestMove[1][1] = optionsStatus[v][0];
                                bestMove[1][0] = optionsStatus[v][1];
                                bestMove[0][3]++;
                                //Log.v("Tie", Integer.toString(bestMove[0][0]));
                            }
                            //Log.v("^1^^^^^^", Integer.toString(attackerStatus[i][0]));
                        }
                    }

                    //execute decision
                    if(bestMove[0][1] == -5){
                        //attack keep
                        Log.v("Entered keep attack", "Y");
                        //if keep is at zero health
                        if(boardStatus[keepPos][1] == 0){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    boardAdapter.get(keepPos).setImageResource
                                            (R.drawable.crusaders_game_over);
                                }
                            });
                            runOnUiThread(new Runnable() {
                                              @Override
                                              public void run() {
                                                  Toast.makeText(context, "Well, thats game. Better luck next time!",
                                                          Toast.LENGTH_SHORT).show();
                                              }
                                          });
                            Log.v("END", "KEEP DESTROYED");
                            timer.cancel();
                            timer.purge();
                        }
                        boardStatus[keepPos][2]--;
                        //Log.v("()((()()(", Integer.toString(boardStatus[keepPos][1]));
                        //break;
                        //if in contact with troop
                    }else if(bestMove[0][1] == -1) {
                        //attack troop
                        Log.v("Entered attack troop", "Y");
                        if (attackerStatus[i][1] <= 0) {
                            Log.v("Troop killed", "Y");
                            final ImageButton deadGuy = boardAdapter.get(attackerStatus[i][0]);
                            //boardStatus[moveOptions[v]][0] = 2;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    deadGuy.setImageResource(R.drawable.crusaders_background);
                                }
                            });
                        }else if (boardStatus[bestMove[0][0]][2] <= 0) {
                            Log.v("Troop killed", "Y");
                            final ImageButton deadGuy = boardAdapter.get(bestMove[0][0]);
                            boardStatus[bestMove[0][0]][0] = 0;
                            //boardStatus[moveOptions[v]][0] = 2;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    deadGuy.setImageResource(R.drawable.crusaders_background);
                                }
                            });
                        } else {
                            boardStatus[bestMove[0][0]][2]--;
                            attackerStatus[i][1]--;
                            Log.v("Attacking", Integer.toString(boardStatus[bestMove[0][0]][1]));
                            Log.v("Attacking", Integer.toString(attackerStatus[i][1]));
                        }
                        //if wall is best way forward
                    } else if(bestMove[0][1] == -2){
                        Log.v("Step", "2");
                        if(boardStatus[bestMove[0][0]][2] <= 0){
                            Log.v("Step", "3a");
                            final ImageButton destroyedWall = boardAdapter.get(bestMove[0][0]);
                            boardStatus[bestMove[0][0]][0] = 0;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    destroyedWall.setImageResource(R.drawable.crusaders_background);
                                }
                            });
                        }else {
                            Log.v("Step", "3b");
                            boardStatus[bestMove[0][0]][2]--;
                            Log.v("Wall health", Integer.toString(boardStatus[bestMove[0][0]][2]));
                        }
                    }
                    //if no closer moves but walls to attack
                    else if(attackerStatus[i][3] < bestMove[0][1]){
                        Log.v("Entered wall attack", "Y");
                        for(int v=0;v<8;v++){
                            Log.v("Step", Integer.toString(optionsStatus[v][0]));
                            if(bestMove[0][1] == -2){
                                Log.v("Step", "2");
                                if(boardStatus[moveOptions[v]][2] == 0){
                                    Log.v("Step", "3a");
                                    final ImageButton destroyedWall = boardAdapter.get
                                            (moveOptions[v]);
                                    boardStatus[moveOptions[v]][0] = 0;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            destroyedWall.setImageResource(R.drawable.crusaders_background);
                                        }
                                    });
                                }else {
                                    Log.v("Step", "3b");
                                    boardStatus[moveOptions[v]][2]--;
                                }
                                attackerStatus[i][2]--;
                            }else{
                                attackerStatus[i][2]--;
                                Log.v("$$$$$$$$$$", "no best move, nothing to attack");
                            }
                        }
                    }
                    //Log.v("^^2^^^^^", Integer.toString(attackerStatus[i][0]));

                    //if two equal moves
                    else if(attackerStatus[i][3] == bestMove[0][1] && attackerStatus[i][2] >= 0){
                        Log.v("Entered move guess", "Y");
                        if(bestMove[0][3] >= 1){
                            final int randChoice = (int) Math.floor((Math.random() *
                                    bestMove[0][3]) + 1);
                            Log.v("randChoice:", Integer.toString(randChoice));
                            final ImageButton newSquare = boardAdapter.get(bestMove[randChoice][0]);
                            //Log.v("^^^3^^^^", Integer.toString(attackerStatus[i][0]));
                            final ImageButton oldSquare = boardAdapter.get(attackerStatus[i][0]);
                            attackerStatus[i][0] = bestMove[randChoice][0];
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //Log.v("########", "We should be updating attk pos");
                                    newSquare.setImageResource(R.drawable.crusaders_enemy_troop);
                                    oldSquare.setImageResource(R.drawable.crusaders_background);
                                }
                            });
                            attackerStatus[i][2]--;
                        }
                    }

                    //if normal move
                    else if(attackerStatus[i][3] > bestMove[0][1]){
                        Log.v("Entered normal move", "Y");
                        final ImageButton newSquare = boardAdapter.get(bestMove[0][0]);
                        final ImageButton oldSquare = boardAdapter.get(attackerStatus[i][0]);
                        attackerStatus[i][0] = bestMove[0][0];
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                newSquare.setImageResource(R.drawable.crusaders_enemy_troop);
                                oldSquare.setImageResource(R.drawable.crusaders_background);
                            }
                        });
                    }
                    Log.v("--Attacker--", Integer.toString(i));
                }
            }
            Log.v("you", "win");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        final String mode = bundle.getString("mode");

        //get size of screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;
        int numCol = screenWidth/20;
        int numRow = screenHeight/24;
        int totalSpaces = boardSize;

        //set up gameboard
        //int[[[T1][time from K + time to destroy1]]
        //    [[T2][time from K + time to destroy2]]]
        GridLayout gridLayout = new GridLayout(this);
        gridLayout.setColumnCount(20);
        gridLayout.setRowCount(24);
        //hgap, vga
        gridLayout.setUseDefaultMargins(false);
        gridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        gridLayout.setRowOrderPreserved(false);
        gridLayout.setMinimumWidth(screenWidth/20);
        int r = 0;
        int c = 0;

        for (int i = 0; i < totalSpaces; i++) {
            if (c == 20){
                c = 0;
                r++;
            }
            final ImageButton tmpButton = new ImageButton(this);
            tmpButton.setScaleType(ImageButton.ScaleType.CENTER_CROP);
            tmpButton.setImageResource(R.drawable.crusaders_background);
            tmpButton.setId(i);

            final Context context = getApplicationContext();
            ImageButton.OnClickListener imgButtonHandler = new View.OnClickListener() {
                public void onClick(View v) {
                    Log.v("######", "ENTERED");
                        if (keepYN == 0) {
                            boardAdapter.get(v.getId()).setImageResource(R.drawable.crusaders_keep);
                            boardStatus[v.getId()][0] = 1;
                            boardStatus[v.getId()][2] =+ 15;
                            keepYN++;
                            Log.v("######", Integer.toString(v.getId()));
                            keepPos = v.getId();
                            v.setTag("K");
                            Toast.makeText(context, "Now for the walls",
                                    Toast.LENGTH_SHORT).show();
                        } else if (wallsYN > 0 && boardStatus[v.getId()][0] == 0) {

                            if(wallsYN == 1){
                                Toast.makeText(context, "And most importantly, the troops!",
                                        Toast.LENGTH_SHORT).show();
                            }
                            boardAdapter.get(v.getId()).setImageResource(R.drawable.crusaders_walls);
                            boardStatus[v.getId()][0] = 2;
                            boardStatus[v.getId()][2] = 5;
                            wallsYN--;
                            v.setTag("W");
                        } else if (troopsYN > 0 && boardStatus[v.getId()][0] == 0){

                            // GAME START GAME START GAME START GAME START GAME START GAME START

                            if(troopsYN == 1){


                                int[][] boardStatusTmp = PlayMode.indexBoard(keepPos, boardStatus);
                                for(int r=0;r < boardSize;r++){
                                    boardStatus[r][1] = boardStatus[r][1] + boardStatusTmp[r][1];
                                    Log.v("board[r][0]", Integer.toString(boardStatus[r][0]));
                                    if(boardStatus[r][1] > 0) {
                                        //boardAdapter.get(r).setImageResource(R.drawable.crusaders_walls);
                                        int dist = boardStatus[r][1];
                                    }
                                }

                                //spawn enemy
                                for(int i=0;i<numAttackers;i++) {
                                    attackerStartingPos = PlayMode.chooseStart();
                                    if (boardStatus[attackerStartingPos][0] != 0 && boardStatus[attackerStartingPos][0] != 7){
                                        i--;
                                    }else {
                                        boardStatus[attackerStartingPos][0] = 7;
                                        boardAdapter.get(attackerStartingPos).setImageResource(R.drawable.crusaders_enemy_troop);
                                        attackerStatus[i][0] = attackerStartingPos;
                                        attackerStatus[i][1] = 3;
                                        attackerStatus[i][2] = 2;
                                        attackerStatus[i][3] = boardStatus[attackerStartingPos][1];
                                    }
                                }
                                timer.scheduleAtFixedRate(moveAttackTask, 1000, 600);

                            }

                            // GAME START GAME START GAME START GAME START GAME START GAME START

                            boardAdapter.get(v.getId()).setImageResource(R.drawable.crusaders_troops);
                            boardStatus[v.getId()][0] = 3;
                            boardStatus[v.getId()][2] = 7;
                            troopsYN--;
                            v.setTag("T");
                        }
                }
            };
            tmpButton.setOnClickListener(imgButtonHandler);
            gridLayout.addView(tmpButton, i);
            boardAdapter.add(tmpButton);
            boardStatus[i][0] = 0;
            boardStatus[i][1] = 0;
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.height = numRow;
            params.width = numCol;
            params.leftMargin = 0;
            params.topMargin = 0;
            params.columnSpec = GridLayout.spec(c);
            params.rowSpec = GridLayout.spec(r);
            tmpButton.setLayoutParams(params);
            c++;
        }
        setContentView(gridLayout);

        Toast.makeText(this, "Time to place your Keep. Do so wisely!",
                Toast.LENGTH_SHORT).show();
    }


    public void determineGameMode(){
        Toast.makeText(this, "Time to place your Keep. Do so wisely!",
                Toast.LENGTH_SHORT).show();
    }
}
