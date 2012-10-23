package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.imageio.ImageIO;
import javax.swing.*;

import commands.Command;
import communication.*;

import simulator.*;

//TODO:
//- Meerdere keys bij manuele simulator tegelijk indrukken werkt niet
//- Pijltjestoetsen blijven indrukken om vooruit te gaan werkt niet
//- Beter allign aan linkerkant nodig
//- Manual control icon?
//- Simulatieborder beter maken (niet volledig correct atm)
//- Dikkere simulatielijn + pijl voor richting (ev. vak met orientatie?)
//- Afstand afgelegd / hoek gedraait bij de pijltjestoetsen zetten?
//- Snelheid aanpassen
//- Simulator aan robot linken.

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
	
	private static JButton focus;
	
	private static JTextArea textArea;
	
	private static boolean onManual = false;
	
    private static void createAndShowGUI() {
        frame = new JFrame("Silver Surfer Command Center");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(221,230,231));
        
        JPanel bluetoothPanel = bluetoothPanel();
        JPanel polygonPanel = polygonPanel();
        JPanel arrowPanel = arrowPanel();
        JPanel focusPanel = focusPanel();
        JPanel mappingPanel = mappingPanel();
        JPanel consolePanel = consolePanel();
        redirectSystemStreams();

        GroupLayout frameLayout = new GroupLayout(frame.getContentPane());
        frame.getContentPane().setLayout(frameLayout);
        frameLayout.setHorizontalGroup(frameLayout.createSequentialGroup()
                .addGroup(frameLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                		.addComponent(bluetoothPanel)
                		.addComponent(polygonPanel)
                		.addComponent(arrowPanel)
                		.addComponent(focusPanel))
                .addGroup(frameLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                		.addComponent(mappingPanel)
                		.addComponent(consolePanel)));
        frameLayout.setVerticalGroup(frameLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(frameLayout.createSequentialGroup()
                		.addComponent(bluetoothPanel)
                		.addComponent(polygonPanel)
                		.addComponent(arrowPanel)
                		.addComponent(focusPanel))
                .addGroup(frameLayout.createSequentialGroup()
                		.addComponent(mappingPanel)
                		.addComponent(consolePanel)));
        frameLayout.linkSize(SwingConstants.HORIZONTAL, bluetoothPanel, polygonPanel, arrowPanel, focusPanel);
        frameLayout.linkSize(SwingConstants.VERTICAL, polygonPanel, consolePanel);

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
				if(bluetoothConnect.getText() == "Connect") {
					if(onManual) {
						onManual = false;
						System.out.println("[GUI] Switched to GUI control.");
					}
					prevCommunicator = unitCommunicator;
					unitCommunicator = new RobotCommunicator();
					try {
						unitCommunicator.openUnitConnection();
						bluetoothConnect.setText("Disconnect");
						bluetoothStatus.setIcon(bluetoothConnectedIcon);
						System.out.println("[CONNECTION] Connection established.");
					} catch (IOException e) {
						unitCommunicator = prevCommunicator;
						System.out.println("[CONNECTION] Oops! Something went wrong! \n[CONNECTION] Please make sure your robot and bluetooth are turned on.");
					}
					
				}
				else if(bluetoothConnect.getText() == "Disconnect") {
					if(onManual) {
						onManual = false;
						System.out.println("[GUI] Switched to GUI control.");
					}
					try {
						unitCommunicator.closeUnitConnection();
						unitCommunicator = prevCommunicator;
						bluetoothConnect.setText("Connect");
						bluetoothStatus.setIcon(bluetoothNotConnectedIcon);
						System.out.println("[CONNECTION] Connection succesfully closed. Entered simulation mode.");
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
			@Override
			public void mouseReleased(MouseEvent arg0) {
				try {
					unitCommunicator.sendCommandToUnit(Command.FORWARD_RELEASED);
					uparrow.setIcon(uparrowicon);
					
				} catch (IOException e) {
					
				}
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				try {
					unitCommunicator.sendCommandToUnit(Command.FORWARD_PRESSED);
					uparrow.setIcon(uparrowpressedicon);
					if(onManual) {
						onManual = false;
						System.out.println("[GUI] Switched to GUI control.");
					}
				} catch (IOException e) {
					
				}
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {}
		});
        downarrow.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				try {
					unitCommunicator.sendCommandToUnit(Command.BACKWARD_RELEASED);
					downarrow.setIcon(downarrowicon);
				} catch (IOException e) {
					
				}
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				try {
					unitCommunicator.sendCommandToUnit(Command.BACKWARD_PRESSED);
					downarrow.setIcon(downarrowpressedicon);
					if(onManual) {
						onManual = false;
						System.out.println("[GUI] Switched to GUI control.");
					}
				} catch (IOException e) {
					
				}
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {}
		});
        leftarrow.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				try {
					unitCommunicator.sendCommandToUnit(Command.LEFT_RELEASED);
					leftarrow.setIcon(leftarrowicon);
				} catch (IOException e) {
					
				}
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				try {
					unitCommunicator.sendCommandToUnit(Command.LEFT_PRESSED);
					leftarrow.setIcon(leftarrowpressedicon);
					if(onManual) {
						onManual = false;
						System.out.println("[GUI] Switched to GUI control.");
					}
				} catch (IOException e) {
					
				}
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {}
		});
        rightarrow.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				try {
					unitCommunicator.sendCommandToUnit(Command.RIGHT_RELEASED);
					rightarrow.setIcon(rightarrowicon);
				} catch (IOException e) {
					
				}
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				try {
					unitCommunicator.sendCommandToUnit(Command.RIGHT_PRESSED);
					rightarrow.setIcon(rightarrowpressedicon);
					if(onManual) {
						onManual = false;
						System.out.println("[GUI] Switched to GUI control.");
					}
				} catch (IOException e) {
					
				}
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {}
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
				SilverSurferGUI.onManual = true;
				System.out.println("[GUI] Switched to manual control.");
				simulationPanel.requestFocusInWindow();
			}
		});

        frame.pack();
        frame.setSize(1000, 800);
        frame.setVisible(true);
        
        unitCommunicator = new SimulatorCommunicator();
        try {
			System.out.println("[CONNECTION] Entered simulator mode.");
			unitCommunicator.openUnitConnection();
		} catch (IOException e1) {
			System.out.println("[CONNECTION] Oops! Something went wrong initializing!");
		}
        simulationPanel.addKeyListener(listen());
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
        
        polygonangles = new JSlider(JSlider.HORIZONTAL, 3, 30, 5);
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

    private static JPanel focusPanel() {
        focus = new JButton("Manual Control");
        
        JPanel focusPanel = new JPanel();
        focusPanel.setBorder(BorderFactory.createTitledBorder(createBorder(),"GUI/Manuel switch"));
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
    
    private static JPanel mappingPanel() {
        simulationPanel = new SimulationJPanel();
        simulationPanel.setSize(20000, 20000);
        simulationPanel.setBackground(Color.white);
        simulationPanel.setBorder(createBorder());
        		
    	JPanel mappingPanel = new JPanel();
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
    
	private static KeyListener listen() {
		return new KeyListener() {
			
			int xForward = 0;
			int xBackward = 0;
			int xRight = 0;
			int xLeft = 0;
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					if(e.getKeyCode()==KeyEvent.VK_UP) {
						unitCommunicator.sendCommandToUnit(Command.FORWARD_RELEASED);
						if(unitCommunicator instanceof SimulatorCommunicator)
							System.out.println(unitCommunicator.getConsoleTag()+ " Travelled " + xForward + " cm forward.");
						xForward = 0;
						uparrow.setIcon(uparrowicon);
					}
					else if(e.getKeyCode()==KeyEvent.VK_DOWN) {
						unitCommunicator.sendCommandToUnit(Command.BACKWARD_RELEASED);
						if(unitCommunicator instanceof SimulatorCommunicator)
							System.out.println(unitCommunicator.getConsoleTag()+ " Travelled " + xBackward + " cm backward.");
						xBackward = 0;
						downarrow.setIcon(downarrowicon);
					}
					else if(e.getKeyCode()==KeyEvent.VK_LEFT) {
						unitCommunicator.sendCommandToUnit(Command.LEFT_RELEASED);
						if(unitCommunicator instanceof SimulatorCommunicator)
							System.out.println(unitCommunicator.getConsoleTag()+ " Turned " + 5*xLeft + " degrees to the left.");
						xLeft = 0;
						leftarrow.setIcon(leftarrowicon);
					}
					else if(e.getKeyCode()==KeyEvent.VK_RIGHT) {
						unitCommunicator.sendCommandToUnit(Command.RIGHT_RELEASED);
						if(unitCommunicator instanceof SimulatorCommunicator)
							System.out.println(unitCommunicator.getConsoleTag()+ " Turned " + 5*xRight + " degrees to the right.");
						xRight = 0;
						rightarrow.setIcon(rightarrowicon);
					}
				} catch(IOException ex) {
						
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				try {
					if(e.getKeyCode()==KeyEvent.VK_UP) {
						xForward++;
						unitCommunicator.sendCommandToUnit(Command.FORWARD_PRESSED);
						uparrow.setIcon(uparrowpressedicon);
					}
					else if(e.getKeyCode()==KeyEvent.VK_DOWN) {
						xBackward++;
						unitCommunicator.sendCommandToUnit(Command.BACKWARD_PRESSED);
						downarrow.setIcon(downarrowpressedicon);
					}
					else if(e.getKeyCode()==KeyEvent.VK_LEFT) {
						xLeft++;
						unitCommunicator.sendCommandToUnit(Command.LEFT_PRESSED);
						leftarrow.setIcon(leftarrowpressedicon);
					}
					else if(e.getKeyCode()==KeyEvent.VK_RIGHT) {
						xRight++;
						unitCommunicator.sendCommandToUnit(Command.RIGHT_PRESSED);
						rightarrow.setIcon(rightarrowpressedicon);
					}
				} catch(IOException ex) {
						
				}
			}
		};
	}
    
	public static void toConsole(String s) {
		System.out.println(s);
	}
	
    public static void main(String[] args) {
    	createAndShowGUI();
    }
    
    private static javax.swing.border.Border createBorder()
    {
    	return BorderFactory.createEtchedBorder(1);
    }
}