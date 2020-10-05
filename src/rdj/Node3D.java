package rdj;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;

public class Node3D
{
    protected String id;

    private final Rotate rxRotate;
    private final Rotate ryRotate;
    private final Rotate rzRotate;
    
    protected Group nodeGroup;
    protected Group trailGroup;
    protected Group hyperSphereGroup;
    protected Group rotYGroup;
    protected Group rotXGroup;
    protected Group rotZGroup;
    protected Group moveGroup;
    protected Group rootGroup;
    
    private static double mouseMovedXLast, mouseMovedX, rotateXThrottle, mouseMovedYLast, mouseMovedY, rotateYThrottle = 0; // Same value on all instances
    private static final double rotatePanThrottleFactor = 0.1; // (de)amplify mouse movement interupts
    private static final double rotateTiltThrottleFactor = 0.1; // (de)amplify mouse movement interupts
    
    private boolean scaleUp,scaleDown,tiltUp,tiltDown,panLeft,panRight,rollLeft,rollRight,mainEnginePropulsion,
		    forwardPropulsion,backwardPropulsion,leftPropulsion,rightPropulsion,upwardPropulsion,downwardPropulsion;
    private Timer   scaleUpTimer, scaleDownTimer, tiltUpTimer, tiltDownTimer, panLeftTimer, panRightTimer, rollLeftTimer, rollRightTimer,
		    mainEngineTimer, forwardPropulsionTimer,backwardPropulsionTimer,leftPropulsionTimer,rightPropulsionTimer,upwardPropulsionTimer,downwardPropulsionTimer;

    protected static final long keyTimerInterval = 100;
    protected static final double keyStrokesPerSecond = 1000 / keyTimerInterval;

    private static final double scaleStep = 1.01;
    private static final double mainEngineThottleStep = 0.01;
    private static final double translateEngineThrottleStep = 0.01;
    private static final double rotateEngineThrottleStep = 0.01;
    
    private double mainEngineThrottle = 0;
    private double forwardThrottle = 0;
    private double backwardThrottle = 0;
    private double leftThrottle = 0;
    private double rightThrottle = 0;
    private double upThrottle = 0;
    private double downThrottle = 0;
    
    private double tiltUpThrottle = 0;
    private double tiltDownThrottle = 0;
    private double panLeftThrottle = 0;
    private double panRightThrottle = 0;
    private double rollLeftThrottle = 0;
    private double rollRightThrottle = 0;
    
    protected double mainEnginePower = 0;			    // KGF
    protected double translateEnginePower = 0;		    // KGF
    protected double rotateEnginePower = 0;		    // KGF
    
    protected	boolean		camera;
    protected	boolean		light;
    protected	boolean		autolight = true;
    protected	boolean		displaying;			// Being used to display
    protected	static String	precision;
    private	boolean		shearing = false; // 0=outer 1=inner
    protected	SuperScene	superscene;
    
    protected	Celestial	celestial;
    protected	Celestial	hyperSphereCelestial;
    protected	boolean		motionStateChangeable = false; // Only these nodes can be paused or continued their motion
    protected	boolean		startingWithMotion = false; // Only these nodes start with motion
    protected	boolean		caching = false; // Used execute motion in single steps by keystroke
    private	boolean		steppingEnabled = false; // Used execute motion in single steps by keystroke
    private	boolean		motionEnabled = false; // Used to pause motionupdates of nodes
    private	boolean		nodeTranslated = false; // Used to pause motionupdates of nodes

    private	Coordinal	coordinal; // Operational Coordinal State
    private	Coordinal	bupCoordinal; // Backup Coordinal State
    private	Point3D		location, lastLocation;
    protected	boolean		floating = true; // Floating means: does not stop on motionStateChanges
    protected	double		motionFactor = 1; // speed up or or slow down motion
//    protected	double		gravityFactor = 1; // speed up or or slow down motion
//    private	Task		transitionsHandlerTask;
    private     HBox		nodeIdMenuItemHBox;
    private     VBox		nodeFuntionsVBox;
//    private     Rectangle	nodeMenuItemRect;
    private     Label		nodeIdMenuItemLabel;
//    private     StackPane	nodeMenuItemStackPane;

    private     Label		viewCameraLabel;
    private     Label		switchLightLabel;
    private     Label		controlNodeLabel;
    private     Label		motionLabel;
    private     Label		steppingLabel;
    private     Label		switchGravityLabel;
    private     Label		switchCollisionsLabel;
    private     Label		switchShearLabel;
    private     Label		backupCoordinalLabel;
    protected   Label		nodeZoomInLabel;
    protected   Label		nodeZoomOutLabel;
    protected   Label		visibleLabel;
    protected   Label		dropSphereTrailLabel;
    protected   Label		createHyperSphereLabel;
    protected   Label		dropHyperSphereLabel;
    protected   Label		cachingLabel;
    
    protected	double		magnifyFactor = 1; // Just for MyCamera zoomed rotation damping
    
    private	Timer		dropSphereTrailTimer;
    protected	boolean		dropTrail = false;
    protected	long		dropTrailInterval = 100l;
    protected	double		trailRadiusPercentage = 10d;
    protected	int		trailDivisions = 16;
    protected	String		trailColor;
    protected	String		trailCullFace;
    protected	String		trailDrawMode;
    protected	double		trailOpacity = 1d;
    protected	int		trailLength = 0;
    
    private	int		shells = 1;
    private	double		shellRadius = 0;
    private	double		shellResolution = 10;
    private	double		shellMotion = 0;
    private	double		randomise = 0;
    
    private	Timer		dropHyperSphereTimer;
    private	boolean		dropHyperSphereTimerOn = false;
    protected	long		dropHyperSphereInterval = 8000l;
    protected	double		hyperSphereRadiusPercentage = 10d;
    protected	int		hyperSphereDivisions = 16;
    protected	String		hyperSphereColor;
    protected	String		hyperSphereCullFace;
    protected	String		hyperSphereDrawMode;
    protected	double		hyperSphereOpacity = 1d;
    protected	int		hyperSphereLength = 0;
    
    private	boolean		onMenu = true;
    private	boolean		coordinalRestorable = true;
    protected	int		verbosity = 0;
    private	double		birth = -1d;
    private	double		lifetime = -1d; // Seconds for object to live
    private	double		hyperSphereLifetime = -1d; // Seconds for object to live
    private	double		death = -1d;
    private ArrayList<MySphere> prototypeArrayList;

    public Node3D()
    {
	coordinal = new Coordinal();
	bupCoordinal = new Coordinal();
	
	precision = "%.1f";

	nodeGroup = new Group();
	trailGroup = new Group();
	hyperSphereGroup = new Group();
	rotZGroup = new Group(nodeGroup);
	rotXGroup = new Group(rotZGroup);
	rotYGroup = new Group(rotXGroup);
	moveGroup = new Group(rotYGroup);
	rootGroup = new Group(moveGroup); rootGroup.setVisible(false);
	rootGroup.getChildren().add(trailGroup);
	
	rxRotate = new Rotate(0, Rotate.X_AXIS);
	ryRotate = new Rotate(0, Rotate.Y_AXIS);
	rzRotate = new Rotate(0, Rotate.Z_AXIS);

	setTransforms();
	
	resetLocations();
	
	trailColor = "GREY";
	trailCullFace = "BACK";
	trailDrawMode = "FILL";

//	scoopedNode3dArrayList = new ArrayList<>();
    }

    protected final void setLifeCycle(double birth, double lifetime) { this.birth = birth; this.lifetime = lifetime; if (this.lifetime > -1.0) { this.death = (birth + lifetime);} }
    public final void resetLocations()
    {
	location =	new Point3D(coordinal.getTxProp().get(),coordinal.getTyProp().get(),coordinal.getTzProp().get());
	lastLocation =	new Point3D(coordinal.getTxProp().get(),coordinal.getTyProp().get(),coordinal.getTzProp().get());
//	location =	new Point3D(moveGroup.localToScene(Point3D.ZERO).getX(),moveGroup.localToScene(Point3D.ZERO).getY(),moveGroup.localToScene(Point3D.ZERO).getZ());
//	lastLocation =	new Point3D(moveGroup.localToScene(Point3D.ZERO).getX(),moveGroup.localToScene(Point3D.ZERO).getY(),moveGroup.localToScene(Point3D.ZERO).getZ());
    }
    
