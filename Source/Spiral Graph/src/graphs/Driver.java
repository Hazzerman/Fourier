package graphs;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import org.jfree.chart.plot.XYPlot;

public class Driver {
	
	private static Window window = new Window();
	private static int bigVal;
	private static XYPlot plot;
	//private

	public static void main(String[] args) {
		CreateWindow();
		javax.swing.Timer timer1 = new javax.swing.Timer(15, timerFrequencyChart);
		javax.swing.Timer timer2 = new javax.swing.Timer(15, timerFourierChart);
       
		timer1.setRepeats(true);
        timer2.setRepeats(true);
        timer1.start();
        timer2.start();
	}
	
	private static void CreateWindow () {
		JFrame frame = new JFrame();
		frame.setTitle("Fourier Transform");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		window.instantiatePanels();
		window.instantiateChart();
		window.instantiateGUIElements();
		window.addGUIElements();
		
		plot = (XYPlot) Window.chartFourier.getPlot();
		
		frame.getContentPane().add(window);
		frame.pack();
		frame.setVisible(true);
	}
	
	public static float clamp(float val, float min, float max) {
	    return Math.max(min, Math.min(max, val));
	}
	
	public static Color hueToRGB (float hue) {
		double huePrime = (float)(hue/600);
		float mod = (float) (huePrime % 2);
		float x = (1 - Math.abs(mod - 1));
		Color color;
		if (huePrime >= 5) {
			color = new Color(255, (int)(x * 255), 0);
		} else if (huePrime >= 4) {
			color = new Color((int)(x * 255), 255, 0);
		} else if (huePrime >= 3) {
			color = new Color(0, 255, (int)(x * 255));
		} else if (huePrime >= 2) {
			color = new Color(0, (int)(x * 255), 255);
		} else if (huePrime >= 1) {
			color = new Color((int)(x * 255), 0, 255);
		} else {
			color = new Color(255, 0, (int)(x * 255));
		}
		return color;
	}
	
	public static ActionListener timerFrequencyChart = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			double val = Window.sliderRange.getValue();
			double step = (val*2)/100000;
			Window.UpdateFrequencyChart((double)(0), (double)(val/100), step);
			plot.getRenderer().setSeriesPaint(0, hueToRGB(Window.sliderColour.getValue()));
			
		}
	};
	public static ActionListener timerFourierChart = new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			if (!Window.updating) {
				double val = Window.sliderRange.getValue();
				double freq = Window.sliderWrapness.getValue();
				if (freq != bigVal) {
					bigVal = (int)freq;
					Window.sliderFine.setValue(0);
				}
				double fine = Window.sliderFine.getValue();
				Window.UpdateFourierChart((double)(val/100), ((double)(freq/1000)+(double)(fine/200000)));
			}
		}
	};
}