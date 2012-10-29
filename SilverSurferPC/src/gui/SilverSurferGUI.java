package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import commands.Command;
import communication.*;
import simulator.*;

public class SilverSurferGUI {
	private static JFrame frame;
	private static SimulationJPanel simulationPanel;

	private static UnitCommunicator unitCommunicator;
	private static UnitCommunicator prevCommunicator;

	private static JButton bluetoothConnect;
	private static JLabel bluetoothStatus;
	private static ImageIcon bluetoothNotConnectedIcon = new ImageIcon("resources/bluetooth_icons/bluetooth_no_connection.png");
	private static ImageIcon bluetoothConnectedIcon = new ImageIcon("resources/bluetooth_icons/bluetooth_connected.png");

	private static JSpinner polygonEdgeLength;
	private static JSlider polygonangles;
	private static JButton polygondraw;

	private static JButton uparrow;
	private static JButton downarrow;
	private static JButton leftarrow;
	private static JButton rightarrow;
	private static ImageIcon uparrowicon = new ImageIcon("resources/round_grey_arrows/arrow-up.png");
	private static ImageIcon uparrowpressedicon = new ImageIcon("resources/round_grey_arrows/arrow-up-pressed.png");
	private static ImageIcon leftarrowicon = new ImageIcon("resources/round_grey_arrows/arrow-left.png");
	private static ImageIcon leftarrowpressedicon = new ImageIcon("resources/round_grey_arrows/arrow-left-pressed.png");
	private static ImageIcon downarrowicon = new ImageIcon("resources/round_grey_arrows/arrow-down.png");
	private static ImageIcon downarrowpressedicon = new ImageIcon("resources/round_grey_arrows/arrow-down-pressed.png");
	private static ImageIcon rightarrowicon = new ImageIcon("resources/round_grey_arrows/arrow-right.png");
	private static ImageIcon rightarrowpressedicon = new ImageIcon("resources/round_grey_arrows/arrow-right-pressed.png");

	private static JSlider speedvalues;
	private static JButton speedbutton;

	private static JButton focus;

	private static JTextArea textArea;
	private static JTextArea txtLightSensorStatus;
	private static JTextArea txtUltraSensorStatus;
	private static JTextArea txtPushSensor1Status;
	private static JTextArea txtPushSensor2Status;

	private static StatusInfoBuffer informationBuffer;

	private static JPanel mappingPanel;

	private static boolean onManual = false;

	private static JButton clearButton;

	private static JButton compassButton;
	private static ImageIcon compassicon = new ImageIcon("resources/round_grey_arrows/arrow-right.png");




	private  void createAndShowGUI() {
		informationBuffer = new StatusInfoBuffer();
		frame = new JFrame("Silver Surfer Command Center");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setBackground(new Color(221,230,231));

		JPanel compassPanel = compassPanel();
		JPanel bluetoothPanel = bluetoothPanel();
		JPanel polygonPanel = polygonPanel();
		JPanel arrowPanel = arrowPanel();
		JPanel speedPanel = speedPanel();
		JPanel focusPanel = focusPanel();
		JPanel mappingPanel = mappingPanel();
		JPanel consolePanel = consolePanel();
		JPanel clearPanel = clearPanel();
		//JPanel statusPanel = statusPanel();
		
		redirectSystemStreams();

		GroupLayout frameLayout = new GroupLayout(frame.getContentPane());
		frame.getContentPane().setLayout(frameLayout);
		frameLayout.setHorizontalGroup(frameLayout.createSequentialGroup()
				.addGroup(frameLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addGroup(frameLayout.createSequentialGroup()
								.addComponent(bluetoothPanel)
								.addComponent(compassPanel))
								.addComponent(polygonPanel)
								.addComponent(arrowPanel)
								.addComponent(speedPanel)
								.addGroup(frameLayout.createSequentialGroup()
										.addComponent(focusPanel)
										.addComponent(clearPanel)))
										.addGroup(frameLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
												.addComponent(mappingPanel)
												.addComponent(consolePanel)));
		frameLayout.setVerticalGroup(frameLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addGroup(frameLayout.createSequentialGroup()
						.addGroup(frameLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(bluetoothPanel)
								.addComponent(compassPanel))
								.addComponent(polygonPanel)
								.addComponent(arrowPanel)
								.addComponent(speedPanel)
								.addGroup(frameLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
										.addComponent(focusPanel)
										.addComponent(clearPanel)))
										.addGroup(frameLayout.createSequentialGroup()
												.addComponent(mappingPanel)
												.addComponent(consolePanel)));
		frameLayout.linkSize(SwingConstants.HORIZONTAL, polygonPanel, speedPanel);
		frameLayout.linkSize(SwingConstants.VERTICAL, polygonPanel, consolePanel);

		frame.pack();
		frame.setSize(1000, 800);
		frame.setVisible(true);

		unitCommunicator = new SimulatorCommunicator(informationBuffer);
		try {
			System.out.println("[CONNECTION] Entered simulator mode.");
			unitCommunicator.openUnitConnection();
		} catch (IOException e) {
			System.out.println("[CONNECTION] Oops! Something went wrong initializing!");
		}
		addListeners();
	}

