package rdj;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import static javafx.application.Application.launch;
import javafx.geometry.Point3D;

public class MyHyperSphere
{
//    private Group sceneGroup;
    private static final double PIMIN = -(Math.PI-(Math.PI/2d)); // -(..) brackets very important
    private static final double PIMAX =  (Math.PI-(Math.PI/2d));
    private static SuperScene superscene;
    private static String id;
    private static Celestial celestial;
    private static boolean cached;
    private static int verbosity = 0;
    
    public MyHyperSphere(SuperScene sceneInterface, String id, Celestial celestial, boolean cached, int verbosity)
    {
	MyHyperSphere.superscene = sceneInterface;
	MyHyperSphere.id = id;
	MyHyperSphere.celestial = celestial;
	MyHyperSphere.cached = cached;
	MyHyperSphere.verbosity = verbosity;
    }

    public synchronized static ArrayList<MySphere> createHyperSphere(Point3D centerPoint, int shells, double shellRadius, double shellResolution, double shellMotion, double randomise, double hyperSphereRadius,int divisions, String cullFace, String drawMode, String color, double alpha)
    {
//        if (1<=verbosity)  { System.out.println("createSpheres(shells "+shells+", shellRadius "+shellRadius+", shellResolution "+shellResolution+", randomise "+randomise+", sphereRadius "+sphereRadius+",divisions "+divisions+", cullFace "+cullFace+", drawMode "+drawMode+", color "+color+", alpha "+alpha+", verbosity "+verbosity+")"); }
        if (1<=verbosity)  { System.out.println("createSpheres(....)"); }
        ArrayList<MySphere> mySphereArrayList = new ArrayList<>();
        for (int shellCounter = 1; shellCounter <= shells; shellCounter++)
        {
            if (1<=verbosity) { System.out.println("Creating HyperSphere: " + shellCounter + " ShellRadius: " + shellRadius + " SinalResolution: " + shellResolution/* + "\nHyperSphereCelestial:\n" + celestial.toString()*/); }
            for (double sinalStep = PIMIN; sinalStep < PIMAX; sinalStep += getSinalStep(shellResolution)) // Creates a single sphere
            {
//                mySphereArrayList.addAll(createTorus(centerPoint,sinalStep,1,shellRadius,shellResolution,shellMotion,randomise,hyperSphereRadius,divisions, cullFace, drawMode, color, alpha, false));
                mySphereArrayList.addAll(createTorus(new Point3D(0,0,0),sinalStep,1,shellRadius,shellResolution,shellMotion,randomise,hyperSphereRadius,divisions, cullFace, drawMode, color, alpha, false));
            }
            shellRadius += getResolutionDist(shellRadius,shellResolution);
        }
        if (1<=verbosity) System.out.println("Created " + shells + " shells holding " + mySphereArrayList.size() + " nodes"); 
        
	// At this point the spheres are positioned directly for intersecting detection
//	removeSharingPositionNodes(sphereArrayList);
	removeIntersectingNodes(mySphereArrayList);
	migrateSphere2CoordinalPositions(mySphereArrayList); // migrate direct position to moveGroup position
	
	return mySphereArrayList;
    }
    
    public synchronized static ArrayList<MySphere> createTorus(Point3D centerPoint, double torusRotate, int shells, double shellRadius, double shellResolution, final double shellMotion, double randomise, double sphereRadius,int divisions, String cullFace, String drawMode, String color, double alpha, boolean deDuplicate)
    {
        ArrayList<MySphere> mySphereArrayList = new ArrayList<>(); 
        double x,y,z; x=0; y=0; z=0;
        double ranx,rany,ranz; ranx=0; rany=0; ranz=0;
        for (int torusShellCounter = 1; torusShellCounter <= shells; torusShellCounter++)
        {
	    if (deDuplicate)	{ if (1<=verbosity) { System.out.println("Creating torusShell: " + torusShellCounter + " TorusRadius: " + shellRadius + " Sinal: " + torusRotate + "\nTorusCelestial:\n" + celestial.toString()); } } // Direct Call
	    else		{ if (3<=verbosity) { System.out.println("Creating torusShell: " + torusShellCounter + " Radius: " + shellRadius + " Sinal: " + torusRotate); } } // Call From createShell
            for (double sinalStep = PIMIN; sinalStep < (PIMAX + getSinalStep(shellResolution)); sinalStep += getSinalStep(shellResolution)) // Creating a single torus
            {
                x = centerPoint.getX() + Math.sin(sinalStep) *   Math.cos(torusRotate) *   shellRadius;
                y = centerPoint.getY() + Math.cos(sinalStep) * /*Math.sin(torusRotate) **/ shellRadius;
                z = centerPoint.getZ() + Math.sin(sinalStep) *   Math.sin(torusRotate) *   shellRadius;

		if ((randomise > 0) && (randomise <= 100))
		{
		    x += ((Math.random() * getResolutionDist(shellRadius,shellResolution) - (getResolutionDist(shellRadius,shellResolution) / 2)) * randomise / 100);
		    y += ((Math.random() * getResolutionDist(shellRadius,shellResolution) - (getResolutionDist(shellRadius,shellResolution) / 2)) * randomise / 100);
		    z += ((Math.random() * getResolutionDist(shellRadius,shellResolution) - (getResolutionDist(shellRadius,shellResolution) / 2)) * randomise / 100);
		}

		mySphereArrayList.add(makeMySphere(shellRadius, shellResolution, divisions, cullFace, drawMode, color, alpha, x, -y, z));
		mySphereArrayList.add(makeMySphere(shellRadius, shellResolution, divisions, cullFace, drawMode, color, alpha, x,  y, z));
							
            }
            shellRadius += getResolutionDist(shellRadius,shellResolution);
        }

	if (deDuplicate)
	{
	    // At this point the spheres are positioned directly for intersecting detection
//	    removeSharingPositionNodes(sphereArrayList);
	    removeIntersectingNodes(mySphereArrayList);
	    migrateSphere2CoordinalPositions(mySphereArrayList); // migrate direct position to moveGroup position
	}
	if (shellMotion != 0d) { mySphereArrayList.stream().forEach((thisnode) -> { addCenterPointMotion(thisnode, centerPoint, shellMotion); });}
	
        return mySphereArrayList;
    }
    
