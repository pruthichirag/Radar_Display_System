/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package radaroutput;

import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 *
 * @author ANIL KUSHWAH
 */
public class Target {
    double range;
    double bearing;
    String tgtid;
    boolean movable=false;
    Group g=new Group();
    Circle c=new Circle(5);
    Text t=new Text();
    private static final String SHIP_URL = "radaroutput/graphics/warship.jpg";
    Image ship=new Image(SHIP_URL);
    Target(double rng,double bng)
    {
        range=rng;
        bearing=bng;
        
    }
    
    Target(double rng,double bng,String id,boolean mv)
    {
        range=rng;
        bearing=bng;
        tgtid=id;
        movable=mv;
    }
    void initialise(Circle basedial)
    {
      int x,y;
          x=(int) ((int) getrange()*Math.sin((Math.PI*getbearing())/180));
          y=(int) ((int) -getrange()*Math.cos((Math.PI*getbearing())/180));
          c.relocate(x+basedial.getRadius()-5, y+basedial.getRadius()-5);
          c.setFill(new ImagePattern(ship, 0, 0, 1, 1, true));
          c.setEffect(new DropShadow(7,2.0f,2.0f,Color.BLACK));
          t.relocate(c.getLayoutX()-10,c.getLayoutY()+10);
          t.setText(gettgtid());
          t.setFont(Font.font("Tahoma", FontWeight.BOLD, 8));
          t.setFill(Color.AZURE);
          g.getChildren().addAll(c,t);
          
    }        
    void setrange(double val)
    {
        range=val;
    }
    
    void setbearing(double val)
    {
        bearing=val;
    }        
    
    void settgtid(String id)
    {
        tgtid=id;
    }
    double getrange()
    {
        return range;
    }
    
    double getbearing()
    {
        return bearing;
    }        
    
     String gettgtid()
    {
        return tgtid;
    }
}
