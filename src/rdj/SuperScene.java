package rdj;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SuperScene
{

    protected	boolean		    startsWithDisplays = false;
    protected	boolean		    startsWithMotion = false;
    protected	NodeDisplay	    nodeDisplay;
    protected	SubScene	    subScene;

    protected	Group		    rootGroup;
    public	Node3D		    node3d;
    protected	MyCamera	    cam;
    protected	PickResult	    pickResult;

//    protected	ArrayList<Node3D>   node3dArrayList;
    protected	List<Node3D>	    nodeList;
    protected	ObservableList<Node3D> nodeObservableList;
    protected	int		    currentNode3D = 0;
    private	Timer		    interactionTimer, motionTimer, translateTimer, frameRateTimer, expireTimer;
    
    protected   int		    targetFrameRate; // Frames per second
    protected	double		    targetFrameRateInterval;
    protected   double		    realFrameRate; // Frames per second
    protected	double		    realFrameRateInterval;
    protected	int		    framesCounted; // counts fps
    private	double		    lastMotionRate; // Motions per second
    public	static double		    motionRate; // Motions per second
    private	double		    motionRateInterval;
    protected	double		    dropSphereTrailInterval;
    
    protected	Rectangle2D	    rectangle2D;
    protected	StackPane	    stackPane;
    protected	double		    screenwidth, screenheight;
    protected	String		    sceneNameId, sceneFileId;
    private	double		    gxm, gym, gzm, gravitationalMotion;		    // Gravitational Translate Motion
    protected	boolean		    caching = false;
    private	boolean		    motionEnabled = false;
    private	boolean		    steppingEnabled = false;
    private	boolean		    gravityEnabled = true;
    private	boolean		    collisionEnabled = false;
    private	boolean		    shearingEnabled = false; // Only to set BOLD for "Switch Label"
    private final boolean	    recording = false;
    protected	double		    motionFactor = 1; // speed up or or slow down motion
    public	static double		    gravityFactor = 1; // speed up or or slow down motion
    
    protected	Button		    switchMotionButton, switchGravityButton, restoreCoordinalButton;
    protected	Label		    sceneTitleLabel, sceneIdLabel, switchMotionLabel, switchGravityLabel, restoreCoordinalLabel, nodesTitleLabel;
    protected	TitledPane	    titledPane;
    protected	VBox		    sceneDisplayActionVBox;
    protected	MainStage	    mainstage;
    protected   VBox		    sceneSectionVBox, sceneListVBox, sceneFunctionsVBox, nodeSectionVBox, nodeListVBox;//, navSectionVBox, navButtonVBox;
    private	RotateTransition    flipSceneSectionToFrontTransition1, flipSceneSectionToFrontTransition2, flipSceneSectionToBackTransition1, flipSceneSectionToBackTransition2;
    private	RotateTransition    flipNodeSectionToFrontTransition1, flipNodeSectionToFrontTransition2, flipNodeSectionToBackTransition1, flipNodeSectionToBackTransition2;
    private     Label		    sceneCachingLabel,sceneMotionLabel,sceneSteppingLabel,sceneGravityLabel,sceneCollisionsLabel,sceneShearLabel,sceneExitLabel,scenePrintLabel,sceneRecordLabel,sceneListItemLabel;
    protected   String		    sceneMotionLabelText,sceneStepLabelText,sceneGravityLabelText,sceneCollisionsLabelText,sceneShearLabelText;
//    private     Label		    navQLabel, navWLabel, navELabel, nav7Label, nav8Label, nav9Label;
//    private     Label		    navALabel, navSLabel, navDLabel, nav4Label, nav5Label, nav6Label;
//    private     Label		    navZLabel, navXLabel, navCLabel, nav1Label, nav2Label, nav3Label;
    private	long		    nanoTime, lastNanoTime;
    private	double		    period;
    private     AnimationTimer	    translateATimer;
//    private	WritableImage	    wimage;
//    private	ArrayList<WritableImage> wImageList;
//    private	int		    wimagecounter;
    private	int		    nodeListStreams = 0;
    protected	double		    runningTime = 0.0; // mSec
    private final   List<Node3D>    expiredNodeList;
    private final   Predicate<Node3D> expiredNodePredicate;
    protected	int		    expiringNodesPresentInScene = 0; // Means present that will expire
    protected	boolean		    nodesQuedForRemoval = false; // Set by nodesExpiryDetectHandler() and Tested by closeStream() to invoke removeExpiredNodes()
    public	static boolean		    nodesAreBeingRemovedNow = false; // Used to prevent streams from opening
    protected	int		    ssVerbosity = 1;
    private	ThreadPerf	    threadperf1;
    private	ThreadPerf	    threadperf2;
    public      static final String PRODUCT = "GravitySimulator3D";

    public  SuperScene()
    {
	sceneNameId = "Empty Scene"; sceneFileId = "";
	targetFrameRate = 0; realFrameRate = 0; realFrameRateInterval = (1000 / realFrameRate);
	motionRate = 100; lastMotionRate = motionRate; motionRateInterval = 1000 / motionRate;
	sceneMotionLabelText = "Motion";sceneStepLabelText = "Steps";sceneGravityLabelText="Gravity";sceneCollisionsLabelText="Collisions";sceneShearLabelText="Shearing";
	motionFactor = 1; gravityFactor = 1;
	nodeList = new ArrayList<>();
	expiredNodePredicate = (Node3D node) -> ((node.isMotionEnabled()) && (node.getLifetime() > -1.0) && (runningTime >= node.getDeath()));
//	Predicate<Node3D> nonNullPredicate = Objects::nonNull;
//	Predicate<Node3D> expNodePredicate = (Node3D node) -> ((node.getLifetime() > -1.0) && (runningTime >= node.getDeath()));
//	Predicate<Node3D> fullExpNodePredicate = nonNullPredicate.and(expNodePredicate);
	expiredNodeList = new ArrayList<>();
	nodeObservableList = FXCollections.observableList(nodeList);
//	nodeObservableList.addListener(new ListChangeListener() {@Override public void onChanged(ListChangeListener.Change change) { node3dListChangeHandler(change); } });
	threadperf1 = new ThreadPerf();
	threadperf2 = new ThreadPerf();
	
//	expireTimer = new Timer();
//	frameRateTimer = new Timer();
//	translateATimer = new AnimationTimer() { @Override synchronized public void handle(long now) { translateHandler(); }};
//	translateTimer = new Timer();
//	motionTimer = new Timer();
//	interactionTimer = new Timer();
}
    
    final public void createSceneSectionVBoxForSceneDisplay() // Being called from subclasses
    {
	if (1<=ssVerbosity) {System.out.println("createSceneSectionVBoxForSceneDisplay()");}
	// create sceneSectionVBox holding sceneFunctionsVBox (frontside) and sceneListVBox (backside)
        sceneSectionVBox = new VBox(); sceneSectionVBox.setPrefWidth(130); sceneSectionVBox.setPrefHeight(150);

// ==================================================================================================================================================	
	
	// create sceneFunctionsVBox for backside of sceneSectionVBox
        sceneFunctionsVBox = new VBox(); sceneFunctionsVBox.setPrefWidth(130);// sceneFunctionsVBox.setPrefHeight(228);
	
	Label scenesEmptyLabel = new Label(""); scenesEmptyLabel.setFont(getFont(Font.getDefault().getName(), FontWeight.NORMAL, FontPosture.REGULAR, 11)); scenesEmptyLabel.setTextFill(Paint.valueOf("WHITE")); scenesEmptyLabel.setAlignment(Pos.CENTER);
	HBox scenesEmptyHBox = new HBox(scenesEmptyLabel); scenesEmptyHBox.setPrefWidth(130); scenesEmptyHBox.setPrefHeight(11); scenesEmptyHBox.setAlignment(Pos.CENTER);
	sceneFunctionsVBox.getChildren().add(scenesEmptyHBox);
	
	sceneTitleLabel = new Label(sceneNameId); sceneTitleLabel.setFont(getFont(Font.getDefault().getName(), FontWeight.NORMAL, FontPosture.REGULAR, 11)); sceneTitleLabel.setTextFill(Paint.valueOf("WHITE")); sceneTitleLabel.setAlignment(Pos.CENTER);
	HBox sceneTitleHBox = new HBox(sceneTitleLabel); sceneTitleHBox.setPrefWidth(130); sceneTitleHBox.setPrefHeight(20); sceneTitleHBox.setAlignment(Pos.CENTER);
	sceneFunctionsVBox.getChildren().add(sceneTitleHBox);
	
	// Scenes
	Rectangle sceneScenesRect = createRect(110, 15);
	Label sceneScenesLabel = new Label("Scenes"); sceneScenesLabel.setFont(getFont(Font.getDefault().getName(), FontWeight.NORMAL, FontPosture.REGULAR, 11)); sceneScenesLabel.setTextFill(Paint.valueOf("WHITE")); sceneScenesLabel.setTextAlignment(TextAlignment.CENTER);
	StackPane sceneScenesStackPane = new StackPane(sceneScenesLabel, sceneScenesRect); sceneScenesStackPane.setPrefWidth(80); sceneScenesStackPane.setPrefHeight(20); sceneScenesStackPane.setAlignment(Pos.CENTER);
	HBox sceneScenesHBox = new HBox(sceneScenesStackPane); sceneScenesHBox.setPrefWidth(130); sceneScenesHBox.setPrefHeight(20); sceneScenesHBox.setAlignment(Pos.CENTER);

	sceneScenesRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { sceneScenesRect.setFill(Color.rgb(255,255,255,0.5)); });
	sceneScenesRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> 
	{
	    mainstage.getSceneDisplayController().disableMouseMovement();
//	    flipSceneSectionDisplayToBack();
	    flipSceneSectionDisplayToBack(getSceneListVBox(180d, 130d, 15d, Font.getDefault().getName(), FontWeight.NORMAL, FontPosture.REGULAR, 10d, true, false));
	});
	sceneScenesRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { sceneScenesRect.setFill(Color.rgb(0,0,0,0.0)); });
	sceneFunctionsVBox.getChildren().add(sceneScenesHBox);
	
	// Reload
	Rectangle sceneReloadRect = createRect(110, 15);
	Label sceneReloadLabel = new Label("Reload"); sceneReloadLabel.setFont(getFont(Font.getDefault().getName(), FontWeight.NORMAL, FontPosture.REGULAR, 10)); sceneReloadLabel.setTextFill(Paint.valueOf("WHITE")); sceneReloadLabel.setTextAlignment(TextAlignment.CENTER);
	StackPane sceneReloadStackPane = new StackPane(sceneReloadLabel, sceneReloadRect); sceneReloadStackPane.setPrefWidth(80); sceneReloadStackPane.setPrefHeight(20); sceneReloadStackPane.setAlignment(Pos.CENTER);
	HBox sceneReloadHBox = new HBox(sceneReloadStackPane); sceneReloadHBox.setPrefWidth(130); sceneReloadHBox.setPrefHeight(20); sceneReloadHBox.setAlignment(Pos.CENTER);

	sceneReloadRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { sceneReloadRect.setFill(Color.rgb(255,255,255,0.5)); });
	sceneReloadRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> 
	{
	    mainstage.getSceneDisplayController().disableMouseMovement();
	    mainstage.switch2LoadScene(MainStage.scenefile);
	});
	sceneReloadRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { sceneReloadRect.setFill(Color.rgb(0,0,0,0.0)); });
	sceneFunctionsVBox.getChildren().add(sceneReloadHBox);
	
	// Caching
	Rectangle cachingRect = createRect(110, 15);
	sceneCachingLabel = new Label("Caching On"); sceneCachingLabel.setFont(getFont(Font.getDefault().getName(), FontWeight.NORMAL, FontPosture.REGULAR, 10)); sceneCachingLabel.setTextFill(Paint.valueOf("WHITE")); sceneCachingLabel.setTextAlignment(TextAlignment.CENTER);
	StackPane cachingStackPane = new StackPane(sceneCachingLabel, cachingRect); cachingStackPane.setPrefWidth(80); cachingStackPane.setPrefHeight(20); cachingStackPane.setAlignment(Pos.CENTER);
	HBox cachingHBox = new HBox(cachingStackPane); cachingHBox.setPrefWidth(130); cachingHBox.setPrefHeight(20); cachingHBox.setAlignment(Pos.CENTER);

	cachingRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { cachingRect.setFill(Color.rgb(255,255,255,0.5)); });
	cachingRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> 
	{
	    mainstage.getSceneDisplayController().disableMouseMovement(); 
	    caching=!caching; setCachingForAllNodes(caching); if (caching) { setCachingLabelBold(caching); } else { setCachingLabelBold(caching); }
	});
	cachingRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { cachingRect.setFill(Color.rgb(0,0,0,0.0)); });
	sceneFunctionsVBox.getChildren().add(cachingHBox);

	// Motion
	Rectangle sceneMotionRect = createRect(110, 15);
	sceneMotionLabel = new Label("Motion On ↵"); sceneMotionLabel.setFont(getFont(Font.getDefault().getName(), FontWeight.NORMAL, FontPosture.REGULAR, 10)); sceneMotionLabel.setTextFill(Paint.valueOf("WHITE")); sceneMotionLabel.setTextAlignment(TextAlignment.CENTER);
	StackPane sceneMotionStackPane = new StackPane(sceneMotionLabel, sceneMotionRect); sceneMotionStackPane.setPrefWidth(80); sceneMotionStackPane.setPrefHeight(20); sceneMotionStackPane.setAlignment(Pos.CENTER);
	HBox sceneMotionHBox = new HBox(sceneMotionStackPane); sceneMotionHBox.setPrefWidth(130); sceneMotionHBox.setPrefHeight(20); sceneMotionHBox.setAlignment(Pos.CENTER);

	sceneMotionRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { sceneMotionRect.setFill(Color.rgb(255,255,255,0.5)); });
	sceneMotionRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> 
	{
	    mainstage.getSceneDisplayController().disableMouseMovement(); 
//	    motional=!motional; setMotionForAllMotionStateChangeableNodes(motional); setMotionLabelBold(motional);
	    switchMotionForAllMotionStateChangeableNodes();
	});
	sceneMotionRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { sceneMotionRect.setFill(Color.rgb(0,0,0,0.0)); });
	sceneFunctionsVBox.getChildren().add(sceneMotionHBox);

	// Step
	Rectangle sceneSteppingRect = createRect(110, 15);
	sceneSteppingLabel = new Label("Steps On bs"); sceneSteppingLabel.setFont(getFont(Font.getDefault().getName(), FontWeight.NORMAL, FontPosture.REGULAR, 10)); sceneSteppingLabel.setTextFill(Paint.valueOf("WHITE")); sceneSteppingLabel.setTextAlignment(TextAlignment.CENTER);
	StackPane sceneSteppingStackPane = new StackPane(sceneSteppingLabel, sceneSteppingRect); sceneSteppingStackPane.setPrefWidth(80); sceneSteppingStackPane.setPrefHeight(20); sceneSteppingStackPane.setAlignment(Pos.CENTER);
	HBox sceneSteppingHBox = new HBox(sceneSteppingStackPane); sceneSteppingHBox.setPrefWidth(130); sceneSteppingHBox.setPrefHeight(20); sceneSteppingHBox.setAlignment(Pos.CENTER);

	sceneSteppingRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { sceneSteppingRect.setFill(Color.rgb(255,255,255,0.5)); });
	sceneSteppingRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> 
	{
	    mainstage.getSceneDisplayController().disableMouseMovement(); 
	    steppingEnabled=!steppingEnabled; setSteppingForAllMotionStateChangeableNodes(steppingEnabled); setSteppingLabelBold(steppingEnabled);
	});
	sceneSteppingRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { sceneSteppingRect.setFill(Color.rgb(0,0,0,0.0)); });
	sceneFunctionsVBox.getChildren().add(sceneSteppingHBox);

	// Gravity
	Rectangle sceneGravityRect = createRect(110, 15);
	sceneGravityLabel = new Label("Gravitational"); sceneGravityLabel.setFont(getFont(Font.getDefault().getName(), FontWeight.BOLD, FontPosture.REGULAR, 10)); sceneGravityLabel.setTextFill(Paint.valueOf("WHITE")); sceneGravityLabel.setTextAlignment(TextAlignment.CENTER);
	StackPane sceneGravityStackPane = new StackPane(sceneGravityLabel, sceneGravityRect); sceneGravityStackPane.setPrefWidth(80); sceneGravityStackPane.setPrefHeight(20); sceneGravityStackPane.setAlignment(Pos.CENTER);
	HBox sceneGravityHBox = new HBox(sceneGravityStackPane); sceneGravityHBox.setPrefWidth(130); sceneGravityHBox.setPrefHeight(20); sceneGravityHBox.setAlignment(Pos.CENTER);

	sceneGravityRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { sceneGravityRect.setFill(Color.rgb(255,255,255,0.5)); });
	sceneGravityRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> 
	{
	    mainstage.getSceneDisplayController().disableMouseMovement(); 
	    switchGravitationalMotion();
	});
	sceneGravityRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { sceneGravityRect.setFill(Color.rgb(0,0,0,0.0)); });
	sceneFunctionsVBox.getChildren().add(sceneGravityHBox);

	// Collisions
	Rectangle sceneCollisionsRect = createRect(110, 15);
	sceneCollisionsLabel = new Label("Collisional"); sceneCollisionsLabel.setFont(getFont(Font.getDefault().getName(), FontWeight.NORMAL, FontPosture.REGULAR, 10)); sceneCollisionsLabel.setTextFill(Paint.valueOf("WHITE")); sceneCollisionsLabel.setTextAlignment(TextAlignment.CENTER);
	StackPane sceneCollisionsStackPane = new StackPane(sceneCollisionsLabel, sceneCollisionsRect); sceneCollisionsStackPane.setPrefWidth(80); sceneCollisionsStackPane.setPrefHeight(20); sceneCollisionsStackPane.setAlignment(Pos.CENTER);
	HBox sceneCollisionsHBox = new HBox(sceneCollisionsStackPane); sceneCollisionsHBox.setPrefWidth(130); sceneCollisionsHBox.setPrefHeight(20); sceneCollisionsHBox.setAlignment(Pos.CENTER);

	sceneCollisionsRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { sceneCollisionsRect.setFill(Color.rgb(255,255,255,0.5)); });
	sceneCollisionsRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> 
	{
	    mainstage.getSceneDisplayController().disableMouseMovement(); 
	    switchCollisionDetection();
	});
	sceneCollisionsRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { sceneCollisionsRect.setFill(Color.rgb(0,0,0,0.0)); });
	sceneFunctionsVBox.getChildren().add(sceneCollisionsHBox);

	// Rotate / Shear
	Rectangle sceneShearRect = createRect(110, 15);
	sceneShearLabel = new Label("Shearing"); sceneShearLabel.setFont(getFont(Font.getDefault().getName(), FontWeight.NORMAL, FontPosture.REGULAR, 10)); sceneShearLabel.setTextFill(Paint.valueOf("WHITE")); sceneShearLabel.setTextAlignment(TextAlignment.CENTER);
	StackPane sceneShearStackPane = new StackPane(sceneShearLabel, sceneShearRect); sceneShearStackPane.setPrefWidth(80); sceneShearStackPane.setPrefHeight(20); sceneShearStackPane.setAlignment(Pos.CENTER);
	HBox sceneShearHBox = new HBox(sceneShearStackPane); sceneShearHBox.setPrefWidth(130); sceneShearHBox.setPrefHeight(20); sceneShearHBox.setAlignment(Pos.CENTER);

	sceneShearRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { sceneShearRect.setFill(Color.rgb(255,255,255,0.5)); });
	sceneShearRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> 
	{
	    mainstage.getSceneDisplayController().disableMouseMovement(); 
	    switchShearForAllNodes();
	});
	sceneShearRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { sceneShearRect.setFill(Color.rgb(0,0,0,0.0)); });
	sceneFunctionsVBox.getChildren().add(sceneShearHBox);

	// Backup
	Rectangle sceneBackupRect = createRect(110, 15);
	Label sceneBackupLabel = new Label("Backup"); sceneBackupLabel.setFont(getFont(Font.getDefault().getName(), FontWeight.NORMAL, FontPosture.REGULAR, 10)); sceneBackupLabel.setTextFill(Paint.valueOf("WHITE")); sceneBackupLabel.setTextAlignment(TextAlignment.CENTER);
	StackPane sceneBackupStackPane = new StackPane(sceneBackupLabel, sceneBackupRect); sceneBackupStackPane.setPrefWidth(80); sceneBackupStackPane.setPrefHeight(20); sceneBackupStackPane.setAlignment(Pos.CENTER);
	HBox sceneBackupHBox = new HBox(sceneBackupStackPane); sceneBackupHBox.setPrefWidth(130); sceneBackupHBox.setPrefHeight(20); sceneBackupHBox.setAlignment(Pos.CENTER);

	sceneBackupRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { sceneBackupRect.setFill(Color.rgb(255,255,255,0.5)); });
	sceneBackupRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> 
	{
	    mainstage.getSceneDisplayController().disableMouseMovement(); 
	    backupCoordinalsForAllNodes();
	});
	sceneBackupRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { sceneBackupRect.setFill(Color.rgb(0,0,0,0.0)); });
	sceneFunctionsVBox.getChildren().add(sceneBackupHBox);

	// Restore
	Rectangle sceneRestoreRect = createRect(110, 15);
	Label sceneRestoreLabel = new Label("Restore ↹"); sceneRestoreLabel.setFont(getFont(Font.getDefault().getName(), FontWeight.NORMAL, FontPosture.REGULAR, 10)); sceneRestoreLabel.setTextFill(Paint.valueOf("WHITE")); sceneRestoreLabel.setTextAlignment(TextAlignment.CENTER);
	StackPane sceneRestoreStackPane = new StackPane(sceneRestoreLabel, sceneRestoreRect); sceneRestoreStackPane.setPrefWidth(80); sceneRestoreStackPane.setPrefHeight(20); sceneRestoreStackPane.setAlignment(Pos.CENTER);
	HBox sceneRestoreHBox = new HBox(sceneRestoreStackPane); sceneRestoreHBox.setPrefWidth(130); sceneRestoreHBox.setPrefHeight(20); sceneRestoreHBox.setAlignment(Pos.CENTER);

	sceneRestoreRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { sceneRestoreRect.setFill(Color.rgb(255,255,255,0.5)); });
	sceneRestoreRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> 
	{
	    mainstage.getSceneDisplayController().disableMouseMovement(); 
	    restoreCoordinalsForAllMotionStateChangeableNodes();
	});
	sceneRestoreRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { sceneRestoreRect.setFill(Color.rgb(0,0,0,0.0)); });
	sceneFunctionsVBox.getChildren().add(sceneRestoreHBox);

	// Print
	Rectangle scenePrintRect = createRect(110, 15);
	sceneRecordLabel = new Label("Print"); sceneRecordLabel.setFont(getFont(Font.getDefault().getName(), FontWeight.NORMAL, FontPosture.REGULAR, 10)); sceneRecordLabel.setTextFill(Paint.valueOf("WHITE")); sceneRecordLabel.setTextAlignment(TextAlignment.CENTER);
	StackPane scenePrintStackPane = new StackPane(sceneRecordLabel, scenePrintRect); scenePrintStackPane.setPrefWidth(80); scenePrintStackPane.setPrefHeight(20); scenePrintStackPane.setAlignment(Pos.CENTER);
	HBox scenePrintHBox = new HBox(scenePrintStackPane); scenePrintHBox.setPrefWidth(130); scenePrintHBox.setPrefHeight(20); scenePrintHBox.setAlignment(Pos.CENTER);

	scenePrintRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { scenePrintRect.setFill(Color.rgb(255,255,255,0.5)); });
	scenePrintRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> 
	{
	    mainstage.getSceneDisplayController().disableMouseMovement(); 
	    print();
	});
	scenePrintRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { scenePrintRect.setFill(Color.rgb(0,0,0,0.0)); });
	sceneFunctionsVBox.getChildren().add(scenePrintHBox);

