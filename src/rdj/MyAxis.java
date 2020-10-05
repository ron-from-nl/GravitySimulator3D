package rdj;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;

public class MyAxis extends Node3D
{
    private Box rootbox,xbox,ybox,zbox,nodebox;

    public MyAxis(SuperScene sceneInterface, String id, double width, double hight, double depth, Celestial celestial, boolean cache, int verbosity)
    {
	super.superscene = sceneInterface;
	super.celestial = celestial;
	super.camera = false;
	super.light = false;
	super.id = id;
	super.verbosity = verbosity;
	
//	createMenu();

	PhongMaterial rootboxMaterial = new PhongMaterial(); rootboxMaterial.setDiffuseColor(Color.BLACK);
	PhongMaterial xboxMaterial = new PhongMaterial(); xboxMaterial.setDiffuseColor(Color.RED);
	PhongMaterial yboxMaterial = new PhongMaterial(); yboxMaterial.setDiffuseColor(Color.GREEN);
	PhongMaterial zboxMaterial = new PhongMaterial(); zboxMaterial.setDiffuseColor(Color.BLUE);
	PhongMaterial material = new PhongMaterial(); material.setDiffuseColor(Color.BLACK);
	
	int div = 20;
	rootbox = new Box(width, hight, depth); rootbox.setCullFace(CullFace.NONE); rootbox.setMaterial(rootboxMaterial); rootbox.setDrawMode(DrawMode.LINE);
	xbox = new Box(width, hight/div, depth/div); xbox.setCullFace(CullFace.BACK); xbox.setMaterial(xboxMaterial);
	ybox = new Box(width/div, hight, depth/div); ybox.setCullFace(CullFace.BACK); ybox.setMaterial(yboxMaterial);
	zbox = new Box(width/div, hight/div, depth); zbox.setCullFace(CullFace.BACK); zbox.setMaterial(zboxMaterial);
	nodebox = new Box(width/(div/2), hight/(div/2), depth/(div/2)); nodebox.setCullFace(CullFace.NONE); nodebox.setMaterial(material); nodebox.setDrawMode(DrawMode.LINE);
	
	super.moveGroup.getChildren().add(rootbox);
	super.rotXGroup.getChildren().add(xbox);
	super.rotYGroup.getChildren().add(ybox);
	super.rotZGroup.getChildren().add(zbox);
	super.nodeGroup.getChildren().add(nodebox);

	setCaching(cache);
    }

    public Box getNode() { return xbox; }
}