	public JFrame getFrame() {
		return frame;
	}

	public SimulationJPanel getSimulationPanel() {
		return simulationPanel;
	}

	private static JPanel bluetoothPanel() {
		bluetoothConnect = new JButton("Connect");

		bluetoothStatus = new JLabel(bluetoothNotConnectedIcon);

		JPanel bluetoothPanel = new JPanel();
		bluetoothPanel.setBorder(BorderFactory.createTitledBorder(createBorder(),"Bluetooth"));
		bluetoothPanel.setOpaque(false);

		GroupLayout bluetoothlayout = new GroupLayout(bluetoothPanel);
		bluetoothPanel.setLayout(bluetoothlayout);
		bluetoothlayout.setAutoCreateGaps(true);
		bluetoothlayout.setAutoCreateContainerGaps(true);
		bluetoothlayout.setHorizontalGroup(bluetoothlayout.createSequentialGroup()
				.addComponent(bluetoothConnect)
				.addComponent(bluetoothStatus));
		bluetoothlayout.setVerticalGroup(bluetoothlayout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(bluetoothConnect)
				.addComponent(bluetoothStatus));

		return bluetoothPanel;
	}

	private static JPanel polygonPanel() {
		JLabel polygonAnglesLabel = new JLabel("Angles", JLabel.CENTER);

		polygonangles = new JSlider(JSlider.HORIZONTAL, 1, 30, 5);
		polygonangles.setSnapToTicks(true);
		polygonangles.setMajorTickSpacing(3);
		polygonangles.setMinorTickSpacing(1);
		polygonangles.setPaintTicks(true);
		polygonangles.setPaintLabels(true);
		polygonangles.setOpaque(false);

		JLabel polygonEdgeLengthLabel = new JLabel("Edge Length (centimeters)", JLabel.CENTER);

		SpinnerNumberModel polygonEdgeLengthModel = new SpinnerNumberModel(10, 0, 1000, 1);
		polygonEdgeLength = new JSpinner(polygonEdgeLengthModel);

		polygondraw = new JButton("Execute polygon");

		JPanel polygonPanel = new JPanel();
		polygonPanel.setBorder(BorderFactory.createTitledBorder(createBorder(),"Polygon"));
		polygonPanel.setOpaque(false);

		GroupLayout polygonlayout = new GroupLayout(polygonPanel);
		polygonPanel.setLayout(polygonlayout);
		polygonlayout.setAutoCreateGaps(true);
		polygonlayout.setAutoCreateContainerGaps(true);
		polygonlayout.setHorizontalGroup(polygonlayout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addGroup(polygonlayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(polygonAnglesLabel)
						.addComponent(polygonangles))
						.addGroup(polygonlayout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(polygonEdgeLengthLabel)
								.addComponent(polygonEdgeLength))
								.addComponent(polygondraw));
		polygonlayout.setVerticalGroup(polygonlayout.createSequentialGroup()
				.addGroup(polygonlayout.createSequentialGroup()
						.addComponent(polygonAnglesLabel)
						.addComponent(polygonangles))
						.addGroup(polygonlayout.createSequentialGroup()
								.addComponent(polygonEdgeLengthLabel)
								.addComponent(polygonEdgeLength))
								.addComponent(polygondraw));

		return polygonPanel;
	}

