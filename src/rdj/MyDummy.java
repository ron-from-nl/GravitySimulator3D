package rdj;

public class MyDummy extends Node3D
{

    public MyDummy(SuperScene sceneInterface, String id, Celestial celestial, boolean cache, int verbosity)
    {
	super.superscene = sceneInterface;
	super.celestial = celestial;
	super.camera = false;
	super.light = false;
	super.id = id;
	super.verbosity = verbosity;

//	createMenu();

	setCaching(cache);
    }
}