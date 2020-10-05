package rdj;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;

public class Coordinal
{
    private final DoubleProperty sxProp;  // Scale
    private final DoubleProperty syProp;
    private final DoubleProperty szProp;
    
    private final DoubleProperty sxmProp; // Scale Motion
    private final DoubleProperty symProp;
    private final DoubleProperty szmProp;
    
    private final Point3D	 location;
    
    private final DoubleProperty txProp;  // Translate
    private final DoubleProperty tyProp;
    private final DoubleProperty tzProp;
    
    private final DoubleProperty txmProp; // Translate Motion
    private final DoubleProperty tymProp;
    private final DoubleProperty tzmProp;
    
    private final DoubleProperty rxProp;  // Rotate
    private final DoubleProperty ryProp;
    private final DoubleProperty rzProp;
    
    private final DoubleProperty rxmProp; // Rotate Motion
    private final DoubleProperty rymProp;
    private final DoubleProperty rzmProp;
    
    private final DoubleProperty rpxxProp; // Rotate Pivots
    private final DoubleProperty rpxyProp;
    private final DoubleProperty rpxzProp;
    
    private final DoubleProperty rpyxProp;
    private final DoubleProperty rpyyProp;
    private final DoubleProperty rpyzProp;
    
    private final DoubleProperty rpzxProp;
    private final DoubleProperty rpzyProp;
    private final DoubleProperty rpzzProp;
    
    public Coordinal()
    {
	sxProp = new SimpleDoubleProperty(this, "SXProp", 1);
	syProp = new SimpleDoubleProperty(this, "SYProp", 1);
	szProp = new SimpleDoubleProperty(this, "SZProp", 1);

	sxmProp = new SimpleDoubleProperty(this, "SXMProp", 0);
	symProp = new SimpleDoubleProperty(this, "SYMProp", 0);
	szmProp = new SimpleDoubleProperty(this, "SZMProp", 0);

	location = new Point3D(0,0,0);
	
	txProp = new SimpleDoubleProperty(this, "TXProp", 0);
	tyProp = new SimpleDoubleProperty(this, "TYProp", 0);
	tzProp = new SimpleDoubleProperty(this, "TZProp", 0);

	txmProp = new SimpleDoubleProperty(this, "TXMProp", 0);
	tymProp = new SimpleDoubleProperty(this, "TYMProp", 0);
	tzmProp = new SimpleDoubleProperty(this, "TZMProp", 0);

	rxProp = new SimpleDoubleProperty(this, "RXProp", 0);  
	ryProp = new SimpleDoubleProperty(this, "RYProp", 0);
	rzProp = new SimpleDoubleProperty(this, "RZProp", 0);

	rxmProp = new SimpleDoubleProperty(this, "RXMProp", 0);  
	rymProp = new SimpleDoubleProperty(this, "RYMProp", 0);
	rzmProp = new SimpleDoubleProperty(this, "RZMProp", 0);

	rpxxProp = new SimpleDoubleProperty(this, "RPXXProp", 0);  
	rpxyProp = new SimpleDoubleProperty(this, "RPXYProp", 0);
	rpxzProp = new SimpleDoubleProperty(this, "RPXZProp", 0);

	rpyxProp = new SimpleDoubleProperty(this, "RPYXProp", 0);  
	rpyyProp = new SimpleDoubleProperty(this, "RPYYProp", 0);
	rpyzProp = new SimpleDoubleProperty(this, "RPYZProp", 0);

	rpzxProp = new SimpleDoubleProperty(this, "RPZXProp", 0);  
	rpzyProp = new SimpleDoubleProperty(this, "RPZYProp", 0);
	rpzzProp = new SimpleDoubleProperty(this, "RPZZProp", 0);

    }

    public void backupTo(Coordinal bupCoordinal)
    {
	bupCoordinal.sxProp.set(sxProp.get());	    bupCoordinal.syProp.set(syProp.get());	bupCoordinal.szProp.set(szProp.get()); 
	bupCoordinal.sxmProp.set(sxmProp.get());    bupCoordinal.symProp.set(symProp.get());	bupCoordinal.szmProp.set(szmProp.get()); 
	bupCoordinal.txProp.set(txProp.get());	    bupCoordinal.tyProp.set(tyProp.get());	bupCoordinal.tzProp.set(tzProp.get()); 
	bupCoordinal.txmProp.set(txmProp.get());    bupCoordinal.tymProp.set(tymProp.get());	bupCoordinal.tzmProp.set(tzmProp.get()); 
	bupCoordinal.rxProp.set(rxProp.get());	    bupCoordinal.ryProp.set(ryProp.get());	bupCoordinal.rzProp.set(rzProp.get()); 
	bupCoordinal.rxmProp.set(rxmProp.get());    bupCoordinal.rymProp.set(rymProp.get());	bupCoordinal.rzmProp.set(rzmProp.get()); 
	bupCoordinal.rpxxProp.set(rpxxProp.get());  bupCoordinal.rpxyProp.set(rpxyProp.get());	bupCoordinal.rpxzProp.set(rpxzProp.get()); 
	bupCoordinal.rpyxProp.set(rpyxProp.get());  bupCoordinal.rpyyProp.set(rpyyProp.get());	bupCoordinal.rpyzProp.set(rpyzProp.get()); 
	bupCoordinal.rpzxProp.set(rpzxProp.get());  bupCoordinal.rpzyProp.set(rpzyProp.get());	bupCoordinal.rpzzProp.set(rpzzProp.get()); 
    }
    
