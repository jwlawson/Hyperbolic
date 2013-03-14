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

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author John
 * 
 */
public class Focal implements CanvasHolder, PointHandler {

	private static final Logger log = Logger.getLogger(Focal.class.getName());

	private static final String upgradeMessage = "Your browser does not support the HTML5 Canvas. Please upgrade your browser to view this demo.";

	private Canvas canvas;

	private List<Point> mPointList;
	private List<Line> mLineList;

	// mouse positions relative to canvas
	private int mouseX, mouseY;

	// canvas size, in px
	private int height = 400;
	private int width = 400;

	private CssColor redrawColor = CssColor.make("rgb(255,255,255)");
	private Context2d context;

	private Point mOrigin;

	private LineFactory mFactory;

	private SizeChangeListener sizeListener;

	public Focal() {
		canvas = Canvas.createIfSupported();
		if (canvas == null) {
			Window.alert(upgradeMessage);
			return;
		}
		context = canvas.getContext2d();
		mLineList = new ArrayList<Line>();
		mPointList = new ArrayList<Point>();
	}

	public void setSizeListener(SizeChangeListener listener) {
		this.sizeListener = listener;
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
		System.out.println(mOrigin + " " + next);
		Line line = mFactory.getPerpendicularBisector(mOrigin, next);
		mLineList.add(line);
		// next.scale(width / 2);
		mPointList.add(next);

		drawDrawables(next, line);
	}

	@Override
	public void pointsAdded() {
		System.out.println("Compute done!");
		doUpdate();
	}

	private void drawDrawables(Drawable... list) {
		context.save();
		context.translate(width, height);
		context.scale(2.0, -2.0);
		for (Drawable d : list) {
			d.draw(context);
		}
		context.restore();
	}

	public void initSize() {
		Widget panel = canvas.getParent();
		width = panel.getOffsetWidth();
		height = width;

		canvas.setWidth(width + "px");
		canvas.setHeight(height + "px");
		canvas.setCoordinateSpaceWidth(2 * width);
		canvas.setCoordinateSpaceHeight(2 * height);

		sizeListener.sizeChanged(width, height);

		doUpdate();
	}

	@Override
	public void doUpdate() {

		clearCanvas();

		context.save();
		context.translate(width, height);
		context.scale(2.0, -2.0);

//		drawBoundingCircle();

		for (Line line : mLineList) {
			line.draw(context);
		}
		for (Point point : mPointList) {
			point.draw(context);
		}
		context.restore();
		System.out.println("Update done");

	}

	private void drawBoundingCircle() {
		context.beginPath();
		context.arc(0, 0, width / 2, 0, 2 * Math.PI);
		context.closePath();
		context.setStrokeStyle("#000000");
		context.setLineWidth(0.5);
		context.stroke();
	}

	private void clearCanvas() {
		context.setFillStyle(redrawColor);
		context.fillRect(0, 0, 2 * width, 2 * height);
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

	@Override
	public void addToPanel(Panel panel) {
		panel.add(canvas);

		initSize();
	}

}
