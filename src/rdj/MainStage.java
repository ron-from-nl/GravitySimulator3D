package rdj;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.application.Preloader.ErrorNotification;
import javafx.application.Preloader.ProgressNotification;
import javafx.application.Preloader.StateChangeNotification;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import static javafx.application.Application.launch;

public class MainStage extends Application
{
    public	Stage			    stage;
    private	SceneDisplayController	    sceneDisplayController;
    protected	static SuperScene	    superscene;
    protected	EventHandler		    keyPressedHandler, keyReleasedHandler;
    private	StackPane		    stackPane;
    private	Scene			    scene;
    private     EventHandler<MouseEvent>    mouseClickHandler;
    protected   EventHandler<MouseEvent>    mouseMovedHandler;
    private     EventHandler<ScrollEvent>   mouseScrollHandler;
    private    	boolean			    editing;
    protected	static Path		    scenefile;
    private	int			    major, minor, update;
    private    	String			    version;
    private    	static int		    verbosity = 1;

    public void updatePreloaderProgress(double param)
    {
        notifyPreloader(new ProgressNotification(param));
    }

    @Override public void init() 
    {
	major = 2; minor = 0; update = 1; version = "v" + major + "." + minor + "." + update;
	notifyPreloader(new ErrorNotification("progress","show",new Throwable("")));
	notifyPreloader(new ErrorNotification("application","title",new Throwable("GravitySimulator3D " + version)));
        notifyPreloader(new Preloader.StateChangeNotification(StateChangeNotification.Type.BEFORE_INIT));
	notifyPreloader(new ErrorNotification("info","GravitySimulator3D",new Throwable("initializing...")));
	notifyPreloader(new ProgressNotification(0));

//        FileSystem defaultfs = FileSystems.getDefault(); // FS Object default SSD
//        Path usrdir = defaultfs.getPath(System.getProperty("user.dir"));
//        Path hmedir = defaultfs.getPath(System.getProperty("user.home"));
//        Path jarfile = Paths.get(usrdir.toString(), "GravitySimulator3D.jar"); // path = /home/ron/tmp/orig
//        FileSystem jarfs = null; try { jarfs = FileSystems.newFileSystem(jarfile, null); } catch (IOException ex) { System.err.println(ex); } // FS jar file
//        Path jarscenes = jarfs.getPath("rdj", "scenes");
//        Path jarresources = jarfs.getPath("rdj", "resources");
///        Path jarreadmesrc = jarfs.getPath("README.txt");
//        Path jarreadmedst = defaultfs.getPath(System.getProperty("user.dir", "README.txt"));
	
	updatePreloaderProgress(0.3); notifyPreloader(new ErrorNotification("info","GravitySimulator3D",new Throwable("copy scenes...")));
        
        Path productpath = Paths.get(System.getProperty("user.home"), SuperScene.PRODUCT); // home dir
//	scenefile = defaultfs.getPath(System.getProperty("user.dir"),SuperScene.PRODUCT,"rdj","scenes","default.scene");
        if (Files.notExists(productpath))
        {
            try {Files.createDirectory(productpath);}
            catch (IOException e) {notifyPreloader(new ErrorNotification("info","GravitySimulator3D",new Throwable("could not create: " + productpath)));}
        }
	
        try { MyNIO.copyTree(MyNIO.getJarFS().getPath("rdj", "scenes"),productpath); } catch (IOException ex) {  }
	updatePreloaderProgress(0.7); notifyPreloader(new ErrorNotification("info","GravitySimulator3D",new Throwable("copy resources...")));
	try { MyNIO.copyTree(MyNIO.getJarFS().getPath("rdj", "resources"),productpath); } catch (IOException ex)	{  }
//	if (Files.notExists(jarreadmedst, NOFOLLOW_LINKS)) { try { Files.copy(jarreadmesrc, jarreadmedst); } catch (IOException ex) { System.err.println(ex); } }
	
        notifyPreloader(new StateChangeNotification(StateChangeNotification.Type.BEFORE_START));
    }
    
