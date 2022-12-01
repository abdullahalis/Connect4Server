

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;




public class MyController implements Initializable {
    private int portNumber;
    Server serverConnection;
    CFourInfo info;

    @FXML
    private VBox root;
    @FXML
    private TextField portField;
    @FXML
    private ListView<String> infoList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub

    }
    public void setPort(ActionEvent e) throws IOException {
        this.portNumber = Integer.parseInt(portField.getText());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/GameState.fxml"));
        Parent root2 = loader.load(); //load view into parent
        root2.getStylesheets().add("/styles/ServerStart");//set style
        root.getScene().setRoot(root2);//update scene graph

    }

    public void createServer(ActionEvent e) throws IOException{
        infoList = new ListView<>();
        infoList.getItems().add("hello");
        serverConnection = new Server(data -> {
            Platform.runLater(()-> {
                // instance of data
                infoList.getItems().add(data.toString());
            });
        }, portNumber);
        infoList.getItems().add("Server created");
    }
}
