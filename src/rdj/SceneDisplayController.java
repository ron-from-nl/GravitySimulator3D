package rdj;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.util.Duration;

public	class SceneDisplayController extends Group implements Initializable // Gets shown in mainstage.setStageScene
{
    @FXML	private		Rectangle	    displayRect;
    
    @FXML       private	        VBox		    mainVBox;

    @FXML	protected	VBox		    fpsVBox;
    @FXML       protected	VBox		    factorVBox;
    @FXML       protected	VBox		    sceneSectionVBox;
    @FXML       protected	VBox		    nodesVBox;

    @FXML	private		Label		    fpsHeaderLabel;
    @FXML	protected	Label		    fpsLabel;
    @FXML	private		Rectangle	    fpsRect;
    
    @FXML	private	        Label		    frameRateHeaderLabel;
    @FXML       private	        Label		    frameRateDecreaseLabel;
    @FXML       private	        Rectangle	    frameRateDecreaseRect;
    @FXML       protected	Label		    frameRateLabel;
    @FXML       private	        TextField	    frameRateTField;
    @FXML       private	        Rectangle	    frameRateRect;
    @FXML       private		Label		    frameRateIncreaseLabel;
    @FXML       private	        Rectangle	    frameRateIncreaseRect;

    @FXML	private		Label		    motionRateHeaderLabel;
    @FXML	private		Label		    motionRateDecreaseLabel;
    @FXML	private		Rectangle	    motionRateDecreaseRect;
    @FXML	protected	Label		    motionRateLabel;
    @FXML	private		TextField	    motionRateTField;
    @FXML	private		Rectangle	    motionRateRect;
    @FXML	private		Label		    motionRateIncreaseLabel;
    @FXML	private		Rectangle	    motionRateIncreaseRect;

    @FXML       private	        Label		    motionFactorHeaderLabel;
    @FXML	private		Rectangle	    motionFactorDecreaseRect;
    @FXML	private		Label		    motionFactorDecreaseLabel;
    @FXML	private		Rectangle	    motionFactorRect;
    @FXML	protected	Label		    motionFactorLabel;
    @FXML	private		Rectangle	    motionFactorIncreaseRect;
    @FXML	private		Label		    motionFactorIncreaseLabel;

    @FXML       private	        Label		    gravetyFactorHeaderLabel;
    @FXML       private	        Rectangle	    gravityFactorIncreaseRect;
    @FXML       private	        Rectangle	    gravityFactorRect;
    @FXML       protected	Label		    gravityFactorLabel;
    @FXML       private	        Rectangle	    gravityFactorDecreaseRect;
    @FXML       private	        Label		    gravityFactorDecreaseLabel;
    @FXML	private		Label		    gravityFactorIncreaseLabel;
    @FXML       private		TextField	    motionFactorTField;
    @FXML       private		TextField	    gravityFactorTField;

	    
    @FXML	private		Group		    rootGroup;

    private	MainStage	    mainstage;
    private	String		    precision;
    protected	Rectangle2D	    rectangle2D;
    protected	double		    screenwidth, screenheight;
    protected	double		    rectangleWidth;
    protected	double		    rectangleHeight;
    protected	double		    displayHidingBaseX;
    protected	double		    displayShowingBaseX;
    protected	double		    rectangleBaseX;
    private	TranslateTransition hideTransition, showTransition;
    private	boolean		    showing = false;
    private	boolean		    frameRateDecreaseMousePressed;
    private	boolean		    frameRateIncreaseMousePressed;
    private	boolean		    motionRateDecreaseMousePressed;
    private	boolean		    motionRateIncreaseMousePressed;
    private	boolean		    motionFactorDecreaseMousePressed;
    private	boolean		    motionFactorIncreaseMousePressed;
    private	boolean		    gravityFactorDecreaseMousePressed;
    private	boolean		    gravityFactorIncreaseMousePressed;
    private     Timer		    frameRateDecreaseTimer;
    private     Timer		    frameRateIncreaseTimer;
    private     Timer		    motionRateDecreaseTimer;
    private     Timer		    motionRateIncreaseTimer;
    private     Timer		    motionFactorDecreaseTimer;
    private     Timer		    motionFactorIncreaseTimer;
    private     Timer		    gravityFactorDecreaseTimer;
    private     Timer		    gravityFactorIncreaseTimer;
    
