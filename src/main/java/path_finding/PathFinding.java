package path_finding;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang3.time.StopWatch;

import path_finding.dialog.JDialogCustomMapSize;
// import path_finding.dialog.JDialogMyCustomGAsParams;
import path_finding.dialog.JDialogli2006_GAsParams;
import path_finding.dialog.JFileChooserExportMap;
import path_finding.dialog.JFileChooserImportMap;

// import sun.awt.www.content.audio.wav;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;

/**
 * @author greerviau
 * @author thanhle1547
 */
public class PathFinding {

	// FRAME
	JFrame frame;
	// GENERAL VARIABLES
	private Map m = new Map();
	private int columns = m.getColumns();
	private int rows = m.getRows();
	private int delay = 30;
	private int tool = 0;
	private int checks = 0;
	private double length = 0;
	private long elapsed = 0;
	private int curAlg = 0;
	private int WIDTH = 1145; // 850;
	private final int HEIGHT = 650;
	private final int MSIZE = 600;
	private int CSIZE = MSIZE / (columns > rows ? columns : rows);
	private StopWatch watch = new StopWatch();
	// UTIL ARRAYS
	private String[] algorithms = { "GAs", "Dijkstra", "A*" };
	private String[] tools = { "Start", "Finish", "Wall", "Eraser" };
	// BOOLEANS
	private boolean solving = false;
	// UTIL
	Node[][] map = m.getMap();
	ArrayList<Node> wallList;
	ArrayList<ArrayList<Node>> pathList;
	Algorithm Alg = new Algorithm();
	Random r = new Random();
	// SLIDERS
	JSlider size = new JSlider(1, 5, 2);
	JSlider speed = new JSlider(0, 500, delay);
	JSlider obstacles = new JSlider(1, 100, 50);
	// LABELS
	JLabel algL = new JLabel("Algorithms");
	JLabel toolL = new JLabel("Toolbox");
	JLabel sizeL = new JLabel("Size:");
	JLabel cellsL = new JLabel(columns + " × " + rows);
	JLabel delayL = new JLabel("Delay:");
	JLabel msL = new JLabel(delay + " ms");
	JLabel obstacleL = new JLabel("Dens:");
	JLabel densityL = new JLabel(obstacles.getValue() + "%");
	JLabel checkL = new JLabel("Checks: " + checks);
	JLabel lengthL = new JLabel("Path Length: " + length);
	JLabel elapsedL = new JLabel("Elapsed Time: " + elapsed + " ms");
	JLabel tableL = new JLabel(
			"<html><nobr><b><font color=red>Tips:</font></b><br>"
		+ 	"&nbsp;&nbsp;+ Ctrl + Click chuột trái để bỏ chọn<br>"
		+ 	"&nbsp;&nbsp;+ Dùng phím mũi tên để chọn sẽ không<br>"
		+	"chính xác bằng chuột</nobr></html>"
	);
	// BUTTONS
	JButton searchB = new JButton("Start Search");
	JButton resetB = new JButton("Reset");
	JButton importB = new JButton("Import");
	JButton exportB = new JButton("Export");
	JButton genMapB = new JButton("Generate Map");
	JButton clearMapB = new JButton("Clear Map");
	// DROP DOWN
	JComboBox<String> algorithmsBx = new JComboBox<>(algorithms);
	JComboBox<String> toolBx = new JComboBox<>(tools);
	// TEXT AREAS
	JTextArea textArea = new JTextArea();
	// TABLE
	PathTable pathT = new PathTable();
	// PANELS
	JPanel menuBarP = new JPanel(new FlowLayout(FlowLayout.LEFT));
	JPanel toolP = new JPanel();
	JPanel ctrlsP = new JPanel();
	JPanel mapP = new JPanel();
	JPanel imExportP = new JPanel();
	JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
	JPanel resultP = new JPanel(new BorderLayout());
	JPanel pathListP = new JPanel(new BorderLayout());
	// MENU
	JMenuBar mapMB = new JMenuBar();
	JMenu mapM = new JMenu("Map");
	JMenuItem customMapSizeMI = new JMenuItem("Custom map size", KeyEvent.VK_C);
	JCheckBoxMenuItem showMapNumCBMI = new JCheckBoxMenuItem("Show cell number");
	JMenuItem creditMI = new JMenuItem("Credit");
	// CANVAS
	Canvas canvas;
	// BORDER
	Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