    public void restoreFrom(Coordinal bupCoordinal)
    {
	sxProp.set(bupCoordinal.sxProp.get());	    syProp.set(bupCoordinal.syProp.get());	szProp.set(bupCoordinal.szProp.get()); 
	sxmProp.set(bupCoordinal.sxmProp.get());    symProp.set(bupCoordinal.symProp.get());	szmProp.set(bupCoordinal.szmProp.get()); 
	txProp.set(bupCoordinal.txProp.get());	    tyProp.set(bupCoordinal.tyProp.get());	tzProp.set(bupCoordinal.tzProp.get()); 
	txmProp.set(bupCoordinal.txmProp.get());    tymProp.set(bupCoordinal.tymProp.get());	tzmProp.set(bupCoordinal.tzmProp.get()); 
	rxProp.set(bupCoordinal.rxProp.get());	    ryProp.set(bupCoordinal.ryProp.get());	rzProp.set(bupCoordinal.rzProp.get()); 
	rxmProp.set(bupCoordinal.rxmProp.get());    rymProp.set(bupCoordinal.rymProp.get());	rzmProp.set(bupCoordinal.rzmProp.get()); 
	rpxxProp.set(bupCoordinal.rpxxProp.get());  rpxyProp.set(bupCoordinal.rpxyProp.get());	rpxzProp.set(bupCoordinal.rpxzProp.get()); 
	rpyxProp.set(bupCoordinal.rpyxProp.get());  rpyyProp.set(bupCoordinal.rpyyProp.get());	rpyzProp.set(bupCoordinal.rpyzProp.get()); 
	rpzxProp.set(bupCoordinal.rpzxProp.get());  rpzyProp.set(bupCoordinal.rpzyProp.get());	rpzzProp.set(bupCoordinal.rpzzProp.get()); 
    }
    
    // Scale
    
    public DoubleProperty getSxProp()				{return sxProp;}
    public DoubleProperty getSyProp()				{return syProp;}
    public DoubleProperty getSzProp()				{return szProp;}

//    public void setSxProp(DoubleProperty sxProp)		{this.sxProp = sxProp;}
//    public void setSyProp(DoubleProperty syProp)		{this.syProp = syProp;}
//    public void setSzProp(DoubleProperty szProp)		{this.szProp = szProp;}

    // Scale Motion
    
    public DoubleProperty getSxmProp()				{return sxmProp;}
    public DoubleProperty getSymProp()				{return symProp;}
    public DoubleProperty getSzmProp()				{return szmProp;}

//    public void setSxmProp(DoubleProperty sxmProp)		{this.sxmProp = sxmProp;}
//    public void setSymProp(DoubleProperty symProp)		{this.symProp = symProp;}
//    public void setSzmProp(DoubleProperty szmProp)		{this.szmProp = szmProp;}

    // Translate

    public DoubleProperty getTxProp()				{return txProp;}
    public DoubleProperty getTyProp()				{return tyProp;}
    public DoubleProperty getTzProp()				{return tzProp;}

    public Point3D getLocation()				{return new Point3D(txProp.get(),tyProp.get(),tzProp.get()); }

//    public void setTxProp(DoubleProperty txProp)		{this.txProp = txProp;}
//    public void setTyProp(DoubleProperty tyProp)		{this.tyProp = tyProp;}
//    public void setTzProp(DoubleProperty tzProp)		{this.tzProp = tzProp;}

    // Translate Motion

    public DoubleProperty getTxmProp()				{return txmProp;}
    public DoubleProperty getTymProp()				{return tymProp;}
    public DoubleProperty getTzmProp()				{return tzmProp;}

//    public void setTxmProp(DoubleProperty txmProp)		{this.txmProp = txmProp;}
//    public void setTymProp(DoubleProperty tymProp)		{this.tymProp = tymProp;}
//    public void setTzmProp(DoubleProperty tzmProp)		{this.tzmProp = tzmProp;}