    // This method behaves a bit like superscene.gravityHandler(), but instead of gravitational acceleration it adds fixed centerpoint inward or outward motion / momentum like radiation
    private static void addCenterPointMotion(Node3D targetnode, Point3D centerPoint, double shellMotion) // (m/Sec) Gets called by MySpheres to mimic graviton radiation
    {
	double gxm = 0; double gym = 0; double gzm = 0;
	Point3D vector = getUnifiedVector(targetnode.getCoordinal().getLocation(), centerPoint);
	gxm = ((vector.getX() * shellMotion / superscene.getMotionRate()));
	gym = ((vector.getY() * shellMotion / superscene.getMotionRate()));
	gzm = ((vector.getZ() * shellMotion / superscene.getMotionRate()));
	targetnode.addMotion(gxm, gym, gzm); // Adds motion to the nodes motion
//	targetnode.setMotion(gxm, gym, gzm); // Sets initial centerpoint motion of the node
	if (10<=verbosity) { System.out.println("3)  SS.motionRate" + superscene.getMotionRate() + " node: " + targetnode.getId() + " shellMotion: " + shellMotion + " xm - ym - zm " + gxm+" - "+gym+" - "+gzm); }
	if (10<=verbosity) { System.out.println("motion: " + getNum(gxm,3) + " - " + getNum(gym,3) + " - " + getNum(gzm,3)); }
    }
    
    public static Point3D getUnifiedVector(Point3D refpoint, Point3D targetpoint) // Unify sum of motion to 1.0
    {
	double high = (Math.abs(refpoint.getX() - targetpoint.getX()) + Math.abs(refpoint.getY() - targetpoint.getY()) + Math.abs(refpoint.getZ() - targetpoint.getZ()));
	Point3D vectorUnified = new Point3D((refpoint.getX() - targetpoint.getX()) / high,(refpoint.getY() - targetpoint.getY()) / high,(refpoint.getZ() - targetpoint.getZ()) / high);
	if (10<=verbosity) { System.out.println("vectorUnified: " + vectorUnified.toString()); }
        return vectorUnified;
    }
    
    public static double getDistance(Point3D point1, Point3D point2) {	return Math.sqrt(Math.pow(point2.getX() - point1.getX(), 2) + Math.pow(point2.getY() - point1.getY(), 2) + Math.pow(point2.getZ() - point1.getZ(), 2)); }

    public static String getNum(double num, int decimal) { return String.format("%."+Integer.toString(decimal)+"f", num); }
    public synchronized static ArrayList<MySphere> removeSharingPositionNodes(ArrayList<MySphere> mySphereArrayList)
    {
        double dx,dy,dz; dx=0; dy=0; dz=0;
        double sx,sy,sz; sx=0; sy=0; sz=0;
        String s = ":";
        
        if (1<=verbosity) System.out.println("Identifying doubles from " + mySphereArrayList.size() + " nodes...");
        ArrayList<Integer> removeList = new ArrayList<>();
        
        for(int source=0; source < mySphereArrayList.size(); source++)
        {
            int matchCounter = 0;
            sx = mySphereArrayList.get(source).getCoordinal().getTxProp().get();
            sy = mySphereArrayList.get(source).getCoordinal().getTyProp().get();
            sz = mySphereArrayList.get(source).getCoordinal().getTzProp().get();
            
            for(int dest=0; dest < mySphereArrayList.size(); dest++)
            {
		dx = mySphereArrayList.get(dest).getCoordinal().getTxProp().get();
		dy = mySphereArrayList.get(dest).getCoordinal().getTyProp().get();
		dz = mySphereArrayList.get(dest).getCoordinal().getTzProp().get();
//		if((sx==dx) && (sy==dy) && (sz==dz))    { matchCounter++; if (matchCounter >1) { removeList.add(dest); }}
		if((Objects.equals(sx, dx)) && (Objects.equals(sy, dy)) && (Objects.equals(sz, dz)))    { matchCounter++; if (matchCounter >1) { removeList.add(dest); }} // untested!!!
            }
        }
        if (3<=verbosity) System.out.println("Removing " + removeList.stream().distinct().sorted(Comparator.naturalOrder()).sorted(Comparator.reverseOrder()).toArray().length + " of " +  mySphereArrayList.size() + " doubles...");
        for (Object myint:removeList.stream().distinct().sorted(Comparator.naturalOrder()).sorted(Comparator.reverseOrder()).toArray()) { mySphereArrayList.remove((int)myint); }
        if (1<=verbosity) System.out.println("Total nodes in list now is: " + mySphereArrayList.size());
        
        return mySphereArrayList;
    }