    public void createMenu()
    {
//	if (onMenu)
//	{
	    // Single NodeMenuId at frontside 
	    Rectangle nodeIdMenuItemRect = createRect(110, 15);
	    nodeIdMenuItemLabel = new Label(id); nodeIdMenuItemLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10));
	    nodeIdMenuItemLabel.setTextFill(Paint.valueOf("WHITE")); nodeIdMenuItemLabel.setTextAlignment(TextAlignment.CENTER);

	    StackPane nodeIdMenuItemStackPane = new StackPane(nodeIdMenuItemLabel, nodeIdMenuItemRect); nodeIdMenuItemStackPane.setPrefWidth(80);
	    nodeIdMenuItemStackPane.setPrefHeight(20); nodeIdMenuItemStackPane.setAlignment(Pos.CENTER);

	    nodeIdMenuItemHBox = new HBox(nodeIdMenuItemStackPane); nodeIdMenuItemHBox.setPrefWidth(130); nodeIdMenuItemHBox.setPrefHeight(20); nodeIdMenuItemHBox.setAlignment(Pos.CENTER);

	    // create the nodeMenuItemHBox EventFilters
	    nodeIdMenuItemRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { nodeIdMenuItemRect.setFill(Color.rgb(255,255,255,0.5)); });
	    nodeIdMenuItemRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) ->
	    {
		superscene.mainstage.getSceneDisplayController().disableMouseMovement(); 
		superscene.flipNodeSectionDisplayToBack(nodeFuntionsVBox);
	    });
	    nodeIdMenuItemRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { nodeIdMenuItemRect.setFill(Color.rgb(0,0,0,0.0)); });

    //	==========================================================================================================================================================	

	    // create nodeVBox for backside
	    nodeFuntionsVBox = new VBox(); nodeFuntionsVBox.setPrefWidth(130); nodeFuntionsVBox.setPrefHeight(228);

	    Label scenesEmptyLabel = new Label(""); scenesEmptyLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 11)); scenesEmptyLabel.setTextFill(Paint.valueOf("WHITE")); scenesEmptyLabel.setAlignment(Pos.CENTER);
	    HBox scenesEmptyHBox = new HBox(scenesEmptyLabel); scenesEmptyHBox.setPrefWidth(130); scenesEmptyHBox.setPrefHeight(11); scenesEmptyHBox.setAlignment(Pos.CENTER);
	    nodeFuntionsVBox.getChildren().add(scenesEmptyHBox);

	    Label titleLabel = new Label(id); titleLabel.setFont(getFont(FontWeight.BOLD, FontPosture.REGULAR, 11)); titleLabel.setTextFill(Paint.valueOf("WHITE")); titleLabel.setAlignment(Pos.CENTER);
	    HBox titleHBox = new HBox(titleLabel); titleHBox.setPrefWidth(130); titleHBox.setPrefHeight(20); titleHBox.setAlignment(Pos.CENTER);
	    nodeFuntionsVBox.getChildren().add(titleHBox);

	    // Control
	    Rectangle controlNodeRect = createRect(110, 15);
	    controlNodeLabel = new Label("Controlled c-nr"); controlNodeLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); controlNodeLabel.setTextFill(Paint.valueOf("WHITE")); controlNodeLabel.setTextAlignment(TextAlignment.CENTER);
	    StackPane controlNodeStackPane = new StackPane(controlNodeLabel, controlNodeRect); controlNodeStackPane.setPrefWidth(80); controlNodeStackPane.setPrefHeight(20); controlNodeStackPane.setAlignment(Pos.CENTER);
	    HBox controlNodeHBox = new HBox(controlNodeStackPane); controlNodeHBox.setPrefWidth(130); controlNodeHBox.setPrefHeight(20); controlNodeHBox.setAlignment(Pos.CENTER);

	    controlNodeRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { controlNodeRect.setFill(Color.rgb(255,255,255,0.5)); });
	    controlNodeRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { superscene.switchControlNode(this); superscene.mainstage.getSceneDisplayController().disableMouseMovement(); });
	    controlNodeRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { controlNodeRect.setFill(Color.rgb(0,0,0,0.0)); });

	    nodeFuntionsVBox.getChildren().add(controlNodeHBox);

	    if (isCamera())
	    {
		Rectangle viewCameraRect = createRect(110, 15);
		viewCameraLabel = new Label("Monitor nr"); viewCameraLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); viewCameraLabel.setTextFill(Paint.valueOf("WHITE")); viewCameraLabel.setTextAlignment(TextAlignment.CENTER);
		StackPane viewCameraStackPane = new StackPane(viewCameraLabel, viewCameraRect); viewCameraStackPane.setPrefWidth(80); viewCameraStackPane.setPrefHeight(20); viewCameraStackPane.setAlignment(Pos.CENTER);
		HBox viewCameraHBox = new HBox(viewCameraStackPane); viewCameraHBox.setPrefWidth(130); viewCameraHBox.setPrefHeight(20); viewCameraHBox.setAlignment(Pos.CENTER);

		viewCameraRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { viewCameraRect.setFill(Color.rgb(255,255,255,0.5)); });
		viewCameraRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> 
		{ superscene.switchCameraNode((MyCamera) this); superscene.switchControlNode(this); superscene.mainstage.disableMouseMovement(); superscene.flipNodeSectionDisplayToFront(superscene.getNodeListVBox()); });

		viewCameraRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { viewCameraRect.setFill(Color.rgb(0,0,0,0.0)); });

		nodeFuntionsVBox.getChildren().add(viewCameraHBox);

		Rectangle nodeZoomInRect = createRect(110, 15);
		nodeZoomInLabel = new Label("Zoom In ]"); nodeZoomInLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); nodeZoomInLabel.setTextFill(Paint.valueOf("WHITE")); nodeZoomInLabel.setTextAlignment(TextAlignment.CENTER);
		StackPane nodeZoomInStackPane = new StackPane(nodeZoomInLabel, nodeZoomInRect); nodeZoomInStackPane.setPrefWidth(80); nodeZoomInStackPane.setPrefHeight(20); nodeZoomInStackPane.setAlignment(Pos.CENTER);
		HBox nodeZoomInHBox = new HBox(nodeZoomInStackPane); nodeZoomInHBox.setPrefWidth(130); nodeZoomInHBox.setPrefHeight(20); nodeZoomInHBox.setAlignment(Pos.CENTER);

		nodeZoomInRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { nodeZoomInRect.setFill(Color.rgb(255,255,255,0.5)); });
		nodeZoomInRect.addEventFilter(MouseEvent.MOUSE_PRESSED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { switchOnZoomIn(); superscene.mainstage.disableMouseMovement(); });
		nodeZoomInRect.addEventFilter(MouseEvent.MOUSE_RELEASED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { switchOffZoomIn(); });
		nodeZoomInRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { nodeZoomInRect.setFill(Color.rgb(0,0,0,0.0)); });

		nodeFuntionsVBox.getChildren().add(nodeZoomInHBox);

		Rectangle nodeZoomOutRect = createRect(110, 15);
		nodeZoomOutLabel = new Label("Zoom Out ["); nodeZoomOutLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); nodeZoomOutLabel.setTextFill(Paint.valueOf("WHITE")); nodeZoomOutLabel.setTextAlignment(TextAlignment.CENTER);
		StackPane nodeZoomOutStackPane = new StackPane(nodeZoomOutLabel, nodeZoomOutRect); nodeZoomOutStackPane.setPrefWidth(80); nodeZoomOutStackPane.setPrefHeight(20); nodeZoomOutStackPane.setAlignment(Pos.CENTER);
		HBox nodeZoomOutHBox = new HBox(nodeZoomOutStackPane); nodeZoomOutHBox.setPrefWidth(130); nodeZoomOutHBox.setPrefHeight(20); nodeZoomOutHBox.setAlignment(Pos.CENTER);

		nodeZoomOutRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { nodeZoomOutRect.setFill(Color.rgb(255,255,255,0.5)); });
		nodeZoomOutRect.addEventFilter(MouseEvent.MOUSE_PRESSED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { switchOnZoomOut(); superscene.mainstage.disableMouseMovement(); });
		nodeZoomOutRect.addEventFilter(MouseEvent.MOUSE_RELEASED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { switchOffZoomOut(); });
		nodeZoomOutRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { nodeZoomOutRect.setFill(Color.rgb(0,0,0,0.0)); });

		nodeFuntionsVBox.getChildren().add(nodeZoomOutHBox);
	    }

	    if (isLight())
	    {
		Rectangle switchLightRect = createRect(110, 15);
		switchLightLabel = new Label("Light On"); switchLightLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); switchLightLabel.setTextFill(Paint.valueOf("WHITE")); switchLightLabel.setTextAlignment(TextAlignment.CENTER);
		StackPane switchLightStackPane = new StackPane(switchLightLabel, switchLightRect); switchLightStackPane.setPrefWidth(80); switchLightStackPane.setPrefHeight(20); switchLightStackPane.setAlignment(Pos.CENTER);
		HBox switchLightHBox = new HBox(switchLightStackPane); switchLightHBox.setPrefWidth(130); switchLightHBox.setPrefHeight(20); switchLightHBox.setAlignment(Pos.CENTER);

		switchLightRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { switchLightRect.setFill(Color.rgb(255,255,255,0.5)); });
		switchLightRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { switchLight(); superscene.mainstage.getSceneDisplayController().disableMouseMovement(); });
		switchLightRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) ->  { switchLightRect.setFill(Color.rgb(0,0,0,0.0)); });

		nodeFuntionsVBox.getChildren().add(switchLightHBox);

		Rectangle dropPLightRect = createRect(110, 15);
		Label dropPLightLabel = new Label("Drop PLight"); dropPLightLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); dropPLightLabel.setTextFill(Paint.valueOf("WHITE")); dropPLightLabel.setTextAlignment(TextAlignment.CENTER);
		StackPane dropPLightStackPane = new StackPane(dropPLightLabel, dropPLightRect); dropPLightStackPane.setPrefWidth(80); dropPLightStackPane.setPrefHeight(20); dropPLightStackPane.setAlignment(Pos.CENTER);
		HBox dropPLightHBox = new HBox(dropPLightStackPane); dropPLightHBox.setPrefWidth(130); dropPLightHBox.setPrefHeight(20); dropPLightHBox.setAlignment(Pos.CENTER);

		dropPLightRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { dropPLightRect.setFill(Color.rgb(255,255,255,0.5)); });
		dropPLightRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { dropPLight(); superscene.mainstage.getSceneDisplayController().disableMouseMovement(); });
		dropPLightRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { dropPLightRect.setFill(Color.rgb(0,0,0,0.0)); });

		nodeFuntionsVBox.getChildren().add(dropPLightHBox);

		Rectangle dropALightRect = createRect(110, 15);
		Label dropALightLabel = new Label("Drop ALight"); dropALightLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); dropALightLabel.setTextFill(Paint.valueOf("WHITE")); dropALightLabel.setTextAlignment(TextAlignment.CENTER);
		StackPane dropALightStackPane = new StackPane(dropALightLabel, dropALightRect); dropALightStackPane.setPrefWidth(80); dropALightStackPane.setPrefHeight(20); dropALightStackPane.setAlignment(Pos.CENTER);
		HBox dropALightHBox = new HBox(dropALightStackPane); dropALightHBox.setPrefWidth(130); dropALightHBox.setPrefHeight(20); dropALightHBox.setAlignment(Pos.CENTER);

		dropALightRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { dropALightRect.setFill(Color.rgb(255,255,255,0.5)); });
		dropALightRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { dropALight(); superscene.mainstage.getSceneDisplayController().disableMouseMovement(); });
		dropALightRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { dropALightRect.setFill(Color.rgb(0,0,0,0.0)); });

		nodeFuntionsVBox.getChildren().add(dropALightHBox);
	    }

	    // Drop Sphere
	    Rectangle dropSphereRect = createRect(110, 15);
	    Label dropSphereLabel = new Label("Drop Sphere"); dropSphereLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); dropSphereLabel.setTextFill(Paint.valueOf("WHITE")); dropSphereLabel.setTextAlignment(TextAlignment.CENTER);
	    StackPane dropSphereStackPane = new StackPane(dropSphereLabel, dropSphereRect); dropSphereStackPane.setPrefWidth(80); dropSphereStackPane.setPrefHeight(20); dropSphereStackPane.setAlignment(Pos.CENTER);
	    HBox dropSphereHBox = new HBox(dropSphereStackPane); dropSphereHBox.setPrefWidth(130); dropSphereHBox.setPrefHeight(20); dropSphereHBox.setAlignment(Pos.CENTER);

	    dropSphereRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { dropSphereRect.setFill(Color.rgb(255,255,255,0.5)); });
	    dropSphereRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { dropSphere(); superscene.mainstage.disableMouseMovement(); });
	    dropSphereRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { dropSphereRect.setFill(Color.rgb(0,0,0,0.0)); });

	    nodeFuntionsVBox.getChildren().add(dropSphereHBox);

	    // Drop Trail
	    Rectangle dropSphereTrailRect = createRect(110, 15);
	    dropSphereTrailLabel = new Label("Drop Spheres"); dropSphereTrailLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); dropSphereTrailLabel.setTextFill(Paint.valueOf("WHITE")); dropSphereTrailLabel.setTextAlignment(TextAlignment.CENTER);
	    StackPane dropSphereTrailStackPane = new StackPane(dropSphereTrailLabel, dropSphereTrailRect); dropSphereTrailStackPane.setPrefWidth(80); dropSphereTrailStackPane.setPrefHeight(20); dropSphereTrailStackPane.setAlignment(Pos.CENTER);
	    HBox dropSphereTrailHBox = new HBox(dropSphereTrailStackPane); dropSphereTrailHBox.setPrefWidth(130); dropSphereTrailHBox.setPrefHeight(20); dropSphereTrailHBox.setAlignment(Pos.CENTER);

	    dropSphereTrailRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { dropSphereTrailRect.setFill(Color.rgb(255,255,255,0.5)); });
	    dropSphereTrailRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { switchDropSphereTrail(); superscene.mainstage.disableMouseMovement(); });
	    dropSphereTrailRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { dropSphereTrailRect.setFill(Color.rgb(0,0,0,0.0)); });

	    nodeFuntionsVBox.getChildren().add(dropSphereTrailHBox);

	    // Create HyperSpheres
	    Rectangle createHSphereTrailRect = createRect(110, 15);
	    createHyperSphereLabel = new Label("Create HyperSpheres"); createHyperSphereLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); createHyperSphereLabel.setTextFill(Paint.valueOf("WHITE")); createHyperSphereLabel.setTextAlignment(TextAlignment.CENTER);
	    StackPane createHSphereTrailStackPane = new StackPane(createHyperSphereLabel, createHSphereTrailRect); createHSphereTrailStackPane.setPrefWidth(80); createHSphereTrailStackPane.setPrefHeight(20); createHSphereTrailStackPane.setAlignment(Pos.CENTER);
	    HBox createHSphereTrailHBox = new HBox(createHSphereTrailStackPane); createHSphereTrailHBox.setPrefWidth(130); createHSphereTrailHBox.setPrefHeight(20); createHSphereTrailHBox.setAlignment(Pos.CENTER);

	    createHSphereTrailRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { createHSphereTrailRect.setFill(Color.rgb(255,255,255,0.5)); });
	    createHSphereTrailRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { constructHyperSphere(); superscene.mainstage.disableMouseMovement(); });
	    createHSphereTrailRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { createHSphereTrailRect.setFill(Color.rgb(0,0,0,0.0)); });

	    nodeFuntionsVBox.getChildren().add(createHSphereTrailHBox);

	    // Create HyperSphere
	    Rectangle dropHSphereRect = createRect(110, 15);
	    Label dropHSphereLabel = new Label("Drop HyperSphere"); dropHSphereLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); dropHSphereLabel.setTextFill(Paint.valueOf("WHITE")); dropHSphereLabel.setTextAlignment(TextAlignment.CENTER);
	    StackPane dropHSphereStackPane = new StackPane(dropHSphereLabel, dropHSphereRect); dropHSphereStackPane.setPrefWidth(80); dropHSphereStackPane.setPrefHeight(20); dropHSphereStackPane.setAlignment(Pos.CENTER);
	    HBox dropHSphereHBox = new HBox(dropHSphereStackPane); dropHSphereHBox.setPrefWidth(130); dropHSphereHBox.setPrefHeight(20); dropHSphereHBox.setAlignment(Pos.CENTER);

	    dropHSphereRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { dropHSphereRect.setFill(Color.rgb(255,255,255,0.5)); });
	    dropHSphereRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { dropHyperSphere(); superscene.mainstage.disableMouseMovement(); });
	    dropHSphereRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { dropHSphereRect.setFill(Color.rgb(0,0,0,0.0)); });

	    nodeFuntionsVBox.getChildren().add(dropHSphereHBox);

	    // Drop HyperSpheres
	    Rectangle dropHSphereTrailRect = createRect(110, 15);
	    dropHyperSphereLabel = new Label("Drop HyperSpheres"); dropHyperSphereLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); dropHyperSphereLabel.setTextFill(Paint.valueOf("WHITE")); dropHyperSphereLabel.setTextAlignment(TextAlignment.CENTER);
	    StackPane dropHSphereTrailStackPane = new StackPane(dropHyperSphereLabel, dropHSphereTrailRect); dropHSphereTrailStackPane.setPrefWidth(80); dropHSphereTrailStackPane.setPrefHeight(20); dropHSphereTrailStackPane.setAlignment(Pos.CENTER);
	    HBox dropHSphereTrailHBox = new HBox(dropHSphereTrailStackPane); dropHSphereTrailHBox.setPrefWidth(130); dropHSphereTrailHBox.setPrefHeight(20); dropHSphereTrailHBox.setAlignment(Pos.CENTER);

	    dropHSphereTrailRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { dropHSphereTrailRect.setFill(Color.rgb(255,255,255,0.5)); });
	    dropHSphereTrailRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { switchDropHyperSphereTrail(); superscene.mainstage.disableMouseMovement(); });
	    dropHSphereTrailRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { dropHSphereTrailRect.setFill(Color.rgb(0,0,0,0.0)); });

	    nodeFuntionsVBox.getChildren().add(dropHSphereTrailHBox);

	    // Mesh Visibility
	    Rectangle visibleRect = createRect(110, 15);
	    visibleLabel = new Label("Visible"); visibleLabel.setFont(getFont(FontWeight.BOLD, FontPosture.REGULAR, 10)); visibleLabel.setTextFill(Paint.valueOf("WHITE")); visibleLabel.setTextAlignment(TextAlignment.CENTER);
	    StackPane visibleStackPane = new StackPane(visibleLabel, visibleRect); visibleStackPane.setPrefWidth(80); visibleStackPane.setPrefHeight(20); visibleStackPane.setAlignment(Pos.CENTER);
	    HBox visibleHBox = new HBox(visibleStackPane); visibleHBox.setPrefWidth(130); visibleHBox.setPrefHeight(20); visibleHBox.setAlignment(Pos.CENTER);

	    visibleRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { visibleRect.setFill(Color.rgb(255,255,255,0.5)); });
	    visibleRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { switchVisibility(); superscene.mainstage.disableMouseMovement(); });
	    visibleRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { visibleRect.setFill(Color.rgb(0,0,0,0.0)); });

	    nodeFuntionsVBox.getChildren().add(visibleHBox);

	    // Caching
	    Rectangle cachingRect = createRect(110, 15);
	    cachingLabel = new Label("Caching"); cachingLabel.setFont(getFont(FontWeight.BOLD, FontPosture.REGULAR, 10)); cachingLabel.setTextFill(Paint.valueOf("WHITE")); cachingLabel.setTextAlignment(TextAlignment.CENTER);
	    StackPane cachingStackPane = new StackPane(cachingLabel, cachingRect); cachingStackPane.setPrefWidth(80); cachingStackPane.setPrefHeight(20); cachingStackPane.setAlignment(Pos.CENTER);
	    HBox cachingHBox = new HBox(cachingStackPane); cachingHBox.setPrefWidth(130); cachingHBox.setPrefHeight(20); cachingHBox.setAlignment(Pos.CENTER);

	    cachingRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { cachingRect.setFill(Color.rgb(255,255,255,0.5)); });
	    cachingRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { switchCaching(); superscene.mainstage.disableMouseMovement(); });
	    cachingRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { cachingRect.setFill(Color.rgb(0,0,0,0.0)); });

	    nodeFuntionsVBox.getChildren().add(cachingHBox);

	    // Motion
	    Rectangle switchMotionRect = createRect(110, 15);
	    motionLabel = new Label("Motional"); motionLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); motionLabel.setTextFill(Paint.valueOf("WHITE")); motionLabel.setTextAlignment(TextAlignment.CENTER);
	    StackPane switchMotionStackPane = new StackPane(motionLabel, switchMotionRect); switchMotionStackPane.setPrefWidth(80); switchMotionStackPane.setPrefHeight(20); switchMotionStackPane.setAlignment(Pos.CENTER);
	    HBox switchMotionHBox = new HBox(switchMotionStackPane); switchMotionHBox.setPrefWidth(130); switchMotionHBox.setPrefHeight(20); switchMotionHBox.setAlignment(Pos.CENTER);

	    switchMotionRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { switchMotionRect.setFill(Color.rgb(255,255,255,0.5)); });
	    switchMotionRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { switchMotion(); superscene.mainstage.disableMouseMovement(); });
	    switchMotionRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { switchMotionRect.setFill(Color.rgb(0,0,0,0.0)); });
	    setMotionLabelBold(motionEnabled); nodeFuntionsVBox.getChildren().add(switchMotionHBox);

	    // Stepping
	    Rectangle switchSteppingRect = createRect(110, 15);
	    steppingLabel = new Label("Stepping"); steppingLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); steppingLabel.setTextFill(Paint.valueOf("WHITE")); steppingLabel.setTextAlignment(TextAlignment.CENTER);
	    StackPane switchSteppingStackPane = new StackPane(steppingLabel, switchSteppingRect); switchSteppingStackPane.setPrefWidth(80); switchSteppingStackPane.setPrefHeight(20); switchSteppingStackPane.setAlignment(Pos.CENTER);
	    HBox switchSteppingHBox = new HBox(switchSteppingStackPane); switchSteppingHBox.setPrefWidth(130); switchSteppingHBox.setPrefHeight(20); switchSteppingHBox.setAlignment(Pos.CENTER);

	    switchSteppingRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { switchSteppingRect.setFill(Color.rgb(255,255,255,0.5)); });
	    switchSteppingRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { switchStepping(); superscene.mainstage.disableMouseMovement(); });
	    switchSteppingRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { switchSteppingRect.setFill(Color.rgb(0,0,0,0.0)); });
	    setSteppingLabelBold(steppingEnabled); nodeFuntionsVBox.getChildren().add(switchSteppingHBox);

	    // Gravitational
	    Rectangle switchGravityRect = createRect(110, 15);
	    switchGravityLabel = new Label("Gravitational"); switchGravityLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); switchGravityLabel.setTextFill(Paint.valueOf("WHITE")); switchGravityLabel.setTextAlignment(TextAlignment.CENTER);
	    StackPane switchGravityStackPane = new StackPane(switchGravityLabel, switchGravityRect); switchGravityStackPane.setPrefWidth(80); switchGravityStackPane.setPrefHeight(20); switchGravityStackPane.setAlignment(Pos.CENTER);
	    HBox switchGravityHBox = new HBox(switchGravityStackPane); switchGravityHBox.setPrefWidth(130); switchGravityHBox.setPrefHeight(20); switchGravityHBox.setAlignment(Pos.CENTER);

	    switchGravityRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { switchGravityRect.setFill(Color.rgb(255,255,255,0.5)); });
	    switchGravityRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { setSwitchGravityBold(getCelestial().switchGravitational()); superscene.mainstage.disableMouseMovement(); });
	    switchGravityRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { switchGravityRect.setFill(Color.rgb(0,0,0,0.0)); });
	    setSwitchGravityBold(celestial.isGravitational()); nodeFuntionsVBox.getChildren().add(switchGravityHBox);

	    // Collisional
	    Rectangle switchCollisionsRect = createRect(110, 15);
	    switchCollisionsLabel = new Label("Collisional"); switchCollisionsLabel.setFont(getFont(FontWeight.BOLD, FontPosture.REGULAR, 10)); switchCollisionsLabel.setTextFill(Paint.valueOf("WHITE")); switchCollisionsLabel.setTextAlignment(TextAlignment.CENTER);
	    StackPane switchCollisionsStackPane = new StackPane(switchCollisionsLabel, switchCollisionsRect); switchCollisionsStackPane.setPrefWidth(80); switchCollisionsStackPane.setPrefHeight(20); switchCollisionsStackPane.setAlignment(Pos.CENTER);
	    HBox switchCollisionsHBox = new HBox(switchCollisionsStackPane); switchCollisionsHBox.setPrefWidth(130); switchCollisionsHBox.setPrefHeight(20); switchCollisionsHBox.setAlignment(Pos.CENTER);

	    switchCollisionsRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { switchCollisionsRect.setFill(Color.rgb(255,255,255,0.5)); });
	    switchCollisionsRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { setSwitchCollisionalBold(getCelestial().switchCollisional()); superscene.mainstage.disableMouseMovement(); });
	    switchCollisionsRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { switchCollisionsRect.setFill(Color.rgb(0,0,0,0.0)); });
	    setSwitchCollisionalBold(celestial.isGravitational()); nodeFuntionsVBox.getChildren().add(switchCollisionsHBox);

	    // Shear
	    Rectangle switchShearRect = createRect(110, 15);
	    switchShearLabel = new Label("Shearing"); switchShearLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); switchShearLabel.setTextFill(Paint.valueOf("WHITE")); switchShearLabel.setTextAlignment(TextAlignment.CENTER);
	    StackPane switchShearStackPane = new StackPane(switchShearLabel, switchShearRect); switchShearStackPane.setPrefWidth(80); switchShearStackPane.setPrefHeight(20); switchShearStackPane.setAlignment(Pos.CENTER);
	    HBox switchShearHBox = new HBox(switchShearStackPane); switchShearHBox.setPrefWidth(130); switchShearHBox.setPrefHeight(20); switchShearHBox.setAlignment(Pos.CENTER);

	    switchShearRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { switchShearRect.setFill(Color.rgb(255,255,255,0.5)); });
	    switchShearRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { flipRotation(); superscene.mainstage.disableMouseMovement(); });
	    switchShearRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { switchShearRect.setFill(Color.rgb(0,0,0,0.0)); });
	    setSwitchShearBold(celestial.isGravitational()); nodeFuntionsVBox.getChildren().add(switchShearHBox);

	    // Backup
	    Rectangle backupCoordinalRect = createRect(110, 15);
	    backupCoordinalLabel = new Label("Backup"); backupCoordinalLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); backupCoordinalLabel.setTextFill(Paint.valueOf("WHITE")); backupCoordinalLabel.setTextAlignment(TextAlignment.CENTER);
	    StackPane backupCoordinalStackPane = new StackPane(backupCoordinalLabel, backupCoordinalRect); backupCoordinalStackPane.setPrefWidth(80); backupCoordinalStackPane.setPrefHeight(20); backupCoordinalStackPane.setAlignment(Pos.CENTER);
	    HBox backupCoordinalHBox = new HBox(backupCoordinalStackPane); backupCoordinalHBox.setPrefWidth(130); backupCoordinalHBox.setPrefHeight(20); backupCoordinalHBox.setAlignment(Pos.CENTER);

	    backupCoordinalRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { backupCoordinalRect.setFill(Color.rgb(255,255,255,0.5)); });
	    backupCoordinalRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { backupCoordinalState(); superscene.mainstage.disableMouseMovement(); });
	    backupCoordinalRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { backupCoordinalRect.setFill(Color.rgb(0,0,0,0.0)); });

	    nodeFuntionsVBox.getChildren().add(backupCoordinalHBox);

	    // Restore
	    Rectangle restoreCoordinalRect = createRect(110, 15);
	    Label restoreCoordinalLabel = new Label("Restore"); restoreCoordinalLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); restoreCoordinalLabel.setTextFill(Paint.valueOf("WHITE")); restoreCoordinalLabel.setTextAlignment(TextAlignment.CENTER);
	    StackPane restoreCoordinalStackPane = new StackPane(restoreCoordinalLabel, restoreCoordinalRect); restoreCoordinalStackPane.setPrefWidth(80); restoreCoordinalStackPane.setPrefHeight(20); restoreCoordinalStackPane.setAlignment(Pos.CENTER);
	    HBox restoreCoordinalHBox = new HBox(restoreCoordinalStackPane); restoreCoordinalHBox.setPrefWidth(130); restoreCoordinalHBox.setPrefHeight(20); restoreCoordinalHBox.setAlignment(Pos.CENTER);

	    restoreCoordinalRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { restoreCoordinalRect.setFill(Color.rgb(255,255,255,0.5)); });
	    restoreCoordinalRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { restoreCoordinalState(); superscene.mainstage.disableMouseMovement(); });
	    restoreCoordinalRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { restoreCoordinalRect.setFill(Color.rgb(0,0,0,0.0)); });

	    nodeFuntionsVBox.getChildren().add(restoreCoordinalHBox);

	    // Back
	    Rectangle nodeBackRect = createRect(110, 15);
	    Label nodeBackLabel = new Label("Back"); nodeBackLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); nodeBackLabel.setTextFill(Paint.valueOf("WHITE")); nodeBackLabel.setTextAlignment(TextAlignment.CENTER);
	    StackPane nodeBackStackPane = new StackPane(nodeBackLabel, nodeBackRect); nodeBackStackPane.setPrefWidth(80); nodeBackStackPane.setPrefHeight(20); nodeBackStackPane.setAlignment(Pos.CENTER);
	    HBox nodeBackHBox = new HBox(nodeBackStackPane); nodeBackHBox.setPrefWidth(130); nodeBackHBox.setPrefHeight(20); nodeBackHBox.setAlignment(Pos.CENTER);

	    nodeBackRect.addEventFilter(MouseEvent.MOUSE_ENTERED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { nodeBackRect.setFill(Color.rgb(255,255,255,0.5)); });
	    nodeBackRect.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> 
	    {
		superscene.mainstage.getSceneDisplayController().disableMouseMovement(); 
		superscene.flipNodeSectionDisplayToFront(superscene.getNodeListVBox());
	    });
	    nodeBackRect.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { nodeBackRect.setFill(Color.rgb(0,0,0,0.0)); });

	    nodeFuntionsVBox.getChildren().add(nodeBackHBox);
	    nodeFuntionsVBox.setRotationAxis(Rotate.Y_AXIS); nodeFuntionsVBox.setRotate(180); // Gets displayed on the back of nodeSectionVBox
