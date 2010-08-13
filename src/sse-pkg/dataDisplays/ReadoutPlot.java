/*
 * Interactive readout of xvalue, yvalue positions on the plot.
 *
 * Project: OpenSonATA
 * Version: 1.0
 * Author:  Jon Richards (current maintainer)
 *          The OpenSonATA code is the result of many programmers over many
 *          years.
 *          Edward A. Lee was mentioned as the author in old comments
 *          originally in this file.
 *
 * This class displays an FFT waterfall plot of SonATA data.
 *
 * Copyright 2010 The SETI Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * Attribution must be: “Licensed through SETI” in all published
 * uses of the software including analytics based on the software,
 * combined and merged software, papers, articles, books, reports,
 * web pages, etc.
 */

/*
 * Extension of plot that allows interactive readout of plot position.
 *
 * Copyright (c) 1998-2001 The Regents of the University of California.
 * All rights reserved.
 * Permission is hereby granted, without written agreement and without
 * license or royalty fees, to use, copy, modify, and distribute this
 * software and its documentation for any purpose, provided that the above
 * copyright notice and the following two paragraphs appear in all copies
 * of this software.
 *
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY
 * FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES
 * ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
 * THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
 * PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
 * CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS.
 *
 *                                        PT_COPYRIGHT_VERSION_2
 *                                        COPYRIGHTENDKEY
 *@ProposedRating Green (yourname@eecs.berkeley.edu)
 *@AcceptedRating Green (reviewmoderator@eecs.berkeley.edu)
 */

/**
 * @file ReadoutPlot.java
 *
 * Interactive readout of xvalue, yvalue positions on the plot.
 *
 * Project: OpenSonATA
 * <BR>
 * Version: 1.0
 * <BR>
 * Authors:
 * - Jon Richards (current maintainer)
 * - The OpenSonATA code is the result of many programmers over many
 * - Edward A. Lee was mentioned as the author in old comments
 *   originally in this file.
 * years.
 */

package opensonata.dataDisplays;

import ptolemy.plot.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;
import java.util.Vector;
import java.util.Stack;
import java.util.Enumeration;


/**
 * This extension of Plot permits interactive readout of xvalue, yvalue
 * positions on the plot.  
 * Mouse motion over the plot calls the registered ReadoutListeners, giving 
 * them the position information.
 * <p>
 * To be notified when this occurs, create an object that implements the 
 * ReadoutListener interface and add that listener using addReadoutListener().
 */
public class ReadoutPlot extends Plot {


    /** 
     * Constructor.
     */
    public ReadoutPlot() 
    {
        super();
        addMouseMotionListener(new ReadoutMouseListener());
    }

    /** 
     * Add a listener to be informed when the user modifies a data set.
     *
     * @param listener The listener.
     * @see ReadoutListener
     */
    public void addReadoutListener(ReadoutListener listener) 
    {
        if (_readoutListeners == null) 
        {
            _readoutListeners = new Vector();
        } 
        else 
        {
            if (_readoutListeners.contains(listener)) 
            {
                return;
            }
        }
        _readoutListeners.addElement(listener);
    }


    /** 
     * Unregister a readout listener.  If the specified listener has not
     * been previously registered, then do nothing.
     *
     * @param listener The listener to remove from the list of listeners
     * to which edit events are sent.
     */
    public void removeReadoutListener(ReadoutListener listener) 
    {
        if (_readoutListeners == null) 
        {
            return;
        }
        _readoutListeners.removeElement(listener);
    }

    /**
     * Readout the given position.
     * Convert to xvalue, yvalue and send the result
     * to the registered readout listeners.
     *
     * @param x the x pixel position in the graph.
     * @param y the y pixel position in the graph.
     */
    private synchronized void _readout(int x, int y) 
    {

        // constrain to be in range
        if (y > _lry) y = _lry;
        if (y < _uly) y = _uly;
        if (x > _lrx) x = _lrx;
        if (x < _ulx) x = _ulx;

        PlotPoint pt;
        double xPlotValue = _xMin + (x - _ulx)/ _xscale;
        if (xPlotValue < _xMin) xPlotValue = _xMin;
        if (xPlotValue > _xMax) xPlotValue = _xMax;

        double yPlotValue = _yMax - (y - _uly)/ _yscale;
        if (yPlotValue < _yMin) yPlotValue = _yMin;
        if (yPlotValue > _yMax) yPlotValue = _yMax;

        _notifyListeners(xPlotValue, yPlotValue);
    }



    /**
     * Notify all edit listeners that have registered.
     *
     * @param xValue the x pixel position in the graph.
     * @param yValue the y pixel position in the graph.
     */
    private void _notifyListeners(double xValue, double yValue) 
    {
        if (_readoutListeners == null) 
        {
            return;
        } 
        else 
        {
            Enumeration listeners = _readoutListeners.elements();
            while (listeners.hasMoreElements()) 
            {
                ((ReadoutListener)listeners.nextElement()).
                    readoutData(this, xValue, yValue);
            }
        }
    }


    /** Edit listeners. */
    private Vector _readoutListeners = null;


    /**
     * Handle the mouse listener.
     */
    public class ReadoutMouseListener implements MouseMotionListener 
    {
        /**
         * Handle the mouse drag event. This is not used.
         *
         * @param event the MouseEvent instance containing the event 
         * information.
         */
        public void mouseDragged(MouseEvent event) 
        {
        }

        /**
         * handle the mouse moved event.
         *
         * @param event the MouseEvent instance containing the event
         * information.
         */
        public void mouseMoved(MouseEvent event) 
        {
            ReadoutPlot.this._readout(event.getX(), event.getY());
        }
    }
}
