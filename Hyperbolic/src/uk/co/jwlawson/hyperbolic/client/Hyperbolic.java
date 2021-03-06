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
package uk.co.jwlawson.hyperbolic.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import uk.co.jwlawson.hyperbolic.client.geometry.hyperbolic.HypLineFactory;
import uk.co.jwlawson.hyperbolic.client.ui.Focal;
import uk.co.jwlawson.hyperbolic.client.ui.SizeChangeListener;
import uk.co.jwlawson.hyperbolic.client.ui.Tiling;
import uk.co.jwlawson.hyperbolic.client.widget.Slider;
import uk.co.jwlawson.hyperbolic.client.widget.SliderEvent;
import uk.co.jwlawson.hyperbolic.client.widget.SliderListener;

import java.util.logging.Logger;

/**
 * @author John Lawson
 */
public class Hyperbolic implements EntryPoint, SliderListener, SizeChangeListener {

	private static final String TILING_MOUNT_ID = "tilingcanvas";
	private static final String FOCAL_MOUNT_ID = "focalcanvas";
	private static final String SLIDER_ID = "options";
	private static final String SLIDER_VALUE_STYLE = "slider-values";
	private static final String Y_LABEL = "y = ";
	private static final String X_LABEL = "x = ";

	private static final Logger log = Logger.getLogger("Hyperbolic");

	private Tiling mTiling;
	private Focal mFocal;

	private Slider ySlider;
	private Label yValue;

	private Slider xSlider;
	private Label xValue;

	private QuadHypPointGen pointGen = new QuadHypPointGen();

	private SizeChangeTimer sizeTimer = new SizeChangeTimer();

	@Override
	public void onModuleLoad() {

		yValue = new Label(Y_LABEL + "1");
		yValue.addStyleName(SLIDER_VALUE_STYLE);
		ySlider = new Slider("x", 1, 500, 100);
		ySlider.addListener(this);

//		xValue = new Label(X_LABEL + "1");
//		xValue.addStyleName(SLIDER_VALUE_STYLE);
//		xSlider = new Slider("y", 1, 500, 100);
//		xSlider.addListener(this);

		RootPanel.get(SLIDER_ID).add(yValue);
		RootPanel.get(SLIDER_ID).add(ySlider);

//		RootPanel.get(SLIDER_ID).add(xValue);
//		RootPanel.get(SLIDER_ID).add(xSlider);

		mTiling = new Tiling();
		mFocal = new Focal();
		mFocal.setSizeListener(this);
		mTiling.setSizeListener(this);

		pointGen.addPointHandler(mFocal);
		pointGen.addPointHandler(mTiling);

		mTiling.addToPanel(RootPanel.get(TILING_MOUNT_ID));
		mFocal.addToPanel(RootPanel.get(FOCAL_MOUNT_ID));

		Window.addResizeHandler(new ResizeHandler() {

			Timer resizeTimer = new Timer() {
				@Override
				public void run() {
					doLayoutCalculations();
				}

			};

			@Override
			public void onResize(ResizeEvent event) {
				resizeTimer.schedule(300);
			}
		});
	}

	private void doLayoutCalculations() {
		mTiling.initSize();
		mFocal.initSize();
	}

	@Override
	public void onStart(SliderEvent e) {

	}

	@Override
	public boolean onSlide(SliderEvent e) {
		Slider source = e.getSource();
		pointGen.stop();

		if (ySlider == source) {
			yValue.setText(Y_LABEL + (double) e.getValues()[0] / 100);
			reloadTimer.schedule(500);
			return true;
		}
//		if (xSlider == source) {
//			xValue.setText(X_LABEL + (double) e.getValues()[0] / 100);
//			reloadTimer.schedule(500);
//			return true;
//		}
		return false;
	}

	private Timer reloadTimer = new Timer() {

		@Override
		public void run() {
//			pointGen.setX((double) xSlider.getValueAtIndex(0) / 100);
			pointGen.setY((double) ySlider.getValueAtIndex(0) / 100);
			pointGen.start();
		}
	};

	@Override
	public void onChange(SliderEvent e) {

	}

	@Override
	public void onStop(SliderEvent e) {

	}

	@Override
	public void sizeChanged(float width, float height) {
		pointGen.stop();
		sizeTimer.setHeight(height);
		sizeTimer.setWidth(width);
		sizeTimer.schedule(20);
	}

	/**
	 * Used to delay recalculations after the size is changed, so that it only
	 * happens once no matter how many listeners there are.
	 */
	private class SizeChangeTimer extends Timer {

		private float width, height;

		public void setHeight(float height) {
			this.height = height;
		}

		public void setWidth(float width) {
			this.width = width;
		}

		@Override
		public void run() {
			mFocal.setLineFactory(new HypLineFactory(width / 2));

//			pointGen.setHeight(height);
//			pointGen.setWidth(width);
			pointGen.start();
		}

	};
}