    @Override   public	    void initialize(URL url, ResourceBundle rb) // invoked directly after FXML loading
    {
//	System.out.println("initialize method");
    }

    public void setApp(MainStage param) // no default constructor, use this to set a caller reference and the rest
    {
//	System.out.println("setApp");
	mainstage = param;
	
	precision = "%.3f";

	refresh();
	loadScreen();
	
	double showDuration=0.25;
	double flipDuration=0.5;
	
	showTransition = new TranslateTransition(Duration.seconds(showDuration), rootGroup);
	showTransition.setInterpolator(Interpolator.EASE_OUT);
        showTransition.setFromX(displayHidingBaseX);
        showTransition.setToX(displayShowingBaseX);
        showTransition.setCycleCount(1);

	hideTransition = new TranslateTransition(Duration.seconds(showDuration), rootGroup);
	hideTransition.setInterpolator(Interpolator.EASE_OUT);
        hideTransition.setFromX(displayShowingBaseX);
        hideTransition.setToX(displayHidingBaseX);
        hideTransition.setCycleCount(1);
	hideTransition.setOnFinished((ActionEvent arg0) -> { showing = false; rootGroup.setVisible(showing); });

	rootGroup.addEventFilter(MouseEvent.MOUSE_CLICKED, (EventHandler<MouseEvent>) (MouseEvent mouseEvent) -> { disableMouseMovement(); });
    	
	showDisplay(true);
}
    
    public void loadScreen()
    {
	rectangle2D = Screen.getPrimary().getVisualBounds();
	screenwidth = mainstage.stage.getWidth();
	screenheight = mainstage.stage.getHeight();
	rectangleWidth = displayRect.getWidth();
	rectangleHeight = displayRect.getHeight();

	displayHidingBaseX = -(screenwidth / 2) - (rectangleWidth / 2);
	displayShowingBaseX = displayHidingBaseX+rectangleWidth;
    }
    
    public void refresh()			    { refreshTargetFrameRate();refreshRealFrameRate();refreshMotionRate();refreshMotionFactor();refreshGravityFactor();}
    public void refreshTargetFrameRate()	    { frameRateLabel.setText(Double.toString(mainstage.superscene.targetFrameRate)); }
    public void refreshRealFrameRate()		    { fpsLabel.setText(Double.toString(mainstage.superscene.realFrameRate)); }
    public void refreshMotionRate()		    { motionRateLabel.setText(Double.toString(mainstage.superscene.getMotionRate())); }
    public void refreshMotionFactor()		    { motionFactorLabel.setText(String.format(precision, mainstage.superscene.motionFactor)); }
    public void refreshGravityFactor()		    { gravityFactorLabel.setText(String.format(precision, mainstage.superscene.gravityFactor)); }
    
    public void showDisplay(boolean param)	    { showing = param; if (param) { rootGroup.setTranslateX(displayShowingBaseX);} else { rootGroup.setTranslateX(displayHidingBaseX); } rootGroup.setVisible(param); }
    public void showAnimateDisplay(boolean param)   { showing = param; if (param) { rootGroup.setVisible(showing); showTransition.play(); } else { hideTransition.play(); } }