//	}
    }
    
    public void setMenuItemUnderlined(boolean param)	{ if (nodeIdMenuItemLabel!=null) { nodeIdMenuItemLabel.setUnderline(param);}}
    public void setMenuItemBold(boolean param)		{ if (nodeIdMenuItemLabel!=null) { if (param) { nodeIdMenuItemLabel.setFont(getFont(FontWeight.BOLD, FontPosture.REGULAR, 10));} else {nodeIdMenuItemLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); }}}
    public void setViewCameraBold(boolean param)	{ if (viewCameraLabel!=null) { if (param) { viewCameraLabel.setFont(getFont(FontWeight.BOLD, FontPosture.REGULAR, 10));} else {viewCameraLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); }}}
    public void setSwitchLightBold(boolean param)	{ if (switchLightLabel!=null) { if (param) { switchLightLabel.setFont(getFont(FontWeight.BOLD, FontPosture.REGULAR, 10));} else {switchLightLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); }}}
    public void setControlNodeBold(boolean param)	{ if (controlNodeLabel!=null) { if (param) { controlNodeLabel.setFont(getFont(FontWeight.BOLD, FontPosture.REGULAR, 10));} else {controlNodeLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); }}}
    public void setMotionLabelBold(boolean param)	{ if (motionLabel!=null) { if (param) { motionLabel.setFont(getFont(FontWeight.BOLD, FontPosture.REGULAR, 10));} else {motionLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); }}}
    public void setSteppingLabelBold(boolean param)	{ if (steppingLabel!=null) { if (param) { steppingLabel.setFont(getFont(FontWeight.BOLD, FontPosture.REGULAR, 10));} else {steppingLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); }}}
    public void setSwitchGravityBold(boolean param)	{ if (switchGravityLabel!=null) { if (param) { switchGravityLabel.setFont(getFont(FontWeight.BOLD, FontPosture.REGULAR, 10));} else {switchGravityLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); }}}
    public void setSwitchCollisionalBold(boolean param)	{ if (switchCollisionsLabel!=null) { if (param) { switchCollisionsLabel.setFont(getFont(FontWeight.BOLD, FontPosture.REGULAR, 10));} else {switchCollisionsLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); }}}
    public void setSwitchShearBold(boolean param)	{ if (switchShearLabel!=null) { if (param) { switchShearLabel.setFont(getFont(FontWeight.BOLD, FontPosture.REGULAR, 10));} else {switchShearLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); }}}
    
    protected Rectangle createRect(double width, double height)
    {
	Rectangle rect = new Rectangle(0, 0, width, height);
//	rect.setArcWidth(Math.min(width, height)*0.5);
//	rect.setArcHeight(Math.min(width, height)*0.5);
	rect.setArcWidth(10);
	rect.setArcHeight(10);
	rect.setFill(Color.TRANSPARENT);
	rect.setStroke(Color.GREY);
	rect.setStrokeWidth(1);
	rect.setOpacity(1);
	return rect;
    }
    
    protected Rectangle createBGRect(double width, double height)
    {
	Rectangle rect = new Rectangle(0, 0, width, height);
	rect.setArcWidth(Math.min(width, height)*0.5);
	rect.setArcHeight(Math.min(width, height)*0.5);
//	rect.setStroke(Color.GREY);
//	rect.setStrokeWidth(1);
	rect.setOpacity(0.3);
	return rect;
    }
    
    public HBox getNodeIdMenuItemHBox() { return nodeIdMenuItemHBox; }
    
