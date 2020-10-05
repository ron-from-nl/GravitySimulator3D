package rdj;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import javafx.application.Preloader;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javax.imageio.ImageIO;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class LoadScene extends SuperScene
{
    private static Path sceneFile;

    private ArrayList<String[]> line;
    private String begin = "";
    private String group = "";
    private boolean load = false;
    private int lsVerbosity = 0;
    private String type = "";
    private long quantity = 1l;
    private String[] lifetime = {"Lifetime", "-1"};
    private boolean onMenu = true;
    private String name = "";
    private String function = "";
    
    // dropTrail
    private boolean dropTrail = false;
    private int trailLength = 0;
    private int trailDivisions = 16;
    private String trailColor = "GREY";
    private String trailCullFace = "BACK";
    private String trailDrawMode = "FILL";
    private String[] trailOpacity = {"TrailOpacity","1.0"};
    private long dropTrailInterval = 100l;
    private String[] trailRadiusPercentage = {"TrailRadiusPercentage","25.0"}; // Relative to mothernode radius
    //(Point3D centerPoint, int shells, double shellRadius, double shellResolution, double shellMotion, double randomise, double hyperSphereRadius,int divisions, String cullFace, String drawMode, String color, double alpha)
    
    // MyHyperSphere or Torus
    private String[] torusRotate = {"TorusRotate","0.0"};
    private int shells = 1;
    private String[] shellRadius = {"ShellRadius","0.0"};
    private String[] shellResolution = {"ShellResolution","10.0"}; // Degrees
    private String[] shellMotion = {"ShellMotion","0.0"}; // centerpoint motion
    private String[] randomise = {"Randomise","0.0"}; // Percentage random shift between nodes
    
    private boolean dropHyperSphere = false;
    private String[] hyperSphereLifetime = {"HyperSphereLifetime", "-1"};
    private int hyperSphereDivisions = 16;
    private String hyperSphereColor = "GREY";
    private String hyperSphereCullFace = "BACK";
    private String hyperSphereDrawMode = "FILL";
    private String[] hyperSphereOpacity = {"HyperSphereOpacity","1.0"};
    private long dropHyperSphereInterval = 100l;
    private String[] hyperSphereRadiusPercentage = {"HyperSphereRadiusPercentage","25.0"}; // Relative to mothernode radius
    
    private String textOrFile = "";
    private String fontName = "Droid Sans";
    private String fontWeight = "NORMAL";
    private String fontPosture = "REGULAR";
    private double fontSize = 16;
    
    private String[] width = {"Width", "0.0"};
    private String[] height = {"Height", "0.0"};
    private String[] depth = {"Depth", "0.0"};
    
    private String[] totalStars = {"TotalStars","0.0"};
    private String[] minStarDistance = {"MinStarDistance","0.0"};
    private String[] maxStarDistance = {"MaxStarDistance","0.0"};
    private boolean selfIlluminating = false;

    private String[] zoomAngle = {"ZoomAngle", "60.0"};
    private int lightType = 0;
//    private int divisions = 0;
    private String[] divisions = {"Divisions", "64.0"};
    private String[] opacity = {"Opacity", "1.0"};
    
    private String cullFace = "BACK";
    private String drawMode = "FILL";
    private String color = "";
    private String mapType = "";

    private String meshFile = "";
    private String meshNodeFilter = "";
    private String imageFile = "";

    private String celestialRef = "";
    private String celestialName = "";
    private String[] celestialMass = {"CelestialMass", "0.0"};
    private String[] celestialRadius = {"CelestialRadius", "0.0"};
    private boolean celestialGravitational = false;
    private boolean celestialCollisional = false;
    private int celestialGroup = 0;
    private int celestialPolarity = 0;

    private String hyperSphereCelestialRef = "";
    private String hyperSphereCelestialName = "";
    private String[] hyperSphereCelestialMass = {"HypeSphereCelestialMass", "0.0"};
    private String[] hyperSphereCelestialRadius = {"HypeSphereCelestialRadius", "0.0"};
    private boolean hyperSphereCelestialGravitational = false;
    private boolean hyperSphereCelestialCollisional = false;
    private int hyperSphereCelestialGroup = 0;
    private int hyperSphereCelestialPolarity = 0;

    private boolean startingWithMotion = false;
    private boolean motionStateChangeble = false;
    private String[] mainEngPwrMassFactor = {"MainEngPwrMassFactor","0.0"};
    private String[] transEngPwrMassFactor = {"TransEngPwrMassFactor","0.0"};
    private String[] rotEngPwrMassFactor = {"RotEngPwrMassFactor","0.0"};
    
    private String[] scaleX = {"ScaleX","1.0"};
    private String[] scaleY = {"ScaleY","1.0"};
    private String[] scaleZ = {"ScaleZ","1.0"};
    private String[] translateX = {"TranslateX","0.0"};
    private String[] translateY = {"TranslateY","0.0"};
    private String[] translateZ = {"TranslateZ","0.0"};
    private String[] translateXMotion = {"TranslateXMotion","0.0"};
    private String[] translateYMotion = {"TranslateYMotion","0.0"};
    private String[] translateZMotion = {"TranslateZMotion","0.0"};
    private String[] rotationX = {"RotationX","0.0"};
    private String[] rotationY = {"RotationY","0.0"};
    private String[] rotationZ = {"RotationZ","0.0"};
    private String[] rotationXMotion = {"RotationXMotion","0.0"};
    private String[] rotationYMotion = {"RotationYMotion","0.0"};
    private String[] rotationZMotion = {"RotationZMotion","0.0"};

    private String[] rotationXX = {"RotationXX","0.0"};
    private String[] rotationXY = {"RotationXY","0.0"};
    private String[] rotationXZ = {"RotationXZ","0.0"};
    private String[] rotationYX = {"RotationYX","0.0"};
    private String[] rotationYY = {"RotationYY","0.0"};
    private String[] rotationYZ = {"RotationYZ","0.0"};
    private String[] rotationZX = {"RotationZX","0.0"};
    private String[] rotationZY = {"RotationZY","0.0"};
    private String[] rotationZZ = {"RotationZZ","0.0"};

    private boolean cached = false;

    private boolean setTranslations = false;
    private boolean backupCoordinal = false;
    private boolean coordinalRestorable = true;
    private boolean addNodeToList = false;
    private boolean fixToNode = false;
    private String  fixToNodeName = "";
    private String  addNodeToGroup = "";
    private boolean setNodeVisible = false;
    private boolean addNodeToScene = false;
    private String  end = "";
    private ArrayList<Node3D> groupedNode3dArrayList;
    private ArrayList<Node3D> scoopedNode3dArrayList;
    private static ScriptEngineManager factory;
    private static ScriptEngine scriptEngine;
        
    public LoadScene(MainStage ms, Path scenefile) throws Exception
    {
	lsVerbosity = 0;
	super.mainstage = ms;
	setupSubscene();
	createSceneSectionVBoxForSceneDisplay();
	createNodeSectionVBoxForSceneDisplay();
	LoadScene.sceneFile = scenefile;
	
	line = new ArrayList<>();

	// Load Objects
	if (1<=ssVerbosity) { System.out.println("LoadScene: " + sceneFile.toString()); }
	Scanner scanner = null; try { scanner = new Scanner(new File(sceneFile.toString())); }	catch (FileNotFoundException ex) { System.err.println("LoadScene() scanner = new Scanner(new File(" + sceneFile.toString() + ")); " + ex); }
	String[] fields;
	while ((scanner!=null)&&(scanner.hasNextLine()))
	{
	    fields = scanner.nextLine().split("\\s+\\=\\s+");
	    if ((fields.length > 1) && (!fields[0].matches("^\\s*#.*"))) { line.add(new String[]{clean(fields[0]), clean(fields[1])}); }
	}
	if (scanner!=null) {scanner.close();}
	
	factory = new ScriptEngineManager();
	scriptEngine = factory.getEngineByName("JavaScript");
	scoopedNode3dArrayList = new ArrayList<>();
	
//	createSceneSectionVBoxForSceneDisplay();
//	createNodeSectionVBoxForSceneDisplay();
	
	lsVerbosity = 0;
	String field[];
	Iterator<String[]> iterator = line.iterator();
	while (iterator.hasNext()) // Looping though (lines) file
	{
	    field = iterator.next();
	    if (1<=lsVerbosity) { System.out.println("Line -> " + field[0] + " = " + field[1]); }//SceneTargetFrameRate

	    if	    ((field[0].equalsIgnoreCase("Begin")) && 
		    (field[1].equalsIgnoreCase("General Settings")))		{ begin = field[1]; resetNodeVars(); }
	    else if ((field[0].equalsIgnoreCase("Begin")) && 
		     (field[1].equalsIgnoreCase("Object")))			{ begin = field[1]; resetNodeVars(); }
	    else if (field[0].equalsIgnoreCase("Verbosity"))			{ if (begin.equalsIgnoreCase("General Settings")) { ssVerbosity = Integer.valueOf(field[1]); } else lsVerbosity = Integer.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase("SceneColor"))			{ setupSubsceneColor(Color.valueOf(field[1])); }
	    else if (field[0].equalsIgnoreCase("StartsWithDisplays"))		{ super.startsWithDisplays = Boolean.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase("StartsWithMotion"))		{ super.startsWithMotion = Boolean.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase("SceneId"))			{ sceneNameId = field[1]; sceneFileId = sceneFile.getFileName().toString(); sceneTitleLabel.setText(sceneNameId);}
	    else if (field[0].equalsIgnoreCase("SceneTargetFrameRate"))		{ super.targetFrameRate = Integer.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase("SceneMotionRate"))		{ super.setMotionRate(Integer.valueOf(field[1])); }
	    else if (field[0].equalsIgnoreCase("SceneMotionFactor"))		{ super.motionFactor = Double.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase("SceneGravityFactor"))		{ super.gravityFactor = Double.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase("Group"))			{ group = field[1]; if (group.equalsIgnoreCase("Begin")) {groupedNode3dArrayList = new ArrayList<>();} }
	    else if (field[0].equalsIgnoreCase("Load"))				{ load = Boolean.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase("Type"))				{ type = field[1]; }
	    else if (field[0].equalsIgnoreCase("Quantity"))			{ quantity = Long.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase(lifetime[0]))			{ lifetime[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase("OnMenu"))			{ onMenu = Boolean.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase("Name"))				{ name = field[1]; }
	    else if (field[0].equalsIgnoreCase("Function"))			{ function = field[1]; }

	    else if (field[0].equalsIgnoreCase(torusRotate[0]))			{ torusRotate[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase("Shells"))			{ shells = Integer.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase(shellRadius[0]))			{ shellRadius[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(shellResolution[0]))		{ shellResolution[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(shellMotion[0]))			{ shellMotion[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(randomise[0]))			{ randomise[1] = field[1]; }
	    
	    else if (field[0].equalsIgnoreCase(opacity[0]))			{ opacity[1] = field[1]; }

	    else if (field[0].equalsIgnoreCase("TextOrFile"))			{ textOrFile = field[1]; }
	    else if (field[0].equalsIgnoreCase("FontName"))			{ fontName = field[1]; }
	    else if (field[0].equalsIgnoreCase("FontWeight"))			{ fontWeight = field[1].toUpperCase(); }
	    else if (field[0].equalsIgnoreCase("FontPosture"))			{ fontPosture = field[1].toUpperCase(); }
	    else if (field[0].equalsIgnoreCase("FontSize"))			{ fontSize = Double.valueOf(field[1]); }

	    else if (field[0].equalsIgnoreCase(width[0]))			{ width[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(height[0]))			{ height[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(depth[0]))			{ depth[1] = field[1]; }
	    
	    else if (field[0].equalsIgnoreCase(totalStars[0]))			{ totalStars[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(minStarDistance[0]))		{ minStarDistance[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(maxStarDistance[0]))		{ maxStarDistance[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase("SelfIlluminating"))		{ selfIlluminating = Boolean.valueOf(field[1]); }

	    else if (field[0].equalsIgnoreCase(zoomAngle[0]))			{ zoomAngle[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase("lightType"))			{ lightType = Integer.valueOf(field[1]); }
//	    else if (field[0].equalsIgnoreCase("divisions"))			{ divisions = Integer.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase("divisions"))			{ divisions[1] = field[1]; }
	    
	    else if (field[0].equalsIgnoreCase("CullFace"))			{ cullFace = field[1].toUpperCase(); }
	    else if (field[0].equalsIgnoreCase("DrawMode"))			{ drawMode = field[1].toUpperCase(); }
	    else if (field[0].equalsIgnoreCase("Color"))			{ color = field[1]; }
	    else if (field[0].equalsIgnoreCase("MapType"))			{ mapType = field[1]; }
	    
	    else if (field[0].equalsIgnoreCase("MeshFile"))			{ meshFile = field[1]; }
	    else if (field[0].equalsIgnoreCase("MeshNodeFilter"))		{ meshNodeFilter = field[1]; }
	    else if (field[0].equalsIgnoreCase("ImageFile"))			{ imageFile = field[1]; }

	    else if (field[0].equalsIgnoreCase("CelestialRef"))			{ celestialRef = field[1]; }
	    else if (field[0].equalsIgnoreCase("CelestialName"))		{ celestialName = field[1]; }
	    else if (field[0].equalsIgnoreCase(celestialMass[0]))		{ celestialMass[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(celestialRadius[0]))		{ celestialRadius[1] = field[1]; }	    
	    else if (field[0].equalsIgnoreCase("CelestialGravitational"))	{ celestialGravitational = Boolean.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase("CelestialCollisional"))		{ celestialCollisional = Boolean.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase("CelestialGroup"))		{ celestialGroup = Integer.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase("CelestialPolarity"))		{ celestialPolarity = Integer.valueOf(field[1]); }

	    else if (field[0].equalsIgnoreCase("StartingWithMotion"))		{ startingWithMotion = Boolean.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase("MotionStateChangeble"))		{ motionStateChangeble = Boolean.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase(mainEngPwrMassFactor[0]))	{ mainEngPwrMassFactor[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(transEngPwrMassFactor[0]))	{ transEngPwrMassFactor[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(rotEngPwrMassFactor[0]))		{ rotEngPwrMassFactor[1] = field[1]; }
	    
	    // Trail
	    else if (field[0].equalsIgnoreCase("DropTrail"))			{ dropTrail = Boolean.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase("TrailDivisions"))		{ trailDivisions = Integer.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase("TrailColor"))			{ trailColor = field[1]; }
	    else if (field[0].equalsIgnoreCase("TrailCullFace"))		{ trailCullFace = field[1].toUpperCase(); }
	    else if (field[0].equalsIgnoreCase("TrailDrawMode"))		{ trailDrawMode = field[1].toUpperCase(); }
	    else if (field[0].equalsIgnoreCase("DropTrailInterval"))		{ dropTrailInterval = Long.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase(trailRadiusPercentage[0]))	{ trailRadiusPercentage[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase("TrailLength"))			{ trailLength = Integer.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase(trailOpacity[0]))		{ trailOpacity[1] = field[1]; }

	    // HyperSphere
    	    else if (field[0].equalsIgnoreCase("DropHyperSphere"))		{ dropHyperSphere = Boolean.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase(hyperSphereLifetime[0]))		{ hyperSphereLifetime[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase("HyperSphereDivisions"))		{ hyperSphereDivisions = Integer.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase("HyperSphereCullFace"))		{ hyperSphereCullFace = field[1].toUpperCase(); }
	    else if (field[0].equalsIgnoreCase("HyperSphereDrawMode"))		{ hyperSphereDrawMode = field[1].toUpperCase(); }
	    else if (field[0].equalsIgnoreCase("HyperSphereColor"))		{ hyperSphereColor = field[1]; }
	    else if (field[0].equalsIgnoreCase(hyperSphereOpacity[0]))		{ hyperSphereOpacity[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(hyperSphereRadiusPercentage[0]))	{ hyperSphereRadiusPercentage[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase("DropHyperSphereInterval"))	{ dropHyperSphereInterval = Long.valueOf(field[1]); }
	    
	    else if (field[0].equalsIgnoreCase("HyperSphereCelestialRef"))		{ hyperSphereCelestialRef = field[1]; }
	    else if (field[0].equalsIgnoreCase("HyperSphereCelestialName"))		{ hyperSphereCelestialName = field[1]; }
	    else if (field[0].equalsIgnoreCase(hyperSphereCelestialMass[0]))		{ hyperSphereCelestialMass[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(hyperSphereCelestialRadius[0]))		{ hyperSphereCelestialRadius[1] = field[1]; }	    
	    else if (field[0].equalsIgnoreCase("HyperSphereCelestialGravitational"))	{ hyperSphereCelestialGravitational = Boolean.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase("HyperSphereCelestialCollisional"))	{ hyperSphereCelestialCollisional = Boolean.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase("HyperSphereCelestialGroup"))		{ hyperSphereCelestialGroup = Integer.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase("HyperSphereCelestialPolarity"))		{ hyperSphereCelestialPolarity = Integer.valueOf(field[1]); }

	    else if (field[0].equalsIgnoreCase(scaleX[0]))			{ scaleX[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(scaleY[0]))			{ scaleY[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(scaleZ[0]))			{ scaleZ[1] = field[1]; }
	    
	    else if (field[0].equalsIgnoreCase(translateX[0]))			{ translateX[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(translateY[0]))			{ translateY[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(translateZ[0]))			{ translateZ[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(translateXMotion[0]))		{ translateXMotion[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(translateYMotion[0]))		{ translateYMotion[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(translateZMotion[0]))		{ translateZMotion[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(rotationX[0]))			{ rotationX[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(rotationY[0]))			{ rotationY[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(rotationZ[0]))			{ rotationZ[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(rotationXMotion[0]))		{ rotationXMotion[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(rotationYMotion[0]))		{ rotationYMotion[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(rotationZMotion[0]))		{ rotationZMotion[1] = field[1]; }

	    else if (field[0].equalsIgnoreCase(rotationXX[0]))			{ rotationXX[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(rotationXY[0]))			{ rotationXY[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(rotationXZ[0]))			{ rotationXZ[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(rotationYX[0]))			{ rotationYX[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(rotationYY[0]))			{ rotationYY[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(rotationYZ[0]))			{ rotationYZ[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(rotationZX[0]))			{ rotationZX[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(rotationZY[0]))			{ rotationZY[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(rotationZZ[0]))			{ rotationZZ[1] = field[1]; }

	    else if (field[0].equalsIgnoreCase("Cached"))			{ cached = Boolean.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase("SetTransitions"))		{ setTranslations = Boolean.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase("BackupCoordinalState"))		{ backupCoordinal = Boolean.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase("CoordinalRestorable"))		{ coordinalRestorable = Boolean.valueOf(field[1]); } // True by default in Node3D
	    else if (field[0].equalsIgnoreCase("AddNodeToList"))		{ addNodeToList = Boolean.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase("SetNodeVisible"))		{ setNodeVisible = Boolean.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase("AddNodeToScene"))		{ addNodeToScene = Boolean.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase("FixToNodeName"))		{ fixToNode = true; fixToNodeName = field[1]; }
	    else if (field[0].equalsIgnoreCase("End"))
	    {
		if ((field[1].equalsIgnoreCase("Object")) && (load))
		{
		    end = field[1];
		    String ns = ""; // namesuffix
		    for (long count = 1; count <= quantity; count++) // Be careful this quantisizes nodes into a multitude of node3d
		    {
			scoopedNode3dArrayList = new ArrayList<>();
			if (quantity > 1) { ns = "_" + count; }
			
			if (function.equalsIgnoreCase("Browser"))
			{
			    if (super.mainstage != null)	{ if (2<=lsVerbosity) { super.mainstage.notifyPreloader(new Preloader.ErrorNotification("info","LoadScene",new Throwable(" Creating " + type + " " + name))); }}
			    if ((celestialRef.length()>0)&&(imageFile.length()>3)) {scoopedNode3dArrayList.add(0, new MyBrowser(this, name+ns, eval(width), eval(height) , Celestials.get(celestialRef,ssVerbosity), fontName, fontWeight, fontPosture, fontSize, eval(opacity), cached, ssVerbosity));}
			    else						{scoopedNode3dArrayList.add(0, new MyBrowser(this, name+ns, eval(width), eval(height) , new Celestial(celestialName,eval(celestialMass),eval(celestialRadius),celestialGravitational, celestialCollisional, celestialGroup, celestialPolarity), fontName, fontWeight, fontPosture, fontSize, eval(opacity), cached, ssVerbosity));}
			}
			else if (function.equalsIgnoreCase("Camera"))
			{
			    if (super.mainstage != null)	{ if (2<=lsVerbosity) { super.mainstage.notifyPreloader(new Preloader.ErrorNotification("info","LoadScene",new Throwable(" Creating " + type + " " + name))); }}
			    if (celestialRef.length()>0)		{scoopedNode3dArrayList.add(0, new MyCamera(this, name+ns, Celestials.get(celestialRef,ssVerbosity), eval(zoomAngle), new ImportModel(super.mainstage, meshFile, meshNodeFilter, ssVerbosity).getGroup(), lightType, cached, ssVerbosity));}
			    else				{scoopedNode3dArrayList.add(0, new MyCamera(this, name+ns, new Celestial(celestialName,eval(celestialMass),eval(celestialRadius),celestialGravitational, celestialCollisional, celestialGroup, celestialPolarity), eval(zoomAngle), new ImportModel(super.mainstage, meshFile, meshNodeFilter, ssVerbosity).getGroup(), 2, cached, ssVerbosity));}
			}
			else if (function.equalsIgnoreCase("Light"))
			{
			    if (super.mainstage != null)	{ if (2<=lsVerbosity) { super.mainstage.notifyPreloader(new Preloader.ErrorNotification("info","LoadScene",new Throwable(" Creating " + type + " " + name))); }}
			    if (celestialRef.length()>0)		{scoopedNode3dArrayList.add(0, new MyLight(this, name+ns, lightType, (int)Math.round(eval(divisions)), Celestials.get(celestialRef,ssVerbosity), cached, ssVerbosity));}
			    else				{scoopedNode3dArrayList.add(0, new MyLight(this, name+ns, lightType, (int)Math.round(eval(divisions)), new Celestial(celestialName,eval(celestialMass),eval(celestialRadius),celestialGravitational, celestialCollisional, celestialGroup, celestialPolarity), cached, ssVerbosity));}
			}
			else if (function.equalsIgnoreCase("Box"))
			{
			    if (super.mainstage != null)	{ if (2<=lsVerbosity) { super.mainstage.notifyPreloader(new Preloader.ErrorNotification("info","LoadScene",new Throwable(" Creating " + type + " " + name))); }}
			    if ((!fileIsReadable(imageFile)) || (mapType.equalsIgnoreCase("DiffuseColor")))
			    {
				if ((celestialRef.length()>0)&&(imageFile.length()>3)) {scoopedNode3dArrayList.add(0, new MyBox(this, name+ns, eval(width), eval(height) , eval(depth), Celestials.get(celestialRef,ssVerbosity), cullFace, drawMode, color, eval(opacity), cached, ssVerbosity));}
				else						    {scoopedNode3dArrayList.add(0, new MyBox(this, name+ns, eval(width), eval(height) , eval(depth), new Celestial(celestialName,eval(celestialMass),eval(celestialRadius),celestialGravitational, celestialCollisional, celestialGroup, celestialPolarity), cullFace, drawMode, color, eval(opacity), cached, ssVerbosity));}
			    }
			    else
			    {
				if ((celestialRef.length()>0)&&(imageFile.length()>3)) {scoopedNode3dArrayList.add(0, new MyBox(this, name+ns, eval(width), eval(height) , eval(depth), Celestials.get(celestialRef,ssVerbosity), cullFace, drawMode, mapType, getImage(imageFile), cached, ssVerbosity));}
				else						    {scoopedNode3dArrayList.add(0, new MyBox(this, name+ns, eval(width), eval(height) , eval(depth), new Celestial(celestialName,eval(celestialMass),eval(celestialRadius),celestialGravitational, celestialCollisional, celestialGroup, celestialPolarity), cullFace, drawMode, mapType, getImage(imageFile), cached, ssVerbosity));}
			    }
			}		
			else if (function.equalsIgnoreCase("Cylinder"))
			{
			    if (super.mainstage != null) { if (2<=lsVerbosity) { super.mainstage.notifyPreloader(new Preloader.ErrorNotification("info","LoadScene",new Throwable(" Creating " + type + " " + name))); }}
			    if ((!fileIsReadable(imageFile)) || (mapType.equalsIgnoreCase("DiffuseColor")))
			    {
				if (celestialRef.length()>0)   {scoopedNode3dArrayList.add(0, new MyCylinder(this, name+ns, eval(height), Celestials.get(celestialRef,ssVerbosity), (int)Math.round(eval(divisions)), cullFace, drawMode, color, eval(opacity), cached, ssVerbosity));}
				else			    {scoopedNode3dArrayList.add(0, new MyCylinder(this, name+ns, eval(height), new Celestial(celestialName,eval(celestialMass),eval(celestialRadius),celestialGravitational, celestialCollisional, celestialGroup, celestialPolarity), (int)Math.round(eval(divisions)), cullFace, drawMode, color, eval(opacity), cached, ssVerbosity));}
			    }
			    else
			    {
				if (celestialRef.length()>0)   {scoopedNode3dArrayList.add(0, new MyCylinder(this, name+ns, eval(height), Celestials.get(celestialRef,ssVerbosity), (int)Math.round(eval(divisions)), cullFace, drawMode, mapType, getImage(imageFile), cached, ssVerbosity));}
				else			    {scoopedNode3dArrayList.add(0, new MyCylinder(this, name+ns, eval(height), new Celestial(celestialName,eval(celestialMass),eval(celestialRadius),celestialGravitational, celestialCollisional, celestialGroup, celestialPolarity), (int)Math.round(eval(divisions)), cullFace, drawMode, mapType, getImage(imageFile), cached, ssVerbosity));}
			    }
			}
			else if (function.equalsIgnoreCase("Sphere")) // Single Sphere
			{
			    if (super.mainstage != null)	{ if (2<=lsVerbosity) { super.mainstage.notifyPreloader(new Preloader.ErrorNotification("info","LoadScene",new Throwable(" Creating " + type + " " + name))); }}
			    if ((!fileIsReadable(imageFile)) || (mapType.equalsIgnoreCase("DiffuseColor")))
			    {
				if (celestialRef.length()>0)	{scoopedNode3dArrayList.add(0, new MySphere(this, name+ns, Celestials.get(celestialRef,ssVerbosity), (int)Math.round(eval(divisions)), cullFace, drawMode, color, eval(opacity), cached, ssVerbosity));}
				else				{scoopedNode3dArrayList.add(0, new MySphere(this, name+ns, new Celestial(celestialName,eval(celestialMass),eval(celestialRadius),celestialGravitational, celestialCollisional, celestialGroup, celestialPolarity), (int)Math.round(eval(divisions)), cullFace, drawMode, color, eval(opacity), cached, ssVerbosity));}
			    }
			    else
			    {
				if (celestialRef.length()>0)	{scoopedNode3dArrayList.add(0, new MySphere(this, name+ns, Celestials.get(celestialRef,ssVerbosity), (int)Math.round(eval(divisions)), cullFace, drawMode, mapType, getImage(imageFile), cached, ssVerbosity));}
				else				{scoopedNode3dArrayList.add(0, new MySphere(this, name+ns, new Celestial(celestialName,eval(celestialMass),eval(celestialRadius),celestialGravitational, celestialCollisional, celestialGroup, celestialPolarity), (int)Math.round(eval(divisions)), cullFace, drawMode, mapType, getImage(imageFile), cached, ssVerbosity));}
			    }
			}
			else if (function.equalsIgnoreCase("HyperSphere")) // Spheres in a HyperSphere shape
			{
			    MyHyperSphere myHyperSphere;
			    if (super.mainstage != null)	{ if (2<=lsVerbosity) { super.mainstage.notifyPreloader(new Preloader.ErrorNotification("info","LoadScene",new Throwable(" Creating " + type + " " + name))); }}
			    if (celestialRef.length()>0)	{ myHyperSphere = new MyHyperSphere(this, name+ns, Celestials.get(celestialRef,ssVerbosity), cached, ssVerbosity);}
			    else				{ myHyperSphere = new MyHyperSphere(this, name+ns, new Celestial(celestialName,eval(celestialMass),eval(celestialRadius),celestialGravitational, celestialCollisional, celestialGroup, celestialPolarity), cached, ssVerbosity);}
			    if ((!fileIsReadable(imageFile)) || (mapType.equalsIgnoreCase("DiffuseColor"))) { scoopedNode3dArrayList.addAll(myHyperSphere.createHyperSphere(new Point3D(0,0,0), shells, eval(shellRadius), eval(shellResolution), eval(shellMotion), eval(randomise), eval(celestialRadius), (int)Math.round(eval(divisions)), cullFace, drawMode, color, eval(opacity))); }
			    else { scoopedNode3dArrayList.addAll(myHyperSphere.createHyperSphere(new Point3D(0,0,0), shells, eval(shellRadius), eval(shellResolution), eval(shellMotion), eval(randomise), eval(celestialRadius), (int)Math.round(eval(divisions)), cullFace, drawMode, color, eval(opacity)));	}
			}
			else if (function.equalsIgnoreCase("Torus")) // Spheres in Torus shape
			{
			    MyHyperSphere myHyperSphere;
			    if (super.mainstage != null)	{ if (2<=lsVerbosity) { super.mainstage.notifyPreloader(new Preloader.ErrorNotification("info","LoadScene",new Throwable(" Creating " + type + " " + name))); }}
			    if (celestialRef.length()>0)	{ myHyperSphere = new MyHyperSphere(this, name+ns, Celestials.get(celestialRef,ssVerbosity), cached, ssVerbosity);}
			    else				{ myHyperSphere = new MyHyperSphere(this, name+ns, new Celestial(celestialName,eval(celestialMass),eval(celestialRadius),celestialGravitational, celestialCollisional, celestialGroup, celestialPolarity), cached, ssVerbosity);}
			    if ((!fileIsReadable(imageFile)) || (mapType.equalsIgnoreCase("DiffuseColor"))) { scoopedNode3dArrayList.addAll(myHyperSphere.createTorus(new Point3D(0,0,0), eval(torusRotate), shells, eval(shellRadius), eval(shellResolution), eval(shellMotion), eval(randomise), eval(celestialRadius), (int)Math.round(eval(divisions)), cullFace, drawMode, color, eval(opacity), true)); }
			    else { scoopedNode3dArrayList.addAll(myHyperSphere.createTorus(new Point3D(0,0,0), eval(torusRotate), shells, eval(shellRadius), eval(shellResolution), eval(shellMotion), eval(randomise), eval(celestialRadius), (int)Math.round(eval(divisions)), cullFace, drawMode, color, eval(opacity), true)); }
			}
			else if (function.equalsIgnoreCase("Mesh"))
			{
			    if (super.mainstage != null)	{ if (2<=lsVerbosity) { super.mainstage.notifyPreloader(new Preloader.ErrorNotification("info","LoadScene",new Throwable(" Creating " + type + " " + name))); }}
			    if (celestialRef.length()>0)	{scoopedNode3dArrayList.add(0, new MyMesh(this, name+ns, Celestials.get(celestialRef,ssVerbosity), getModel(meshFile, meshNodeFilter, ssVerbosity), cached, ssVerbosity));}
			    else				{scoopedNode3dArrayList.add(0, new MyMesh(this, name+ns, new Celestial(celestialName,eval(celestialMass),eval(celestialRadius),celestialGravitational, celestialCollisional, celestialGroup, celestialPolarity), getModel(meshFile, meshNodeFilter, ssVerbosity), cached, ssVerbosity));}
			}		
			else if (function.equalsIgnoreCase("Axis"))
			{
			    if (super.mainstage != null)	{ if (2<=lsVerbosity) { super.mainstage.notifyPreloader(new Preloader.ErrorNotification("info","LoadScene",new Throwable(" Creating " + type + " " + name))); }}
			    if (celestialRef.length()>0)	{scoopedNode3dArrayList.add(0, new MyAxis(this, name+ns, eval(width), eval(height) , eval(depth), Celestials.get(celestialRef,ssVerbosity), cached, ssVerbosity));}
			    else				{scoopedNode3dArrayList.add(0, new MyAxis(this, name+ns, 1, 1 , 1, new Celestial(celestialName,eval(celestialMass),eval(celestialRadius),celestialGravitational, celestialCollisional, celestialGroup, celestialPolarity), cached, ssVerbosity));}
			}		
			else if (function.equalsIgnoreCase("Stars"))
			{
			    if (super.mainstage != null)	{ if (2<=lsVerbosity) { super.mainstage.notifyPreloader(new Preloader.ErrorNotification("info","LoadScene",new Throwable(" Creating " + type + " " + name))); }}
			    if (celestialRef.length()>0)	{scoopedNode3dArrayList.add(0, new MyStars(this, name+ns, Celestials.get(celestialRef,ssVerbosity), eval(totalStars), eval(minStarDistance), eval(maxStarDistance), getImage(imageFile), selfIlluminating, cached, ssVerbosity));}
			    else				{scoopedNode3dArrayList.add(0, new MyStars(this, name+ns, new Celestial(celestialName,eval(celestialMass),eval(celestialRadius),celestialGravitational, celestialCollisional, celestialGroup, celestialPolarity), eval(totalStars), eval(minStarDistance), eval(maxStarDistance), getImage(imageFile), selfIlluminating, cached, ssVerbosity));}
			}		
			else if (function.equalsIgnoreCase("Text"))
			{
			    if (super.mainstage != null)	{ if (2<=lsVerbosity) { super.mainstage.notifyPreloader(new Preloader.ErrorNotification("info","LoadScene",new Throwable(" Creating " + type + " " + name))); }}
			    if (celestialRef.length()>0)	{scoopedNode3dArrayList.add(0, new MyText(this, name+ns, Celestials.get(celestialRef,ssVerbosity), getText(textOrFile), fontName, fontWeight, fontPosture, fontSize, color, eval(opacity), cached, ssVerbosity));}
			    else				{scoopedNode3dArrayList.add(0, new MyText(this, name+ns, new Celestial(celestialName,eval(celestialMass),eval(celestialRadius),celestialGravitational, celestialCollisional, celestialGroup, celestialPolarity), getText(textOrFile), fontName, fontWeight, fontPosture, fontSize, color, eval(opacity), cached, ssVerbosity));}
			}		
			else if (function.equalsIgnoreCase("Dummy"))
			{
			    if (super.mainstage != null)	{ if (2<=lsVerbosity) { super.mainstage.notifyPreloader(new Preloader.ErrorNotification("info","LoadScene",new Throwable(" Creating " + type + " " + name))); }}
			    if (celestialRef.length()>0)	{scoopedNode3dArrayList.add(0, new MyDummy(this, name+ns, Celestials.get(celestialRef,ssVerbosity), cached, ssVerbosity));}
			    else				{scoopedNode3dArrayList.add(0, new MyDummy(this, name+ns, new Celestial(celestialName,eval(celestialMass),eval(celestialRadius),celestialGravitational, celestialCollisional, celestialGroup, celestialPolarity), cached, ssVerbosity));}
			}		
			else
			{
			    if (super.mainstage != null)	{ if (2<=lsVerbosity) { super.mainstage.notifyPreloader(new Preloader.ErrorNotification("info","LoadScene",new Throwable(" Ignoring " + type + " " + name))); }}
			    System.out.println("Ignoring Unknown Node Function: " + function);
			    load = false;
			}		

			if (load) // Load could change during node3d creation right above
			{
			    if (2<=lsVerbosity) { System.out.println("scoopedNode3dArrayList.size: " + scoopedNode3dArrayList.size()); }
			    scoopedNode3dArrayList.stream().forEach((thisScoopedNode) ->
			    {
				thisScoopedNode.setOnMenu(onMenu);
				thisScoopedNode.setOpacity(eval(opacity));
				thisScoopedNode.setStartingWithMotion(startingWithMotion);
				thisScoopedNode.setMotionStateChangeable(motionStateChangeble);

				// Trail
				thisScoopedNode.setTrailLength(trailLength);
				thisScoopedNode.setTrailDivisions(trailDivisions);
				thisScoopedNode.setTrailColor(trailColor);
				thisScoopedNode.setTrailCullFace(trailCullFace);
				thisScoopedNode.setTrailDrawMode(trailDrawMode);
				thisScoopedNode.setTrailOpacity(eval(trailOpacity));
				thisScoopedNode.setDropSphereTrail(dropTrail,dropTrailInterval);
//				thisScoopedNode.setDropSphereTrail(dropTrail,Double.doubleToLongBits(Double.longBitsToDouble(dropTrailInterval) / motionFactor));
				thisScoopedNode.setTrailRadiusPercentage(eval(trailRadiusPercentage));
				
				// HyperSphere
				thisScoopedNode.setDropHyperSphere(dropHyperSphere,dropHyperSphereInterval);
				thisScoopedNode.setHyperSphereLifetime(eval(hyperSphereLifetime));
				thisScoopedNode.setHyperSphereShells(shells);
				thisScoopedNode.setHyperSphereShellRadius(eval(shellRadius));
				thisScoopedNode.setHyperSphereShellResolution(eval(shellResolution));
				thisScoopedNode.setHyperSphereShellMotion(eval(shellMotion));
				thisScoopedNode.setHyperSphereRandomise(eval(randomise));
				thisScoopedNode.setHyperSphereDivisions(hyperSphereDivisions);
				thisScoopedNode.setHyperSphereCullFace(hyperSphereCullFace);
				thisScoopedNode.setHyperSphereDrawMode(hyperSphereDrawMode);
				thisScoopedNode.setHyperSphereColor(hyperSphereColor);
				thisScoopedNode.setHyperSphereOpacity(eval(hyperSphereOpacity));
//				thisScoopedNode.setHyperSphereRadiusPercentage(eval(hyperSphereRadiusPercentage));
				
				if (hyperSphereCelestialRef.length()>0) thisScoopedNode.setHyperSphereCelestial(Celestials.get(hyperSphereCelestialRef,ssVerbosity));
				else thisScoopedNode.setHyperSphereCelestial(new Celestial(hyperSphereCelestialName,eval(hyperSphereCelestialMass),eval(hyperSphereCelestialRadius),hyperSphereCelestialGravitational, hyperSphereCelestialCollisional, hyperSphereCelestialGroup, hyperSphereCelestialPolarity));
				
				thisScoopedNode.setMainEngPwr(thisScoopedNode.getCelestial().getMass() * eval(mainEngPwrMassFactor));	thisScoopedNode.setTransEngPwr(thisScoopedNode.getCelestial().getMass() * eval(transEngPwrMassFactor)); thisScoopedNode.setRotEngPwr(thisScoopedNode.getCelestial().getMass() * eval(rotEngPwrMassFactor));
				thisScoopedNode.getCoordinal().getRPXXProp().set(eval(rotationXX));				thisScoopedNode.getCoordinal().getRPXYProp().set(eval(rotationXY));			thisScoopedNode.getCoordinal().getRPXZProp().set(eval(rotationXZ)); 
				thisScoopedNode.getCoordinal().getRPYXProp().set(eval(rotationYX));				thisScoopedNode.getCoordinal().getRPYYProp().set(eval(rotationYY));			thisScoopedNode.getCoordinal().getRPYZProp().set(eval(rotationYZ)); 
				thisScoopedNode.getCoordinal().getRPZXProp().set(eval(rotationZX));				thisScoopedNode.getCoordinal().getRPZYProp().set(eval(rotationZY));			thisScoopedNode.getCoordinal().getRPZZProp().set(eval(rotationZZ)); 
				thisScoopedNode.getCoordinal().getSxProp().set(eval(scaleX));					thisScoopedNode.getCoordinal().getSyProp().set(eval(scaleY));				thisScoopedNode.getCoordinal().getSzProp().set(eval(scaleZ));
//				thisScoopedNode.getOpCoordinal().getTxProp().set(eval(translateX));				thisScoopedNode.getOpCoordinal().getTyProp().set(eval(translateY));			thisScoopedNode.getOpCoordinal().getTzProp().set(eval(translateZ));
				thisScoopedNode.getCoordinal().getTxProp().set(thisScoopedNode.getCoordinal().getTxProp().get() + eval(translateX));		thisScoopedNode.getCoordinal().getTyProp().set(thisScoopedNode.getCoordinal().getTyProp().get() + eval(translateY));	thisScoopedNode.getCoordinal().getTzProp().set(thisScoopedNode.getCoordinal().getTzProp().get() + eval(translateZ));
//				thisScoopedNode.getOpCoordinal().getTxmProp().set(eval(translateXMotion)/getMotionRate());	thisScoopedNode.getOpCoordinal().getTymProp().set(eval(translateYMotion)/getMotionRate());thisScoopedNode.getOpCoordinal().getTzmProp().set(eval(translateZMotion)/getMotionRate());
				thisScoopedNode.getCoordinal().getTxmProp().set(thisScoopedNode.getCoordinal().getTxmProp().get() + eval(translateXMotion)/getMotionRate());	thisScoopedNode.getCoordinal().getTymProp().set(thisScoopedNode.getCoordinal().getTymProp().get() + eval(translateYMotion)/getMotionRate());	thisScoopedNode.getCoordinal().getTzmProp().set(thisScoopedNode.getCoordinal().getTzmProp().get() + eval(translateZMotion)/getMotionRate());
				thisScoopedNode.getCoordinal().getRxProp().set(eval(rotationX));				thisScoopedNode.getCoordinal().getRyProp().set(eval(rotationY));			thisScoopedNode.getCoordinal().getRzProp().set(eval(rotationZ));
				thisScoopedNode.getCoordinal().getRxmProp().set(eval(rotationXMotion)/getMotionRate());	thisScoopedNode.getCoordinal().getRymProp().set(eval(rotationYMotion)/getMotionRate());	thisScoopedNode.getCoordinal().getRzmProp().set(eval(rotationZMotion)/getMotionRate());

				thisScoopedNode.setLifeCycle(runningTime,eval(lifetime));

				if (setTranslations)		{ thisScoopedNode.setTranslations(); }
				if (backupCoordinal)		{ thisScoopedNode.backupCoordinalState(); }
				if (!coordinalRestorable)	{ thisScoopedNode.setCoordinalRestorable(coordinalRestorable);}
				if (setNodeVisible)		{ thisScoopedNode.setVisible(); } else { thisScoopedNode.setInvisible(); }
				if (fixToNode)			{ nodeList.stream().forEach((thisnode) -> { if (thisnode.getId().equals(fixToNodeName)) {thisnode.getNodeGroup().getChildren().add(thisScoopedNode.getRootGroup()); System.out.println("fixToNodeName = " + fixToNodeName);}}); }
//				--------
				if (group.equalsIgnoreCase("Begin"))
				{
				    groupedNode3dArrayList.add(thisScoopedNode);
				    if (3<=lsVerbosity) { System.out.println("Add Node To Group: " + thisScoopedNode.getId()); } // Add gathered nodegroups later to GroupEnd node
				}
				else if ((group.equalsIgnoreCase("End")) && (groupedNode3dArrayList != null))
				{
				    if (3<=lsVerbosity) { System.out.println("###### groupedNode3dArrayList.size: " + groupedNode3dArrayList.size()); }
				    groupedNode3dArrayList.stream().forEach((thisnode) ->
				    {
					thisScoopedNode.nodeGroup.getChildren().add(thisnode.getRootGroup()); // Add the groupnodes rootgroups to nodegroup of thisscoopednode
				    });
				    resetGroupVars();
				}
				if (addNodeToScene)		{ addNodeToScene(thisScoopedNode); if (3<=lsVerbosity) { System.out.println("addNodeToScene(" + thisScoopedNode.getId()+ ")"); } }
//				--------
			    
			    }); // End of scoopedNode3dArrayList
			} // End of load coordinal / generic settings
		    } // End of quantity loop
//		    verbosity = 0;//		    verbosity = 0;
		} // "End" = "Object" block
	    } // "End" block
	    else { System.err.println("WARNING in file: " + scenefile.toString() + " UNKNOWN KEY: " + field[0]); } 
	} // line iterator block
	
	setupNodeDisplay();
	
	for (Node3D node:nodeList) { if (node.isCamera())
	{
	    setCameraNode((MyCamera)node);
	    setControlNode(node);
	    break;
	} else { System.err.println("Error: No Camera object found in scenefile: " + sceneFile); } }
	mainstage.getSceneDisplayController().frameRateLabel.setText(super.getNum(targetFrameRate,1));
	mainstage.getSceneDisplayController().motionRateLabel.setText(super.getNum(motionRate,1));
	mainstage.getSceneDisplayController().motionFactorLabel.setText(super.getNum(motionFactor,1));
	mainstage.getSceneDisplayController().gravityFactorLabel.setText(super.getNum(gravityFactor,1));

	startScene();
    }

    private Boolean fileIsReadable(String file)
    {
	boolean isEmpty = file.isEmpty();
	if (isEmpty)
	{
	    if (1<=ssVerbosity) { System.err.println("fileIsReadable(String "+ file+ ") fileIsEmpty = " + file.toString()); } return false;
	}
	else
	{
	    FileSystem defaultfs = FileSystems.getDefault();
//	    Path textfile = defaultfs.getPath(System.getProperty("user.dir"),SuperScene.PRODUCT,file);
	    Path textfile = defaultfs.getPath(System.getProperty("user.home"),SuperScene.PRODUCT,file);
	    boolean isRegular = Files.isRegularFile(textfile, LinkOption.NOFOLLOW_LINKS);
	    if (!isRegular) { if (1<=ssVerbosity) { System.err.println("fileIsReadable(String "+ file+ ") isRegular = " + file.toString()); } return false; }
    	    return true;
	}
    }
    
    private static String clean(final String input)
    {
	String output;
	if (input.indexOf("#")>1) { output = input.substring(0, input.indexOf("#")).trim(); } 	else { output = input.trim(); }
	return output;
    }
    
    protected static double eval(String[] str)
    {
        double output = 0;
        Object number = new Object();
	try { number = scriptEngine.eval(str[1]); }
	catch (ScriptException ex) { System.err.println("LoadScene.eval(String[] str): ScriptException: " + ex); }
        finally
        {
            if      (number instanceof Integer) { output = (int) number;}
            else if (number instanceof Long)    { output = (long) number; }
            else if (number instanceof Double)  { output = (double) number; }
            else                                { System.err.println("Error in file: " + sceneFile.toString() + " can not evaluate field: " + str[0] + " = " + str[1]); output = (double) number; }
        }
        return output;
    }

//    private Image getImage(String imageFile)
//    {
//	return new Image(getClass().getResource(imageFile).toExternalForm());
//    }
    
    private Image getImage(String imageFile)
    {
//	Path path = Paths.get(System.getProperty("user.dir"), SuperScene.PRODUCT,imageFile);
	Path path = Paths.get(System.getProperty("user.home"), SuperScene.PRODUCT,imageFile);
	BufferedImage bufImage = null;
	try { bufImage = ImageIO.read(path.toFile()); } catch (IOException ex) { System.err.println(ex.getMessage()); }
	Image image = SwingFXUtils.toFXImage(bufImage, null);
	return image;
    }
    
    private Group getModel(String meshFile, String meshNodeFilter, int verbosity)  { return new ImportModel(super.mainstage, meshFile, meshNodeFilter, verbosity).getGroup(); }
    
    public String getText(String textOrFile)
    {
	String string = new String();
	FileSystem defaultfs = FileSystems.getDefault();
//        Path textfile = defaultfs.getPath(System.getProperty("user.dir"),SuperScene.PRODUCT,textOrFile);
        Path textfile = defaultfs.getPath(System.getProperty("user.home"),SuperScene.PRODUCT,textOrFile);
	try { if ( (Files.exists(textfile)) && (Files.isRegularFile(textfile)) && (Files.size(textfile) > 0) )
	{
	    Scanner scanner = null; try { scanner = new Scanner(new File(textfile.toString())); } catch (FileNotFoundException ex) { System.err.println("Err file: " + textOrFile + ": " + ex); }
	    while ((scanner!=null)&&(scanner.hasNextLine())) { string += scanner.nextLine() + "\r\n"; }
	    if (scanner!=null) {scanner.close();}
	}
	else
	{
	    string = textOrFile;
	}
	} catch (IOException ex) { System.err.println("Err IOExeption: (Files.exists(textfile)): " + textOrFile + ": " + ex); }


	return string;
    }
    
    @Override public void setScenePeriods() {targetFrameRate = 30; setMotionRate(100); realFrameRateInterval = 1000 / targetFrameRate; setMotionRateInterval(1000 / getMotionRate());}

    private void resetNodeVars()
    {
//	begin = ""; // Do not reset (used as statemachine).
//	group = "";
//	lsVerbosity = 0;
	load = false;
	type = "";
	quantity = 1l;
	
	lifetime[0] = "Lifetime"; lifetime[1] = "-1";
	onMenu = true;
	name = "";
	function = "";
	
	torusRotate[0] = "TorusRotate"; torusRotate[1] = "0.0";
	shells = 1;
	shellRadius[0] = "ShellRadius"; shellRadius[1] = "0.0";
	shellResolution[0] = "ShellResolution"; shellResolution[1] = "10.0"; // Degrees
	shellMotion[0] = "ShellMotion"; shellMotion[1] = "0.0"; // centerpoint motion
	randomise[0] = "Randomise"; randomise[1] = "0.0"; // Percentage random shift between nodes
	
	dropHyperSphere = false;
	hyperSphereLifetime[0] = "HyperSphereLifetime"; hyperSphereLifetime[1] = "-1";
	hyperSphereDivisions = 16;
	hyperSphereColor = "GREY";
	hyperSphereCullFace = "BACK";
	hyperSphereDrawMode = "FILL";
	hyperSphereOpacity[0] = "HyperSphereOpacity"; hyperSphereOpacity[1] = "1.0";
	dropHyperSphereInterval = 100l;
	hyperSphereRadiusPercentage[0] = "HyperSphereRadiusPercentage"; hyperSphereRadiusPercentage[1] = "25.0"; // Relative to mothernode radius

	textOrFile = "";
	fontName = "Droid Sans";
	fontWeight = "NORMAL";
	fontPosture = "REGULAR";
    	fontSize = 16;
	
	width[0] = "Width"; width[1] =  "0.0";
	height[0] = "Height"; height[1] =  "0.0";
	depth[0] = "Depth"; depth[1] =  "0.0";
    
	totalStars[0] = "TotalStars"; totalStars[1] = "0.0";
	minStarDistance[0] = "MinStarDistance"; minStarDistance[1] = "0.0";
	maxStarDistance[0] = "MaxStarDistance"; maxStarDistance[1] = "0.0";
	selfIlluminating = false;
	
	zoomAngle[0] = "ZoomAngle"; zoomAngle[1] =  "60.0";
	lightType = 0;
	opacity[0] = "Opacity"; opacity[1] =  "1.0";
	divisions[0] = "Divisions"; divisions[0] = "64.0";
//	divisions = 0;
	
	cullFace = "BACK"; // BACK FRONT NONE
	drawMode = "FILL"; // LINE FILL
	color = "WHITE";
	mapType = "DiffuseColor"; // DiffuseColor DiffuseMap SelfIlluminationMap BumpMap
	
	meshFile = "";
	meshNodeFilter = "";
	imageFile = "";
	
	celestialRef = "";
	celestialName = "";
	celestialMass[0] = "CelestialMass"; celestialMass[1] =  "0.0";
	celestialRadius[0] = "CelestialRadius"; celestialRadius[1] =  "0.0";
	celestialGravitational = false;
	celestialCollisional = false;
	celestialGroup = 0;
	celestialPolarity = 0;
		
	hyperSphereCelestialRef = "";
	hyperSphereCelestialName = "";
	hyperSphereCelestialMass[0] = "HyperSphereCelestialMass"; celestialMass[1] =  "0.0";
	hyperSphereCelestialRadius[0] = "HyperSphereCelestialRadius"; celestialRadius[1] =  "0.0";
	hyperSphereCelestialGravitational = false;
	hyperSphereCelestialCollisional = false;
	hyperSphereCelestialGroup = 0;
	hyperSphereCelestialPolarity = 0;
		
	startingWithMotion = false;
	motionStateChangeble = false;
	mainEngPwrMassFactor[0] = "MainEngPwrMassFactor"; mainEngPwrMassFactor[1] = "0.0";
	transEngPwrMassFactor[0] = "TransEngPwrMassFactor"; transEngPwrMassFactor[1] = "0.0";
	rotEngPwrMassFactor[0] = "RotEngPwrMassFactor"; rotEngPwrMassFactor[1] = "0.0";
	dropTrail = false;
	trailDivisions = 16;
	trailColor = "GREY";
	trailCullFace = "BACK";
	trailDrawMode = "FILL";
	dropTrailInterval = 100l;
	trailRadiusPercentage[0] = "TrailRadiusPercentage"; trailRadiusPercentage[1] = "25.0"; // Relative to mothernode radius
	trailLength = 0;
	trailOpacity[0] = "TrailOpacity"; trailOpacity[1] = "1.0";

	scaleX[0] = "ScaleX"; scaleX[1] = "1.0";
	scaleY[0] = "ScaleY"; scaleY[1] = "1.0";
	scaleZ[0] = "ScaleZ"; scaleZ[1] = "1.0";
	translateX[0] = "TranslateX"; translateX[1] = "0.0";
	translateY[0] = "TranslateY"; translateY[1] = "0.0";
	translateZ[0] = "TranslateZ"; translateZ[1] = "0.0";
	translateXMotion[0] = "TranslateXMotion"; translateXMotion[1] = "0.0";
	translateYMotion[0] = "TranslateYMotion"; translateYMotion[1] = "0.0";
	translateZMotion[0] = "TranslateZMotion"; translateZMotion[1] = "0.0";
	rotationX[0] = "RotationX"; rotationX[1] = "0.0";
	rotationY[0] = "RotationY"; rotationY[1] = "0.0";
	rotationZ[0] = "RotationZ"; rotationZ[1] = "0.0";
	rotationXMotion[0] = "RotationXMotion"; rotationXMotion[1] = "0.0";
	rotationYMotion[0] = "RotationYMotion"; rotationYMotion[1] = "0.0";
	rotationZMotion[0] = "RotationZMotion"; rotationZMotion[1] = "0.0";
	rotationXX[0] = "RotationXX"; rotationXX[1] = "0.0";
	rotationXY[0] = "RotationXY"; rotationXY[1] = "0.0";
	rotationXZ[0] = "RotationXZ"; rotationXZ[1] = "0.0";
	rotationYX[0] = "RotationYX"; rotationYX[1] = "0.0";
	rotationYY[0] = "RotationYY"; rotationYY[1] = "0.0";
	rotationYZ[0] = "RotationYZ"; rotationYZ[1] = "0.0";
	rotationZX[0] = "RotationZX"; rotationZX[1] = "0.0";
	rotationZY[0] = "RotationZY"; rotationZY[1] = "0.0";
	rotationZZ[0] = "RotationZZ"; rotationZZ[1] = "0.0";
	
	cached = false;

	setTranslations = false;
	backupCoordinal = false;
	coordinalRestorable = true;
	addNodeToList = false;
	fixToNode = false;
	fixToNodeName = "";
	addNodeToGroup = "";
	setNodeVisible = false;
	addNodeToScene = false;
	end = "";
    }
        
    private void resetGroupVars()		    {group = ""; groupedNode3dArrayList.clear();}
    
    protected static ScriptEngine getScriptEngine() {return scriptEngine;}
//    @Override protected void keyPressedHandler(KeyEvent keyEvent)
//    {
//    }
//    
//    @Override protected void keyReleasedHandler(KeyEvent keyEvent)
//    {
//    }
}
