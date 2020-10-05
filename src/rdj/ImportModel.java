package rdj;

import com.interactivemesh.jfx.importer.Viewpoint;
import com.interactivemesh.jfx.importer.col.ColAsset;
import com.interactivemesh.jfx.importer.col.ColModelImporter;
import com.interactivemesh.jfx.importer.obj.ObjImportOption;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import com.interactivemesh.jfx.importer.stl.StlImportOption;
import com.interactivemesh.jfx.importer.stl.StlMeshImporter;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Preloader.ErrorNotification;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;

public class ImportModel // extends Application
{
//    private ColModelImporter colimporter;
//    
    private Node[]	nodes;
//    private ColAsset	colAsset;
//    private Map<String, PhongMaterial> namedMaterials;
//    private Map<String, Node> namedNodes;
//    private Viewpoint[] viewpoints;
    private Group	group;
    private String	output;
    
    public ImportModel(MainStage sn, String filename, String meshNodeFilter, int verbose)
    {
	group = new Group();
	String extension = "";

	int i = filename.lastIndexOf('.');
	if (i >= 0) {extension = filename.substring(i+1);} // get extension
	if (extension.equalsIgnoreCase("dae")) {importColladaDAE(sn, filename, meshNodeFilter, verbose);}
	else if (extension.equalsIgnoreCase("obj")) {importWavefrontObject(sn, filename, meshNodeFilter, verbose);}
	else if (extension.equalsIgnoreCase("stl")) {importSTL(sn, filename, meshNodeFilter, verbose);}
	
    }
    
    private void importColladaDAE(MainStage sn, String filename, String meshNodeFilter, int verbosity)
    {
        URL url = null;
//        url = getClass().getResource(filename);
        File file = new File("filename");
//        Path path = Paths.get(System.getProperty("user.dir"), SuperScene.PRODUCT, filename); // working dir
        Path path = Paths.get(System.getProperty("user.home"), SuperScene.PRODUCT, filename); // working dir
        try { url = path.toUri().toURL(); } catch (MalformedURLException ex) { sn.notifyPreloader(new ErrorNotification("info","importColladaDAE",new Throwable(" MalformedURLException: " + path.toAbsolutePath().toString()))); }
	if (2<=verbosity) { if (sn != null) { sn.notifyPreloader(new ErrorNotification("info","importColladaDAE",new Throwable(" Loading file: " + filename + "..."))); } }

	final ColModelImporter colimporter =	new ColModelImporter();
	colimporter.read(url);

	final ColAsset colAsset =		colimporter.getAsset();
	final Node[] nodes =			colimporter.getImport();
	final Map<String, PhongMaterial>	namedMaterials = colimporter.getNamedMaterials();
	final Map<String, Node> namedNodes =	colimporter.getNamedNodes();
	final Viewpoint[] viewpoints =	colimporter.getViewpoints();

	colimporter.close();

	if (meshNodeFilter.length()>0)  {for (Node node:nodes) { if ( node.getId().equals(meshNodeFilter)) { group.getChildren().add(node);} }}
	else				{for (Node node:nodes) { group.getChildren().add(node); }}
	
	if (3<=verbosity)
	{
	    output = "\r\nAssets\r\n";
	    output += "asset title " + colAsset.getTitle() + "\r\n";
	    output += "asset unit name " + colAsset.getUnitName() + "\r\n";
	    output += "asset unit meter " + colAsset.getUnitMeter() + "\r\n";
	    output += "asset up axis " + colAsset.getUpAxis() + "\r\n";
	    output += "\r\n";

	    output += "Nodes\r\n";
	    for (Node node:nodes)
	    {
		output += "node.getId(): " + node.getId() + "\r\n";
		output += "node.getTypeSelector(): " + node.getTypeSelector() + "\r\n";
		output += "node.toString(): " + node.toString() + "\r\n";
		output += "node.getDepthTest(): " + node.getDepthTest() + "\r\n";
    //	    output += "node.getUserData().toString(): " + node.getUserData().toString() + "\r\n";
		output += "\r\n";
	    }
	    output += "\r\n";

	    output += "Materials\r\n";

	    namedMaterials.entrySet().stream().forEach((pmaterial) -> { output += "phong material: " + pmaterial.getKey() + " -> " + pmaterial.getValue() + "\r\n"; });
	    output += "\r\n";

	    output += "NamedNodes\r\n";
	    namedNodes.entrySet().stream().forEach((thisnode) -> { output += "node: " + thisnode.getKey() + " -> " + thisnode.getValue() + "\r\n"; });
	    output += "\r\n";

	    output += "Viewpoints\r\n";
	    if (viewpoints != null) for (Viewpoint vpoint : viewpoints)
	    {
		output += "vpoint.getName()" + vpoint.getName() + "\r\n"; 
		output += "vpoint.getFieldOfView()" + vpoint.getFieldOfView() + "\r\n"; 
		output += "vpoint.getNearClip()" + vpoint.getNearClip() + "\r\n"; 
		output += "vpoint.getFarClip()" + vpoint.getFarClip() + "\r\n"; 
		output += "vpoint.getTransform().toString()" + vpoint.getTransform().toString() + "\r\n"; 
		output += "vpoint.isPerspective()" + vpoint.isPerspective() + "\r\n"; 
		output += "vpoint.isVerticalFieldOfView()" + vpoint.isVerticalFieldOfView() + "\r\n"; 
	    }
	    output += "\r\n";

	    System.out.println(output);
	}
    }
    