//	// Record
//	Rectangle sceneRecordRect = createRect(110, 15);
	sceneRecordLabel = new Label("Record"); sceneRecordLabel.setFont(getFont(Font.getDefault().getName(), FontWeight.NORMAL, FontPosture.REGULAR, 10)); sceneRecordLabel.setTextFill(Paint.valueOf("WHITE")); sceneRecordLabel.setTextAlignment(TextAlignment.CENTER);
//	StackPane sceneRecordStackPane = new StackPane(sceneRecordLabel, sceneRecordRect); sceneRecordStackPane.setPrefWidth(80); sceneRecordStackPane.setPrefHeight(20); sceneRecordStackPane.setAlignment(Pos.CENTER);
//	HBox sceneRecordHBox = new HBox(sceneRecordStackPane); sceneRecordHBox.setPrefWidth(130); sceneRecordHBox.setPrefHeight(20); sceneRecordHBox.setAlignment(Pos.CENTER);
//
//	sceneRecordRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { sceneRecordRect.setFill(Color.rgb(255,255,255,0.5)); });
//	sceneRecordRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> 
//	{
//	    mainstage.getSceneDisplayController().disableMouseMovement(); 
//	    switchRecording();
//	});
//	sceneRecordRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { sceneRecordRect.setFill(Color.rgb(0,0,0,0.0)); });
//	sceneFunctionsVBox.getChildren().add(sceneRecordHBox);

	// Exit
	Rectangle sceneExitRect = createRect(110, 15);
	sceneExitLabel = new Label("Exit"); sceneExitLabel.setFont(getFont(Font.getDefault().getName(), FontWeight.NORMAL, FontPosture.REGULAR, 10)); sceneExitLabel.setTextFill(Paint.valueOf("WHITE")); sceneExitLabel.setTextAlignment(TextAlignment.CENTER);
	StackPane sceneExitStackPane = new StackPane(sceneExitLabel, sceneExitRect); sceneExitStackPane.setPrefWidth(80); sceneExitStackPane.setPrefHeight(20); sceneExitStackPane.setAlignment(Pos.CENTER);
	HBox sceneExitHBox = new HBox(sceneExitStackPane); sceneExitHBox.setPrefWidth(130); sceneExitHBox.setPrefHeight(20); sceneExitHBox.setAlignment(Pos.CENTER);

	sceneExitRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { sceneExitRect.setFill(Color.rgb(255,255,255,0.5)); });
	sceneExitRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> 
	{
	    mainstage.getSceneDisplayController().disableMouseMovement();
	    System.exit(0);
	});
	sceneExitRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { sceneExitRect.setFill(Color.rgb(0,0,0,0.0)); });
	sceneFunctionsVBox.getChildren().add(sceneExitHBox);


// ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------	

	// Add and set initial sceneSectionVBox in sceneDisplay
	if (sceneSectionVBox.getChildren().isEmpty()) { sceneSectionVBox.getChildren().add(sceneFunctionsVBox); } else {sceneSectionVBox.getChildren().set(0,sceneFunctionsVBox);}
//	if (!nodeObservableList.isEmpty()) { mainstage.getSceneDisplayController().getMainVBox().getChildren().set(2, sceneSectionVBox); }
	mainstage.getSceneDisplayController().getMainVBox().getChildren().set(2, sceneSectionVBox);
    }
    
    public VBox getSceneListVBox(double rotateY, double prefWidth, double rowHeight, String fontName, FontWeight fontWeight, FontPosture fontPosture, double fontSize, boolean backEntry, boolean exitEntry)
    {
	sceneListVBox = new VBox(); sceneListVBox.setPrefWidth(prefWidth); // sceneListVBox.setPrefHeight(228);
	Label scenesEmptyLabel = new Label(""); scenesEmptyLabel.setFont(getFont(Font.getDefault().getName(), fontWeight, fontPosture, 11)); scenesEmptyLabel.setTextFill(Paint.valueOf("WHITE")); scenesEmptyLabel.setAlignment(Pos.CENTER);
	HBox scenesEmptyHBox = new HBox(scenesEmptyLabel); scenesEmptyHBox.setPrefWidth(130); scenesEmptyHBox.setPrefHeight(rowHeight); scenesEmptyHBox.setAlignment(Pos.CENTER);
	sceneListVBox.getChildren().add(scenesEmptyHBox);
	
	// create Title item 
	Label scenesTitleLabel = new Label("Scenes"); scenesTitleLabel.setFont(getFont(fontName, fontWeight, fontPosture, fontSize)); scenesTitleLabel.setTextFill(Paint.valueOf("WHITE")); scenesTitleLabel.setAlignment(Pos.CENTER);
	HBox scenesTitleHBox = new HBox(scenesTitleLabel); scenesTitleHBox.setPrefWidth(130); scenesTitleHBox.setPrefHeight(rowHeight); scenesTitleHBox.setAlignment(Pos.CENTER);
	sceneListVBox.getChildren().add(scenesTitleHBox);
	
	FileSystem defaultfs = FileSystems.getDefault();
//        Path scenefiles = defaultfs.getPath(System.getProperty("user.dir"),SuperScene.PRODUCT,"rdj","scenes");
        Path scenefiles = defaultfs.getPath(System.getProperty("user.home"),SuperScene.PRODUCT,"rdj","scenes");
	searchTree(scenefiles, "*.scene").stream().sorted(Comparator.naturalOrder()).forEach((path) ->
	{
	    Rectangle startSceneListItemRect = createRect(prefWidth*85/100, rowHeight);
	    sceneListItemLabel = new Label(path.getFileName().toString()); 
	    if (path.getFileName().toString().equals(sceneFileId))  { sceneListItemLabel.setFont(getFont(Font.getDefault().getName(), fontWeight, fontPosture, fontSize)); sceneListItemLabel.setTextFill(Paint.valueOf("GREY")); sceneListItemLabel.setTextAlignment(TextAlignment.CENTER); }
	    else						    { sceneListItemLabel.setFont(getFont(Font.getDefault().getName(), fontWeight, fontPosture, fontSize)); sceneListItemLabel.setTextFill(Paint.valueOf("WHITE")); sceneListItemLabel.setTextAlignment(TextAlignment.CENTER); }
	    StackPane startSceneListItemStackPane = new StackPane(sceneListItemLabel, startSceneListItemRect); startSceneListItemStackPane.setPrefWidth(80); startSceneListItemStackPane.setPrefHeight(20); startSceneListItemStackPane.setAlignment(Pos.CENTER);
	    HBox startSceneListHBox = new HBox(startSceneListItemStackPane); startSceneListHBox.setPrefWidth(130); startSceneListHBox.setPrefHeight(20); startSceneListHBox.setAlignment(Pos.CENTER);

	    // create the scene EventFilters
	    if (!path.getFileName().toString().equals(sceneFileId))
	    {
		startSceneListItemRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { startSceneListItemRect.setFill(Color.rgb(255,255,255,0.5)); });
		startSceneListItemRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) ->
		{
		    mainstage.getSceneDisplayController().disableMouseMovement(); 
		    mainstage.switch2LoadScene(path);
		});
		startSceneListItemRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { startSceneListItemRect.setFill(Color.rgb(0,0,0,0.0)); });
	    }

	    // Add scene to sceneListVBox
	    sceneListVBox.getChildren().add(startSceneListHBox);
	});

	if (backEntry)
	{
	    // Back
	    Rectangle sceneListItemBackRect = createRect(110, 15);
	    Label sceneListItemBackLabel = new Label("Back"); sceneListItemBackLabel.setFont(getFont(Font.getDefault().getName(), fontWeight, fontPosture, 10)); sceneListItemBackLabel.setTextFill(Paint.valueOf("WHITE")); sceneListItemBackLabel.setTextAlignment(TextAlignment.CENTER);
	    StackPane sceneListItemBackStackPane = new StackPane(sceneListItemBackLabel, sceneListItemBackRect); sceneListItemBackStackPane.setPrefWidth(80); sceneListItemBackStackPane.setPrefHeight(20); sceneListItemBackStackPane.setAlignment(Pos.CENTER);
	    HBox sceneListItemBackHBox = new HBox(sceneListItemBackStackPane); sceneListItemBackHBox.setPrefWidth(130); sceneListItemBackHBox.setPrefHeight(20); sceneListItemBackHBox.setAlignment(Pos.CENTER);

	    // create the backbutton EventFilters
	    sceneListItemBackRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { sceneListItemBackRect.setFill(Color.rgb(255,255,255,0.5)); });
	    sceneListItemBackRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) ->
	    {
		mainstage.getSceneDisplayController().disableMouseMovement(); 
		flipSceneSectionDisplayToFront(sceneFunctionsVBox);
	    });
	    sceneListItemBackRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { sceneListItemBackRect.setFill(Color.rgb(0,0,0,0.0)); });

	    // Add scene to sceneListVBox
	    sceneListVBox.getChildren().add(sceneListItemBackHBox);
	}
	
	if (exitEntry)
	{
	    // Exit
	    Rectangle sceneExitRect = createRect(110, 15);
	    sceneExitLabel = new Label("Exit"); sceneExitLabel.setFont(getFont(Font.getDefault().getName(), fontWeight, fontPosture, 10)); sceneExitLabel.setTextFill(Paint.valueOf("WHITE")); sceneExitLabel.setTextAlignment(TextAlignment.CENTER);
	    StackPane sceneExitStackPane = new StackPane(sceneExitLabel, sceneExitRect); sceneExitStackPane.setPrefWidth(80); sceneExitStackPane.setPrefHeight(20); sceneExitStackPane.setAlignment(Pos.CENTER);
	    HBox sceneExitHBox = new HBox(sceneExitStackPane); sceneExitHBox.setPrefWidth(130); sceneExitHBox.setPrefHeight(20); sceneExitHBox.setAlignment(Pos.CENTER);

	    sceneExitRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { sceneExitRect.setFill(Color.rgb(255,255,255,0.5)); });
	    sceneExitRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> 
	    {
		mainstage.getSceneDisplayController().disableMouseMovement();
		System.exit(0);
	    });
	    sceneExitRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { sceneExitRect.setFill(Color.rgb(0,0,0,0.0)); });
	    sceneListVBox.getChildren().add(sceneExitHBox);
	}

	// Rotate 180 degrees as the sceneListVBox is being displayed on the back of sceneSectionVBox
	sceneListVBox.setRotationAxis(Rotate.Y_AXIS); sceneListVBox.setRotate(rotateY); // Gets displayed on the back of nodeSectionVBox
	
	return sceneListVBox;
    }
    
    private ArrayList<Path> searchTree(Path source, String wildcard)
    {
	ArrayList<Path> paths = new ArrayList<>();
	PathMatcher matcher;
        matcher = FileSystems.getDefault().getPathMatcher("glob:" + wildcard);
	
        if (2<=ssVerbosity) { System.out.println("searchTree: " + source + " " + wildcard); }

        try { Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new FileVisit()
        {
	    @Override public FileVisitResult postVisitDirectory(Object dir, IOException exc) throws IOException
		{
	    //        System.out.println("Visited: " + (Path) dir);
		    return FileVisitResult.CONTINUE;
		}

		@Override public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs) throws IOException
		{
		    return FileVisitResult.CONTINUE;
		}

		@Override public FileVisitResult visitFile(Object file, BasicFileAttributes attrs) throws IOException
		{
		    Path name = (Path) file;

		    if (name.getFileName() != null && matcher.matches(name.getFileName()))
		    {
			paths.add(name);
		    }
		    return FileVisitResult.CONTINUE;
		}

		@Override public FileVisitResult visitFileFailed(Object file, IOException exc) throws IOException
		{
		    //report an error if necessary
		    return FileVisitResult.CONTINUE;
		}        }
        
        ); } catch (IOException ex) { System.err.println("Files.walkFileTree ListTree IOException" + ex); }
        if (2<=ssVerbosity) { System.out.println(); }
	return paths;
    }
    
    protected void print()
    {
	double width = 350;
	double height = 700;
	TextArea textArea = new TextArea();
        textArea.setPrefWidth(width);
        textArea.setPrefHeight(height);

        Text text = new Text();
	text.setText(text.getText() + "ScaleX = " + node3d.getCoordinal().getSxProp().get() + "\r\n");
	text.setText(text.getText() + "ScaleY = " + node3d.getCoordinal().getSyProp().get() + "\r\n");
	text.setText(text.getText() + "ScaleZ = " + node3d.getCoordinal().getSzProp().get() + "\r\n");

	text.setText(text.getText() + "TranslateX = " + node3d.getCoordinal().getTxProp().get() + "\r\n");
	text.setText(text.getText() + "TranslateY = " + node3d.getCoordinal().getTyProp().get() + "\r\n");
	text.setText(text.getText() + "TranslateZ = " + node3d.getCoordinal().getTzProp().get() + "\r\n");

	text.setText(text.getText() + "TranslateXMotion = " + node3d.getCoordinal().getTxmProp().get()*motionRate + "\r\n");
	text.setText(text.getText() + "TranslateYMotion = " + node3d.getCoordinal().getTymProp().get()*motionRate + "\r\n");
	text.setText(text.getText() + "TranslateZMotion = " + node3d.getCoordinal().getTzmProp().get()*motionRate + "\r\n");

	text.setText(text.getText() + "RotationX = " + node3d.getCoordinal().getRxProp().get() + "\r\n");
	text.setText(text.getText() + "RotationY = " + node3d.getCoordinal().getRyProp().get() + "\r\n");
	text.setText(text.getText() + "RotationZ = " + node3d.getCoordinal().getRzProp().get() + "\r\n");

	text.setText(text.getText() + "RotationXMotion = " + node3d.getCoordinal().getRxmProp().get()*motionRate + "\r\n");
	text.setText(text.getText() + "RotationYMotion = " + node3d.getCoordinal().getRymProp().get()*motionRate + "\r\n");
	text.setText(text.getText() + "RotationZMotion = " + node3d.getCoordinal().getRzmProp().get()*motionRate + "\r\n");

	text.setText(text.getText() + "RotationXX = " + node3d.getCoordinal().getRPXXProp().get() + "\r\n");
	text.setText(text.getText() + "RotationXY = " + node3d.getCoordinal().getRPXYProp().get() + "\r\n");
	text.setText(text.getText() + "RotationXZ = " + node3d.getCoordinal().getRPXZProp().get() + "\r\n");

	text.setText(text.getText() + "RotationYX = " + node3d.getCoordinal().getRPYXProp().get() + "\r\n");
	text.setText(text.getText() + "RotationYY = " + node3d.getCoordinal().getRPYYProp().get() + "\r\n");
	text.setText(text.getText() + "RotationYZ = " + node3d.getCoordinal().getRPYZProp().get() + "\r\n");

	text.setText(text.getText() + "RotationZX = " + node3d.getCoordinal().getRPZXProp().get() + "\r\n");
	text.setText(text.getText() + "RotationZY = " + node3d.getCoordinal().getRPZYProp().get() + "\r\n");
	text.setText(text.getText() + "RotationZZ = " + node3d.getCoordinal().getRPZZProp().get() + "\r\n");

	textArea.setText(text.getText());
        Group root = new Group(textArea);
	mainstage.stage.setFullScreen(false);
	Stage stage = new Stage();
	stage.setAlwaysOnTop(true);
        stage.setTitle("Printed node: " + node3d.getId());
        
        Scene scene = new Scene(root, width, height, Color.WHITE);
        stage.setScene(scene);
        stage.show();
    }
    
    public void setSteppingLabelBold(boolean param)	{ if (param) {sceneSteppingLabel.setFont(getFont(Font.getDefault().getName(), FontWeight.BOLD, FontPosture.REGULAR, 10));} else {sceneSteppingLabel.setFont(getFont(Font.getDefault().getName(), FontWeight.NORMAL, FontPosture.REGULAR, 10));}}
    public void setCachingLabelBold(boolean param)	{ if (param) {sceneCachingLabel.setFont(getFont(Font.getDefault().getName(), FontWeight.BOLD, FontPosture.REGULAR, 10));} else {sceneCachingLabel.setFont(getFont(Font.getDefault().getName(), FontWeight.NORMAL, FontPosture.REGULAR, 10));}}
    public void setMotionLabelBold(boolean param)	{ if (param) {sceneMotionLabel.setFont(getFont(Font.getDefault().getName(), FontWeight.BOLD, FontPosture.REGULAR, 10));} else {sceneMotionLabel.setFont(getFont(Font.getDefault().getName(), FontWeight.NORMAL, FontPosture.REGULAR, 10));}}
    public void setSwitchGravityBold(boolean param)	{ if (param) {switchGravityLabel.setFont(getFont(Font.getDefault().getName(), FontWeight.BOLD, FontPosture.REGULAR, 10));} else {switchGravityLabel.setFont(getFont(Font.getDefault().getName(), FontWeight.NORMAL, FontPosture.REGULAR, 10));}}

    public void createNodeSectionVBoxForSceneDisplay() // Being called from BlaBlaScene subclasses
    {
	if (1<=ssVerbosity) {System.out.println("createNodeSectionVBoxForSceneDisplay()");}
	// create nodeSectionVBox sceneDisplay
        nodeSectionVBox = new VBox(); nodeSectionVBox.setPrefWidth(130); nodeSectionVBox.setPrefHeight(228);

	// create nodeListVBox
        nodeListVBox = new VBox(); nodeListVBox.setPrefWidth(130); nodeListVBox.setPrefHeight(228);
	HBox linespace = new HBox(); linespace.setPrefHeight(11); nodeListVBox.getChildren().add(linespace); // Just an empty linespace at the top

	// create Title item 
	nodesTitleLabel = new Label("Nodes"); nodesTitleLabel.setFont(getFont(Font.getDefault().getName(), FontWeight.BOLD, FontPosture.REGULAR, 11)); nodesTitleLabel.setTextFill(Paint.valueOf("WHITE")); nodesTitleLabel.setAlignment(Pos.CENTER);
	HBox nodesTitleHBox = new HBox(nodesTitleLabel); nodesTitleHBox.setPrefWidth(130); nodesTitleHBox.setPrefHeight(20); nodesTitleHBox.setAlignment(Pos.CENTER);

	// Add Title to nodeListVBox
	nodeListVBox.getChildren().add(nodesTitleHBox);

	// Add nodeListVBox to nodeSectionVBox
	nodeSectionVBox.getChildren().add(nodeListVBox);

	// Stick the nodesection to the SceneDisplay
//	if ( ! nodeObservableList.isEmpty() )
//	{
	    mainstage.getSceneDisplayController().getMainVBox().getChildren().set(3, nodeSectionVBox); 
	    mainstage.getSceneDisplayController().fpsVBox.setVisible(true);
	    mainstage.getSceneDisplayController().factorVBox.setVisible(true);
//	}
	
//	// Add nodeItems to nodeListVBox
//	nodeObs ervableList.stream().filter((thisnode3d) -> (thisnode3d.isOnMenu())).forEach((thisnode) -> { nodeListVBox.getChildren().add(thisnode.getNodeMenuItemHBox()); });
    }
    
    public void flipSceneSectionDisplayToBack(VBox vbox)
    {
	double flipDuration = 0.25;
	mainstage.disableMouseMovement();
	flipSceneSectionToBackTransition1 = new RotateTransition(Duration.seconds(flipDuration), sceneSectionVBox);
	flipSceneSectionToBackTransition1.setInterpolator(Interpolator.EASE_IN);
	flipSceneSectionToBackTransition1.setAxis(Rotate.Y_AXIS);
	flipSceneSectionToBackTransition1.setByAngle(sceneSectionVBox.getRotate() + 90);
	flipSceneSectionToBackTransition1.setCycleCount(1);
	flipSceneSectionToBackTransition1.setOnFinished((ActionEvent event) -> { sceneSectionVBox.getChildren().set(0, vbox);
	flipSceneSectionToBackTransition2.play(); });

	flipSceneSectionToBackTransition2 = new RotateTransition(Duration.seconds(flipDuration), sceneSectionVBox);
	flipSceneSectionToBackTransition2.setInterpolator(Interpolator.EASE_OUT);
	flipSceneSectionToBackTransition2.setAxis(Rotate.Y_AXIS);
	flipSceneSectionToBackTransition2.setByAngle(sceneSectionVBox.getRotate() + 90);
	flipSceneSectionToBackTransition2.setCycleCount(1);
	flipSceneSectionToBackTransition2.setOnFinished((ActionEvent event) -> { sceneSectionVBox.setRotate(180); });
	flipSceneSectionToBackTransition1.play();
    }
    
    public void flipSceneSectionDisplayToFront(VBox vbox)
    {
	double flipDuration = 0.25;
	mainstage.disableMouseMovement();
	flipSceneSectionToFrontTransition1 = new RotateTransition(Duration.seconds(flipDuration), sceneSectionVBox);
	flipSceneSectionToFrontTransition1.setInterpolator(Interpolator.EASE_IN);
	flipSceneSectionToFrontTransition1.setAxis(Rotate.Y_AXIS);
	flipSceneSectionToFrontTransition1.setByAngle(sceneSectionVBox.getRotate() - 90);
	flipSceneSectionToFrontTransition1.setCycleCount(1);
	flipSceneSectionToFrontTransition1.setOnFinished((ActionEvent event) -> { sceneSectionVBox.getChildren().set(0, vbox); flipSceneSectionToFrontTransition2.play(); });

	flipSceneSectionToFrontTransition2 = new RotateTransition(Duration.seconds(flipDuration), sceneSectionVBox);
	flipSceneSectionToFrontTransition2.setInterpolator(Interpolator.EASE_OUT);
	flipSceneSectionToFrontTransition2.setAxis(Rotate.Y_AXIS);
	flipSceneSectionToFrontTransition2.setByAngle(sceneSectionVBox.getRotate() - 90);
	flipSceneSectionToFrontTransition2.setCycleCount(1);
	flipSceneSectionToFrontTransition2.setOnFinished((ActionEvent event) -> { sceneSectionVBox.setRotate(0); });

	flipSceneSectionToFrontTransition1.play(); 
    }
    
    public void flipNodeSectionDisplayToBack(VBox vbox)
    {
	double flipDuration = 0.25;
	mainstage.disableMouseMovement();
	flipNodeSectionToBackTransition1 = new RotateTransition(Duration.seconds(flipDuration), nodeSectionVBox);
	flipNodeSectionToBackTransition1.setInterpolator(Interpolator.EASE_IN);
	flipNodeSectionToBackTransition1.setAxis(Rotate.Y_AXIS);
	flipNodeSectionToBackTransition1.setByAngle(nodeSectionVBox.getRotate() + 90);
	flipNodeSectionToBackTransition1.setCycleCount(1);
	flipNodeSectionToBackTransition1.setOnFinished((ActionEvent event) -> { nodeSectionVBox.getChildren().set(0, vbox); flipNodeSectionToBackTransition2.play(); });

	flipNodeSectionToBackTransition2 = new RotateTransition(Duration.seconds(flipDuration), nodeSectionVBox);
	flipNodeSectionToBackTransition2.setInterpolator(Interpolator.EASE_OUT);
	flipNodeSectionToBackTransition2.setAxis(Rotate.Y_AXIS);
	flipNodeSectionToBackTransition2.setByAngle(nodeSectionVBox.getRotate() + 90);
	flipNodeSectionToBackTransition2.setCycleCount(1);
	flipNodeSectionToBackTransition2.setOnFinished((ActionEvent event) -> { nodeSectionVBox.setRotate(180); });

	flipNodeSectionToBackTransition1.play();
    }
    
    public void flipNodeSectionDisplayToFront(VBox vbox)
    {
	double flipDuration = 0.25;
	mainstage.disableMouseMovement();
	flipNodeSectionToFrontTransition1 = new RotateTransition(Duration.seconds(flipDuration), nodeSectionVBox);
	flipNodeSectionToFrontTransition1.setInterpolator(Interpolator.EASE_IN);
	flipNodeSectionToFrontTransition1.setAxis(Rotate.Y_AXIS);
	flipNodeSectionToFrontTransition1.setByAngle(nodeSectionVBox.getRotate() - 90);
	flipNodeSectionToFrontTransition1.setCycleCount(1);
	flipNodeSectionToFrontTransition1.setOnFinished((ActionEvent event) -> { nodeSectionVBox.getChildren().set(0, vbox); flipNodeSectionToFrontTransition2.play(); });

	flipNodeSectionToFrontTransition2 = new RotateTransition(Duration.seconds(flipDuration), nodeSectionVBox);
	flipNodeSectionToFrontTransition2.setInterpolator(Interpolator.EASE_OUT);
	flipNodeSectionToFrontTransition2.setAxis(Rotate.Y_AXIS);
	flipNodeSectionToFrontTransition2.setByAngle(nodeSectionVBox.getRotate() - 90);
	flipNodeSectionToFrontTransition2.setCycleCount(1);
	flipNodeSectionToFrontTransition2.setOnFinished((ActionEvent event) -> { nodeSectionVBox.setRotate(0); });

	flipNodeSectionToFrontTransition1.play(); 
    }
    
    public void setScenePeriods()	    { /*frameRate = 0; motionRate = 0; frameRateInterval = 1000 / frameRate;motionRateInterval = 1000 / motionRate;*/ } // Overriden by subclasses
    
    public void startScene()
    {
	if (1<=ssVerbosity) {System.out.println("startScene()");}
	createFrameRateCalcTimer();
	createTranslationTimer();
	createMotionTimer();
	enableMotionForAllStartingWithMotionNodes();
    }
    
    public void pauseScene()
    {
	if (2<=ssVerbosity) {System.out.println("pauseScene()");}
	cancelMotionTimer();
	cancelTranslationTimer();
//	cancelFrameRateCalcTimer();
    }

    public void continueScene()
    {
	if (2<=ssVerbosity) {System.out.println("continueScene()");}
//	createFrameRateCalcTimer();
	createTranslationTimer();
	createMotionTimer();
    }
    
    public void stopScene() // Only use when ending / switching scene
    {
	if (1<=ssVerbosity) {System.out.println("stopScene()");}
	cancelMotionTimer();
	cancelTranslationTimer();
	cancelFrameRateCalcTimer();
	disableMotionForAllNodes();
    }

    private void detectAndQueExpiredNodesHandler()	
    {
	if ((!nodesAreBeingRemovedNow))
	{
	    Platform.runLater(() ->
	    {
		expiredNodeList.clear(); expiredNodeList.addAll(nodeObservableList.filtered(expiredNodePredicate));
		if (expiredNodeList.size() > 0) {nodesQuedForRemoval = true; if (3<=ssVerbosity) {System.out.println("detectAndQueExpiredNodesHandler() nodesQuedForRemoval: " + nodesQuedForRemoval + ")");}}
	    }); 
	    runningTime += 100.0; // Should only run when scene is motional
	}
    }

    private void removeExpiredNodes() // Only when: nodesQuedForRemoval
    {
	if (!nodesAreBeingRemovedNow) // locks indoor while busy
	{
	    nodesAreBeingRemovedNow = true;
	    Platform.runLater(() ->
	    {
		pauseScene();
		if (3<=ssVerbosity) {System.out.println("Removing expiredNodeList(): " + expiredNodeList.size() + " nodes");}
		expiredNodeList.stream().forEach((node) -> { removeNodeFromScene(node); }); // Removing node
		expiredNodeList.clear();
		nodesAreBeingRemovedNow = false;
		nodesQuedForRemoval = false;
		continueScene();
	    });
	}
    }
    
    synchronized protected void addNodeToScene(Node3D node)
    {
	if (3<=ssVerbosity) {System.out.println("addNodeToScene(Node3D " + node.getId() + ")");}
	nodeObservableList.add(node); nodesTitleLabel.setText("Nodes (" + nodeObservableList.size() + ")" );
	if (rootGroup.getChildren().indexOf(node.getRootGroup()) == -1) { rootGroup.getChildren().add(node.getRootGroup()); }
	if (node.isOnMenu()) { nodeListVBox.getChildren().add(node.getNodeIdMenuItemHBox());  } if (3<=ssVerbosity) {System.out.println("node.isOnMenu() " + node.isOnMenu() + ")");}
	if (node.getLifetime() > -1.0) { expiringNodesPresentInScene++; } if (3<=ssVerbosity) {System.out.println("add node.getLifetime() " + node.getLifetime() + ") cnt: " + expiringNodesPresentInScene);}
	if (expiringNodesPresentInScene==1) {createExpireTimer();}
    }
    
    synchronized protected void removeNodeFromScene(Node3D node)
    {
	if (3<=ssVerbosity) {System.out.println("removeNodeFromScene(Node3D " + node.getId() + ")");}
	if (node.isOnMenu()) { nodeListVBox.getChildren().remove(node.getNodeIdMenuItemHBox()); } // Remove from menu
	if (rootGroup.getChildren().indexOf(node.getRootGroup()) != -1) { rootGroup.getChildren().remove(node.getRootGroup()); } // Remove from superscene rootGroup
	if (node.getLifetime() > -1.0) { expiringNodesPresentInScene--; if (3<=ssVerbosity) {System.out.println("rem node.getLifetime() " + node.getLifetime() + ") cnt: " + expiringNodesPresentInScene);}} // 
	nodeObservableList.remove(node); nodesTitleLabel.setText("Nodes (" + nodeObservableList.size() + ")" );
	if (expiringNodesPresentInScene==0) {cancelExpireTimer();}
    }
    
    private void node3dListChangeHandler(ListChangeListener.Change change)
    {
	if (3<=ssVerbosity)
	{
	    while (change.next())
	    {
		if (change.wasAdded()) {System.out.println("nodeObsList wasAdded: " + change.getAddedSize());}
		if (change.wasPermutated()) {System.out.println("nodeObsList wasPermutated");}
		if (change.wasRemoved()) {System.out.println("nodeObsList wasRemoved: " + change.getRemovedSize());}
		if (change.wasReplaced()) {System.out.println("nodeObsList wasReplaced");}
		if (change.wasUpdated()) {System.out.println("nodeObsList wasUpdated");}
	    }
	}
    }
    
    // We only want to removed expired nodes when there are no nodearraystreams open, so we're counting the number of streams and only remove nodes from the nodesarray when there are no streams open
    private Stream<Node3D> openStream()		{ if (!nodesAreBeingRemovedNow) { nodeListStreams++; return nodeObservableList.stream(); } else { return new ArrayList<Node3D>().stream(); }}
    private void closeStream()			{ if (!nodesAreBeingRemovedNow) { if ( nodeListStreams > 0 ) { nodeListStreams--; } if ((nodesQuedForRemoval) && (nodeListStreams == 0)) { removeExpiredNodes();} }}

    public void createExpireTimer()		{ expireTimer = new Timer(); expireTimer.scheduleAtFixedRate(new TimerTask() { @Override public void run() { detectAndQueExpiredNodesHandler(); } }, 0, 100L); {System.out.println("createExpireTimer(100mSec)");}}
    public void createFrameRateCalcTimer()	{ frameRateTimer = new Timer(); frameRateTimer.scheduleAtFixedRate(new TimerTask() { @Override public void run() { frameRateHandler(); } }, 0, 250L); }
    public void createTranslationTimer()	{ if (targetFrameRate == 0) { createTranslateAnimTimer(); } else { createTranslateTimer(); } }
    public void createTranslateAnimTimer()	{ translateATimer = new AnimationTimer() { @Override synchronized public void handle(long now) { translateHandler(); }}; translateATimer.start(); }
    public void createTranslateTimer()		{ if (targetFrameRate > 0)	{ targetFrameRateInterval = 1000 / targetFrameRate; translateTimer = new Timer(); translateTimer.scheduleAtFixedRate(new TimerTask() { @Override public void run() { translateHandler(); } }, 0, (long) targetFrameRateInterval); } else { System.out.println("Error: SuperScene.createTranslateTimer() started with targetFrameRate = 0\r\n" + Arrays.toString(Thread.currentThread().getStackTrace())); }}
    public void createMotionTimer()		{ motionRateInterval = 1000 / motionRate; motionTimer = new Timer(); motionTimer.scheduleAtFixedRate(new TimerTask()   { @Override public void run() { motionHandler(); interactionHandler(); } }, 0, (long) motionRateInterval); }
    public void createInteractionTimer()	{ interactionTimer = new Timer(); interactionTimer.scheduleAtFixedRate(new TimerTask() { @Override public void run() { interactionHandler(); } }, 0, (long) motionRateInterval); }
    
    public void cancelExpireTimer()		{ if (expireTimer != null) { expireTimer.cancel(); if (1<=ssVerbosity) {System.out.println("cancelExpireTimer()");}}}
    public void cancelFrameRateCalcTimer()	{ if (frameRateTimer != null) { frameRateTimer.cancel(); }}
    public void cancelTranslationTimer()	{ cancelTranslateTimer(); cancelTranslateATimer(); }
    public void cancelTranslateTimer()		{ if (translateTimer != null) { translateTimer.cancel(); }}
    public void cancelTranslateATimer()		{ if (translateATimer != null) { translateATimer.stop(); }}
    public void cancelMotionTimer()		{ if (motionTimer != null) { motionTimer.cancel(); }}
    public void cancelInteractionTimer()	{ if (interactionTimer != null) { interactionTimer.cancel(); }}

    synchronized private void frameRateHandler()	
    {
	nanoTime = System.nanoTime(); period = ((nanoTime - lastNanoTime)); lastNanoTime = nanoTime;
	realFrameRate = 1000000000/(period/framesCounted); realFrameRateInterval = 1000/realFrameRate; framesCounted = 0;
	// Prevent error: Not on FX application thread;
	Platform.runLater(() -> 
	{
	    mainstage.getSceneDisplayController().fpsLabel.setText(getNum(realFrameRate,1));
	});
    }

    synchronized private void translateHandler()	
    {
	// Better motional performance
	Platform.runLater(() -> 
	{
	    cam.getNode().setDisable(true);
	    cam.getNode().setNearClip(0); cam.getNode().setFarClip(0); // Disable rendering
	    if (!nodesAreBeingRemovedNow) { openStream().filter((thisnode3d) -> (thisnode3d.isMotionEnabled())).forEach((thisnode3d) -> { thisnode3d.translateHandler(); }); closeStream(); }
	    cam.getNode().setNearClip(0.2); cam.getNode().setFarClip(10000000000000d); // Enable rendering
	    cam.getNode().setDisable(false);
//	    if (recording) { Platform.runLater(() -> {WritableImage wimage = new WritableImage((int) subScene.getWidth(), (int) subScene.getHeight());subScene.snapshot(null, wimage);wImageList.add(wimage);});}
	    framesCounted++;
	});
    }

    public String getNum(double num, int decimal) { return String.format("%."+Integer.toString(decimal)+"f", num); }
    
    synchronized private void motionHandler() // Adds motion to translation
    {
	// Better performance without
//	Platform.runLater(() -> 
//	{
	    if (!nodesAreBeingRemovedNow) { openStream().filter((thisnode3d) -> (thisnode3d.isMotionEnabled())).forEach((thisnode) -> { thisnode.motionHandler(); if (thisnode.isSingleStepMotion()) {thisnode.setMotionEnabled(false);} }); closeStream();}
//	});
    }