// Human Interface Events from Keyboard and Mouse sets the node motion    
    
    
    // Resets mousepointer readings
    protected void resetMousePointerOffset(MouseEvent mouseEvent) { mouseMovedX = mouseEvent.getSceneX(); mouseMovedY = mouseEvent.getSceneY();  mouseMovedXLast = mouseMovedX;  mouseMovedYLast = mouseMovedY; }

    // Rotate
    public void mouseRotateMotion(MouseEvent mouseEvent) // Choose for better performance (uses )
    {
	if (isMotionEnabled())
	{
	    mouseMovedX = mouseEvent.getSceneX();
	    mouseMovedY = mouseEvent.getSceneY();

//	    Right Panning Rotation
	    
	    if ((mouseMovedX - mouseMovedXLast) > 0)
	    {
		setPanRightThrottle(((mouseMovedX - mouseMovedXLast) / magnifyFactor) * rotatePanThrottleFactor); mouseMovedXLast = mouseMovedX;
		if (getPanRightThrottle() > 1) { setPanRightThrottle(1); } rotateLYPowerDisplay();
	        coordinal.getRymProp().set(coordinal.getRymProp().get() + (( (getPanRightThrottle() / superscene.getMotionRate()) * getRotEngPwr() ) / (celestial.getMass())));
//		setPanRightThrottle(0); rotateLYPowerDisplay();
	    }

//	    Left Panning Rotation

	    if ((mouseMovedX - mouseMovedXLast) < 0)
	    {
		setPanLeftThrottle(-(((mouseMovedX - mouseMovedXLast) / magnifyFactor) * rotatePanThrottleFactor)); mouseMovedXLast = mouseMovedX;
		if (getPanLeftThrottle() > 1) { setPanLeftThrottle(1); } rotateLYPowerDisplay();
	        coordinal.getRymProp().set(coordinal.getRymProp().get() - (( (getPanLeftThrottle()/superscene.getMotionRate()) * getRotEngPwr() ) / (celestial.getMass())));
//		setPanLeftThrottle(0); rotateLYPowerDisplay();
	    }
	    
//	    Tilt Up Rotation

	    if ((mouseMovedY - mouseMovedYLast) > 0)
	    {
		setTiltUpThrottle(((mouseMovedY - mouseMovedYLast) / magnifyFactor * rotateTiltThrottleFactor)); mouseMovedYLast = mouseMovedY;
		if (getTiltUpThrottle() > 1) { setTiltUpThrottle(1); } rotateLXPowerDisplay();
	        coordinal.getRxmProp().set(coordinal.getRxmProp().get() + (( (getTiltUpThrottle()/superscene.getMotionRate()) * getRotEngPwr() ) / (celestial.getMass())));
//		setTiltUpThrottle(0); rotateLXPowerDisplay();
	    }

//	    Tilt Down Rotation
	    if ((mouseMovedY - mouseMovedYLast) < 0)
	    {
		setTiltDownThrottle(-(((mouseMovedY - mouseMovedYLast) / magnifyFactor) * rotateTiltThrottleFactor)); mouseMovedYLast = mouseMovedY;
		if (getTiltDownThrottle() > 1) { setTiltDownThrottle(1); } rotateLXPowerDisplay();
	        coordinal.getRxmProp().set(coordinal.getRxmProp().get() - (( (getTiltDownThrottle()/superscene.getMotionRate()) * getRotEngPwr() ) / (celestial.getMass())));
//		setTiltDownThrottle(0); rotateLXPowerDisplay();
	    }
	}
    }

    // 3DNavigator
    public void navigatorMotion(MouseEvent mouseEvent)
    {
	if (isMotionEnabled())
	{
	    mouseMovedX = mouseEvent.getSceneX(); rotateXThrottle = ((mouseMovedX - mouseMovedXLast)/magnifyFactor); mouseMovedXLast = mouseMovedX;
	    mouseMovedY = mouseEvent.getSceneY(); rotateYThrottle = ((mouseMovedY - mouseMovedYLast)/magnifyFactor); mouseMovedYLast = mouseMovedY;
	    
	    coordinal.getRymProp().set(coordinal.getRymProp().get() + (((( (rotateXThrottle/superscene.getMotionRate()) * getRotEngPwr() ) / (celestial.getMass()/keyStrokesPerSecond)))));
	    coordinal.getRxmProp().set(coordinal.getRxmProp().get() + (((( (rotateYThrottle/superscene.getMotionRate()) * getRotEngPwr() ) / (celestial.getMass()/keyStrokesPerSecond)))));
	}
    }

    public void mouseScroll(ScrollEvent scrollEvent) // Mother method for subclasses like MyCamera
    {
    }

    public void scaleUp()	{ coordinal.getSxProp().set(coordinal.getSxProp().get()*scaleStep); coordinal.getSyProp().set(coordinal.getSyProp().get()*scaleStep); coordinal.getSzProp().set(coordinal.getSzProp().get()*scaleStep); setScale(); }
    public void scaleDown()	{ coordinal.getSxProp().set(coordinal.getSxProp().get()/scaleStep); coordinal.getSyProp().set(coordinal.getSyProp().get()/scaleStep); coordinal.getSzProp().set(coordinal.getSzProp().get()/scaleStep); setScale(); }
    
    public void switchOnScaleUp() // Not being used
    {
	if (isMotionEnabled())
	{
	    if (! isScaleUp())
	    {
		setTiltUp(true); scaleUpTimer = new Timer(); scaleUpTimer.scheduleAtFixedRate(new TimerTask() {@Override public void run() { Platform.runLater(() -> 
		{
		    coordinal.getSxmProp().set(coordinal.getRxmProp().get() * scaleStep); coordinal.getSymProp().set(coordinal.getRymProp().get() * scaleStep); coordinal.getSzmProp().set(coordinal.getRzmProp().get() * scaleStep); 
		}); }}, 0, keyTimerInterval);
	    }
	}
    }
    public void switchOffScaleUp() { if (isMotionEnabled()) { setScaleUp(false); scaleUpTimer.cancel(); } }

    public void switchOnScaleDown() // Not being used
    {
	if (isMotionEnabled())
	{
	    if (! isScaleDown())
	    {
		setTiltDown(true); scaleDownTimer = new Timer(); scaleDownTimer.scheduleAtFixedRate(new TimerTask() {@Override public void run() { Platform.runLater(() -> 
		{
		    coordinal.getSxmProp().set(coordinal.getRxmProp().get() / scaleStep); coordinal.getSymProp().set(coordinal.getRymProp().get() / scaleStep); coordinal.getSzmProp().set(coordinal.getRzmProp().get() / scaleStep); 
		}); }}, 0, keyTimerInterval);
	    }
	}
    }
    public void switchOffScaleDown() { if (isMotionEnabled()) { setScaleDown(false); scaleDownTimer.cancel(); } }

    public void switchOnMainEnginePropulsion()
    {
	if (isMotionEnabled())
	{
	    if (! isMainEngineForwardPropulsion())
	    {
		setMainEngineForwardPropulsion(true); mainEngineTimer = new Timer(); mainEngineTimer.scheduleAtFixedRate(new TimerTask() {@Override public void run() { Platform.runLater(() -> 
		{
		    if (getMainEngineThrottle() < 1) { setMainEngineThrottle(getMainEngineThrottle() + mainEngineThottleStep); } else { setMainEngineThrottle(1); } 
		    coordinal.getTxmProp().set(coordinal.getTxmProp().get() + (((( getMainEngineThrottle() * getMainEngPwr() ) / celestial.getMass()) / (superscene.getMotionRate() / keyStrokesPerSecond)) * nodeGroup.getLocalToSceneTransform().getMxz())   );
		    coordinal.getTymProp().set(coordinal.getTymProp().get() + (((( getMainEngineThrottle() * getMainEngPwr() ) / celestial.getMass()) / (superscene.getMotionRate() / keyStrokesPerSecond)) * nodeGroup.getLocalToSceneTransform().getMyz())   );
		    coordinal.getTzmProp().set(coordinal.getTzmProp().get() + (((( getMainEngineThrottle() * getMainEngPwr() ) / celestial.getMass()) / (superscene.getMotionRate() / keyStrokesPerSecond)) * nodeGroup.getLocalToSceneTransform().getMzz())   );
		}); }}, 0, keyTimerInterval);
	    }
	}
    }
    public void switchOffMainEnginePropulsion() { if (isMotionEnabled()) { setMainEngineThrottle(0); setMainEngineForwardPropulsion(false); mainEngineTimer.cancel(); } }

    public void switchOnForwardPropulsion()
    {
	if (isMotionEnabled())
	{
	    if (! isForwardPropulsion())
	    {
		setForwardPropulsion(true); forwardPropulsionTimer = new Timer(); forwardPropulsionTimer.scheduleAtFixedRate(new TimerTask() {@Override public void run() { Platform.runLater(() -> 
		{
		    if (getForwardThrottle() < 1) { setForwardThrottle(getForwardThrottle() + translateEngineThrottleStep); } else { setForwardThrottle(1); } translateLZPowerDisplay();
		    coordinal.getTxmProp().set(coordinal.getTxmProp().get() + (((( getForwardThrottle() * getTransEngPwr() ) / celestial.getMass()) / (superscene.getMotionRate() / keyStrokesPerSecond)) * nodeGroup.getLocalToSceneTransform().getMxz()) );
		    coordinal.getTymProp().set(coordinal.getTymProp().get() + (((( getForwardThrottle() * getTransEngPwr() ) / celestial.getMass()) / (superscene.getMotionRate() / keyStrokesPerSecond)) * nodeGroup.getLocalToSceneTransform().getMyz()) );
		    coordinal.getTzmProp().set(coordinal.getTzmProp().get() + (((( getForwardThrottle() * getTransEngPwr() ) / celestial.getMass()) / (superscene.getMotionRate() / keyStrokesPerSecond)) * nodeGroup.getLocalToSceneTransform().getMzz()) );
		}); }}, 0, keyTimerInterval);
	    }
	}
    }
    public void switchOffForwardPropulsion() { if (isMotionEnabled()) { setForwardThrottle(0); translateLZPowerDisplay(); setForwardPropulsion(false); forwardPropulsionTimer.cancel(); } }

    public void switchOnBackwardPropulsion()
    {
	if (isMotionEnabled())
	{
	    if (! isBackwardPropulsion())
	    {
		setBackwardPropulsion(true); backwardPropulsionTimer = new Timer(); backwardPropulsionTimer.scheduleAtFixedRate(new TimerTask() {@Override public void run() { Platform.runLater(() -> 
		{
		    if (getBackwardThrottle() < 1) { setBackwardThrottle(getBackwardThrottle() + translateEngineThrottleStep); } else { setBackwardThrottle(1); } translateLZPowerDisplay();
		    coordinal.getTxmProp().set(coordinal.getTxmProp().get() - (((( getBackwardThrottle() * getTransEngPwr() ) / celestial.getMass()) / (superscene.getMotionRate() / keyStrokesPerSecond)) * nodeGroup.getLocalToSceneTransform().getMxz()) );
		    coordinal.getTymProp().set(coordinal.getTymProp().get() - (((( getBackwardThrottle() * getTransEngPwr() ) / celestial.getMass()) / (superscene.getMotionRate() / keyStrokesPerSecond)) * nodeGroup.getLocalToSceneTransform().getMyz()) );
		    coordinal.getTzmProp().set(coordinal.getTzmProp().get() - (((( getBackwardThrottle() * getTransEngPwr() ) / celestial.getMass()) / (superscene.getMotionRate() / keyStrokesPerSecond)) * nodeGroup.getLocalToSceneTransform().getMzz()) );
		}); }}, 0, keyTimerInterval);
	    }
	}
    }
    public void switchOffBackwardPropulsion() { if (isMotionEnabled()) { setBackwardThrottle(0); translateLZPowerDisplay(); setBackwardPropulsion(false); backwardPropulsionTimer.cancel(); } }

    public void switchOnLeftPropulsion()
    {
	if (isMotionEnabled())
	{
	    if (! isLeftPropulsion())
	    {
		setLeftPropulsion(true); leftPropulsionTimer = new Timer(); leftPropulsionTimer.scheduleAtFixedRate(new TimerTask() {@Override public void run() { Platform.runLater(() -> 
		{
		    if (getLeftThrottle() < 1) { setLeftThrottle(getLeftThrottle() + translateEngineThrottleStep); } else { setLeftThrottle(1); } translateLXPowerDisplay();
		    coordinal.getTxmProp().set(coordinal.getTxmProp().get() - (((( getLeftThrottle() * getTransEngPwr() ) / celestial.getMass()) / (superscene.getMotionRate() / keyStrokesPerSecond)) * nodeGroup.getLocalToSceneTransform().getMxx()) );
		    coordinal.getTymProp().set(coordinal.getTymProp().get() - (((( getLeftThrottle() * getTransEngPwr() ) / celestial.getMass()) / (superscene.getMotionRate() / keyStrokesPerSecond)) * nodeGroup.getLocalToSceneTransform().getMyx()) );
		    coordinal.getTzmProp().set(coordinal.getTzmProp().get() - (((( getLeftThrottle() * getTransEngPwr() ) / celestial.getMass()) / (superscene.getMotionRate() / keyStrokesPerSecond)) * nodeGroup.getLocalToSceneTransform().getMzx()) );
		}); }}, 0, keyTimerInterval);
	    }
	}
    }
    public void switchOffLeftPropulsion() { if (isMotionEnabled()) { setLeftThrottle(0); translateLXPowerDisplay(); setLeftPropulsion(false); leftPropulsionTimer.cancel(); } }

    public void switchOnRightPropulsion()
    {
	if (isMotionEnabled())
	{
	    if (! isRightPropulsion())
	    {
		setRightPropulsion(true); rightPropulsionTimer = new Timer(); rightPropulsionTimer.scheduleAtFixedRate(new TimerTask() {@Override public void run() { Platform.runLater(() -> 
		{
		    if (getRightThrottle() < 1) { setRightThrottle(getRightThrottle() + translateEngineThrottleStep); } else { setRightThrottle(1); } translateLXPowerDisplay();
		    coordinal.getTxmProp().set(coordinal.getTxmProp().get() + (((( getRightThrottle() * getTransEngPwr() ) / celestial.getMass()) / (superscene.getMotionRate() / keyStrokesPerSecond)) * nodeGroup.getLocalToSceneTransform().getMxx()) );
		    coordinal.getTymProp().set(coordinal.getTymProp().get() + (((( getRightThrottle() * getTransEngPwr() ) / celestial.getMass()) / (superscene.getMotionRate() / keyStrokesPerSecond)) * nodeGroup.getLocalToSceneTransform().getMyx()) );
		    coordinal.getTzmProp().set(coordinal.getTzmProp().get() + (((( getRightThrottle() * getTransEngPwr() ) / celestial.getMass()) / (superscene.getMotionRate() / keyStrokesPerSecond)) * nodeGroup.getLocalToSceneTransform().getMzx()) );
		}); }}, 0, keyTimerInterval);
	    }
	}
    }
    public void switchOffRightPropulsion() { if (isMotionEnabled()) { setRightThrottle(0); translateLXPowerDisplay(); setRightPropulsion(false); rightPropulsionTimer.cancel(); } }

    public void switchOnUpwardPropulsion()
    {
	if (isMotionEnabled())
	{
	    if (! isUpwardPropulsion())
	    {
		setUpwardPropulsion(true); upwardPropulsionTimer = new Timer(); upwardPropulsionTimer.scheduleAtFixedRate(new TimerTask() {@Override public void run() { Platform.runLater(() -> 
		{
		    if (getUpThrottle() < 1) { setUpThrottle(getUpThrottle() + translateEngineThrottleStep); } else { setUpThrottle(1); } translateLYPowerDisplay();
		    coordinal.getTxmProp().set(coordinal.getTxmProp().get() - (((( getUpThrottle() * getTransEngPwr() ) / celestial.getMass()) / (superscene.getMotionRate() / keyStrokesPerSecond)) * nodeGroup.getLocalToSceneTransform().getMxy()) );
		    coordinal.getTymProp().set(coordinal.getTymProp().get() - (((( getUpThrottle() * getTransEngPwr() ) / celestial.getMass()) / (superscene.getMotionRate() / keyStrokesPerSecond)) * nodeGroup.getLocalToSceneTransform().getMyy()) );
		    coordinal.getTzmProp().set(coordinal.getTzmProp().get() - (((( getUpThrottle() * getTransEngPwr() ) / celestial.getMass()) / (superscene.getMotionRate() / keyStrokesPerSecond)) * nodeGroup.getLocalToSceneTransform().getMzy()) );
		}); }}, 0, keyTimerInterval);
	    }
	}
    }
    public void switchOffUpwardPropulsion() { if (isMotionEnabled()) { setUpThrottle(0); translateLYPowerDisplay(); setUpwardPropulsion(false); upwardPropulsionTimer.cancel(); } }

    public void switchOnDownwardPropulsion()
    {
	if (isMotionEnabled())
	{
	    if (! isDownwardPropulsion())
	    {
		setDownwardPropulsion(true); downwardPropulsionTimer = new Timer(); downwardPropulsionTimer.scheduleAtFixedRate(new TimerTask() {@Override public void run() { Platform.runLater(() -> 
		{
		    if (getDownThrottle() < 1) { setDownThrottle(getDownThrottle() + translateEngineThrottleStep); } else { setDownThrottle(1); } translateLYPowerDisplay();
		    coordinal.getTxmProp().set(coordinal.getTxmProp().get() + (((( getDownThrottle() * getTransEngPwr() ) / celestial.getMass()) / (superscene.getMotionRate() / keyStrokesPerSecond)) * nodeGroup.getLocalToSceneTransform().getMxy()) );
		    coordinal.getTymProp().set(coordinal.getTymProp().get() + (((( getDownThrottle() * getTransEngPwr() ) / celestial.getMass()) / (superscene.getMotionRate() / keyStrokesPerSecond)) * nodeGroup.getLocalToSceneTransform().getMyy()) );
		    coordinal.getTzmProp().set(coordinal.getTzmProp().get() + (((( getDownThrottle() * getTransEngPwr() ) / celestial.getMass()) / (superscene.getMotionRate() / keyStrokesPerSecond)) * nodeGroup.getLocalToSceneTransform().getMzy()) );
		}); }}, 0, keyTimerInterval);
	    }
	}
    }
    public void switchOffDownwardPropulsion() { if (isMotionEnabled()) { setDownThrottle(0); translateLYPowerDisplay(); setDownwardPropulsion(false); downwardPropulsionTimer.cancel(); } }

    public void switchOnTiltUp()
    {
	if (isMotionEnabled())
	{
	    if (! isTiltUp())
	    {
		setTiltUp(true); tiltUpTimer = new Timer(); tiltUpTimer.scheduleAtFixedRate(new TimerTask() {@Override public void run() { Platform.runLater(() -> 
		{
		    if (getTiltUpThrottle() < 1) { setTiltUpThrottle(getTiltUpThrottle() + (rotateEngineThrottleStep / magnifyFactor)); } else { setTiltUpThrottle(1); } rotateLXPowerDisplay();
		    coordinal.getRxmProp().set(coordinal.getRxmProp().get() + (((( getTiltUpThrottle() * getRotEngPwr() ) / (celestial.getMass()) / (superscene.getMotionRate() / keyStrokesPerSecond)))));
		}); }}, 0, keyTimerInterval);
	    }
	}
    }
    public void switchOffTiltUp() { if (isMotionEnabled()) { setTiltUpThrottle(0); rotateLXPowerDisplay(); setTiltUp(false); tiltUpTimer.cancel(); } }

    public void switchOnTiltDown()
    {
	if (isMotionEnabled())
	{
	    if (! isTiltDown())
	    {
		setTiltDown(true); tiltDownTimer = new Timer(); tiltDownTimer.scheduleAtFixedRate(new TimerTask() {@Override public void run() { Platform.runLater(() -> 
		{
		    if (getTiltDownThrottle() < 1) { setTiltDownThrottle(getTiltDownThrottle() + (rotateEngineThrottleStep / magnifyFactor)); } else { setTiltDownThrottle(1); } rotateLXPowerDisplay();
		    coordinal.getRxmProp().set(coordinal.getRxmProp().get() - (((( getTiltDownThrottle() * getRotEngPwr() ) / (celestial.getMass()) / (superscene.getMotionRate() / keyStrokesPerSecond)))));
		}); }}, 0, keyTimerInterval);
	    }
	}
    }
    public void switchOffTiltDown() { if (isMotionEnabled()) { setTiltDownThrottle(0); rotateLXPowerDisplay(); setTiltDown(false); tiltDownTimer.cancel(); } }

    public void switchOnPanLeft()
    {
	if (isMotionEnabled())
	{
	    if (! isPanLeft())
	    {
		setPanLeft(true); panLeftTimer = new Timer(); panLeftTimer.scheduleAtFixedRate(new TimerTask() {@Override public void run() { Platform.runLater(() -> 
		{
		    if (getPanLeftThrottle() < 1) {setPanLeftThrottle(getPanLeftThrottle() + (rotateEngineThrottleStep / magnifyFactor));} else { setPanLeftThrottle(1); } rotateLYPowerDisplay();
		    coordinal.getRymProp().set(coordinal.getRymProp().get() - (((( getPanLeftThrottle() * getRotEngPwr() ) / (celestial.getMass()) / (superscene.getMotionRate() / keyStrokesPerSecond)))));
		}); }}, 0, keyTimerInterval);
	    }
	}
    }
    public void switchOffPanLeft() { if (isMotionEnabled()) { setPanLeftThrottle(0); rotateLYPowerDisplay(); setPanLeft(false); panLeftTimer.cancel(); } }

    public void switchOnPanRight()
    {
	if (isMotionEnabled())
	{
	    if (! isPanRight())
	    {
		setPanRight(true); panRightTimer = new Timer(); panRightTimer.scheduleAtFixedRate(new TimerTask() {@Override public void run() { Platform.runLater(() -> 
		{
		    if (getPanRightThrottle() < 1) { setPanRightThrottle(getPanRightThrottle() + (rotateEngineThrottleStep / magnifyFactor)); } else { setPanRightThrottle(1); } rotateLYPowerDisplay();
		    coordinal.getRymProp().set(coordinal.getRymProp().get() + (((( getPanRightThrottle() * getRotEngPwr() ) / (celestial.getMass()) / (superscene.getMotionRate() / keyStrokesPerSecond)))));
		}); }}, 0, keyTimerInterval);
	    }
	}
    }
    public void switchOffPanRight() { if (isMotionEnabled()) { setPanRightThrottle(0); rotateLYPowerDisplay(); setPanRight(false); panRightTimer.cancel(); } }

    public void switchOnRollLeft()
    {
	if (isMotionEnabled())
	{
	    if (! isRollLeft())
	    {
		setRollLeft(true); rollLeftTimer = new Timer(); rollLeftTimer.scheduleAtFixedRate(new TimerTask() {@Override public void run() { Platform.runLater(() -> 
		{
		    if (getRollLeftThrottle() < 1) { setRollLeftThrottle(getRollLeftThrottle() + (rotateEngineThrottleStep / magnifyFactor)); } else { setRollLeftThrottle(1); } rotateLZPowerDisplay();
		    coordinal.getRzmProp().set(coordinal.getRzmProp().get() - (((( getRollLeftThrottle() * getRotEngPwr() ) / (celestial.getMass()) / (superscene.getMotionRate() / keyStrokesPerSecond)))));
		}); }}, 0, keyTimerInterval);
	    }
	}
    }
    public void switchOffRollLeft() { if (isMotionEnabled()) { setRollLeftThrottle(0); rotateLZPowerDisplay(); setRollLeft(false); rollLeftTimer.cancel(); } }

    public void switchOnRollRight()
    {
	if (isMotionEnabled())
	{
	    if (! isRollRight())
	    {
		setRollRight(true); rollRightTimer = new Timer(); rollRightTimer.scheduleAtFixedRate(new TimerTask() {@Override public void run() { Platform.runLater(() -> 
		{
		    if (getRollRightThrottle() < 1) { setRollRightThrottle(getRollRightThrottle() + (rotateEngineThrottleStep / magnifyFactor)); } else { setRollRightThrottle(1); } rotateLZPowerDisplay();
		    coordinal.getRzmProp().set(coordinal.getRzmProp().get() + (((( getRollRightThrottle() * getRotEngPwr() ) / (celestial.getMass()) / (superscene.getMotionRate() / keyStrokesPerSecond)))));
		}); }}, 0, keyTimerInterval);
	    }
	}
    }
    protected void switchOffRollRight() { if (isMotionEnabled()) { setRollRightThrottle(0); rotateLZPowerDisplay(); setRollRight(false); rollRightTimer.cancel(); } }

    protected void stopTranslation()			    { coordinal.getTxmProp().set(0); coordinal.getTymProp().set(0); coordinal.getTzmProp().set(0); displayAll(); }
    protected void stopRotation()			    { coordinal.getRxmProp().set(0); coordinal.getRymProp().set(0); coordinal.getRzmProp().set(0); displayAll(); }
  
    private void switchMotion()				    { setMotionEnabled(!motionEnabled); setMotionLabelBold(motionEnabled); }
    private void switchStepping()			    { steppingEnabled = !steppingEnabled; setSteppingLabelBold(steppingEnabled); }

    protected void switchMotionForMotionStateChangeable()   { if (motionStateChangeable) { setMotionEnabled(!motionEnabled); setMotionLabelBold(motionEnabled); } }
    protected void setMotion(boolean param)		    { setMotionEnabled(param); setMotionLabelBold(motionEnabled); /*System.out.println("Continue: "+ this.getId() + " animation: " + animationTimerEnabled);*/ }
    protected void enableMotion()			    { setMotionEnabled(true); setMotionLabelBold(motionEnabled); /*System.out.println("Continue: "+ this.getId() + " animation: " + animationTimerEnabled);*/ }
    protected void disableMotion()			    { setMotionEnabled(false); setMotionLabelBold(motionEnabled); /*System.out.println("Continue: "+ this.getId() + " animation: " + animationTimerEnabled);*/ }
    protected void setStepping(boolean param)		    { steppingEnabled = param; setSteppingLabelBold(steppingEnabled); }
    protected void updateMotionRate2Motion(double param)    { getCoordinal().recalculateMotion(param); getBUPCoordinal().recalculateMotion(param); if (displaying) { translateMotionDisplay(); rotateMotionDisplay(); }}
    
