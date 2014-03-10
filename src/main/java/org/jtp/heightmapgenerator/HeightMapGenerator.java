package org.jtp.heightmapgenerator;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class HeightMapGenerator extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        MainWindow2 mw = new MainWindow2();
        
        Scene scene = new Scene(mw);
        scene.getStylesheets().add("/styles/viewer.css");
        
        stage.setTitle("3D Heightmap Generator");
        stage.setScene(scene);        
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
