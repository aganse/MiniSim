/** GUIFrame.java */

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;  // for enumeration and vector

/** <B>GUIFrame</B> - Java-1.2/Swing based GUI for simulator MiniSim.
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
public class GUIFrame implements ActionListener {

    double pi = java.lang.Math.PI;
    boolean frozen = true;
    boolean simRegistered = false;
    boolean simStarted = false;
    String buoyModel = new String("LOFAR");
    JSlider st;
    JTextField fednameTxt;
    JButton startPauseSimButton;
    MapPane mapPane;
    double initLat=20, initLon=20;
    JTextArea comMsgTxt;
    //JScrollPane comMsgScrl;
    JTextArea diagTxt;
    JScrollPane diagScrl;
    javax.swing.Timer animTimer;
    int frameNumber = -1;
    SimInterface sim;
    private SimRegisThread regThread = null;
    private SimStartThread startThread = null;


    public GUIFrame(Container container, Image seaflrImage,
		    Image subImage, Image buoyImage, SimInterface newSim){

	/** Connect the handle to the simulation interface (note that's not
	 *  a java interface) **/
	this.sim = newSim;

	/** Hardwired maxSimTime here, but could be made user-settable */
	int maxSimTime = 120;
	this.sim.setMaxSimTime(maxSimTime);

	/** Create and set the animation timer */
        int fps = 10;
        // Set how many milliseconds between frames
        int delay = (fps > 0) ? (1000 / fps) : 100;
	// Set up a timer that calls this object's action handler
        animTimer = new javax.swing.Timer(delay, this);
        animTimer.setInitialDelay(0);
        animTimer.setCoalesce(true);
	
        /** Create the simTime panel: */
        JPanel simTimePane = new JPanel();
        st = new JSlider(JSlider.HORIZONTAL, 0, maxSimTime, 0);
        st.setValue(0);
        st.setEnabled(false);
        st.setPaintTicks(true);
        st.setPaintLabels(true);
        st.setMajorTickSpacing(maxSimTime/4);
        st.setMinorTickSpacing(maxSimTime/8);
        simTimePane.add(st, BorderLayout.EAST);
        simTimePane.setBorder(new 
          TitledBorder(BorderFactory.createEtchedBorder(), "Simulation Time"));

        /** Create the subName panel: */
        JPanel subNamePane = new JPanel();
        fednameTxt = new JTextField("USS Foo", 16);
        subNamePane.add(fednameTxt);
        subNamePane.setBorder(new 
          TitledBorder(BorderFactory.createEtchedBorder(), "Submarine Name"));

        /** Create the buttons panel: */
        JPanel buttonsPane = new JPanel();
        buttonsPane.setLayout(new GridLayout(2,2));
        JButton regisFedButton = new JButton("Register Sim");
        regisFedButton.addActionListener(new RegisBtnLsnr());
        buttonsPane.add(regisFedButton);
        JButton scuttleButton = new JButton("Scuttle");
        scuttleButton.addActionListener(new ScuttleBtnLsnr());
        buttonsPane.add(scuttleButton);
        startPauseSimButton = new JButton("Start Sim");
        startPauseSimButton.addActionListener(new StartPauseBtnLsnr());
        buttonsPane.add(startPauseSimButton);
        JButton exitSimButton = new JButton("Exit");
        exitSimButton.addActionListener(new ExitBtnLsnr());
        buttonsPane.add(exitSimButton);
        buttonsPane.setBorder(new 
          TitledBorder(BorderFactory.createEtchedBorder(),
            "Simulation Control"));

        /** Create the sonobuoy-type panel: */
        JPanel buoyPane = new JPanel();
        buoyPane.setLayout(new GridLayout(2,2));
        buoyPane.setBorder(new 
          TitledBorder(BorderFactory.createEtchedBorder(), "Sonobuoy Type"));
        ButtonGroup buoyGroup = new ButtonGroup();
        JRadioButton LOFARbtn = new JRadioButton("LOFAR");
        LOFARbtn.setSelected(true);
        JRadioButton DICASSbtn = new JRadioButton("DICASS");
        JRadioButton DIFARbtn = new JRadioButton("DIFAR");
        JRadioButton VLADbtn = new JRadioButton("VLAD");
	LOFARbtn.addActionListener(new LOFARlsnr());
	DICASSbtn.addActionListener(new DICASSlsnr());
	DIFARbtn.addActionListener(new DIFARlsnr());
	VLADbtn.addActionListener(new VLADlsnr());
        buoyGroup.add(LOFARbtn);
        buoyGroup.add(DICASSbtn);
        buoyGroup.add(DIFARbtn);
        buoyGroup.add(VLADbtn);
        buoyPane.add(LOFARbtn);
        buoyPane.add(DICASSbtn);
        buoyPane.add(DIFARbtn);
        buoyPane.add(VLADbtn);

        /** Create the leftControl panel: */
        JPanel leftConPane = new JPanel();
        leftConPane.setLayout(new GridLayout(2,1));
        leftConPane.add(subNamePane, BorderLayout.NORTH);
        leftConPane.add(buoyPane, BorderLayout.SOUTH);

        /** Create the rightControl panel: */
        JPanel rightConPane = new JPanel();
        rightConPane.setLayout(new GridLayout(2,1));
        rightConPane.add(buttonsPane, BorderLayout.NORTH);
        rightConPane.add(simTimePane, BorderLayout.SOUTH);

        /** Create the more general controlsPanel and add leftControl-panel
         *  and rightControl-panel to it: */
        JPanel controlsPane = new JPanel();
        controlsPane.setLayout(new GridLayout(1,2));
        controlsPane.add(leftConPane, BorderLayout.WEST);
        controlsPane.add(rightConPane, BorderLayout.EAST);

        /** Create the message panel: */
        JPanel msgPane = new JPanel();
        msgPane.setBorder(new 
          TitledBorder(BorderFactory.createEtchedBorder(), "Comms Messages"));
        comMsgTxt = new JTextArea("",1,38);
        comMsgTxt.setEditable(false);
        //comMsgScrl = new JScrollPane(comMsgTxt);
        //msgPane.add(comMsgScrl);
        msgPane.add(comMsgTxt);

        /** Create the status panel: */
        JPanel statusPane = new JPanel();
        statusPane.setBorder(new
 	       	     TitledBorder(BorderFactory.createEtchedBorder(),
	             "Status/Diagnostic Messages"));
        diagTxt = new JTextArea("Status/Diagnostic messages will display here:",
				3, 38);
        diagTxt.setEditable(false);
        diagScrl = new JScrollPane(diagTxt);
        statusPane.add(diagScrl);

        /** Create the directions panel: */
        JPanel dirsPane = new JPanel();
        dirsPane.setBorder(new 
        TitledBorder(BorderFactory.createEtchedBorder(), "Directions for Use"));
        dirsPane.setLayout(new GridLayout(6,1));
        dirsPane.add(new 
           JLabel(" 1.) Enter your own name in Submarine Name field."));
        dirsPane.add(new 
           JLabel(" 2.) Click Register Sim button to register simulation."));
        dirsPane.add(new 
           JLabel(" 3.) Click Start button to start simulation."));
        dirsPane.add(new 
           JLabel(" 4.) Choose Sonobuoy Types and click on map to drop them in."));
        dirsPane.add(new 
           JLabel(" 5.) Click Scuttle button to clear sonobuoys."));
        dirsPane.add(new 
           JLabel(" 6.) To exit click Exit button or the window's X button."));

        /** Create the map panel: */
        mapPane = new MapPane(seaflrImage, subImage, buoyImage);
        JScrollPane mapScrollPane = new JScrollPane(mapPane);

        /** Create the bottom panel: */
        JPanel bottomPane = new JPanel();
        bottomPane.setLayout(new BorderLayout());
        bottomPane.add(msgPane, BorderLayout.NORTH);
        //bottomPane.add(statusPane, BorderLayout.SOUTH);
        bottomPane.add(dirsPane, BorderLayout.SOUTH);

        /** Place all the panels onto the GUIFrame: */
        container.add(controlsPane, BorderLayout.NORTH);
        container.add(mapScrollPane, BorderLayout.CENTER);
	container.add(bottomPane, BorderLayout.SOUTH);
    }


    /** Start the animation updates in the mapPane and simTime slider */
    public synchronized void startAnimation() {
        if (!frozen) { 
            // Start animating!
            if (!animTimer.isRunning()) {
                animTimer.start();
            }
        }
    }


    /** Stop the animation updates in the mapPane and simTime slider */
    public synchronized void stopAnimation() {
        // Stop the animating thread.
        if (animTimer.isRunning()) {
            animTimer.stop();
        }
    }


    /** Trap actionEvents on GUIFrame object (fired by Timer) */
    public void actionPerformed(ActionEvent e) {
        //Advance animation frame
        frameNumber++;

        // Update display
        mapPane.repaint();
    }

    /** Appends a note to the Diagnostic Output textbox on the GUI */
    public void diagOut(String diagString) {
        diagTxt.append("\n");
        diagTxt.append(diagString);
	diagScrl.getVerticalScrollBar().setValue(
	    diagScrl.getVerticalScrollBar().getMaximum() );
    	}

    /** Appends a 'comms' message to the Comms Msgs textbox on the GUI */
    public void displayComMsg(String comString) {
        comMsgTxt.setText("lastest msg: " + comString);
        //comMsgTxt.append(comString + "\n");
	//comMsgScrl.getVerticalScrollBar().setValue(
	//    comMsgScrl.getVerticalScrollBar().getMaximum() );
    	}

    /** This is the map area of the applet where the action takes place.
     *  Each repaint of this panel is one frame of the map "animation". */
    class MapPane extends JPanel {

        Image subImg, buoyImg, seafloorImg;
        LatLonGrid viewgrid = new LatLonGrid(0,40,0,40);
    
        public MapPane(Image seafloorImg, Image subImg, Image buoyImg) {
	    this.seafloorImg = seafloorImg;
	    this.subImg = subImg;
	    this.buoyImg = buoyImg;
	    setBorder(BorderFactory.createEtchedBorder());
	    setCursor( Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR) );
	    addMouseListener(new MouseAdapter() {
	    	public void mousePressed(MouseEvent e) {
	    	    /** add a buoy where mouse was clicked */
	    	    e.consume();
	    	    if (simRegistered && simStarted) {
		    sim.addBuoy(
		      ( ((double)(e.getX())/(double)(getWidth())) *
		        (double)(viewgrid.maxlon-viewgrid.minlon) +
		        (double)(viewgrid.minlon) ),
		      ( ((double)(e.getY())/(double)(getHeight())) *
		        (double)(viewgrid.maxlat-viewgrid.minlat) +
		        (double)(viewgrid.minlat) ),
		      buoyModel);
	    	    }
	    	}
	    });
        }
   
        /** Draw the current frame of map. */
        public void paintComponent(Graphics g1) {

	    Graphics2D g = (Graphics2D)g1;

            super.setBackground(Color.darkGray);
            super.paintComponent(g);  //paint any space not covered
                                      //by the seafloor image
            int mapPaneWidth = getWidth();
            int mapPaneHeight = getHeight();
            int seaflrImgWidth, seaflrImgHeight;
            int subImgWidth, subImgHeight;
            int buoyImgWidth, buoyImgHeight;
            int x, y;
            double simTime;

            /** Get sim data from sim execution */
            simTime = (sim==null)?0.:sim.getSimTime();
	    Vector navalObjectsList = sim.getNavalObjects();
    
            /** Get image params */
            seaflrImgWidth = seafloorImg.getWidth(this);
            seaflrImgHeight = seafloorImg.getHeight(this);
            subImgWidth = subImg.getWidth(this);
            subImgHeight = subImg.getHeight(this);
	    buoyImgWidth = buoyImg.getWidth(this);
	    buoyImgHeight = buoyImg.getHeight(this);

            /** Draw seafloor image on map panel */
            if ((seaflrImgWidth > 0) && (seaflrImgHeight > 0)) {
                g.drawImage(seafloorImg, 
                            (mapPaneWidth - seaflrImgWidth)/2,
                            (mapPaneHeight - seaflrImgHeight)/2, this);
	    }


            /** Draw lat lines every 10 degrees */
            for(y=(int)viewgrid.minlat; y<=(int)viewgrid.maxlat; y++) {
              if( y%10==0 ) {
                g.drawLine(
                  (int)( ((double)mapPaneWidth-(double)seaflrImgWidth)/2 ),
                  (int)( (double)y / (viewgrid.maxlat-viewgrid.minlat) *
                    (double)seaflrImgHeight+
                    ((double)mapPaneHeight-(double)seaflrImgHeight)/2 ),
                  (int)( (double)seaflrImgWidth +
                    ((double)mapPaneWidth-(double)seaflrImgWidth)/2 ),
                  (int)( (double)y / (viewgrid.maxlat-viewgrid.minlat) *
                    (double)seaflrImgHeight+
                    ((double)mapPaneHeight-(double)seaflrImgHeight)/2 )
                );
                g.setColor(Color.black);
                g.drawString(String.valueOf(y), 
                  (int)( ((double)mapPaneWidth-(double)seaflrImgWidth)/2 ) + 5,
                  (int)( (double)y / (viewgrid.maxlat-viewgrid.minlat) *
                    (double)seaflrImgHeight+
                    ((double)mapPaneHeight-(double)seaflrImgHeight)/2 )
                );
              }
            }
    
            /** Draw lon lines every 10 degrees */
            for(x=(int)viewgrid.minlon; x<=(int)viewgrid.maxlon; x++) {
              if( x%10==0 ) {
                g.drawLine(
                  (int)( (double)x / (viewgrid.maxlon-viewgrid.minlon) *
                    (double)seaflrImgWidth+
                    ((double)mapPaneWidth-(double)seaflrImgWidth)/2 ),
                  (int)( ((double)mapPaneHeight-(double)seaflrImgHeight)/2 ),
                  (int)( (double)x / (viewgrid.maxlon-viewgrid.minlon) *
                    (double)seaflrImgWidth+
                    ((double)mapPaneWidth-(double)seaflrImgWidth)/2 ),
                  (int)( (double)seaflrImgHeight +
                    ((double)mapPaneHeight-(double)seaflrImgHeight)/2 )
                );
                g.setColor(Color.black);
                g.drawString(String.valueOf(x), 
                  (int)( (double)x / (viewgrid.maxlon-viewgrid.minlon) *
                    (double)seaflrImgWidth+
                    ((double)mapPaneWidth-(double)seaflrImgWidth)/2 ),
                  (int)( ((double)mapPaneHeight-(double)seaflrImgHeight)/2 )+10
                );
              }
	    }

            /** Draw sub(s) and sonobuoy(s) on map panel */
	    Enumeration navalObjEnum = navalObjectsList.elements();
	    while (navalObjEnum.hasMoreElements()) {
		NavalObject navObj = (NavalObject)navalObjEnum.nextElement();
		double lat = navObj.getLat();
		double lon = navObj.getLon();
		String msg = navObj.getLatestComm();
		if(msg!=null) displayComMsg(msg);

		if(navObj instanceof Submarine) {
		    Submarine sub = (Submarine)navObj;
		    double subx =
			(int)(lon/(viewgrid.maxlon-viewgrid.minlon)*
			      (double)seaflrImgWidth +
			      ((double)mapPaneWidth-
			       (double)seaflrImgWidth)/2 -
			      (double)subImgWidth/2 );
		    double suby =
			(int)(lat/(viewgrid.maxlat-viewgrid.minlat)*
			      (double)seaflrImgHeight+
			      ((double)mapPaneHeight-
			       (double)seaflrImgHeight)/2 -
			      (double)subImgHeight/2 );
		    AffineTransform at = new AffineTransform();
		    at.translate(subx,suby);
		    at.rotate( sub.getHeading()*2*pi/360,
			       (double)subImgWidth/2, (double)subImgHeight/2 );
		    g.drawImage(subImg, at, this);


		    g.setColor(Color.white);
		    g.drawString(sub.getName(), 
				 (int)(lon/(viewgrid.maxlon-viewgrid.minlon)*
				       (double)seaflrImgWidth +
				       ((double)mapPaneWidth-
					(double)seaflrImgWidth)/2 -
				       (double)subImgWidth/2 )+23,
				 (int)(lat/(viewgrid.maxlat-viewgrid.minlat)*
				       (double)seaflrImgHeight+
				       ((double)mapPaneHeight-
					(double)seaflrImgHeight)/2 -
				       (double)subImgHeight/2 )+18);
		}
		if(navObj instanceof Sonobuoy) {
		    Sonobuoy buoy = (Sonobuoy)navObj;
		    g.drawImage(buoyImg, 
				(int)(lon/(viewgrid.maxlon-viewgrid.minlon)*
				      (double)seaflrImgWidth +
				      ((double)mapPaneWidth-
				       (double)seaflrImgWidth)/2 -
				      (double)buoyImgWidth/2 ),
				(int)(lat/(viewgrid.maxlat-viewgrid.minlat)*
				      (double)seaflrImgHeight+
				      ((double)mapPaneHeight-
				       (double)seaflrImgHeight)/2 -
				      (double)buoyImgHeight/2 ),
				this);
		    g.setColor(Color.white);
		    g.drawString(Integer.toString(buoy.getIDNum()) +
				 " ("+buoy.getModel()+")",
				 (int)(lon/(viewgrid.maxlon-viewgrid.minlon)*
				       (double)seaflrImgWidth +
				       ((double)mapPaneWidth-
					(double)seaflrImgWidth)/2 -
				       (double)buoyImgWidth/2 )+5,
				 (int)(lat/(viewgrid.maxlat-viewgrid.minlat)*
				       (double)seaflrImgHeight+
				       ((double)mapPaneHeight-
					(double)seaflrImgHeight)/2 -
				       (double)buoyImgHeight/2 ));
				       }

	    }

            /** Update simTime slider position. */
            st.setValue((int)simTime);

	    /** Check for "simOver" - if over then display & stop animation */
	    if(sim.isOver()) {
		animTimer.stop();
		g.setColor(Color.red);
		g.drawString("SIMULATION OVER",
		    (int)(0.39*(double)seaflrImgWidth +
			  ((double)mapPaneWidth-(double)seaflrImgWidth)/2),
		    (int)(0.39*(double)seaflrImgHeight+
			  ((double)mapPaneHeight-(double)seaflrImgHeight)/2) );
		g.setColor(Color.white);
	    }

        }


        /** Contains map panel lat-lon boundaries which can be changed */
        public class LatLonGrid {
            public double minlat;
            public double maxlat;
            public double minlon;
            public double maxlon;

            LatLonGrid (double lon1, double lon2, double lat1, double lat2) {
                minlat=lat1;
                maxlat=lat2;
                minlon=lon1;
                maxlon=lon2;
                if(minlat>maxlat || minlon>maxlon) {
                  System.out.println(
                  "MiniSim: LatLonGrid: error: screwy entry in grid bounds\n");
		  System.exit(1);
               }
            }
	}
    }


    /** Button listener for LOFAR radio button */
    class LOFARlsnr implements ActionListener {
      public void actionPerformed(ActionEvent e) {
	  buoyModel="LOFAR";
      }
    }


    /** Button listener for DIFAR radio button */
    class DIFARlsnr implements ActionListener {
      public void actionPerformed(ActionEvent e) {
	  buoyModel="DIFAR";
      }
    }


    /** Button listener for DICASS radio button */
    class DICASSlsnr implements ActionListener {
      public void actionPerformed(ActionEvent e) {
	  buoyModel="DICASS";
      }
    }


    /** Button listener for VLAD radio button */
    class VLADlsnr implements ActionListener {
      public void actionPerformed(ActionEvent e) {
	  buoyModel="VLAD";
      }
    }


    /** Button listener for regis button */
    class RegisBtnLsnr implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    if (!simRegistered && !simStarted) {
		displayComMsg("Sim registered with user " +
                   fednameTxt.getText() + ".");
		diagOut("registering sim...");
                fednameTxt.setEnabled(false);
		sim.setSubInitValues(fednameTxt.getText(),initLon,initLat);
		simRegistered=true;
		if(regThread==null) regThread = new SimRegisThread();
		mapPane.repaint();
	    }
	}
    }
    

    /** Button listener for start/pause button */
    class StartPauseBtnLsnr implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    if (frozen) {
		frozen = false;
		if (simRegistered && !simStarted) { // starting for first time
		    diagOut("starting sim");
		    displayComMsg("Sim started.");
		    startPauseSimButton.setText("Pause");		    
		    simStarted=true;
		    if(startThread==null) startThread = new SimStartThread();
		    startAnimation();
		} else if (simRegistered && simStarted) { // return from pause
		    displayComMsg("Resuming sim animation.");
		    startPauseSimButton.setText("Pause");
		    startAnimation();
		} else {
		    displayComMsg("Must first register sim.");
		}
	    } else { // pause animation
		frozen = true;
		displayComMsg("Sim animation paused (sim itself still going)");
		startPauseSimButton.setText("Unpause");
		stopAnimation();
	    }
	}
    } 


    /** Button listener for scuttle button */
    class ScuttleBtnLsnr implements ActionListener {
      public void actionPerformed(ActionEvent e) {
        displayComMsg("Sonobuoys scuttled!");
	diagOut("scuttling buoys");
	if(sim!=null) sim.scuttleBuoys();
      }
    }


    /** Button listener for exit button */
    class ExitBtnLsnr implements ActionListener {
      public void actionPerformed(ActionEvent e) {
	  stopAnimation();
          displayComMsg("Closing simulation.");
	  //destroy();
	  System.exit(0);
      }
    }


    /** Thread-handling class for registering sim without freezing up the UI */
    private class SimRegisThread extends Thread {
       SimRegisThread() { this.start(); }
        public void run() {
            sim.register();
        }
    }

    /** Thread-handling class for starting sim without freezing up the UI */
    private class SimStartThread extends Thread {
        SimStartThread() { this.start(); }
        public void run() {
            sim.start();
        }
    }

}