//  Movement Update Timer Handlers
    
    synchronized public void translateHandler() // Called from SuperScene to render all object in synch for better performance than asynchronious binded properties rendering
    {
	if (moveGroup.getTranslateX() != coordinal.getTxProp().get()) { setTranslateX(); nodeTranslated = true; }
	if (moveGroup.getTranslateY() != coordinal.getTyProp().get()) { setTranslateY(); nodeTranslated = true; }
	if (moveGroup.getTranslateZ() != coordinal.getTzProp().get()) { setTranslateZ(); nodeTranslated = true; }

	if (rxRotate.getAngle() != coordinal.getRxProp().get()) { setRotateX(); }
	if (ryRotate.getAngle() != coordinal.getRyProp().get()) { setRotateY(); }
	if (rzRotate.getAngle() != coordinal.getRzProp().get()) { setRotateZ(); }

	if ( (displaying) && (nodeTranslated) )	{ distanceDisplay(); speedDisplay(); }
    }
    
    public void motionHandler() // Called from SuperScene to render all object in synch for better performance than asynchronious binded properties rendering
    {
	if ( coordinal.getTxmProp().get() != 0 ) { coordinal.getTxProp().set(coordinal.getTxProp().get() + (coordinal.getTxmProp().get() * motionFactor)); }
	if ( coordinal.getTymProp().get() != 0 ) { coordinal.getTyProp().set(coordinal.getTyProp().get() + (coordinal.getTymProp().get() * motionFactor)); }
	if ( coordinal.getTzmProp().get() != 0 ) { coordinal.getTzProp().set(coordinal.getTzProp().get() + (coordinal.getTzmProp().get() * motionFactor)); }

	if ( coordinal.getRxmProp().get() != 0 ) { coordinal.getRxProp().set(coordinal.getRxProp().get() + (coordinal.getRxmProp().get() * motionFactor));
	if ( coordinal.getRxProp().get() >= 360) { coordinal.getRxProp().set(coordinal.getRxProp().get()-360);} if (coordinal.getRxProp().get() < 0) { coordinal.getRxProp().set(coordinal.getRxProp().get()+360);} }

	if ( coordinal.getRymProp().get() != 0 ) { coordinal.getRyProp().set(coordinal.getRyProp().get() + (coordinal.getRymProp().get() * motionFactor));
	if ( coordinal.getRyProp().get() >= 360) { coordinal.getRyProp().set(coordinal.getRyProp().get()-360);} if (coordinal.getRyProp().get() < 0) { coordinal.getRyProp().set(coordinal.getRyProp().get()+360);} }

	if ( coordinal.getRzmProp().get() != 0 ) { coordinal.getRzProp().set(coordinal.getRzProp().get() + (coordinal.getRzmProp().get() * motionFactor));
	if ( coordinal.getRzProp().get() >= 360) { coordinal.getRzProp().set(coordinal.getRzProp().get()-360);} if (coordinal.getRzProp().get() < 0) { coordinal.getRzProp().set(coordinal.getRzProp().get()+360);} }

	setLastLocation(new Point3D(getLocation().getX(), getLocation().getY(), getLocation().getZ()));
	setLocation(new Point3D(coordinal.getTxProp().get(),coordinal.getTyProp().get(),coordinal.getTzProp().get()));

//	if (steppingEnabled) { motionEnabled = false; setMotionLabelBold(motionEnabled); }
    }
    
    synchronized public void addMotion(double txm, double tym, double tzm)
    {
	coordinal.getTxmProp().set(coordinal.getTxmProp().get() + (txm) );
	coordinal.getTymProp().set(coordinal.getTymProp().get() + (tym) );
	coordinal.getTzmProp().set(coordinal.getTzmProp().get() + (tzm) );
    }

    synchronized public void setMotion(double txm, double tym, double tzm)
    {
	coordinal.getTxmProp().set(txm);
	coordinal.getTymProp().set(tym);
	coordinal.getTzmProp().set(tzm);
    }

    public void dropSphere()
    {
	Sphere sphere = new Sphere(celestial.getRadius()*trailRadiusPercentage/100,16);

	sphere.setCullFace(CullFace.valueOf(trailCullFace));
	sphere.setDrawMode(DrawMode.valueOf(trailDrawMode));

	PhongMaterial phongMaterial = new PhongMaterial();

	Color c = Color.web(trailColor, trailOpacity);
	phongMaterial.setDiffuseColor(c);

	sphere.setMaterial(phongMaterial);
//	sphere.setOpacity(trailOpacity);

	sphere.setTranslateX(getCoordinal().getTxProp().get());sphere.setTranslateY(getCoordinal().getTyProp().get());sphere.setTranslateZ(getCoordinal().getTzProp().get());
	trailGroup.getChildren().add(sphere);
	if (trailLength > 0)
	{
	    if (trailGroup.getChildren().size() > trailLength) { trailGroup.getChildren().remove(0); }
	}
    }
    
    // Creates array of prototypes
    public void constructHyperSphere()
    {
	Platform.runLater(() -> 
	{
	    if (shellRadius > 0)
	    {
		prototypeArrayList = new ArrayList<>();
		MyHyperSphere myHyperSphere = new MyHyperSphere(superscene, id, hyperSphereCelestial, caching, verbosity);
		prototypeArrayList.addAll(MyHyperSphere.createHyperSphere(getCoordinal().getLocation(), shells, shellRadius, shellResolution, shellMotion, randomise, hyperSphereCelestial.getRadius(), hyperSphereDivisions, hyperSphereCullFace, hyperSphereDrawMode, hyperSphereColor, hyperSphereOpacity));
	    }
	});
    }
    
    public void dropHyperSphere() // Reconstructing new MySphere array from prototype array
    {
//	Platform.runLater(() -> 
//	{
	    if (prototypeArrayList != null)
	    {
		ArrayList<Node3D> cloneArrayList = new ArrayList<>();
		prototypeArrayList.stream().forEach((prototypeNode) ->
		{
//		    MySphere mySphereClone = MyHyperSphere.makeMySphere(shellRadius, shellResolution, hyperSphereDivisions, hyperSphereCullFace, hyperSphereDrawMode, hyperSphereColor, hyperSphereOpacity, prototypeNode.getCoordinal().getTxProp().get(), prototypeNode.getCoordinal().getTyProp().get(), prototypeNode.getCoordinal().getTzProp().get()); 
		    MySphere mySphereClone = MyHyperSphere.makeMySphere(shellRadius, shellResolution, hyperSphereDivisions, hyperSphereCullFace, hyperSphereDrawMode, hyperSphereColor, hyperSphereOpacity, 0,0,0); 
		    mySphereClone.setOnMenu(false);
		    mySphereClone.setStartingWithMotion(startingWithMotion);
		    mySphereClone.setMotionStateChangeable(motionStateChangeable);

		    mySphereClone.getCoordinal().getTxProp().set(prototypeNode.getCoordinal().getTxProp().get() + coordinal.getTxProp().get());
		    mySphereClone.getCoordinal().getTyProp().set(prototypeNode.getCoordinal().getTyProp().get() + coordinal.getTyProp().get());
		    mySphereClone.getCoordinal().getTzProp().set(prototypeNode.getCoordinal().getTzProp().get() + coordinal.getTzProp().get());
		    mySphereClone.getCoordinal().getTxmProp().set(prototypeNode.getCoordinal().getTxmProp().get() + coordinal.getTxmProp().get());
		    mySphereClone.getCoordinal().getTymProp().set(prototypeNode.getCoordinal().getTymProp().get() + coordinal.getTymProp().get());
		    mySphereClone.getCoordinal().getTzmProp().set(prototypeNode.getCoordinal().getTzmProp().get() + coordinal.getTzmProp().get());
//		    mySphereClone.getCoordinal().getRxProp().set(getCoordinal().getRxProp().get());
//		    mySphereClone.getCoordinal().getRyProp().set(getCoordinal().getRyProp().get());
//		    mySphereClone.getCoordinal().getRzProp().set(getCoordinal().getRzProp().get());

		    mySphereClone.setTranslations();
		    mySphereClone.backupCoordinalState();
		    mySphereClone.setCoordinalRestorable(coordinalRestorable);
		    mySphereClone.setVisible();
		    mySphereClone.setMotion(motionEnabled);
		    cloneArrayList.add(mySphereClone);
		});
		cloneArrayList.stream().forEach((cloneNode) ->	{ cloneNode.setLifeCycle(superscene.getRunningTime(),hyperSphereLifetime); superscene.addNodeToScene(cloneNode); });
	    } else { constructHyperSphere(); }
//	});
    }
    
