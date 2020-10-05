package rdj;

import javafx.scene.AmbientLight;
import javafx.scene.PointLight;
//import javafx.scene.effect.Lighting;
import javafx.scene.paint.Color;

public class MyLight extends Node3D
{
    private PointLight plight;
    private AmbientLight alight;
    private int ltype;
    private static final int NOLIGHT = 0;
    private static final int AMBIENTLIGHT = 1;
    private static final int POINTLIGHT = 2;
//    private Effect effect;
//    private Lighting lighting;
    
//    private Sphere sphere;

//    public MyLight(SuperScene sceneInterface, String id, int type, int divisions, Celestial celestial, String filename, boolean cache)
    public MyLight(SuperScene sceneInterface, String id, int ltype, int divisions, Celestial celestial, boolean cache, int verbosity)
    {
	super.superscene = sceneInterface;
	super.celestial = celestial;
	super.camera = false;
	super.light = true;
	super.id = id;
	super.verbosity = verbosity;
//	this.type = ltype;

//	createMenu();

//	lighting = new Lighting();
//	Image image =	new Image(GravitySimulator3D.class.getResource(filename).toExternalForm());
//	PhongMaterial phongMaterial = new PhongMaterial();
//	phongMaterial.setDiffuseMap(image);

	this.ltype = ltype;
	plight = new PointLight(Color.WHITE); plight.setLightOn(false);
	alight = new AmbientLight(Color.WHITE); alight.setLightOn(false);
	nodeGroup.getChildren().addAll(alight,plight);
	
	if	(ltype == NOLIGHT)	{ alight.setLightOn(false); plight.setLightOn(false); }
	else if (ltype == AMBIENTLIGHT)	{ alight.setLightOn(true); plight.setLightOn(false); }
	else if (ltype == POINTLIGHT)	{ alight.setLightOn(false); plight.setLightOn(true); }
	else				{ alight.setLightOn(false); plight.setLightOn(false); }

//	sphere = new Sphere(celestial.getRadius(),divisions);
//	sphere.setDrawMode(DrawMode.FILL);
//	sphere.setCullFace(CullFace.BACK);
//	sphere.setMaterial(phongMaterial);
//	nodeGroup.getChildren().add(sphere);

	setCaching(cache);
    }

    @Override public void switchLight()		
    {
	if	((! plight.isLightOn()) && (! alight.isLightOn())) { plight.setLightOn(true);  alight.setLightOn(false); super.setSwitchLightBold(true); }
	else if ((! plight.isLightOn()) && (  alight.isLightOn())) { plight.setLightOn(false); alight.setLightOn(false); super.setSwitchLightBold(false); }
	else if ((  plight.isLightOn()) && (! alight.isLightOn())) { plight.setLightOn(false); alight.setLightOn(true); super.setSwitchLightBold(true); }
	else if ((  plight.isLightOn()) && (  alight.isLightOn())) { plight.setLightOn(false); alight.setLightOn(false); super.setSwitchLightBold(false); }
    }
    @Override public void setLight(boolean param)
    {
	if	(ltype == NOLIGHT)	{ alight.setLightOn(false); plight.setLightOn(false); super.setSwitchLightBold(false);}
	else if (ltype == AMBIENTLIGHT)	{ alight.setLightOn(param); plight.setLightOn(false); super.setSwitchLightBold(true);}
	else if (ltype == POINTLIGHT)	{ alight.setLightOn(false); plight.setLightOn(param); super.setSwitchLightBold(true);}
	else				{ alight.setLightOn(false); plight.setLightOn(false); super.setSwitchLightBold(false);}
    }
    private AmbientLight getAmbientLight() { return alight; }
    private PointLight getLight() { return plight; }

//    public Sphere getNode() { return sphere; }
}