package rdj;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import javax.script.ScriptException;

public class Celestials
{
    private static ArrayList<String[]> line;
    private static ArrayList<Celestial> celestials;
    
    private static String[] Gstr = {"GravitationalConstant","6.67384e-11"};    // Gravitational constant (N.m^2/kg^2)
    private static double   G = 6.67384e-11;    // Gravitational constant (N.m^2/kg^2)

    private static String   begin = "";
    private static boolean  load = false;
    private static String   name = "";
    private static String[] mass = {"Mass","0.0"};
    private static String[] radius = {"Radius","0.0"};
    private static boolean  gravitational = false;
    private static boolean  collisional = false;
    private static int	    group = 0;
    private static int	    polarity = 0;
    private static String   end = "";
    private static Path	    deffile;

    private static void loadCelestials(int verbosity)
    {
	celestials = new ArrayList<>();
	line = new ArrayList<String[]>();

	// Load Objects
	FileSystem defaultfs = FileSystems.getDefault(); // FS Object default SSD
//        Path localdeffile = defaultfs.getPath(System.getProperty("user.dir"),"rdj","scenes","celestials.def");
        Path localdeffile = defaultfs.getPath(System.getProperty("user.home"),SuperScene.PRODUCT,"rdj","scenes","celestials.def");
	deffile = localdeffile;
	if (1<=verbosity) { System.out.println("loadCelestials: " + deffile.toString()); }
	
	Scanner scanner = null; try { scanner = new Scanner(new File(deffile.toString())); }	catch (FileNotFoundException ex) { System.err.println("loadCelestials() scanner = new Scanner(new File(" + deffile.toString() + ")); " + ex); }
	String[] fields;
	while (scanner.hasNextLine())
	{
	    fields = scanner.nextLine().split("\\s+\\=\\s+");
	    if ((fields.length > 1) && (!fields[0].matches("^\\s*#.*"))) { line.add(new String[]{clean(fields[0]), clean(fields[1])}); }
	}
	scanner.close();

	String field[];
	Iterator<String[]> iterator = line.iterator();
	while (iterator.hasNext())
	{
	    field = iterator.next();
	    if (3<=verbosity) { System.out.println("Line -> " + field[0] + " = " + field[1]); }//SceneTargetFrameRate
	    
	    if	    (field[0].equalsIgnoreCase(Gstr[0]))		    { Gstr[1] = field[1]; G = eval(Gstr); } // immediate assignment
	    else if (field[0].equalsIgnoreCase("Begin"))		    { begin = field[1]; resetCelestialVars(); }
	    else if (field[0].equalsIgnoreCase("Load"))			    { load = Boolean.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase("Name"))			    { name = field[1]; }
	    else if (field[0].equalsIgnoreCase(mass[0]))			    { mass[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase(radius[0]))		    { radius[1] = field[1]; }
	    else if (field[0].equalsIgnoreCase("Gravitational"))	    { gravitational = Boolean.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase("Collisional"))		    { collisional = Boolean.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase("Group"))		    { group = Integer.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase("Polarity"))		    { polarity = Integer.valueOf(field[1]); }
	    else if (field[0].equalsIgnoreCase("End"))
	    {
		if ((field[1].equalsIgnoreCase("Celestial")) && (load))
		{
		    end = field[1];
		    celestials.add(new Celestial(name, eval(mass), eval(radius), gravitational, collisional, group, polarity));
		}
	    } else { System.err.println("WARNING in file: " + deffile.toString() + " UNKNOWN KEY: " + field[0]); } 
	}

    
    }
    
    protected static double eval(String[] str)
    {
        double output = 0;
        Object number = new Object();
	try { number = LoadScene.getScriptEngine().eval(str[1]); } // Single heavy expensive object from LoadScene
	catch (ScriptException ex) { System.err.println("Celestials.eval(String[] str): ScriptException: " + ex); }
        finally
        {
            if      (number instanceof Integer) { output = (int) number;}
            else if (number instanceof Long)    { output = (long) number; }
            else if (number instanceof Double)  { output = (double) number; }
            else                                { System.err.println("Error in file: " + deffile.toString() + " can not evaluate field: " + str[0] + " = " + str[1]); output = (double) number; }
        }
        return output;
    }
//    private static double eval(String[] str) { return LoadScene.eval(str); }
    
    public static Celestial get(String name, int verbosity)
    {
	if (1<=verbosity) { System.out.println("Celestial get(" + name + ")"); }
	if ( celestials == null ) { loadCelestials(verbosity); }
	Celestial celestial = null;
	for (Celestial celest:celestials) { if (celest.getName().toLowerCase().equals(name.toLowerCase())) { celestial = celest; break; } }
	if (celestial == null) { System.out.println("Celestials.get(\"" + name + "\")! demoted to: Celestials.get(\"dummy\")");	celestial = new Celestial("dummy", 0, 0, false, false, 0, 0);}
	if (2<=verbosity) { System.out.println(celestial.toString()); }
	return celestial;
    }
    
    private static String clean(final String input)
    {
	String output;
	if (input.indexOf("#")>1) { output = input.substring(0, input.indexOf("#")); }
	else { output = input; }
	return output.trim();
    }
    
    private static void resetCelestialVars()
    {
	Gstr[0] = "GravitationalConstant"; Gstr[1] = "6.67384e-11";
	G = 6.67384e-11;
	
	begin = "";
	load = false;
	name = "";
	mass[0] = "Mass"; mass[1] = "0.0";
	radius[0] = "Radius"; radius[1] = "0.0";
	gravitational = false;
	collisional = false;
	polarity = 0;
	end = "";
    }
    public static double getGravitConst() { return G; }
}

