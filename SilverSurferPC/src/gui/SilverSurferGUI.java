package gui;

import commands.Command;
import communication.*;
import simulator.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

public class SilverSurferGUI {

	private static JFrame frame;
	private static SimulationJPanel simulationPanel;
	private static StatusInfoBuffer informationBuffer;

	private static UnitCommunicator unitCommunicator;
	private static UnitCommunicator prevCommunicator;

	private static JButton bluetoothConnect;
	private static JLabel bluetoothStatus;
	private static ImageIcon bluetoothNotConnectedIcon = new ImageIcon(
			"resources/bluetooth_icons/bluetooth_no_connection.png");
	private static ImageIcon bluetoothConnectedIcon = new ImageIcon(
	"resources/bluetooth_icons/bluetooth_connected.png");

	private static JSpinner polygonEdgeLength;
	private static JSlider polygonangles;
	private static JButton resetpolygonButton;
	private static JButton polygondraw;

	private static JButton uparrow;
	private static JButton downarrow;
	private static JButton leftarrow;
	private static JButton rightarrow;
	private static ImageIcon uparrowicon = new ImageIcon(
			"resources/round_grey_arrows/arrow-up.png");
	private static ImageIcon uparrowpressedicon = new ImageIcon(
	"resources/round_grey_arrows/arrow-up-pressed.png");
	private static ImageIcon leftarrowicon = new ImageIcon(
	"resources/round_grey_arrows/arrow-left.png");
	private static ImageIcon leftarrowpressedicon = new ImageIcon(
	"resources/round_grey_arrows/arrow-left-pressed.png");
	private static ImageIcon downarrowicon = new ImageIcon(
	"resources/round_grey_arrows/arrow-down.png");
	private static ImageIcon downarrowpressedicon = new ImageIcon(
	"resources/round_grey_arrows/arrow-down-pressed.png");
	private static ImageIcon rightarrowicon = new ImageIcon(
	"resources/round_grey_arrows/arrow-right.png");
	private static ImageIcon rightarrowpressedicon = new ImageIcon(
	"resources/round_grey_arrows/arrow-right-pressed.png");

	private static JLabel speedLabel;
	private static JSlider speedvalues;
	private static JButton resetspeedButton;
	private static JButton speedButton;

	private static JButton clearButton;

	private static JButton alignWhiteLineButton;
	private static JButton alignWall;

	private static JSpinner angle;
	private static JButton turnLeftButton;
	private static JButton turnRightButton;
	private static JSpinner length;
	private static JButton moveButton;

	private static JButton lookAroundButton;

	private static JTextArea textArea;

	private static JPanel mappingPanel;
	private static JPanel consolePanel;

	private static boolean robotConnected = false;

	private void createAndShowGUI() {
		informationBuffer = new StatusInfoBuffer();
		informationBuffer.setSSG(this);

		frame = new JFrame("Silver Surfer Command Center");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setBackground(new Color(221, 230, 231));

		JPanel directionPanel = directionPanel();
		JPanel bluetoothPanel = bluetoothPanel();
		JPanel polygonPanel = polygonPanel();
		JPanel arrowPanel = arrowPanel();
		JPanel speedPanel = speedPanel();
		JPanel clearPanel = clearPanel();
		JPanel alignPanel = alignPanel();
		JPanel lookAroundPanel = lookAroundPanel();
		JPanel mappingPanel = mappingPanel();
		JPanel consolePanel = consolePanel();

		redirectSystemStreams();

		GUIMenuBar bar = new GUIMenuBar(this);
		frame.setJMenuBar(bar);

		GroupLayout frameLayout = new GroupLayout(frame.getContentPane());
		frame.getContentPane().setLayout(frameLayout);
		frameLayout.setHorizontalGroup(frameLayout
				.createSequentialGroup()
				.addGroup(
						frameLayout
						.createParallelGroup(
								GroupLayout.Alignment.CENTER)
								.addComponent(bluetoothPanel)
								.addComponent(polygonPanel)
								.addComponent(arrowPanel)
								.addComponent(speedPanel)
								.addComponent(clearPanel))
								.addGroup(
										frameLayout
										.createParallelGroup(
												GroupLayout.Alignment.CENTER)
												.addComponent(mappingPanel)
												.addComponent(consolePanel))
												.addGroup(
														frameLayout
														.createParallelGroup(
																GroupLayout.Alignment.CENTER)
																.addComponent(directionPanel)
																.addComponent(lookAroundPanel)
																.addComponent(alignPanel)));
		frameLayout.setVerticalGroup(frameLayout
				.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addGroup(
						frameLayout.createSequentialGroup()
						.addComponent(bluetoothPanel)
						.addComponent(polygonPanel)
						.addComponent(arrowPanel)
						.addComponent(speedPanel)
						.addComponent(clearPanel))
						.addGroup(
								frameLayout.createSequentialGroup()
								.addComponent(mappingPanel)
								.addComponent(consolePanel))
								.addGroup(
										frameLayout.createSequentialGroup()
										.addComponent(directionPanel)
										.addComponent(lookAroundPanel)
										.addComponent(alignPanel)));
		frameLayout.linkSize(SwingConstants.HORIZONTAL, polygonPanel, speedPanel);
		frameLayout.linkSize(SwingConstants.HORIZONTAL, directionPanel);
		frameLayout.linkSize(SwingConstants.VERTICAL, polygonPanel, consolePanel);
		frameLayout.linkSize(SwingConstants.VERTICAL, directionPanel);

		frame.pack();
		frame.setSize(1000, 800);
		frame.setVisible(true);

		unitCommunicator = new SimulatorCommunicator(informationBuffer);
		try {
			unitCommunicator.openUnitConnection();
			System.out.println("[CONNECTION] Entered simulator mode.");
		} catch (IOException e) {
			System.out.println("[CONNECTION] Oops! Something went wrong initializing!");
		}
		getSimulationPanel().setSimulationPilot(((SimulatorCommunicator) unitCommunicator).getSim());
		addListeners();
		simulationPanel.requestFocusInWindow();
		unitCommunicator.setSpeed(speedvalues.getValue());

		updateStatus();
	}