    @Override public void start(Stage param) throws Exception 
    {
	// Send progress to preloader
	notifyPreloader(new ErrorNotification("info","GravitySimulator3D",new Throwable("starting...")));

	stage = param;
	stage.setTitle("GravitySimulator3D");
	stage.initStyle(StageStyle.UNIFIED);
	stage.setFullScreenExitKeyCombination(new KeyCodeCombination(KeyCode.ESCAPE, KeyCombination.SHIFT_DOWN));
	stage.setFullScreenExitHint("<Shift-Esc> exit");
	
	initStage();
	
	notifyPreloader(new ErrorNotification("info","GravitySimulator3D",new Throwable("loading SceneDisplay.fxml...")));
	
	// Loading the FXML SceneDisplayController
	FXMLLoader fxmlLoader = new FXMLLoader();
	InputStream inputStream = getClass().getResourceAsStream("SceneDisplay.fxml");
	fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
	fxmlLoader.setLocation(getClass().getResource("SceneDisplay.fxml"));
	Group group; try { group = (Group) fxmlLoader.load(inputStream); } finally { inputStream.close(); } // It does not work without this line
	
	//1
	try { sceneDisplayController = (SceneDisplayController) (Initializable) fxmlLoader.getController(); } catch (Exception ex) { System.err.println(ex.getMessage()); }
	sceneDisplayController.fpsVBox.setVisible(false);

	//2
	superscene = new LoadScene(this, scenefile);

	notifyPreloader(new ErrorNotification("info","GravitySimulator3D",new Throwable("Loaded: " + superscene.getSceneId())));

	//3
	sceneDisplayController.setApp(this); // No default constructor, just this construction!

	notifyPreloader(new ErrorNotification("info","GravitySimulator3D",new Throwable(" creating: UI EventListeners")));
	setupEvents();
	
	setStageScene(superscene);
	
	notifyPreloader(new ProgressNotification(1)); notifyPreloader(new ErrorNotification("progress","hide",new Throwable("")));
    }
    
    private void initStage()
    {
	stage.setX(Screen.getPrimary().getBounds().getMinX());
	stage.setY(Screen.getPrimary().getBounds().getMinY());
	stage.setWidth(Screen.getPrimary().getBounds().getMaxX());
	stage.setHeight(Screen.getPrimary().getBounds().getMaxY());
//	stage.setFullScreen(true);
    }
    
    private void setStageScene(SuperScene param)
    {
	superscene = param;
	if (1<=superscene.ssVerbosity) { System.out.println("setStageScene((" + param.getSceneId()+ ")"); }
	sceneDisplayController.distributeMotionFactor();
	if (superscene.nodeList.isEmpty())   {stackPane = new StackPane(superscene.getSubScene(), sceneDisplayController.getRootGroup());}
	else					    {stackPane = new StackPane(superscene.getSubScene(), sceneDisplayController.getRootGroup(), superscene.nodeDisplay.getRootGroup());}
	sceneDisplayController.showDisplay(superscene.startsWithDisplays);
	superscene.nodeDisplay.showDisplay(superscene.startsWithDisplays);
	superscene.setMotionForAllMotionStateChangeableNodes(superscene.startsWithMotion);
	scene = new Scene(stackPane);
	stage.setScene(scene);
	stage.setFullScreen(true);
	if (!stage.isShowing()) stage.show();
    }

    private void clearStageScene()
    {
	disableMouseMovement();
//	stage.hide();
	scene = null;
	stackPane.getChildren().removeAll();
	stackPane = null;
	superscene.stopScene();
	superscene.getRootGroup().getChildren().removeAll();
	superscene.nodeList.clear();
	superscene.subScene = null;
	superscene = null;
	System.gc();
    }

    private void setupEvents()
    {
	// Stage Events
	if (1<=superscene.ssVerbosity) { System.out.println("setupEvents()"); }
	stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, (WindowEvent window) ->	{ System.exit(0); });

	keyPressedHandler = (EventHandler<KeyEvent>) (KeyEvent keyEvent) -> { keyPressedHandler(keyEvent); };
	keyReleasedHandler = (EventHandler<KeyEvent>) (KeyEvent keyEvent) -> { keyReleasedHandler(keyEvent); };
	
	stage.addEventFilter(KeyEvent.KEY_PRESSED, keyPressedHandler);
	stage.addEventFilter(KeyEvent.KEY_RELEASED, keyReleasedHandler);

