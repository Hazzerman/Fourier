package graphs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.apache.commons.math3.complex.Complex;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Window extends JPanel {
	private static final long serialVersionUID = 1L;
	public static JFrame frame = new JFrame ();
	public JPanel panelMain, panelGraph, panelCircleGraph, panelGUI, panelGUISliderRange, 
			panelGUISliderWrapness, panelGUISliderFine, panelGUISliderColour, panelFourier, 
			panelButtons, panelSave, panelTransform, panelGUIOptions, panelSpacer0, 
			panelSpacer1, panelSpacer2, panelSpacer3, panelSpacer4, panelSpacer5, 
			panelSpacer6, panelSpacer7;
	public static JSlider sliderRange = new JSlider(JSlider.HORIZONTAL, 314, 6284, 628);
	public static JSlider sliderWrapness = new JSlider(JSlider.HORIZONTAL, 0, 478, 31);
	public static JSlider sliderFine = new JSlider(JSlider.HORIZONTAL, -1767, 1767, 0);
	public static JSlider sliderColour = new JSlider(JSlider.VERTICAL, 0, 3600, 3600);
	public static JButton wave, octave, harmony1, harmony2, noise, showTransform, save, animate;
	private static Color defaultColor;
	public static JLabel labelRange, labelWrapness, labelFine;
	public static JFreeChart chart, chartFourier, chartTransform;
	public static ChartPanel chartPanel, chartFourierPanel, chartTransformPanel;
	public static XYDataset dataset, datasetFourier, datasetTransform;
	public static XYSeries seriesFrequency = new XYSeries("XYGraph");
	public static XYSeries seriesFourier = new XYSeries("XYGraph");
	public static XYSeries seriesTransform = new XYSeries("XYGraph");
	private static boolean equation, oct, harm1, harm2, noisequat;
	private static int magnitudeOfWaves;
	private static javax.swing.Timer timer;
	private static double wind, range;
	public static boolean updating = false;
	public static int firstTimeFourier = 0;
	public static SymbolAxis rangeAxis;

	public static void UpdateFrequencyChart (double startVal, double endVal, double step) {
		seriesFrequency.clear();
		for (float i = (float)startVal;i<endVal;i+=step) {
			seriesFrequency.add(i,frequency(i));
		}
	}

	public static void UpdateFourierChart (double val, double freq) {
		seriesFourier.clear();
		if (magnitudeOfWaves > 0) {
			for (double i = (.01);i<val;) {
				Complex valFourier = fourier (i, freq);
				seriesFourier.add(valFourier.getReal(), valFourier.getImaginary());

				double dist = Math.abs(frequency(i));
				double srt = 10 * Math.sqrt((double)sliderWrapness.getValue());
				double incr = magnitudeOfWaves / (dist * srt );
				if (dist < 0.03 * magnitudeOfWaves) {
					i += .02f * magnitudeOfWaves;
				} else if (incr > 0.002 && incr < .015f) {
					i += Math.abs(incr);
				} else {
					i += .015f;
				}
			}
		}
	}
	
	public static void FastFourierChart (float val, float freq) {
		seriesFourier.clear();
		if (magnitudeOfWaves > 0) {
			for (float i = (float) (.01);i<val;) {
				Complex valFourier = fourier (i, freq);
				seriesFourier.add(valFourier.getReal(), valFourier.getImaginary());

				float dist = (float) Math.abs(frequency(i));
				float srt = (float) (10 * Math.sqrt((float)wind));
				float incr = magnitudeOfWaves / (dist * srt );
				if (dist < 0.03 * magnitudeOfWaves) {
					i += .02f * magnitudeOfWaves * 10;
				} else if (incr > 0.002 && incr < .015f) {
					i += Math.abs(incr)  * 10;
				} else {
					i += .015f  * 10;
				}
			}
		}
	}
	
	public static void UpdateTransformChart () {
		for (wind = 0;wind < 956; wind++) {
			System.out.println(wind);
			Window.FastFourierChart((float)(range/100), (float)(wind/1000));
			float ave = 0;
			for (int i = 0; i < seriesFourier.getItemCount(); i += 1) {
				ave += seriesFourier.getX(i).floatValue();
			}
			ave/=(float)seriesFourier.getItemCount();
			seriesTransform.add((double)wind, ave);
		}
		rangeAxis.setRange(0,956);
		//timer.stop();
		updating = false;
		showTransform.setBackground(defaultColor);
	}
	
	public static ActionListener timerTransformT1 = new ActionListener() {
		@SuppressWarnings("deprecation")
		public void actionPerformed(ActionEvent evt) {
			if (wind < 956) {
				if (wind < 477) {
					if (firstTimeFourier == 0) {
						rangeAxis.setRange(0,478);
						firstTimeFourier = 1;
					}
				} else {
					rangeAxis.setRange(0,wind);
					firstTimeFourier = 0;
				}
				Window.FastFourierChart((float)(range/100), (float)(wind/1000));
				float ave = 0;
				for (int i = 0; i < seriesFourier.getItemCount(); i += 1) {
					ave += seriesFourier.getX(i).floatValue();
				}
				ave/=(float)seriesFourier.getItemCount();
				seriesTransform.add((double)wind, ave);
				wind ++;
				timer.restart();
			} else {
				animate.setLabel("Animate Transform");
				updating = false;
				rangeAxis.setRange(0,956);
			}
		}
	};
	
	public static Complex fourier (double x, double freq) {
		//e^(-2 * pi * i * frequency * x)
		Complex e = new Complex(Math.E);

		Complex i = new Complex(0, 1);
		Complex vars = i.multiply(Math.PI);
		vars = vars.multiply(x * freq);
		vars = vars.multiply(-2);
		vars = e.pow(vars);
		return (vars.multiply(frequency(x)));
	}

	public static double frequency (double x) {
		//cosine x
		double val = 0;
		if (noisequat) {
			val += Math.cos(x)+Math.cos(x*99)+Math.cos(x*101)+Math.cos(x*936)+Math.cos(x*973);
		}else {
			if (equation)
				val += Math.cos(x);
			if (oct)
				val += Math.cos(x/2);
			if (harm1)
				val += Math.cos(x*2/3);
			if (harm2)
				val += Math.cos(x/3);
		}

        return (val);
	}

	public void instantiatePanels () {
		//Instantiate
		panelMain = new JPanel();
		panelGraph = new JPanel(new BorderLayout());
		panelCircleGraph = new JPanel(new BorderLayout());
		panelFourier = new JPanel();
		panelTransform = new JPanel();
		panelGUI = new JPanel();
		panelButtons = new JPanel();
		panelSave = new JPanel();
		panelGUIOptions = new JPanel ();
		//spacers
		panelSpacer0 = new JPanel();
		panelSpacer1 = new JPanel();
		panelSpacer2 = new JPanel();
		panelSpacer3 = new JPanel();
		panelSpacer4 = new JPanel();
		panelSpacer5 = new JPanel();
		panelSpacer6 = new JPanel();
		panelSpacer7 = new JPanel();
		//sliders
		panelGUISliderRange = new JPanel(new BorderLayout());
		panelGUISliderWrapness = new JPanel(new BorderLayout());
		panelGUISliderFine = new JPanel(new BorderLayout());
		panelGUISliderColour = new JPanel(new BorderLayout());

		//background
		this.setBackground(Color.BLACK);
		panelMain.setBackground(Color.BLACK);
		panelGraph.setBackground(Color.GRAY);
		panelCircleGraph.setBackground(Color.GRAY);
		panelGUI.setBackground(Color.BLACK);
		panelFourier.setBackground(Color.BLACK);
		panelTransform.setBackground(Color.BLACK);
		panelGUIOptions.setBackground(Color.BLACK);
		//spacers
		panelSpacer0.setBackground(Color.BLACK);
		panelSpacer1.setBackground(Color.BLACK);
		panelSpacer2.setBackground(Color.BLACK);
		panelSpacer3.setBackground(Color.BLACK);
		panelSpacer4.setBackground(Color.BLACK);
		panelSpacer5.setBackground(Color.BLACK);
		panelSpacer6.setBackground(Color.BLACK);
		panelSpacer7.setBackground(Color.BLACK);

		//size
		this.setPreferredSize(new Dimension(1280,800));
		panelMain.setPreferredSize(new Dimension(1270,790));
		panelGraph.setPreferredSize(new Dimension(627,390));
		panelCircleGraph.setPreferredSize(new Dimension(427,385));
		panelGUI.setPreferredSize(new Dimension(627,390));
		panelFourier.setPreferredSize(new Dimension(827,385));
		panelTransform.setPreferredSize(new Dimension(697,380));
		panelGUIOptions.setPreferredSize(new Dimension(120,380));
		//Slider panels
		panelGUISliderRange.setPreferredSize(new Dimension(500, 20));
		panelGUISliderWrapness.setPreferredSize(new Dimension(500, 20));
		panelGUISliderFine.setPreferredSize(new Dimension(500, 20));
		panelGUISliderColour.setPreferredSize(new Dimension(104, 334));
		panelButtons.setPreferredSize(new Dimension(425, 37));
		panelSave.setPreferredSize(new Dimension(104, 36));
		//spacers
		panelSpacer0.setPreferredSize(new Dimension(500, 20));
		panelSpacer1.setPreferredSize(new Dimension(500, 20));
		panelSpacer2.setPreferredSize(new Dimension(500, 20));
		panelSpacer3.setPreferredSize(new Dimension(500, 20));
		panelSpacer4.setPreferredSize(new Dimension(500, 50));
		panelSpacer5.setPreferredSize(new Dimension(500, 20));
		panelSpacer6.setPreferredSize(new Dimension(500, 40));
		panelSpacer7.setPreferredSize(new Dimension(500, 30));
	}

	public void instantiateChart () {
		// Create a simple XY chart
		for (float i = -10; i <= 10; i+=1) {
			seriesFrequency.add(i,frequency(i));
		}
		for (double i = (.01);i<1.1;i+=.01) {
			Complex valFourier = fourier (i, (double)(1.0/1.0));
			seriesFourier.add(valFourier.getReal(), valFourier.getImaginary());
		}
		// Add the series to your data set
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeriesCollection datasetFourier = new XYSeriesCollection();
		XYSeriesCollection datasetTransform = new XYSeriesCollection();
		dataset.addSeries(seriesFrequency);
		datasetFourier.addSeries(seriesFourier);
		datasetTransform.addSeries(seriesTransform);
		// Generate the graph
		chart = ChartFactory.createXYLineChart(
				"Intensity", // Title
				"Time", // x-axis Label
				"", // y-axis Label
				dataset, // Dataset
				PlotOrientation.VERTICAL, // Plot Orientation
				false, // Show Legend
				false, // Use tooltips
				false // Configure chart to generate URLs?
		);
		chartFourier = ChartFactory.createScatterPlot(
				"", // Title
				"", // x-axis Label
				"", // y-axis Label
				datasetFourier, // Dataset
				PlotOrientation.VERTICAL, // Plot Orientation
				false, // Show Legend
				false, // Use tooltips
				false // Configure chart to generate URLs?
		);
		chartTransform = ChartFactory.createXYLineChart(
				"Fourier Transform", // Title
				"", // x-axis Label
				"", // y-axis Label
				datasetTransform, // Dataset
				PlotOrientation.VERTICAL, // Plot Orientation
				false, // Show Legend
				false, // Use tooltips
				false // Configure chart to generate URLs?
		);
		XYPlot plot = (XYPlot) chartFourier.getPlot();
		Object renderer = plot.getRenderer();
		((XYItemRenderer) renderer).setSeriesShape(0, new Ellipse2D.Double(-.5, -.5, 1, 1)); 
		plot.getRenderer().setSeriesPaint(0, new Color (0,255,0));
		NumberAxis domain = (NumberAxis) plot.getDomainAxis();
		domain.setRange(-1.00, 1.00);
		NumberAxis range = (NumberAxis) plot.getRangeAxis();
		range.setRange(-1.00, 1.00);
		
		plot = (XYPlot) chartTransform.getPlot();
		renderer = plot.getRenderer();
		domain = (NumberAxis) plot.getDomainAxis();
		seriesTransform.add(0,0);
		seriesTransform.add(478,0);
		
		String[] grade =  new String[956];
		grade[0] = "0";//"pi 2";
		grade[157] = "\u03C0" +  "/2";
		grade[314] = "\u03C0";
		grade[471] = "3" + "\u03C0" +  "/2";
		grade[628] = "2 \u03C0";
		grade[785] = "5" + "\u03C0" +  "/2";
		grade[942] = "3 \u03C0";

		rangeAxis = new SymbolAxis("", grade);
		rangeAxis.setTickUnit(new NumberTickUnit(157));
		rangeAxis.setRange(0,478);
		plot.setDomainAxis(rangeAxis);
		//add charts to window
		chartPanel = new ChartPanel(chart);
		chartFourierPanel = new ChartPanel(chartFourier);
		chartTransformPanel = new ChartPanel (chartTransform);
		chartPanel.setPreferredSize(new java.awt.Dimension(617, 380));
		chartFourierPanel.setPreferredSize(new java.awt.Dimension(417, 375));
		chartTransformPanel.setPreferredSize(new java.awt.Dimension(690, 375));
		panelGraph.add(chartPanel);
		panelCircleGraph.add(chartFourierPanel);
		panelTransform.add(chartTransformPanel);
	}

	public void instantiateGUIElements () {	
		//instantiate buttons
		wave = new JButton ("Wave");
		octave = new JButton ("Octave");
		harmony1 = new JButton ("Harmony 1");
		harmony2 = new JButton ("Harmony 2");
		noise = new JButton ("Noise");
		showTransform = new JButton ("Show Transform");
		save = new JButton("Save Chart");
		animate = new JButton("Animate Transform");
		defaultColor = save.getBackground();
		//add action listeners
		wave.addActionListener(new buttonWave());
		octave.addActionListener(new buttonOctave());
		harmony1.addActionListener(new buttonHarm1());
		harmony2.addActionListener(new buttonHarm2());
		noise.addActionListener(new buttonNoise());
		save.addActionListener(new buttonSave());
		showTransform.addActionListener(new buttonTransform());
		animate.addActionListener(new buttonAnimateTransform());
		
		//instantiate labels
		labelRange = new JLabel ("Range");
		labelWrapness = new JLabel ("Winding");
		labelFine = new JLabel ("Fine Tuning");
		
		//sliders
		sliderRange.setBackground(Color.BLACK);
		sliderWrapness.setBackground(Color.BLACK);
		sliderFine.setBackground(Color.BLACK);
		sliderColour.setBackground(Color.BLACK);
	}
	
	public void addGUIElements () {
		//add to panel
		add(panelMain);
		panelMain.add(panelGraph);
		panelMain.add(panelGUI);
		panelMain.add(panelCircleGraph);
		panelMain.add(panelFourier);
		
		//labels
		panelSpacer0.add(labelRange);
		panelSpacer1.add(labelWrapness);
		panelSpacer2.add(labelFine);
		
		//add sliders to gui panel
		//Range Slider
		panelGUI.add(panelSpacer0);
		panelGUI.add(panelGUISliderRange);
		panelGUI.add(panelSpacer6);
		panelGUI.add(panelSpacer1);
		//Winding Slider
		panelGUI.add(panelGUISliderWrapness);
		panelGUI.add(panelSpacer2);
		//Fine Tuning Slider
		panelGUI.add(panelGUISliderFine);
		panelGUI.add(panelSpacer3);
		//Colour Slider
		panelGUI.add(panelSpacer4);

		//add Sliders to GUI
		panelGUISliderRange.add(sliderRange);
		panelGUISliderWrapness.add(sliderWrapness);
		panelGUISliderFine.add(sliderFine);		
		
		//Buttons
		panelGUI.add(panelButtons);
		//add buttons
		panelButtons.add(wave);
		panelButtons.add(octave);
		panelButtons.add(harmony1);
		panelButtons.add(harmony2);
		panelButtons.add(noise);
		//panelButtons.add(showTransform);
		//add labelspanelButtons.add(wave);
		
		//Transform
		panelGUI.add(panelSpacer7);
		panelGUI.add(showTransform);
		panelGUI.add(animate);
		
		//Fourier Transform Panel
		panelGUISliderColour.add(sliderColour);
		panelFourier.add(panelGUIOptions);
		panelFourier.add(panelTransform);
		panelGUIOptions.add(panelGUISliderColour);
		panelGUIOptions.add(panelSave);
		panelSave.add(save);
	}

	public void updateRange (int num) {
		if (num > 0) {
			XYPlot plot = (XYPlot) chartFourier.getPlot();
			NumberAxis domain = (NumberAxis) plot.getDomainAxis();
			domain.setRange(-num, num);
			NumberAxis range = (NumberAxis) plot.getRangeAxis();
			range.setRange(-num, num);
			sliderRange.setMaximum((int) (6284*Math.sqrt(num)));
		}
	}
	
	public static void draw() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy MM dd, HH mm ss");
		Date date = new Date();
		String name = "Chart(" + dateFormat.format(date) + ").jpeg";
		//name = "cjart";
		System.out.println(name);
		FileOutputStream leiah = null;
		try {
			leiah = new FileOutputStream (name);
		} catch (FileNotFoundException e1) {
			System.out.println("Failed to create File");
		}
		try {
			ChartUtilities.writeChartAsPNG(leiah, chartFourier, 500, 500);
		} catch (IOException e) {
			System.out.println("Failed to write File");
		}
		System.out.println("done");
	}
	//wave, octave, harmony1, harmony2, noise
	private class buttonWave implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (!updating) {
				if (noisequat)
					magnitudeOfWaves = 0;
				noisequat = false;
				noise.setBackground(defaultColor);
				if (equation) {
					equation = false;
					wave.setBackground(defaultColor);
					magnitudeOfWaves--;
				} else {
					equation = true;
					wave.setBackground(Color.GRAY);
					magnitudeOfWaves++;
				}
				updateRange (magnitudeOfWaves);
			}
		}
	}
	
	private class buttonOctave implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (!updating) {
				if (noisequat)
					magnitudeOfWaves = 0;
				noisequat = false;
				noise.setBackground(defaultColor);
				if (oct) {
					oct = false;
					octave.setBackground(defaultColor);
					magnitudeOfWaves--;
				} else {
					oct = true;	
					octave.setBackground(Color.GRAY);
					magnitudeOfWaves++;
				}
				updateRange (magnitudeOfWaves);
			}
		}
	}
	
	private class buttonHarm1 implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (!updating) {
				if (noisequat)
					magnitudeOfWaves = 0;
				noisequat = false;
				noise.setBackground(defaultColor);
				if (harm1) {
					harm1 = false;
					harmony1.setBackground(defaultColor);
					magnitudeOfWaves--;
				} else {
					harm1 = true;
					harmony1.setBackground(Color.GRAY);
					magnitudeOfWaves++;
				}
				updateRange (magnitudeOfWaves);
			}
		}
	}
	
	private class buttonHarm2 implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (!updating) {
				if (noisequat)
					magnitudeOfWaves = 0;
				noisequat = false;
				noise.setBackground(defaultColor);
				if (harm2) {
					harm2 = false;
					harmony2.setBackground(defaultColor);
					magnitudeOfWaves--;
				} else {
					harm2 = true;
					harmony2.setBackground(Color.GRAY);
					magnitudeOfWaves++;
				}
				updateRange (magnitudeOfWaves);
			}
		}
	}
	
	private class buttonNoise implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (!updating) {
				harm1 = false;
				harmony1.setBackground(defaultColor);
				harm2 = false;
				harmony2.setBackground(defaultColor);
				equation = false;
				wave.setBackground(defaultColor);
				oct = false;
				octave.setBackground(defaultColor);
				noisequat = true;
				noise.setBackground(Color.GRAY);
				magnitudeOfWaves = 1;
				updateRange (5);
			}
		}
	}
	
	private class buttonSave implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (!updating) {
				draw();
			}
		}
	}
	
	private class buttonTransform implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			seriesTransform.clear();
			if (magnitudeOfWaves > 0) {
				updating = true;
				range = Window.sliderRange.getValue();
				showTransform.setBackground(Color.GRAY);
				UpdateTransformChart();
			}
		}
	}
	
	private class buttonAnimateTransform implements ActionListener {
		@SuppressWarnings("deprecation")
		public void actionPerformed(ActionEvent e) {
			if (updating) {
				updating = false;
				animate.setLabel("Animate Transform");
				timer.stop();
			} else {
				seriesTransform.clear();
				if (magnitudeOfWaves > 0) {
					animate.setLabel("Stop Animation");
					updating = true;
					range = Window.sliderRange.getValue();
					wind = 0;
					firstTimeFourier = 0;
					timer = new javax.swing.Timer(0, timerTransformT1);
					timer.stop();
				    timer.setRepeats(false);
				    timer.start();
				}
			}
		}
	}
	
	
	

}