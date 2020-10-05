package rdj;

import java.util.ArrayList;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Sphere;

public class MyStars extends Node3D
{
//    private PointLight pointLight;
//    private AmbientLight ambientLight;
    
//    public Sphere sphere;

    private PhongMaterial phongMaterial;
    private Group starsGroup;
    private double tx, ty, tz = 0;
    private ArrayList<Sphere> sphereArrayList;
    private Sphere sphere;
//    private final Image image;
    
    public MyStars(SuperScene sceneInterface, String id, Celestial celestial, double total, double minDistance, double maxDistance, Image image, boolean selfillumin, boolean cache, int verbosity)
    {
	super.superscene = sceneInterface;
	super.celestial = celestial;
	super.camera = false;
	super.light = false;
	super.id = id;
	super.verbosity = verbosity;

//	createMenu();

	phongMaterial = new PhongMaterial();
	
	if (selfillumin) {phongMaterial.setSelfIlluminationMap(image);} else {phongMaterial.setDiffuseMap(image);}

	starsGroup = new Group();
	sphereArrayList=new ArrayList<>();
	
	for (int index=0; index < total; index++)
	{
	    tx = getRandom(0, maxDistance);
	    ty = getRandom(0, maxDistance);
	    tz = getRandom(0, maxDistance);
	    
	    sphereArrayList.add(new Sphere(celestial.getRadius(),32));
	    sphereArrayList.get(index).setTranslateX(tx);
	    sphereArrayList.get(index).setTranslateY(ty);
	    sphereArrayList.get(index).setTranslateZ(tz);
	    sphereArrayList.get(index).setDrawMode(DrawMode.FILL);
	    sphereArrayList.get(index).setMaterial(phongMaterial);
	    sphereArrayList.get(index).setCache(true); sphereArrayList.get(index).setCacheHint(CacheHint.SPEED);

	    nodeGroup.getChildren().add(sphereArrayList.get(index));
	    
	    setCaching(cache);
	}
    }

    private double getRandom(double min, double max)
    {
	// min 2 max 10
	// 0.9 
	// -0.3
	double num = (double)(Math.random() * (max - min) + min);
	int dice = (int) (Math.random() * 100);	if (dice >= 50) {num = -num;}
	return num; 
    }

//    public Group getRootGroup() { return starsGroup; }
    public Group getRootGroup() { return moveGroup; }
    public Sphere getNode() { return sphere; }
}