	// Scene
	mouseClickHandler = (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> 
	{
//	    pickResult = mouseEvent.getPickResult();
//	    if ((pickResult!=null) && ( pickResult.getIntersectedNode()!=null ) && (pickResult.getIntersectedNode().getId() !=null))
//	    {
//		if ( pickResult.getIntersectedNode().getId().equals("moon1") ) { switchControlNode(moon1); }
//		if ( pickResult.getIntersectedNode().getId().equals("moon2") ) { switchControlNode(moon2); }
//		if ( pickResult.getIntersectedNode().getId().equals("moon3") ) { switchControlNode(moon3); }
//		if ( pickResult.getIntersectedNode().getId().equals("earth") ) { switchControlNode(earth); }
//		
//		getNodeDisplay().nodeLabel.setText(pickResult.getIntersectedNode().getId());
//	    } else { switchControlNode(cam); }
	    if (superscene.node3d != null) { stage.addEventFilter(MouseEvent.MOUSE_MOVED, mouseMovedHandler); } // Depends on clicked, which in turn depends on scene specific nodes
	    if (superscene.node3d != null) { stage.addEventFilter(ScrollEvent.SCROLL, mouseScrollHandler); }
	    if (superscene.node3d != null) { superscene.node3d.resetMousePointerOffset(mouseEvent); }
	    superscene.subScene.setCursor(Cursor.NONE);
	};
	
	mouseMovedHandler = (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { superscene.node3d.mouseRotateMotion(mouseEvent); };
	mouseScrollHandler = (EventHandler<ScrollEvent>) (ScrollEvent scrollEvent) -> { superscene.node3d.mouseScroll(scrollEvent);};	    

	stage.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseClickHandler);
	if (superscene.node3d != null) { stage.addEventFilter(ScrollEvent.SCROLL, mouseScrollHandler); }
    }

    protected void switch2LoadScene(Path path) { scenefile = path; try{ clearStageScene(); setStageScene(new LoadScene(this,scenefile));} catch(Exception ex) { System.err.println(ex.getMessage()); } }
    
    private void keyPressedHandler(KeyEvent keyEvent)
    {
	if (! isEditing())
	{
	    // SuperScene Key Events

	    if (keyEvent.getCode() == KeyCode.P)		{ superscene.print(); }

	    if (keyEvent.getCode() == KeyCode.ENTER)		{superscene.switchMotionForAllMotionStateChangeableNodes(); }

	    if (keyEvent.getCode() == KeyCode.ESCAPE)		{disableMouseMovement(); }
	    if (keyEvent.getCode() == KeyCode.CLOSE_BRACKET)	{superscene.cam.switchOnZoomIn();}
	    if (keyEvent.getCode() == KeyCode.OPEN_BRACKET)	{superscene.cam.switchOnZoomOut();}
	    if (keyEvent.getCode() == KeyCode.SPACE)		{sceneDisplayController.switchAnimateDisplay(); if (superscene.getNodeDisplay() != null) { superscene.getNodeDisplay().switchAnimateDisplay();}}

	    if (keyEvent.getCode() == KeyCode.Q)		{superscene.node3d.switchOnMainEnginePropulsion();}
	    if (keyEvent.getCode() == KeyCode.W)		{superscene.node3d.switchOnForwardPropulsion();}
	    if (keyEvent.getCode() == KeyCode.E)		{superscene.node3d.switchOnUpwardPropulsion();}
	    if (keyEvent.getCode() == KeyCode.A)			{superscene.node3d.switchOnLeftPropulsion();}
	    if (keyEvent.getCode() == KeyCode.S)		{superscene.node3d.stopTranslation();}
	    if (keyEvent.getCode() == KeyCode.D)		{superscene.node3d.switchOnRightPropulsion();}
	    if (keyEvent.getCode() == KeyCode.X)		{superscene.node3d.switchOnBackwardPropulsion();}
	    if (keyEvent.getCode() == KeyCode.C)		{superscene.node3d.switchOnDownwardPropulsion();}

	    if (keyEvent.getCode() == KeyCode.DOWN)		{superscene.node3d.switchOnTiltUp();}
	    if (keyEvent.getCode() == KeyCode.UP)		{superscene.node3d.switchOnTiltDown();}
	    if (keyEvent.getCode() == KeyCode.LEFT)		{superscene.node3d.switchOnPanLeft();}
	    if (keyEvent.getCode() == KeyCode.RIGHT)		{superscene.node3d.switchOnPanRight();}

	    if (keyEvent.getCode() == KeyCode.M)		{superscene.node3d.scaleDown();}
	    if (keyEvent.getCode() == KeyCode.COMMA)		{superscene.node3d.switchOnTiltUp();}
	    if (keyEvent.getCode() == KeyCode.PERIOD)		{superscene.node3d.scaleUp();}
	    if (keyEvent.getCode() == KeyCode.J)			{superscene.node3d.switchOnPanLeft();}
	    if (keyEvent.getCode() == KeyCode.K)		{superscene.node3d.stopRotation();}
	    if (keyEvent.getCode() == KeyCode.L)			{superscene.node3d.switchOnPanRight();}
	    if (keyEvent.getCode() == KeyCode.U)		{superscene.node3d.switchOnRollLeft();}
	    if (keyEvent.getCode() == KeyCode.I)		{superscene.node3d.switchOnTiltDown();}
	    if (keyEvent.getCode() == KeyCode.O)		{superscene.node3d.switchOnRollRight();}
	    if (keyEvent.getCode() == KeyCode.TAB)		{superscene.restoreCoordinalsForAllMotionStateChangeableNodes();}

	    superscene.keyPressedHandler(keyEvent);
	}
	else
	{
	    //System.out.println("Blocked "+ keyEvent.getCode().getName() + " event editing = " + this.editing);
	}
    }
    
