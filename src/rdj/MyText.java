package rdj;

import java.util.Scanner;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class MyText extends Node3D
{

    public MyText(SuperScene sceneInterface, String id, Celestial celestial, String mytext, String fontName, String fontWeight, String fontPosture, double fontSize, String color, double alpha, boolean cache, int verbosity)
    {
	super.superscene = sceneInterface;
	super.celestial = celestial;
	super.camera = false;
	super.light = false;
	super.id = id;
	super.verbosity = verbosity;

//	createMenu();
	
	int linecounter = 0;
	Scanner scanner = new Scanner(mytext);
	while (scanner.hasNextLine())
	{
	    String line = scanner.nextLine();
	    Font font = Font.font(fontName, FontWeight.valueOf(fontWeight), FontPosture.valueOf(fontPosture), fontSize);
//	    Text text = new Text(line); text.setFill(Color.GREY); text.setFont(Font.font(fontName, fontSize)); text.setTranslateY((linecounter * fontSize)+2);
	    Text text = new Text(line); text.setFill(Color.web(color,alpha)); text.setFont(font); text.setTranslateY((linecounter * fontSize)+2);
//	    font(String family, FontWeight weight, FontPosture posture, double size);
	    nodeGroup.getChildren().add(text);
	    linecounter++;
	}
	scanner.close();
	
	setCaching(cache);
    }
}