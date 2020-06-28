package org.li.guesscolor.GameSys;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

public class Game {
    static int UNKNOWN=-1;
    static int BLACK=0;
    static int BLUE=1;
    static int GREEN=2;
    static int WHITE=3;
    static int RED=4;
    static int YELLOW=5;
    public Game(){
        this(false);
    }
    public Game(boolean canSame){
        this.canSame=canSame;
        selectmat=new int[8][4];
        answer=new int[4];
        for(int i=0;i<8;i++) {
            for (int j = 0; j < 4; j++) {
                selectmat[i][j] = -1;
            }
        }
        Random random=new Random(System.currentTimeMillis());
        for(int i=0;i<4;i++){
            if(canSame){
                answer[i]=random.nextInt(6);
            }else {
                int temp=-1;
                boolean flag=true;
                while(flag){
                    flag=false;
                    temp=random.nextInt(6);
                    for(int j=i-1;j>=0;j--){
                        if(temp==answer[j]){
                            flag=true;break;
                        }
                    }
                }
                answer[i]=temp;
            }
        }
        for(int i=0;i<4;i++) Log.d("test", "Game: "+answer[i]);
    }

    /**
     * 返回true的时候这一列已经填满
     */
    public boolean select(int list,int color,int pos){
        selectmat[list][pos]=color;
        boolean flag=true;
        for(int i=0;i<4;i++){
            if(selectmat[list][i]==-1){flag=false;break;}
        }
        return flag;
    }

    /**
     * 返回一个数组，第一个数字是全对数量，第二个数字是颜色正确数量
     */
    public int[] judge(int list){
        boolean flag=true;
        int color=0,correct=0;
        int[] res=new int[2];
        int[] temp=new int[4];
        for(int i=0;i<4;i++){
            if(answer[i]==selectmat[list][i]){
                correct++;
                temp[i]=-1;
                selectmat[list][i]=-1;
            }else {
                temp[i]=answer[i];
            }
        }
        for(int i=0;i<4;i++) {
            if(temp[i]!=-1) {
                for (int j = 0; j < 4; j++) {
                    if(selectmat[list][j]!=-1&&temp[i]==selectmat[list][j]){
                        selectmat[list][j]=-1;
                        color++;
                        break;
                    }
                }
            }
        }
        res[0]=correct;
        res[1]=color;
        Log.d("test", "judge: "+res[0]);
        Log.d("test", "judge: "+res[1]);
        return res;
    }


    private boolean canSame;
    private int[] answer;
    private int[][] selectmat;
}