	private static JPanel arrowPanel() {
		uparrow = new JButton(uparrowicon);
		uparrow.setBorder(BorderFactory.createEmptyBorder());
		uparrow.setContentAreaFilled(false);

		leftarrow = new JButton(leftarrowicon);
		leftarrow.setBorder(BorderFactory.createEmptyBorder());
		leftarrow.setContentAreaFilled(false);

		downarrow = new JButton(downarrowicon);
		downarrow.setBorder(BorderFactory.createEmptyBorder());
		downarrow.setContentAreaFilled(false);

		rightarrow = new JButton(rightarrowicon);
		rightarrow.setBorder(BorderFactory.createEmptyBorder());
		rightarrow.setContentAreaFilled(false);

		JPanel arrowPanel = new JPanel();
		arrowPanel.setBorder(BorderFactory.createTitledBorder(createBorder(),"GUI control"));
		arrowPanel.setOpaque(false);

		GroupLayout arrowlayout = new GroupLayout(arrowPanel);
		arrowPanel.setLayout(arrowlayout);
		arrowlayout.setAutoCreateGaps(true);
		arrowlayout.setAutoCreateContainerGaps(true);
		arrowlayout.setHorizontalGroup(arrowlayout.createSequentialGroup()
				.addComponent(leftarrow)
				.addGroup(arrowlayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(uparrow)
						.addComponent(downarrow))
						.addComponent(rightarrow));
		arrowlayout.setVerticalGroup(arrowlayout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(leftarrow)
				.addGroup(arrowlayout.createSequentialGroup()
						.addComponent(uparrow)
						.addComponent(downarrow))
						.addComponent(rightarrow));

		return arrowPanel;
	}

	private static JPanel speedPanel() {
		JLabel speedLabel = new JLabel("Speed Level", JLabel.CENTER);

		speedvalues = new JSlider(JSlider.HORIZONTAL, 1, 4, 2);
		speedvalues.setSnapToTicks(true);
		speedvalues.setMajorTickSpacing(1);
		speedvalues.setMinorTickSpacing(1);
		speedvalues.setPaintTicks(true);
		speedvalues.setPaintLabels(true);
		speedvalues.setOpaque(false);

		speedbutton = new JButton("Change speed");

		JPanel speedPanel = new JPanel();
		speedPanel.setBorder(BorderFactory.createTitledBorder(createBorder(),"Speed"));
		speedPanel.setOpaque(false);

		GroupLayout arrowlayout = new GroupLayout(speedPanel);
		speedPanel.setLayout(arrowlayout);
		arrowlayout.setAutoCreateGaps(true);
		arrowlayout.setAutoCreateContainerGaps(true);
		arrowlayout.setHorizontalGroup(arrowlayout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(speedLabel)
				.addComponent(speedvalues)
				.addComponent(speedbutton));
		arrowlayout.setVerticalGroup(arrowlayout.createSequentialGroup()
				.addComponent(speedLabel)
				.addComponent(speedvalues)
				.addComponent(speedbutton));

		return speedPanel;
	}

	private static JPanel compassPanel() {
		compassButton = new JButton(compassicon);
		compassButton.setBorder(BorderFactory.createEmptyBorder());
		compassButton.setContentAreaFilled(false);

		JPanel compassPanel = new JPanel();
		compassPanel.setBorder(BorderFactory.createTitledBorder(createBorder(),"Compass"));
		compassPanel.setOpaque(false);

		GroupLayout compasslayout = new GroupLayout(compassPanel);
		compassPanel.setLayout(compasslayout);
		compasslayout.setAutoCreateGaps(true);
		compasslayout.setAutoCreateContainerGaps(true);
		compasslayout.setHorizontalGroup(compasslayout.createSequentialGroup()
				.addComponent(compassButton));
		compasslayout.setVerticalGroup(compasslayout.createSequentialGroup()
				.addComponent(compassButton));


		return compassPanel;
	}
	private static JPanel focusPanel() {
		focus = new JButton("Manual Control");

		JPanel focusPanel = new JPanel();
		focusPanel.setBorder(BorderFactory.createTitledBorder(createBorder(),"GUI/Manual switch"));
		focusPanel.setOpaque(false);

		GroupLayout focuslayout = new GroupLayout(focusPanel);
		focusPanel.setLayout(focuslayout);
		focuslayout.setAutoCreateGaps(true);
		focuslayout.setAutoCreateContainerGaps(true);
		focuslayout.setHorizontalGroup(focuslayout.createSequentialGroup()
				.addComponent(focus));
		focuslayout.setVerticalGroup(focuslayout.createSequentialGroup()
				.addComponent(focus));

		return focusPanel;
	}