	public JFrame getFrame() {
		return frame;
	}

	public SimulationJPanel getSimulationPanel() {
		return simulationPanel;
	}

	private JPanel bluetoothPanel() {
		bluetoothConnect = new JButton("Connect");

		bluetoothStatus = new JLabel(bluetoothNotConnectedIcon);

		JPanel bluetoothPanel = new JPanel();
		bluetoothPanel.setBorder(BorderFactory.createTitledBorder(
				createBorder(), "Bluetooth"));
		bluetoothPanel.setOpaque(false);

		GroupLayout bluetoothlayout = new GroupLayout(bluetoothPanel);
		bluetoothPanel.setLayout(bluetoothlayout);
		bluetoothlayout.setAutoCreateGaps(true);
		bluetoothlayout.setAutoCreateContainerGaps(true);
		bluetoothlayout.setHorizontalGroup(bluetoothlayout
				.createSequentialGroup().addComponent(bluetoothConnect)
				.addComponent(bluetoothStatus));
		bluetoothlayout.setVerticalGroup(bluetoothlayout
				.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(bluetoothConnect).addComponent(bluetoothStatus));

		return bluetoothPanel;
	}

	private JPanel polygonPanel() {
		JLabel polygonAnglesLabel = new JLabel("Angles", JLabel.CENTER);

		polygonangles = new JSlider(JSlider.HORIZONTAL, 2, 32, 4);
		polygonangles.setSnapToTicks(true);
		polygonangles.setMajorTickSpacing(3);
		polygonangles.setMinorTickSpacing(1);
		polygonangles.setPaintTicks(true);
		polygonangles.setPaintLabels(true);
		polygonangles.setOpaque(false);

		JLabel polygonEdgeLengthLabel = new JLabel("Edge Length (centimeters)",
				JLabel.CENTER);

		SpinnerNumberModel polygonEdgeLengthModel = new SpinnerNumberModel(10,
				0, 1000, 1);
		polygonEdgeLength = new JSpinner(polygonEdgeLengthModel);

		resetpolygonButton = new JButton("Reset");

		polygondraw = new JButton("Execute polygon");

		JPanel polygonPanel = new JPanel();
		polygonPanel.setBorder(BorderFactory.createTitledBorder(createBorder(),
				"Polygon"));
		polygonPanel.setOpaque(false);

		GroupLayout polygonlayout = new GroupLayout(polygonPanel);
		polygonPanel.setLayout(polygonlayout);
		polygonlayout.setAutoCreateGaps(true);
		polygonlayout.setAutoCreateContainerGaps(true);
		polygonlayout.setHorizontalGroup(polygonlayout
				.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addGroup(
						polygonlayout
						.createParallelGroup(
								GroupLayout.Alignment.CENTER)
								.addComponent(polygonAnglesLabel)
								.addComponent(polygonangles))
								.addGroup(
										polygonlayout
										.createParallelGroup(
												GroupLayout.Alignment.CENTER)
												.addComponent(polygonEdgeLengthLabel)
												.addComponent(polygonEdgeLength))
												.addGroup(
														polygonlayout.createSequentialGroup()
														.addComponent(resetpolygonButton)
														.addComponent(polygondraw)));
		polygonlayout.setVerticalGroup(polygonlayout
				.createSequentialGroup()
				.addGroup(
						polygonlayout.createSequentialGroup()
						.addComponent(polygonAnglesLabel)
						.addComponent(polygonangles))
						.addGroup(
								polygonlayout.createSequentialGroup()
								.addComponent(polygonEdgeLengthLabel)
								.addComponent(polygonEdgeLength))
								.addGroup(
										polygonlayout
										.createParallelGroup(
												GroupLayout.Alignment.CENTER)
												.addComponent(resetpolygonButton)
												.addComponent(polygondraw)));

		return polygonPanel;
	}