//    synchronized protected void interactionHandler()
//    {
//	// Better performance without
////	Platform.runLater(() -> 
////	{
//	for(Node3D thisnode:nodeObservableList)
//	{
//		if ((gravitational) && (thisnode.getCelestial().isGravitational())&&(thisnode.isMotionEnabled())) { new AddGravitationalMotion(thisnode, nodeObservableList,motionRate,gravityFactor); }
//	}
////	});
//    }

    synchronized protected void interactionHandler()
    {
	// Better performance without
//	Platform.runLater(() -> 
//	{
//	    if (motionEnabled) { threadperf2.startWatch("interactionHandler()"); }
	    if ((motionEnabled) && (!nodesAreBeingRemovedNow))
	    {
		openStream().filter((thisnode) -> (thisnode.isMotionEnabled())).forEach((thisnode) -> 
		{
		    if ((gravityEnabled) && (thisnode.getCelestial().isGravitational())) { gravityHandler(thisnode); }
    //		if ((gravitational) && (thisnode.getCelestial().isGravitational())) { new AddGravitationalMotion(thisnode, nodeObservableList,motionRate,gravityFactor); }
		    if ((collisionEnabled)   && (thisnode.getCelestial().isCollisional()))   { collisionHandler(thisnode); } 
		}); closeStream();}
//	});
    }

    public double getGravitationalMotion(double mass, double radius)		{ return (Celestials.getGravitConst() * mass / Math.pow(radius, 2)); }	// m/Sec

    private void gravityHandler(Node3D targetnode) // Gets called by interactionHandler
    {
	// Better performance without
//	Platform.runLater(() -> 
//	{
	    gxm = 0;
	    gym = 0;
	    gzm = 0;
	    if (!nodesAreBeingRemovedNow)
	    { nodeObservableList.stream().filter((refnode) -> ((refnode.getCelestial().isGravitational()) && (refnode.isMotionEnabled()) && (targetnode != refnode))).forEach((refnode) ->
		{
		    // Gravity Groups 0->any || any->0 || group==group
		    if 
		    (
			(targetnode.getCelestial().getGroup()==0) || (refnode.getCelestial().getGroup()==0) ||
			(targetnode.getCelestial().getGroup()==refnode.getCelestial().getGroup())
		    )
		    {
			Point3D vector = getUnifiedVector(refnode.getCoordinal().getLocation(),targetnode.getCoordinal().getLocation());
			double distance = getDistance(targetnode.getCoordinal().getLocation(),refnode.getCoordinal().getLocation());

    //			v1 = getVector(destnode.getMoveGroup().localToScene(Point3D.ZERO).getX(),destnode.getMoveGroup().localToScene(Point3D.ZERO).getY(),destnode.getMoveGroup().localToScene(Point3D.ZERO).getZ(),sourcenode.getMoveGroup().localToScene(Point3D.ZERO).getX(),sourcenode.getMoveGroup().localToScene(Point3D.ZERO).getY(),sourcenode.getMoveGroup().localToScene(Point3D.ZERO).getZ());
    //			double distance = getDistance
    //			(
    //				sourcenode.getMoveGroup().localToScene(Point3D.ZERO).getX(),sourcenode.getMoveGroup().localToScene(Point3D.ZERO).getY(),sourcenode.getMoveGroup().localToScene(Point3D.ZERO).getZ(),
    //				destnode.getMoveGroup().localToScene(Point3D.ZERO).getX(),destnode.getMoveGroup().localToScene(Point3D.ZERO).getY(),destnode.getMoveGroup().localToScene(Point3D.ZERO).getZ()
    //			);
			// System.out.println("getDistance(sourcenode.getLocation(" + sourcenode.getLocation() + "), innernode.getLocation(" + destnode.getLocation() + "))");

			gravitationalMotion = getGravitationalMotion(refnode.getCelestial().getMass(), distance) / motionRate; // interval rate division
			// System.out.println("gravitationalMotion(" + gravitationalMotion + ") = getGravitationalMotion(destnode.getCelestial().getMass(" + destnode.getCelestial().getMass() + "), distance(" + distance + ")) / motionRate(" + motionRate + ")");

			if ((targetnode.getCelestial().getPolarity() != 0) && (refnode.getCelestial().getPolarity() != 0)) // Calculate polarized (anti-)gravitation
			{
			    double antiGravitationalMotion = Math.negateExact(targetnode.getCelestial().getPolarity() * refnode.getCelestial().getPolarity()) * gravitationalMotion;
			    gxm += ((vector.getX() * antiGravitationalMotion) * gravityFactor / motionRate);
			    gym += ((vector.getY() * antiGravitationalMotion) * gravityFactor / motionRate);
			    gzm += ((vector.getZ() * antiGravitationalMotion) * gravityFactor / motionRate);
			    // System.out.println(" -> " + destnode.getCelestial().getName() + " motion x,y,z: " + v1.x + "," + v1.y + "," + v1.z + " a-gmotion: " + antiGravitationalMotion);
			}
			else
			{
			    gxm += ((vector.getX() * gravitationalMotion) * gravityFactor/ motionRate);
			    gym += ((vector.getY() * gravitationalMotion) * gravityFactor/ motionRate);
			    gzm += ((vector.getZ() * gravitationalMotion) * gravityFactor/ motionRate);
			    // System.out.println(" -> " + destnode.getCelestial().getName() + " motion x,y,z: " + v1.x + "," + v1.y + "," + v1.z + "   gmotion: " + gravitationalMotion);
			}
		    }
		}); 
	    }
	    // System.out.println(sourcenode.getCelestial().getName() + " totalmotion " + " x,y,z: " + gxm + "," + gym + "," + gzm);
	    targetnode.addMotion(gxm, gym, gzm); // Adds gravitational motion to the nodes motion
//	});
    }

    public static Point3D getUnifiedVector(Point3D refpoint, Point3D targetpoint) // Unify sum of motion to 1.0
    {
	double high = (Math.abs(refpoint.getX() - targetpoint.getX()) + Math.abs(refpoint.getY() - targetpoint.getY()) + Math.abs(refpoint.getZ() - targetpoint.getZ()));
	Point3D vectorUnified = new Point3D((refpoint.getX() - targetpoint.getX()) / high,(refpoint.getY() - targetpoint.getY()) / high,(refpoint.getZ() - targetpoint.getZ()) / high);
//	System.out.println("vectorUnified: " + vectorUnified.toString());
        return vectorUnified;
    }
    
    public double getDistance(Point3D point1, Point3D point2) {	return Math.sqrt(Math.pow(point2.getX() - point1.getX(), 2) + Math.pow(point2.getY() - point1.getY(), 2) + Math.pow(point2.getZ() - point1.getZ(), 2)); }

    private void collisionHandler(Node3D sourcenode)
    {
	// Better performance without
//	Platform.runLater(() -> 
//	{
	    if (!nodesAreBeingRemovedNow) { nodeObservableList.stream().filter((destnode) -> ((destnode.getCelestial().isCollisional()) && (sourcenode != destnode))).forEach((destnode) ->
	    {
		if (getDistance(sourcenode.getCoordinal().getLocation(),destnode.getCoordinal().getLocation() ) < sourcenode.getCelestial().getRadius()+destnode.getCelestial().getRadius())
		{
		    if (sourcenode.getCelestial().getMass() < destnode.getCelestial().getMass())
			    {destnode.getNodeGroup().getChildren().add(sourcenode.moveGroup); sourcenode.setMotionEnabled(false);sourcenode.getCelestial().setCollisional(false);}
		    else    {sourcenode.getNodeGroup().getChildren().add(destnode.moveGroup); destnode.setMotionEnabled(false);destnode.getCelestial().setCollisional(false);}
		}
	    }); }
//	});

//	if (sourcenode.getCelestial().isCollisional())
//	{
//	    node3dArrayList.stream().filter((destnode) -> ((destnode.getCelestial().isCollisional()) && (sourcenode != destnode))).forEach((destnode) ->
//	    {
//		if (sourcenode.getRootGroup().intersects(destnode.getRootGroup().getLayoutBounds())) { System.out.println(sourcenode.getId() + " -> " + destnode.getId()); }
//	    });
//	}
    }
    
    public double getGravitationalForce(double m1, double m2, double radius)	{ return (Celestials.getGravitConst() * m1 * m2 / Math.pow(radius, 2)); } // Newton
    public double getOrbitalVelocity(double mass, double radius)		{ return Math.sqrt((Celestials.getGravitConst() * mass) / radius); }

    private void camTrackingHandler()
    {
//	Platform.runLater(() -> 
//	{
//	});
//	    // Camera auto-aiming to node 
//	    v1 = getVector(node3dArrayList.get(7).getTranslateX(),node3dArrayList.get(7).getTranslateY(),node3dArrayList.get(7).getTranslateZ(),node3dArrayList.get(3).getTranslateX(),node3dArrayList.get(3).getTranslateY(),node3dArrayList.get(3).getTranslateZ());
//	    double x = cam.getOCState().getTxProp().get(); double z = cam.getOCState().getTzProp().get();
//
//	    if (z<0)	    // BackPlane
//	    {
//		if (x<0)    // LeftPlane
//		{
//			if (x<z)	// 08:00 Pie
//			{
//			    cam.getOCState().getRyProp().set(-v1.z*90+90); 
//			}
//			else	// 07:00 Pie
//			{
//			    cam.getOCState().getRyProp().set(v1.x*90+0); 
//			}
//		}
//		else	// RightPlane
//		{
//			if (x<Math.abs(z))	// 05:00 Pie
//			{
//			    cam.getOCState().getRyProp().set(v1.x*90+0); 
//			}
//			else		// 04:00 Pie
//			{
//			    cam.getOCState().getRyProp().set(v1.z*90+270); 
//			}
//		}
//	    }
//	    else	    // FrontPlane
//	    {
//		if (x<0)    // LeftPlane
//		{
//			if (Math.abs(x)>=z)	// 10:00 Pie
//			{
//			    cam.getOCState().getRyProp().set(-v1.z*90+90); 
//			}
//			else		// 11:00 Pie
//			{
//			    cam.getOCState().getRyProp().set(-v1.x*90+180); 
//			}
//		}
//		else	// RightPlane
//		{
//			if (x<z)	// 01:00 Pie
//			{
//			    cam.getOCState().getRyProp().set(-v1.x*90+180); 
//			}
//			else		// 02:00 Pie
//			{
//			    cam.getOCState().getRyProp().set(v1.z*90+270); 
//			}
//		}
//	    }
//	    
////	    if	(z < 0)    {cam.getOCState().getRyProp().set(v1.x*90);} else {cam.getOCState().getRyProp().set(-v1.x*90+180);}
//	    
//	    
////	    cam.getOCState().getRyProp().set(v1.x*90+0);	// -Z Pie (V)
////	    cam.getOCState().getRyProp().set(-v1.z*90+90);	// -X Pie (<)
////	    cam.getOCState().getRyProp().set(-v1.x*90+180);	// +Z Pie (^)
////	    cam.getOCState().getRyProp().set(v1.z*90+270);	// +X Pie (>)
//	    
//	    cam.getOCState().getRxProp().set(-(v1.y *90));
//	    nodeDisplay.statusLabel.setText(node3dArrayList.get(0).getCelestial().getName() + " vector x,y,z: " + getNum(v1.x,3) + "," + getNum(v1.y,3) + "," + getNum(v1.z,3));
    }

