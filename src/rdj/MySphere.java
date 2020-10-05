package rdj;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Sphere;

public class MySphere extends Node3D
{
    private Sphere sphere;

    public MySphere(SuperScene sceneInterface, String id, Celestial celestial, int divisions, String cullface, String drawmode, String color, double alpha, boolean cache, int verbosity)
    {
	super.superscene = sceneInterface;
	super.celestial = celestial;
	super.id = id;
	super.verbosity = verbosity;

//	createMenu();

//  Create Sphere	
	sphere = new Sphere(celestial.getRadius(),divisions);
	
//  Set cullface & drawmode
	sphere.setCullFace(CullFace.valueOf(cullface));
	sphere.setDrawMode(DrawMode.valueOf(drawmode));

//  Set material
	PhongMaterial phongMaterial = new PhongMaterial();
	phongMaterial.setDiffuseColor(Color.web(color,alpha));
	
	sphere.setMaterial(phongMaterial);

	nodeGroup.getChildren().add(sphere);

	setCaching(cache);
    }

    public MySphere(SuperScene sceneInterface, String id, Celestial celestial, int divisions, String cullface, String drawmode, String maptype, Image image, boolean cache, int verbosity)
    {
	super.superscene = sceneInterface;
	super.celestial = celestial;
	super.id = id;
	super.verbosity = verbosity;

//	createMenu();

//  Create Sphere	
	sphere = new Sphere(celestial.getRadius(),divisions);

//  Set cullface & drawmode
	sphere.setCullFace(CullFace.valueOf(cullface));
	sphere.setDrawMode(DrawMode.valueOf(drawmode));

//  Set material
	PhongMaterial phongMaterial = new PhongMaterial();
	
	if (maptype.equalsIgnoreCase("BumpMap")) { phongMaterial.setBumpMap(image); }
	else if (maptype.equalsIgnoreCase("DiffuseMap")) { phongMaterial.setDiffuseMap(image); }
	else if (maptype.equalsIgnoreCase("SelfIlluminationMap")) { phongMaterial.setSelfIlluminationMap(image); }
	else { phongMaterial.setDiffuseMap(image); }
	
	sphere.setMaterial(phongMaterial);
	
	nodeGroup.getChildren().add(sphere);

	setCaching(cache);
    }

    @Override public Sphere getNode() { return sphere; }
}