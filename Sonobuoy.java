/** Sonobuoy.java */


/** <B>Sonobuoy</B> - sonobuoy class for use in MiniSim simulator.
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
public class Sonobuoy extends NavalObject {

    private String model, commMsg;
    private double senseRange, senseDepth;

    public Sonobuoy(){
    }

    public void setModel( String buoyModel ){
	this.model=buoyModel;
	if(buoyModel.compareTo("LOFAR")==0) senseRange=10;
	if(buoyModel.compareTo("DIFAR")==0) senseRange=20;
	if(buoyModel.compareTo("DICASS")==0) senseRange=40;
	if(buoyModel.compareTo("VLAD")==0) senseRange=50;
    }

    /** set sensing range, ie radius around buoy, in decimal degrees */
    public void setSenseRange( double senseRange ){
	this.senseRange=senseRange;
    }

    public void setSenseDepth( double senseDepth ){
	this.senseDepth=senseDepth;
    }

    /** get sensing range, ie radius around buoy, in decimal degrees */
    public double getSenseRange(){
	return senseRange;
    }

    public String getModel(){
	return model;
    }

    /** only "might sense" because even though now within distance range we
     *  still take into account TS variability due to sub's heading (if sub) */
    public void mightSense(NavalObject navObj){
	if(navObj instanceof Submarine) {
	    Submarine sub = (Submarine)navObj;
	    // get sub's true bearing to this buoy, using atan2 method on diffs
	    //double trueBearingOfSub=360/(2*Math.PI) * atan2(lonDiff,latDiff);

	    // for now just assume always senses...

	    // send msg saying "hey I saw him!"
	    sendComm("Buoy #"+idNum+" just detected submarine "+sub.getName());
	}
    }

    public void updatePosition( double simTime ){
	// (simple drift function)
	lat+=.005;
	lon-=.005;
    }

}