//  Local
    
    protected void switchMotionForAllMotionStateChangeableNodes()		{ motionEnabled = !motionEnabled; setMotionForAllMotionStateChangeableNodes(motionEnabled); if (1<=ssVerbosity) { System.out.println("switchMotionForAllMotionStateChangeableNodes: " + motionEnabled); }}
    protected void setMotionForAllMotionStateChangeableNodes(boolean motional)	{ nodeObservableList.stream().filter((thisnode) -> (thisnode.isMotionStateChangeable())).forEach((thisnode2) ->	{thisnode2.setMotion(motional); motionEnabled = motional; setMotionLabelBold(motionEnabled); }); }
    private void enableMotionForAllStartingWithMotionNodes()			{ nodeObservableList.stream().filter((thisnode) -> (thisnode.isStartingWithMotion())).forEach((thisnode2) ->	{ thisnode2.enableMotion(); }); if (1<=ssVerbosity) { System.out.println("enableMotionForAllStartingWithMotionNodes"); } }
    private void disableMotionForAllNodes()					{ nodeObservableList.stream().forEach((thisnode) -> {thisnode.disableMotion(); }); motionEnabled = false; setMotionLabelBold(motionEnabled); if (1<=ssVerbosity) { System.out.println("disableMotionForAllNodes: " + motionEnabled); } }
    public void setSteppingForAllMotionStateChangeableNodes(boolean stepping)	{ nodeObservableList.stream().filter((thisnode) -> (thisnode.isMotionStateChangeable())).forEach((thisnode2) ->	{thisnode2.setStepping(stepping); }); if (1<=ssVerbosity) { System.out.println("setSteppingForAllMotionStateChangeableNodes: " + stepping); } }

    protected void switchShearForAllNodes()					{ nodeObservableList.stream().forEach((thisnode) ->								{thisnode.flipRotation();}); shearingEnabled = !shearingEnabled; setSceneShearBold(shearingEnabled); if (1<=ssVerbosity) { System.out.println("switchShearForAllNodes: " + shearingEnabled); }}
    protected void backupCoordinalsForAllNodes()				{ nodeObservableList.stream().forEach((thisnode) ->								{thisnode.backupCoordinalState(); }); if (1<=ssVerbosity) { System.out.println("backupCoordinalsForAllNodes"); }}
    protected void restoreCoordinalsForAllMotionStateChangeableNodes()		{ nodeObservableList.stream().filter((thisnode) -> (thisnode.isMotionStateChangeable())).filter((thisnode) -> (thisnode.isCoordinalRestorable())).forEach((thisnode2) ->	{thisnode2.restoreCoordinalState(); thisnode2.resetLocations(); }); node3d.displayAll(); if (1<=ssVerbosity) { System.out.println("restoreCoordinalsForAllMotionStateChangeableNodes"); } }

    protected void restoreCoordinalsForAllNodes()				{ nodeObservableList.stream().forEach((thisnode) ->								{thisnode.restoreCoordinalState(); thisnode.resetLocations(); }); node3d.displayAll(); if (1<=ssVerbosity) { System.out.println("restoreCoordinalsForAllNodes"); } }
    protected void setMotionFactorForAllMotionStateChangeableNodes()		{ nodeObservableList.stream().filter((thisnode) -> (thisnode.isMotionStateChangeable())).forEach((thisnode2) ->	{thisnode2.setMotionFactor(motionFactor); }); if (1<=ssVerbosity) { System.out.println("setMotionFactorForAllMotionStateChangeableNodes: " + motionFactor); } }
    protected void updateMotionRate2Motion(double param)			{ nodeObservableList.stream().forEach((thisnode) -> {thisnode.updateMotionRate2Motion(param); }); if (1<=ssVerbosity) { System.out.println("updateMotionRate2Motion: " + param); } }