	private JPanel arrowPanel() {
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
		arrowPanel.setBorder(BorderFactory.createTitledBorder(createBorder(),
				"GUI control"));
		arrowPanel.setOpaque(false);

		GroupLayout arrowlayout = new GroupLayout(arrowPanel);
		arrowPanel.setLayout(arrowlayout);
		arrowlayout.setAutoCreateGaps(true);
		arrowlayout.setAutoCreateContainerGaps(true);
		arrowlayout.setHorizontalGroup(arrowlayout
				.createSequentialGroup()
				.addComponent(leftarrow)
				.addGroup(
						arrowlayout
						.createParallelGroup(
								GroupLayout.Alignment.CENTER)
								.addComponent(uparrow).addComponent(downarrow))
								.addComponent(rightarrow));
		arrowlayout.setVerticalGroup(arrowlayout
				.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(leftarrow)
				.addGroup(
						arrowlayout.createSequentialGroup()
						.addComponent(uparrow).addComponent(downarrow))
						.addComponent(rightarrow));

		return arrowPanel;
	}

	private JPanel speedPanel() {
		speedLabel = new JLabel("Current Speed Level: 2", JLabel.CENTER);

		speedvalues = new JSlider(JSlider.HORIZONTAL, 1, 4, 2);
		speedvalues.setSnapToTicks(true);
		speedvalues.setMajorTickSpacing(1);
		speedvalues.setMinorTickSpacing(1);
		speedvalues.setPaintTicks(true);
		speedvalues.setPaintLabels(true);
		speedvalues.setOpaque(false);

		resetspeedButton = new JButton("Reset");

		speedButton = new JButton("Change speed");

		JPanel speedPanel = new JPanel();
		speedPanel.setBorder(BorderFactory.createTitledBorder(createBorder(),
				"Speed"));
		speedPanel.setOpaque(false);

		GroupLayout speedlayout = new GroupLayout(speedPanel);
		speedPanel.setLayout(speedlayout);
		speedlayout.setAutoCreateGaps(true);
		speedlayout.setAutoCreateContainerGaps(true);
		speedlayout.setHorizontalGroup(speedlayout
				.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(speedLabel)
				.addComponent(speedvalues)
				.addGroup(
						speedlayout.createSequentialGroup()
						.addComponent(resetspeedButton)
						.addComponent(speedButton)));
		speedlayout.setVerticalGroup(speedlayout
				.createSequentialGroup()
				.addComponent(speedLabel)
				.addComponent(speedvalues)
				.addGroup(
						speedlayout
						.createParallelGroup(
								GroupLayout.Alignment.CENTER)
								.addComponent(resetspeedButton)
								.addComponent(speedButton)));

		return speedPanel;
	}

