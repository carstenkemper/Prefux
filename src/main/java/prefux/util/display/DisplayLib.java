/*  
 * Copyright (c) 2004-2013 Regents of the University of California.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3.  Neither the name of the University nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * Copyright (c) 2014 Martin Stockhammer
 */
package prefux.util.display;

import java.util.Iterator;

import prefux.Display;
import prefux.data.util.Point2D;
import prefux.data.util.Rectangle2D;
import prefux.visual.VisualItem;

/**
 * Library routines pertaining to a prefux Display.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class DisplayLib {

	private DisplayLib() {
		// don't instantiate
	}

	/**
	 * Get a bounding rectangle of the VisualItems in the input iterator.
	 * 
	 * @param iter
	 *            an iterator of VisualItems
	 * @param margin
	 *            a margin to add on to the bounding rectangle
	 * @param b
	 *            the Rectangle instance in which to store the result
	 * @return the bounding rectangle. This is the same object as the parameter
	 *         <code>b</code>.
	 */
	public static Rectangle2D getBounds(Iterator iter, double margin,
	        Rectangle2D b) {
		b = new Rectangle2D(Double.NaN, Double.NaN, Double.NaN, Double.NaN);
		// TODO: synchronization?
		if (iter.hasNext()) {
			VisualItem item = (VisualItem) iter.next();
			Rectangle2D nb = item.getBounds();
			b = nb;
		}
		while (iter.hasNext()) {
			VisualItem item = (VisualItem) iter.next();
			Rectangle2D nb = item.getBounds();
			double x1 = (nb.getMinX() < b.getMinX() ? nb.getMinX() : b
			        .getMinX());
			double x2 = (nb.getMaxX() > b.getMaxX() ? nb.getMaxX() : b
			        .getMaxX());
			double y1 = (nb.getMinY() < b.getMinY() ? nb.getMinY() : b
			        .getMinY());
			double y2 = (nb.getMaxY() > b.getMaxY() ? nb.getMaxY() : b
			        .getMaxY());
			b = new Rectangle2D(x1, y1, x2 - x1, y2 - y1);
		}
		b = new Rectangle2D(b.getMinX() - margin, b.getMinY() - margin,
		        b.getWidth() + 2 * margin, b.getHeight() + 2 * margin);
		return b;
	}

	/**
	 * Get a bounding rectangle of the VisualItems in the input iterator.
	 * 
	 * @param iter
	 *            an iterator of VisualItems
	 * @param margin
	 *            a margin to add on to the bounding rectangle
	 * @return the bounding rectangle. A new Rectangle2D instance is allocated
	 *         and returned.
	 */
	public static Rectangle2D getBounds(Iterator iter, double margin) {
		Rectangle2D b = new Rectangle2D(0.0, 0.0, 0.0, 0.0);
		return getBounds(iter, margin, b);
	}

	/**
	 * Return the centroid (averaged location) of a group of items.
	 * 
	 * @param iter
	 *            an iterator of VisualItems
	 * @param p
	 *            a Point2D instance in which to store the result
	 * @return the centroid point. This is the same object as the parameter
	 *         <code>p</code>.
	 */
	public static Point2D getCentroid(Iterator iter, Point2D p) {
		double cx = 0, cy = 0;
		int count = 0;

		while (iter.hasNext()) {
			VisualItem item = (VisualItem) iter.next();
			double x = item.getX(), y = item.getY();
			if (!(Double.isInfinite(x) || Double.isNaN(x))
			        && !(Double.isInfinite(y) || Double.isNaN(y))) {
				cx += x;
				cy += y;
				count++;
			}
		}
		if (count > 0) {
			cx /= count;
			cy /= count;
		}
		p = new Point2D(cx, cy);
		return p;
	}

	/**
	 * Return the centroid (averaged location) of a group of items.
	 * 
	 * @param iter
	 *            an iterator of VisualItems
	 * @return the centroid point. A new Point2D instance is allocated and
	 *         returned.
	 */
	public static Point2D getCentroid(Iterator iter) {
		return getCentroid(iter, new Point2D(0.0, 0.0));
	}

	/**
	 * Set the display view such that the given bounds are within view.
	 * 
	 * @param display
	 *            the Display instance
	 * @param bounds
	 *            the bounds that should be visible in the Display view
	 * @param duration
	 *            the duration of an animated transition. A value of zero will
	 *            result in an instantaneous change.
	 */
	public static void fitViewToBounds(Display display, Rectangle2D bounds,
	        long duration) {
		fitViewToBounds(display, bounds, null, duration);
	}

	/**
	 * Set the display view such that the given bounds are within view, subject
	 * to a given center point being maintained.
	 * 
	 * @param display
	 *            the Display instance
	 * @param bounds
	 *            the bounds that should be visible in the Display view
	 * @param center
	 *            the point that should be the center of the Display
	 * @param duration
	 *            the duration of an animated transition. A value of zero will
	 *            result in an instantaneous change.
	 */
	public static void fitViewToBounds(Display display, Rectangle2D bounds,
	        Point2D center, long duration) {
		// init variables
		double w = display.getWidth(), h = display.getHeight();
		double cx = (center == null ? bounds.getCenterX() : center.getX());
		double cy = (center == null ? bounds.getCenterY() : center.getY());

		// compute half-widths of final bounding box around
		// the desired center point
		double wb = Math.max(cx - bounds.getMinX(), bounds.getMaxX() - cx);
		double hb = Math.max(cy - bounds.getMinY(), bounds.getMaxY() - cy);

		// compute scale factor
		// - figure out if z or y dimension takes priority
		// - then balance against the current scale factor
		double scale = Math.min(w / (2 * wb), h / (2 * hb))
		        / display.getScale();

		// animate to new display settings
		if (center == null)
			center = new Point2D(cx, cy);
		display.panToAbs(center);
		display.zoomAbs(center, scale);
	}

} // end of class DisplayLib