//    protected void setGravityFactorForAllMotionStateChangeableNodes()		{ node3dArrayList.stream().filter((thisnode) -> (thisnode.isMotionStateChangeable())).forEach((thisnode2) ->	{thisnode2.setGravityFactor(gravityFactor); }); if (1<=ssVerbosity) { System.out.println("setGravityFactorForAllMotionStateChangeableNodes: " + gravityFactor); } }
    private void setCachingForAllNodes(boolean param)				{ nodeObservableList.stream().forEach((thisnode2) ->								{thisnode2.setCaching(param); }); if (1<=ssVerbosity) { System.out.println("setCachingForAllNodes: " + param); } }

    protected void switchGravitationalMotion()
    {
	gravityEnabled = !gravityEnabled; if (gravityEnabled) {sceneGravityLabel.setFont(getFont(Font.getDefault().getName(), FontWeight.BOLD, FontPosture.REGULAR, 10));} else {sceneGravityLabel.setFont(getFont(Font.getDefault().getName(), FontWeight.NORMAL, FontPosture.REGULAR, 10));}
	if (1<=ssVerbosity) { System.out.println("switchGravitationalMotion: " + gravityEnabled); }
    }

//    protected void switchRecording() // Videorecording
//    {
//	recording = !recording; if (recording)
//	{
//	    sceneRecordLabel.setFont(getFont(FontWeight.BOLD, FontPosture.REGULAR, 10));
//	    wImageList = new ArrayList<WritableImage>();
//	    WritableImage wimage = new WritableImage((int) subScene.getWidth(), (int) subScene.getHeight());
//	}
//	else // processing recordings
//	{
//	    sceneRecordLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10));
//	    
////	    Platform.runLater(() ->
////	    {
//                Thread thread1 = new Thread(new Runnable()
//                {
//                    @Override
//                    @SuppressWarnings({"static-access"})
//                    public void run()
//                    {
//			boolean failed = false;
//			int images = wImageList.size();
//			for (WritableImage image:wImageList)
//			{
//			    File file = new File("snapshot" + wimagecounter + ".png");
//			    try
//			    {
//				Platform.runLater(() ->
//				{
//					nodeDisplay.statusLabel.setText("Writing image: " + (wimagecounter + 1) + "-" + images);
//				});
//				ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file); wimagecounter++; 
//			    } catch (Exception s) { System.err.println("Writing images: " + s.getMessage()); failed = true;}
//			}
//			if (!failed) { Platform.runLater(() -> { nodeDisplay.statusLabel.setText("Writing " + images + " images completed"); }); }
//			wimagecounter = 0;
//                    }
//                });
//                thread1.setName("thread1");
//                thread1.setDaemon(true);
//                thread1.start();
////	    });
//	}
//    }

    protected void switchCollisionDetection()
    {
	collisionEnabled = !collisionEnabled; if (collisionEnabled) {sceneCollisionsLabel.setFont(getFont(Font.getDefault().getName(), FontWeight.BOLD, FontPosture.REGULAR, 10));} else {sceneCollisionsLabel.setFont(getFont(Font.getDefault().getName(), FontWeight.NORMAL, FontPosture.REGULAR, 10));}
	if (1<=ssVerbosity) { System.out.println("switchCollisionDetection: " + collisionEnabled); }
    }

    public void setSceneShearBold(boolean param)	{ if (param) {sceneShearLabel.setFont(getFont(Font.getDefault().getName(), FontWeight.BOLD, FontPosture.REGULAR, 10));} else {sceneShearLabel.setFont(getFont(Font.getDefault().getName(), FontWeight.NORMAL, FontPosture.REGULAR, 10));}}
    
    protected void keyPressedHandler(KeyEvent keyEvent) // Do not remove for polymorphic method in scenes
    {
	ArrayList<KeyCode> keycodes = new ArrayList<>( Arrays.asList(KeyCode.DIGIT1, KeyCode.DIGIT2, KeyCode.DIGIT3, KeyCode.DIGIT4, KeyCode.DIGIT5, KeyCode.DIGIT6, KeyCode.DIGIT7, KeyCode.DIGIT8, KeyCode.DIGIT9, KeyCode.DIGIT0));
	int nodeCounter = 0;
	for (KeyCode keycode:keycodes)
	{
	    if ( nodeCounter < nodeObservableList.size())
	    {
		if((keyEvent.getCode()==keycode) && (nodeObservableList.get(nodeCounter) != null))
		{
		    if(!keyEvent.isControlDown())   { switchControlNode(nodeObservableList.get(nodeCounter)); if (nodeObservableList.get(nodeCounter).isCamera())   { switchCameraNode((MyCamera) nodeObservableList.get(nodeCounter)); } }
		    else			    { switchControlNode(nodeObservableList.get(nodeCounter)); }
		}
	    } else { break; }
	    nodeCounter++;
	}
    }

    protected void keyReleasedHandler(KeyEvent keyEvent)
    {
    }

    protected void setupSubscene()
    {
	rootGroup = new Group();
	if (1<=ssVerbosity) {System.out.println("SubScene(rootGroup, " +mainstage.stage.getWidth() + ", " +mainstage.stage.getHeight()+ ", " + true + ", SceneAntialiasing.BALANCED)");}
	subScene = new SubScene(rootGroup, mainstage.stage.getWidth(), mainstage.stage.getHeight(), true, SceneAntialiasing.BALANCED);			// Create subscene with group
	subScene.setFill(Color.BLACK);
    }

    protected void setupSubsceneColor(Color color)
    {
	if (1<=ssVerbosity) {System.out.println("setupSubsceneColor(Color " + color.toString() + ")");}
	subScene.setFill(color);
    }

    protected void setupNodeDisplay()	{if (1<=ssVerbosity) {System.out.println("setupNodeDisplay()");} nodeDisplay = new NodeDisplay(this, true);}

    protected void setCameraNode(MyCamera param)
    {
	if (1<=ssVerbosity) { System.out.println("setCameraNode(" + param.getId()+ ")"); }
	cam = param;
	cam.getNode().setDisable(false);
	cam.setMenuItemUnderlined(true);
	cam.setViewCameraBold(true);
	cam.setVisibility(false);
	if (cam.autolight) { cam.setLight(true); }
	subScene.setCamera(cam.getNode());
	nodeDisplay.camNodeDisplay(cam.getId());
	cam.zoomDisplay();
    }

    protected void switchCameraNode(MyCamera param)
    {
//	if (1<=ssVerbosity) {System.out.println("switchCameraNode(MyCamera " + param.getId() + ")");} // catched at setCameraNode
	if (param != null)
	{
	    cam.setMenuItemUnderlined(false);
	    cam.setViewCameraBold(false);
	    cam.setVisibility(true);
	    if (cam.autolight) { cam.setLight(false); }
	    cam.getNode().setDisable(true);
	    cam.setDisable();
	    setCameraNode(param);
	}
    }

    protected void setControlNode(Node3D param)
    {
	if (1<=ssVerbosity) { System.out.println("setControlNode(" + param.getId()+ ")"); }
	node3d = param;
	node3d.setMenuItemBold(true);
	node3d.setControlNodeBold(true);
	node3d.setDisplaying();
	nodeDisplay.nodeLabel.setText("Control: " + node3d.getId());
	node3d.displayAll();
    }

    protected void switchControlNode(Node3D param)
    {
//	if (1<=ssVerbosity) {System.out.println("switchControlNode(Node3D " + param.getId() + ")");} // catched at setControlNode
	if (param != null)
	{
	    node3d.setMenuItemBold(false);
	    node3d.setControlNodeBold(false);
	    node3d.stopDisplaying();
	    setControlNode(param);
	}
    }

    // speed(meters/sec) d = distance(meter); p = period(mS)
    public double getSpeed(double d, double p) { return (d * (1000 / p)); }
    
    public  String getSceneId()		{return sceneNameId;}
    public  NodeDisplay getNodeDisplay()	{return nodeDisplay;}

    protected Rectangle createRect(double width, double height)
    {
	Rectangle rect = new Rectangle(0, 0, width, height);
	rect.setArcWidth(10);
	rect.setArcHeight(10);
	rect.setFill(Color.TRANSPARENT);
	rect.setStroke(Color.GREY);
	rect.setOpacity(1);
	return rect;
    }
    
    public SubScene getSubScene()	{return subScene;}
    public Group getRootGroup()		{return rootGroup;}
    public VBox getNodeListVBox()	{return nodeListVBox;}
    public double getRealFrameRate()	{return realFrameRate;}

//    protected Font getFont(String fontName, FontWeight fontweight, FontPosture postture, double size) { return Font.font(Font.getDefault().getName(), fontweight, postture, size); }
    protected Font getFont(String fontName, FontWeight fontweight, FontPosture postture, double size) { return Font.font(fontName, fontweight, postture, size); }
    
    protected double getMotionRate()				    { return motionRate; }
    protected void setMotionRate(double param)			    { lastMotionRate = motionRate; motionRate = param; }
    protected double getLastMotionRateFactor()			    { return motionRate / lastMotionRate;}

    protected double getMotionRateInterval()			    { return motionRateInterval; }
    protected void setMotionRateInterval(double motionRateInterval) { this.motionRateInterval = motionRateInterval; }
    public double getRunningTime()				    { return runningTime; }
}