	private JPanel clearPanel() {
		clearButton = new JButton("Clear screen");

		JPanel clearPanel = new JPanel();
		clearPanel.setBorder(BorderFactory.createTitledBorder(createBorder(),
				"Clear screen"));
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

	private JPanel directionPanel() {
		JLabel angleLabel = new JLabel("Angle (degrees)", JLabel.CENTER);

		SpinnerNumberModel angleModel = new SpinnerNumberModel(90, 0, 1080, 1);
		angle = new JSpinner(angleModel);

		turnLeftButton = new JButton("Turn left"); // "\u00b0" is de escape sequence voor het graden symbool
		turnRightButton = new JButton("Turn right");

		JLabel lengthLabel = new JLabel("Length (centimeters)", JLabel.CENTER);

		SpinnerNumberModel lenghtModel = new SpinnerNumberModel(26, 0, 1000, 1);
		length = new JSpinner(lenghtModel);

		moveButton = new JButton("Move");

		JPanel directionPanel = new JPanel();
		directionPanel.setBorder(BorderFactory.createTitledBorder(
				createBorder(), "Turn and Move"));
		directionPanel.setOpaque(false);

		GroupLayout directionLayout = new GroupLayout(directionPanel);
		directionPanel.setLayout(directionLayout);
		directionLayout.setAutoCreateGaps(true);
		directionLayout.setAutoCreateContainerGaps(true);
		directionLayout.setHorizontalGroup(directionLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(angleLabel)
				.addComponent(angle)
				.addGroup(directionLayout.createSequentialGroup()
						.addComponent(turnLeftButton)
						.addComponent(turnRightButton))
						.addComponent(lengthLabel)
						.addComponent(length)
						.addComponent(moveButton));
		directionLayout.setVerticalGroup(directionLayout.createSequentialGroup()
				.addComponent(angleLabel)
				.addComponent(angle)
				.addGroup(directionLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(turnLeftButton)
						.addComponent(turnRightButton))
						.addComponent(lengthLabel)
						.addComponent(length)
						.addComponent(moveButton));

		return directionPanel;
	}

	private JPanel lookAroundPanel() {
		lookAroundButton = new JButton("Look Around");

		JPanel lookAroundPanel = new JPanel();
		lookAroundPanel.setBorder(BorderFactory.createTitledBorder(
				createBorder(), "Look for obstructions"));
		lookAroundPanel.setOpaque(false);

		GroupLayout lookAroundLayout = new GroupLayout(lookAroundPanel);
		lookAroundPanel.setLayout(lookAroundLayout);
		lookAroundLayout.setAutoCreateGaps(true);
		lookAroundLayout.setAutoCreateContainerGaps(true);
		lookAroundLayout.setHorizontalGroup(lookAroundLayout
				.createSequentialGroup().addComponent(lookAroundButton));
		lookAroundLayout.setVerticalGroup(lookAroundLayout
				.createSequentialGroup().addComponent(lookAroundButton));

		return lookAroundPanel;
	}

	private JPanel mappingPanel() {
		simulationPanel = new SimulationJPanel();
		simulationPanel.setSSG(this);
		simulationPanel.setSize(20000, 20000);
		simulationPanel.setBackground(Color.WHITE);
		simulationPanel.setBorder(createBorder());

		mappingPanel = new JPanel();
		mappingPanel.setBorder(BorderFactory.createTitledBorder(createBorder(),
				"Simulator"));
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
		mappingPanel.setBorder(BorderFactory.createTitledBorder(createBorder(),
				s));
	}

	private JPanel consolePanel() {
		textArea = new JTextArea(5, 20);
		textArea.setEditable(false);
		textArea.setFocusable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);

		scrollPane.setBorder(createBorder());

		consolePanel = new JPanel();
		consolePanel.setBorder(BorderFactory.createTitledBorder(createBorder(),
				"Output"));
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

	private JPanel alignPanel() {
		alignWhiteLineButton = new JButton("Align on white line");
		alignWall = new JButton("Align on walls");

		JPanel alignPanel = new JPanel();
		alignPanel.setBorder(BorderFactory.createTitledBorder(createBorder(),
				"Align"));
		alignPanel.setOpaque(false);

		GroupLayout alignLayout = new GroupLayout(alignPanel);
		alignPanel.setLayout(alignLayout);
		alignLayout.setAutoCreateGaps(true);
		alignLayout.setAutoCreateContainerGaps(true);
		alignLayout.setHorizontalGroup(alignLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(alignWhiteLineButton)
				.addComponent(alignWall));
		alignLayout.setVerticalGroup(alignLayout.createSequentialGroup()
				.addComponent(alignWhiteLineButton)
				.addComponent(alignWall));

		return alignPanel;
	}

	// oude versie van de methode;  
	//    public void updateStatus() {
	//        String s = new String(
	//        		"(US: " + informationBuffer.getLatestUltraSensorInfo()
	//        		+ ", LS: " + informationBuffer.getLatestLightSensorInfo()
	//        		+ ", TS1: " + informationBuffer.getLatestTouchSensor1Info()
	//              + ", TS2: " + informationBuffer.getLatestTouchSensor2Info()
	//              + ", LM: " + informationBuffer.getLeftMotorMoving() + " " + informationBuffer.getLeftMotorSpeed()
	//              + ", RM: " + informationBuffer.getRightMotorMoving() + " " + informationBuffer.getRightMotorSpeed()
	//              + ", B: " + informationBuffer.getBusy() + ")");
	//        consolePanel.setBorder(BorderFactory.createTitledBorder(createBorder(),"Output " + s));
	//        
	//        if(robotConnected){
	//        	((SimulatorCommunicator)prevCommunicator).getSim().updateArc(informationBuffer.getLatestUltraSensorInfo());
	//        }
	//    }

	public void updateStatus() {
		String s = new String(
				"(US: " + this.getSimulationPanel().getSimulationPilot().getUltraSensorValue()
				+ ", LS: " + this.getSimulationPanel().getSimulationPilot().getLightSensorValue()
				+ ", TS1: " + this.getSimulationPanel().getSimulationPilot().getTouchSensor1Value()
				+ ", TS2: " + this.getSimulationPanel().getSimulationPilot().getTouchSensor2Value()
				+ ", LM: " + informationBuffer.getLeftMotorMoving() + " " + informationBuffer.getLeftMotorSpeed()
				+ ", RM: " + informationBuffer.getRightMotorMoving() + " " + informationBuffer.getRightMotorSpeed()
				+ ", B: " + informationBuffer.getBusy() + ")");
		consolePanel.setBorder(BorderFactory.createTitledBorder(createBorder(),"Output " + s));

		if(robotConnected){
			((SimulatorCommunicator)prevCommunicator).getSim().updateArc(this.getSimulationPanel().getSimulationPilot().getUltraSensorValue());
		}
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

	protected static void disconnectBluetooth() {
		try {
			unitCommunicator.sendCommandToUnit(16);
			unitCommunicator.closeUnitConnection();
			unitCommunicator = prevCommunicator;
			((SimulatorCommunicator)unitCommunicator).getSim().setRealRobot(false);
			bluetoothConnect.setText("Connect");
			bluetoothStatus.setIcon(bluetoothNotConnectedIcon);
			System.out.println("[CONNECTION] Connection succesfully closed. Entered simulator mode.");
			speedvalues.setValue(2);
			unitCommunicator.setSpeed(speedvalues.getValue());
			speedLabel.setText("Current Speed Level: 2");
			System.out.println(unitCommunicator.getConsoleTag() + " Current Speed Level: 2.");
			simulationPanel.requestFocusInWindow();
			robotConnected = false;
		} catch (Exception e) {
			robotConnected = true;
			System.out
			.println("[CONNECTION] Oops! Something went wrong disconnecting!");
			simulationPanel.requestFocusInWindow();
		}
	}

	protected static void connectBluetooth() {
		prevCommunicator = unitCommunicator;
		try {
			((SimulatorCommunicator)prevCommunicator).getSim().setRealRobot(true);
			robotConnected = true;
			unitCommunicator = new RobotCommunicator(informationBuffer);
			unitCommunicator.openUnitConnection();
			bluetoothConnect.setText("Disconnect");
			bluetoothStatus.setIcon(bluetoothConnectedIcon);
			System.out.println("[CONNECTION] Connection established.");
			speedvalues.setValue(2);
			unitCommunicator.setSpeed(speedvalues.getValue());
			prevCommunicator.setSpeed(speedvalues.getValue());
			speedLabel.setText("Current Speed Level: 2");
			System.out.println(unitCommunicator.getConsoleTag() + " Current Speed Level: 2.");
			simulationPanel.requestFocusInWindow();
		} catch (IOException e) {
			unitCommunicator = prevCommunicator;
			robotConnected = false;
			System.out.println("[CONNECTION] Oops! Something went wrong connecting! \n[CONNECTION] Please make sure your robot and bluetooth are turned on.");
			simulationPanel.requestFocusInWindow();
		}
	}

	private static void addListeners() {
		bluetoothConnect.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (bluetoothConnect.getText() == "Connect")
					connectBluetooth();
				else if (bluetoothConnect.getText() == "Disconnect")
					disconnectBluetooth();
			}

		});
		resetpolygonButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				polygonangles.setValue(4);
				polygonEdgeLength.setValue(10);
				simulationPanel.requestFocusInWindow();
			}
		});
		turnLeftButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				MoveTurnThread MTT = new MoveTurnThread("MTT");
				MTT.setUnitCommunicator(unitCommunicator);
				MTT.setAngles(-1*Integer.parseInt(angle.getValue().toString()));
				MTT.setLength(0);
				if (robotConnected) {
					MoveTurnThread MMT1 = new MoveTurnThread("MTT1");
					MMT1.setUnitCommunicator(prevCommunicator);
					MMT1.setAngles(-1*Integer.parseInt(angle.getValue().toString()));
					MMT1.setLength(0);
					MMT1.start();
				}
				MTT.start();
				simulationPanel.requestFocusInWindow();
			}
		});
		turnRightButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				MoveTurnThread MTT = new MoveTurnThread("MTT");
				MTT.setUnitCommunicator(unitCommunicator);
				MTT.setAngles(Integer.parseInt(angle.getValue().toString()));
				MTT.setLength(0);
				if (robotConnected) {
					MoveTurnThread MMT1 = new MoveTurnThread("MTT1");
					MMT1.setUnitCommunicator(prevCommunicator);
					MMT1.setAngles(Integer.parseInt(angle.getValue().toString()));
					MMT1.setLength(0);
					MMT1.start();
				}
				MTT.start();
				simulationPanel.requestFocusInWindow();
			}
		});
		moveButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				MoveTurnThread MTT = new MoveTurnThread("MTT");
				MTT.setUnitCommunicator(unitCommunicator);
				MTT.setAngles(0);
				MTT.setLength(Integer.parseInt(length.getValue().toString()));
				if (robotConnected) {
					MoveTurnThread MTT1 = new MoveTurnThread("MTT1");
					MTT1.setUnitCommunicator(prevCommunicator);
					MTT1.setAngles(0);
					MTT1.setLength(Integer.parseInt(length.getValue().toString()));
					MTT1.start();
				}
				MTT.start();
				simulationPanel.requestFocusInWindow();
			}
		});
		polygondraw.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				PolygonDrawThread PDT = new PolygonDrawThread("PDT");
				PDT.setUnitCommunicator(unitCommunicator);
				PDT.setAngles((int) polygonangles.getValue());
				PDT.setLength(Integer.parseInt(polygonEdgeLength.getValue().toString()));
				PDT.start();
				if (robotConnected) {
					PolygonDrawThread PDT1 = new PolygonDrawThread("PDT1");
					PDT1.setUnitCommunicator(prevCommunicator);
					PDT1.setAngles((int) polygonangles.getValue());
					PDT1.setLength(Integer.parseInt(polygonEdgeLength.getValue().toString()));
					PDT1.start();
				}
				simulationPanel.requestFocusInWindow();
			}
		});
		uparrow.addMouseListener(new MouseListener() {
			MouseClickThread MCT;

			@Override
			public void mouseReleased(MouseEvent arg0) {
				try {
					MCT.setQuit(true);
					unitCommunicator.sendCommandToUnit(Command.FORWARD_RELEASED);
					if (robotConnected)
						prevCommunicator.sendCommandToUnit(Command.FORWARD_RELEASED);
					uparrow.setIcon(uparrowicon);
					simulationPanel.requestFocusInWindow();
				} catch (IOException e) {

				}
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				MCT = new MouseClickThread("MCT");
				if (unitCommunicator instanceof SimulatorCommunicator) {
					MCT.setUnitCommunicator(unitCommunicator);
					MCT.setCommand(Command.FORWARD_PRESSED);
					MCT.setSpeed(unitCommunicator.getSpeed());
					MCT.start();
				} 
				else if (unitCommunicator instanceof RobotCommunicator) {
					try {
						unitCommunicator.sendCommandToUnit(Command.FORWARD_PRESSED);
						MCT.setUnitCommunicator(prevCommunicator);
						MCT.setCommand(Command.FORWARD_PRESSED);
						MCT.setSpeed(prevCommunicator.getSpeed());
						MCT.start();
					} catch (IOException e) {

					}
				}
				uparrow.setIcon(uparrowpressedicon);
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
		});
		downarrow.addMouseListener(new MouseListener() {
			MouseClickThread MCT;

			@Override
			public void mouseReleased(MouseEvent arg0) {
				try {
					MCT.setQuit(true);
					unitCommunicator.sendCommandToUnit(Command.BACKWARD_RELEASED);
					if (robotConnected)
						prevCommunicator.sendCommandToUnit(Command.BACKWARD_RELEASED);
					downarrow.setIcon(downarrowicon);
					simulationPanel.requestFocusInWindow();
				} catch (IOException e) {

				}
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				MCT = new MouseClickThread("MCT");
				if (unitCommunicator instanceof SimulatorCommunicator) {
					MCT.setUnitCommunicator(unitCommunicator);
					MCT.setCommand(Command.BACKWARD_PRESSED);
					MCT.setSpeed(unitCommunicator.getSpeed());
					MCT.start();
				}
				else if (unitCommunicator instanceof RobotCommunicator) {
					try {
						unitCommunicator.sendCommandToUnit(Command.BACKWARD_PRESSED);
						MCT.setUnitCommunicator(prevCommunicator);
						MCT.setCommand(Command.BACKWARD_PRESSED);
						MCT.setSpeed(prevCommunicator.getSpeed());
						MCT.start();
					} catch (IOException e) {

					}
				}
				downarrow.setIcon(downarrowpressedicon);
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
		});
		leftarrow.addMouseListener(new MouseListener() {
			MouseClickThread MCT;

			@Override
			public void mouseReleased(MouseEvent arg0) {
				try {
					MCT.setQuit(true);
					unitCommunicator.sendCommandToUnit(Command.LEFT_RELEASED);
					if (robotConnected)
						prevCommunicator.sendCommandToUnit(Command.LEFT_RELEASED);
					leftarrow.setIcon(leftarrowicon);
					simulationPanel.requestFocusInWindow();
				} catch (IOException e) {

				}
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				MCT = new MouseClickThread("MCT");
				if (unitCommunicator instanceof SimulatorCommunicator) {
					MCT.setUnitCommunicator(unitCommunicator);
					MCT.setCommand(Command.LEFT_PRESSED);
					MCT.setSpeed(unitCommunicator.getSpeed());
					MCT.start();
				}
				else if (unitCommunicator instanceof RobotCommunicator) {
					try {
						unitCommunicator.sendCommandToUnit(Command.LEFT_PRESSED);
						MCT.setUnitCommunicator(prevCommunicator);
						MCT.setCommand(Command.LEFT_PRESSED);
						MCT.setSpeed(prevCommunicator.getSpeed());
						MCT.start();
					} catch (IOException e) {

					}
				}
				leftarrow.setIcon(leftarrowpressedicon);
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
		});
		rightarrow.addMouseListener(new MouseListener() {
			MouseClickThread MCT;

			@Override
			public void mouseReleased(MouseEvent arg0) {
				try {
					MCT.setQuit(true);
					unitCommunicator.sendCommandToUnit(Command.RIGHT_RELEASED);
					if (robotConnected)
						prevCommunicator.sendCommandToUnit(Command.RIGHT_RELEASED);
					rightarrow.setIcon(rightarrowicon);
					simulationPanel.requestFocusInWindow();
				} catch (IOException e) {

				}
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				MCT = new MouseClickThread("MCT");
				if (unitCommunicator instanceof SimulatorCommunicator) {
					MCT.setUnitCommunicator(unitCommunicator);
					MCT.setCommand(Command.RIGHT_PRESSED);
					MCT.setSpeed(unitCommunicator.getSpeed());
					MCT.start();
				}
				else if (unitCommunicator instanceof RobotCommunicator) {
					try {
						unitCommunicator.sendCommandToUnit(Command.RIGHT_PRESSED);
						MCT.setUnitCommunicator(prevCommunicator);
						MCT.setCommand(Command.RIGHT_PRESSED);
						MCT.setSpeed(prevCommunicator.getSpeed());
						MCT.start();
					} catch (IOException e) {

					}
				}
				rightarrow.setIcon(rightarrowpressedicon);
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
		});
		resetspeedButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				speedvalues.setValue(2);
				unitCommunicator.setSpeed(speedvalues.getValue());
				if (robotConnected)
					prevCommunicator.setSpeed(speedvalues.getValue());
				speedLabel.setText("Current Speed Level: 2");
				System.out.println(unitCommunicator.getConsoleTag() + " Current Speed Level: 2.");
				simulationPanel.requestFocusInWindow();
			}
		});
		speedButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				unitCommunicator.setSpeed(speedvalues.getValue());
				if (robotConnected)
					prevCommunicator.setSpeed(speedvalues.getValue());
				speedLabel.setText("Current Speed Level: " + speedvalues.getValue());
				System.out.println(unitCommunicator.getConsoleTag() + " Current Speed Level: " + speedvalues.getValue() + ".");
				simulationPanel.requestFocusInWindow();
			}
		});
		clearButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("[GUI] Screen cleared.");
				simulationPanel.clear();
				simulationPanel.requestFocusInWindow();
			}
		});
		alignWhiteLineButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println(unitCommunicator.getConsoleTag() + " Aligning on white line.");
				AllignOnWhiteLineThread AWL = new AllignOnWhiteLineThread("AWL");
				AWL.setUnitCommunicator(unitCommunicator);
				AWL.setCommand(Command.ALIGN_PERPENDICULAR);
				if (robotConnected) {
					AllignOnWhiteLineThread AWL1 = new AllignOnWhiteLineThread("AWL1");
					AWL1.setUnitCommunicator(prevCommunicator);
					AWL1.setCommand(Command.ALIGN_PERPENDICULAR);
					AWL1.start();
				}
				AWL.start();
				simulationPanel.requestFocusInWindow();
			}
		});
		alignWall.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println(unitCommunicator.getConsoleTag() + " Aligning on walls.");
				AllignOnWhiteLineThread AWL = new AllignOnWhiteLineThread("AWL");
				AWL.setUnitCommunicator(unitCommunicator);
				AWL.setCommand(Command.ALIGN_WALL);
				if (robotConnected) {
					AllignOnWhiteLineThread AWL1 = new AllignOnWhiteLineThread("AWL1");
					AWL1.setUnitCommunicator(prevCommunicator);
					AWL1.setCommand(Command.ALIGN_WALL);
					AWL1.start();
				}
				AWL.start();
				simulationPanel.requestFocusInWindow();
			}
		});
		lookAroundButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println(unitCommunicator.getConsoleTag() + " Looking around for obstructions.");
				AllignOnWhiteLineThread AWL = new AllignOnWhiteLineThread("AWL");
				AWL.setUnitCommunicator(unitCommunicator);
				AWL.setCommand(Command.LOOK_AROUND);
				if (robotConnected) {
					AllignOnWhiteLineThread AWL1 = new AllignOnWhiteLineThread("AWL1");
					AWL1.setUnitCommunicator(prevCommunicator);
					AWL1.setCommand(Command.LOOK_AROUND);
					AWL1.start();
				}
				AWL.start();
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
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					if (e.getKeyCode() == KeyEvent.VK_UP) {
						if (!runningD) {
							MCTU.setQuit(true);
							runningU = false;
							unitCommunicator.sendCommandToUnit(Command.FORWARD_RELEASED);
							if (robotConnected)
								prevCommunicator.sendCommandToUnit(Command.FORWARD_RELEASED);
							uparrow.setIcon(uparrowicon);
						}
					} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
						if (!runningU) {
							MCTD.setQuit(true);
							runningD = false;
							unitCommunicator.sendCommandToUnit(Command.BACKWARD_RELEASED);
							if (robotConnected)
								prevCommunicator.sendCommandToUnit(Command.BACKWARD_RELEASED);
							downarrow.setIcon(downarrowicon);
						}
					} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
						if (!runningR) {
							MCTL.setQuit(true);
							runningL = false;
							unitCommunicator.sendCommandToUnit(Command.LEFT_RELEASED);
							if (robotConnected)
								prevCommunicator.sendCommandToUnit(Command.LEFT_RELEASED);
							leftarrow.setIcon(leftarrowicon);
						}
					} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
						if (!runningL) {
							MCTR.setQuit(true);
							runningR = false;
							unitCommunicator.sendCommandToUnit(Command.RIGHT_RELEASED);
							if (robotConnected)
								prevCommunicator.sendCommandToUnit(Command.RIGHT_RELEASED);
							rightarrow.setIcon(rightarrowicon);
						}
					}
				} catch (IOException ex) {

				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				try {
					if (e.getKeyCode() == KeyEvent.VK_UP) {
						if (!runningD) {
							if (!runningU && unitCommunicator instanceof SimulatorCommunicator) {
								MCTU = new MouseClickThread("MCTU");
								MCTU.setUnitCommunicator(unitCommunicator);
								MCTU.setCommand(Command.FORWARD_PRESSED);
								MCTU.setSpeed(unitCommunicator.getSpeed());
								MCTU.start();
							}
							else if (!runningU && unitCommunicator instanceof RobotCommunicator) {
								unitCommunicator.sendCommandToUnit(Command.FORWARD_PRESSED);
								MCTU = new MouseClickThread("MCTU");
								MCTU.setUnitCommunicator(prevCommunicator);
								MCTU.setCommand(Command.FORWARD_PRESSED);
								MCTU.setSpeed(prevCommunicator.getSpeed());
								MCTU.start();
							}
							runningU = true;
							uparrow.setIcon(uparrowpressedicon);
						}
					} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
						if (!runningU) {
							if (!runningD && unitCommunicator instanceof SimulatorCommunicator) {
								MCTD = new MouseClickThread("MCTD");
								MCTD.setUnitCommunicator(unitCommunicator);
								MCTD.setCommand(Command.BACKWARD_PRESSED);
								MCTD.setSpeed(unitCommunicator.getSpeed());
								MCTD.start();
							} else if (!runningD && unitCommunicator instanceof RobotCommunicator) {
								unitCommunicator.sendCommandToUnit(Command.BACKWARD_PRESSED);
								MCTD = new MouseClickThread("MCTD");
								MCTD.setUnitCommunicator(prevCommunicator);
								MCTD.setCommand(Command.BACKWARD_PRESSED);
								MCTD.setSpeed(prevCommunicator.getSpeed());
								MCTD.start();
							}
							runningD = true;
							downarrow.setIcon(downarrowpressedicon);
						}
					} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
						if (!runningR) {
							if (!runningL && unitCommunicator instanceof SimulatorCommunicator) {
								MCTL = new MouseClickThread("MCTL");
								MCTL.setUnitCommunicator(unitCommunicator);
								MCTL.setCommand(Command.LEFT_PRESSED);
								MCTL.setSpeed(unitCommunicator.getSpeed());
								MCTL.start();
							} else if (!runningL && unitCommunicator instanceof RobotCommunicator) {
								unitCommunicator.sendCommandToUnit(Command.LEFT_PRESSED);
								MCTL = new MouseClickThread("MCTL");
								MCTL.setUnitCommunicator(prevCommunicator);
								MCTL.setCommand(Command.LEFT_PRESSED);
								MCTL.setSpeed(prevCommunicator.getSpeed());
								MCTL.start();
							}
							runningL = true;
							leftarrow.setIcon(leftarrowpressedicon);
						}
					} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
						if (!runningL) {
							if (!runningR && unitCommunicator instanceof SimulatorCommunicator) {
								MCTR = new MouseClickThread("MCTR");
								MCTR.setUnitCommunicator(unitCommunicator);
								MCTR.setCommand(Command.RIGHT_PRESSED);
								MCTR.setSpeed(unitCommunicator.getSpeed());
								MCTR.start();
							} else if (!runningR && unitCommunicator instanceof RobotCommunicator) {
								unitCommunicator .sendCommandToUnit(Command.RIGHT_PRESSED);
								MCTR = new MouseClickThread("MCTR");
								MCTR.setUnitCommunicator(prevCommunicator);
								MCTR.setCommand(Command.RIGHT_PRESSED);
								MCTR.setSpeed(prevCommunicator.getSpeed());
								MCTR.start();
							}
							runningR = true;
							rightarrow.setIcon(rightarrowpressedicon);
						}
					}
				}
				catch (IOException ex) {

				}
			}
		});
	}

	private static javax.swing.border.Border createBorder() {
		return BorderFactory.createEtchedBorder(1);
	}

	public static UnitCommunicator getUnitCommunicator() {
		return unitCommunicator;
	}

	public static UnitCommunicator getPrevCommunicator() {
		return prevCommunicator;
	}

	public static StatusInfoBuffer getInformationBuffer() {
		return informationBuffer;
	}

	public static void main(String[] args) {
		SilverSurferGUI SSG = new SilverSurferGUI();
		SSG.createAndShowGUI();
	}
}