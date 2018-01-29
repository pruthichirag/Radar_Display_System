/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulatorinputapp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
import static javafx.application.Application.launch;

/**
 *
 * @author ANIL KUSHWAH
 */
public class SimulatorInputApp extends Application {
    private DatagramSocket socket;
    private static final int MAX_TARGET=20;
    private int tgcount;
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Simulator Data Input");
        Text statustext=new Text();
        statustext.setFill(Color.RED);
        GridPane grid1 = new GridPane();
        grid1.setAlignment(Pos.CENTER);
        grid1.setHgap(10);
        grid1.setVgap(10);
        grid1.setPadding(new Insets(25, 25, 25, 25));
        Text scenetitle = new Text("Simulator Data Input");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid1.add(scenetitle, 0, 0, 2, 1);
        Label tcount=new Label("Target Count");
        TextField tgtCountField=new TextField();
        grid1.add(tcount,0,1);
        grid1.add(tgtCountField, 1, 1);
        Button tgtbtn = new Button("Submit");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_CENTER);
        hbBtn.getChildren().add(tgtbtn);
        grid1.add(hbBtn, 2, 1);      
        
        GridPane grid2=new GridPane();
        grid2.setAlignment(Pos.CENTER);
        grid2.setHgap(10);
        grid2.setVgap(10);
        grid2.setPadding(new Insets(25, 25, 25, 25));
        
        ScrollPane spane1=new ScrollPane(grid2);
        
        Scene scene2 = new Scene(spane1 ,600,600);
        
        Scene scene = new Scene(grid1, 400, 200);
        
        tgtbtn.setOnAction(new EventHandler<ActionEvent>() {
 
    @Override
    public void handle(ActionEvent e) {
        
        String tcoun=tgtCountField.getText();
        
        Integer tg=0;
        try{
         tg=Integer.valueOf(tcoun);
         if(tg>MAX_TARGET)
         { Alert exceed=new Alert(AlertType.ERROR,"Re-Enter the Target Count",ButtonType.OK);
           exceed.setHeaderText("Exceeded Target Count");
           exceed.showAndWait();
           return;
            }
        }
        catch(Exception num1)
        {   Alert a1=new Alert(Alert.AlertType.ERROR,"Invalid Value",ButtonType.OK);
            a1.showAndWait();
            return;
           }
        
        tgcount=tg;
      
        primaryStage.setScene(scene2);
        primaryStage.setResizable(true);
        grid2.add(scenetitle, 0, 0, 2, 1);
        Label tgt=new  Label("Target ");
        Label rng=new  Label("Range  ");
        Label brng=new Label("Bearing");
        Label target[]=new Label[tgcount];
        TextField range[]=new TextField[tgcount];
        TextField bearing[]=new TextField[tgcount];
         List<CheckBox> movable = new ArrayList<CheckBox>();
         for (int j = 0; j < tgcount; j++) {
         CheckBox checkbox = new CheckBox("Movable");
         movable.add(checkbox);
        }
        grid2.add(tgt, 0, 2);
        grid2.add(rng, 1, 2);
        grid2.add(brng, 2, 2);
        int i;        
        for(i=0;i<tgcount;i++)
        {
         target[i]=new Label("Target "+(i+1));
         range[i]=new TextField();
         bearing[i]=new TextField();
         grid2.add(target[i], 0, i+3);
         grid2.add(range[i], 1, i+3);
         grid2.add(bearing[i], 2, i+3);
         grid2.add(movable.get(i), 3, i+3);
        }
        Button subbtn = new Button("Submit");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_CENTER);
        hbBtn.getChildren().add(subbtn);
        grid2.add(hbBtn, 1, i+3);
        final Text actiontarget = new Text();
        grid2.add(actiontarget, 2, i+4);

        subbtn.setOnAction(new EventHandler<ActionEvent>() {
 
    @Override
    public void handle(ActionEvent e) {
        actiontarget.setFill(Color.CHOCOLATE);
        Integer b[]=new Integer[tgcount];
        Integer r[]=new Integer[tgcount];
        Float fb,fr;
        Integer i=0;
        byte buff[];
        
        try{
             socket=new DatagramSocket();
            }
        catch(SocketException ex)
           {
            System.exit(1);
             }
        try{ 
            
            buff=Integer.toString(tgcount).getBytes();
            DatagramPacket packetSend=
               new DatagramPacket(buff, buff.length,
               InetAddress.getLocalHost(), 45678);
            socket.send(packetSend);
            for(i=0;i<tgcount;i++)
            { 
            fr=Float.valueOf(range[i].getText());    
            fb=Float.valueOf(bearing[i].getText()); 
            if(fb>360||fr>250)
            { 
              statustext.setText("Values Out of Range");
              return;
            }    
            b[i] = Math.round(100*fb);
            r[i] = Math.round(100*fr);
            
            }
            try{
            for(i=0;i<tgcount;i++)
            {
            buff=r[i].toString().getBytes();
            packetSend.setData(buff);
            packetSend.setLength(buff.length);
            socket.send(packetSend);
            buff=b[i].toString().getBytes();
            packetSend.setData(buff);
            packetSend.setLength(buff.length);
            socket.send(packetSend);
            buff=(movable.get(i).isSelected()?"1":"0").getBytes();
            packetSend.setData(buff);
            packetSend.setLength(buff.length);
            socket.send(packetSend);
            } 
            Alert success=new Alert(Alert.AlertType.INFORMATION,"Successfully Sent the Data",ButtonType.OK);
            success.showAndWait();
            }
            catch(IOException ex)
            {
              ex.getMessage();
            }
             }
        catch(IOException | NumberFormatException e1)
        {   Alert fail=new Alert(AlertType.ERROR,"Invalid Value Entered",ButtonType.OK);
            fail.showAndWait();
            }
    }
                });
        
        Button resbtn = new Button("Reset");
        HBox hb1Btn = new HBox(10);
        hb1Btn.setAlignment(Pos.BOTTOM_CENTER);
        hb1Btn.getChildren().add(resbtn);
        grid2.add(hb1Btn, 2, i+3);
        grid2.add(statustext, 2, i+4);
        resbtn.setOnAction(new EventHandler<ActionEvent>() {
 
    @Override
    public void handle(ActionEvent e) {
        for(int i=0;i<tgcount;i++)
        { range[i].setText(null);
          bearing[i].setText(null); 
          movable.get(i).setSelected(false);
        }
    }
                });
        
    }
        });
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    public int isInt(String s)
    {   Integer a;
        try{
            a= Integer.valueOf(s);    
            }
            catch(Exception e)
            {   return -1;
                }
        return a;
        }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
