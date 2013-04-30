/**
 * Copyright 2012 John Lawson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.jwlawson.hyperbolic.client.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import uk.co.jwlawson.hyperbolic.client.framework.Drawable;
import uk.co.jwlawson.hyperbolic.client.geometry.Line;
import uk.co.jwlawson.hyperbolic.client.geometry.LineFactory;
import uk.co.jwlawson.hyperbolic.client.geometry.Point;

import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.GestureStartEvent;
import com.google.gwt.event.dom.client.GestureStartHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;

/**
 * @author John
 * 
 */
public class Focal extends SquareCanvasHolder implements PointHandler {

	private static final Logger log = Logger.getLogger(Focal.class.getName());

	private List<Point> mPointList;
	private List<Line> mLineList;

	// These really DO get used. Don't delete.
	@SuppressWarnings("unused")
	private int mouseX, mouseY;

	private Point mOrigin;

	private LineFactory mFactory;

	public Focal() {
		mLineList = new ArrayList<Line>();
		mPointList = new ArrayList<Point>();
	}

	public void setLineFactory(LineFactory factory) {
		mFactory = factory;
	}

	@Override
	public void clear() {
		mLineList.clear();
		mPointList.clear();
		doUpdate();
	}

	@Override
	public void addInitialPoint(Point p) {
		mOrigin = p;
		mPointList.add(p);
		drawDrawables(p);
	}

	@Override
	public void addPoint(Point next) {
		next.scale(width / 8.5);
		log.finer(mOrigin + " " + next);
		Line line = mFactory.getPerpendicularBisector(mOrigin, next);
		mLineList.add(line);
		// next.scale(width / 2);
		mPointList.add(next);

		drawDrawables(next, line);
	}

	@Override
	public void pointsAdded() {
		log.fine("Compute done!");
		doUpdate();
	}

	@Override
	public void doUpdate() {

		clearCanvas();

		drawDrawables((Drawable[]) mLineList.toArray());
		drawDrawables((Drawable[]) mPointList.toArray());

	}

	@Override
	public void initHandlers() {
		canvas.addMouseMoveHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				mouseX = event.getRelativeX(canvas.getElement());
				mouseY = event.getRelativeY(canvas.getElement());
			}
		});

		canvas.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				mouseX = -200;
				mouseY = -200;
			}
		});

		canvas.addTouchMoveHandler(new TouchMoveHandler() {
			@Override
			public void onTouchMove(TouchMoveEvent event) {
				event.preventDefault();
				if (event.getTouches().length() > 0) {
					Touch touch = event.getTouches().get(0);
					mouseX = touch.getRelativeX(canvas.getElement());
					mouseY = touch.getRelativeY(canvas.getElement());
				}
				event.preventDefault();
			}
		});

		canvas.addTouchEndHandler(new TouchEndHandler() {
			@Override
			public void onTouchEnd(TouchEndEvent event) {
				event.preventDefault();
				mouseX = -200;
				mouseY = -200;
			}
		});

		canvas.addGestureStartHandler(new GestureStartHandler() {
			@Override
			public void onGestureStart(GestureStartEvent event) {
				event.preventDefault();
			}
		});
	}

}
