package rdj;

import javafx.scene.input.MouseEvent;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class NodeDisplay
{
    private SuperScene superscene;
    private Rectangle bgRectangle;
    protected String precision;
    private String sxPrefix,syPrefix,szPrefix,tlxpPrefix,tlypPrefix,tlzpPrefix,txmPrefix,tymPrefix,tzmPrefix,txPrefix,tyPrefix,tzPrefix;
    private String rlxpPrefix,rlypPrefix,rlzpPrefix,rxmPrefix,rymPrefix,rzmPrefix,rxPrefix,ryPrefix,rzPrefix;
    private String mxxPrefix,mxyPrefix,mxzPrefix,myxPrefix,myyPrefix,myzPrefix,mzxPrefix,mzyPrefix,mzzPrefix;
    private String nxxPrefix,nxyPrefix,nxzPrefix,distPrefix,distSuffix,speedPrefix,speedSuffix;

    protected Label sceneLabel, statusLabel, camLabel, zoomFactLabel, fieldOfViewLabel, mainEngineForwardPowerLabel, nxxLabel, nxyLabel, nxzLabel;
    protected Label c0Label, c1Label, c2Label, c3Label, c4Label, c5Label, c6Label, c7Label, c8Label, c9Label, c10Label, c11Label;
    protected Label nodeLabel, massLabel, sxLabel, tlzpLabel, txmLabel, txLabel, rlxpLabel, rxmLabel, rxLabel, mxxLabel, myxLabel, mzxLabel;
    protected Label speedLabel, radiusLabel, syLabel, tlxpLabel, tymLabel, tyLabel, rlypLabel, rymLabel, ryLabel, mxyLabel, myyLabel, mzyLabel;
    protected Label distLabel, polarityLabel, szLabel, tlypLabel, tzmLabel, tzLabel, rlzpLabel, rzmLabel, rzLabel, mxzLabel, myzLabel, mzzLabel;

    protected TextField sceneTField, statusTField, camTField, zoomFactTField, fieldOfViewTField, mainEngineForwardPowerTField, nxxTField, nxyTField, nxzTField;
    protected TextField c0TField, c1TField, c2TField, c3TField, c4TField, c5TField, c6TField, c7TField, c8TField, c9TField, c10TField, c11TField;
    protected TextField nodeTField, massTField, sxTField, tlzpTField, txmTField, txTField, rlxpTField, rxmTField, rxTField, mxxTField, myxTField, mzxTField;
    protected TextField speedTField, radiusTField, syTField, tlxpTField, tymTField, tyTField, rlypTField, rymTField, ryTField, mxyTField, myyTField, mzyTField;
    protected TextField distTField, polarityTField, szTField, tlypTField, tzmTField, tzTField, rlzpTField, rzmTField, rzTField, mxzTField, myzTField, mzzTField;

    protected HBox labelRow1HBox, labelRow2HBox, labelRow3HBox, labelRow4HBox, labelRow5HBox;
    protected HBox tfieldRow1HBox, tfieldRow2HBox, tfieldRow3HBox, tfieldRow4HBox, tfieldRow5HBox;

    protected VBox labelVBox;
    protected VBox tfieldVBox;
    
    protected StackPane stackpane;
    
    protected Group rootGroup;

    protected Rectangle2D rectangle2D;
    protected double screenwidth, screenheight;
    
    private double rectangleWidth;
    private double rectangleHeight; // 20 / line
    private double labelWidth = 100;
    private double labelHeight = 20;
    private double tfieldWidth = 100;
    private double tfieldHeight = 20;
    private double displayHidingBaseY;
    private double displayShowingBaseY;
    
    private TranslateTransition hideTransition, showTransition;
    private boolean showing = true;
    EventHandler<MouseEvent> mouseClickedHandler;

    public NodeDisplay(SuperScene superscene, boolean show) // Gets shown in GravitySimulator3D.setStageScene
    {
	this.superscene = superscene;

	labelVBox = createLabelVBox();
	tfieldVBox = createTFieldVBox();
	createFieldEvents();
	
	rectangleWidth = Math.max(labelRow5HBox.getChildren().size() * labelWidth,tfieldRow5HBox.getChildren().size() * tfieldWidth) + 10;
	rectangleHeight = Math.max(labelVBox.getChildren().size() * labelHeight,tfieldVBox.getChildren().size() * tfieldHeight);
	bgRectangle = createBGRect(rectangleWidth, rectangleHeight);

	stackpane = new StackPane(bgRectangle,tfieldVBox,labelVBox); // Last one gets mouseevents

	rootGroup = new Group(stackpane);
	rootGroup.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { disableMouseMovement(); });

	loadScreen();
	
	double duration=0.2;
	showTransition = new TranslateTransition(Duration.seconds(duration), rootGroup);
	showTransition.setInterpolator(Interpolator.EASE_BOTH);
        showTransition.setFromY(displayHidingBaseY);
        showTransition.setToY(displayShowingBaseY);
        showTransition.setCycleCount(1);

	hideTransition = new TranslateTransition(Duration.seconds(duration), rootGroup);
	hideTransition.setInterpolator(Interpolator.EASE_BOTH);
        hideTransition.setFromY(displayShowingBaseY);
        hideTransition.setToY(displayHidingBaseY);
        hideTransition.setCycleCount(1);
	hideTransition.setOnFinished((ActionEvent arg0) -> { showing = false; rootGroup.setVisible(showing); });

	showDisplay(show);
    }
    
    public void loadScreen()
    {
	screenwidth = superscene.getSubScene().getWidth();
	screenheight = superscene.getSubScene().getHeight();
	
	displayHidingBaseY = ((screenheight / 2) + (rectangleHeight / 2) + 20);
	displayShowingBaseY = displayHidingBaseY-rectangleHeight -50;
    }
    
    public void showDisplay(boolean param)			{showing = param; rootGroup.setVisible(showing); if (param) { rootGroup.setTranslateY(displayShowingBaseY);} else { rootGroup.setTranslateY(displayHidingBaseY); } }
    public void showAnimateDisplay(boolean param)		{showing = param; if (param) { rootGroup.setVisible(showing); showTransition.play(); } else { hideTransition.play(); } }

    public void switchDisplay()					{if (showing) { rootGroup.setTranslateY(displayHidingBaseY); showing = false; rootGroup.setVisible(showing); } else { rootGroup.setTranslateY(displayShowingBaseY); showing = true; rootGroup.setVisible(showing);}}
    public void switchAnimateDisplay()				{if (showing) { hideTransition.play(); } else { showing = true; rootGroup.setVisible(showing); showTransition.play(); }}
    
    public void controlNodeDisplay(String name) {nodeLabel.setText("Ctrl: " + name);}
    public void camNodeDisplay(String name)	{camLabel.setText("Cam: " + name);}

    public void zoomDisplay(double magfac, double fov)
    {
	zoomFactLabel.setText(String.format("Mag: " + precision, magfac));
	fieldOfViewLabel.setText(String.format("Zoom: " + precision, fov));
    }

    public void distanceDisplay(double dist)
    {
	distLabel.setText(String.format("Dist: " + precision + " m", dist));
//	if (superscene.cam == superscene.node3d) // Measuring itself
//	{
//	    distLabel.setText(String.format("Dist: " + precision + " m", superscene.getDistance(superscene.node3d.getLocation(), superscene.node3d.getLastLocation())));
//	}
//	else
//	{
//	    distLabel.setText(String.format("Dist: " + precision + " m", superscene.getDistance(superscene.cam.getLocation(), superscene.node3d.getLocation())));
//	}
    }

    public void speedDisplay(double speed) { speedLabel.setText(String.format("Spd: " + precision + " m/Sec", speed));}

    public void celestialDisplay(double mass, double radius, int polarity)
    {
	massLabel.setText(String.format("Mass: " + precision, mass) + " kg");
	radiusLabel.setText(String.format("Rad: " + precision, radius) + " m");
	polarityLabel.setText("Polarity: " + Integer.toString(polarity));
    }

    public void scaleDisplay(double sx, double sy, double sz) {scaleXDisplay(sx);scaleYDisplay(sy);scaleZDisplay(sz);}
    public void scaleXDisplay(double sx)   {sxLabel.setText(String.format(sxPrefix + precision, sx));}
    public void scaleYDisplay(double sy)   {syLabel.setText(String.format(syPrefix + precision, sy));}
    public void scaleZDisplay(double sz)   {szLabel.setText(String.format(szPrefix + precision, sz));}

    public void translatePowerDisplay(double tlzp, double tlxp, double tlyp)	    {translateLZPowerDisplay(tlzp);translateLXPowerDisplay(tlxp);translateLYPowerDisplay(tlyp);}
    public void translateLZPowerDisplay(double tlzp)   {tlzpLabel.setText(String.format(tlzpPrefix + precision, tlzp));}
    public void translateLXPowerDisplay(double tlxp)   {tlxpLabel.setText(String.format(tlxpPrefix + precision, tlxp));}
    public void translateLYPowerDisplay(double tlyp)   {tlypLabel.setText(String.format(tlypPrefix + precision, tlyp));}

    public void translateMotionDisplay(double txm, double tym, double tzm)    {translateXMDisplay(txm);translateYMDisplay(tym);translateZMDisplay(tzm);}
    public void translateXMDisplay(double txm)	    {txmLabel.setText(String.format(txmPrefix + precision, txm));}
    public void translateYMDisplay(double tym)	    {tymLabel.setText(String.format(tymPrefix + precision, tym));}
    public void translateZMDisplay(double tzm)	    {tzmLabel.setText(String.format(tzmPrefix + precision, tzm));}

    public void translateDisplay(double tx, double ty, double tz)   {translateXDisplay(tx);translateYDisplay(ty);translateZDisplay(tz);}
    public void translateXDisplay(double tx)	{txLabel.setText(String.format(txPrefix + precision, tx));}
    public void translateYDisplay(double ty)	{tyLabel.setText(String.format(tyPrefix + precision, ty));}
    public void translateZDisplay(double tz)	{tzLabel.setText(String.format(tzPrefix + precision, tz));}

    public void rotatePowerDisplay(double rlxp, double rlyp, double rlzp)    {rotateLXPowerDisplay(rlxp);rotateLYPowerDisplay(rlyp);rotateLZPowerDisplay(rlzp);}
    public void rotateLXPowerDisplay(double rlxp)    {rlxpLabel.setText(String.format(rlxpPrefix + precision, rlxp));}
    public void rotateLYPowerDisplay(double rlyp)    {rlypLabel.setText(String.format(rlypPrefix + precision, rlyp));}
    public void rotateLZPowerDisplay(double rlzp)    {rlzpLabel.setText(String.format(rlzpPrefix + precision, rlzp));}

    public void rotateMotionDisplay(double rxm, double rym, double rzm)	    {rotateXMDisplay(rxm);rotateYMDisplay(rym);rotateZMDisplay(rzm);}
    public void rotateXMDisplay(double rxm)    {rxmLabel.setText(String.format(rxmPrefix + precision, rxm));}
    public void rotateYMDisplay(double rym)    {rymLabel.setText(String.format(rymPrefix + precision, rym));}
    public void rotateZMDisplay(double rzm)    {rzmLabel.setText(String.format(rzmPrefix + precision, rzm));}

    public void rotateDisplay(double rx, double ry, double rz)	    {rotateXDisplay(rx);rotateYDisplay(ry);rotateZDisplay(rz);}
    public void rotateXDisplay(double rx)    {rxLabel.setText(String.format(rxPrefix + precision, rx));}
    public void rotateYDisplay(double ry)    {ryLabel.setText(String.format(ryPrefix + precision, ry));}
    public void rotateZDisplay(double rz)    {rzLabel.setText(String.format(rzPrefix + precision, rz));}

    public void angleXDisplay(double mxy, double mxz, double myy, double myz) { mxyDisplay(mxy); mxzDisplay(mxz); myyDisplay(myy); myzDisplay(myz); }
    public void angleYDisplay(double mxx, double mxz, double mzx, double mzz) { mxxDisplay(mxx); mxzDisplay(mxz); mzxDisplay(mzx); mzzDisplay(mzz); }
    public void angleZDisplay(double myx, double myy, double mzx, double mzy) { myxDisplay(myx); myyDisplay(myy); mzxDisplay(mzx); mzyDisplay(mzy); }
    
    public void angleDisplay(double mxx,double mxy,double mxz,double myx,double myy,double myz,double mzx,double mzy,double mzz)
    { mxxDisplay(mxx); mxyDisplay(mxy); mxzDisplay(mxz); myxDisplay(myx); myyDisplay(myy); myzDisplay(myz); mzxDisplay(mzx); mzyDisplay(mzy); mzzDisplay(mzz); }
    
    public void mxxDisplay(double mxx) { mxxLabel.setText(String.format(mxxPrefix + precision, mxx)); }
    public void mxyDisplay(double mxy) { mxyLabel.setText(String.format(mxyPrefix + precision, mxy)); }
    public void mxzDisplay(double mxz) { mxzLabel.setText(String.format(mxzPrefix + precision, mxz)); }
    
    public void myxDisplay(double myx) { myxLabel.setText(String.format(myxPrefix + precision, myx)); }
    public void myyDisplay(double myy) { myyLabel.setText(String.format(myyPrefix + precision, myy)); }
    public void myzDisplay(double myz) { myzLabel.setText(String.format(myzPrefix + precision, myz)); }
    
    public void mzxDisplay(double mzx) { mzxLabel.setText(String.format(mzxPrefix + precision, mzx)); }
    public void mzyDisplay(double mzy) { mzyLabel.setText(String.format(mzyPrefix + precision, mzy)); }
    public void mzzDisplay(double mzz) { mzzLabel.setText(String.format(mzzPrefix + precision, mzz)); }

    public void nxxDisplay(double param) {nxxLabel.setText(String.format(nxxPrefix + precision, param));}
    public void nxyDisplay(double param) {nxyLabel.setText(String.format(nxyPrefix + precision, param));}
    public void nxzDisplay(double param) {nxzLabel.setText(String.format(nxzPrefix + precision, param));}

    private Rectangle createBGRect(double width, double height)
    {
	Rectangle rect = new Rectangle(0, 0, width, height);
	rect.setArcWidth(Math.min(width, height)*0.5);
	rect.setArcHeight(Math.min(width, height)*0.5);
//	rect.setStroke(Color.GREY);
//	rect.setStrokeWidth(1);
	rect.setOpacity(0.3);
	return rect;
    }
    
    private VBox createLabelVBox()
    {
	sxPrefix = "sx: ";
	syPrefix = "sy: ";
	szPrefix = "sz: ";

	tlxpPrefix = "tlxp: ";
	tlypPrefix = "tlyp: ";
	tlzpPrefix = "tlzp: ";

	txmPrefix = "txm: ";
	tymPrefix = "tym: ";
	tzmPrefix = "tzm: ";

	txPrefix = "tx: ";
	tyPrefix = "ty: ";
	tzPrefix = "tz: ";

	rlxpPrefix = "rlxp: ";
	rlypPrefix = "rlyp: ";
	rlzpPrefix = "rlzp: ";

	rxmPrefix = "rxm: ";
	rymPrefix = "rym: ";
	rzmPrefix = "rzm: ";

	rxPrefix = "rx: ";
	ryPrefix = "ry: ";
	rzPrefix = "rz: ";

	mxxPrefix = "mxx: ";
	mxyPrefix = "mxy: ";
	mxzPrefix = "mxz: ";
	myxPrefix = "myx: ";
	myyPrefix = "myy: ";
	myzPrefix = "myz: ";
	mzxPrefix = "mzx: ";
	mzyPrefix = "mzy: ";
	mzzPrefix = "mzz: ";

	nxxPrefix = "nxx: ";
	nxyPrefix = "nxy ";
	nxzPrefix = "nxz: ";

	distPrefix = "Dist: ";
	distSuffix = " m";
	speedPrefix = "Spd: ";
	speedSuffix = " m/s";

	precision = "%.5f";

	sceneLabel = createLabel("Scene: ");
	sceneLabel.setPrefWidth(200);
	statusLabel = createLabel("");
	statusLabel.setPrefWidth(200);
	camLabel = createLabel("Cam: -");
	fieldOfViewLabel = createLabel(String.format("Zoom: -"));
	zoomFactLabel = createLabel(String.format("Mag: -"));

	c0Label = createLabel(String.format("Node"));
	c1Label = createLabel(String.format("Celest"));
	c2Label = createLabel(String.format("Scale"));
	c3Label = createLabel(String.format("Trans Force"));
	c4Label = createLabel(String.format("Trans Motion"));
	c5Label = createLabel(String.format("Trans Position"));
	c6Label = createLabel(String.format("Angul Force"));
	c7Label = createLabel(String.format("Angul Motion"));
	c8Label = createLabel(String.format("Angul Position"));
	c9Label = createLabel(String.format("Orient-X"));
	c10Label = createLabel(String.format("Orient-Y"));
	c11Label = createLabel(String.format("Orient-Z"));

	nodeLabel = createLabel("Ctrl: -");
	distLabel = createLabel(distPrefix);
	speedLabel = createLabel("Spd: -");
	massLabel = createLabel(String.format("Mass: -"));
	radiusLabel = createLabel(String.format("Rad: -"));
	polarityLabel = createLabel(String.format("Polr: -"));

	
	sxLabel = createLabel(String.format(sxPrefix));
	syLabel = createLabel(String.format(syPrefix));
	szLabel = createLabel(String.format(szPrefix));

	tlzpLabel = createLabel(String.format(tlxpPrefix));
	tlxpLabel = createLabel(String.format(tlypPrefix));
	tlypLabel = createLabel(String.format(tlzpPrefix));

	txmLabel = createLabel(String.format(txmPrefix));
	tymLabel = createLabel(String.format(tymPrefix));
	tzmLabel = createLabel(String.format(tzmPrefix));

	txLabel = createLabel(String.format(txPrefix));
	tyLabel = createLabel(String.format(tyPrefix));
	tzLabel = createLabel(String.format(tzPrefix));

	rlxpLabel = createLabel(String.format(rlxpPrefix));
	rlypLabel = createLabel(String.format(rlypPrefix));
	rlzpLabel = createLabel(String.format(rlzpPrefix));

	rxmLabel = createLabel(String.format(rxmPrefix));
	rymLabel = createLabel(String.format(rymPrefix));
	rzmLabel = createLabel(String.format(rzmPrefix));

	rxLabel = createLabel(String.format(rxPrefix));
	ryLabel = createLabel(String.format(ryPrefix));
	rzLabel = createLabel(String.format(rzPrefix));

	mxxLabel = createLabel(String.format(mxxPrefix));
	mxyLabel = createLabel(String.format(mxyPrefix));
	mxzLabel = createLabel(String.format(mxzPrefix));
	myxLabel = createLabel(String.format(myxPrefix));
	myyLabel = createLabel(String.format(myyPrefix));
	myzLabel = createLabel(String.format(myzPrefix));
	mzxLabel = createLabel(String.format(mzxPrefix));
	mzyLabel = createLabel(String.format(mzyPrefix));
	mzzLabel = createLabel(String.format(mzzPrefix));

	nxxLabel = createLabel(String.format(nxxPrefix));
	nxyLabel = createLabel(String.format(nxyPrefix));
	nxzLabel = createLabel(String.format(nxzPrefix));

	double inset = 0;
	labelRow1HBox = new HBox(inset, sceneLabel, camLabel, fieldOfViewLabel, zoomFactLabel, statusLabel); labelRow1HBox.setPadding(new Insets(inset, inset, inset, inset)); labelRow1HBox.setAlignment(Pos.CENTER);
	labelRow2HBox = new HBox(inset, c0Label, c1Label, c2Label, c3Label, c4Label, c5Label, c6Label, c7Label, c8Label, c9Label, c10Label, c11Label); labelRow2HBox.setPadding(new Insets(inset, inset, inset, inset));labelRow2HBox.setAlignment(Pos.CENTER);
	labelRow3HBox = new HBox(inset, nodeLabel, massLabel, sxLabel, tlzpLabel, txmLabel, txLabel, rlxpLabel, rxmLabel, rxLabel, mxxLabel, myxLabel, mzxLabel);labelRow3HBox.setPadding(new Insets(inset, inset, inset, inset));labelRow3HBox.setAlignment(Pos.CENTER);
	labelRow4HBox = new HBox(inset, distLabel, radiusLabel, syLabel, tlxpLabel, tymLabel, tyLabel, rlypLabel, rymLabel, ryLabel, mxyLabel, myyLabel, mzyLabel);labelRow4HBox.setPadding(new Insets(inset, inset, inset, inset));labelRow4HBox.setAlignment(Pos.CENTER);
	labelRow5HBox = new HBox(inset, speedLabel, polarityLabel, szLabel, tlypLabel, tzmLabel, tzLabel, rlzpLabel, rzmLabel, rzLabel,mxzLabel, myzLabel, mzzLabel);labelRow5HBox.setPadding(new Insets(inset, inset, inset, inset));labelRow5HBox.setAlignment(Pos.CENTER);

	VBox vBox = new VBox(inset, labelRow1HBox, labelRow2HBox, labelRow3HBox, labelRow4HBox, labelRow5HBox);									    // Add hboxes to vbox
	vBox.setPadding(new Insets(inset, inset, inset, inset));
	vBox.setVisible(true);
	
	return vBox;
    }
    
    private void handler() { System.out.println("clicked"); }
    
    // All textFields will be created invisible
    private VBox createTFieldVBox()
    {
	sceneTField = createTField("");
	sceneTField.setPrefWidth(200);
	statusTField = createTField("");
	statusTField.setPrefWidth(200);
	camTField = createTField("Cam: -");
	fieldOfViewTField = createTField(String.format("Zoom: -"));
	zoomFactTField = createTField(String.format("Mag: -"));

	c0TField = createTField(String.format("Node"));
	c1TField = createTField(String.format("Celest"));
	c2TField = createTField(String.format("Scale"));
	c3TField = createTField(String.format("Trans Force"));
	c4TField = createTField(String.format("Trans Motion"));
	c5TField = createTField(String.format("Trans Position"));
	c6TField = createTField(String.format("Angul Force"));
	c7TField = createTField(String.format("Angul Motion"));
	c8TField = createTField(String.format("Angul Position"));
	c9TField = createTField(String.format("Orient-X"));
	c10TField = createTField(String.format("Orient-Y"));
	c11TField = createTField(String.format("Orient-Z"));

	nodeTField = createTField("Ctrl: -");
	distTField = createTField(distPrefix);
	speedTField = createTField("Spd: -");
	massTField = createTField(String.format("Mass: -"));
	radiusTField = createTField(String.format("Rad: -"));
	polarityTField = createTField(String.format("Polr: -"));

	
	sxTField = createTField(String.format(sxPrefix));
	syTField = createTField(String.format(syPrefix));
	szTField = createTField(String.format(szPrefix));

	tlzpTField = createTField(String.format(tlxpPrefix));
	tlxpTField = createTField(String.format(tlypPrefix));
	tlypTField = createTField(String.format(tlzpPrefix));

	txmTField = createTField(String.format(txmPrefix));
	tymTField = createTField(String.format(tymPrefix));
	tzmTField = createTField(String.format(tzmPrefix));

	txTField = createTField(String.format(txPrefix));
	tyTField = createTField(String.format(tyPrefix));
	tzTField = createTField(String.format(tzPrefix));

	rlxpTField = createTField(String.format(rlxpPrefix));
	rlypTField = createTField(String.format(rlypPrefix));
	rlzpTField = createTField(String.format(rlzpPrefix));

	rxmTField = createTField(String.format(rxmPrefix));
	rymTField = createTField(String.format(rymPrefix));
	rzmTField = createTField(String.format(rzmPrefix));

	rxTField = createTField(String.format(rxPrefix));
	ryTField = createTField(String.format(ryPrefix));
	rzTField = createTField(String.format(rzPrefix));

	mxxTField = createTField(String.format(mxxPrefix));
	mxyTField = createTField(String.format(mxyPrefix));
	mxzTField = createTField(String.format(mxzPrefix));
	myxTField = createTField(String.format(myxPrefix));
	myyTField = createTField(String.format(myyPrefix));
	myzTField = createTField(String.format(myzPrefix));
	mzxTField = createTField(String.format(mzxPrefix));
	mzyTField = createTField(String.format(mzyPrefix));
	mzzTField = createTField(String.format(mzzPrefix));

	nxxTField = createTField(String.format(nxxPrefix));
	nxyTField = createTField(String.format(nxyPrefix));
	nxzTField = createTField(String.format(nxzPrefix));
	double inset = 0;
	tfieldRow1HBox = new HBox(inset, sceneTField, camTField, fieldOfViewTField, zoomFactTField, statusTField); tfieldRow1HBox.setPadding(new Insets(inset, inset, inset, inset)); tfieldRow1HBox.setAlignment(Pos.CENTER);
	tfieldRow2HBox = new HBox(inset, c0TField, c1TField, c2TField, c3TField, c4TField, c5TField, c6TField, c7TField, c8TField, c9TField, c10TField, c11TField); tfieldRow2HBox.setPadding(new Insets(inset, inset, inset, inset));tfieldRow2HBox.setAlignment(Pos.CENTER);
	tfieldRow3HBox = new HBox(inset, nodeTField, massTField, sxTField, tlzpTField, txmTField, txTField, rlxpTField, rxmTField, rxTField, mxxTField, myxTField, mzxTField);tfieldRow3HBox.setPadding(new Insets(inset, inset, inset, inset));tfieldRow3HBox.setAlignment(Pos.CENTER);
	tfieldRow4HBox = new HBox(inset, distTField, radiusTField, syTField, tlxpTField, tymTField, tyTField, rlypTField, rymTField, ryTField, mxyTField, myyTField, mzyTField);tfieldRow4HBox.setPadding(new Insets(inset, inset, inset, inset));tfieldRow4HBox.setAlignment(Pos.CENTER);
	tfieldRow5HBox = new HBox(inset, speedTField, polarityTField, szTField, tlypTField, tzmTField, tzTField, rlzpTField, rzmTField, rzTField,mxzTField, myzTField, mzzTField);tfieldRow5HBox.setPadding(new Insets(inset, inset, inset, inset));tfieldRow5HBox.setAlignment(Pos.CENTER);

	VBox vBox = new VBox(inset, tfieldRow1HBox, tfieldRow2HBox, tfieldRow3HBox, tfieldRow4HBox, tfieldRow5HBox);									    // Add hboxes to vbox
	vBox.setPadding(new Insets(inset, inset, inset, inset));
	vBox.setVisible(true);
	
	return vBox;
    }
    
    private Label createLabel(String value)
    {
	Label label = new Label(value);
	label.setPrefWidth(labelWidth);
	label.setPrefHeight(labelHeight);
	label.setFont(Font.font(Font.getDefault().getName(),FontWeight.NORMAL, 9));
	label.setTextFill(Paint.valueOf("WHITE"));
	label.setAlignment(Pos.CENTER);
	label.setVisible(true);

	return label;
    }

    private TextField createTField(String value)
    {
	TextField tfield = new TextField("");
	tfield.setPrefWidth(tfieldWidth);
	tfield.setPrefHeight(tfieldHeight);
	tfield.setFont(Font.font(Font.getDefault().getName(),FontWeight.NORMAL, 8));
	tfield.setAlignment(Pos.CENTER);
	tfield.setVisible(false);
	return tfield;
    }

    private void createFieldEvents() // For editing the backup coordinals through the node3d labels
    {
	createCelestialEvents();
	createScaleEvents();
	createTranslateMotionEvents();
	createTranslateEvents();
	createRotateMotionEvents();
	createRotateEvents();
    }
    
    private void createCelestialEvents()
    {
	massLabel.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { if (mouseEvent.getClickCount() == 1)
	{ superscene.mainstage.setEditing(true); massTField.setText(Double.toString(superscene.node3d.getCelestial().getMass())); massLabel.setVisible(false); massTField.setVisible(true); } });
	massTField.addEventFilter(KeyEvent.KEY_PRESSED, (EventHandler<KeyEvent>) (KeyEvent keyEvent) -> { if (keyEvent.getCode() == KeyCode.ENTER)
	{ superscene.node3d.getCelestial().setMass(Double.parseDouble(massTField.getText())); massTField.setVisible(false); massLabel.setVisible(true); superscene.mainstage.setEditing(false); restoreCoordinals(); }
	else if (keyEvent.getCode() == KeyCode.ESCAPE) { massTField.setVisible(false); massLabel.setVisible(true); superscene.mainstage.setEditing(false); } });

	radiusLabel.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { if (mouseEvent.getClickCount() == 1)
	{ superscene.mainstage.setEditing(true); radiusTField.setText(Double.toString(superscene.node3d.getCelestial().getRadius())); radiusLabel.setVisible(false); radiusTField.setVisible(true); } });
	radiusTField.addEventFilter(KeyEvent.KEY_PRESSED, (EventHandler<KeyEvent>) (KeyEvent keyEvent) -> { if (keyEvent.getCode() == KeyCode.ENTER)
	{ superscene.node3d.getCelestial().setRadius(Double.parseDouble(radiusTField.getText())); radiusTField.setVisible(false); radiusLabel.setVisible(true); superscene.mainstage.setEditing(false); restoreCoordinals(); }
	else if (keyEvent.getCode() == KeyCode.ESCAPE) { radiusTField.setVisible(false); radiusLabel.setVisible(true); superscene.mainstage.setEditing(false); } });

	polarityLabel.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { if (mouseEvent.getClickCount() == 1)
	{ superscene.mainstage.setEditing(true); polarityTField.setText(Double.toString(superscene.node3d.getCelestial().getPolarity())); polarityLabel.setVisible(false); polarityTField.setVisible(true); } });
	polarityTField.addEventFilter(KeyEvent.KEY_PRESSED, (EventHandler<KeyEvent>) (KeyEvent keyEvent) -> { if (keyEvent.getCode() == KeyCode.ENTER)
	{ superscene.node3d.getCelestial().setPolarity(Integer.parseInt(polarityTField.getText())); polarityTField.setVisible(false); polarityLabel.setVisible(true); superscene.mainstage.setEditing(false); restoreCoordinals(); }
	else if (keyEvent.getCode() == KeyCode.ESCAPE) { polarityTField.setVisible(false); polarityLabel.setVisible(true); superscene.mainstage.setEditing(false); } });
    }
    
    private void createScaleEvents()
    {
	c2Label.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { if (mouseEvent.getClickCount() == 1)
	{ superscene.mainstage.setEditing(true); c2TField.setText(Double.toString(superscene.node3d.getBUPCoordinal().getSxProp().get())); c2Label.setVisible(false); c2TField.setVisible(true); } });
	c2TField.addEventFilter(KeyEvent.KEY_PRESSED, (EventHandler<KeyEvent>) (KeyEvent keyEvent) -> { if (keyEvent.getCode() == KeyCode.ENTER)
	{ superscene.node3d.getBUPCoordinal().getSxProp().set(Double.parseDouble(c2TField.getText())); superscene.node3d.getBUPCoordinal().getSyProp().set(Double.parseDouble(c2TField.getText()));
	superscene.node3d.getBUPCoordinal().getSzProp().set(Double.parseDouble(c2TField.getText())); superscene.node3d.setScale(); c2TField.setVisible(false); c2Label.setVisible(true); superscene.mainstage.setEditing(false);
	restoreCoordinals(); } else if (keyEvent.getCode() == KeyCode.ESCAPE) { c2TField.setVisible(false); c2Label.setVisible(true); superscene.mainstage.setEditing(false); } });
		
	sxLabel.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { if (mouseEvent.getClickCount() == 1)
	{ superscene.mainstage.setEditing(true); sxTField.setText(Double.toString(superscene.node3d.getBUPCoordinal().getSxProp().get())); sxLabel.setVisible(false); sxTField.setVisible(true); } });
	sxTField.addEventFilter(KeyEvent.KEY_PRESSED, (EventHandler<KeyEvent>) (KeyEvent keyEvent) -> { if (keyEvent.getCode() == KeyCode.ENTER)
	{ superscene.node3d.getBUPCoordinal().getSxProp().set(Double.parseDouble(sxTField.getText())); superscene.node3d.setScaleX(); sxTField.setVisible(false); sxLabel.setVisible(true); superscene.mainstage.setEditing(false); restoreCoordinals(); }
	else if (keyEvent.getCode() == KeyCode.ESCAPE) { sxTField.setVisible(false); sxLabel.setVisible(true); superscene.mainstage.setEditing(false); } });

	syLabel.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { if (mouseEvent.getClickCount() == 1)
	{ superscene.mainstage.setEditing(true); syTField.setText(Double.toString(superscene.node3d.getBUPCoordinal().getSyProp().get())); syLabel.setVisible(false); syTField.setVisible(true); } });
	syTField.addEventFilter(KeyEvent.KEY_PRESSED, (EventHandler<KeyEvent>) (KeyEvent keyEvent) -> { if (keyEvent.getCode() == KeyCode.ENTER)
	{ superscene.node3d.getBUPCoordinal().getSyProp().set(Double.parseDouble(syTField.getText())); superscene.node3d.setScaleY(); syTField.setVisible(false); syLabel.setVisible(true); superscene.mainstage.setEditing(false); restoreCoordinals(); }
	else if (keyEvent.getCode() == KeyCode.ESCAPE) { syTField.setVisible(false); syLabel.setVisible(true); superscene.mainstage.setEditing(false); } });

	szLabel.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { if (mouseEvent.getClickCount() == 1)
	{ superscene.mainstage.setEditing(true); szTField.setText(Double.toString(superscene.node3d.getBUPCoordinal().getSzProp().get())); szLabel.setVisible(false); szTField.setVisible(true); } });
	szTField.addEventFilter(KeyEvent.KEY_PRESSED, (EventHandler<KeyEvent>) (KeyEvent keyEvent) -> { if (keyEvent.getCode() == KeyCode.ENTER)
	{ superscene.node3d.getBUPCoordinal().getSzProp().set(Double.parseDouble(szTField.getText())); superscene.node3d.setScaleZ(); szTField.setVisible(false); szLabel.setVisible(true); superscene.mainstage.setEditing(false); restoreCoordinals(); }
	else if (keyEvent.getCode() == KeyCode.ESCAPE) { szTField.setVisible(false); szLabel.setVisible(true); superscene.mainstage.setEditing(false); } });
    }
    
    private void createTranslateMotionEvents()
    {
	txmLabel.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { if (mouseEvent.getClickCount() == 1)
	{ superscene.mainstage.setEditing(true); txmTField.setText(Double.toString(superscene.node3d.getBUPCoordinal().getTxmProp().get())); txmLabel.setVisible(false); txmTField.setVisible(true); } });
	txmTField.addEventFilter(KeyEvent.KEY_PRESSED, (EventHandler<KeyEvent>) (KeyEvent keyEvent) -> { if (keyEvent.getCode() == KeyCode.ENTER)
	{ superscene.node3d.getBUPCoordinal().getTxmProp().set(Double.parseDouble(txmTField.getText())); txmTField.setVisible(false); txmLabel.setVisible(true); superscene.mainstage.setEditing(false); restoreCoordinals(); }
	else if (keyEvent.getCode() == KeyCode.ESCAPE) { txmTField.setVisible(false); txmLabel.setVisible(true); superscene.mainstage.setEditing(false); } });

	tymLabel.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { if (mouseEvent.getClickCount() == 1)
	{ superscene.mainstage.setEditing(true); tymTField.setText(Double.toString(superscene.node3d.getBUPCoordinal().getTymProp().get())); tymLabel.setVisible(false); tymTField.setVisible(true); } });
	tymTField.addEventFilter(KeyEvent.KEY_PRESSED, (EventHandler<KeyEvent>) (KeyEvent keyEvent) -> { if (keyEvent.getCode() == KeyCode.ENTER)
	{ superscene.node3d.getBUPCoordinal().getTymProp().set(Double.parseDouble(tymTField.getText())); tymTField.setVisible(false); tymLabel.setVisible(true); superscene.mainstage.setEditing(false); restoreCoordinals(); }
	else if (keyEvent.getCode() == KeyCode.ESCAPE) { tymTField.setVisible(false); tymLabel.setVisible(true); superscene.mainstage.setEditing(false); } });
	
	tzmLabel.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { if (mouseEvent.getClickCount() == 1)
	{ superscene.mainstage.setEditing(true); tzmTField.setText(Double.toString(superscene.node3d.getBUPCoordinal().getTzmProp().get())); tzmLabel.setVisible(false); tzmTField.setVisible(true); } });
	tzmTField.addEventFilter(KeyEvent.KEY_PRESSED, (EventHandler<KeyEvent>) (KeyEvent keyEvent) -> { if (keyEvent.getCode() == KeyCode.ENTER)
	{ superscene.node3d.getBUPCoordinal().getTzmProp().set(Double.parseDouble(tzmTField.getText())); tzmTField.setVisible(false); tzmLabel.setVisible(true); superscene.mainstage.setEditing(false); restoreCoordinals(); }
	else if (keyEvent.getCode() == KeyCode.ESCAPE) { tzmTField.setVisible(false); tzmLabel.setVisible(true); superscene.mainstage.setEditing(false); } });
    }
    
    private void createTranslateEvents()
    {
	txLabel.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { if (mouseEvent.getClickCount() == 1)
	{ superscene.mainstage.setEditing(true); txTField.setText(Double.toString(superscene.node3d.getBUPCoordinal().getTxProp().get())); txLabel.setVisible(false); txTField.setVisible(true); } });
	txTField.addEventFilter(KeyEvent.KEY_PRESSED, (EventHandler<KeyEvent>) (KeyEvent keyEvent) -> { if (keyEvent.getCode() == KeyCode.ENTER)
	{ superscene.node3d.getBUPCoordinal().getTxProp().set(Double.parseDouble(txTField.getText())); txTField.setVisible(false); txLabel.setVisible(true); superscene.mainstage.setEditing(false); restoreCoordinals(); }
	else if (keyEvent.getCode() == KeyCode.ESCAPE) { txTField.setVisible(false); txLabel.setVisible(true); superscene.mainstage.setEditing(false); } });

	tyLabel.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { if (mouseEvent.getClickCount() == 1)
	{ superscene.mainstage.setEditing(true); tyTField.setText(Double.toString(superscene.node3d.getBUPCoordinal().getTyProp().get())); tyLabel.setVisible(false); tyTField.setVisible(true); } });
	tyTField.addEventFilter(KeyEvent.KEY_PRESSED, (EventHandler<KeyEvent>) (KeyEvent keyEvent) -> { if (keyEvent.getCode() == KeyCode.ENTER)
	{ superscene.node3d.getBUPCoordinal().getTyProp().set(Double.parseDouble(tyTField.getText())); tyTField.setVisible(false); tyLabel.setVisible(true); superscene.mainstage.setEditing(false); restoreCoordinals(); }
	else if (keyEvent.getCode() == KeyCode.ESCAPE) { tyTField.setVisible(false); tyLabel.setVisible(true); superscene.mainstage.setEditing(false); } });
	
	tzLabel.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { if (mouseEvent.getClickCount() == 1)
	{ superscene.mainstage.setEditing(true); tzTField.setText(Double.toString(superscene.node3d.getBUPCoordinal().getTzProp().get())); tzLabel.setVisible(false); tzTField.setVisible(true); } });
	tzTField.addEventFilter(KeyEvent.KEY_PRESSED, (EventHandler<KeyEvent>) (KeyEvent keyEvent) -> { if (keyEvent.getCode() == KeyCode.ENTER)
	{ superscene.node3d.getBUPCoordinal().getTzProp().set(Double.parseDouble(tzTField.getText())); tzTField.setVisible(false); tzLabel.setVisible(true); superscene.mainstage.setEditing(false); restoreCoordinals(); }
	else if (keyEvent.getCode() == KeyCode.ESCAPE) { tzTField.setVisible(false); tzLabel.setVisible(true); superscene.mainstage.setEditing(false); } });
    }
    
    private void createRotateMotionEvents()
    {
	rxmLabel.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { if (mouseEvent.getClickCount() == 1)
	{ superscene.mainstage.setEditing(true); rxmTField.setText(Double.toString(superscene.node3d.getBUPCoordinal().getRxmProp().get())); rxmLabel.setVisible(false); rxmTField.setVisible(true); } });
	rxmTField.addEventFilter(KeyEvent.KEY_PRESSED, (EventHandler<KeyEvent>) (KeyEvent keyEvent) -> { if (keyEvent.getCode() == KeyCode.ENTER)
	{ superscene.node3d.getBUPCoordinal().getRxmProp().set(Double.parseDouble(rxmTField.getText())); rxmTField.setVisible(false); rxmLabel.setVisible(true); superscene.mainstage.setEditing(false); restoreCoordinals(); }
	else if (keyEvent.getCode() == KeyCode.ESCAPE) { rxmTField.setVisible(false); rxmLabel.setVisible(true); superscene.mainstage.setEditing(false); } });

	rymLabel.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { if (mouseEvent.getClickCount() == 1)
	{ superscene.mainstage.setEditing(true); rymTField.setText(Double.toString(superscene.node3d.getBUPCoordinal().getRymProp().get())); rymLabel.setVisible(false); rymTField.setVisible(true); } });
	rymTField.addEventFilter(KeyEvent.KEY_PRESSED, (EventHandler<KeyEvent>) (KeyEvent keyEvent) -> { if (keyEvent.getCode() == KeyCode.ENTER)
	{ superscene.node3d.getBUPCoordinal().getRymProp().set(Double.parseDouble(rymTField.getText())); rymTField.setVisible(false); rymLabel.setVisible(true); superscene.mainstage.setEditing(false); restoreCoordinals(); }
	else if (keyEvent.getCode() == KeyCode.ESCAPE) { rymTField.setVisible(false); rymLabel.setVisible(true); superscene.mainstage.setEditing(false); } });
	
	rzmLabel.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { if (mouseEvent.getClickCount() == 1)
	{ superscene.mainstage.setEditing(true); rzmTField.setText(Double.toString(superscene.node3d.getBUPCoordinal().getRzmProp().get())); rzmLabel.setVisible(false); rzmTField.setVisible(true); } });
	rzmTField.addEventFilter(KeyEvent.KEY_PRESSED, (EventHandler<KeyEvent>) (KeyEvent keyEvent) -> { if (keyEvent.getCode() == KeyCode.ENTER)
	{ superscene.node3d.getBUPCoordinal().getRzmProp().set(Double.parseDouble(rzmTField.getText())); rzmTField.setVisible(false); rzmLabel.setVisible(true); superscene.mainstage.setEditing(false); restoreCoordinals(); }
	else if (keyEvent.getCode() == KeyCode.ESCAPE) { rzmTField.setVisible(false); rzmLabel.setVisible(true); superscene.mainstage.setEditing(false); } });
    }
    
    private void createRotateEvents()
    {
	rxLabel.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { if (mouseEvent.getClickCount() == 1)
	{ superscene.mainstage.setEditing(true); rxTField.setText(Double.toString(superscene.node3d.getBUPCoordinal().getRxProp().get())); rxLabel.setVisible(false); rxTField.setVisible(true); } });
	rxTField.addEventFilter(KeyEvent.KEY_PRESSED, (EventHandler<KeyEvent>) (KeyEvent keyEvent) -> { if (keyEvent.getCode() == KeyCode.ENTER)
	{ superscene.node3d.getBUPCoordinal().getRxProp().set(Double.parseDouble(rxTField.getText())); rxTField.setVisible(false); rxLabel.setVisible(true); superscene.mainstage.setEditing(false); restoreCoordinals(); }
	else if (keyEvent.getCode() == KeyCode.ESCAPE) { rxTField.setVisible(false); rxLabel.setVisible(true); superscene.mainstage.setEditing(false); } });

	ryLabel.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { if (mouseEvent.getClickCount() == 1)
	{ superscene.mainstage.setEditing(true); ryTField.setText(Double.toString(superscene.node3d.getBUPCoordinal().getRyProp().get())); ryLabel.setVisible(false); ryTField.setVisible(true); } });
	ryTField.addEventFilter(KeyEvent.KEY_PRESSED, (EventHandler<KeyEvent>) (KeyEvent keyEvent) -> { if (keyEvent.getCode() == KeyCode.ENTER)
	{ superscene.node3d.getBUPCoordinal().getRyProp().set(Double.parseDouble(ryTField.getText())); ryTField.setVisible(false); ryLabel.setVisible(true); superscene.mainstage.setEditing(false); restoreCoordinals(); }
	else if (keyEvent.getCode() == KeyCode.ESCAPE) { ryTField.setVisible(false); ryLabel.setVisible(true); superscene.mainstage.setEditing(false); } });
	
	rzLabel.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { if (mouseEvent.getClickCount() == 1)
	{ superscene.mainstage.setEditing(true); rzTField.setText(Double.toString(superscene.node3d.getBUPCoordinal().getRzProp().get())); rzLabel.setVisible(false); rzTField.setVisible(true); } });
	rzTField.addEventFilter(KeyEvent.KEY_PRESSED, (EventHandler<KeyEvent>) (KeyEvent keyEvent) -> { if (keyEvent.getCode() == KeyCode.ENTER)
	{ superscene.node3d.getBUPCoordinal().getRzProp().set(Double.parseDouble(rzTField.getText())); rzTField.setVisible(false); rzLabel.setVisible(true); superscene.mainstage.setEditing(false); restoreCoordinals(); }
	else if (keyEvent.getCode() == KeyCode.ESCAPE) { rzTField.setVisible(false); rzLabel.setVisible(true); superscene.mainstage.setEditing(false); } });
    }
    
//    public void disableMouseMovement()			    {superscene.mainstage.stage.removeEventFilter(MouseEvent.MOUSE_MOVED, superscene.mainstage.mouseMovedHandler); superscene.subScene.setCursor(Cursor.DEFAULT);}
    public final void disableMouseMovement()			    {superscene.mainstage.disableMouseMovement();}
    private void restoreCoordinals() { superscene.restoreCoordinalsForAllNodes(); }
    
    public Group getRootGroup() { return rootGroup; }
    public boolean isShowing()  { return showing; }
}