    public void switchDisplay()			    {if (showing) { rootGroup.setTranslateX(displayHidingBaseX); showing = false; rootGroup.setVisible(showing); } else { rootGroup.setTranslateX(displayShowingBaseX); showing = true; rootGroup.setVisible(showing);}}
    public void switchAnimateDisplay()		    {if (showing) { hideTransition.play(); } else { showing = true; rootGroup.setVisible(showing); showTransition.play(); }}
    
    
    
    
    @FXML private void frameRateDecreaseOnMouseEntered(MouseEvent event)    {frameRateDecreaseRect.setFill(Color.rgb(255,255,255,0.5));}
    @FXML private void frameRateDecreaseOnMouseClicked(MouseEvent event)    { if (mainstage.superscene.targetFrameRate > 0) { mainstage.superscene.targetFrameRate--; updateFrameRate(); } }
    @FXML private void frameRateDecreaseOnMousePressed(MouseEvent event)
    {
	if (! frameRateDecreaseMousePressed)
	{
	    frameRateDecreaseMousePressed = true; frameRateDecreaseTimer = new Timer(); frameRateDecreaseTimer.scheduleAtFixedRate(new TimerTask() {@Override public void run() { Platform.runLater(() -> 
	    {
		if ( mainstage.superscene.targetFrameRate>0 ) { mainstage.superscene.targetFrameRate--; } updateFrameRate();
	    }); }}, 250, 100);
	}
    }
    @FXML private void frameRateDecreaseOnMouseReleased(MouseEvent event)   {frameRateDecreaseTimer.cancel(); frameRateDecreaseMousePressed = false;}
    @FXML private void frameRateDecreaseOnMouseExited(MouseEvent event)	    {frameRateDecreaseRect.setFill(Color.rgb(0,0,0,0.0));}
//  ---------    
    @FXML private void frameRateRectOnMouseEntered(MouseEvent event)	    {frameRateRect.setFill(Color.rgb(255,255,255,0.5));}
    @FXML private void frameRateRectOnMouseClicked(MouseEvent event)	    {if (event.getClickCount() == 1)    { mainstage.setEditing(true); frameRateTField.setText(Double.toString(mainstage.superscene.targetFrameRate)); frameRateLabel.setVisible(false); frameRateTField.setVisible(true); }}
    @FXML private void frameRateTFieldOnKeyPressed(KeyEvent event)
    {
	if (event.getCode() == KeyCode.ESCAPE)	{ frameRateTField.setVisible(false); frameRateLabel.setVisible(true); mainstage.setEditing(false); }
	if (event.getCode() == KeyCode.ENTER)	{ mainstage.superscene.targetFrameRate = Integer.parseInt(frameRateTField.getText()); frameRateTField.setVisible(false); frameRateLabel.setVisible(true); updateFrameRate(); mainstage.setEditing(false); }
    }
    @FXML private void frameRateRectOnMouseExited(MouseEvent event)	    {frameRateRect.setFill(Color.rgb(0,0,0,0.0));}
//  ---------    
    @FXML private void frameRateIncreaseOnMouseEntered(MouseEvent event)    {frameRateIncreaseRect.setFill(Color.rgb(255,255,255,0.5));}
    @FXML private void frameRateIncreaseOnMouseClicked(MouseEvent event)    {if ( mainstage.superscene.targetFrameRate<1000 ) { mainstage.superscene.targetFrameRate++; } updateFrameRate();}
    @FXML private void frameRateIncreaseOnMousePressed(MouseEvent event)
    {
	if (! frameRateIncreaseMousePressed)
	{
	    frameRateIncreaseMousePressed = true; frameRateIncreaseTimer = new Timer(); frameRateIncreaseTimer.scheduleAtFixedRate(new TimerTask() {@Override public void run() { Platform.runLater(() -> 
	    {
		if ( mainstage.superscene.targetFrameRate<1000 ) { mainstage.superscene.targetFrameRate++; } updateFrameRate();
	    }); }}, 250, 100);
	}
    }
    @FXML private void frameRateIncreaseOnMouseReleased(MouseEvent event)	{frameRateIncreaseTimer.cancel(); frameRateIncreaseMousePressed = false;}
    @FXML private void frameRateIncreaseOnMouseExited(MouseEvent event)		{frameRateIncreaseRect.setFill(Color.rgb(0,0,0,0.0));}

