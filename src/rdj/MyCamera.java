package rdj;

import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MyCamera extends Node3D
{
    private final PerspectiveCamera pcamera;
    private final PointLight plight;
    private final AmbientLight alight;
    private static final double minViewAngle = 1.01;
    private static final double maxViewAngle = 100;
    private Timer zoomInTimer;
    private Timer zoomOutTimer;
//    private double magnifyFactor =1;
    private static final double zoomStepDegreesPerSec = 20;
    private boolean zoomIn;
    private boolean zoomOut;
    private final DoubleProperty zoomProp;
//    private final Node[] camMesh;
    private final Group camMeshGroup;
    private int ltype;
    private static final int NOLIGHT = 0;
    private static final int AMBIENTLIGHT = 1;
    private static final int POINTLIGHT = 2;
    
    public MyCamera(SuperScene superscene, String id, Celestial celestial, double zoomAngle, int ltype, boolean cache, int verbose) // was superscene
    {
//	this.camMesh = null;
	super.verbosity = verbose;
	camMeshGroup = null;
	
	super.superscene = superscene;
	super.celestial = celestial;
	super.camera = true;
	super.light = true;
	super.id = id;
	super.verbosity = verbose;

//	createMenu();
	
	zoomProp = new SimpleDoubleProperty(this, "zoomProp", zoomAngle);
	pcamera = new PerspectiveCamera(true);
	setZoom(zoomProp);
	pcamera.setNearClip(0.2);
	pcamera.setFarClip(10000000000000d);
	
	
	nodeGroup.getChildren().addAll(pcamera);
	
	this.ltype = ltype;
	plight = new PointLight(Color.WHITE); plight.setLightOn(false);
	alight = new AmbientLight(Color.WHITE); alight.setLightOn(false);

	if	(ltype == NOLIGHT)	{ alight.setLightOn(false); plight.setLightOn(false); }
	else if (ltype == AMBIENTLIGHT)	{ alight.setLightOn(true); plight.setLightOn(false); }
	else if (ltype == POINTLIGHT)	{ alight.setLightOn(false); plight.setLightOn(true); }
	else				{ alight.setLightOn(false); plight.setLightOn(false); }

	nodeGroup.getChildren().addAll(plight,alight);
//	nodeGroup.getChildren().addAll(camMeshGroup);
	
	setCaching(cache);
    }
    
    public MyCamera(SuperScene superscene, String id, Celestial celestial, double zoomAngle, Group group, int ltype, boolean cache, int verbosity) // was superscene
    {
	super.superscene = superscene;
	super.celestial = celestial;
	super.camera = true;
	super.light = true;
	super.id = id;
	super.verbosity = verbosity;
//	createMenu();
	
	zoomProp = new SimpleDoubleProperty(this, "zoomProp", zoomAngle);
	pcamera = new PerspectiveCamera(true);
	setZoom(zoomProp);
	pcamera.setNearClip(0.1);
	pcamera.setFarClip(10000000000000d);
	
	
	camMeshGroup = group;
	nodeGroup.getChildren().addAll(pcamera);

	this.ltype = ltype;
	plight = new PointLight(Color.WHITE); plight.setLightOn(false);
	alight = new AmbientLight(Color.WHITE); alight.setLightOn(false);

	if	(ltype == NOLIGHT)	{ alight.setLightOn(false); plight.setLightOn(false); }
	else if (ltype == AMBIENTLIGHT)	{ alight.setLightOn(true); plight.setLightOn(false); }
	else if (ltype == POINTLIGHT)	{ alight.setLightOn(false); plight.setLightOn(false); }
	else				{ alight.setLightOn(false); plight.setLightOn(false); }

//	this.camMesh = camMesh;
//	camMeshGroup = new Group(camMesh);
	
	nodeGroup.getChildren().addAll(plight,alight);
	nodeGroup.getChildren().addAll(camMeshGroup);
	
	setCaching(cache);
    }
    
    // Zoom
    @Override synchronized public void mouseScroll(ScrollEvent scrollEvent) // Inherited from motherclass Node3D
    {
	double zoomMotion = scrollEvent.getDeltaY() / zoomStepDegreesPerSec;
	if (zoomMotion > 0)
	{
	    if (zoomProp.get() + zoomMotion <= maxViewAngle)
	    {
		zoomProp.set(zoomProp.get() + (zoomMotion/magnifyFactor)); setZoom(zoomProp); zoomDisplay();
	    } else {zoomProp.set(maxViewAngle); setZoom(zoomProp); zoomDisplay(); }
	}
	else // < 0
	{
	    if (zoomProp.get() + zoomMotion >= minViewAngle)
	    {
		zoomProp.set(zoomProp.get() + (zoomMotion/magnifyFactor)); setZoom(zoomProp); zoomDisplay();
	    } else {zoomProp.set(minViewAngle); setZoom(zoomProp); zoomDisplay(); }	    
	}
    }

    @Override public void setVisibility(boolean param)
    {
	if (camMeshGroup!=null)
	{
	    camMeshGroup.setVisible(param);
	    if (camMeshGroup.isVisible())	{visibleLabel.setFont(Font.font(Font.getDefault().getName(),FontWeight.BOLD, 10));}
	    else				{visibleLabel.setFont(Font.font(Font.getDefault().getName(),FontWeight.NORMAL, 10));}
	}
    }
	    
    @Override public void switchVisibility()
    {
	if (camMeshGroup!=null)
	{
	    camMeshGroup.setVisible(!camMeshGroup.isVisible());
	    if (camMeshGroup.isVisible())	{visibleLabel.setFont(Font.font(Font.getDefault().getName(),FontWeight.BOLD, 10));}
	    else				{visibleLabel.setFont(Font.font(Font.getDefault().getName(),FontWeight.NORMAL, 10));}
	}
    }
	    
    @Override synchronized public void switchOnZoomIn()
    {
	if (! zoomIn)
	{
	    zoomIn=true; nodeZoomInLabel.setFont(Font.font(Font.getDefault().getName(),FontWeight.BOLD, 10)); zoomInTimer = new Timer();
	    zoomInTimer.scheduleAtFixedRate(new TimerTask() {@Override public void run() { Platform.runLater(() -> 
	    {
		if (zoomProp.get() > minViewAngle) { zoomProp.set(zoomProp.get() - ((zoomStepDegreesPerSec / superscene.getRealFrameRate())/magnifyFactor)); setZoom(zoomProp); zoomDisplay(); }
		else {zoomProp.set(minViewAngle); setZoom(zoomProp); zoomDisplay(); }
	    }); }}, 0,  /*superscene.realFrameRateInterval*/ 100l);
	}
    }
    @Override synchronized public void switchOffZoomIn() {zoomIn=false; nodeZoomInLabel.setFont(Font.font(Font.getDefault().getName(),FontWeight.NORMAL, 10)); zoomInTimer.cancel();}

    @Override synchronized public void switchOnZoomOut()
    {
	if (! zoomOut)
	{
	    zoomOut=true; zoomOutTimer = new Timer(); nodeZoomOutLabel.setFont(Font.font(Font.getDefault().getName(),FontWeight.BOLD, 10));
	    zoomOutTimer.scheduleAtFixedRate(new TimerTask() {@Override public void run() { Platform.runLater(() -> 
	    {
		if (zoomProp.get() < maxViewAngle) { zoomProp.set(zoomProp.get() + ((zoomStepDegreesPerSec / superscene.getRealFrameRate())/magnifyFactor)); setZoom(zoomProp); zoomDisplay(); }
		else {zoomProp.set(maxViewAngle); setZoom(zoomProp); zoomDisplay(); }
	    }); }}, 0, /*superscene.realFrameRateInterval*/ 100l);
	}
    }
    @Override synchronized public void switchOffZoomOut() {zoomOut=false; nodeZoomOutLabel.setFont(Font.font(Font.getDefault().getName(),FontWeight.NORMAL, 10)); zoomOutTimer.cancel();}

    synchronized private void setZoom(DoubleProperty zoomProp) {pcamera.setFieldOfView(zoomProp.get()); zoomDisplay();}

    @Override synchronized public void zoomDisplay()
    {
	magnifyFactor = (1 / (pcamera.getFieldOfView() / maxViewAngle));
	if (displaying) {superscene.getNodeDisplay().zoomDisplay(magnifyFactor, pcamera.getFieldOfView());}
    }
    
    @Override public void switchLight()
    {
	if	    ((! plight.isLightOn()) && (! alight.isLightOn())) { plight.setLightOn(true);  alight.setLightOn(false); super.setSwitchLightBold(true); }
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

    public PerspectiveCamera getNode()		    { return pcamera; }
    public PointLight getLight()		    { return plight; }
    
    @Override public void dropPLight()
    {
	PointLight pointLight = new PointLight(Color.WHITE);
	superscene.rootGroup.getChildren().add(pointLight);
	pointLight.setTranslateX(getCoordinal().getTxProp().get());pointLight.setTranslateY(getCoordinal().getTyProp().get());pointLight.setTranslateZ(getCoordinal().getTzProp().get());
    }
    
    @Override public void dropALight()
    {
	AmbientLight ambientLight = new AmbientLight(Color.WHITE);
	superscene.rootGroup.getChildren().add(ambientLight);
	ambientLight.setTranslateX(getCoordinal().getTxProp().get());ambientLight.setTranslateY(getCoordinal().getTyProp().get());ambientLight.setTranslateZ(getCoordinal().getTzProp().get());
    }
    
    @Override public void setDisplaying()	    { displaying = true; displayAll();}
    @Override public void stopDisplaying()	    { displaying = false; }

    @Override public void switchEnable()	    { if (pcamera.isDisabled()) {setEnable(); } else {setDisable(); } }
    @Override public void setEnable()		    { pcamera.setDisable(false); }
    @Override public void setDisable()		    { pcamera.setDisable(true); }
    
    public double getMagnifyFactor()		    {return magnifyFactor;}
}