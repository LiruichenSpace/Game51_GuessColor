package org.li.guesscolor;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import org.li.guesscolor.GameSys.Game;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    static Game game;
    static String TAG="GameActivity";
    private ArrayList<ArrayList<Button>> resultMat;
    private ArrayList<ArrayList<Button>> selectMat;
    private ArrayList<Button> submitList;
    private ArrayList<Button> colorList;
    private ArrayList<ConstraintLayout> resLayouts;
    static int currColor=-1;
    static void setColor(Button button,int num){
        switch(num){
            case 0:
                button.setBackgroundResource(R.drawable.black);
                break;
            case 1:
                button.setBackgroundResource(R.drawable.blue);
                break;
            case 2:
                button.setBackgroundResource(R.drawable.green);
                break;
            case 3:
                button.setBackgroundResource(R.drawable.grey);
                break;
            case 4:
                button.setBackgroundResource(R.drawable.red);
                break;
            case 5:
                button.setBackgroundResource(R.drawable.yellow);
                break;
        }
    }
    static class UpdateRun implements Runnable{
        UpdateRun(Button b,boolean flag){
            button=b;
            this.flag=flag;
        }
        Button button;
        boolean flag;
        @Override
        public void run() {
            if(flag)button.setBackgroundResource(R.drawable.circle_shape_red);
            else button.setBackgroundResource(R.drawable.circle_shape_white);
        }
    }
    static class SelectListener implements View.OnClickListener{
        SelectListener(int list,int pos,ArrayList<ArrayList<Button>> selectMat
                ,ArrayList<Button> submitList){
            this.targetList=list;
            this.targetPos=pos;
            this.selects=selectMat;
            this.submits=submitList;
        }
        private int targetList;
        private int targetPos;
        private ArrayList<ArrayList<Button>> selects;
        private ArrayList<Button> submits;
        @Override
        public void onClick(View view) {
            if(currColor!=-1){
                setColor(selects.get(targetList).get(targetPos),currColor);
                boolean flag=game.select(targetList,currColor,targetPos);
                if(flag){
                    submits.get(targetList).setClickable(true);
                    submits.get(targetList).setBackgroundResource(R.drawable.ic_ok);
                }
            }
        }
    }
    static class ColorListener implements View.OnClickListener{
        ColorListener(int pos,ArrayList<Button> colorList,Context context){
            this.pos=pos;
            this.context=context;
            colors=colorList;
        }
        private int pos;
        private ArrayList<Button> colors;
        private Context context;
        @Override
        public void onClick(View view) {
            if(pos!=currColor){

                if(currColor!=-1){
                    ViewGroup.LayoutParams params= (ViewGroup.LayoutParams) colors.get(currColor).getLayoutParams();
                    params.height=Dp2Px(context,50);
                    params.width=Dp2Px(context,50);
                    colors.get(currColor).setLayoutParams(params);
                }
                ViewGroup.LayoutParams params= (ViewGroup.LayoutParams) colors.get(pos).getLayoutParams();
                params.height=Dp2Px(context,60);
                params.width=Dp2Px(context,60);
                colors.get(pos).setLayoutParams(params);
                currColor=pos;
            }
        }
    }
    static class SubmitListener implements View.OnClickListener{
        SubmitListener(int pos,
                       ArrayList<ArrayList<Button>> resultMat,
                       ArrayList<ArrayList<Button>> selectMat,
                       ConstraintLayout layout,
                       Button submit){
            this.pos=pos;
            this.results=resultMat;
            this.selects=selectMat;
            this.self=submit;
            this.layout=layout;
        }
        private ArrayList<ArrayList<Button>> results;
        private ArrayList<ArrayList<Button>> selects;
        private ConstraintLayout layout;
        private Button self;
        private int pos;
        private int p;
        @Override
        public void onClick(View view) {
            if(view.isClickable()) {
                //隐藏自身
                self.setVisibility(View.GONE);
                //显示结果框
                layout.setVisibility(View.VISIBLE);
                //禁止重复点击
                for (int i = 0; i < 4; i++) {
                    selects.get(pos).get(i).setClickable(false);
                }
                //判定结果
                int[] temp = game.judge(pos);
                p = 0;
                //更新结果
                Handler handler = new Handler();
                while (temp[0] > 0) {
                    handler.postDelayed(new UpdateRun(results.get(pos).get(p), true), 200 * (p + 1));
                    p++;
                    temp[0]--;
                }
                while (temp[1] > 0) {
                    handler.postDelayed(new UpdateRun(results.get(pos).get(p), false), 200 * (p + 1));
                    p++;
                    temp[1]--;
                }
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS|WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //初始化颜色
        currColor=-1;
        //下方颜色按钮
        initColorB();
        //提交按钮
        initSubmit();
        //结果矩阵
        initRes();
        //选择矩阵
        initSel();
        //结果区块
        initLayouts();
        //绑定选择按钮
        for(int i=0;i<8;i++){
            for(int j=0;j<4;j++){
                selectMat.get(i).get(j).setOnClickListener(new SelectListener(i,j,selectMat,submitList));
            }
        }
        //绑定颜色按钮
        for(int i=0;i<6;i++){
            colorList.get(i).setOnClickListener(new ColorListener(i,colorList,getApplicationContext()));
        }
        //绑定提交按钮
        for(int i=0;i<8;i++){
            submitList.get(i).setOnClickListener(new SubmitListener(i,
                    resultMat,
                    selectMat,
                    resLayouts.get(i),
                    submitList.get(i)));
        }
    }
    static public int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density; //当前屏幕密度因子
        return (int) (dp * scale + 0.5f);
    }
    private void initLayouts(){
        resLayouts=new ArrayList<>();
        resLayouts.add((ConstraintLayout) findViewById(R.id.res1));
        resLayouts.add((ConstraintLayout) findViewById(R.id.res2));
        resLayouts.add((ConstraintLayout) findViewById(R.id.res3));
        resLayouts.add((ConstraintLayout) findViewById(R.id.res4));
        resLayouts.add((ConstraintLayout) findViewById(R.id.res5));
        resLayouts.add((ConstraintLayout) findViewById(R.id.res6));
        resLayouts.add((ConstraintLayout) findViewById(R.id.res7));
        resLayouts.add((ConstraintLayout) findViewById(R.id.res8));
    }
    private void initSel(){
        selectMat=new ArrayList<>();
        ArrayList<Button> res=new ArrayList<>();
        res.add((Button)findViewById(R.id.sel11));
        res.add((Button)findViewById(R.id.sel12));
        res.add((Button)findViewById(R.id.sel13));
        res.add((Button)findViewById(R.id.sel14));
        selectMat.add(res);
        res=new ArrayList<>();
        res.add((Button)findViewById(R.id.sel21));
        res.add((Button)findViewById(R.id.sel22));
        res.add((Button)findViewById(R.id.sel23));
        res.add((Button)findViewById(R.id.sel24));
        selectMat.add(res);
        res=new ArrayList<>();
        res.add((Button)findViewById(R.id.sel31));
        res.add((Button)findViewById(R.id.sel32));
        res.add((Button)findViewById(R.id.sel33));
        res.add((Button)findViewById(R.id.sel34));
        selectMat.add(res);
        res=new ArrayList<>();
        res.add((Button)findViewById(R.id.sel41));
        res.add((Button)findViewById(R.id.sel42));
        res.add((Button)findViewById(R.id.sel43));
        res.add((Button)findViewById(R.id.sel44));
        selectMat.add(res);
        res=new ArrayList<>();
        res.add((Button)findViewById(R.id.sel51));
        res.add((Button)findViewById(R.id.sel52));
        res.add((Button)findViewById(R.id.sel53));
        res.add((Button)findViewById(R.id.sel54));
        selectMat.add(res);
        res=new ArrayList<>();
        res.add((Button)findViewById(R.id.sel61));
        res.add((Button)findViewById(R.id.sel62));
        res.add((Button)findViewById(R.id.sel63));
        res.add((Button)findViewById(R.id.sel64));
        selectMat.add(res);
        res=new ArrayList<>();
        res.add((Button)findViewById(R.id.sel71));
        res.add((Button)findViewById(R.id.sel72));
        res.add((Button)findViewById(R.id.sel73));
        res.add((Button)findViewById(R.id.sel74));
        selectMat.add(res);
        res=new ArrayList<>();
        res.add((Button)findViewById(R.id.sel81));
        res.add((Button)findViewById(R.id.sel82));
        res.add((Button)findViewById(R.id.sel83));
        res.add((Button)findViewById(R.id.sel84));
        selectMat.add(res);
    }
    private void initRes(){
        resultMat=new ArrayList<>();
        ArrayList<Button> res=new ArrayList<>();
        res.add((Button)findViewById(R.id.res11));
        res.add((Button)findViewById(R.id.res12));
        res.add((Button)findViewById(R.id.res13));
        res.add((Button)findViewById(R.id.res14));
        resultMat.add(res);
        res=new ArrayList<>();
        res.add((Button)findViewById(R.id.res21));
        res.add((Button)findViewById(R.id.res22));
        res.add((Button)findViewById(R.id.res23));
        res.add((Button)findViewById(R.id.res24));
        resultMat.add(res);
        res=new ArrayList<>();
        res.add((Button)findViewById(R.id.res31));
        res.add((Button)findViewById(R.id.res32));
        res.add((Button)findViewById(R.id.res33));
        res.add((Button)findViewById(R.id.res34));
        resultMat.add(res);
        res=new ArrayList<>();
        res.add((Button)findViewById(R.id.res41));
        res.add((Button)findViewById(R.id.res42));
        res.add((Button)findViewById(R.id.res43));
        res.add((Button)findViewById(R.id.res44));
        resultMat.add(res);
        res=new ArrayList<>();
        res.add((Button)findViewById(R.id.res51));
        res.add((Button)findViewById(R.id.res52));
        res.add((Button)findViewById(R.id.res53));
        res.add((Button)findViewById(R.id.res54));
        resultMat.add(res);
        res=new ArrayList<>();
        res.add((Button)findViewById(R.id.res61));
        res.add((Button)findViewById(R.id.res62));
        res.add((Button)findViewById(R.id.res63));
        res.add((Button)findViewById(R.id.res64));
        resultMat.add(res);
        res=new ArrayList<>();
        res.add((Button)findViewById(R.id.res71));
        res.add((Button)findViewById(R.id.res72));
        res.add((Button)findViewById(R.id.res73));
        res.add((Button)findViewById(R.id.res74));
        resultMat.add(res);
        res=new ArrayList<>();
        res.add((Button)findViewById(R.id.res81));
        res.add((Button)findViewById(R.id.res82));
        res.add((Button)findViewById(R.id.res83));
        res.add((Button)findViewById(R.id.res84));
        resultMat.add(res);
    }
    private void initSubmit(){
        submitList=new ArrayList<>();
        submitList.add((Button)findViewById(R.id.ok1));
        submitList.add((Button)findViewById(R.id.ok2));
        submitList.add((Button)findViewById(R.id.ok3));
        submitList.add((Button)findViewById(R.id.ok4));
        submitList.add((Button)findViewById(R.id.ok5));
        submitList.add((Button)findViewById(R.id.ok6));
        submitList.add((Button)findViewById(R.id.ok7));
        submitList.add((Button)findViewById(R.id.ok8));
        for(int i=0;i<8;i++)submitList.get(i).setClickable(false);
    }
    private void initColorB(){
        colorList=new ArrayList<>();
        colorList.add((Button) findViewById(R.id.b_black));
        colorList.add((Button) findViewById(R.id.b_blue));
        colorList.add((Button) findViewById(R.id.b_green));
        colorList.add((Button) findViewById(R.id.b_grey));
        colorList.add((Button) findViewById(R.id.b_red));
        colorList.add((Button) findViewById(R.id.b_yellow));
    }
}