//    public void dropHyperSphere()
//    {
//	Platform.runLater(() -> 
//	{
//	    ArrayList<Node3D> scoopedNode3dArrayList = new ArrayList<>();
//	    MyHyperSphere myHyperSphere = new MyHyperSphere(superscene, id, hyperSphereCelestial, caching, verbosity);
//	    scoopedNode3dArrayList.addAll(MyHyperSphere.createHyperSphere(getCoordinal().getLocation(), shells, shellRadius, shellResolution, shellMotion, randomise, hyperSphereCelestial.getRadius(), hyperSphereDivisions, hyperSphereCullFace, hyperSphereDrawMode, hyperSphereColor, hyperSphereOpacity));
//	    scoopedNode3dArrayList.stream().forEach((thisScoopedNode) ->
//	    {
//		thisScoopedNode.setOnMenu(false);
//		thisScoopedNode.setStartingWithMotion(startingWithMotion);
//		thisScoopedNode.setMotionStateChangeable(motionStateChangeable);
//		thisScoopedNode.setMotion(motionEnabled);
//
//		thisScoopedNode.getCoordinal().getTxmProp().set(thisScoopedNode.getCoordinal().getTxmProp().get() + coordinal.getTxmProp().get()); thisScoopedNode.getCoordinal().getTymProp().set(thisScoopedNode.getCoordinal().getTymProp().get() + coordinal.getTymProp().get()); thisScoopedNode.getCoordinal().getTzmProp().set(thisScoopedNode.getCoordinal().getTzmProp().get() + coordinal.getTzmProp().get());
//		thisScoopedNode.getCoordinal().getRxProp().set(getCoordinal().getRxProp().get()); thisScoopedNode.getCoordinal().getRyProp().set(getCoordinal().getRyProp().get()); thisScoopedNode.getCoordinal().getRzProp().set(getCoordinal().getRzProp().get());
//
//		thisScoopedNode.setLifeCycle(superscene.getRunningTime(),hyperSphereLifetime);
//
//		thisScoopedNode.setTranslations();
//		thisScoopedNode.backupCoordinalState();
//		thisScoopedNode.setCoordinalRestorable(coordinalRestorable);
//		thisScoopedNode.setVisible();
////		hyperSphereGroup.getChildren().add(thisScoopedNode.getRootGroup());
//		superscene.addNodeToScene(thisScoopedNode);
//	    }); // End of scoopedNode3dArrayList
//	});
//    }
    
    public double getTranslateX() { return moveGroup.getTranslateX(); }
    public double getTranslateY() { return moveGroup.getTranslateY(); }
    public double getTranslateZ() { return moveGroup.getTranslateZ(); }

    public void setTranslations() { setScale(); setTranslate(); setRotate(); setRotationPivots(); }
    
    public void setScale()  { setScaleX(); setScaleY(); setScaleZ(); }
    public void setScaleX() { moveGroup.setScaleX(coordinal.getSxProp().get()); if (displaying) {scaleXDisplay();} }
    public void setScaleY() { moveGroup.setScaleY(coordinal.getSyProp().get()); if (displaying) {scaleYDisplay();} }
    public void setScaleZ() { moveGroup.setScaleZ(coordinal.getSzProp().get()); if (displaying) {scaleZDisplay();} }
    
    public void setTranslate()  { setTranslateX(); setTranslateY(); setTranslateZ(); }
    public void setTranslateX() { moveGroup.setTranslateX(coordinal.getTxProp().get()); if (displaying) { translateXDisplay(); translateXMDisplay(); } }
    public void setTranslateY() { moveGroup.setTranslateY(coordinal.getTyProp().get()); if (displaying) { translateYDisplay(); translateYMDisplay(); } }
    public void setTranslateZ() { moveGroup.setTranslateZ(coordinal.getTzProp().get()); if (displaying) { translateZDisplay(); translateZMDisplay(); } }

    private void setTransforms()
    {
	if ( ! shearing )
	{
	    moveGroup.getTransforms().removeAll(moveGroup.getTransforms());
	    rotXGroup.getTransforms().removeAll(rotXGroup.getTransforms());
	    rotYGroup.getTransforms().removeAll(rotYGroup.getTransforms());
	    rotZGroup.getTransforms().removeAll(rotZGroup.getTransforms());

	    rotXGroup.getTransforms().add(rxRotate); // Don't add multiple Transforms here, it won't work!!!
	    rotYGroup.getTransforms().add(ryRotate); // Don't add the translates here!!!	    
	    rotZGroup.getTransforms().add(rzRotate);
	}
	else
	{
	    moveGroup.getTransforms().removeAll(moveGroup.getTransforms());
	    rotXGroup.getTransforms().removeAll(rotXGroup.getTransforms());
	    rotYGroup.getTransforms().removeAll(rotYGroup.getTransforms());
	    rotZGroup.getTransforms().removeAll(rotZGroup.getTransforms());

	    moveGroup.getTransforms().add(rxRotate); // Don't add multiple Transforms here, it won't work!!!
	    moveGroup.getTransforms().add(ryRotate); // Don't add the translates here!!!	    
	    moveGroup.getTransforms().add(rzRotate);
	}
    }

    public void setRotate()  { setRotateX(); setRotateY(); setRotateZ(); }
    public void setRotateX()
    {
	if ( !shearing )
	{
	    rxRotate.setAngle(coordinal.getRxProp().get()); 
	}
	else
	{
	    rxRotate.setAngle(coordinal.getRxProp().get());
	}
	if (displaying) { rotateXDisplay(); rotateXMDisplay(); angleXDisplay(); }
    }
    
    public void setRotateY()
    {
	if ( !shearing )
	{
	    ryRotate.setAngle(coordinal.getRyProp().get()); 
	}
	else
	{
	    ryRotate.setAngle(coordinal.getRyProp().get()); 
	}
	if (displaying) { rotateYDisplay(); rotateYMDisplay();	angleYDisplay(); }
    }

    public void setRotateZ()
    {
	if ( !shearing )
	{
	    rzRotate.setAngle(coordinal.getRzProp().get()); 
	}
	else
	{
	    rzRotate.setAngle(coordinal.getRzProp().get()); 
	}
	if (displaying) { rotateZDisplay(); rotateZMDisplay(); angleZDisplay(); }
    }

    public void angleZDisplay(double myx, double myy, double mzx, double mzy) { superscene.getNodeDisplay().mxyDisplay(myx); superscene.getNodeDisplay().mxzDisplay(myy); superscene.getNodeDisplay().myyDisplay(mzx); superscene.getNodeDisplay().myzDisplay(mzy); }

    public void setRotationPivots() { setRotationPivotX(); setRotationPivotY(); setRotationPivotZ(); }
    public void setRotationPivotX() { rxRotate.setPivotX(coordinal.getRPXXProp().get()); rxRotate.setPivotY(coordinal.getRPXYProp().get()); rxRotate.setPivotZ(coordinal.getRPXZProp().get()); }
    public void setRotationPivotY() { ryRotate.setPivotX(coordinal.getRPYXProp().get()); ryRotate.setPivotY(coordinal.getRPYYProp().get()); ryRotate.setPivotZ(coordinal.getRPYZProp().get()); }
    public void setRotationPivotZ() { rzRotate.setPivotX(coordinal.getRPZXProp().get()); rzRotate.setPivotY(coordinal.getRPZYProp().get()); rzRotate.setPivotZ(coordinal.getRPZZProp().get()); }
    
    public void displayAll()
    {
	nodeNameDisplay();
	zoomDisplay();
	distanceDisplay();
	speedDisplay();
	celestialDisplay();
	scaleDisplay();
	translatePowerDisplay();
	translateDisplay();
	translateMotionDisplay();
	rotatePowerDisplay();
	rotateDisplay();
	rotateMotionDisplay();
	angleDisplay();
    }
    
    public void nodeNameDisplay()
    {
	superscene.getNodeDisplay().controlNodeDisplay(getId());
    }
    
    public void zoomDisplay()
    {
    }
    
    protected void distanceDisplay()
    {
		superscene.getNodeDisplay().distanceDisplay(superscene.getDistance(getLocation(), getLastLocation()));
    }
    protected void speedDisplay()
    {
		double lastDistance =	 superscene.getDistance(getLastLocation(),Point3D.ZERO);
		double currentDistance = superscene.getDistance(getLocation(),Point3D.ZERO);
		double trajectDistance = currentDistance - lastDistance;
		superscene.getNodeDisplay().speedDisplay(superscene.getSpeed(trajectDistance, superscene.getMotionRateInterval()));
    }
    
    protected void celestialDisplay()		{superscene.getNodeDisplay().celestialDisplay(getCelestial().getMass(), getCelestial().getRadius(), getCelestial().getPolarity());}
    
    protected void scaleDisplay()		{scaleXDisplay(); scaleYDisplay(); scaleZDisplay(); }
    protected void scaleXDisplay()		{superscene.getNodeDisplay().scaleXDisplay(coordinal.getSxProp().get());}
    protected void scaleYDisplay()		{superscene.getNodeDisplay().scaleYDisplay(coordinal.getSyProp().get());}
    protected void scaleZDisplay()		{superscene.getNodeDisplay().scaleZDisplay(coordinal.getSzProp().get());}
    
    protected void translatePowerDisplay()	{ translateLZPowerDisplay(); translateLXPowerDisplay(); translateLYPowerDisplay(); }
    protected void translateLZPowerDisplay()	{ superscene.getNodeDisplay().translateLZPowerDisplay(getForwardThrottle() - getBackwardThrottle()); }
    protected void translateLXPowerDisplay()	{ superscene.getNodeDisplay().translateLXPowerDisplay(getLeftThrottle() - getRightThrottle()); }
    protected void translateLYPowerDisplay()	{ superscene.getNodeDisplay().translateLYPowerDisplay(getUpThrottle() - getDownThrottle()); }
	
    protected void translateDisplay()		{ translateXDisplay(); translateYDisplay(); translateZDisplay(); }
    protected void translateXDisplay()	    { superscene.getNodeDisplay().translateXDisplay(coordinal.getTxProp().get()); }
    protected void translateYDisplay()	    { superscene.getNodeDisplay().translateYDisplay(coordinal.getTyProp().get()); }
    protected void translateZDisplay()	    { superscene.getNodeDisplay().translateZDisplay(coordinal.getTzProp().get()); }
    
    protected void translateMotionDisplay() { translateXMDisplay(); translateYMDisplay(); translateZMDisplay(); }
    protected void translateXMDisplay()	    { superscene.getNodeDisplay().translateXMDisplay(coordinal.getTxmProp().get()); }
    protected void translateYMDisplay()	    { superscene.getNodeDisplay().translateYMDisplay(coordinal.getTymProp().get()); }
    protected void translateZMDisplay()	    { superscene.getNodeDisplay().translateZMDisplay(coordinal.getTzmProp().get()); }
    
    protected void rotatePowerDisplay()	    { rotateLXPowerDisplay(); rotateLYPowerDisplay(); rotateLZPowerDisplay(); }
    protected void rotateLXPowerDisplay()   { superscene.getNodeDisplay().rotateLXPowerDisplay(getTiltUpThrottle() - getTiltDownThrottle()); }
    protected void rotateLYPowerDisplay()   { superscene.getNodeDisplay().rotateLYPowerDisplay(getPanLeftThrottle() - getPanRightThrottle()); }
    protected void rotateLZPowerDisplay()   { superscene.getNodeDisplay().rotateLZPowerDisplay(getRollLeftThrottle() - getRollRightThrottle()); }

    protected void rotateDisplay()	    { rotateXDisplay(); rotateYDisplay(); rotateZDisplay(); }
    protected void rotateXDisplay()	    { superscene.getNodeDisplay().rotateXDisplay(coordinal.getRxProp().get()); }
    protected void rotateYDisplay()	    { superscene.getNodeDisplay().rotateYDisplay(coordinal.getRyProp().get()); }
    protected void rotateZDisplay()	    { superscene.getNodeDisplay().rotateZDisplay(coordinal.getRzProp().get()); }
    
    protected void rotateMotionDisplay()    { rotateXMDisplay(); rotateYMDisplay(); rotateZMDisplay();  }
    protected void rotateXMDisplay()	    { superscene.getNodeDisplay().rotateXMDisplay(coordinal.getRxmProp().get()); }
    protected void rotateYMDisplay()	    { superscene.getNodeDisplay().rotateYMDisplay(coordinal.getRymProp().get()); }
    protected void rotateZMDisplay()	    { superscene.getNodeDisplay().rotateZMDisplay(coordinal.getRzmProp().get()); }
    
    
    protected void angleDisplay()		{ angleXDisplay(); angleYDisplay(); angleZDisplay(); }
    protected void angleXDisplay()		    
    {
	superscene.getNodeDisplay().angleXDisplay
	(
	    getNodeGroup().getLocalToSceneTransform().getMxy(),
	    getNodeGroup().getLocalToSceneTransform().getMxz(),
	    getNodeGroup().getLocalToSceneTransform().getMyy(),
	    getNodeGroup().getLocalToSceneTransform().getMyz()
	);
    }
    protected void angleYDisplay()
    {
	superscene.getNodeDisplay().angleYDisplay
	(
	    getNodeGroup().getLocalToSceneTransform().getMxx(),
	    getNodeGroup().getLocalToSceneTransform().getMxz(),
	    getNodeGroup().getLocalToSceneTransform().getMzx(),
	    getNodeGroup().getLocalToSceneTransform().getMzz()
	);
    }
    protected void angleZDisplay()	    
    {
	superscene.getNodeDisplay().angleZDisplay
	(
	    getNodeGroup().getLocalToSceneTransform().getMyx(),
	    getNodeGroup().getLocalToSceneTransform().getMyy(),
	    getNodeGroup().getLocalToSceneTransform().getMzx(),
	    getNodeGroup().getLocalToSceneTransform().getMzy()
	);
    }

    public void backupCoordinalState()				    { coordinal.backupTo(bupCoordinal); }
    public void restoreCoordinalState()				    { coordinal.restoreFrom(bupCoordinal); setTranslations(); }
    
    public void flipRotation()					    { shearing = !shearing; setTransforms(); setSwitchShearBold(shearing);}
    
    public String getId()					    { return id; }
    
    public boolean isDisplaying()				    { return displaying; }
    public void setDisplaying()					    { displaying = true; displayAll();}
    public void stopDisplaying()				    { displaying = false; }
    
    public Group getNodeGroup()					    { return nodeGroup;}
    public Group getRotateZGroup()				    { return rotZGroup;}
    public Group getRotateXGroup()				    { return rotXGroup;}
    public Group getRotateYGroup()				    { return rotYGroup;} // rotateYGroup doet het
    public Group getMoveGroup()					    { return moveGroup;}
    public Group getRootGroup()					    { return rootGroup;}

    public boolean  isTiltUp()					    {return tiltUp;}
    public void	    setTiltUp(boolean param)			    {this.tiltUp = param;}
    public boolean  isTiltDown()				    {return tiltDown;}
    public void	    setTiltDown(boolean param)			    {this.tiltDown = param;}
    public boolean  isPanLeft()					    {return panLeft;}
    public void	    setPanLeft(boolean param)			    {this.panLeft = param;}
    public boolean  isPanRight()				    {return panRight;}
    public void	    setPanRight(boolean param)			    {this.panRight = param;}

    public boolean  isScaleUp()					    {return scaleUp;}
    public void	    setScaleUp(boolean scaleUp)			    {this.scaleUp = scaleUp;}
    public boolean  isScaleDown()				    {return scaleDown;}
    public void	    setScaleDown(boolean scaleDown)		    {this.scaleDown = scaleDown;}

    public boolean  isRollLeft()				    {return rollLeft;}
    public void	    setRollLeft(boolean param)			    {this.rollLeft = param;}
    public boolean  isRollRight()				    {return rollRight;}
    public void	    setRollRight(boolean param)			    {this.rollRight = param;}
    
    public boolean  isMainEngineForwardPropulsion()		    { return mainEnginePropulsion; }
    public void	    setMainEngineForwardPropulsion(boolean param)   { mainEnginePropulsion = param; }
    public boolean  isForwardPropulsion()			    {return forwardPropulsion;}
    public void	    setForwardPropulsion(boolean param)		    {this.forwardPropulsion = param;}
    public boolean  isBackwardPropulsion()			    {return backwardPropulsion;}
    public void	    setBackwardPropulsion(boolean param)	    {this.backwardPropulsion = param;}
    public boolean  isLeftPropulsion()				    {return leftPropulsion;}
    public void	    setLeftPropulsion(boolean param)		    {this.leftPropulsion = param;}
    public boolean  isRightPropulsion()				    {return rightPropulsion;}
    public void	    setRightPropulsion(boolean param)		    {this.rightPropulsion = param;}
    public boolean  isUpwardPropulsion()			    {return upwardPropulsion;}
    public void	    setUpwardPropulsion(boolean param)		    {this.upwardPropulsion = param;}
    public boolean  isDownwardPropulsion()			    {return downwardPropulsion;}
    public void	    setDownwardPropulsion(boolean param)	    {this.downwardPropulsion = param;}

    public Point3D getLocation()				    { return location; }
    public Point3D getLastLocation()				    { return lastLocation; }

    public void setLocation(Point3D location)			    { this.location = location; }
    public void setLastLocation(Point3D lastLocation)		    { this.lastLocation = lastLocation; }

    public double getMainEngineThrottle()			    {return mainEngineThrottle;}
    public double getForwardThrottle()				    {return forwardThrottle;}
    public double getBackwardThrottle()				    {return backwardThrottle;}
    public double getLeftThrottle()				    {return leftThrottle;}
    public double getRightThrottle()				    {return rightThrottle;}
    public double getUpThrottle()				    {return upThrottle;}
    public double getDownThrottle()				    {return downThrottle;}
    public double getTiltUpThrottle()				    {return tiltUpThrottle;}
    public double getTiltDownThrottle()				    {return tiltDownThrottle;}
    public double getPanLeftThrottle()				    {return panLeftThrottle;}
    public double getPanRightThrottle()				    {return panRightThrottle;}
    public double getRollLeftThrottle()				    {return rollLeftThrottle;}
    public double getRollRightThrottle()			    {return rollRightThrottle;}
    
    public void	setMainEngineThrottle(double param)		    {mainEngineThrottle = param;}
    public void setForwardThrottle(double param)		    {forwardThrottle = param;}
    public void setBackwardThrottle(double param)		    {backwardThrottle = param;}
    public void setLeftThrottle(double param)			    {leftThrottle = param;}
    public void setRightThrottle(double param)			    {rightThrottle = param;}
    public void setUpThrottle(double param)			    {upThrottle = param;}
    public void setDownThrottle(double param)			    {downThrottle = param;}
    public void setTiltUpThrottle(double param)			    {tiltUpThrottle = param;}
    public void setTiltDownThrottle(double param)		    {tiltDownThrottle = param;}
    public void setPanLeftThrottle(double param)		    {panLeftThrottle = param;}
    public void setPanRightThrottle(double param)		    {panRightThrottle = param;}
    public void setRollLeftThrottle(double param)		    {rollLeftThrottle = param;}
    public void setRollRightThrottle(double param)		    {rollRightThrottle = param;}

    public double getMainEngPwr()				    {return mainEnginePower;}
    public double getTransEngPwr()				    {return translateEnginePower;}
    public double getRotEngPwr()				    {return rotateEnginePower;}
    
    public void setMainEngPwr(double mainEnginePower)		    {this.mainEnginePower = mainEnginePower;}
    public void setTransEngPwr(double param)			    {this.translateEnginePower = param;}
    public void setRotEngPwr(double param)			    {this.rotateEnginePower = param;}

    public Celestial getCelestial()				    { return celestial; }
    public void setCelestial(Celestial celestial)		    { this.celestial = celestial; }

    public boolean isMotionEnabled()				    { return motionEnabled; }
    public void setMotionEnabled(boolean param)			    { this.motionEnabled = param; this.cancelDropSphereTrailTimer(); this.createDropSphereTrailTimer(dropTrailInterval);}
    public boolean isSingleStepMotion()				    { return steppingEnabled; }
    public void setSingleStepMotion(boolean singleStepMotion)	    { this.steppingEnabled = singleStepMotion; }

    public Coordinal getCoordinal()				    { return coordinal; }
    public void setCoordinal(Coordinal param)			    { this.coordinal = param; }

    public Coordinal getBUPCoordinal()				    { return bupCoordinal; }
    public void setBUPCoordinal(Coordinal param)			    { this.bupCoordinal = param; }
 
    public boolean isCamera()					    { return camera; }
    public boolean isLight()					    { return light; }
    public void setCamera(boolean camera)			    { this.camera = camera; }
    public void setLight(boolean light)				    { this.light = light; }

    public boolean isStartingWithMotion()			    {return startingWithMotion;}
    public void setStartingWithMotion(boolean startingWithMotion)   {this.startingWithMotion = startingWithMotion;}

    public boolean isMotionStateChangeable()			    {return motionStateChangeable;}
    public void setMotionStateChangeable(boolean param)		    {this.motionStateChangeable = param;}

    public double getMotionFactor()				    {return motionFactor;}
    public void setMotionFactor(double param)			    {this.motionFactor = param;}
