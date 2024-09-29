
import java.awt.*;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import java.net.*;
import java.awt.event.*;
import java.io.*;
import java.lang.annotation.Target;

import javax.swing.filechooser.*;
import javax.swing.plaf.BorderUIResource.EtchedBorderUIResource;
import javax.xml.parsers.FactoryConfigurationError;

import org.w3c.dom.events.MouseEvent;

import java.util.Scanner;
import javax.lang.model.util.ElementScanner6;
import javax.naming.InvalidNameException;
import javax.net.ssl.SSLEngineResult.Status;

import java.net.*;
import java.util.Scanner;
import java.io.*;

// Server class starts here 

class cancelCheckerServer extends fileTranWLServer {
    String socket_add = "";
    public static boolean rServ = false;

    public void setSocketAdd(String str) {
        socket_add = str;
    }

    public void run() {
        try {
            rServ = true;
            fileTranWLServer ob1 = new fileTranWLServer();
            ob1.setSocketAdd(socket_add);
            Thread t1 = new Thread(ob1);
            t1.start();
            ServerSocket ss2 = new ServerSocket(3076, 1, InetAddress.getByName(socket_add));
            Socket s2 = ss2.accept();
            DataInputStream din = new DataInputStream(s2.getInputStream());
            String str = "";
            while (true) {
                if (din.available() != 0) {
                    str = din.readUTF();
                    if (str.equals("cancel")) {
                        System.out.println(str);
                        ob1.stopper();
                        break;
                    }
                }
                Thread.sleep(1000);
            }
            rServ = false;
            ss2.close();
            System.out.println("yaha bhi aa gya");
        } catch (Exception e) {
            rServ = false;
            System.out.println(e.getMessage());
        }
    }
}

class fileTranWLServer implements Runnable {
    public static ServerSocket ss;
    public static Socket sc;
    public static boolean r = true;
    public static boolean receiveStart = false;
    String socket_add = "";
    static String fPath = "";

    public void setFPath(String str) {
        fPath = str;
    }

    public void stopper() {
        r = false;
    }

    public void setSocketAdd(String str) {
        socket_add = str;
    }

    boolean getR() {
        return r;
    }