    private void updateFrameRate()    { refreshTargetFrameRate(); refreshRealFrameRate(); distributeFrameRate(); disableMouseMovement(); }
//  ---------
    
//  ---------
    @FXML   private void motionRateDecreaseOnMouseEntered(MouseEvent event)	{motionRateDecreaseRect.setFill(Color.rgb(255,255,255,0.5));}
    @FXML   private void motionRateDecreaseOnMouseClicked(MouseEvent event)	{ if (mainstage.superscene.getMotionRate() > 0) { mainstage.superscene.setMotionRate(mainstage.superscene.getMotionRate()-1); updateMotionRate(); } }
    @FXML   private void motionRateDecreaseOnMousePressed(MouseEvent event)
    {
	if (! motionRateDecreaseMousePressed)
	{
	    motionRateDecreaseMousePressed = true; motionRateDecreaseTimer = new Timer(); motionRateDecreaseTimer.scheduleAtFixedRate(new TimerTask() {@Override public void run() { Platform.runLater(() -> 
	    {
		if ( mainstage.superscene.getMotionRate()>0 ) { mainstage.superscene.setMotionRate(mainstage.superscene.getMotionRate()-1); } updateMotionRate();
	    }); }}, 250, 100);
	}
    }
    @FXML   private void motionRateDecreaseOnMouseReleased(MouseEvent event)	{motionRateDecreaseTimer.cancel(); motionRateDecreaseMousePressed = false;}
    @FXML   private void motionRateDecreaseOnMouseExited(MouseEvent event)	{motionRateDecreaseRect.setFill(Color.rgb(0,0,0,0.0));}
//  ---------        
    @FXML   private void motionRateRectOnMouseEntered(MouseEvent event)		{motionRateRect.setFill(Color.rgb(255,255,255,0.5));}
    @FXML   private void motionRateRectOnMouseClicked(MouseEvent event)		{if (event.getClickCount() == 1)    { mainstage.setEditing(true); motionRateTField.setText(Double.toString(mainstage.superscene.getMotionRate())); motionRateLabel.setVisible(false); motionRateTField.setVisible(true); }}
    @FXML   private void motionRateTFieldOnKeyPressed(KeyEvent event)
    {
	if (event.getCode() == KeyCode.ESCAPE)	{ motionRateTField.setVisible(false); motionRateLabel.setVisible(true); mainstage.setEditing(false); }
	if (event.getCode() == KeyCode.ENTER)	{ mainstage.superscene.setMotionRate(Integer.parseInt(motionRateTField.getText())); motionRateTField.setVisible(false); motionRateLabel.setVisible(true); updateMotionRate(); mainstage.setEditing(false); }
    }
    @FXML   private void motionRateRectOnMouseExited(MouseEvent event)		{motionRateRect.setFill(Color.rgb(0,0,0,0.0));}
    
    @FXML   private void motionRateIncreaseOnMouseEntered(MouseEvent event)	{motionRateIncreaseRect.setFill(Color.rgb(255,255,255,0.5));}
    @FXML   private void motionRateIncreaseOnMouseClicked(MouseEvent event)	{if ( mainstage.superscene.getMotionRate()<1000 ) { mainstage.superscene.setMotionRate(mainstage.superscene.getMotionRate()+1); } updateMotionRate();}
    @FXML   private void motionRateIncreaseOnMousePressed(MouseEvent event)
    {
	if (! motionRateIncreaseMousePressed)
	{
	    motionRateIncreaseMousePressed = true; motionRateIncreaseTimer = new Timer(); motionRateIncreaseTimer.scheduleAtFixedRate(new TimerTask() {@Override public void run() { Platform.runLater(() -> 
	    {
		if ( mainstage.superscene.getMotionRate()<1000 ) { mainstage.superscene.setMotionRate(mainstage.superscene.getMotionRate()+1); } updateMotionRate();
	    }); }}, 250, 100);
	}
    }
    @FXML   private void motionRateIncreaseOnMouseReleased(MouseEvent event)	{motionRateIncreaseTimer.cancel(); motionRateIncreaseMousePressed = false;}
    @FXML   private void motionRateIncreaseOnMouseExited(MouseEvent event)	{motionRateIncreaseRect.setFill(Color.rgb(0,0,0,0.0));}

    private void updateMotionRate()    { refreshMotionRate(); distributeMotionRate(); disableMouseMovement(); }
//  ---------
    
//  ---------
    @FXML private void motionFactorDecreaseOnMouseEntered(MouseEvent event) {motionFactorDecreaseRect.setFill(Color.rgb(255,255,255,0.5));}
    @FXML private void motionFactorDecreaseOnMouseClicked(MouseEvent event) {mainstage.superscene.motionFactor /= 1.1; updateMotionFactor();}
    @FXML private void motionFactorDecreaseOnMousePressed(MouseEvent event)
    {
	if (! motionFactorDecreaseMousePressed)
	{
	    motionFactorDecreaseMousePressed = true; motionFactorDecreaseTimer = new Timer(); motionFactorDecreaseTimer.scheduleAtFixedRate(new TimerTask() {@Override public void run() { Platform.runLater(() -> 
	    {
		mainstage.superscene.motionFactor /= 1.1;
		updateMotionFactor();
	    }); }}, 250, 100);
	}
    }
    