//    public double getGravityFactor()				    {return gravityFactor;}
//    public void setGravityFactor(double param)		    {this.gravityFactor = param;}

    public void switchLight()		{    }
    public void dropPLight()		{    }
    public void dropALight()		{    }
    public void setOpacity(double param)			    { nodeGroup.setOpacity(param); }

    public void setVisibility(boolean param)
    {
	nodeGroup.setVisible(param);
	if (nodeGroup.isVisible())	{visibleLabel.setFont(getFont(FontWeight.BOLD, FontPosture.REGULAR, 10));}
	else				{visibleLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10));}
    }
    
    
    public void createDropSphereTrailTimer(long param)
    {
	if ( dropTrail )
	{
	    this.setDropTrailInterval(param);
	    if (dropSphereTrailLabel != null)   { dropSphereTrailLabel.setFont(getFont(FontWeight.BOLD, FontPosture.REGULAR, 10)); }
	    dropSphereTrailTimer = new Timer();
	    dropSphereTrailTimer.scheduleAtFixedRate(new TimerTask() { @Override public void run() { Platform.runLater(() -> { if (motionEnabled) { dropSphere();} }); } }, 0, Double.doubleToLongBits(Double.longBitsToDouble(dropTrailInterval) / superscene.motionFactor));
	}
    }
    public void cancelDropSphereTrailTimer()			{ if (dropSphereTrailLabel != null) { dropSphereTrailLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); } if (dropSphereTrailTimer != null) { dropSphereTrailTimer.cancel(); } }
    
    
    public void switchDropSphereTrail()				{ if (dropSphereTrailTimer == null) { createDropSphereTrailTimer(dropTrailInterval); } else { cancelDropSphereTrailTimer(); } }
    public void setDropSphereTrail(boolean param1, long param2)	{ if (param1) { dropTrail = param1; createDropSphereTrailTimer(param2); } else { cancelDropSphereTrailTimer(); } }
    public void setDropTrailInterval(long param)		{ dropTrailInterval = param; }
    public void setTrailRadiusPercentage(double param)		{ trailRadiusPercentage = param; }
    public void setTrailDivisions(int param)			{ trailDivisions = param; }
    public void setTrailColor(String color)			{ trailColor = color; }
    public void setTrailOpacity(double param)			{ trailOpacity = param; }
    public void setTrailCullFace(String param)			{ trailCullFace = param; }
    public void setTrailDrawMode(String param)			{ trailDrawMode = param; }
    public void setTrailLength(int param)			{ trailLength = param; }
    
    
