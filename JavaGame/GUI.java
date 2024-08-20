import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class GUI extends JFrame {
	/**
	 * 
	 */
	private static DatagramSocket udpSocket = null;
    private static ServerSocket serverSocket = null;
	private static final long serialVersionUID = 1L;
	private static ArrayList<String> peers = new ArrayList<>(); 
	private static ArrayList<String> files = new ArrayList<>();
	private static Map<String, Integer> peerServerPorts = new HashMap<>();
    private static String secretKey;
    private static final SocketAddress PORT = null;
	private JFileChooser fileChooser = new JFileChooser();
	private static int serverPort = -1;
    private static DefaultListModel<String> computersList;
    private static DefaultListModel<String> filesList;
    private static String sharedFolderPath;
    private static final int CHUNK_SIZE = 512 * 1024; // 512KB
    public GUI(String title) {
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        setLayout(new GridLayout(7,2));
        //Menu
        JMenuBar menuBar = new JMenuBar();
        JMenu File_Menu = new JMenu("File");
        JMenuItem connectItem = new JMenuItem("Connect");
        JMenuItem disconnectItem = new JMenuItem("Disconnect");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        File_Menu.add(connectItem);
        File_Menu.add(disconnectItem);
        File_Menu.add(exitItem);
        JMenu Help_Menu = new JMenu("Help");
        JMenuItem helpItem = new JMenuItem("About");
        helpItem.addActionListener(e -> {
            JFrame helpFrame = new JFrame("Help");
            JTextArea helpText = new JTextArea("Ezgi Kara");
            helpText.setEditable(false);
            helpFrame.add(helpText);
            helpFrame.setSize(400, 200);
            helpFrame.setLocationRelativeTo(GUI.this); 
            helpFrame.setVisible(true);
            
        JMenu headerMenu = new JMenu("P2P File Sharing App");   
        });
        Help_Menu.add(helpItem);
        menuBar.add(File_Menu);
        menuBar.add(Help_Menu);
        menuBar.add(helpItem);
        setJMenuBar(menuBar);
        
        //Shared Folder Location
        JPanel FolderPanel = new JPanel();
        FolderPanel.setBorder(BorderFactory.createTitledBorder("Shared Folder Location"));
        JTextArea TextArea = new JTextArea(3,15);
        TextArea.setBackground(Color.WHITE);
        FolderPanel.add(new JScrollPane(TextArea));
        add(FolderPanel);
        //Shared Secret
        JPanel SharedSecretPanel = new JPanel();
        SharedSecretPanel.setBorder(BorderFactory.createTitledBorder("Shared Secret"));
        JTextArea STextArea = new JTextArea(3,15);
        STextArea.setBackground(Color.WHITE);
        SharedSecretPanel.add(new JScrollPane(STextArea));
        add(SharedSecretPanel);
        Component frame;
		//UDP
        connectItem.addActionListener(e -> {
        	startSearchClient();
            
      	  new Thread(GUI::listenForUDPPackets).start();
           
            sendUDPPackets();
            JOptionPane.showMessageDialog(frame, "Connected", "Connect", JOptionPane.INFORMATION_MESSAGE);
      });

      disconnectItem.addActionListener(e -> {
          new Thread(GUI::disconnect).start();
          JOptionPane.showMessageDialog(frame, "Disconnected", "Disconnect", JOptionPane.INFORMATION_MESSAGE);
      });
      helpItem.addActionListener(e -> {
          JOptionPane.showMessageDialog(frame, "Ezgi",, JOptionPane.INFORMATION_MESSAGE);
      });
      exitItem.addActionListener(e -> {
        
          ((Window) frame).dispose();
          System.exit(0);
          });
  }
    private static void startSearchClient() {
        new Thread(() -> {
            try {
            	ServerSocket serverSocket = new ServerSocket(0); // 0 for any available port
                int serverPort = serverSocket.getLocalPort();
                System.out.println("File Server is running on port: " + serverPort);

                while (true) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        new Thread(() -> ClientRequest(clientSocket)).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    private static void listenForUDPPackets() {
    	try {
        	
           DatagramSocket socket = new DatagramSocket(PORT);
          
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (true) {
            	socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received: "+ received);
                String[] parts = received.split(";");

                Object secretKey;
				if (parts.length >= 4 && parts[1].equals(secretKey)) {
                    String peerAddress = parts[0];
                    if (!peers.contains(peerAddress)) {
                        peers.add(peerAddress);
                        SwingUtilities.invokeLater(() -> computersList.addElement(peerAddress));
                    }

                    String fileListString = parts[2];
                  
                    String[] files = fileListString.split(",");
                    SwingUtilities.invokeLater(() -> {
                        filesList.clear();
                        for (String file : files) {
                            if (!file.isEmpty()) {
                                filesList.addElement(peerAddress + ": " + file);
                            }
                        }
                    });

                    int receivedPort = Integer.parseInt(parts[3]);
                    peerServerPorts.put(peerAddress, receivedPort);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }         
private static void sendUDPPackets() {
    new Thread(() -> {
        DatagramSocket udpSocket;
		try {
            udpSocket = new DatagramSocket();
            while (true) {
                Object sharedFolderPath;
				ArrayList<String> fileList = listFilesInFolder(sharedFolderPath);
                String fileListString = String.join(",", fileList);

                Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                String serverPort;
				while (networkInterfaces.hasMoreElements()) {
                    NetworkInterface networkInterface = networkInterfaces.nextElement();
                    if (!networkInterface.isLoopback() && networkInterface.isUp()) {
                        for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                            InetAddress broadcast = interfaceAddress.getBroadcast();
                            InetAddress localAddress = interfaceAddress.getAddress();
                            if (broadcast != null) {
                                System.out.println("Using interface: " + networkInterface.getName() + " with broadcast address: " + broadcast.getHostAddress());
                                String message = localAddress.getHostAddress() + ";" + secretKey + ";" + fileListString + ";" + serverPort;
                                DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), PORT);
                                udpSocket.send(packet);
                                System.out.println("Sent: " + message);
                            }
                        }
                    }
                }

                Enumeration<NetworkInterface> networkInterfacesIpv4 = NetworkInterface.getNetworkInterfaces();
                while (networkInterfacesIpv4.hasMoreElements()) {
                    NetworkInterface networkInterface = networkInterfacesIpv4.nextElement();
                    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress address = addresses.nextElement();
                        if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                            System.out.println("Using interface: " + networkInterface.getName() + " with address: " + address.getHostAddress());
                            byte[] ip = address.getAddress();

                            for (int i = 1; i <= 254; i++) {
                                ip[3] = (byte) i;
                                InetAddress subnetAddress = InetAddress.getByAddress(ip);
                                System.out.println("Subnets: " + subnetAddress.getHostAddress());
                                if (!subnetAddress.equals(address) && serverPort != -1) {
                                    String message = address.getHostAddress() + ";" + secretKey + ";" + fileListString + ";" + serverPort;
                                    DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), PORT);
                                    udpSocket.send(packet);
                                    System.out.println("Sent: " + message);
                                }
                            }
                        }
                    }
                }

                Thread.sleep(10000);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (udpSocket != null && !udpSocket.isClosed()) {
                udpSocket.close();
            }
        }
    }).start();
}
private static ArrayList<String> listFilesInFolder(Object sharedFolderPath) {
	// TODO Auto-generated method stub
	return null;
}
private static void ClientRequest(Socket clientSocket) {
    try (DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
         DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream())) {

        String clientMessage = dis.readUTF();

        if ("Hi".equals(clientMessage)) {
            dos.writeUTF("Hi_RESPONSE");
        } else {
            File file = new File(sharedFolderPath, clientMessage);
            if (file.exists()) {
                long fileSize = file.length();
                dos.writeLong(fileSize);

                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[CHUNK_SIZE];
                    int bytes;
                    while ((bytes = fis.read(buffer)) > 0) {
                        dos.write(buffer, 0, bytes);
                    }
                }
            } else {
                dos.writeLong(0); // File not found
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}
private static void disconnect() {
    try {
        
		if (udpSocket != null && !udpSocket.isClosed()) {
            udpSocket.close();
        }
		if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
      
        serverPort = -1;

        peers.clear();
        files.clear();

        SwingUtilities.invokeLater(() -> {
            computersList.clear();
            filesList.clear();
        });

       
    } catch (IOException e) {
        e.printStackTrace();
    }
}

}
