package rdj;

import javafx.scene.Group;
import javafx.scene.Node;

public class MyMesh extends Node3D
{
//    private Image image;
//    private PhongMaterial phongMaterial;
    public static final int DAE = 0;
    public static final int OBJ = 1;
    public static final int STL = 2;
    
    public MyMesh(SuperScene sceneInterface, String id, Celestial celestial, Group group, boolean cache, int verbosity)
    {
	super.superscene = sceneInterface;
	super.celestial = celestial;
	super.id = id;
	super.verbosity = verbosity;

//	createMenu();
	
	nodeGroup.getChildren().add(group);
	
	setCaching(cache);
    }
}