	private static JPanel clearPanel(){
		clearButton = new JButton("Clear Screen");

		JPanel clearPanel = new JPanel();
		clearPanel.setBorder(BorderFactory.createTitledBorder(createBorder(),"Clear Screen"));
		clearPanel.setOpaque(false);

		GroupLayout clearlayout = new GroupLayout(clearPanel);
		clearPanel.setLayout(clearlayout);
		clearlayout.setAutoCreateGaps(true);
		clearlayout.setAutoCreateContainerGaps(true);
		clearlayout.setHorizontalGroup(clearlayout.createSequentialGroup()
				.addComponent(clearButton));
		clearlayout.setVerticalGroup(clearlayout.createSequentialGroup()
				.addComponent(clearButton));

		return clearPanel;
	}

	private JPanel mappingPanel() {

		simulationPanel = new SimulationJPanel();
		simulationPanel.setSSG(this);
		simulationPanel.setSize(20000, 20000);
		simulationPanel.setBackground(Color.WHITE);
		simulationPanel.setBorder(createBorder());

		mappingPanel = new JPanel();
		mappingPanel.setBorder(BorderFactory.createTitledBorder(createBorder(), "Simulator"));
		mappingPanel.setOpaque(false);

		GroupLayout mappingLayout = new GroupLayout(mappingPanel);
		mappingPanel.setLayout(mappingLayout);
		mappingLayout.setAutoCreateGaps(true);
		mappingLayout.setAutoCreateContainerGaps(true);
		mappingLayout.setHorizontalGroup(mappingLayout.createSequentialGroup()
				.addComponent(simulationPanel));
		mappingLayout.setVerticalGroup(mappingLayout.createSequentialGroup()
				.addComponent(simulationPanel));

		return mappingPanel;
	}

	public void updateCoordinates(String s) {
		mappingPanel.setBorder(BorderFactory.createTitledBorder(createBorder(), s));

	}

	private static JPanel consolePanel() {
		textArea = new JTextArea(5,20);
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);

		scrollPane.setBorder(createBorder());

		JPanel consolePanel = new JPanel();
		consolePanel.setBorder(BorderFactory.createTitledBorder(createBorder(), "Output"));
		consolePanel.setOpaque(false);

		GroupLayout consolelayout = new GroupLayout(consolePanel);
		consolePanel.setLayout(consolelayout);
		consolelayout.setAutoCreateGaps(true);
		consolelayout.setAutoCreateContainerGaps(true);
		consolelayout.setHorizontalGroup(consolelayout.createSequentialGroup()
				.addComponent(scrollPane));
		consolelayout.setVerticalGroup(consolelayout.createSequentialGroup()
				.addComponent(scrollPane));

