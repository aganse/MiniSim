/** SimInterface.java - independent demo version (no networking) */

import java.util.*; // for Vectors & Enumeration
import javax.swing.Timer; // just listing it up here to keep track of things;
                          // but had specify explicitly it below because there
                          // is a Timer class in java.util also.
import java.awt.event.*;  // for ActionListener

/** <B>SimInterface</B> - simulation interface object for simulator MiniSim.
 *  Independent demo applet version, no networking in this one.
 *  See the MiniSim description for more info.
 * 
 *  <I>Written by 
 *  <A HREF="mailto:aganse@apl.washington.edu">Andy Ganse</A>,
 *  Applied Physics Lab, University of Washington, copyright 1999 by UW.
 *  </I>
 *
 *  <P>
 *  @see MiniSim
 */
public class SimInterface implements ActionListener {

    private int buoyNum=0;
    private double simTime=0;
    private double maxSimTime=500;  // default in case maxSimTime not set
    private boolean mySubReady=false, simRegistered=false;
    Vector navalObjects;
    javax.swing.Timer simTimer;
    boolean simIsOver=false;

    public SimInterface() {
	navalObjects = new Vector();
	/** Create and set the simulation timer */
        int fps = 20;
        // Set how many milliseconds between frames from fps
        int delay = (fps > 0) ? (1000 / fps) : 100;
	// Set up a timer that calls this object's action handler
        simTimer = new javax.swing.Timer(delay, this);
        simTimer.setInitialDelay(0);
        simTimer.setCoalesce(true);
    }

    /** Trap actionEvents on SimInterface object (from simTimer) for updates */
    public void actionPerformed(ActionEvent e) {
        simTime += 0.1;  // 0.1 second, because fps above was 10
	Enumeration navObjEnum = navalObjects.elements();
	while (navObjEnum.hasMoreElements()) {
	    NavalObject navObj = (NavalObject)navObjEnum.nextElement();
	    navObj.updatePosition(simTime);
	    // now for each buoy check if each sub is within range
	    if(navObj instanceof Sonobuoy) {
		Sonobuoy buoy = (Sonobuoy)navObj;

		// loop over navalObjects again to get subs
		Enumeration navObjEnum2 = navalObjects.elements();
		while (navObjEnum2.hasMoreElements()) {
		    NavalObject navObj2=(NavalObject)navObjEnum2.nextElement();
		    if(navObj2 instanceof Submarine) {
			Submarine sub = (Submarine)navObj2;
			// check range between current buoy & current sub,
			// if in range the buoy gets sub object to check other
			// params to see if it senses sub (sub's TS, etc)
			double latDiff = buoy.getLat()-sub.getLat();
			double lonDiff = buoy.getLon()-sub.getLon();
			double range = Math.sqrt( latDiff*latDiff +
						  lonDiff*lonDiff );
			if(range < buoy.getSenseRange()) {
			    buoy.mightSense(sub);
			}
		    }
		}
	    }
	}
	if(simTime>=maxSimTime) {
	    if(simTimer.isRunning()) simTimer.stop();
	    simIsOver=true;
	}
    }

    public double getSimTime(){
	return simTime;
    }

    public Vector getNavalObjects(){
	return navalObjects;
    }

    public void addBuoy( double lon, double lat, String buoyModel ) {
	Sonobuoy newBuoy = new Sonobuoy();
	buoyNum++;
	newBuoy.setIDNum(buoyNum);
	newBuoy.setInitLon(lon);
	newBuoy.setInitLat(lat);
	newBuoy.setModel(buoyModel);
	navalObjects.add(newBuoy);
    }

    public void setSubInitValues( String subName, double lon, double lat ){
	if( mySubReady==false ) {
	    Submarine mySub = new Submarine();
	    mySub.setName(subName);
	    mySub.setInitLon(lon);
	    mySub.setInitLat(lat);
	    navalObjects.add(mySub);
	    mySubReady=true;
	} else {
	    // call an exception because this is a coding issue - don't want
	    // to create more than one local sub.
	}
    }

    public void setMaxSimTime( double maxSimTime ){
	this.maxSimTime=maxSimTime;
    }

    public void scuttleBuoys(){
	// loop thru navalObjects vector via enumeration, if current obj
	// is of type "buoy", remove from vector
	buoyNum=0;
	Vector buoysToScuttle = new Vector();
	Enumeration navalObjEnum = navalObjects.elements();
	while (navalObjEnum.hasMoreElements()) {
	    NavalObject navObj = (NavalObject)navalObjEnum.nextElement();
	    if(navObj instanceof Sonobuoy) buoysToScuttle.add(navObj);
	}
	navalObjects.removeAll(buoysToScuttle);

    }

    public void register(){
	simRegistered=true;
    }

    public void start(){
	if( mySubReady && simRegistered ) {
	    simTimer.start();
	    // send signal back to gui somehow?
	} else {
	    // send signal back to gui somehow?
	}
    }

    public void stop(){
	simTimer.stop();
	simIsOver=true;
    }

    public boolean isOver() {
	return simIsOver;
    }

    public void reset() {
	buoyNum=0;
	simTime=0;
	mySubReady=false;
	simRegistered=false;
	navalObjects.clear();
	simIsOver=false;
    }

}
