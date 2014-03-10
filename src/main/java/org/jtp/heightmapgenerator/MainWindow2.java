/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jtp.heightmapgenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Dub-Laptop
 */
public class MainWindow2 extends AnchorPane implements Initializable {

    public MainWindow2() {
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Scene2.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(MainWindow2.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final MeshView meshView = new MeshView();
    private final PhongMaterial meshMaterial = new PhongMaterial();
    private File IMAGE_FILE;
    private Image meshImage, convertedImage;
    private PointLight pointLight, headLight;
    private AmbientLight ambientLight;
    private Group root3D, axisGroup;
    private final ExtensionFilter imageFilter = new ExtensionFilter("Image Files", "*.jpg", "*.png");
    private final ExtensionFilter fxmlFilter = new ExtensionFilter("FXML Document", "*.fxml");
    private final FileChooser fileChooser = new FileChooser();
    
    private static final Rotate cameraXRotate = new Rotate(-20,0,0,0,Rotate.X_AXIS);
    private static final Rotate cameraYRotate = new Rotate(-20,0,0,0,Rotate.Y_AXIS);
    private static final Rotate cameraLookXRotate = new Rotate(0,0,0,0,Rotate.X_AXIS);
    private static final Rotate cameraLookZRotate = new Rotate(0,0,0,0,Rotate.Z_AXIS);
    private static final Translate cameraPosition = new Translate(0,0,-7);
    private double dragStartX, dragStartY, dragStartRotateX, dragStartRotateY;
    private Box xAxis,yAxis,zAxis;
    
    
    @FXML
    private StackPane subScenePane;
    @FXML
    private SubScene subScene;
    @FXML
    private VBox navControl;
    @FXML
    private FourWayNavControl eyeNav;
    @FXML
    private FourWayNavControl camNav;
    @FXML
    private ScrollBar zoomBar;
    @FXML
    private VBox meshButtons;
    @FXML
    private Button generateMeshButton;
    @FXML
    private Button exportAsFXMLButton;
    @FXML
    private VBox editor;
    @FXML
    private VBox meshAttributes;
    @FXML
    private VBox pixelSkip;
    @FXML
    private Label pixelSkipValueLabel;
    @FXML
    private Slider pixelSkipSlider;
    @FXML
    private VBox height;
    @FXML
    private Label maxHeightValueLabel;
    @FXML
    private Slider maxHeightSlider;
    @FXML
    private VBox scale;
    @FXML
    private Label scaleValueLabel;
    @FXML
    private Slider scaleSlider;
    @FXML
    private VBox meshImaging;
    @FXML
    private ImageView meshImageView;
    @FXML
    private CheckBox convertImage;
    @FXML
    private CheckBox useForMaterial;
    @FXML
    private HBox materialColors;
    @FXML
    private ColorPicker diffColorPicker;
    @FXML
    private ColorPicker specColorPicker;
    @FXML
    private VBox header;
    @FXML
    private CheckBox wireFrameOn;
    @FXML
    private CheckBox axesOn;
    @FXML
    private CheckBox headLightOn;
    @FXML
    private CheckBox ambientLightOn;
    @FXML
    private CheckBox pointLightOn;
    @FXML
    private Label progressLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button loadImageButton;
    @FXML
    private ComboBox<CullFace> cullFaceChooser;
    @FXML
    private ColorPicker ambientColorPicker;
    @FXML
    private ColorPicker pointColorPicker;
    @FXML
    private ColorPicker headColorPicker;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        initAll();
        
        Timeline defaultTimeline = new Timeline();
        defaultTimeline.getKeyFrames().addAll(new KeyFrame(new Duration(14 - (System.currentTimeMillis() % 14)), (ActionEvent t) -> {
            
            Timeline everySecond = new Timeline();
            everySecond.setCycleCount(Timeline.INDEFINITE);
            everySecond.getKeyFrames().addAll(new KeyFrame(Duration.valueOf(14 + "ms"), (ActionEvent event) -> {
                update();                  
            }));
            everySecond.play();
            System.err.println("Playing");
        }));
        defaultTimeline.play();
    }    

    @FXML
    private void generateMesh(ActionEvent event) {
        if(meshImageView.getImage() != null){
            meshView.setMesh(MeshUtils.buildTriangleMesh(meshImageView.getImage(), (int)pixelSkipSlider.getValue(), (float)maxHeightSlider.getValue(), (float)scaleSlider.getValue()));
        }
    }

    @FXML
    private void exportAsFXML(ActionEvent event) {
    }

    @FXML
    private void convertImageToGrayScale(ActionEvent event) {
        if(convertImage.isSelected()){
            if(convertedImage == null && meshImage != null){
                new ImageConversionTask(meshImage,progressBar,progressLabel).start();
            }else if(convertedImage != null){
                meshImageView.setImage(convertedImage);
            }
        }else{
            meshImageView.setImage(meshImage);
        }
    }

    @FXML
    private void useImageForMaterial(ActionEvent event) {
        if(useForMaterial.isSelected() && meshImageView.getImage() != null){
            meshMaterial.setDiffuseMap(meshImageView.getImage());
        }else{
            meshMaterial.setDiffuseMap(null);
        }
    }

    @FXML
    private void setWireFrame(ActionEvent event) {
        if(wireFrameOn.isSelected()){
            meshView.setDrawMode(DrawMode.LINE);
        }else{
            meshView.setDrawMode(DrawMode.FILL);
        }
    }

    @FXML
    private void showAxes(ActionEvent event) {
        if(axisGroup.isVisible()){
            axisGroup.setVisible(false);
        }else{
            axisGroup.setVisible(true);
        }
    }

    @FXML
    private void headLightOn(ActionEvent event) {
        if(headLightOn.isSelected()){
            headLight.setLightOn(true);
        }else{
            headLight.setLightOn(false);
        }
    }

    @FXML
    private void ambientLightOn(ActionEvent event) {
        if(ambientLightOn.isSelected()){
            ambientLight.setLightOn(true);
        }else{
            ambientLight.setLightOn(false);
        }
    }

    @FXML
    private void pointLightOn(ActionEvent event) {
        if(pointLightOn.isSelected()){
            pointLight.setLightOn(true);
        }else{
            pointLight.setLightOn(false);
        }
    }

    @FXML
    private void loadImageforMesh(ActionEvent event) throws FileNotFoundException {
        
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(imageFilter);
        File tmp = fileChooser.showOpenDialog(this.getScene().getWindow());
        if(tmp != null){
            IMAGE_FILE = tmp;
        }
        meshImage = new Image(new FileInputStream(IMAGE_FILE));
        meshImageView.setImage(meshImage);
    }
    
    @FXML
    private void setCullFace(ActionEvent event){
        meshView.setCullFace(cullFaceChooser.getValue());
    }
    
    //======== Initializers ====================================================
    
    private void initAll(){
        initSubScene();
        initLayoutBindings();
        initHeader();
        initEditor();
        
        initLights();
        createAxes();
        initMeshView();
        
        
        
    }
    
    private void initLights(){
        headLight = new PointLight();        
        headLight.translateXProperty().bindBidirectional(camera.translateXProperty());
        headLight.translateYProperty().bindBidirectional(camera.translateYProperty());
        headLight.translateZProperty().bindBidirectional(camera.translateZProperty());
        headLight.setLightOn(headLightOn.isSelected());
        headLight.colorProperty().bind(headColorPicker.valueProperty());
        headLight.setRotationAxis(Rotate.Y_AXIS);
        headLight.setRotate(cameraYRotate.getAngle());
        
        
        pointLight = new PointLight();
        pointLight.setTranslateX(-1000);
        pointLight.setTranslateY(-1000);
        pointLight.setTranslateZ(-1000);
        pointLight.setLightOn(pointLightOn.isSelected());
        pointLight.colorProperty().bind(pointColorPicker.valueProperty());
        
        ambientLight = new AmbientLight();
        ambientLight.setTranslateY(-1000);
        ambientLight.setLightOn(ambientLightOn.isSelected());
        ambientLight.colorProperty().bind(ambientColorPicker.valueProperty());
        
        root3D.getChildren().addAll(pointLight, headLight, ambientLight);
    }
    
    private void initHeader(){
        
    }
    
    private void initLayoutBindings(){
     subScene.widthProperty().bind(subScenePane.widthProperty());
     subScene.heightProperty().bind(subScenePane.heightProperty());
     
     progressBar.prefWidthProperty().bind(subScene.widthProperty());
     progressLabel.prefWidthProperty().bind(subScene.widthProperty());
    }
    
    private void initMeshView(){
        meshView.setMaterial(meshMaterial);
        root3D.getChildren().add(meshView);        
    }
    
    private void initSubScene(){
        initNavControls();
        root3D = ((Group)subScene.getRoot());
        // CAMERA
        camera.setNearClip(0.0001); // TODO: Workaround as per RT-31255
        camera.setFarClip(100000.0);
        camera.getTransforms().addAll(
               // yUpRotate,
                cameraXRotate,
                cameraYRotate,
                cameraPosition,
                cameraLookXRotate,
                cameraLookZRotate);
        subScene.setCamera(camera);
        root3D.getChildren().add(camera);
        
        // SCENE EVENT HANDLING FOR CAMERA NAV
        subScene.addEventHandler(MouseEvent.ANY, (MouseEvent event) -> {
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                dragStartX = event.getSceneX();
                dragStartY = event.getSceneY();
                dragStartRotateX = cameraXRotate.getAngle();
                dragStartRotateY = cameraYRotate.getAngle();
            } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                double xDelta = event.getSceneX() -  dragStartX;
                double yDelta = event.getSceneY() -  dragStartY;
                cameraXRotate.setAngle(dragStartRotateX - (yDelta*0.7));
                cameraYRotate.setAngle(dragStartRotateY + (xDelta*0.7));
            }
        });
        subScene.addEventHandler(ScrollEvent.ANY, (ScrollEvent event) -> {
            double z = cameraPosition.getZ()-(event.getDeltaY()*0.2);
            z = Math.max(z,-100);
            z = Math.min(z,0);
            cameraPosition.setZ(z);
        });
        
    }
    
    private void initNavControls(){
        zoomBar.setMin(-10000);
        zoomBar.setMax(0);
        zoomBar.setBlockIncrement(10);
        zoomBar.setValue(this.getCameraPosition().getZ());
        zoomBar.setVisibleAmount(5);
        this.getCameraPosition().zProperty().bindBidirectional(zoomBar.valueProperty());
        eyeNav.setListener((Side direction, double amount) -> {
            switch (direction) {
                case TOP:
                    this.getCameraLookXRotate().setAngle(this.getCameraLookXRotate().getAngle()+amount);
                    break;
                case BOTTOM:
                    this.getCameraLookXRotate().setAngle(this.getCameraLookXRotate().getAngle()-amount);
                    break;
                case LEFT:
                    this.getCameraLookZRotate().setAngle(this.getCameraLookZRotate().getAngle()-amount);
                    break;
                case RIGHT:
                    this.getCameraLookZRotate().setAngle(this.getCameraLookZRotate().getAngle()+amount);
                    break;
            }
        });
        camNav.setListener((Side direction, double amount) -> {
            switch (direction) {
                case TOP:
                    this.getCameraXRotate().setAngle(this.getCameraXRotate().getAngle()-amount);
                    break;
                case BOTTOM:
                    this.getCameraXRotate().setAngle(this.getCameraXRotate().getAngle()+amount);
                    break;
                case LEFT:
                    this.getCameraYRotate().setAngle(this.getCameraYRotate().getAngle()+amount);
                    break;
                case RIGHT:
                    this.getCameraYRotate().setAngle(this.getCameraYRotate().getAngle()-amount);
                    break;
            }
        });
    }
    
    private void initEditor(){
        
        cullFaceChooser.getItems().addAll(CullFace.values());
        
        meshMaterial.diffuseColorProperty().bind(diffColorPicker.valueProperty());
        meshMaterial.specularColorProperty().bind(specColorPicker.valueProperty());
        
        pixelSkipSlider.valueProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) -> {
            pixelSkipValueLabel.setText("Pixel Skip : " + t1.intValue());
        });
        
        maxHeightSlider.valueProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) -> {
            maxHeightValueLabel.setText("Max Height : " + t1.floatValue());
        });
        
        scaleSlider.valueProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) -> {
            scaleValueLabel.setText("Scale : " + t1.floatValue());
        });
    }
    
    private void createAxes() {
        axisGroup = new Group();
        
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);
        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);
        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);
        final Sphere red = new Sphere(50);
        red.setMaterial(redMaterial);
        final Sphere blue = new Sphere(50);
        blue.setMaterial(blueMaterial);
        xAxis = new Box(24.0, 0.05, 0.05);
        yAxis = new Box(0.05, 24.0, 0.05);
        zAxis = new Box(0.05, 0.05, 24.0);
        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);
        
        axisGroup.getChildren().addAll(xAxis,yAxis,zAxis);
        root3D.getChildren().add(axisGroup);
    }
    
    private void update() {
        if(pixelSkipSlider.isValueChanging()){
            if(meshView.getMesh() != null){
                meshView.setMesh(MeshUtils.buildTriangleMesh(meshImageView.getImage(), (int)pixelSkipSlider.getValue() , (float)maxHeightSlider.getValue(), (float)scaleSlider.getValue()));
            }
        }
        if(maxHeightSlider.isValueChanging()){
            if(meshView.getMesh() != null){
                meshView.setMesh(MeshUtils.buildTriangleMesh(meshImageView.getImage(), (int)pixelSkipSlider.getValue() , (float)maxHeightSlider.getValue(), (float)scaleSlider.getValue()));
            }
        }
        if(scaleSlider.isValueChanging()){
            if(meshView.getMesh() != null){
                meshView.setMesh(MeshUtils.buildTriangleMesh(meshImageView.getImage(), (int)pixelSkipSlider.getValue() , (float)maxHeightSlider.getValue(), (float)scaleSlider.getValue()));
            }
        }
    }
    //=========== Getters / Setters ============================================
    public PerspectiveCamera getCamera() {
        return camera;
    }

    public File getIMAGE_FILE() {
        return IMAGE_FILE;
    }

    public Rotate getCameraXRotate() {
        return cameraXRotate;
    }

    public Rotate getCameraYRotate() {
        return cameraYRotate;
    }

    public Rotate getCameraLookXRotate() {
        return cameraLookXRotate;
    }

    public Rotate getCameraLookZRotate() {
        return cameraLookZRotate;
    }

    public Translate getCameraPosition() {
        return cameraPosition;
    }

       
    //======= Services and other Concurrency related Items =====================
    private class ImageConversionTask extends Service<WritableImage>{

        private Image imageToConvert;
        private double progress = 0, total = 0;
        private ProgressBar bar;
        private Label label;

        public ImageConversionTask(Image imageToConvert, ProgressBar bar, Label label) {
            this.imageToConvert = imageToConvert;
            this.bar = bar;
            this.label = label;
            this.bar.progressProperty().bind(this.progressProperty());
            this.label.textProperty().bind(this.messageProperty());
        }
                
        @Override
        protected Task<WritableImage> createTask() {
            return new Task<WritableImage>() {

                @Override
                protected WritableImage call() throws Exception {
                    WritableImage tmp = new WritableImage(imageToConvert.getPixelReader(),(int)imageToConvert.getWidth(), (int)imageToConvert.getHeight());
                    total = (int)tmp.getWidth() * (int)tmp.getHeight();
                    updateMessage("Preparing to Convert ... ");
                    for(int y = 0; y < (int)tmp.getHeight(); y++){
                        for(int x = 0; x < (int)tmp.getWidth(); x++){
                            tmp.getPixelWriter().setColor(x, y, imageToConvert.getPixelReader().getColor(x, y).grayscale());
                            progress = x + y;
                            updateProgress(progress , total); 
                            updateMessage("Converting ... ");
                        }
                    }                    
                    return tmp;
                }
            };
            
        }

        @Override
        protected void failed() {
            super.failed();
            bar.progressProperty().unbind();
            label.textProperty().unbind();
            label.setText("Image Conversion Failed");
            bar.setProgress(0);
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            bar.progressProperty().unbind();
            label.textProperty().unbind();
            bar.setProgress(0);
            label.setText("Image Conversion Successful");
            convertedImage = getValue();
            meshImageView.setImage(convertedImage);
        }
    
    }
    
//========================== EOF ===============================================    
}