//  Hypersphere
    public void setDropHyperSphere(boolean param1, long param2)	{ dropHyperSphereInterval = param2; if (param1) { createDropHyperSphereTimer(); } else { cancelDropHyperSphereTimer(); } }

    public void switchDropHyperSphereTrail()			{ if (!dropHyperSphereTimerOn) { createDropHyperSphereTimer(); } else {cancelDropHyperSphereTimer(); } }
    public void createDropHyperSphereTimer()			{ dropHyperSphereTimer = new Timer(); if (1<=verbosity) { System.out.println("createDropHyperSphereTimer: " + dropHyperSphereInterval); } dropHyperSphereTimer.scheduleAtFixedRate(new TimerTask()   { @Override public void run() { Platform.runLater(() -> { if (motionEnabled) {dropHyperSphere();} }); } }, 0, (long) dropHyperSphereInterval); dropHyperSphereLabel.setFont(getFont(FontWeight.BOLD, FontPosture.REGULAR, 10)); dropHyperSphereTimerOn = true;}
    public void cancelDropHyperSphereTimer()			{ if (dropHyperSphereTimer != null) { dropHyperSphereTimer.cancel(); if (1<=verbosity) { System.out.println("cancelDropHyperSphereTimer()"); } dropHyperSphereLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10));  dropHyperSphereTimerOn = false;}}
    
    public void setHyperSphereInterval(long param)		{ dropHyperSphereInterval = param; }
    public void setHyperSphereLifetime(double param)		{ hyperSphereLifetime = param; }

    public void setHyperSphereShells(int param)			{ shells = param; }
    public void setHyperSphereShellRadius(double param)		{ shellRadius = param; if ( shellRadius > 0) { /*constructHyperSphere();*/ }}
    public void setHyperSphereShellResolution(double param)	{ shellResolution = param; }
    public void setHyperSphereShellMotion(double param)		{ shellMotion = param; }
    public void setHyperSphereRandomise(double param)		{ randomise = param; }
    public void setHyperSphereDivisions(int param)		{ hyperSphereDivisions = param; }
    public void setHyperSphereCullFace(String param)		{ hyperSphereCullFace = param; }
    public void setHyperSphereDrawMode(String param)		{ hyperSphereDrawMode = param; }
    public void setHyperSphereColor(String param)		{ hyperSphereColor = param; }
    public void setHyperSphereOpacity(double param)		{ hyperSphereOpacity = param; }
    public void setHyperSphereCelestial(Celestial param)	{ hyperSphereCelestial = param; }    
    
    
    public void switchVisibility()	{ if (rootGroup.isVisible()) {setInvisible();} else {setVisible();} }
    public void setVisible()		{ rootGroup.setVisible(true); if (visibleLabel!= null) {visibleLabel.setFont(getFont(FontWeight.BOLD, FontPosture.REGULAR, 10)); }}
    public void setInvisible()		{ rootGroup.setVisible(false); if (visibleLabel!= null) {visibleLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); }}
    
    public void switchCaching()		{ setCaching(!isCaching()); }
    public void enableCaching()		{ setCaching(true); }
    public void disableCaching()	{ setCaching(false); }
    
    public void switchEnable()		{ if (rootGroup.isDisabled()) {setEnable(); } else {setDisable(); } }
    public void setEnable()		{ rootGroup.setDisable(false); }
    public void setDisable()		{ rootGroup.setDisable(true); }
    
//    public void addNodeToScene()	{ if (superscene.rootGroup.getChildren().indexOf(rootGroup) == -1) { superscene.rootGroup.getChildren().add(rootGroup); } }
//    public void removeNodeFromScene()	{ if (superscene.rootGroup.getChildren().indexOf(rootGroup) != -1) { superscene.rootGroup.getChildren().remove(rootGroup); } }
	    
    public void switchOnZoomIn()	{    }
    public void switchOffZoomIn()	{    }
    public void switchOnZoomOut()	{    }
    public void switchOffZoomOut()	{    }

    public boolean isCaching()		    { return caching; }
    public void setCaching(boolean caching)
    {
	this.caching = caching;
	if (this.caching)
	{
	    rootGroup.setCache(this.caching);
	    rootGroup.setCacheHint(CacheHint.SPEED);
	    if ( cachingLabel!= null) { cachingLabel.setFont(getFont(FontWeight.BOLD, FontPosture.REGULAR, 10)); }
	}
	else
	{
	    rootGroup.setCache(this.caching);
	    rootGroup.setCacheHint(CacheHint.SPEED);
	    if ( cachingLabel!= null) { cachingLabel.setFont(getFont(FontWeight.NORMAL, FontPosture.REGULAR, 10)); }
	}
	
    }
    private Font getFont(FontWeight fontweight, FontPosture postture, int size) { return Font.font(Font.getDefault().getName(), fontweight, postture, size); }

    protected	boolean isOnMenu()			    { return onMenu; }
    protected   void setOnMenu(boolean param)		    { this.onMenu = param; if (onMenu) { createMenu(); }}
    protected   boolean isCoordinalRestorable()		    { return coordinalRestorable; }
    protected   void setCoordinalRestorable(boolean param)  { this.coordinalRestorable = param; }
    public double getBirth()				    { return birth; }
    public double getLifetime()				    { return lifetime; }
    public double getDeath()				    { return death; }
    
    public Object getNode()				    { return new Object(); }
}