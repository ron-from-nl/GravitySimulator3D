package rdj;

public class Celestial
{
    private String name;			    // Id
    private double mass;			    // KG
    private double radius;			    // Meters
    private static final double G = 6.67408e-11;    // Gravitational constant (N.m^2/kg^2)
    private boolean gravitational;		    // Under gravitational influemce
    private boolean collisional;		    // Under colliding influemce
    private int group;				    // Gravity Group (only equal celestial groups influence each other gravitationally)
    private int polarity;			    // Gravitational Polarity 1,0,-1


    public Celestial()
    {
	this.name = "Undefined";
	this.mass = 0;
	this.radius = 0;
	this.group = 0; // 0 standard group
	this.polarity = 0; // 0 = classic; -1,1 = quantum 
    }

    public Celestial(String name, double mass, double radius, boolean gravitational, boolean collisional, int group, int polarity)
    {
	this.name = name;
	this.mass = mass;
	this.radius = radius;
	this.gravitational = gravitational;
	this.collisional = collisional;
	this.group = group;
	this.polarity = polarity;
    }

    public String getName()					    { return name; }
    public double getMass()					    { return mass; }
    public double getRadius()					    { return radius; }
    public boolean isGravitational()				    { return gravitational;}
    public boolean isCollisional()				    { return collisional;}
    public int getGroup()					    { return group; }
    public int getPolarity()					    { return polarity; }
    
    public void setName(String name)				    { this.name = name; }
    public void setMass(double mass)				    { this.mass = mass; }
    public void setRadius(double radius)			    { this.radius = radius; }

    public  void setGravitational(boolean gravitational)	    { this.gravitational = gravitational;}
    public  boolean switchGravitational()			    { gravitational = !gravitational; return gravitational;}
    
    public  void setCollisional(boolean collisional)		    { this.collisional = collisional;}
    public  boolean switchCollisional()				    { collisional = !collisional; return collisional;}
    public void setGroup(int param)				    { this.group = param; }
    public void setPolarity(int polarity)			    { this.polarity = polarity; }
    
    @Override public String toString()
    {
	String output;
	output  = ("Name: " +		name + "\r\n");
	output += ("Mass: " +		mass + "\r\n");
	output += ("Radius: " +		radius + "\r\n");
	output += ("Gravitational: " +	gravitational + "\r\n");
	output += ("Collisional: " +	collisional + "\r\n");
	output += ("Group: " +		group + "\r\n");
	output += ("Polarity: " +	polarity);
	return output;
    }

}