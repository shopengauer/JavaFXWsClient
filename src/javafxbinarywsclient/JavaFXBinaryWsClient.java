/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxbinarywsclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

/**
 *
 * @author Василий
 */
@ClientEndpoint
public class JavaFXBinaryWsClient extends Application {
    
  private Session session;
  private ImageView imageView;
  private static final Logger LOGGER = Logger.getLogger(JavaFXBinaryWsClient.class.getName());
  private Label label;
  private Button btn;
    @Override
    public void start(Stage primaryStage) {
       
        connectToWebSocket();
        
        btn  = new Button();
    btn.setText("Send Image!");
    btn.setPrefSize(400, 27);

    imageView = new ImageView();
    imageView.setFitHeight(400);
    imageView.setFitWidth(400);
    imageView.setPreserveRatio(true);
    imageView.setSmooth(true);

    AnchorPane root = new AnchorPane();

    AnchorPane.setTopAnchor(btn, 0.0);
    AnchorPane.setLeftAnchor(btn, 0.0);
    AnchorPane.setRightAnchor(btn, 0.0);
    AnchorPane.setTopAnchor(imageView, 27.0);
    AnchorPane.setBottomAnchor(imageView, 0.0);
    AnchorPane.setLeftAnchor(imageView, 0.0);
    AnchorPane.setRightAnchor(imageView, 0.0);

     btn.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        selectAndSendImage(primaryStage);
      }
    });
    
    label = new Label("");
    root.getChildren().add(label);
    root.getChildren().add(btn);
    
    root.getChildren().add(imageView);

    Scene scene = new Scene(root, 400, 427);

    primaryStage.setTitle("Image push!");
    primaryStage.setScene(scene);
    primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    
    private void connectToWebSocket() {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            URI uri = URI.create("ws://localhost:8080/BinaryWebSocketServer-1.0/endpoint");
            container.connectToServer(this, uri);
        } catch (DeploymentException | IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }

    private void selectAndSendImage(Stage stage){
         
      try {
          session.getUserProperties().put("Key", "Parameter");
          session.getBasicRemote().sendText("Hello");
          
      } catch (IOException ex) {
          Logger.getLogger(JavaFXBinaryWsClient.class.getName()).log(Level.SEVERE, null, ex);
      }
           
    }

    @OnOpen
    public void onOpen(Session session) {
       
        this.session = session;
        
        
    }

    @OnMessage
    public void onMessage(String input) {
        System.out.println("WebSocket message Received!");
        System.out.println(input);
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                 btn.setText(input);
             //    Image image = new Image(input);
        //imageView.setImage(image);
            }
        });
//        
    }

    @OnClose
    public void onClose() {
        connectToWebSocket();
    }

    
    
}
