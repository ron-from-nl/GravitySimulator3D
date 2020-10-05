package rdj;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;

public class MyBox extends Node3D
{
    private Box box;

    public MyBox(SuperScene sceneInterface, String id, double width, double hight, double depth, Celestial celestial, String cullface, String drawmode, String color, double alpha, boolean cache, int verbosity)
    {
	super.superscene = sceneInterface;
	super.celestial = celestial;
	super.camera = false;
	super.light = false;
	super.id = id;
	super.verbosity = verbosity;

//	createMenu();

	box = new Box(width, hight, depth);
	
//  Set cullface & drawmode
	box.setCullFace(CullFace.valueOf(cullface));
	box.setDrawMode(DrawMode.valueOf(drawmode));
	
//  Set material
	PhongMaterial phongMaterial = new PhongMaterial();
	phongMaterial.setDiffuseColor(Color.web(color,alpha));

	box.setMaterial(phongMaterial);
	
	nodeGroup.getChildren().add(box);

	setCaching(cache);
    }

    public MyBox(SuperScene sceneInterface, String id, double width, double hight, double depth, Celestial celestial, String cullface, String drawmode, String maptype, Image image, boolean cache, int verbosity)
    {
	super.superscene = sceneInterface;
	super.celestial = celestial;
	super.camera = false;
	super.light = false;
	super.id = id;
	super.verbosity = verbosity;

//	createMenu();

	box = new Box(width, hight, depth);
	
//  Set cullface & drawmode
	box.setCullFace(CullFace.valueOf(cullface));
	box.setDrawMode(DrawMode.valueOf(drawmode));
	
//  Set material
	PhongMaterial phongMaterial = new PhongMaterial();
	
	if (maptype.equalsIgnoreCase("BumpMap")) { phongMaterial.setBumpMap(image); }
	else if (maptype.equalsIgnoreCase("DiffuseMap")) { phongMaterial.setDiffuseMap(image); }
	else if (maptype.equalsIgnoreCase("SelfIlluminationMap")) { phongMaterial.setSelfIlluminationMap(image); }
	else { phongMaterial.setDiffuseMap(image); }
	
	box.setMaterial(phongMaterial);
	
	nodeGroup.getChildren().add(box);

	setCaching(cache);
    }

    public Box getNode() { return box; }
}