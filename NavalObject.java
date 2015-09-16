/** NavalObject.java */


/** <B>NavalObject</B> - general class for objects-in-water in MiniSim
 *  simulator, from which submarines and sonobuoys are inherited (others may
 *  be added to MiniSim later).  Note object might not necessarily be a
 *  powered vessel - bearing and velocity might apply to drift.
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
public abstract class NavalObject {

    protected double lat;     // decimal degrees
    protected double lon;     // decimal degrees
    protected double depth;   // ft
    protected double speed;   // kts
    protected String commMsg;
    protected int idNum=0;    // some kind of unique, automatically assigned
                              // ID # resolve like-named navalObjects


    public void setIDNum( int idNum ){
	this.idNum=idNum;
    }

    public void setInitLon( double initLon ){
	this.lon=initLon;
    }

    public void setInitLat( double initLat ){
	this.lat=initLat;
    }

    public void SetInitDepth( double initDepth ){
	this.depth=initDepth;
    }

    public void SetInitSpeed( double initSpeed ){
	this.speed=initSpeed;
    }

    public abstract void updatePosition( double simTime );

    /** sends out a "comm" message to the rest of the sim */
    protected void sendComm(String msg) {
	this.commMsg=msg;
    }

    /** receive latest message sent by sonobuoy */
    public String getLatestComm() {
	String msg = commMsg;
	commMsg = null;
	return msg;
    }

    public int getIDNum(){
	return idNum;
    }

    public double getLon(){
	return lon;
    }

    public double getLat(){
	return lat;
    }

    public double getDepth(){
	return depth;
    }

    public double getSpeed(){
	return speed;
    }

}
