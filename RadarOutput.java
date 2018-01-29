/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package radaroutput;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.text.DecimalFormat;
import javafx.scene.text.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 *
 * @author ANIL KUSHWAH
 */
public class RadarOutput extends Application {
    Circle basedial=new Circle(250,Color.SEAGREEN);
    Text statustext=new Text("Welcome");
    Rectangle selection=new Rectangle(basedial.getRadius()-10,basedial.getRadius()-10,20,20);
    Target CurrentTarget=new Target(0,0);
    List<Target> targets=new ArrayList<>();
    Random random = new Random(System.currentTimeMillis());
    private DatagramSocket socket;
    Group MainGroup=new Group();
    int TotalTargets=0;
    @Override
    public void start(Stage primaryStage) {
        //Set the Window Title
        primaryStage.setTitle("Radar Output");
        //Fuction call to create socket and accept data
        getData();

        System.out.print(TotalTargets);
        //Radar Pane
        FlowPane RadarPane=new FlowPane(MainGroup);
        RadarPane.getStyleClass().add("grid2");
        RadarPane.setCursor(Cursor.CROSSHAIR);
        
        GridPane targetTable=new GridPane();
        ScrollPane side=new ScrollPane(targetTable);
        GridPane MainGrid=new GridPane();
        GridPane SidePane=new GridPane();
        MainGrid.add(RadarPane, 0, 1);
        MainGrid.add(SidePane, 1, 1);
        MainGrid.autosize();
        Scene scene = new Scene(MainGrid, 705, 520,Color.AQUA);
        scene.getStylesheets().add(RadarOutput.class.getResource("layoutstyles.css").toExternalForm());
        
        addShipData();
        
        

        //scene.getStylesheets().add("radaroutput/layoutstyles.css");
        
        SidePane.getStyleClass().add("grid");
        
        Text TgtSelected=new Text("None Selected  ");
        TgtSelected.getStyleClass().add("header");
        TgtSelected.setFont(Font.font("Tahoma", FontWeight.BOLD, 10));
        
        Button rng=new Button();
        rng.setText("--");
        rng.getStyleClass().add("btns");
        
        Button bng=new Button();
        bng.setText("--");
        bng.getStyleClass().add("btns");
        
        Text CurrentCursor=new Text("Current Cursor");
        CurrentCursor.getStyleClass().add("header");
        
        Button crng=new Button("");
        crng.getStyleClass().add("btns");
        
        Button cbng=new Button("");
        cbng.getStyleClass().add("btns");

        SidePane.add(CurrentCursor, 0, 0, 3, 1);
        SidePane.add(new Text("Bearing"), 1, 1);
        SidePane.add(new Text("Range"), 0, 1);
        SidePane.add(crng, 0, 2);
        SidePane.add(cbng, 1, 2);
        SidePane.add(TgtSelected, 0, 3, 4, 1);
        SidePane.add(new Text("Range"), 0, 4);
        SidePane.add(rng,0,5);
        SidePane.add(new Text("Bearing"), 1, 4);
        SidePane.add(bng,1,5);
        SidePane.add(side, 0, 6, 4, 10);
        
        side.getStyleClass().add("spane");
        
        targetTable.getStyleClass().add("basegrid");
        targetTable.add(new Text("Target"), 0, 0);
        targetTable.add(new Text("Range"), 1, 0);
        targetTable.add(new Text("Bearing"), 2, 0);
        for(int k=0;k<targets.size();k++)
        {
        targetTable.add(new Text(targets.get(k).gettgtid()), 0, k+1);
        targetTable.add(new Text(new DecimalFormat("##.##").format(targets.get(k).getrange())), 1, k+1);
        targetTable.add(new Text(new DecimalFormat("##.##").format(targets.get(k).getbearing())), 2, k+1);
            
        }
        


        createdial();
        
        statustext.setX(10);
        statustext.setY(510);
        statustext.setFill(Color.FLORALWHITE);
        MainGroup.getChildren().add(statustext);
        
        Circle OwnShip=new Circle(10);
        OwnShip.relocate(basedial.getRadius()-OwnShip.getRadius(), basedial.getRadius()-OwnShip.getRadius());
        MainGroup.getChildren().add(OwnShip);
        LinearGradient gg2=new LinearGradient(0,0,0.5,0.5, false, CycleMethod.REFLECT,
            new Stop[]{
            new Stop(0,Color.BLUE),
            new Stop(0.2, Color.DARKMAGENTA),
            new Stop(0.4,Color.YELLOWGREEN),
            new Stop(0.6,Color.DARKRED),
            new Stop(0.8,Color.GOLD)});
        OwnShip.setStroke(Color.CORNFLOWERBLUE);
        OwnShip.setFill(gg2);
        OwnShip.setEffect(new DropShadow(8,2.0f,2.0f,Color.BLUE));
        
        Group marker=createfocus();
        MainGroup.getChildren().add(marker);
        marker.setOpacity(0);
        
        Random GRand = new Random(System.currentTimeMillis());
        

        for(Target tt:targets)
        {
          
          tt.initialise(basedial);
           
          MainGroup.getChildren().add(tt.g);
          
          
          tt.c.setOnMouseClicked((MouseEvent me) -> {
              statustext.setText("Target Number "+tt.gettgtid()+" is Selected");
              selection.relocate(tt.c.getLayoutX() - 10, tt.c.getLayoutY() - 10);
              bng.setText(Integer.toString((int) tt.getbearing()));
              rng.setText(Integer.toString((int) tt.getrange()));
              TgtSelected.setText("Selected Target "+tt.gettgtid());
              
              marker.setOpacity(1);
              marker.relocate(tt.c.getLayoutX()-12.5,tt.c.getLayoutY()-12.5);
              if(!tt.g.getChildren().contains(marker)&&!tt.g.getChildren().contains(selection))
              {   
                  tt.g.getChildren().add(marker);
                  tt.g.getChildren().add(selection);
               }
              
              System.out.println(tt.c.getLayoutX());
              System.out.println(tt.c.getLayoutY());
              System.out.println(me.getSceneX());
              System.out.println(me.getSceneY());
              System.out.println("-------------------");
          });
        
        
        
        }
        
        Timeline timeline = new Timeline();
        double limx=0,limy=0;
        for (Target tt:targets) {
        if(!tt.movable)
            continue;
        do
        {    
        limx=Math.pow(-1, GRand.nextInt(2))*GRand.nextInt(500);
        limy=Math.pow(-1, GRand.nextInt(2))*GRand.nextInt(500);
        }while(!basedial.contains(limx+tt.c.getLayoutX()-basedial.getRadius(), limy+tt.c.getLayoutY()-basedial.getRadius()));
        
        timeline.getKeyFrames().addAll(new KeyFrame(Duration.ZERO, // set start position at 0
            new KeyValue(tt.g.translateXProperty(), 0),
            new KeyValue(tt.g.translateYProperty(), 0)        
        ),
            new KeyFrame(Duration.minutes(0.2), // set end position at 40s
            new KeyValue(tt.g.translateXProperty(), limx),
            new KeyValue(tt.g.translateYProperty(), limy)
        )
        );
        }

timeline.play();
        
       
        basedial.setOnMouseClicked((MouseEvent t) -> {
            selection.relocate(basedial.getRadius()-10, basedial.getRadius()-10);
            MainGroup.getChildren().add(selection);
            marker.setOpacity(0);
            rng.setText("--");
            bng.setText("--");
            TgtSelected.setText("None Selected  ");
            
        });
        basedial.setOnMouseMoved(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent t){
                Double xv=t.getX();
                Double yv=t.getY();
                CurrentTarget.setbearing(Math.atan(yv/xv));
                CurrentTarget.setbearing((360*CurrentTarget.getbearing())/(2*Math.PI)+90+(xv<0?180:0));
                CurrentTarget.setrange(Math.sqrt(xv*xv+yv*yv));
                statustext.setText("Current Cursor : "+CurrentTarget.getrange()+" Meters and "+CurrentTarget.getbearing()+" Degrees");
                crng.setText(Integer.toString((int)CurrentTarget.getrange()));
                cbng.setText(Integer.toString((int)CurrentTarget.getbearing()));
                OwnShip.setOnMouseMoved(this);
                }
        });
        
        OwnShip.setOnMouseMoved(basedial.getOnMouseMoved()::handle);
        
        OwnShip.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent t){
                 statustext.setText("Current Selected Object : Base Ship");
                 OwnShip.setOnMouseMoved(this);
                 
          }});
        
        primaryStage.setScene(scene);
        //primaryStage.setResizable(false);
        MainGrid.autosize();

        primaryStage.setOnCloseRequest((WindowEvent we) -> {
            System.out.println(primaryStage.getHeight());
            System.out.println(primaryStage.getWidth());
            System.out.println(scene.getHeight());
            System.out.println(scene.getWidth());
        });
        primaryStage.show();
        }
    
    /**
     *
     */
    public void addShipData()
    {   String a[]={"E","W","N","S"};
        VBox ShipDetails = new VBox();
        Button lat=new Button(Integer.toString(random.nextInt(100)));
        Button lon=new Button(Integer.toString(random.nextInt(100)));
        Button course=new Button(a[random.nextInt(4)]);
        Button speed=new Button(Integer.toString(random.nextInt(100)));
        lat.getStyleClass().add("btns");
        lon.getStyleClass().add("btns");
        course.getStyleClass().add("btns");
        speed.getStyleClass().add("btns");
        lat.setOnMouseEntered((MouseEvent me) -> {
            Text t=new Text(lat.getWidth(),lat.getHeight(),"Latitude");
            MainGroup.getChildren().add(t);
        });
        lat.setOnMouseExited((MouseEvent me) -> {
            MainGroup.getChildren().remove(MainGroup.getChildren().toArray().length-1);
        });
        lon.setOnMouseEntered((MouseEvent me) -> {
            Text t=new Text(lon.getWidth(),lon.getHeight()*2,"Longitude");
            MainGroup.getChildren().add(t);
        });
        lon.setOnMouseExited((MouseEvent me) -> {
            MainGroup.getChildren().remove(MainGroup.getChildren().toArray().length-1);
        });
        course.setOnMouseEntered((MouseEvent me) -> {
            Text t=new Text(course.getWidth(),course.getHeight()*3,"Course");
            MainGroup.getChildren().add(t);
        });
        course.setOnMouseExited((MouseEvent me) -> {
            MainGroup.getChildren().remove(MainGroup.getChildren().toArray().length-1);
        });
        speed.setOnMouseEntered((MouseEvent me) -> {
            Text t=new Text(speed.getWidth(),speed.getHeight()*4,"Speed");
            MainGroup.getChildren().add(t);
        });
        speed.setOnMouseExited((MouseEvent me) -> {
            MainGroup.getChildren().remove(MainGroup.getChildren().toArray().length-1);
        });
        lat.setPrefSize(40, 10);
        course.setPrefSize(40, 10);
        speed.setPrefSize(40, 10);
        lon.setPrefSize(40, 10);
        ShipDetails.setAlignment(Pos.BOTTOM_CENTER);
        ShipDetails.getChildren().add(lat);
        ShipDetails.getChildren().add(lon);
        ShipDetails.getChildren().add(course);
        ShipDetails.getChildren().add(speed);
        ShipDetails.getStyleClass().add(".hbox");
        MainGroup.getChildren().add(ShipDetails);
    }
    
    public void createdial()
    {
        DropShadow dialShadow = new DropShadow(7,2.0f,2.0f,Color.BLACK);
        String GRAPH_URL = "radaroutput/graphics/blue_graph.jpg";        
        Image graph=new Image(GRAPH_URL);
        
        
        basedial.relocate(basedial.getCenterX(), basedial.getCenterY());
        basedial.setFill(new ImagePattern(graph, 0, 0, 1, 1, true));
        basedial.setSmooth(true);
        basedial.setEffect(dialShadow);      
        MainGroup.getChildren().add(basedial);
        selection.setFill(Color.rgb(0,0,0,0.2));
        MainGroup.getChildren().add(selection);
        
        for(int j=0;j<360;j+=30)
        { int x1=(int) (250*Math.sin((2*Math.PI*j)/360));
          int y1=(int) (250*Math.cos((2*Math.PI*j)/360));
          int x2=(int) ((j%10==0?230:240)*Math.sin((2*Math.PI*j)/360));
          int y2=(int) ((j%10==0?230:240)*Math.cos((2*Math.PI*j)/360));
          int y3=(int) (-(j%10==0?220:230)*Math.cos((2*Math.PI*j)/360));
          int x3=(int) ((j%10==0?220:230)*Math.sin((2*Math.PI*j)/360));
          Text t=new Text(x3,y3,String.valueOf(j));
          t.setFont(Font.font(Font.getFontNames().get(4), 15));
          Line l1=new Line(x1,y1,x2,y2);
          l1.setStrokeWidth(2);
          l1.setTranslateX(basedial.getRadius());
          l1.setTranslateY(basedial.getRadius());
          t.setTranslateX(basedial.getRadius()-5);
          t.setTranslateY(basedial.getRadius()+5);
          l1.setStroke(Color.WHITE);
          t.setFill(Color.WHITE);
          MainGroup.getChildren().add(l1);
          MainGroup.getChildren().add(t);
         }
    }
        
    public Group createfocus()
    {   Group maing=new Group();
        Group arrow1=new Group();
        Group arrow2=new Group();
        Group arrow3=new Group();
        Group arrow4=new Group();
        arrow1.getChildren().addAll(new Line(0,0,0,10),new Line(0,0,5,5),new Line(0,0,-5,5));
        arrow2.getChildren().addAll(new Line(0,0,0,10),new Line(0,0,5,5),new Line(0,0,-5,5));
        arrow3.getChildren().addAll(new Line(0,0,0,10),new Line(0,0,5,5),new Line(0,0,-5,5));
        arrow4.getChildren().addAll(new Line(0,0,0,10),new Line(0,0,5,5),new Line(0,0,-5,5));
        arrow1.relocate(arrow1.getLayoutX()-7.5,arrow1.getLayoutY()-7.5);
        arrow1.setRotate(135.0);
        arrow2.relocate(arrow2.getLayoutX()+7.5,arrow2.getLayoutY()-7.5);
        arrow2.setRotate(225);
        arrow3.relocate(arrow3.getLayoutX()+7.5,arrow3.getLayoutY()+7.5);
        arrow3.setRotate(315);
        arrow4.relocate(arrow4.getLayoutX()-7.5,arrow4.getLayoutY()+7.5);
        arrow4.setRotate(45);
        maing.getChildren().addAll(arrow1,arrow2,arrow3,arrow4);
        maing.relocate(10,10 );
        maing.getStyleClass().add("marker");
        return maing;
    }

    public int getTgtData(byte[] buffer,DatagramPacket packet) throws IOException
    {
    String temp="";
    Arrays.fill(buffer, (byte) 0);
    socket.receive(packet);
    buffer=packet.getData();
    for(int j=0;j<buffer.length&&buffer[j]!=0;j++)
    {
     temp+=((int) buffer[j]-48);
       }
    int val=Integer.valueOf(temp);
    return val;
    }
    
    public void initialScene(Stage primaryStage)
    {   GridPane bp=new GridPane();
        Scene scene1=new Scene(bp,520,700);
        bp.add(new Text("asdasdsad"), 0 , 2);
        primaryStage.setScene(scene1);
        primaryStage.show();
    }
    
    public void getData()
    {
    try {
         socket = new DatagramSocket(45678);

      } catch (SocketException ex) {
         Logger.getLogger(RadarOutput.class.getName()).log(Level.SEVERE, null, ex);
         System.exit(1);
      }
        
        while(true)
        {   int rn,bn,tgc;
            float rn1,bn1;
            try 
            {   
                byte buffer[] = new byte[128];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                tgc=getTgtData(buffer,packet);
                boolean mv=false;
                for(int i =0;i<tgc;i++)
                {
                rn=getTgtData(buffer,packet);
                rn1=(float)rn/100;
                System.out.print(rn);
                bn=getTgtData(buffer,packet);
                bn1=(float)bn/100;
                System.out.print(rn);
                mv=getTgtData(buffer,packet)==1;
                Target tt=new Target(rn1,bn1,"TGT "+(i+1),mv);
                targets.add(tt);
                }
                Alert recv=new Alert(AlertType.CONFIRMATION,"RECEIVED DATA",ButtonType.NEXT);
                recv.showAndWait();
                break;    
                } 
            catch (IOException ex) 
            {
                Logger.getLogger(RadarOutput.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
            }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
    