    // Rotate
    
    public DoubleProperty getRxProp()				{return rxProp;}
    public DoubleProperty getRyProp()				{return ryProp;}
    public DoubleProperty getRzProp()				{return rzProp;}

//    public void setRxProp(DoubleProperty rxProp)		{this.rxProp = rxProp;}
//    public void setRyProp(DoubleProperty ryProp)		{this.ryProp = ryProp;}
//    public void setRzProp(DoubleProperty rzProp)		{this.rzProp = rzProp;}

    // Rotate Motion
    
    public DoubleProperty getRxmProp()				{return rxmProp;}
    public DoubleProperty getRymProp()				{return rymProp;}
    public DoubleProperty getRzmProp()				{return rzmProp;}

//    public void setRxmProp(DoubleProperty rxmProp)		{this.rxmProp = rxmProp;}
//    public void setRymProp(DoubleProperty rymProp)		{this.rymProp = rymProp;}
//    public void setRzmProp(DoubleProperty rzmProp)		{this.rzmProp = rzmProp;}

    // Rotation Axis Pivots
    
    public DoubleProperty getRPXXProp()				{return rpxxProp;}
    public DoubleProperty getRPXYProp()				{return rpxyProp;}
    public DoubleProperty getRPXZProp()				{return rpxzProp;}

    public DoubleProperty getRPYXProp()				{return rpyxProp;}
    public DoubleProperty getRPYYProp()				{return rpyyProp;}
    public DoubleProperty getRPYZProp()				{return rpyzProp;}

    public DoubleProperty getRPZXProp()				{return rpzxProp;}
    public DoubleProperty getRPZYProp()				{return rpzyProp;}
    public DoubleProperty getRPZZProp()				{return rpzzProp;}

//    public void setRPXXProp(DoubleProperty rpxxProp)		{this.rpxxProp = rpxxProp;}
//    public void setRPXYProp(DoubleProperty rpxyProp)		{this.rpxyProp = rpxyProp;}
//    public void setRPXZProp(DoubleProperty rpxzProp)		{this.rpxzProp = rpxzProp;}
//
//    public void setRPYXProp(DoubleProperty rpyxProp)		{this.rpyxProp = rpyxProp;}
//    public void setRPYYProp(DoubleProperty rpyyProp)		{this.rpyyProp = rpyyProp;}
//    public void setRPYZProp(DoubleProperty rpyzProp)		{this.rpyzProp = rpyzProp;}
//
//    public void setRPZXProp(DoubleProperty rpzxProp)		{this.rpzxProp = rpzxProp;}
//    public void setRPZYProp(DoubleProperty rpzyProp)		{this.rpzyProp = rpzyProp;}
//    public void setRPZZProp(DoubleProperty rpzzProp)		{this.rpzzProp = rpzzProp;}
    
    public void recalculateMotion(double param) // Used by updateMotionRate2Motion
    {
	txmProp.set(txmProp.get()/param);
	tymProp.set(tymProp.get()/param);
	tzmProp.set(tzmProp.get()/param);
	
	rxmProp.set(rxmProp.get()/param);
	rymProp.set(rymProp.get()/param);
	rzmProp.set(rzmProp.get()/param);
    }
    
    public void negateMotion()					{txmProp.set(-txmProp.get()); tymProp.set(-tymProp.get()); tzmProp.set(-tzmProp.get());}

    @Override public String toString()
    {
	String output;
	output  = ("Scale: " +		getSxProp().get() +	"," + getSyProp().get() +	"," + getSzProp().get() + "\r\n");
	output += ("Translate: " +	getTxProp().get() +	"," + getTyProp().get() +	"," + getTzProp().get() + "\r\n");
	output += ("TMotion: " +	getTxmProp().get() +	"," + getTymProp().get() +	"," + getTzmProp().get() + "\r\n");
	output += ("Rotate: " +		getRxProp().get() +	"," + getRyProp().get() +	"," + getRzProp().get() + "\r\n");
	output += ("RMotion: " +	getRxmProp().get() +	"," + getRymProp().get() +	"," + getRzmProp().get() + "\r\n");
	output += ("RXPivots: " +	getRPXXProp().get() +	"," + getRPXYProp().get() +	"," + getRPXZProp().get() + "\r\n");
	output += ("RYPivots: " +	getRPYXProp().get() +	"," + getRPYYProp().get() +	"," + getRPYZProp().get() + "\r\n");
	output += ("RZPivots: " +	getRPZXProp().get() +	"," + getRPZYProp().get() +	"," + getRPZZProp().get() + "\r\n");
	
	return output;
    }
}