    private void importWavefrontObject(MainStage sn, String filename, String meshNodeFilter, int verbosity)
    {
        URL url; url = getClass().getResource(filename);
	if (2<=verbosity) { if (sn != null) { sn.notifyPreloader(new ErrorNotification("info","ImportWavefrontObject",new Throwable(" Loading file: " + filename + "..."))); }}

	ObjModelImporter objimporter = new ObjModelImporter();
	objimporter.read(url);

	nodes = objimporter.getImport();
	Map<String, MeshView> namedMeshViews = objimporter.getNamedMeshViews();
	Map<String, PhongMaterial> namedMaterials =    objimporter.getNamedMaterials();
	EnumSet<ObjImportOption> options = objimporter.getOptions();

	objimporter.close();
	
	if (meshNodeFilter.length()>0)  {for (Node node:nodes) { if ( node.getId().equals(meshNodeFilter)) { group.getChildren().add(node);} }}
	else				{for (Node node:nodes) { group.getChildren().add(node); }}
	
	if (3<=verbosity)
	{
	    output = "\r\nNodes\r\n";
	    for (Node node:nodes)
	    {
		output += "node.getId(): " + node.getId() + "\r\n";
		output += "node.getTypeSelector(): " + node.getTypeSelector() + "\r\n";
		output += "node.toString(): " + node.toString() + "\r\n";
		output += "node.getDepthTest(): " + node.getDepthTest() + "\r\n";
		output += "node.getUserData().toString(): " + node.getUserData().toString() + "\r\n";
		output += "\r\n";
	    }
	    output += "\r\n";

	    output += "MeshViews\r\n";
	    namedMeshViews.entrySet().stream().forEach((meshview) -> { output += "meshview: " + meshview.getKey() + " -> " + meshview.getValue() + "\r\n"; });
	    output += "\r\n";

	    output += "Materials\r\n";
	    namedMaterials.entrySet().stream().forEach((pmaterial) -> { output += "phong material: " + pmaterial.getKey() + " -> " + pmaterial.getValue() + "\r\n"; });
	    output += "\r\n";

	    output += "Options\r\n";
	    if (options != null) for (ObjImportOption option : options) { output += "option: " + option.toString() + "\r\n"; }
	    output += "\r\n";

	    System.out.println(output);
	}
    }
    
    private void importSTL(MainStage sn, String filename, String meshNodeFilter, int verbosity)
    {
        URL url; url = getClass().getResource(filename);
	if (2<=verbosity) { if (sn != null) { sn.notifyPreloader(new ErrorNotification("info","ImportSTL",new Throwable(" Loading file: " + filename + "..."))); }}
	
	StlMeshImporter stlimporter = new StlMeshImporter();
	stlimporter.read(url);

	Mesh mesh =	    stlimporter.getImport();
	nodes =	    new MeshView[] { new MeshView(mesh) };
	EnumSet<StlImportOption> options = stlimporter.getOptions();

	stlimporter.close();
	
	if (meshNodeFilter.length()>0)  {for (Node node:nodes) { if ( node.getId().equals(meshNodeFilter)) { group.getChildren().add(node);} }}
	else				{for (Node node:nodes) { group.getChildren().add(node); }}
	
	if (3<=verbosity)
	{
	    String output;
	    output = "\r\nNodes\r\n";
	    for (Node node:nodes)
	    {
		output += "node.getId(): " + node.getId() + "\r\n";
		output += "node.getTypeSelector(): " + node.getTypeSelector() + "\r\n";
		output += "node.toString(): " + node.toString() + "\r\n";
		output += "node.getDepthTest(): " + node.getDepthTest() + "\r\n";
		output += "node.getUserData().toString(): " + node.getUserData().toString() + "\r\n";
		output += "\r\n";
	    }
	    output += "\r\n";

	    output += "Options\r\n";
	    if (options != null) for (StlImportOption option : options) { output += "option: " + option.toString() + "\r\n"; }
	    output += "\r\n";

	    System.out.println(output);
	}
    }
    
    public Group getGroup()					{ return group; }
    
    static String path2String(String path, Charset encoding) throws IOException 
    {
      byte[] encoded = Files.readAllBytes(Paths.get(path));
      return new String(encoded, encoding);
    }
}