    public void run() {
        try {
            ServerSocket ss = new ServerSocket(3075, 1, InetAddress.getByName(socket_add));
            Socket s = ss.accept();
            P_FILE.lblError2.setText("Connected Successfully");
            // System.out.println("yaha aaya");
            DataInputStream din = new DataInputStream(s.getInputStream());
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            // s1.close();
            String instr;
            Long fSize = din.readLong();
            instr = din.readUTF();
            File file = new File(fPath + instr);
            if (file.createNewFile()) {
                dout.writeUTF("success");
                dout.flush();
            } else if (file.exists()) {
                dout.writeUTF("success");
                dout.flush();
            }
            FileOutputStream outputStream = new FileOutputStream(file);
            DataOutputStream fileOut = new DataOutputStream(outputStream);
            try {
                int count = 0;
                long fRecievedSize = 1;
                int percent;
                while (din.available() == 0 && getR()) {
                    count = din.available();
                }
                receiveStart = true;
                while (s.isConnected() && getR()) {
                    fRecievedSize = file.length();
                    count = din.available();
                    percent = (int) (((double) fRecievedSize / (double) fSize) * 100);
                    P_FILE.progress = percent;
                    // System.out.println(percent + " " + count + " " + (count / (1024 * 1024)));
                    if ((fRecievedSize == fSize) || (percent == 100))
                        break;
                    byte[] b = new byte[count];
                    din.readFully(b);
                    fileOut.write(b);
                }
            } catch (Exception e) {
                r = false;
                System.out.println(e.getCause());
                System.out.println(e.getMessage());
            }

            fileOut.close();
            outputStream.close();
            ss.close();
            if (!getR()) {
                // System.out.println("yaha toh aaya");
                file.delete();
                if (!file.exists()) {
                    System.out.println("deleted");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

// Client class starts here

class cancelCheckerClient extends fileTranWLClient {
    static public boolean r = false;
    String socket_add2 = "";

    public void run() {
        try {
            r = true;
            fileTranWLClient ob1 = new fileTranWLClient();
            ob1.setSocketAdd(socket_add);
            Thread t1 = new Thread(ob1);
            t1.start();
            Socket s = new Socket(socket_add, 3076);
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            while (r && exceptionCheck) {
                Thread.sleep(1000);
            }
            if (!r)
                dout.writeUTF("cancel");
            s.close();
        } catch (Exception e) {
            r = false;
            System.out.println(e.getMessage());
        }
    }

    public void setSocketAdd2(String str) {
        socket_add = str;
    }

    public void stopClient() {
        r = false;
    }
}

class fileTranWLClient implements Runnable {
    public static Socket s;
    String socket_add = "";
    static String filePath = "";
    public static boolean exceptionCheck = true;
    public static boolean sendClicked = false;
    public static boolean executionCheck = true;

    public void setFilePath(String str) {
        filePath = str;
    }

    public void setSocketAdd(String str) {
        socket_add = str;
    }

    public void run() {
        try {
            Socket s = new Socket(socket_add, 3075);
            executionCheck = false;
            while (filePath.equals("")) {
                Thread.sleep(500);
                System.out.println("ye chalra");
            }
            while (!sendClicked) {
                Thread.sleep(500);
                System.out.println("ab ye chalra");
            }
            System.out.println("Reading from : " + filePath);
            File inFile = new File(filePath);
            FileInputStream inStream = new FileInputStream(inFile);
            DataInputStream input = new DataInputStream(inStream);
            String fileName = "";
            int i = filePath.length() - 1;
            while (filePath.charAt(i) != '\\') {
                fileName = filePath.charAt(i) + fileName;
                i--;
            }
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            dout.writeLong(inFile.length());
            DataInputStream din = new DataInputStream(s.getInputStream());
            dout.writeUTF(fileName);
            String response = "";
            response = din.readUTF();
            // System.out.println("ONE 1");
            // System.out.println(din.readUTF());
            System.out.println(response);
            // System.out.println("one 2");
            // yaha pr tha
            if (response.equals("success")) {
                Long fSizeTotal = inFile.length();
                Long fSizeSent = 0l;
                System.out.println("connection succeed now copying");
                try {
                    int count = 65536;
                    while ((input.available()) != 0) {
                        count = 65536;
                        // System.out.println(count);
                        if (input.available() < 65536)
                            count = input.available();
                        fSizeSent += count;
                        P_FILE.progress = (int) (((double) fSizeSent / (double) fSizeTotal) * 100);
                        System.out.println(count);
                        byte[] b = new byte[count];
                        input.readFully(b);
                        dout.write(b);
                        // i = input.readByte();
                        // dout.writeByte(i);
                    }
                } catch (Exception e) {
                    exceptionCheck = false;
                    System.out.println(e.getMessage());
                } finally {
                    inStream.close();
                    dout.close();
                    System.out.println("aaya");
                    s.close();
                }
            }
        } catch (Exception e) {
            exceptionCheck = false;
            System.out.println(e.getMessage());
        }

    }
}

class ExtIppp extends Thread {
    private String ip;

    String send_ip() {
        return ip;
    }

    public void run() {
        try {
            // We are running "dir" and "ping" command on cmd
            Process p = Runtime.getRuntime().exec("cmd /c ipconfig ");
            boolean stop = true;
            try {
                String reads;
                Boolean val1 = false;
                Boolean val2 = false;
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
                while ((reads = stdInput.readLine()) != null) {
                    String data[] = reads.split(" ", 0);
                    int i = 0;
                    while (i < data.length && stop) {
                        if (data[i].equals("Wi-Fi:")) {
                            val1 = true;
                        }
                        if (val1 && data[i].equals("IPv4")) {
                            val2 = true;
                        }
                        if (val2 && data[i].equals(":")) {
                            ip = data[i + 1];
                            stop = !stop;
                        }
                        i++;
                    }
                }
                System.out.println("Ip found : " + ip);
                stdInput.close();
            } catch (FileNotFoundException e) {
                System.out.println("IpExtraction Error");
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("File opening error");
            e.printStackTrace();
        }
    }

}

// pc file is starting from here
public class P_FILE extends JFrame implements ActionListener, Runnable, FocusListener {

    static JLabel lbl1;
    static JLabel lbl2;
    static JLabel lbl3;
    static JLabel lbl4;
    static JLabel lbl5;
    static JLabel lbl6;
    static JLabel lbl7;
    static JLabel lblError;
    static JLabel lblError2;
    static JTextField tfR;
    static JTextField tfS;
    static JTextArea tfH;
    static JProgressBar progressBar = new JProgressBar(0, 100);
    public static int progress = 0;
    private boolean cancelClicked = true;
    JFileChooser J_Chooser;
    String jchoosertitle;

    P_FILE() {

    }

    public void run() {
        Color c1 = new Color(41, 185, 171);
        // Color c1 = new Color(32,162,110);
        Color c3 = new Color(255, 51, 153);
        Color c2 = new Color(48, 216, 199);
        // Color c2 = new Color(42,188,129);
        Color c4 = new Color(153, 51, 255);

        P_FILE PF = new P_FILE();

        JButton btn3_HOST = new JButton("HOST");

        JFrame frm1 = new JFrame("Wi-Fi File Transfer");
        JFrame frm2 = new JFrame("RECEIVE");
        JFrame frm3 = new JFrame("SEND");
        JFrame frm4 = new JFrame("HELP");
        JFrame frm5 = new JFrame("ProgressBar");
        JFrame frm6 = new JFrame("CREATOR");
        JFrame frm7 = new JFrame("SETTINGS");
        // Main front frame is here

        frm1.setSize(1000, 700);
        frm1.setLayout(null);
        JPanel panel_top = new JPanel();
        JPanel panel_left = new JPanel();
        JPanel panel_main = new JPanel();
        panel_main.setLayout(null);
        panel_left.setLayout(null);
        panel_top.setLayout(null);
        JButton btn1_SEND = new JButton("SEND");
        JButton btn2_RECEIVE = new JButton("RECEIVE");
        JButton btn7_HELP = new JButton("HELP");
        JButton btn11_CREATOR = new JButton("CREATOR");
        JButton btn12_SETTING = new JButton("DOWNLOAD PATH");
        JButton btnfrm1_EXIT = new JButton("EXIT");
        lbl1 = new JLabel("FILE SHARING");
        lbl2 = new JLabel("IP ADDRESS", JLabel.CENTER);

        panel_top.setBackground(c1);
        btn1_SEND.setBackground(c1);
        btn2_RECEIVE.setBackground(c1);
        btn7_HELP.setBackground(c2);
        btn11_CREATOR.setBackground(c2);
        btn12_SETTING.setBackground(c2);
        btnfrm1_EXIT.setBackground(c2);
        panel_main.setBackground(c2);
        panel_left.setBackground(c1);
        panel_main.setBounds(150, 150, 850, 550);
        panel_left.setBounds(0, 0, 150, 700);
        panel_top.setBounds(0, 0, 1000, 150);
        lbl1.setBounds(300, 50, 400, 50);
        lbl2.setBounds(330, 50, 200, 50);
        btn1_SEND.setBounds(200, 300, 100, 50);
        btn2_RECEIVE.setBounds(565, 300, 100, 50);
        btn7_HELP.setBounds(50, 200, 100, 30);
        btn11_CREATOR.setBounds(50, 250, 100, 30);
        btn12_SETTING.setBounds(20, 300, 130, 30);
        btn12_SETTING.setMargin(new Insets(0, 0, 0, 0));
        btnfrm1_EXIT.setBounds(50, 600, 100, 30);
        lbl1.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        lbl1.setFont(new Font("Arial", Font.PLAIN, 55));
        lbl2.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        lbl2.setFont(new Font("Arial", Font.PLAIN, 20));
        panel_main.add(lbl2);
        panel_main.add(btn1_SEND);
        panel_top.add(lbl1);
        panel_main.add(btn2_RECEIVE);
        panel_left.add(btn7_HELP);
        panel_left.add(btn11_CREATOR);
        panel_left.add(btn12_SETTING);
        panel_left.add(btnfrm1_EXIT);
        frm1.add(panel_main);
        frm1.add(panel_top);
        frm1.add(panel_left);

        frm1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm1.setLocationRelativeTo(null);
        frm1.setResizable(false);
        frm1.setVisible(true);

        btn2_RECEIVE.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent args0) {
                try {
                    ExtIppp esr = new ExtIppp();
                    esr.run();
                    tfR.setText(esr.send_ip());
                    if (esr.send_ip() == null) {
                        tfR.setText("Wi-Fi error !!");
                        btn3_HOST.setEnabled(false);
                    } else {
                        btn3_HOST.setEnabled(true);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                frm1.setVisible(false);
                frm2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frm2.setLocationRelativeTo(null);
                frm2.setResizable(false);
                frm2.setVisible(true);
            }
        });

        btn1_SEND.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent args0) {
                lblError.setText("");
                frm1.setVisible(false);
                frm3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frm3.setLocationRelativeTo(null);
                frm3.setResizable(false);
                frm3.setVisible(true);
            }
        });
        btn7_HELP.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent args0) {
                frm4.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frm4.setLocationRelativeTo(null);
                frm4.setResizable(false);
                frm4.setVisible(true);
            }
        });
        btn11_CREATOR.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent args0) {
                frm6.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frm6.setLocationRelativeTo(null);
                frm6.setResizable(false);
                frm6.setVisible(true);
            }
        });
        btnfrm1_EXIT.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent args0) {
                System.exit(0);
            }
        });
        // SETTING frame is here
        frm7.setSize(500, 500);
        frm7.setLayout(null);
        JPanel panel_sett = new JPanel();
        // panel_sett.add(lbl7);
        panel_sett.setLayout(null);
        panel_sett.setBackground(c1);
        frm7.add(panel_sett);
        // frm7.add(lbl7);

        btn12_SETTING.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent args0) {
                String s1 = args0.getActionCommand();
                if (s1.equals("DOWNLOAD PATH")) {
                    J_Chooser = new JFileChooser();
                    J_Chooser.setCurrentDirectory(new java.io.File("."));
                    // J_Chooser.setDialogTitle(jchoosertitle);
                    J_Chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    J_Chooser.setAcceptAllFileFilterUsed(false);
                    // try{
                    if (J_Chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        System.out.println("Current Directory: " + J_Chooser.getCurrentDirectory());
                        // lbl7.setText(J_Chooser.getSelectedFile().getAbsolutePath());
                        System.out.println("Selected file:" + J_Chooser.getSelectedFile());

                    } else {
                        System.out.println("no selection");
                    }
                    // }
                    // catch(NullPointerException e){
                    // System.out.println("nullpointer exception caught ");
                    // }
                }
                // frm7.addWindowListener(new WindowAdapter(){
                // public void windowClosing(WindowEvent e){
                // System.exit(0);
                // }
                // });

                frm7.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frm7.setLocationRelativeTo(null);
                frm7.setResizable(false);
                // frm7.setVisible(true);
            }
        });

        // Receive frame is here
        frm2.setSize(1000, 700);
        frm2.setLayout(null);
        JPanel panel_Rtop = new JPanel();
        JPanel panel_Rleft = new JPanel();
        JPanel panel_Rmain = new JPanel();
        panel_Rmain.setLayout(null);
        panel_Rleft.setLayout(null);
        panel_Rtop.setLayout(null);
        tfR = new JTextField("", JTextField.CENTER);
        lbl4 = new JLabel("FILE SHARING");
        lblError2 = new JLabel("");
        JButton btn8_HELP = new JButton("HELP");
        JButton btn12_CREATOR = new JButton("CREATOR");
        JButton btn13_BACK = new JButton("BACK");

        panel_Rtop.setBackground(c1);
        panel_Rleft.setBackground(c1);
        panel_Rmain.setBackground(c2);
        btn3_HOST.setBackground(c1);
        btn8_HELP.setBackground(c2);
        btn12_CREATOR.setBackground(c2);
        btn13_BACK.setBackground(c2);
        panel_Rmain.setBounds(150, 150, 850, 550);
        panel_Rleft.setBounds(0, 0, 150, 700);
        panel_Rtop.setBounds(0, 0, 1000, 150);
        lbl4.setBounds(300, 50, 400, 50);
        lblError2.setBounds(220, 200, 400, 50);
        btn3_HOST.setBounds(380, 300, 100, 50);
        btn8_HELP.setBounds(50, 200, 100, 30);
        btn12_CREATOR.setBounds(50, 250, 100, 30);
        btn13_BACK.setBounds(50, 300, 100, 30);
        tfR.setBounds(330, 50, 200, 50);
        tfR.setBackground(new Color(135, 245, 234));
        lblError2.setHorizontalAlignment(SwingConstants.CENTER);
        tfR.setEditable(false);
        tfR.setFont(new Font("Arial", Font.PLAIN, 20));
        lblError2.setFont(new Font("Arial", Font.PLAIN, 25));
        lbl4.setFont(new Font("Arial", Font.PLAIN, 55));
        lbl4.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        panel_Rmain.add(btn3_HOST);
        panel_Rtop.add(lbl4);
        panel_Rleft.add(btn8_HELP);
        panel_Rleft.add(btn12_CREATOR);
        panel_Rleft.add(btn13_BACK);
        panel_Rmain.add(tfR);
        panel_Rmain.add(lblError2);
        frm2.add(panel_Rmain);
        frm2.add(panel_Rleft);
        frm2.add(panel_Rtop);

        btn8_HELP.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent args0) {
                frm4.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frm4.setLocationRelativeTo(null);
                frm4.setResizable(false);
                frm4.setVisible(true);
            }
        });
        btn12_CREATOR.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent args0) {
                frm6.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frm6.setLocationRelativeTo(null);
                frm6.setResizable(false);
                frm6.setVisible(true);
            }
        });
        btn13_BACK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent args0) {
                frm1.setVisible(true);
                frm2.setVisible(false);
            }
        });
        btn3_HOST.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent args0) {
                cancelCheckerServer ob2 = new cancelCheckerServer();
                System.out.println(tfR.getText());
                ob2.setSocketAdd(tfR.getText());
                Thread t2 = new Thread(ob2);
                t2.start();
                try {
                    Thread.sleep(500);
                    if (cancelCheckerServer.rServ == true) {
                        lblError2.setText("Hosted Successfully");
                        btn3_HOST.setEnabled(false);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        
        // Creator frame is here
        frm6.setSize(700, 600);
        frm6.setLayout(null);
        JPanel panel_Creater_top = new JPanel();
        JPanel panel_Creater_left = new JPanel();
        JPanel panel_Creater_right = new JPanel();
        panel_Creater_top.setLayout(null);
        panel_Creater_left.setLayout(null);
        panel_Creater_right.setLayout(null);

        panel_Creater_top.setBackground(c2);
        panel_Creater_left.setBackground(c4);
        panel_Creater_right.setBackground(c3);
        panel_Creater_top.setBounds(0, 0, 700, 150);
        panel_Creater_left.setBounds(0, 100, 350, 500);
        panel_Creater_right.setBounds(350, 100, 350, 500);

        ImageIcon ic = new ImageIcon(ClassLoader.getSystemResource("himanshu.jpg"));
        Image im = ic.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        ImageIcon ic1 = new ImageIcon(im);
        JLabel lbl_pic = new JLabel(ic1);
        lbl_pic.setBounds(120, 30, 100, 100);
        JLabel lblName = new JLabel("Name : Himanshu");
        JLabel lblCollege = new JLabel("College : S.G.I");
        JLabel lblBranch = new JLabel("Branch :C.S.E");
        JLabel lblLocation = new JLabel("Location : SIKAR");
        JLabel lblRD = new JLabel("Roll In Development : GUI");
        JLabel lblHandles = new JLabel("Handles : @himansh2004");
        lblName.setBounds(20, 100, 300, 50);
        lblCollege.setBounds(20, 150, 300, 50);
        lblBranch.setBounds(20, 200, 300, 50);
        lblLocation.setBounds(20, 250, 300, 50);
        lblRD.setBounds(20, 300, 300, 50); 
        lblHandles.setBounds(20, 350, 300, 50);

        lblName.setForeground(Color.WHITE);
        lblCollege.setForeground(Color.WHITE);
        lblBranch.setForeground(Color.WHITE);
        lblLocation.setForeground(Color.WHITE);
        lblRD.setForeground(Color.WHITE);
        lblHandles.setForeground(Color.WHITE);

        panel_Creater_top.add(lbl_pic);
        panel_Creater_left.add(lblName);
        panel_Creater_left.add(lblCollege);
        panel_Creater_left.add(lblBranch);
        panel_Creater_left.add(lblLocation);
        panel_Creater_left.add(lblRD);
        panel_Creater_left.add(lblHandles);

        ImageIcon ic2 = new ImageIcon(ClassLoader.getSystemResource("himanshu.jpg"));
        Image im1 = ic2.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        ImageIcon ic3 = new ImageIcon(im1);
        JLabel lbl_picR = new JLabel(ic3);
        lbl_picR.setBounds(450, 30, 100, 100);
        JLabel lblNameR = new JLabel("Name: Rahul Somani");
        JLabel lblCollegeR = new JLabel("College : S.G.I");
        JLabel lblBranchR = new JLabel("Branch : C.S.E");
        JLabel lblLocationR = new JLabel("Location : SIKAR");
        JLabel lblRDR = new JLabel("Roll In Development : BACKEND");
        JLabel lblHandlesR = new JLabel("Handles : @rahul__somani");
        lblNameR.setBounds(20, 100, 300, 50);
        lblCollegeR.setBounds(20, 150, 300, 50);
        lblBranchR.setBounds(20, 200, 300, 50);
        lblLocationR.setBounds(20, 250, 300, 50);
        lblRDR.setBounds(20, 300, 300, 50);
        lblHandlesR.setBounds(20, 350, 300, 50);

        lblNameR.setForeground(Color.WHITE);
        lblCollegeR.setForeground(Color.WHITE);
        lblBranchR.setForeground(Color.WHITE);
        lblLocationR.setForeground(Color.WHITE);
        lblRDR.setForeground(Color.WHITE);
        lblHandlesR.setForeground(Color.WHITE);

        panel_Creater_top.add(lbl_picR);
        panel_Creater_right.add(lblNameR);
        panel_Creater_right.add(lblCollegeR);
        panel_Creater_right.add(lblBranchR);
        panel_Creater_right.add(lblLocationR);
        panel_Creater_right.add(lblRDR);
        panel_Creater_right.add(lblHandlesR);

        frm6.add(panel_Creater_top);
        frm6.add(panel_Creater_left);
        frm6.add(panel_Creater_right);

        // Send frame is here
        frm3.setSize(1000, 700);
        frm3.setLayout(null);
        JPanel panel_Stop = new JPanel();
        JPanel panel_Sleft = new JPanel();
        JPanel panel_Smain = new JPanel();
        panel_Smain.setLayout(null);
        panel_Sleft.setLayout(null);
        panel_Stop.setLayout(null);
        tfS = new JTextField("Enter IP here", JTextField.CENTER);
        lbl3 = new JLabel("Select File");
        lblError = new JLabel("");
        lbl5 = new JLabel("FILE SHARING");
        JButton btn4_Send = new JButton("Send");
        JButton btn6_Select = new JButton("Select");
        // JButton btn6=new JButton(new ImageIcon("E:/documents/coder
        // HS/project/icon.png"));
        JButton btn9_HELP = new JButton("HELP");
        JButton btn10_CONNECT = new JButton("CONNECT");
        JButton btn13_CREATOR = new JButton("CREATOR");
        JButton btn14_BACK = new JButton("BACK");

        panel_Stop.setBackground(c1);
        panel_Sleft.setBackground(c1);
        panel_Smain.setBackground(c2);
        btn4_Send.setBackground(c1);
        btn6_Select.setBackground(c1);
        btn9_HELP.setBackground(c2);
        btn10_CONNECT.setBackground(c2);
        btn13_CREATOR.setBackground(c2);
        lbl3.setOpaque(true);
        lbl3.setBackground(new Color(135, 245, 234));
        btn14_BACK.setBackground(c2);
        tfS.setBackground(new Color(135, 245, 234));
        tfS.setBounds(250, 50, 300, 50);
        panel_Smain.setBounds(150, 150, 850, 550);
        panel_Sleft.setBounds(0, 0, 150, 700);
        panel_Stop.setBounds(0, 0, 1000, 150);
        btn4_Send.setBounds(350, 350, 100, 50);
        btn6_Select.setBounds(545, 225, 100, 40);
        btn9_HELP.setBounds(50, 200, 100, 30);
        btn13_CREATOR.setBounds(50, 250, 100, 30);
        btn10_CONNECT.setBounds(350, 150, 100, 30);
        btn14_BACK.setBounds(50, 300, 100, 30);
        lbl3.setBounds(180, 225, 365, 40);
        lblError.setBounds(200, 280, 365, 50);
        lbl5.setBounds(300, 50, 400, 50);
        lbl3.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        lbl3.setFont(new Font("Arial", Font.PLAIN, 15));
        lblError.setFont(new Font("Arial", Font.PLAIN, 25));
        lblError.setHorizontalAlignment(SwingConstants.CENTER);
        lbl5.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        lbl5.setFont(new Font("Arial", Font.PLAIN, 55));
        tfS.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        tfS.setFont(new Font("Arial", Font.PLAIN, 20));
        panel_Smain.add(tfS);
        panel_Smain.add(btn4_Send);

        panel_Smain.add(btn6_Select);
        panel_Smain.add(btn10_CONNECT);
        panel_Smain.add(lbl3);
        panel_Smain.add(lblError);
        panel_Stop.add(lbl5);
        panel_Sleft.add(btn9_HELP);
        panel_Sleft.add(btn13_CREATOR);
        panel_Sleft.add(btn14_BACK);
        frm3.add(panel_Smain);
        frm3.add(panel_Stop);
        frm3.add(panel_Sleft);

        tfS.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                tfS.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
            }
        });
        btn4_Send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent args0) {
                System.out.println(lbl3.getText());
                if (lbl3.getText().equals("Select File")) {
                    lblError.setText("Please Choose a file to SEND !!");
                    return;
                } else if (cancelCheckerClient.r == false) {
                    lblError.setText("Connect to receiver first !!");
                    return;
                }
                fileTranWLClient.sendClicked = true;
                frm5.setUndecorated(true);
                frm5.setLocationRelativeTo(null);
                frm5.setResizable(false);
                frm5.setVisible(true);
                frm3.setVisible(false);
            }
        });
        btn6_Select.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent args0) {
                JFileChooser jf = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                int cp = jf.showOpenDialog(null);
                if (cp == JFileChooser.APPROVE_OPTION) {
                    lbl3.setText(jf.getSelectedFile().getAbsolutePath());
                    fileTranWLClient.filePath = jf.getSelectedFile().getAbsolutePath();
                } else {
                    lbl3.setText("");
                }
            }
        });
        btn9_HELP.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent args0) {
                frm4.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frm4.setLocationRelativeTo(null);
                frm4.setResizable(false);
                frm4.setVisible(true);
            }
        });
        btn13_CREATOR.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent args0) {
                frm6.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frm6.setLocationRelativeTo(null);
                frm6.setResizable(false);
                frm6.setVisible(true);
            }
        });
        btn14_BACK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent args0) {
                frm1.setVisible(true);
                frm3.setVisible(false);
            }
        });
        btn10_CONNECT.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent args0) {
                if (tfS.getText().equals("Enter IP here")) {
                    lblError.setText("Enter IP from receiver to connect");
                    return;
                }
                cancelCheckerClient ob2 = new cancelCheckerClient();
                System.out.println(tfS.getText());
                ob2.setSocketAdd2(tfS.getText());
                Thread tc2 = new Thread(ob2);
                tc2.start();
                while (fileTranWLClient.executionCheck) {
                    if (fileTranWLClient.exceptionCheck == true) {
                        fileTranWLClient.exceptionCheck = false;
                        break;
                    }
                }
                if (cancelCheckerClient.r == false) {
                    lblError.setText("Connection Error !!");
                } else {
                    lblError.setText("Connected Successfully");
                }
            }
        });
        // help frame is here

        frm4.setSize(500, 500);
        JPanel panel_HELP = new JPanel();
        panel_HELP.setLayout(null);
        tfH = new JTextArea(
                "This is an help menu and this will contain all the information about the usage and all other attributes of the app");
        tfH.setLineWrap(true);

        // Enable word wrap (wrap at word boundaries)
        tfH.setWrapStyleWord(true);
        tfH.setEditable(false);
        tfH.setBackground(c1);
        panel_HELP.setBackground(c2);
        tfH.setBounds(2, 2, 482, 482);
        tfH.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        tfH.setFont(new Font("Arial", Font.PLAIN, 18));
        panel_HELP.add(tfH);
        frm4.add(panel_HELP);
        tfH.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                frm4.setVisible(false);
            }
        });
        // progressBar frame is here

        frm5.setSize(1000, 700);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        // JTextArea taskOutput=new JTextArea(5,20);
        // taskOutput.setEditable(false);
        JPanel panel_progBar_main = new JPanel();
        JPanel panel_progBar_top = new JPanel();
        JPanel panel_progBar_left = new JPanel();
        panel_progBar_main.setLayout(null);
        panel_progBar_top.setLayout(null);
        panel_progBar_left.setLayout(null);
        lbl6 = new JLabel("FILE SHARING");
        JButton btn10_HELP = new JButton("HELP");
        JButton btn14_CREATOR = new JButton("CREATOR");
        JButton btn15_START = new JButton("START");
        JButton btn16_CANCEL = new JButton("CANCEL");

        panel_progBar_main.setBackground(c2);
        panel_progBar_top.setBackground(c1);
        panel_progBar_left.setBackground(c1);
        btn10_HELP.setBackground(c2);
        btn14_CREATOR.setBackground(c2);
        btn15_START.setBackground(c1);
        btn16_CANCEL.setBackground(c1);
        lbl6.setBounds(300, 50, 400, 50);
        panel_progBar_main.setBounds(150, 150, 850, 550);
        panel_progBar_left.setBounds(0, 0, 150, 700);
        panel_progBar_top.setBounds(0, 0, 1000, 150);
        progressBar.setBounds(300, 50, 300, 50);
        btn10_HELP.setBounds(50, 200, 100, 30);
        btn14_CREATOR.setBounds(50, 250, 100, 30);
        btn15_START.setBounds(200, 300, 100, 50);
        btn16_CANCEL.setBounds(365, 300, 100, 50);
        lbl6.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        lbl6.setFont(new Font("Arial", Font.PLAIN, 55));
        btn15_START.addActionListener(PF);
        panel_progBar_main.add(progressBar);
        // panel_progBar_main.add(btn15_START);
        panel_progBar_main.add(btn16_CANCEL);
        panel_progBar_top.add(lbl6);
        panel_progBar_left.add(btn10_HELP);
        panel_progBar_left.add(btn14_CREATOR);
        frm5.add(panel_progBar_main);
        frm5.add(panel_progBar_top);
        frm5.add(panel_progBar_left);

        btn16_CANCEL.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent args0) {
                cancelClicked = !cancelClicked;
                frm5.setVisible(false);
                cancelCheckerClient.r = false;
            }
        });

        Thread progThread = new Thread(new Runnable() {
            @Override
            public void run() {
            }
        });

        while (true && cancelClicked) {
            try {
                Thread.sleep(200);
                progressBar.setValue(progress);
                if (progress == 100) {
                    frm3.setVisible(true);
                    frm5.setVisible(false);
                    break;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        progressBar.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                progThread.start();
                System.out.println("");
            }

            @Override
            public void focusLost(FocusEvent e) {
            }
        });

        btn10_HELP.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent args0) {
                frm4.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frm4.setLocationRelativeTo(null);
                frm4.setResizable(false);
                frm4.setVisible(true);
            }
        });
        btn14_CREATOR.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent args0) {
                frm6.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frm6.setLocationRelativeTo(null);
                frm6.setResizable(false);
                frm6.setVisible(true);
            }
        });

        
        // for all working
    }

    @Override
    public void focusGained(FocusEvent e) {
    }

    @Override
    public void focusLost(FocusEvent e) {
    }

    public static void main(String args[]) {
        P_FILE ob1 = new P_FILE();
        Thread t1 = new Thread(ob1);
        t1.run();
    }

    public void actionPerformed(ActionEvent evt) {
    }

    // some garbage code ^_^

    // String s=evt.getActionCommand();

    // // J_Chooser=new JFileChooser();
    // // J_Chooser.setCurrentDirectory(new java.io.File("."));
    // // J_Chooser.setDialogTitle(jchoosertitle);
    // // J_Chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    // // J_Chooser.setAcceptAllFileFilterUsed(false);
    // if(s.equals("Select")){
    // JFileChooser jf=new
    // JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
    // int cp=jf.showOpenDialog(null);
    // if(cp==JFileChooser.APPROVE_OPTION){
    // lbl3.setText(jf.getSelectedFile().getAbsolutePath());
    // }
    // else{
    // lbl3.setText("");
    // }
    // }
    // else if(s.equals("Show IP")){
    // try {
    // ExtIppp esr = new ExtIppp();
    // esr.run();
    // Thread.sleep(1000);
    // tfS.setText(esr.send_ip());
    // } catch (InterruptedException e) {
    // System.out.println("Thread sleep exception");
    // }
    // }
    // else if (s.equals("START")) {
    // progress += 5;
    // progressBar.setValue(progress);
    // System.out.println(s);
    // }
    // // else if(s.equals("SETTING")){
    // // J_Chooser=new JFileChooser();
    // // J_Chooser.setCurrentDirectory(new java.io.File("."));
    // // J_Chooser.setDialogTitle(jchoosertitle);
    // // J_Chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    // // J_Chooser.setAcceptAllFileFilterUsed(false);
    // // // try{
    // // if(J_Chooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
    // //
    // System.out.println("getCurrentDirectory():"+J_Chooser.getCurrentDirectory());
    // // // lbl7.setText(J_Chooser.getSelectedFile().getAbsolutePath());
    // // System.out.println("getSelectedfile():"+J_Chooser.getSelectedFile());
    // // }
    // // else{
    // // System.out.println("no selection");
    // // }
    // // // }
    // // // catch(NullPointerException e){
    // // // System.out.println("nullpointer exception caught ");
    // // // }
    // // }
    // }
}