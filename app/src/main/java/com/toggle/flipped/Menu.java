package com.toggle.flipped;

import android.util.Log;

import com.toggle.katana2d.Boundary;
import com.toggle.katana2d.Component;
import com.toggle.katana2d.Font;
import com.toggle.katana2d.GLRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ankit on 12/6/2015.
 */
public class Menu implements Component {
    public static final int HIDDEN = 0;
    public static final int VISIBLE = 0;

    public class MenuItem{

        public String text;
        public int state;
        public Boundary boundary;
        Font font;
        MenuItem(String text, Font font){
            this.text = text;
            state = HIDDEN;
            this.font = font;
        }
        public void hide(){ state = HIDDEN; }
        public void show(){ state = VISIBLE; }
        public boolean hitTest(float x, float y){
            return boundary.hitTest(x, y);
        }
        public void setPosition(float x, float y){
            this.boundary = font.calculateBoundary(this.text, x, y, 0, 1, 1);
        }
        public void draw(){
            font.draw(this.text, boundary.left, boundary.top, 0, 1, 1);
        }
    }

    public class MenuTemplate{
        List<MenuItem> items = new ArrayList<>();
        public void addItem(String itemName, Font font) {
            items.add(new MenuItem(itemName, font));
        }
        public void setup(){
            float x = 20;
            float verticalSpace = 20.0f;
            float y = items.get(0).font.getRenderer().devHeight/2;
            //y -= verticalSpace;

            for(int i = 0; i < items.size(); i++){
                items.get(i).setPosition(x, y);
                y = items.get(i).boundary.bottom + verticalSpace;
                //Log.d(items.get(i).text, items.get(i).boundary.top+"");
            }
        }
    }

    List<MenuTemplate> menuTemplates = new ArrayList<>();
    public int current;


    // TODO: move these function to the system

    public void setup(Font font){
        MenuTemplate mainMenu = new MenuTemplate();
        mainMenu.addItem("Start Game", font);
        mainMenu.addItem("Settings", font);
        mainMenu.addItem("Exit", font);
        mainMenu.setup();
        this.menuTemplates.add(mainMenu);
        this.current = 0;
    }

    public int hitTest(float x, float y) {
        if(current == -1) return -1;
        int i = 0;
        for(MenuItem menuItem: menuTemplates.get(current).items){
            if( menuItem.hitTest(x, y)){
                return i;
            }
            ++i;
        }
        return -1;
    }

    public void draw(){
        for(MenuItem menuItem: menuTemplates.get(current).items) {
            menuItem.draw();
        }
    }
}