	public PathFinding() { // CONSTRUCTOR
		wallList = new ArrayList<>();
		pathList = new ArrayList<>();
		clearMap();
		initialize();
	}

	public void generateMap() { // GENERATE MAP
		clearMap(); // CREATE CLEAR MAP TO START
		for (int i = 0; i < m.getDensity(); i++) {
			Node current;
			do {
				int x = r.nextInt(columns);
				int y = r.nextInt(rows);
				current = map[x][y]; // FIND A RANDOM NODE IN THE GRID
			} while (current.getType() == 2); // IF IT IS ALREADY A WALL, FIND A NEW ONE
			current.setType(2); // SET NODE TO BE A WALL
			wallList.add(current);
		}
	}

	public void clearMap() { // CLEAR MAP
		m.setFinishX(-1); // RESET THE START AND FINISH
		m.setFinishY(-1);
		m.setStartX(-1);
		m.setStartY(-1);
		m.createNewMap(); // CREATE NEW MAP OF NODES
		this.map = m.getMap();
		for (int x = 0, no = 0; x < columns; x++) {
			for (int y = 0; y < rows; y++, no++) {
				map[x][y] = new Node(no, 3, x, y); // SET ALL NODES TO EMPTY
			}
		}
		wallList.clear();
		reset(); // RESET SOME VARIABLES
	}

	public void resetMap() { // RESET MAP
		for (int x = 0; x < columns; x++) {
			for (int y = 0; y < rows; y++) {
				Node current = map[x][y];
				if (current.getType() == 4 || current.getType() == 5) // CHECK TO SEE IF CURRENT NODE IS EITHER CHECKED
																		// OR FINAL PATH
					// map[x][y] = new Node(3,x,y); //RESET IT TO AN EMPTY NODE
					map[x][y].setType(3); // RESET IT TO AN EMPTY NODE
			}
		}
		if (m.hasStartNode()) { // RESET THE START AND FINISH
			// m.getStartNode() = new Node(0,startx,starty);
			m.getStartNode().setType(0);
			m.getStartNode().setHops(0);
		}
		if (m.hasFinishNode())
			// m.getFinishNode() = new Node(1,finishx,finishy);
			m.getFinishNode().setType(1);
		reset(); // RESET SOME VARIABLES
	}

	/**
	 * @author thanhle1547
	 */
	private int getCenter(int coord) {
		return (coord * CSIZE) + (CSIZE / 2);
	}

	/**
	 * @author thanhle1547
	 */
	private int getAQuarter(int coord) {
		return (coord * CSIZE) + (CSIZE / 4);
	}