    private void keyReleasedHandler(KeyEvent keyEvent)
    {
	if (! isEditing())
	{
	    if (keyEvent.getCode() == KeyCode.CLOSE_BRACKET)	{superscene.cam.switchOffZoomIn();}
	    if (keyEvent.getCode() == KeyCode.OPEN_BRACKET)	{superscene.cam.switchOffZoomOut();}
	    
	    if (keyEvent.getCode() == KeyCode.Q)		{superscene.node3d.switchOffMainEnginePropulsion();}
	    if (keyEvent.getCode() == KeyCode.W)		{superscene.node3d.switchOffForwardPropulsion();}
	    if (keyEvent.getCode() == KeyCode.E)		{superscene.node3d.switchOffUpwardPropulsion();}
	    if (keyEvent.getCode() == KeyCode.A)			{superscene.node3d.switchOffLeftPropulsion();}
	    if (keyEvent.getCode() == KeyCode.D)		{superscene.node3d.switchOffRightPropulsion();}
	    if (keyEvent.getCode() == KeyCode.X)		{superscene.node3d.switchOffBackwardPropulsion();}
	    if (keyEvent.getCode() == KeyCode.C)		{superscene.node3d.switchOffDownwardPropulsion();}

	    if (keyEvent.getCode() == KeyCode.DOWN)		{superscene.node3d.switchOffTiltUp();}
	    if (keyEvent.getCode() == KeyCode.UP)		{superscene.node3d.switchOffTiltDown();}
	    if (keyEvent.getCode() == KeyCode.LEFT)		{superscene.node3d.switchOffPanLeft();}
	    if (keyEvent.getCode() == KeyCode.RIGHT)		{superscene.node3d.switchOffPanRight();}
	    
	    if (keyEvent.getCode() == KeyCode.COMMA)		{superscene.node3d.switchOffTiltUp();}
	    if (keyEvent.getCode() == KeyCode.J)			{superscene.node3d.switchOffPanLeft();}
	    if (keyEvent.getCode() == KeyCode.L)    		{superscene.node3d.switchOffPanRight();}
	    if (keyEvent.getCode() == KeyCode.U)		{superscene.node3d.switchOffRollLeft();}
	    if (keyEvent.getCode() == KeyCode.I)		{superscene.node3d.switchOffTiltDown();}
	    if (keyEvent.getCode() == KeyCode.O)		{superscene.node3d.switchOffRollRight();}
	}
    }

//    public ArrayList getScenIdArrayList()			{return sceneIdArrayList;}
//    public ObservableList<String> getSceneIdObservableList()	{return sceneIdObservableList;}

    public boolean isEditing()					{return editing;}
    
    // While SceneDispController is editing the double parallel enter event needs to be ignored by stage, which is why editing is set with 100 mSec delay
    public void setEditing(boolean param)			{ if (param) {disableMouseMovement();} new Timer().schedule(new TimerTask() { @Override public void run() { editing = param; }}, 100);}

    public void disableMouseMovement()				{stage.removeEventFilter(MouseEvent.MOUSE_MOVED, mouseMovedHandler); superscene.subScene.setCursor(Cursor.DEFAULT);}
    public  SceneDisplayController getSceneDisplayController()  {return sceneDisplayController;}

    public static void main(String[] args)			
    {
	FileSystem defaultfs = FileSystems.getDefault(); // gravity.scene, iss.scene, moon.scene, home.scene, paris.scene, proton.scene, text.scene
//	scenefile = defaultfs.getPath(System.getProperty("user.dir"),SuperScene.PRODUCT,"rdj","scenes","default.scene");
	scenefile = defaultfs.getPath(System.getProperty("user.home"),SuperScene.PRODUCT,"rdj","scenes","default.scene");
//	if (args.length>0){scenefile = defaultfs.getPath(System.getProperty("user.dir"),SuperScene.PRODUCT,args[0]);}
	if (args.length>0){scenefile = defaultfs.getPath(System.getProperty("user.home"),SuperScene.PRODUCT,args[0]);}
	launch(args);
    }
}