		return consolePanel;
	}
	
	private static void redirectSystemStreams() {
		OutputStream out = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				textArea.append((String.valueOf((char) b)));
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				textArea.append((new String(b, off, len)));
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};
		System.setOut(new PrintStream(out, true));
		System.setErr(new PrintStream(out, true));
	}

	private static void addListeners() {
		bluetoothConnect.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0) {}

			@Override
			public void mousePressed(MouseEvent arg0) {}

			@Override
			public void mouseExited(MouseEvent arg0) {}

			@Override
			public void mouseEntered(MouseEvent arg0) {}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(onManual) {
					onManual = false;
					System.out.println("[GUI] Switched to GUI control.");
				}
				if(bluetoothConnect.getText() == "Connect") {
					prevCommunicator = unitCommunicator;
					unitCommunicator = new RobotCommunicator(informationBuffer);
					try {
						unitCommunicator.openUnitConnection();
						bluetoothConnect.setText("Disconnect");
						bluetoothStatus.setIcon(bluetoothConnectedIcon);
						System.out.println("[CONNECTION] Connection established.");
					} catch (IOException e) {
						unitCommunicator = prevCommunicator;
						System.out.println("[CONNECTION] Oops! Something went wrong connecting! \n[CONNECTION] Please make sure your robot and bluetooth are turned on.");
					}

				}
				else if(bluetoothConnect.getText() == "Disconnect") {
					try {
						unitCommunicator.closeUnitConnection();
						unitCommunicator = prevCommunicator;
						bluetoothConnect.setText("Connect");
						bluetoothStatus.setIcon(bluetoothNotConnectedIcon);
						System.out.println("[CONNECTION] Connection succesfully closed. Entered simulator mode.");
					} catch (Exception e) {
						System.out.println("[CONNECTION] Oops! Something went wrong disconnecting!");
					}

				}
			}
		});
		polygondraw.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0) {}

			@Override
			public void mousePressed(MouseEvent arg0) {}

			@Override
			public void mouseExited(MouseEvent arg0) {}

			@Override
			public void mouseEntered(MouseEvent arg0) {}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(onManual) {
					onManual = false;
					System.out.println("[GUI] Switched to GUI control.");
				}
				PolygonDrawThread PDT = new PolygonDrawThread("PDT");
				PDT.setUnitCommunicator(unitCommunicator);
				PDT.setAngles((int)polygonangles.getValue());
				PDT.setLength(Integer.parseInt(polygonEdgeLength.getValue().toString()));
				PDT.start();
			}
		});
		uparrow.addMouseListener(new MouseListener() {
			MouseClickThread MCT;

			@Override
			public void mouseReleased(MouseEvent arg0) {
				try {
					if(unitCommunicator instanceof SimulatorCommunicator)
						MCT.setRide(false);
					unitCommunicator.sendCommandToUnit(Command.FORWARD_RELEASED);
					uparrow.setIcon(uparrowicon);
				} catch (IOException e) {

				}
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				if(onManual) {
					onManual = false;
					System.out.println("[GUI] Switched to GUI control.");
				}
				if(unitCommunicator instanceof SimulatorCommunicator) {
					MCT = new MouseClickThread("MCT");
					MCT.setUnitCommunicator(unitCommunicator);
					MCT.setCommand(Command.FORWARD_PRESSED);
					MCT.setSpeed(unitCommunicator.getSpeed());
					MCT.start();					
				}
				else if(unitCommunicator instanceof RobotCommunicator) {
					try {
						unitCommunicator.sendCommandToUnit(Command.FORWARD_PRESSED);
					} catch (IOException e) {

					}
				}
				uparrow.setIcon(uparrowpressedicon);
			}

			@Override
			public void mouseExited(MouseEvent arg0) {}

			@Override
			public void mouseEntered(MouseEvent arg0) {}

			@Override
			public void mouseClicked(MouseEvent arg0) {}
		});
		downarrow.addMouseListener(new MouseListener() {
			MouseClickThread MCT;

			@Override
			public void mouseReleased(MouseEvent arg0) {
				try {
					if(unitCommunicator instanceof SimulatorCommunicator)
						MCT.setRide(false);
					unitCommunicator.sendCommandToUnit(Command.BACKWARD_RELEASED);
					downarrow.setIcon(downarrowicon);
				} catch (IOException e) {

				}
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				if(onManual) {
					onManual = false;
					System.out.println("[GUI] Switched to GUI control.");
				}
				if(unitCommunicator instanceof SimulatorCommunicator) {
					MCT = new MouseClickThread("MCT");
					MCT.setUnitCommunicator(unitCommunicator);
					MCT.setCommand(Command.BACKWARD_PRESSED);
					MCT.setSpeed(unitCommunicator.getSpeed());
					MCT.start();
				}
				else if(unitCommunicator instanceof RobotCommunicator) {
					try {
						unitCommunicator.sendCommandToUnit(Command.BACKWARD_PRESSED);
					} catch (IOException e) {

					}
				}
				downarrow.setIcon(downarrowpressedicon);
			}

			@Override
			public void mouseExited(MouseEvent arg0) {}

			@Override
			public void mouseEntered(MouseEvent arg0) {}

			@Override
			public void mouseClicked(MouseEvent arg0) {}
		});
		leftarrow.addMouseListener(new MouseListener() {
			MouseClickThread MCT;

			@Override
			public void mouseReleased(MouseEvent arg0) {
				try {
					if(unitCommunicator instanceof SimulatorCommunicator)
						MCT.setRide(false);
					unitCommunicator.sendCommandToUnit(Command.LEFT_RELEASED);
					leftarrow.setIcon(leftarrowicon);
				} catch (IOException e) {

				}
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				if(onManual) {
					onManual = false;
					System.out.println("[GUI] Switched to GUI control.");
				}
				if(unitCommunicator instanceof SimulatorCommunicator) {
					MCT = new MouseClickThread("MCT");
					MCT.setUnitCommunicator(unitCommunicator);
					MCT.setCommand(Command.LEFT_PRESSED);
					MCT.setSpeed(unitCommunicator.getSpeed());
					MCT.start();
				}
				else if(unitCommunicator instanceof RobotCommunicator) {
					try {
						unitCommunicator.sendCommandToUnit(Command.LEFT_PRESSED);
					} catch (IOException e) {

					}
				}
				leftarrow.setIcon(leftarrowpressedicon);
			}

			@Override
			public void mouseExited(MouseEvent arg0) {}

			@Override
			public void mouseEntered(MouseEvent arg0) {}

			@Override
			public void mouseClicked(MouseEvent arg0) {}
		});
		rightarrow.addMouseListener(new MouseListener() {
			MouseClickThread MCT;

			@Override
			public void mouseReleased(MouseEvent arg0) {
				try {
					if(unitCommunicator instanceof SimulatorCommunicator)
						MCT.setRide(false);
					unitCommunicator.sendCommandToUnit(Command.RIGHT_RELEASED);
					rightarrow.setIcon(rightarrowicon);
				} catch (IOException e) {

				}
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				if(onManual) {
					onManual = false;
					System.out.println("[GUI] Switched to GUI control.");
				}
				if(unitCommunicator instanceof SimulatorCommunicator) {
					MCT = new MouseClickThread("MCT");
					MCT.setUnitCommunicator(unitCommunicator);
					MCT.setCommand(Command.RIGHT_PRESSED);
					MCT.setSpeed(unitCommunicator.getSpeed());
					MCT.start();
				}
				else if(unitCommunicator instanceof RobotCommunicator) {
					try {
						unitCommunicator.sendCommandToUnit(Command.RIGHT_PRESSED);
					} catch (IOException e) {

					}
				}
				rightarrow.setIcon(rightarrowpressedicon);
			}

			@Override
			public void mouseExited(MouseEvent arg0) {}

			@Override
			public void mouseEntered(MouseEvent arg0) {}

			@Override
			public void mouseClicked(MouseEvent arg0) {}
		});
		speedbutton.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0) {}

			@Override
			public void mousePressed(MouseEvent arg0) {}

			@Override
			public void mouseExited(MouseEvent arg0) {}

			@Override
			public void mouseEntered(MouseEvent arg0) {}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(onManual) {
					onManual = false;
					System.out.println("[GUI] Switched to GUI control.");
				}
				unitCommunicator.setSpeed(speedvalues.getValue());
				simulationPanel.requestFocusInWindow();
			}
		});
		focus.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseClicked(MouseEvent e) {
				onManual = true;
				System.out.println("[GUI] Switched to manual control.");
				simulationPanel.requestFocusInWindow();
			}
		});
		clearButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseClicked(MouseEvent e) {
				onManual = true;
				System.out.println("[GUI] Screen cleared.");
				simulationPanel.clear();
				simulationPanel.requestFocusInWindow();

			}
		});
		simulationPanel.addKeyListener(new KeyListener() {
			MouseClickThread MCTU;
			MouseClickThread MCTD;
			MouseClickThread MCTL;
			MouseClickThread MCTR;
			boolean runningU = false;
			boolean runningD = false;
			boolean runningL = false;
			boolean runningR = false;

			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					if(e.getKeyCode()==KeyEvent.VK_UP) {
						if(!runningD) {		
							if(unitCommunicator instanceof SimulatorCommunicator)
								MCTU.setRide(false);
							runningU = false;
							unitCommunicator.sendCommandToUnit(Command.FORWARD_RELEASED);
							uparrow.setIcon(uparrowicon);
						}
					}
					else if(e.getKeyCode()==KeyEvent.VK_DOWN) {
						if(!runningU) {	
							if(unitCommunicator instanceof SimulatorCommunicator)	
								MCTD.setRide(false);
							runningD = false;
							unitCommunicator.sendCommandToUnit(Command.BACKWARD_RELEASED);
							downarrow.setIcon(downarrowicon);
						}
					}
					else if(e.getKeyCode()==KeyEvent.VK_LEFT) {
						if(!runningR) {	
							if(unitCommunicator instanceof SimulatorCommunicator)	
								MCTL.setRide(false);
							runningL = false;
							unitCommunicator.sendCommandToUnit(Command.LEFT_RELEASED);
							leftarrow.setIcon(leftarrowicon);
						}
					}
					else if(e.getKeyCode()==KeyEvent.VK_RIGHT) {
						if(!runningL) {	
							if(unitCommunicator instanceof SimulatorCommunicator)	
								MCTR.setRide(false);
							runningR = false;
							unitCommunicator.sendCommandToUnit(Command.RIGHT_RELEASED);
							rightarrow.setIcon(rightarrowicon);
						}
					}
				} catch(IOException ex) {

				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_UP) {
					if(!runningD) {
						if(!runningU && unitCommunicator instanceof SimulatorCommunicator) {
							MCTU = new MouseClickThread("MCTU");
							MCTU.setUnitCommunicator(unitCommunicator);
							MCTU.setCommand(Command.FORWARD_PRESSED);
							MCTU.setSpeed(unitCommunicator.getSpeed());
							MCTU.start();
						}
						else if(!runningU && unitCommunicator instanceof RobotCommunicator) {
							try {
								unitCommunicator.sendCommandToUnit(Command.FORWARD_PRESSED);
							} catch (IOException ex) {

							}
						}
						runningU = true;
						uparrow.setIcon(uparrowpressedicon);
					}
				}
				else if(e.getKeyCode()==KeyEvent.VK_DOWN) {
					if(!runningU) {
						if(!runningD && unitCommunicator instanceof SimulatorCommunicator) {
							MCTD = new MouseClickThread("MCTD");
							MCTD.setUnitCommunicator(unitCommunicator);
							MCTD.setCommand(Command.BACKWARD_PRESSED);
							MCTD.setSpeed(unitCommunicator.getSpeed());
							MCTD.start();
						}
						else if(!runningD && unitCommunicator instanceof RobotCommunicator) {
							try {
								unitCommunicator.sendCommandToUnit(Command.BACKWARD_PRESSED);
							} catch (IOException ex) {

							}
						}
						runningD = true;
						downarrow.setIcon(downarrowpressedicon);
					}
				}
				else if(e.getKeyCode()==KeyEvent.VK_LEFT) {
					if(!runningR) {
						if(!runningL && unitCommunicator instanceof SimulatorCommunicator) {
							MCTL = new MouseClickThread("MCTL");
							MCTL.setUnitCommunicator(unitCommunicator);
							MCTL.setCommand(Command.LEFT_PRESSED);
							MCTL.setSpeed(unitCommunicator.getSpeed());
							MCTL.start();
						}
						else if(!runningL && unitCommunicator instanceof RobotCommunicator) {
							try {
								unitCommunicator.sendCommandToUnit(Command.LEFT_PRESSED);
							} catch (IOException ex) {

							}
						}
						runningL = true;
						leftarrow.setIcon(leftarrowpressedicon);
					}
				}
				else if(e.getKeyCode()==KeyEvent.VK_RIGHT) {
					if(!runningL) {
						if(!runningR && unitCommunicator instanceof SimulatorCommunicator) {
							MCTR = new MouseClickThread("MCTR");
							MCTR.setUnitCommunicator(unitCommunicator);
							MCTR.setCommand(Command.RIGHT_PRESSED);
							MCTR.setSpeed(unitCommunicator.getSpeed());
							MCTR.start();
						}
						else if(!runningR && unitCommunicator instanceof RobotCommunicator) {
							try {
								unitCommunicator.sendCommandToUnit(Command.RIGHT_PRESSED);
							} catch (IOException ex) {

							}
						}
						runningR = true;
						rightarrow.setIcon(rightarrowpressedicon);
					}
				}
			}
		});
	}

	private static javax.swing.border.Border createBorder() {
		return BorderFactory.createEtchedBorder(1);
	}


	
	public static void main(String[] args) {
		SilverSurferGUI SSG = new SilverSurferGUI();
		SSG.createAndShowGUI();
		
	}
}