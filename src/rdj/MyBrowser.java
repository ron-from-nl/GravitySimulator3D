package rdj;

import javafx.scene.layout.VBox;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class MyBrowser extends Node3D
{
//    private Box box;

    public MyBrowser(SuperScene sceneInterface, String id, double width, double height, Celestial celestial, String fontName, String fontWeight, String fontPosture, double fontSize, double alpha, boolean cache, int verbosity)
    {
	super.superscene = sceneInterface;
	super.celestial = celestial;
	super.camera = false;
	super.light = false;
	super.id = id;
	super.verbosity = verbosity;

//	createMenu();

	VBox vbox = super.superscene.getSceneListVBox(0d, width, height, fontName, FontWeight.valueOf(fontWeight), FontPosture.valueOf(fontPosture), fontSize, false, true);
	vbox.setTranslateX(-vbox.getWidth()/2);
	vbox.setTranslateY(-vbox.getHeight()/2);
	
	nodeGroup.getChildren().add(vbox);

	setCaching(cache);
    }
}