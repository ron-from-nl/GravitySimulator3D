package rdj;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;

public class MyCylinder extends Node3D
{
    private Cylinder cylinder;

    public MyCylinder(SuperScene sceneInterface, String id, double height, Celestial celestial, int divisions, String cullface, String drawmode, String color, double alpha, boolean cache, int verbosity)
    {
	super.superscene = sceneInterface;
	super.celestial = celestial;
	super.id = id;
	super.verbosity = verbosity;

//	createMenu();

//  Create Sphere	
	cylinder = new Cylinder(celestial.getRadius(),height,divisions);
	
//  Set cullface & drawmode
	cylinder.setCullFace(CullFace.valueOf(cullface));
	cylinder.setDrawMode(DrawMode.valueOf(drawmode));
	
//  Set material
	PhongMaterial phongMaterial = new PhongMaterial();
	phongMaterial.setDiffuseColor(Color.web(color,alpha));
	
	cylinder.setMaterial(phongMaterial);
	nodeGroup.getChildren().add(cylinder);

	setCaching(cache);
    }

    public MyCylinder(SuperScene sceneInterface, String id, double height, Celestial celestial, int divisions, String cullface, String drawmode, String maptype, Image image, boolean cache, int verbosity)
    {
	super.superscene = sceneInterface;
	super.celestial = celestial;
	super.id = id;
	super.verbosity = verbosity;

//	createMenu();

//  Create Sphere	
	cylinder = new Cylinder(celestial.getRadius(),height,divisions);
	
//  Set cullface & drawmode
	cylinder.setCullFace(CullFace.valueOf(cullface));
	cylinder.setDrawMode(DrawMode.valueOf(drawmode));

//  Set material
	PhongMaterial phongMaterial = new PhongMaterial();
	
	if (maptype.equalsIgnoreCase("BumpMap")) { phongMaterial.setBumpMap(image); }
	else if (maptype.equalsIgnoreCase("DiffuseMap")) { phongMaterial.setDiffuseMap(image); }
	else if (maptype.equalsIgnoreCase("SelfIlluminationMap")) { phongMaterial.setSelfIlluminationMap(image); }
	else { phongMaterial.setDiffuseMap(image); }
	
	cylinder.setMaterial(phongMaterial);
	
	nodeGroup.getChildren().add(cylinder);

	setCaching(cache);
    }

    public Cylinder getNode() { return cylinder; }
}