	/**
	 * Round a double
	 * 
	 * @see <a href="https://stackoverflow.com/a/2808648">
	 * 			Round a double to 2 decimal places [duplicate]
	 * 		</a>
	 * @author thanhle1547
	 */
	private double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		return BigDecimal.valueOf(value)
			.setScale(places, RoundingMode.HALF_UP)
			.doubleValue();
	}

	/**
	 * @author thanhle1547
	 */
	private Component getParentComponent(ActionEvent e) {
		return ((Component) e.getSource()).getParent().getComponent(0);
	}

	/* NOT WORKING
	private void resizeFrame(int width, int height) {
		frame.setSize(width, height);
		frame.validate();
		frame.repaint();
		frame.pack();
	} */
	
	/**
	 * @author thanhle1547
	 */
	private void setEnableJPanel(JPanel panel, boolean state) {
		Component[] components = panel.getComponents();
		for (Component component : components) {
			if (component instanceof JPanel)
				setEnableJPanel((JPanel) component, state);
			if (component instanceof JScrollPane) {
				JScrollPane pane = (JScrollPane) component;
				pane.getHorizontalScrollBar().setEnabled(state);
				pane.getVerticalScrollBar().setEnabled(state);
				pane.getViewport().getView().setEnabled(state);
			}
			component.setEnabled(state);
		}
	}

	/**
	 * @author thanhle1547
	 */
	private void setEnableWorkableComponents(boolean state) {
		setEnableJPanel(mapP, state);
		resetB.setEnabled(state);
		searchB.setEnabled(state);
		algorithmsBx.setEnabled(state);
		mapM.setEnabled(state);
		setEnableJPanel(pathListP, state);
	}

	private void initialize() { // INITIALIZE THE GUI ELEMENTS
		frame = new JFrame();
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setSize(WIDTH, HEIGHT);
		frame.setTitle("Path Finding");
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		mapP.setBorder(BorderFactory.createTitledBorder(loweredetched, "Map"));
		ctrlsP.setBorder(BorderFactory.createTitledBorder(loweredetched, "Controls"));
		int space = 25;
		int buff = 42;

		BoxLayout boxLayout = new BoxLayout(toolP, BoxLayout.Y_AXIS);
		toolP.setLayout(boxLayout);
		// toolP.setLayout(null);
		toolP.setBounds(10, 0, 210, 580);

		// Panel Preferred Size
		menuBarP.setPreferredSize(new Dimension(210, 15));

		mapP.setLayout(null);
		mapP.setPreferredSize(new Dimension(210, 260));
		// mapP.setBounds(0, 0, 210, 360);

		ctrlsP.setLayout(null);
		ctrlsP.setPreferredSize(new Dimension(210, 260));
		// ctrlsP.setBounds(0, 360, 210, 240);

		// Tool Panel Menubar
		mapM.setMnemonic(KeyEvent.VK_M);
		mapMB.add(mapM);
		mapMB.add(creditMI);

		showMapNumCBMI.setSelected(true);
		showMapNumCBMI.setToolTipText("The number won't display if number of columns or rows > 20");
		mapM.add(showMapNumCBMI);

		customMapSizeMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
		mapM.add(customMapSizeMI);
		menuBarP.add(mapMB);

		// Map Panel
		imExportP.setBounds(22, space, 155, 35);
		importB.setMnemonic(KeyEvent.VK_I);
		imExportP.add(importB);
		imExportP.add(exportB);
		mapP.add(imExportP);
		space += buff + 5;

		genMapB.setBounds(40, space, 120, 25);
		mapP.add(genMapB);
		space += buff;

		clearMapB.setBounds(40, space, 120, 25);
		mapP.add(clearMapB);
		space += buff - 10;

		toolL.setBounds(40, space, 120, 25);
		mapP.add(toolL);
		space += 25;

		toolBx.setBounds(40, space, 120, 25);
		mapP.add(toolBx);
		space += buff - 5;

		sizeL.setBounds(15, space, 40, 25);
		mapP.add(sizeL);
		size.setMajorTickSpacing(10);
		size.setBounds(50, space, 100, 25);
		mapP.add(size);
		cellsL.setBounds(158, space, 42, 25);
		mapP.add(cellsL);
		space += buff - 10;

		obstacleL.setBounds(15, space, 100, 25);
		mapP.add(obstacleL);
		obstacles.setToolTipText("Obstacles density (Generate Map function)");
		obstacles.setMajorTickSpacing(5);
		obstacles.setBounds(50, space, 100, 25);
		mapP.add(obstacles);
		densityL.setBounds(158, space, 100, 25);
		mapP.add(densityL);

		// Controls Panel
		space = 25;

		searchB.setBounds(40, space, 120, 25);
		ctrlsP.add(searchB);
		space += buff;

		resetB.setBounds(40, space, 120, 25);
		ctrlsP.add(resetB);
		space += buff - 10;

		algL.setBounds(40, space, 120, 25);
		ctrlsP.add(algL);
		space += 25;

		algorithmsBx.setBounds(40, space, 120, 25);
		ctrlsP.add(algorithmsBx);
		space += 40;

		delayL.setBounds(15, space, 50, 25);
		ctrlsP.add(delayL);
		speed.setMajorTickSpacing(5);
		speed.setBounds(50, space, 100, 25);
		ctrlsP.add(speed);
		msL.setBounds(158, space, 42, 25);
		ctrlsP.add(msL);
		space += buff - 10;

		checkL.setBounds(15, space, 100, 25);
		ctrlsP.add(checkL);
		space += buff - 20;

		lengthL.setBounds(15, space, 180, 25);
		ctrlsP.add(lengthL);
		space += buff - 20;

		elapsedL.setBounds(15, space, 180, 25);
		ctrlsP.add(elapsedL);
		space += buff - 10;

		toolP.add(menuBarP);
		toolP.add(mapP);
		toolP.add(Box.createVerticalStrut(5));
		toolP.add(ctrlsP);

		frame.getContentPane().add(toolP);

		// Canvas
		canvas = new Canvas();
		canvas.setBounds(230, 5, MSIZE + 1, MSIZE + 1);
		frame.getContentPane().add(canvas);

		// Tabbed Pane
		JPanel rightP = new JPanel(new BorderLayout());
		rightP.setBounds(845, 5, 275, MSIZE);
		// Text Panel
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		resultP.add(new JScrollPane(textArea));
		tabbedPane.addTab("Process/Result", resultP);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		// Path List Panel
		tableL.setBorder(new EmptyBorder(4, 4, 4, 4));
		pathListP.add(tableL, BorderLayout.PAGE_START);
		pathListP.add(new JScrollPane(pathT), BorderLayout.CENTER);
		tabbedPane.addTab("Path list", pathListP);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

		tabbedPane.setSelectedIndex(0);
		rightP.add(tabbedPane, BorderLayout.CENTER);

		frame.getContentPane().add(rightP);

		// ACTION LISTENERS
		showMapNumCBMI.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				canvas.repaint();
			}
		});
		customMapSizeMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialogCustomMapSize dialog = new JDialogCustomMapSize();
				if (dialog.show() == JOptionPane.OK_OPTION) {
					m.setMapSize(dialog.getColumns(), dialog.getRows());
					m.createNewMap();
					columns = m.getColumns();
					rows = m.getRows();
					map = m.getMap();
					clearMap();
					Update();
				}
			}
		});
		importB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooserImportMap dialog = new JFileChooserImportMap(getParentComponent(e), m);
				dialog.show();

				if (dialog.getResponse() == JFileChooser.CANCEL_OPTION)
					return;

				clearMap();
				pathList.clear();
				m.setMap(dialog.getResult());
				map = m.getMap();
				columns = m.getColumns();
				rows = m.getRows();
				for (int x = 0; x < columns; x++) {
					for (int y = 0; y < rows; y++) {
						if (map[x][y].getType() == 2)
							wallList.add(map[x][y]);
					}
				}
				Update();
			}
		});
		exportB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooserExportMap dialog = new JFileChooserExportMap(getParentComponent(e), m);
				dialog.show();
			}
		});
		genMapB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				generateMap();
				Update();
			}
		});
		clearMapB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearMap();
				Update();
			}
		});
		toolBx.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				tool = toolBx.getSelectedIndex();
			}
		});
		size.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				m.setMapSize(size.getValue() * 10, size.getValue() * 10);
				// m.setCells(size.getValue());
				columns = m.getColumns();
				rows = m.getRows();
				clearMap();
				Update();
			}
		});
		obstacles.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				m.setDense((double) obstacles.getValue() / 100);
				Update();
			}
		});
		searchB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
				if (m.hasStartNode() && m.hasFinishNode())
					solving = true;
			}
		});
		resetB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetMap();
				Update();
			}
		});
		algorithmsBx.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				curAlg = algorithmsBx.getSelectedIndex();
				Update();
			}
		});
		speed.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				delay = speed.getValue();
				Update();
			}
		});
		pathT.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				ArrayList<ArrayList<Node>> path = pathT.getSelectedPath();
				pathList.clear();
				if (path != null)
					pathList.addAll(path);

				canvas.repaint();
			}
		});
		creditMI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame, "	                         Pathfinding\n"
												   + "             Copyright (c) 2017-2018\n"
												   + "                         Greer Viau\n"
												   + "          Build Date:  March 28, 2018   ", "Credit", JOptionPane.PLAIN_MESSAGE, new ImageIcon(""));
			}
		});
		
		startSearch();	//START STATE
	}
	
	public void startSearch() {	//START STATE
		if(solving) {
			switch(curAlg) {
				case 0:
					Alg.GAs();
					break;
				case 1:
					Alg.Dijkstra();
					break;
				case 2:
					Alg.AStar();
					break;
			}
		}
		pause();	//PAUSE STATE
	}
	
	public void pause() {	//PAUSE STATE
		int i = 0;
		while(!solving) {
			i++;
			if(i > 500)
				i = 0;
			try {
				Thread.sleep(1);
			} catch(Exception e) {}
		}
		startSearch();	//START STATE
	}
	
	public void Update() {	//UPDATE ELEMENTS OF THE GUI
		// auto update capacity when called func setCells
		m.updateDensity();
		CSIZE = MSIZE / (columns > rows ? columns : rows);
		canvas.repaint();
		cellsL.setText(columns + " × " + rows);
		msL.setText(delay + " ms");
		lengthL.setText("Path Length: " + length);
		densityL.setText(obstacles.getValue() + "%");
		checkL.setText("Checks: " + checks);
		elapsedL.setText("Elapsed Time: " + elapsed + " ms");
	}

	public void updateWallList(Node node) {
		if (node.getType() == 2)
			wallList.add(node);
		else if (node.getType() == 3)
			wallList.remove(node);
	}
	
	public void reset() {	//RESET METHOD
		solving = false;
		length = 0;
		checks = 0;
		elapsed = 0;
		textArea.setText(null);
		pathList.clear();
		pathT.clearData();
		watch.reset();
	}
	
	public void delay() {	//DELAY METHOD
		try {
			if (watch.isStarted()) watch.suspend();
			Thread.sleep(delay);
			if (watch.isStarted()) watch.resume();
		} catch(Exception e) {}
	}
	
	class Canvas extends JPanel implements MouseListener, MouseMotionListener{	//MAP CLASS
		private static final long serialVersionUID = 3006316467662386292L;

		public Canvas() {
			addMouseListener(this);
			addMouseMotionListener(this);
		}
		
		public void paintComponent(Graphics g) {	//REPAINT
			super.paintComponent(g);
			for(int x = 0; x < columns; x++) {	//PAINT EACH NODE IN THE GRID
				for(int y = 0; y < rows; y++) {
					switch(map[x][y].getType()) {
						case 0:
							g.setColor(Color.GREEN);
							break;
						case 1:
							g.setColor(Color.RED);
							break;
						case 2:
							g.setColor(Color.BLACK);
							break;
						case 3:
							g.setColor(Color.WHITE);
							break;
						case 4:
							g.setColor(Color.CYAN);
							break;
						case 5:
							g.setColor(Color.YELLOW);
							break;
					}
					g.fillRect(x*CSIZE,y*CSIZE,CSIZE,CSIZE);
					g.setColor(Color.BLACK);
					g.drawRect(x*CSIZE,y*CSIZE,CSIZE,CSIZE);

					if (showMapNumCBMI.isSelected() && columns <= 20 && rows <= 20)
						g.drawString(String.valueOf(map[x][y].getNo()), getAQuarter(x), getCenter(y));
					//DEBUG STUFF
					/* if(curAlg == 1)
						g.drawString(
							map[x][y].getHops() + "/" + map[x][y].getEuclidDist(m.getFinishX(), m.getFinishY()), 
							(x*CSIZE)+(CSIZE/2)-10, 
							(y*CSIZE)+(CSIZE/2)
						);
					else 
						g.drawString("" + map[x][y].getHops(), 
							(x*CSIZE)+(CSIZE/2), (y*CSIZE)+(CSIZE/2)); */
					
				}
			}

			g.setColor(Color.MAGENTA);
			for (ArrayList<Node> path : pathList) {
				g.drawOval(
						getCenter(path.get(0).getX()) - 3, getCenter(path.get(0).getY()) - 3, 6, 6);
				g.fillOval(
						getCenter(path.get(0).getX()) - 3, getCenter(path.get(0).getY()) - 3, 6, 6);
				for (int i = 0; i < path.size() - 1; i++) {
					Node curr = path.get(i),
						 next = path.get(i+1);
					g.drawLine(
							getCenter(curr.getX()), getCenter(curr.getY()), 
							getCenter(next.getX()), getCenter(next.getY())
						);
					g.drawOval(getCenter(next.getX()) - 3, getCenter(next.getY()) - 3, 6, 6);
					g.fillOval(getCenter(next.getX()) - 3, getCenter(next.getY()) - 3, 6, 6);
				}
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			try {
				int x = e.getX()/CSIZE;	
				int y = e.getY()/CSIZE;
				Node current = map[x][y];
				if((tool == 2 || tool == 3) && (current.getType() != 0 && current.getType() != 1))
					current.setType(tool);
				Update();
				updateWallList(current);
			} catch(Exception z) {}
		}

		@Override
		public void mouseMoved(MouseEvent e) {}

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {
			resetMap();	//RESET THE MAP WHENEVER CLICKED
			try {
				int x = e.getX()/CSIZE;	//GET THE X AND Y OF THE MOUSE CLICK IN RELATION TO THE SIZE OF THE GRID
				int y = e.getY()/CSIZE;
				Node current = map[x][y];
				switch(tool) {
					case 0: {	//START NODE
						if(current.getType()!=2) {	//IF NOT WALL
							if(m.hasStartNode()) {	//IF START EXISTS SET IT TO EMPTY
								m.getStartNode().setType(3);
								m.getStartNode().setHops(-1);
							}
							current.setHops(0);
							m.setStartX(x);	//SET THE START X AND Y
							m.setStartY(y);
							current.setType(0);	//SET THE NODE CLICKED TO BE START
						}
						break;
					}
					case 1: {//FINISH NODE
						if(current.getType()!=2) {	//IF NOT WALL
							if(m.hasFinishNode())	//IF FINISH EXISTS SET IT TO EMPTY
								m.getFinishNode().setType(3);
							m.setFinishX(x);	//SET THE FINISH X AND Y
							m.setFinishY(y);
							current.setType(1);	//SET THE NODE CLICKED TO BE FINISH
						}
						break;
					}
					default:
						if(current.getType() != 0 && current.getType() != 1)
							current.setType(tool);
						updateWallList(current);
						break;
				}
				Update();
			} catch(Exception z) {}	//EXCEPTION HANDLER
		}

		@Override
		public void mouseReleased(MouseEvent e) {}
	}
	
	class Algorithm {	//ALGORITHM CLASS
		
		//DIJKSTRA WORKS BY PROPAGATING OUTWARDS UNTIL IT FINDS THE FINISH AND THEN WORKING ITS WAY BACK TO GET THE PATH
		//IT USES A PRIORITY QUE TO KEEP TRACK OF NODES THAT IT NEEDS TO EXPLORE
		//EACH NODE IN THE PRIORITY QUE IS EXPLORED AND ALL OF ITS NEIGHBORS ARE ADDED TO THE QUE
		//ONCE A NODE IS EXLPORED IT IS DELETED FROM THE QUE
		//AN ARRAYLIST IS USED TO REPRESENT THE PRIORITY QUE
		//A SEPERATE ARRAYLIST IS RETURNED FROM A METHOD THAT EXPLORES A NODES NEIGHBORS
		//THIS ARRAYLIST CONTAINS ALL THE NODES THAT WERE EXPLORED, IT IS THEN ADDED TO THE QUE
		//A HOPS VARIABLE IN EACH NODE REPRESENTS THE NUMBER OF NODES TRAVELED FROM THE START
		public void Dijkstra() {
			ArrayList<Node> priority = new ArrayList<Node>();	//CREATE A PRIORITY QUE
			priority.add(m.getStartNode());	//ADD THE START TO THE QUE
			while(solving) {
				if(priority.size() <= 0) {	//IF THE QUE IS 0 THEN NO PATH CAN BE FOUND
					solving = false;
					break;
				}
				int hops = priority.get(0).getHops()+1;	//INCREMENT THE HOPS VARIABLE
				ArrayList<Node> explored = exploreNeighbors(priority.get(0), hops);	//CREATE AN ARRAYLIST OF NODES THAT WERE EXPLORED
				if(explored.size() > 0) {
					priority.remove(0);	//REMOVE THE NODE FROM THE QUE
					priority.addAll(explored);	//ADD ALL THE NEW NODES TO THE QUE
					Update();
					delay();
				} else {	//IF NO NODES WERE EXPLORED THEN JUST REMOVE THE NODE FROM THE QUE
					priority.remove(0);
				}
			}
		}
		
		//A STAR WORKS ESSENTIALLY THE SAME AS DIJKSTRA CREATING A PRIORITY QUE AND PROPAGATING OUTWARDS UNTIL IT FINDS THE END
		//HOWEVER ASTAR BUILDS IN A HEURISTIC OF DISTANCE FROM ANY NODE TO THE FINISH
		//THIS MEANS THAT NODES THAT ARE CLOSER TO THE FINISH WILL BE EXPLORED FIRST
		//THIS HEURISTIC IS BUILT IN BY SORTING THE QUE ACCORDING TO HOPS PLUS DISTANCE UNTIL THE FINISH
		public void AStar() {
			ArrayList<Node> priority = new ArrayList<Node>();
			priority.add(m.getStartNode());
			while(solving) {
				if(priority.size() <= 0) {
					solving = false;
					break;
				}
				int hops = priority.get(0).getHops()+1;
				ArrayList<Node> explored = exploreNeighbors(priority.get(0),hops);
				if(explored.size() > 0) {
					priority.remove(0);
					priority.addAll(explored);
					Update();
					delay();
				} else {
					priority.remove(0);
				}
				sortQue(priority);	//SORT THE PRIORITY QUE
			}
		}

		/**
		 * @author thanhle1547
		 */
		public void GAs() {
			/* int xPos = frame.getLocation().x;
			int yPos = frame.getLocation().y;
			JOptionPaneTextAreas dialog = new JOptionPaneTextAreas(frame, true);

			frame.setLocation(xPos - 200, yPos);
			dialog.setText(population.toString());
			dialog.show(xPos + MSIZE + 50 , yPos, 400, MSIZE); */
			
			// JDialogMyCustomGAsParams dialog = new JDialogMyCustomGAsParams();
			JDialogli2006_GAsParams dialog = new JDialogli2006_GAsParams();
			if (dialog.show() != JOptionPane.OK_OPTION) {
				solving = false;
				return;
			}

			// GeneticAlgorithm alg = new GeneticAlgorithm(m, wallList, CSIZE, dialog.getPenaltyValue());
			ProposedGAs alg = new ProposedGAs(m, wallList, CSIZE, dialog.getNumOfSizeForSelect());
			alg.setCommonParams(dialog.getCrossoverProbability(), dialog.getMutationProbability());
			int generation = 0;

			watch.start();
			// alg.initPopulation(dialog.getPopulationSize(), columns, rows, m.getCapacity());
			alg.initPopulation(dialog.getPopulationSize());
			pathList = alg.getPopulation();

			pathT.addTitle(alg.getStringDataFormat());
			setEnableWorkableComponents(false);
			delay();
			try {
				while (solving) {
					if (generation == dialog.getGenerationNumber()) {
						solving = false;
						break;
					}

					pathT.addTitle("  -  Thế hệ F" + generation);

					Update();
					delay();

					alg.evaluate();
					pathList = alg.getPopulation();
					pathT.addData(
							"    +,  Kq sau khi đánh giá:", 
							"danhgia/" + generation, 
							pathList, 
							alg.dataToString()
					);
					Update();
					delay();

					alg.select();
					pathList = alg.getPopulation();
					pathT.addData(
							"    +,  Kq sau khi chọn lọc:", 
							"chonloc/" + generation, 
							pathList, 
							alg.dataToString()
					);
					Update();
					delay();

					alg.crossover();
					pathList = alg.getPopulation();
					pathT.addData(
							"    +,  Kq sau khi lai ghép:", 
							"laighep/" + generation, 
							pathList, 
							alg.dataToString()
					);
					Update();
					delay();

					alg.mutation();
					pathList = alg.getPopulation();
					pathT.addData(
							"    +,  Kq sau khi đột biến:", 
							"dotbien/" + generation, 
							pathList, 
							alg.dataToString()
					);
					Update();
					delay();

					alg.refinement();
					pathList = alg.getPopulation();
					pathT.addData(
							"    +,  Kq sau khi làm mịn:", 
							"lammin/" + generation, 
							pathList, 
							alg.dataToString()
					);
					Update();
					delay();

					alg.deletion();
					pathList = alg.getPopulation();
					pathT.addData(
							"    +,  Kq sau khi xóa bỏ nút dư thừa:", 
							"boduthua/" + generation, 
							pathList, 
							alg.dataToString()
					);
					Update();
					delay();

					generation++;

					pathT.addTitle("-- -- -- -- -- -- -- -- -- -- -- -- -- -- --");
				}
				watch.stop();
				elapsed = watch.getTime();

				setEnableWorkableComponents(true);
				delay();

				alg.selectBestOnce();
				pathList = alg.getBestIndvAsPopulation();
				pathT.addData(
						null, 
						"best/", 
						pathList, 
						alg.bestIndvToString()
				);
				length = round(alg.getBestIndvFitness(), 2);
				Update();
			} catch (NoSuchElementException e) {
				JOptionPane.showMessageDialog(
					frame, 
					"Có thế tỉ lệ lai ghép hoặc đột biến thấp dẫn đến\n"
					+ "không còn phần tử nào trong quần thể được chọn",
					"Lỗi",
					JOptionPane.ERROR_MESSAGE
				);
			}
		}
		
		public ArrayList<Node> sortQue(ArrayList<Node> sort) {	//SORT PRIORITY QUE
			int c = 0;
			while(c < sort.size()) {
				int sm = c;
				for(int i = c+1; i < sort.size(); i++) {
					if(sort.get(i).getEuclidDistToNode(m.getFinishNode())+sort.get(i).getHops()
						< sort.get(sm).getEuclidDistToNode(m.getFinishNode())+sort.get(sm).getHops())
						sm = i;
				}
				if(c != sm) {
					Node temp = sort.get(c);
					sort.set(c, sort.get(sm));
					sort.set(sm, temp);
				}	
				c++;
			}
			return sort;
		}
		
		public ArrayList<Node> exploreNeighbors(Node current, int hops) {	//EXPLORE NEIGHBORS
			ArrayList<Node> explored = new ArrayList<Node>();	//LIST OF NODES THAT HAVE BEEN EXPLORED
			for(int a = -1; a <= 1; a++) {
				for(int b = -1; b <= 1; b++) {
					int xbound = current.getX()+a;
					int ybound = current.getY()+b;
					if((xbound > -1 && xbound < columns) && (ybound > -1 && ybound < rows)) {	//MAKES SURE THE NODE IS NOT OUTSIDE THE GRID
						Node neighbor = map[xbound][ybound];
						if((neighbor.getHops()==-1 || neighbor.getHops() > hops) && neighbor.getType()!=2) {	//CHECKS IF THE NODE IS NOT A WALL AND THAT IT HAS NOT BEEN EXPLORED
							explore(neighbor, current.getX(), current.getY(), hops);	//EXPLORE THE NODE
							explored.add(neighbor);	//ADD THE NODE TO THE LIST
						}
					}
				}
			}
			return explored;
		}
		
		public void explore(Node current, int lastx, int lasty, int hops) {	//EXPLORE A NODE
			if(current.getType()!=0 && current.getType() != 1)	//CHECK THAT THE NODE IS NOT THE START OR FINISH
				current.setType(4);	//SET IT TO EXPLORED
			current.setLastNode(lastx, lasty);	//KEEP TRACK OF THE NODE THAT THIS NODE IS EXPLORED FROM
			current.setHops(hops);	//SET THE HOPS FROM THE START
			checks++;
			if(current.getType() == 1) {	//IF THE NODE IS THE FINISH THEN BACKTRACK TO GET THE PATH
				backtrack(current.getLastX(), current.getLastY(),hops);
			}
		}
		
		public void backtrack(int lx, int ly, int hops) {	//BACKTRACK
			length = hops;
			while(hops > 1) {	//BACKTRACK FROM THE END OF THE PATH TO THE START
				Node current = map[lx][ly];
				current.setType(5);
				lx = current.getLastX();
				ly = current.getLastY();
				hops--;
			}
			solving = false;
		}
	}
}
