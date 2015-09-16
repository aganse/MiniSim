/** MiniSim.java */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/** <B>MiniSim</B> - Java2/Swing based GUI which creates a sub instance 
 *  in a little distributed sub simulation, and uses one of several kinds of
 *  SimInterface to get/communicate its sim info - the kind included here is
 *  self contained webpage applet demo.
 *  This little app serves two functions:
 *  <UL>
 *    <LI>Proof of concept of quick and easy GUI-based sim interfaces using
 *        Java2, Swing, and various kinds of sim networking interfaces.
 *    <LI>A small networking sim to spew out sonobuoy-drop interactions, which
 *        will be read in by other processes being developed to test them.
 *  </UL>
 * 
 *  <I>Written by 
 *  <A HREF="mailto:aganse@apl.washington.edu">Andy Ganse</A>,
 *  Applied Physics Lab, University of Washington, copyright 1999 by UW.
 *  </I>
 *
 *  <P><P>
 *  Note applet-demo can be viewed in a web-browser if browser has a plug-in
 *  update to make it compatible with Java1.2.  See the 
 *  <A HREF="http://java.sun.com/products/plugin">Sun Java plug-ins</A>
 *  site for details on updating your browser to Java1.2.
 */
public class MiniSim extends JApplet {

    GUIFrame gui;
    static String subImgFile = "sub3.gif";
    static String seaflrImgFile = "water.gif";
    static String buoyImgFile = "buoy1.gif";
    // Note when setting gif files above, there's a trouble using animated
    // gifs (basically, it screws up the animation).  I wanted to use an
    // animated gif for the buoys, but then somehow the jvm automatically
    // called GUIFrame.paintComponent() repeatedly, which I guess might make
    // sense to refresh the gif layers, but it interfered with animation
    // and there was no apparent way to stop it...

    /** Init is only invoked only when MiniSim is run as an applet.
     *  Note similarity to contents of main() for application mode... */
    public void init() {
        //Get the images.
        Image seaflrImage = getImage(getCodeBase(), seaflrImgFile);
        Image subImage = getImage(getCodeBase(), subImgFile);
        Image buoyImage = getImage(getCodeBase(), buoyImgFile);
        createSimulation( getContentPane(), seaflrImage, subImage, buoyImage );
    }

    /** This method, which instantiates the SimInterface and GUIFrame,
     *  replaces the use of a constructor because we must first initialize
     *  its parameters in either init() or main().  **/
    public void createSimulation(Container contentPane, Image seaflrImage,
				 Image subImage, Image buoyImage) {
	SimInterface sim = new SimInterface();
        gui = new GUIFrame( contentPane, seaflrImage, subImage, buoyImage,
			    sim );
    }

    /** Invoked by a browser only. */
    public void start() {
        gui.startAnimation();
    }
    /** Invoked by a browser only. */
    public void stop() {
        gui.stopAnimation();
    }


    /** Main is only invoked only when MiniSim is run as an application.
     *  Note similarity to contents of init() for applet mode... */
    public static void main(String[] args) {
        Image seaflrImage = Toolkit.getDefaultToolkit().getImage(
                                MiniSim.seaflrImgFile);
        Image subImage = Toolkit.getDefaultToolkit().getImage(
                                MiniSim.subImgFile);
        Image buoyImage = Toolkit.getDefaultToolkit().getImage(
                                MiniSim.buoyImgFile);

        JFrame f = new JFrame("MiniSim - graphical ASW sim");
        final MiniSim miniSimApp = new MiniSim();
        miniSimApp.createSimulation( f.getContentPane(), seaflrImage, subImage,
				     buoyImage );

        f.addWindowListener(new WindowAdapter() {
            public void windowIconified(WindowEvent e) {
                miniSimApp.stop();
            }
            public void windowDeiconified(WindowEvent e) {
                miniSimApp.start();
            }
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        f.setSize(new Dimension(505, 690));
        f.setVisible(true);
    }
}


