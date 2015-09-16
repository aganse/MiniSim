/** Submarine.java */

/** <B>Submarine</B> - submarine class for use in MiniSim simulator.
 *  Inherits from class NavalObject.
 *  See the MiniSim description for more info about MiniSim.
 * 
 *  <I>Written by 
 *  <A HREF="mailto:aganse@apl.washington.edu">Andy Ganse</A>,
 *  Applied Physics Lab, University of Washington, copyright 1999 by UW.
 *  </I>
 *
 *  <P>
 *  @see MiniSim
 */
public class Submarine extends NavalObject {

    double a, b, heading=0;
    String name;

    public Submarine(){
	a=Math.random()*3 + 3;
	b=Math.random()*3 + 3;
    }

    public void updatePosition( double simTime ){
	// just some kind of "all over the place" path that stays in bounds
        double r = 16; // radius
	double s=25; //slowness

	lon = r * java.lang.Math.sin(simTime/a) *
	      Math.cos(simTime/s) + 20;
	lat = r * Math.sin(simTime/b) *
	      Math.sin(simTime/s) + 20;
	// computing derivatives to find direction (tanget vector) for heading
        double dxdt = r/a * Math.cos(simTime/a) *
	    Math.cos(simTime/s) -
	    r/s * Math.sin(simTime/a) *
	    Math.sin(simTime/s);
        double dydt = r/b * Math.cos(simTime/b) *
	    Math.sin(simTime/s) +
	    r/s * Math.sin(simTime/a) *
	    Math.cos(simTime/s);
	double theta = Math.atan2(dydt,dxdt);

	/** old simple circle path */
	//lon = r * Math.cos(simTime/s) +20;
	//lat = r * Math.sin(simTime/s) +20;
	//double dxdt = -r/s * Math.sin(simTime/s);
	//double dydt = r/s * Math.cos(simTime/s);

	heading = ( theta*360/(2*Math.PI)+90 )%360;
    }

    public void setName( String name ){
	this.name=name;
    }
    
    public void SetInitHeading( double initHeading ){
	this.heading=initHeading;
    }

    /** getTargetStrength - takes trueBearingFromBuoy because then only uses
     *  sub's heading value internally, so sonobuoy object requesting target
     *  strength won't know sub's heading directly.  trueBearing in degrees. */
    public double getTargetStrength( double trueBearingFromBuoy ){
	// just a simple trig-based function to get a fig-8 shape + offset:
	double sin_t = Math.sin( ( trueBearingFromBuoy +
					     180 - heading ) % 360 );
	return .9 * ( sin_t * sin_t * sin_t * sin_t ) + .1;
    }

    public String getName(){
	return name;
    }

    public double getHeading(){
	return heading;
    }

}
