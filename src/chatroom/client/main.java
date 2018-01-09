package chatroom.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class main extends Application{

    public static void main( String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primeStage) throws Exception {
        Client client = new Client();
        GuiMain guiMain = new GuiMain();
        Bridge bridge = new Bridge(client, guiMain);

        client.setBridge(bridge);

        guiMain.runGui(primeStage, bridge);
    }
}