    @FXML private void motionFactorDecreaseOnMouseReleased(MouseEvent event)	{motionFactorDecreaseTimer.cancel(); motionFactorDecreaseMousePressed = false;}
    @FXML private void motionFactorDecreaseOnMouseExited(MouseEvent event)		{motionFactorDecreaseRect.setFill(Color.rgb(0,0,0,0.0));}
//  ---------    
    @FXML private void motionFactorRectOnMouseEntered(MouseEvent event)		{motionFactorRect.setFill(Color.rgb(255,255,255,0.5));}
    @FXML private void motionFactorRectOnMouseClicked(MouseEvent event)		{ if (event.getClickCount() == 1)	{ mainstage.setEditing(true); motionFactorTField.setText(Double.toString(mainstage.superscene.motionFactor)); motionFactorLabel.setVisible(false); motionFactorTField.setVisible(true); }}
    @FXML private void motionFactorTFieldOnKeyPressed(KeyEvent event)
    {
	if (event.getCode() == KeyCode.ESCAPE)	{ motionFactorTField.setVisible(false); motionFactorLabel.setVisible(true); mainstage.setEditing(false); }
	if (event.getCode() == KeyCode.ENTER)	{ mainstage.superscene.motionFactor = Double.parseDouble(motionFactorTField.getText()); motionFactorTField.setVisible(false); motionFactorLabel.setVisible(true); updateMotionFactor(); mainstage.setEditing(false); }
    }
    @FXML private void motionFactorRectOnMouseExited(MouseEvent event)		{motionFactorRect.setFill(Color.rgb(0,0,0,0.0));}

//  ---------    
    @FXML private void motionFactorIncreaseOnMouseEntered(MouseEvent event)		{motionFactorIncreaseRect.setFill(Color.rgb(255,255,255,0.5));}
    @FXML private void motionFactorIncreaseOnMouseClicked(MouseEvent event)		{mainstage.superscene.motionFactor *= 1.1; updateMotionFactor();}
    @FXML private void motionFactorIncreaseOnMousePressed(MouseEvent event)
    {
	if (! motionFactorIncreaseMousePressed)
	{
	    motionFactorIncreaseMousePressed = true; motionFactorIncreaseTimer = new Timer(); motionFactorIncreaseTimer.scheduleAtFixedRate(new TimerTask() {@Override public void run() { Platform.runLater(() -> 
	    {
		mainstage.superscene.motionFactor *= 1.1;
		updateMotionFactor();
	    }); }}, 250, 100);
	}
    }

    private void updateMotionFactor()    {refreshMotionFactor(); distributeMotionFactor(); disableMouseMovement(); }

