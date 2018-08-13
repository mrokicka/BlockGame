package com.example.guest1.stackgame;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private GridLayout c1;
    private GridLayout c2;
    private GridLayout c3;
    private TextView score;
    private HashMap<GridLayout, ArrayList<ImageView>> position;
    private boolean pTurn;
    private boolean gameStart;
    private Button startResetButton;
    private int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        c1 = findViewById(R.id.column1);
        c2 = findViewById(R.id.column2);
        c3 = findViewById(R.id.column3);

        score = findViewById(R.id.scoreText);
        counter = 0;

        startResetButton = findViewById(R.id.startButton);

        if(savedInstanceState != null) {
            score.setText(savedInstanceState.getString("SCORE_TEXT"));
            counter = Integer.parseInt(savedInstanceState.getString("SCORE_COUNTER"));
            pTurn = savedInstanceState.getString("PLAYER_TURN").equals("true");
            gameStart = savedInstanceState.getString("GAME_START").equals("true");
            startResetButton.setText(savedInstanceState.getString("START_RESET_BUTTON_TEXT"));
        } else {
            pTurn = false;
            gameStart = false;
        }


        /**
         * I used a hash map that will, given a gridlayout, give me an array list of the imageviews that
         * are in the grid layout. With this, I can get the index of the imageview in the grid layout because
         * it is the same as the index of the imageview in the arraylist. There's probably an easier
         * way to do this.
         */
        position = new HashMap<GridLayout, ArrayList<ImageView>>();


        if(savedInstanceState != null) {
            int[] sizes = {Integer.parseInt(savedInstanceState.getString("C1_HEIGHT")), Integer.parseInt(savedInstanceState.getString("C2_HEIGHT")), Integer.parseInt(savedInstanceState.getString("C3_HEIGHT"))};
            for(int i = 0; i < 3; i++) {
                if (i == 0)
                    position.put(c1, new ArrayList<ImageView>());
                else if (i == 1)
                    position.put(c2, new ArrayList<ImageView>());
                else
                    position.put(c3, new ArrayList<ImageView>());

                for(int j = 0; j < sizes[i]; j++) {
                    GridLayout temp;
                    if (i == 0)
                        temp = c1;
                    else if (i == 1)
                        temp = c2;
                    else
                        temp = c3;

                    int orientation = this.getResources().getConfiguration().orientation;

                    ImageView imageview;
                    if(orientation == Configuration.ORIENTATION_PORTRAIT) {
                        imageview = (ImageView) getLayoutInflater().inflate(R.layout.stack_stuff, temp, false);
                    } else {
                        imageview = (ImageView) getLayoutInflater().inflate(R.layout.stack_stuff_landscape, temp, false);
                    }

                    temp.addView(imageview);
                    position.get(temp).add(imageview);
                }
            }
        } else {
            loadBlocks();
        }
    }

    /**
     * This gets called to fill the grids with blocks when the program is first started up and whenever the player resets the game.
     */
    public void loadBlocks() {
        Random r = new Random();
        for(int i = 0; i < 3; i++) {

            int x = r.nextInt(7) + 1; // gives back 1 through 8.
            if (i == 0)
                position.put(c1, new ArrayList<ImageView>());
            else if (i == 1)
                position.put(c2, new ArrayList<ImageView>());
            else
                position.put(c3, new ArrayList<ImageView>());

            for (int j = 0; j < x; j++) {
                GridLayout temp;
                if (i == 0)
                    temp = c1;
                else if (i == 1)
                    temp = c2;
                else
                    temp = c3;

                int orientation = this.getResources().getConfiguration().orientation;

                ImageView imageview;
                if(orientation == Configuration.ORIENTATION_PORTRAIT) {
                    imageview = (ImageView) getLayoutInflater().inflate(R.layout.stack_stuff, temp, false);
                } else {
                    imageview = (ImageView) getLayoutInflater().inflate(R.layout.stack_stuff_landscape, temp, false);
                }

                temp.addView(imageview);
                position.get(temp).add(imageview);

            }

        }
    }

    /**
     * This is the player's turn. If pTurn is true and the game has started, the player can affect the blocks.
     * @param v - the block that the player clicks/touches
     */
    public void removeBlocks(View v) {
        if(pTurn && gameStart) {
            GridLayout g = (GridLayout) v.getParent();

            if (position.get(g).indexOf(v) == g.getChildCount() - 3) {
                for (int i = 0; i < 3; i++) {
                    g.removeViewAt(g.getChildCount() - 1);
                    position.get(g).remove(position.get(g).size() - 1);
                }
            } else if (position.get(g).indexOf(v) == g.getChildCount() - 2) {
                for (int i = 0; i < 2; i++) {
                    g.removeViewAt(g.getChildCount() - 1);
                    position.get(g).remove(position.get(g).size() - 1);
                }
            } else if (position.get(g).indexOf(v) == g.getChildCount() - 1) {
                g.removeView(v);
                position.get(g).remove(v);
            }

            updateGame();
        }
    }

    /**
     * This is called when the button at the bottom is pressed and the game is not currently going.
     * If the game has ended, the button changes to restart and we can refill the grid layout with blocks.
     * @param v the button at the bottom that we press to start the game or reset the blocks
     */
    public void startGame(View v) {
        if(startResetButton.getText().equals("START")) {
            startResetButton.setText("GAME IS IN PROGRESS");
            gameStart = true;

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setMessage("Do you want to go first?").setTitle("TURN ORDER");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    pTurn = true;
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateGame();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else if(startResetButton.getText().equals("RESTART")) {
            loadBlocks();
            startResetButton.setText("START");
        }
    }

    /**
     * A very annoying method that should cover all the best moves a computer can make to win the game.
     * There's probably an easier way to do this. I hope there's an easier way to do this.
     */
    public void enemyTurn() {
        if(c1.getChildCount() % 4 == 0 && c2.getChildCount() % 4 == 0 && c3.getChildCount() % 4 == 0) { // player theoretically wins
            if(c1.getChildCount() != 0) {
                c1.removeViewAt(c1.getChildCount() - 1);
                position.get(c1).remove(position.get(c1).size() - 1);
            } else if(c2.getChildCount() != 0) {
                c2.removeViewAt(c2.getChildCount() - 1);
                position.get(c2).remove(position.get(c2).size() - 1);
                c2.removeViewAt(c2.getChildCount() - 1);
                position.get(c2).remove(position.get(c2).size() - 1);
            } else {
                c3.removeViewAt(c3.getChildCount() - 1);
                position.get(c3).remove(position.get(c3).size() - 1);
            }
        } else if((c1.getChildCount() % 4 == 0 && c2.getChildCount() % 4 == 0) || (c1.getChildCount() % 4 == 0 && c3.getChildCount() % 4 == 0) || (c2.getChildCount() % 4 == 0 && c3.getChildCount() % 4 == 0)) { // computer wins

            if(c1.getChildCount() % 4 == 0 && c2.getChildCount() % 4 == 0) {

                if(c3.getChildCount() - 1 % 4 == 0) {
                    c3.removeViewAt(c3.getChildCount() - 1);
                    position.get(c3).remove(position.get(c3).size() - 1);
                }
                else if(c3.getChildCount() - 2 % 4 == 0) {
                    c3.removeViewAt(c3.getChildCount() - 1);
                    position.get(c3).remove(position.get(c3).size() - 1);
                    c3.removeViewAt(c3.getChildCount() - 1);
                    position.get(c3).remove(position.get(c3).size() - 1);
                }
                else {
                    c3.removeViewAt(c3.getChildCount() - 1);
                    position.get(c3).remove(position.get(c3).size() - 1);
                    c3.removeViewAt(c3.getChildCount() - 1);
                    position.get(c3).remove(position.get(c3).size() - 1);
                    c3.removeViewAt(c3.getChildCount() - 1);
                    position.get(c3).remove(position.get(c3).size() - 1);
                }

            } else if(c1.getChildCount() % 4 == 0 && c3.getChildCount() % 4 == 0) {

                if(c2.getChildCount() - 1 % 4 == 0) {
                    c2.removeViewAt(c2.getChildCount() - 1);
                    position.get(c2).remove(position.get(c2).size() - 1);
                }
                else if(c2.getChildCount() - 2 % 4 == 0) {
                    c2.removeViewAt(c2.getChildCount() - 1);
                    position.get(c2).remove(position.get(c2).size() - 1);
                    c2.removeViewAt(c2.getChildCount() - 1);
                    position.get(c2).remove(position.get(c2).size() - 1);
                }
                else {
                    c2.removeViewAt(c2.getChildCount() - 1);
                    position.get(c2).remove(position.get(c2).size() - 1);
                    c2.removeViewAt(c2.getChildCount() - 1);
                    position.get(c2).remove(position.get(c2).size() - 1);
                    c2.removeViewAt(c2.getChildCount() - 1);
                    position.get(c2).remove(position.get(c2).size() - 1);
                }

            } else {

                if(c1.getChildCount() - 1 % 4 == 0) {
                    c1.removeViewAt(c1.getChildCount() - 1);
                    position.get(c1).remove(position.get(c1).size() - 1);
                }
                else if(c1.getChildCount() - 2 % 4 == 0) {
                    c1.removeViewAt(c1.getChildCount() - 1);
                    position.get(c1).remove(position.get(c1).size() - 1);
                    c1.removeViewAt(c1.getChildCount() - 1);
                    position.get(c1).remove(position.get(c1).size() - 1);
                }
                else {
                    c1.removeViewAt(c1.getChildCount() - 1);
                    position.get(c1).remove(position.get(c1).size() - 1);
                    c1.removeViewAt(c1.getChildCount() - 1);
                    position.get(c1).remove(position.get(c1).size() - 1);
                    c1.removeViewAt(c1.getChildCount() - 1);
                    position.get(c1).remove(position.get(c1).size() - 1);
                }

            }

        } else if(c1.getChildCount() % 4 == 0 || c2.getChildCount() % 4 == 0 || c3.getChildCount() % 4 == 0) {

            if(c1.getChildCount() % 4 == 0) {

                if(c2.getChildCount() % 4 == c3.getChildCount() % 4) { // player theoretically wins

                    if(c1.getChildCount() != 0) {
                        c1.removeViewAt(c1.getChildCount() - 1);
                        position.get(c1).remove(position.get(c1).size() - 1);
                    } else if(c2.getChildCount() != 0) {
                        c2.removeViewAt(c2.getChildCount() - 1);
                        position.get(c2).remove(position.get(c2).size() - 1);
                    } else {
                        c3.removeViewAt(c3.getChildCount() - 1);
                        position.get(c3).remove(position.get(c3).size() - 1);
                    }

                } else { // make them equal

                    if(c2.getChildCount() > c3.getChildCount()) {
                        if(c2.getChildCount() - 1 % 4 == c3.getChildCount() % 4) {
                            c2.removeViewAt(c2.getChildCount() - 1);
                            position.get(c2).remove(position.get(c2).size() - 1);
                        } else if(c2.getChildCount() - 2 % 4 == c3.getChildCount() % 4) {
                            c2.removeViewAt(c2.getChildCount() - 1);
                            position.get(c2).remove(position.get(c2).size() - 1);
                            c2.removeViewAt(c2.getChildCount() - 1);
                            position.get(c2).remove(position.get(c2).size() - 1);
                        } else {
                            c2.removeViewAt(c2.getChildCount() - 1);
                            position.get(c2).remove(position.get(c2).size() - 1);
                            c2.removeViewAt(c2.getChildCount() - 1);
                            position.get(c2).remove(position.get(c2).size() - 1);
                            c2.removeViewAt(c2.getChildCount() - 1);
                            position.get(c2).remove(position.get(c2).size() - 1);

                        }

                    } else {
                        if(c3.getChildCount() - 1 % 4 == c2.getChildCount() % 4) {
                            c3.removeViewAt(c3.getChildCount() - 1);
                            position.get(c3).remove(position.get(c3).size() - 1);
                        } else if(c3.getChildCount() - 2 % 4 == c2.getChildCount() % 4) {
                            c3.removeViewAt(c3.getChildCount() - 1);
                            position.get(c3).remove(position.get(c3).size() - 1);
                            c3.removeViewAt(c3.getChildCount() - 1);
                            position.get(c3).remove(position.get(c3).size() - 1);
                        } else {
                            c3.removeViewAt(c3.getChildCount() - 1);
                            position.get(c3).remove(position.get(c3).size() - 1);
                            c3.removeViewAt(c3.getChildCount() - 1);
                            position.get(c3).remove(position.get(c3).size() - 1);
                            c3.removeViewAt(c3.getChildCount() - 1);
                            position.get(c3).remove(position.get(c3).size() - 1);
                        }
                    }
                }

            } else if(c2.getChildCount() % 4 == 0) {

                if(c1.getChildCount() % 4 == c3.getChildCount() % 4) { // player wins

                    if(c1.getChildCount() != 0) {
                        c1.removeViewAt(c1.getChildCount() - 1);
                        position.get(c1).remove(position.get(c1).size() - 1);
                    } else if(c2.getChildCount() != 0) {
                        c2.removeViewAt(c2.getChildCount() - 1);
                        position.get(c2).remove(position.get(c2).size() - 1);
                    } else {
                        c3.removeViewAt(c3.getChildCount() - 1);
                        position.get(c3).remove(position.get(c3).size() - 1);
                    }

                } else { // make them equal

                    if(c1.getChildCount() > c3.getChildCount()) {
                        if(c1.getChildCount() - 1 % 4 == c3.getChildCount() % 4) {
                            c1.removeViewAt(c1.getChildCount() - 1);
                            position.get(c1).remove(position.get(c1).size() - 1);
                        } else if(c1.getChildCount() - 2 % 4 == c3.getChildCount() % 4) {
                            c1.removeViewAt(c1.getChildCount() - 1);
                            position.get(c1).remove(position.get(c1).size() - 1);
                            c1.removeViewAt(c1.getChildCount() - 1);
                            position.get(c1).remove(position.get(c1).size() - 1);
                        } else {
                            c1.removeViewAt(c1.getChildCount() - 1);
                            position.get(c1).remove(position.get(c1).size() - 1);
                            c1.removeViewAt(c1.getChildCount() - 1);
                            position.get(c1).remove(position.get(c1).size() - 1);
                            c1.removeViewAt(c1.getChildCount() - 1);
                            position.get(c1).remove(position.get(c1).size() - 1);

                        }

                    } else {
                        if(c3.getChildCount() - 1 % 4 == c1.getChildCount() % 4) {
                            c3.removeViewAt(c3.getChildCount() - 1);
                            position.get(c3).remove(position.get(c3).size() - 1);
                        } else if(c3.getChildCount() - 2 % 4 == c2.getChildCount() % 4) {
                            c3.removeViewAt(c3.getChildCount() - 1);
                            position.get(c3).remove(position.get(c3).size() - 1);
                            c3.removeViewAt(c3.getChildCount() - 1);
                            position.get(c3).remove(position.get(c3).size() - 1);
                        } else {
                            c3.removeViewAt(c3.getChildCount() - 1);
                            position.get(c3).remove(position.get(c3).size() - 1);
                            c3.removeViewAt(c3.getChildCount() - 1);
                            position.get(c3).remove(position.get(c3).size() - 1);
                            c3.removeViewAt(c3.getChildCount() - 1);
                            position.get(c3).remove(position.get(c3).size() - 1);
                        }
                    }
                }

            } else {

                if(c1.getChildCount() % 4 == c2.getChildCount() % 4) { // player wins
                    if(c1.getChildCount() != 0) {
                        c1.removeViewAt(c1.getChildCount() - 1);
                        position.get(c1).remove(position.get(c1).size() - 1);
                    } else if(c2.getChildCount() != 0) {
                        c2.removeViewAt(c2.getChildCount() - 1);
                        position.get(c2).remove(position.get(c2).size() - 1);
                    } else {
                        c3.removeViewAt(c3.getChildCount() - 1);
                        position.get(c3).remove(position.get(c3).size() - 1);
                    }

                } else { // make them equal
                    if(c2.getChildCount() > c1.getChildCount()) {
                        if(c2.getChildCount() - 1 % 4 == c3.getChildCount() % 4) {
                            c2.removeViewAt(c2.getChildCount() - 1);
                            position.get(c2).remove(position.get(c2).size() - 1);
                        } else if(c2.getChildCount() - 2 % 4 == c3.getChildCount() % 4) {
                            c2.removeViewAt(c2.getChildCount() - 1);
                            position.get(c2).remove(position.get(c2).size() - 1);
                            c2.removeViewAt(c2.getChildCount() - 1);
                            position.get(c2).remove(position.get(c2).size() - 1);
                        } else {
                            c2.removeViewAt(c2.getChildCount() - 1);
                            position.get(c2).remove(position.get(c2).size() - 1);
                            c2.removeViewAt(c2.getChildCount() - 1);
                            position.get(c2).remove(position.get(c2).size() - 1);
                            c2.removeViewAt(c2.getChildCount() - 1);
                            position.get(c2).remove(position.get(c2).size() - 1);

                        }

                    } else {
                        if(c1.getChildCount() - 1 % 4 == c2.getChildCount() % 4) {
                            c1.removeViewAt(c1.getChildCount() - 1);
                            position.get(c1).remove(position.get(c1).size() - 1);
                        } else if(c1.getChildCount() - 2 % 4 == c2.getChildCount() % 4) {
                            c1.removeViewAt(c1.getChildCount() - 1);
                            position.get(c1).remove(position.get(c1).size() - 1);
                            c1.removeViewAt(c1.getChildCount() - 1);
                            position.get(c1).remove(position.get(c1).size() - 1);
                        } else {
                            c1.removeViewAt(c1.getChildCount() - 1);
                            position.get(c1).remove(position.get(c1).size() - 1);
                            c1.removeViewAt(c1.getChildCount() - 1);
                            position.get(c1).remove(position.get(c1).size() - 1);
                            c1.removeViewAt(c1.getChildCount() - 1);
                            position.get(c1).remove(position.get(c1).size() - 1);
                        }
                    }
                }

            }
        } else {
            if(c1.getChildCount() % 4 == c3.getChildCount() % 4) {

                if(c2.getChildCount() - 1 % 4 == 0) {
                    c2.removeViewAt(c2.getChildCount() - 1);
                    position.get(c2).remove(position.get(c2).size() - 1);
                } else if(c2.getChildCount() - 2 % 4 == 0) {
                    c2.removeViewAt(c2.getChildCount() - 1);
                    position.get(c2).remove(position.get(c2).size() - 1);
                    c2.removeViewAt(c2.getChildCount() - 1);
                    position.get(c2).remove(position.get(c2).size() - 1);
                } else {
                    c2.removeViewAt(c2.getChildCount() - 1);
                    position.get(c2).remove(position.get(c2).size() - 1);
                    c2.removeViewAt(c2.getChildCount() - 1);
                    position.get(c2).remove(position.get(c2).size() - 1);
                    c2.removeViewAt(c2.getChildCount() - 1);
                    position.get(c2).remove(position.get(c2).size() - 1);
                }

            } else if(c1.getChildCount() % 4 == c2.getChildCount() % 4) {

                if(c3.getChildCount() - 1 % 4 == 0) {
                    c3.removeViewAt(c3.getChildCount() - 1);
                    position.get(c3).remove(position.get(c3).size() - 1);
                } else if(c3.getChildCount() - 2 % 4 == 0) {
                    c3.removeViewAt(c3.getChildCount() - 1);
                    position.get(c3).remove(position.get(c3).size() - 1);
                    c3.removeViewAt(c3.getChildCount() - 1);
                    position.get(c3).remove(position.get(c3).size() - 1);
                } else {
                    c3.removeViewAt(c3.getChildCount() - 1);
                    position.get(c3).remove(position.get(c3).size() - 1);
                    c3.removeViewAt(c3.getChildCount() - 1);
                    position.get(c3).remove(position.get(c3).size() - 1);
                    c3.removeViewAt(c3.getChildCount() - 1);
                    position.get(c3).remove(position.get(c3).size() - 1);
                }

            } else if(c2.getChildCount() % 4 == c3.getChildCount() % 4) {

                if(c1.getChildCount() - 1 % 4 == 0) {
                    c1.removeViewAt(c1.getChildCount() - 1);
                    position.get(c1).remove(position.get(c1).size() - 1);
                } else if(c1.getChildCount() - 2 % 4 == 0) {
                    c1.removeViewAt(c1.getChildCount() - 1);
                    position.get(c1).remove(position.get(c1).size() - 1);
                    c1.removeViewAt(c1.getChildCount() - 1);
                    position.get(c1).remove(position.get(c1).size() - 1);
                } else {
                    c1.removeViewAt(c1.getChildCount() - 1);
                    position.get(c1).remove(position.get(c1).size() - 1);
                    c1.removeViewAt(c1.getChildCount() - 1);
                    position.get(c1).remove(position.get(c1).size() - 1);
                    c1.removeViewAt(c1.getChildCount() - 1);
                    position.get(c1).remove(position.get(c1).size() - 1);
                }

            } else {
                if(c1.getChildCount() != 0) {
                    c1.removeViewAt(c1.getChildCount() - 1);
                    position.get(c1).remove(position.get(c1).size() - 1);
                } else if(c2.getChildCount() != 0) {
                    c2.removeViewAt(c2.getChildCount() - 1);
                    position.get(c2).remove(position.get(c2).size() - 1);
                } else if(c3.getChildCount() != 0) {
                    c3.removeViewAt(c3.getChildCount() - 1);
                    position.get(c3).remove(position.get(c3).size() - 1);
                }

            }
        }
    }

    /**
     * This is called either after the player's turn or before the player's first turn if they decided
     * to not go first. It checks if the player or enemy has won, calls enemyTurn() and then allows the player
     * to go again, and finally sets things up so that the game can be restarted if someone won.
     */
    public void updateGame() {
        if(c1.getChildCount() == 0 && c2.getChildCount() == 0 && c3.getChildCount() == 0) {
            Toast.makeText(this, "Player WINS!", Toast.LENGTH_LONG).show();
            gameStart = false;
            counter++;
            score.setText("Score: " + counter);
            startResetButton.setText("RESTART");
        } else {
            enemyTurn();
            if(c1.getChildCount() == 0 && c2.getChildCount() == 0 && c3.getChildCount() == 0) {
                Toast.makeText(this, "Enemy WINS!", Toast.LENGTH_LONG).show();
                gameStart = false;
                counter--;
                score.setText("Score: " + counter);
                startResetButton.setText("RESTART");
            }
        }

        pTurn = true;

    }

    /**
     * The stuff needed to make sure we don't lose anything when we rotate the phone.
     * Unfortunately, even though the grids are saved successfully, I couldn't figure out how to
     * fit the blocks on the landscape screen that has smaller height.
     * @param savedInstanceState the saved data goes in here
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("SCORE_TEXT", score.getText().toString());
        savedInstanceState.putString("SCORE_COUNTER", counter + "");
        savedInstanceState.putString("START_RESET_BUTTON_TEXT", startResetButton.getText().toString());
        savedInstanceState.putString("GAME_START", gameStart ? "true" : "false");
        savedInstanceState.putString("PLAYER_TURN", pTurn ? "true" : "false");
        savedInstanceState.putString("C1_HEIGHT", c1.getChildCount() + "");
        savedInstanceState.putString("C2_HEIGHT", c2.getChildCount() + "");
        savedInstanceState.putString("C3_HEIGHT", c3.getChildCount() + "");
    }

    /**
     * Opens the RulesActivity that explains what you're supposed to do in this game.
     * @param v the RULES button
     */
    public void getRules(View v) {
        Intent i = new Intent(this, RulesActivity.class);
        i.putExtra("SCORE_TEXT", score.getText());
        startActivity(i);
    }
}