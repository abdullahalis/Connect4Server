
import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ServerGUI extends Application{

    Text portText;
    TextField portField;
    Button submitPort;
    HashMap<String, Scene> sceneMap;
    VBox portSelect;
    Scene startScene;
    BorderPane startPane;
    Server serverConnection;

    int portNumber;
    CFourInfo info;

    ListView<String> listItems, listItems2;


    public static void main(String[] args) {
        // TODO Auto-generated method stub
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO Auto-generated method stub
        primaryStage.setTitle("Choose a Port");
        this.portText = new Text("Choose a port number");
        portText.setStyle("-fx-font-size: 40px;\n" +
                "    -fx-font-family: 'Didact Gothic';\n" +
                "    -fx-text-align: center;");

        this.portField = new TextField();
        portField.setStyle("-fx-max-width: 100px;\n" +
                "    -fx-text-alignment: center;");

        this.submitPort = new Button("Submit");
        submitPort.setStyle("-fx-font-size: 20px;\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-background-color: DARKCYAN;\n" +
                "    -fx-font-family: 'Didact Gothic';");

        this.portSelect = new VBox(portText, portField, submitPort);
        portSelect.setAlignment(Pos.CENTER);
        portSelect.setSpacing(10);

        this.submitPort.setOnAction(e->{
            this.portNumber = Integer.parseInt(portField.getText());
            primaryStage.setScene(sceneMap.get("server"));
            primaryStage.setTitle("This is the Server");
        });

        startPane = new BorderPane();
        startPane.setPadding(new Insets(70));
        startPane.setCenter(portSelect);
        startPane.setStyle("-fx-background-color: DARKGRAY");

        startScene = new Scene(startPane, 800,800);

        listItems = new ListView<String>();
        listItems2 = new ListView<String>();
        sceneMap = new HashMap<String, Scene>();

        sceneMap.put("server",  createServerGui());
        //sceneMap.put("client",  createClientGui());

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });




        primaryStage.setScene(startScene);
        primaryStage.show();


    }

    public Scene createServerGui() {

        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(70));
        Button startServer = new Button("Start Server");
        startServer.setStyle("-fx-font-size: 20px;\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-background-color: DARKCYAN;\n" +
                "    -fx-font-family: 'Didact Gothic';");

        startServer.setOnAction(e->{
            serverConnection = new Server(data -> {
                Platform.runLater(()-> {
                    if (data instanceof CFourInfo) {
                        info = ((CFourInfo) data);
                    }
                    else {
                        listItems.getItems().add(data.toString());
                    }

                });
            }, portNumber);
        startServer.setDisable(true);
        });
        VBox serverBox = new VBox(startServer, listItems);
        serverBox.setAlignment(Pos.CENTER);
        serverBox.setSpacing(10);
        pane.setStyle("-fx-background-color: DARKGRAY");
        pane.setCenter(serverBox);

        return new Scene(pane, 500, 400);
    }

//    public Scene createClientGui() {
//
//        clientBox = new VBox(10, c1,b1,listItems2);
//        clientBox.setStyle("-fx-background-color: blue");
//        return new Scene(clientBox, 400, 300);
//
//    }

}
