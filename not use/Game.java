import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public class Game extends JFrame {
	
	JPanel main = new JPanel();
	JPanel gaming_zone = null;
	JPanel control = null;
	JPanel scoreboard = null;
	JPanel player_IP = null;
	JPanel text_area = null;
	JTextField PlayerName; // login panel input player name
	JTextField IPAddress; // login panel input ip address
	JTextField PortNumber; // login panel input port number
	JButton confirm; // login panel confirm button
	JLabel[] player = new JLabel[4]; // score panel player information label
	
	Client client = null;
	
	String name="1",IP="1",port="1";
	
	int[] widthAmount={5,10,15,10};
	int[] heightAmount={6,6,6,8};
	int level = 0;
	int playerNumber = 0;
	String[] playerName = {"player1","player2","player3","player4"};
	boolean[] readyState = {false,false,false,false};
	int[] playerScore = {0,0,0,0};
	
	Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
	
	public Game(Client client){
		this.client = client;
		setSize(1200,750);
		LoginPane(main);
		setContentPane(main);
		setTitle("MakeYouCry");
		setResizable(false);
		setVisible(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void LoginPane(Container pane) {
		pane.setLayout(null);
		pane.setSize(1200,750);
		pane.setBackground(Color.YELLOW);
		JLabel t1 = new JLabel("Name: ");
		t1.setBounds(500,300,50,25);
		JLabel t2 = new JLabel("IP: ");
		t2.setBounds(500,325,50,25);
		JLabel t3 = new JLabel("Port: ");
		t3.setBounds(500,350,50,25);
		PlayerName = new JTextField("Rick");
		PlayerName.setBounds(560,300,150,25);
		IPAddress = new JTextField("localhost");
		IPAddress.setBounds(560,325,150,25);
		PortNumber = new JTextField("6666");
		PortNumber.setBounds(560,350,150,25);
		confirm = new JButton("OK");
		confirm.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				connect();
			}
		});
		confirm.setBounds(570,400,100,50);
		pane.add(t1);
		pane.add(t2);
		pane.add(t3);
		pane.add(PlayerName);
		pane.add(IPAddress);
		pane.add(PortNumber);
		pane.add(confirm);
	}
	public void connect(){
		name = PlayerName.getText();
		IP = IPAddress.getText();
		port = PortNumber.getText();
		if (client.connect(IP,Integer.valueOf(port))){
			client.deliver("Name "+name);
			client.deliver("GetInformation");
			main.removeAll();
			addComponentsToMainPane(main);
			main.revalidate();
			main.repaint();
			pack();
		}else{
			PlayerName.setText("");
			IPAddress.setText("");
			PortNumber.setText("");
		}
	}

	public JPanel getGameZone(){
		if (gaming_zone==null) gaming_zone = new JPanel();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gaming_zone.setBorder(BorderFactory.createTitledBorder("Game"));
		gaming_zone.setLayout(gridBagLayout);
		GridBagConstraints c;
		for(int i = 0;i<heightAmount[level];i++){
		for(int j = 0;j<widthAmount[level];j++){
			c = new GridBagConstraints(j,i,1,1,1,1,GridBagConstraints.CENTER,GridBagConstraints.BOTH, new Insets(1,1,1,1),50,50);
			makeButton(gaming_zone,""/* String.valueOf(i)+String.valueOf(j) */, i*widthAmount[level]+j, gridBagLayout, c);
			}
		}
		return gaming_zone;
	}
	
	public void makeButton(Container pane, String title, int number, GridBagLayout gridBagLayout, GridBagConstraints constraints) {
        MyButton button = new MyButton(title,number,Color.WHITE);
		button.addActionListener(new GameAction(this));
        gridBagLayout.setConstraints(button, constraints);
        pane.add(button);
	}
	
	public void open(int num){
		System.out.println("Open "+num);
	}
	
	public JPanel getControl(){
		control = new JPanel();
		GridBagLayout gridBagLayout = new GridBagLayout();
		control.setLayout(gridBagLayout);

		JButton start = new JButton("Start");
		start.addActionListener(new GameConfirmAction(this));
		start.setFocusable(false);
		
		JButton ready = new JButton("Ready");
		ready.addActionListener(new GameConfirmAction(this));
		start.setFocusable(false);
		
        JLabel levelLabel = new JLabel("Choose level");
		
        ButtonGroup buttonGroup = new ButtonGroup();
		JRadioButton[] rb = new JRadioButton[4];
        ItemListener degree = new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()==e.SELECTED){
                    if(e.getSource() == rb[0]) level = 0;
                    if(e.getSource() == rb[1]) level = 1;
                    if(e.getSource() == rb[2]) level = 2;
                    if(e.getSource() == rb[3]) level = 3;
					gaming_zone.removeAll();
					main.add(getGameZone(),new GridBagConstraints(0, 0, 5, 5, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
					gaming_zone.revalidate();
					gaming_zone.repaint();
					pack();
					updateLevel();
                }
            }
        };
		for (int i=0;i<4;i++){
			rb[i] = new JRadioButton(String.valueOf(widthAmount[i]*heightAmount[i])+" cards");
			rb[i].addItemListener(degree);
			rb[i].setFocusable(false);
			buttonGroup.add(rb[i]);
		}
		rb[0].setSelected(true);
		
        control.add(levelLabel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		for (int i=0;i<4;i++)
			control.add(rb[i], new GridBagConstraints(i, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		control.add(start, new GridBagConstraints(4, 0, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		control.add(ready, new GridBagConstraints(4, 1, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		control.setBorder(BorderFactory.createTitledBorder("Control"));
		
		return control;
	}
	
	public void start(){
		System.out.println("Start "+String.valueOf(level));
	}
	public void ready(){
		readyState[playerNumber] = readyState[playerNumber]?false:true;
		System.out.println("Ready "+playerNumber+" "+String.valueOf(readyState[playerNumber]));
	}
	public void updateLevel(){
		System.out.println("Level "+level);
	}
	
	public JPanel getScoreboard(){
		scoreboard = new JPanel();
		GridBagLayout gridBagLayout = new GridBagLayout();
		scoreboard.setBorder(BorderFactory.createTitledBorder("Score"));
		scoreboard.setLayout(gridBagLayout);
		for (int i=0;i<4;i++){
			String state = readyState[i]?"Ready":"Unready";
			player[i] = new JLabel(playerName[i]+": "+state);
			scoreboard.add(player[i], new GridBagConstraints(0, i, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		}
		return scoreboard;
	}
	
	public JPanel getTextArea(){
		text_area = new JPanel();
		GridBagLayout gridBagLayout = new GridBagLayout();
		text_area.setBorder(BorderFactory.createTitledBorder("ChatBox"));
		text_area.setLayout(gridBagLayout);
		JTextArea chatbox = new JTextArea(20,20);
		chatbox.setEditable(false); 
		JTextField message = new JTextField(20);
		JButton enter = new JButton("Enter");
        enter.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String msg = message.getText();
				if (!msg.equals("")){
					chatbox.append(name+": "+msg+"\n");
					System.out.println(name+" "+msg);
				}
                message.setText("");
            }
        });
		JScrollPane chatbox_ScrollPane = new JScrollPane(chatbox);
		JScrollPane message_ScrollPane = new JScrollPane(message);
		text_area.add(chatbox_ScrollPane, new GridBagConstraints(0, 0, 2, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		text_area.add(message_ScrollPane, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.SOUTH, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		text_area.add(enter, new GridBagConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.SOUTH, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		return text_area;
	}
	
	public JPanel getPlayerIP(){
		player_IP = new JPanel();
		GridBagLayout gridBagLayout = new GridBagLayout();
		player_IP.setBorder(BorderFactory.createTitledBorder("IP&port"));
		player_IP.setLayout(gridBagLayout);
		JTextArea show = new JTextArea(2,2);
		show.append("IP: "+IP+"\nPort: "+port);
		show.setEditable(false); 
		player_IP.add(show,new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		
		return player_IP;
	}
	
	public void addComponentsToMainPane(Container pane) {
		//pane.setPreferredSize(new Dimension((int)screenSize.getWidth()-100,(int)screenSize.getHeight()-100));
		GridBagLayout gridBagLayout = new GridBagLayout();
		pane.setLayout(gridBagLayout);
		pane.add(getGameZone(),new GridBagConstraints(0, 0, 5, 5, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		pane.add(getControl(),new GridBagConstraints(0, 5, 3, 1, 1, 1, GridBagConstraints.SOUTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		pane.add(getScoreboard(),new GridBagConstraints(5, 0, 1, 2, 1, 1, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		pane.add(getTextArea(),new GridBagConstraints(5, 2, 1, 3, 1, 1, GridBagConstraints.SOUTHEAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		pane.add(getPlayerIP(),new GridBagConstraints(5, 5, 1, 1, 1, 1, GridBagConstraints.SOUTHEAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	}
	
/***************************Execute Instruction***************************/
	public void setPlayerNumber(int n){
		playerNumber = n;
	}
	public void setPlayerName(int n, String name){
		System.out.println("DO name");
		playerName[n] = name;
	}
	public void setReadyState(int n, String b){
		readyState[n] = Boolean.valueOf(b);
		String state = readyState[n]?"Ready":"Unready";
		player[n].setText(playerName[n]+": "+state);
	}
/***************************Execute Instruction***************************/
    // public static void main(String[] args) {
        // new Game(); 
    // }
}
class GameAction implements ActionListener{
	
	Game f;
	
	public GameAction(Game f){
		this.f = f;
	}
	
	public void actionPerformed(ActionEvent e){
		MyButton tmp = (MyButton)e.getSource();
		tmp.setText("11");
		f.open(tmp.getNumber());
	}
}
class GameConfirmAction implements ActionListener{
	
	Game f;
	
	public GameConfirmAction(Game f){
		this.f = f;
	}
	
	public void actionPerformed(ActionEvent e){
		JButton tmp = (JButton)e.getSource();
		if (tmp.getText().equals("Start")) f.start();
		if (tmp.getText().equals("Ready")) f.ready();
	}
}