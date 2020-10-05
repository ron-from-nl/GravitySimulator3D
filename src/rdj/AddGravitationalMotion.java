package rdj;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;

public class AddGravitationalMotion
{
    private double gxm;
    private double gym;
    private double gzm;
    private double motionRate;
    private double gravitationalMotion;
    
    public AddGravitationalMotion(Node3D targetnode,ObservableList<Node3D> nodeObservableList,double motionRate, double gravityFactor)
    {
	// Better performance without
	Platform.runLater(() -> 
	{
	    gxm = 0;
	    gym = 0;
	    gzm = 0;
//	    if (!SuperScene.nodesAreBeingRemovedNow) { nodeObservableList.stream().filter((refnode) -> ((refnode.getCelestial().isGravitational()) && (refnode.isMotionEnabled()) && (sourcenode != refnode))).forEach((refnode) ->
	    if (true) { nodeObservableList.stream().filter((refnode) -> ((refnode.getCelestial().isGravitational()) && (refnode.isMotionEnabled()) && (targetnode != refnode))).forEach((refnode) ->
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
//		     System.out.println(distance + " < dist getDistance(sourcenode.getLocation(" + targetnode.getLocation() + "), innernode.getLocation(" + refnode.getLocation() + "))");

		    gravitationalMotion = getGravitationalMotion(refnode.getCelestial().getMass(), distance) / motionRate; // interval rate division
//		     System.out.println("gravitationalMotion(" + gravitationalMotion + ") = getGravitationalMotion(refnode.getCelestial().getMass(" + refnode.getCelestial().getMass() + "), distance(" + distance + ")) / motionRate(" + motionRate + ")");

		    if ((targetnode.getCelestial().getPolarity() != 0) && (refnode.getCelestial().getPolarity() != 0)) // Calculate polarized (anti-)gravitation
		    {
			double antiGravitationalMotion = Math.negateExact(targetnode.getCelestial().getPolarity() * refnode.getCelestial().getPolarity()) * gravitationalMotion;
//			System.out.println("vector.getX(): " + vector.getX() + " antiGravitationalMotion: " + antiGravitationalMotion + " gravityFactor: " +gravityFactor + " motionRate: " +motionRate);
//			System.out.println("vector.getY(): " + vector.getY() + " antiGravitationalMotion: " + antiGravitationalMotion + " gravityFactor: " +gravityFactor + " motionRate: " +motionRate);
//			System.out.println("vector.getZ(): " + vector.getZ() + " antiGravitationalMotion: " + antiGravitationalMotion + " gravityFactor: " +gravityFactor + " motionRate: " +motionRate);
			gxm += ((vector.getX() * antiGravitationalMotion) * gravityFactor / motionRate);
			gym += ((vector.getY() * antiGravitationalMotion) * gravityFactor / motionRate);
			gzm += ((vector.getZ() * antiGravitationalMotion) * gravityFactor / motionRate);
//			 System.out.println(" -> " + refnode.getCelestial().getName() + " motion x,y,z: " + gxm + "," + gym + "," + gzm + " a-gmotion: " + antiGravitationalMotion);
		    }
		    else
		    {
//			System.out.println("vector.getX(): " + vector.getX() + " gravitationalMotion: " + gravitationalMotion + " gravityFactor: " +gravityFactor + " motionRate: " +motionRate);
//			System.out.println("vector.getY(): " + vector.getY() + " gravitationalMotion: " + gravitationalMotion + " gravityFactor: " +gravityFactor + " motionRate: " +motionRate);
//			System.out.println("vector.getZ(): " + vector.getZ() + " gravitationalMotion: " + gravitationalMotion + " gravityFactor: " +gravityFactor + " motionRate: " +motionRate);
			gxm += ((vector.getX() * gravitationalMotion) * gravityFactor/ motionRate);
			gym += ((vector.getY() * gravitationalMotion) * gravityFactor/ motionRate);
			gzm += ((vector.getZ() * gravitationalMotion) * gravityFactor/ motionRate);
//			 System.out.println(" -> " + refnode.getCelestial().getName() + " motion x,y,z: " + gxm + "," + gym + "," + gzm + "   gmotion: " + gravitationalMotion);
		    }
		}
	    }); }
//	     System.out.println(targetnode.getCelestial().getName() + " totalmotion " + " x,y,z: " + gxm + "," + gym + "," + gzm);
	    targetnode.addMotion(gxm, gym, gzm); // Adds gravitational motion to the nodes motion
	});
    }
    private double getGravitationalMotion(double mass, double radius)		{ return (Celestials.getGravitConst() * mass / Math.pow(radius, 2)); }	// m/Sec
    private static Point3D getUnifiedVector(Point3D refpoint, Point3D targetpoint) // Unify sum of motion to 1.0
    {
	double high = (Math.abs(refpoint.getX() - targetpoint.getX()) + Math.abs(refpoint.getY() - targetpoint.getY()) + Math.abs(refpoint.getZ() - targetpoint.getZ()));
	Point3D vectorUnified = new Point3D((refpoint.getX() - targetpoint.getX()) / high,(refpoint.getY() - targetpoint.getY()) / high,(refpoint.getZ() - targetpoint.getZ()) / high);
//	System.out.println("vectorUnified: " + vectorUnified.toString());
        return vectorUnified;
    }
    private
     double getDistance(Point3D point1, Point3D point2) {	return Math.sqrt(Math.pow(point2.getX() - point1.getX(), 2) + Math.pow(point2.getY() - point1.getY(), 2) + Math.pow(point2.getZ() - point1.getZ(), 2)); }
}