    @FXML private void motionFactorIncreaseOnMouseReleased(MouseEvent event){motionFactorIncreaseTimer.cancel(); motionFactorIncreaseMousePressed = false;}
    @FXML private void motionFactorIncreaseOnMouseExited(MouseEvent event)  {motionFactorIncreaseRect.setFill(Color.rgb(0,0,0,0.0));}
//  =========    
    
//  =========    
    @FXML private void gravityFactorDecreaseOnMouseEntered(MouseEvent event){gravityFactorDecreaseRect.setFill(Color.rgb(255,255,255,0.5));}
    @FXML private void gravityFactorDecreaseOnMouseClicked(MouseEvent event){mainstage.superscene.gravityFactor /= 1.1; updateGravityFactor();}
    @FXML    private    void gravityFactorDecreaseOnMousePressed(MouseEvent event)
    {
	if (! gravityFactorDecreaseMousePressed)
	{
	    gravityFactorDecreaseMousePressed = true; gravityFactorDecreaseTimer = new Timer(); gravityFactorDecreaseTimer.scheduleAtFixedRate(new TimerTask() {@Override public void run() { Platform.runLater(() -> 
	    {
		mainstage.superscene.gravityFactor /= 1.1;
		updateGravityFactor();
	    }); }}, 250, 100);
	}
    }
    @FXML private void gravityFactorDecreaseOnMouseReleased(MouseEvent event)	    {gravityFactorDecreaseTimer.cancel(); gravityFactorDecreaseMousePressed = false;}
    @FXML private void gravityFactorDecreaseOnMouseExited(MouseEvent event)		    {gravityFactorDecreaseRect.setFill(Color.rgb(0,0,0,0.0));}
//  ---------    
    @FXML private void gravityFactorRectOnMouseEntered(MouseEvent event)		    {gravityFactorRect.setFill(Color.rgb(255,255,255,0.5));}
    @FXML private void gravityFactorRectOnMouseClicked(MouseEvent event)		    {if (event.getClickCount() == 1)	{ mainstage.setEditing(true); gravityFactorTField.setText(Double.toString(mainstage.superscene.gravityFactor)); gravityFactorLabel.setVisible(false); gravityFactorTField.setVisible(true); }}
    @FXML private void gravityFactorTFieldOnKeyPressed(KeyEvent event)
    {
	if (event.getCode() == KeyCode.ESCAPE)	{ gravityFactorTField.setVisible(false); gravityFactorLabel.setVisible(true); mainstage.setEditing(false); }
	if (event.getCode() == KeyCode.ENTER)	{ mainstage.superscene.gravityFactor = Double.parseDouble(gravityFactorTField.getText()); gravityFactorTField.setVisible(false); gravityFactorLabel.setVisible(true); updateGravityFactor(); mainstage.setEditing(false); }
    }
    @FXML private void gravityFactorRectOnMouseExited(MouseEvent event)		    {gravityFactorRect.setFill(Color.rgb(0,0,0,0.0));}
//  ---------    
    @FXML private void gravityFactorIncreaseOnMouseEntered(MouseEvent event)	    {gravityFactorIncreaseRect.setFill(Color.rgb(255,255,255,0.5));}
    @FXML private void gravityFactorIncreaseOnMouseClicked(MouseEvent event)	    {mainstage.superscene.gravityFactor *= 1.1; updateGravityFactor();}
    @FXML private void gravityFactorIncreaseOnMousePressed(MouseEvent event)
    {
	if (! gravityFactorIncreaseMousePressed)
	{
	    gravityFactorIncreaseMousePressed = true; gravityFactorIncreaseTimer = new Timer(); gravityFactorIncreaseTimer.scheduleAtFixedRate(new TimerTask() {@Override public void run() { Platform.runLater(() -> 
	    {
		mainstage.superscene.gravityFactor *= 1.1;
		updateGravityFactor();
	    }); }}, 250, 100);
	}
    }
    private void updateGravityFactor() {refreshGravityFactor(); /*distributeGravityFactor();*/ disableMouseMovement();}
    @FXML private void gravityFactorIncreaseOnMouseReleased(MouseEvent event)	{gravityFactorIncreaseTimer.cancel(); gravityFactorIncreaseMousePressed = false;}
    @FXML private void gravityFactorIncreaseOnMouseExited(MouseEvent event)     {gravityFactorIncreaseRect.setFill(Color.rgb(0,0,0,0.0));}
//  =========    

    public void distributeFrameRate()			    {mainstage.superscene.cancelTranslationTimer(); mainstage.superscene.createTranslationTimer();}
    public void distributeMotionRate()			    {mainstage.superscene.cancelMotionTimer(); mainstage.superscene.updateMotionRate2Motion(mainstage.superscene.getLastMotionRateFactor()); mainstage.superscene.createMotionTimer();}
    public void distributeMotionFactor()		    {mainstage.superscene.setMotionFactorForAllMotionStateChangeableNodes();}
//    public void distributeGravityFactor()		    {mainstage.superscene.setGravityFactorForAllMotionStateChangeableNodes();}

    public   Group getRootGroup()			    {return rootGroup;}

//    public void disableMouseMovement()			    {mainstage.stage.removeEventFilter(MouseEvent.MOUSE_MOVED, mainstage.mouseMovedHandler); mainstage.superscene.subScene.setCursor(Cursor.DEFAULT);}
    @FXML   public void disableMouseMovement()		    {mainstage.disableMouseMovement();}
    private void disableMouseMove(MouseEvent event)	    {disableMouseMovement();}
    

    public   double getFrameRate()			{return mainstage.superscene.targetFrameRate;}
//    public   void   setFrameRate(double frameRate)	{this.frameRate = frameRate; frameRateLabel.setText(Double.toString(this.frameRate)); }

    public   VBox getNodesVBox()		{return nodesVBox;}
    public   void setNodesVBox(VBox nodesVBox)	{this.nodesVBox = nodesVBox;}
    public   VBox getMainVBox()			{return mainVBox;}

    public   boolean isShowing()		{return showing;}
}