    public synchronized static ArrayList<MySphere> removeIntersectingNodes(ArrayList<MySphere> mySphereArrayList)
    {
        MySphere sMySphere, dMySphere; String s = ":";
        if (1<=verbosity) System.out.println("Identifying Intersectings from " + mySphereArrayList.size() + " nodes...");
        ArrayList<Integer> removeList = new ArrayList<>();
        
        for(int source=0; source < mySphereArrayList.size(); source++)
        {
            int matchCounter = 0;
            sMySphere = mySphereArrayList.get(source);
            
            for(int dest=0; dest < mySphereArrayList.size(); dest++)
            {
		dMySphere = mySphereArrayList.get(dest);
                if ( sMySphere.getNode().getBoundsInParent().intersects(dMySphere.getNode().getBoundsInParent()) )  { matchCounter++; if (matchCounter >1) { removeList.add(dest); }} // Works
//                if ( sMySphere.getMoveGroup().getBoundsInParent().intersects(dMySphere.getMoveGroup().getBoundsInParent()) )  { matchCounter++; if (matchCounter >1) { removeList.add(dest); }} // Works
            }
	    
        }
        if (3<=verbosity) System.out.println("Removing " + removeList.stream().distinct().sorted(Comparator.naturalOrder()).sorted(Comparator.reverseOrder()).toArray().length + " of " +  mySphereArrayList.size() + " doubles...");
        for (Object myint:removeList.stream().distinct().sorted(Comparator.naturalOrder()).sorted(Comparator.reverseOrder()).toArray()) { mySphereArrayList.remove((int)myint); }
        if (1<=verbosity) System.out.println("Total nodes in list now is: " + mySphereArrayList.size());
        mySphereArrayList.forEach((thisnode) -> { thisnode.getNode().setRadius(thisnode.getCelestial().getRadius()); });
	
        return mySphereArrayList;
    }
    
    private static double getAngleRange()                                          { return Math.PI-(Math.PI/2) - (-Math.PI-(Math.PI/2)); }
    private static double getAngleDivisions(double angleResolution)                { return 360/angleResolution; }
    private static double getSinalStep(double degrees)                             { return getAngleRange() / getAngleDivisions(degrees); }
    
    private static double getCircumference(double radius)                          { return radius * 2 * Math.PI; }
    private static double getResolutionDist(double radius, double angleResolution) { return getCircumference(radius) / getAngleDivisions(angleResolution); }
    
    protected static MySphere makeMySphere(double shellRadius, double shellResolution, int divisions, String cullFace, String drawMode, String color, double opacity, double x, double y, double z)
    {
	if (10<=verbosity) {System.out.println("makeMySphere(int " + divisions + ", String " + cullFace + ", String " + drawMode + ", String " + color + ", double " + opacity + ", double " + x + ", double " + y + ", double " + z + ")");}
	MySphere mySphere = new MySphere(superscene, id, celestial, divisions, cullFace, drawMode, color, opacity, cached, verbosity);
	
	// Set coordinal to hypersphere location
	mySphere.getCoordinal().getTxProp().set(x);
	mySphere.getCoordinal().getTyProp().set(y);
	mySphere.getCoordinal().getTzProp().set(z);
//	mySphere.setTranslate();
	
	// Set shape to hypersphere location for removeIntersectingNodes
	mySphere.getNode().setTranslateX(x);
	mySphere.getNode().setTranslateY(y);
	mySphere.getNode().setTranslateZ(z);
//	
	mySphere.getNode().setRadius((getResolutionDist(shellRadius,shellResolution) / 2)*0.4); // Proportion the node's radius and line it up with its neighbours for the removeIntersectings routine 
	
	return mySphere;
    }
    
    // Sets shape back to 0,0,0 and the node's moveGroup to hypersphere location
    private static void migrateSphere2CoordinalPositions(ArrayList<MySphere> mySphereArrayList) // migrate direct position to moveGroup position
    {
	if (2<=verbosity) {System.out.println("resetSpheresToCoordinalPosition(ArrayList<MySphere> mySphereArrayList)");}
	mySphereArrayList.stream().forEach((mySphere) ->
	{
	    mySphere.getNode().setTranslateX(0); 
	    mySphere.getNode().setTranslateY(0); 
	    mySphere.getNode().setTranslateZ(0);
	    mySphere.setTranslations();
	});
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
