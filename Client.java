// File Name Client.java
import java.net.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class Client{
	
	private Socket client = null; //宣告名為client的socket
	private BufferedReader reader; //宣告名為reader的BufferedReader
	private BufferedReader in;  //宣告名為in的BufferedReader
	private PrintWriter out; //宣告名為out的PrintWriter
	private Thread thread = null; //宣告名為thread的Thread
	private boolean connect = true; //宣告名為connect的boolean變數，且初始值設為true
	
	private int port; //宣告port為6666
	private String address; //把address設為localhost的IP
	
	private Game player;
	
	public Client(){
		player = new Game(this);
		player.setVisible(true);
	}
	
	public boolean connect(String address,int port){
		this.address = address;
		this.port = port;
		try{
			client = new Socket(address,port); //建立連線指定host與port
			System.out.println(client==null);
			//獲取系統標準輸入流
			reader = new BufferedReader(new InputStreamReader(System.in)); //設定reader為鍵盤輸入的值
			out = new PrintWriter(client.getOutputStream(),true); //設定out為socket輸出的內容
			in = new BufferedReader(new InputStreamReader(client.getInputStream())); //設定in為socket接收到的訊息
			System.out.println("Connecting to " + client.getRemoteSocketAddress()); //顯示連接到的remote socket的address
			thread = getThread(); //設定thread為getThread()這個function的回傳值
			thread.start(); //啟動執行緒
			//input(); //呼叫input()這個function
		}
		catch (IOException e){ //若無法執行try，則顯示此錯誤訊息
			if (client==null) System.out.println("NULL");
			System.out.println("Can't connect to Server.");
		}
		return (client!=null);
	}
	
	/* public boolean isConnected(){
		return client.isConnected();
	} */
	
	public Thread getThread(){ //建立一個執行緒用於讀取伺服器的資訊
		return new Thread(new Runnable(){ //產生一個thread，不斷接收server廣播出來的訊息
			public void run(){	
				try{ 
					while (true)
						execute(in.readLine()); //顯示socket所接收到的訊息
				}
				catch (IOException e){ 
					System.out.println("Disconnect"); //斷開時顯示disconnect
					connect = false; //把connect設為false
				}
			}
		});
	}
	
	public void input() throws IOException{ 
		String line = reader.readLine(); //line的設為鍵盤輸入的內容
		while (!("end".equalsIgnoreCase(line)) && connect){ //若line的內容不是"end"且connect為true時執行以下內容
			out.println(line); //把line的內容放到out中
			line = reader.readLine(); //擷取下一段內容
		}
	}
	
	public void deliver(String msg){
		out.println(msg);
	}
	
	public void execute(String msg){
		String[] str = msg.split(" ");
		System.out.println("From server: "+msg);
		switch (str[0]){ // search instruction
		case "Number":
			player.setPlayerNumber(Integer.parseInt(str[1]));
			break;
		case "Name":
			player.setPlayerName(Integer.parseInt(str[1]),str[2]);
			break;
		case "State":
			player.setReadyState(Integer.parseInt(str[1]),str[2]);
			break;
		case "Level":
			player.setLevel(Integer.parseInt(str[1]));
			break;
		case "Message":
			player.appendMessage(Integer.parseInt(str[1]),str[2]);
			break;
		case "Ready":
			player.setReadyState(Integer.parseInt(str[1]),str[2]);
			break;
		case "Start":
			player.startGame();
			break;
		case "RightOfFlop":
			player.startFlop(Integer.parseInt(str[1]),Integer.parseInt(str[2]));
			break;
		case "Flop":
			player.flopCard(Integer.parseInt(str[1]));
			break;
		case "Flow":
			player.flowCard(Integer.parseInt(str[1]));
			break;
		case "Add":
			player.addPoint(Integer.parseInt(str[1]),Integer.parseInt(str[2]));
			break;
		case "SetImage":
			player.setButtonImage(Integer.parseInt(str[1]),str[2]);
			break;
		case "Reset":
			player.systemReset();
			break;
		case "Close":
			try{
				client.close(); //關閉socket
			}catch (IOException e){
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}
	
	public static void main(String [] args) {
		new Client();  //創立新的客戶端
	}
}

class Game extends JFrame {
	
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
	JLabel[] player = new JLabel[4]; // score panel player name info
	JLabel[] score = new JLabel[4]; //score panel player score info
	MyButton[] gameBtn; // game panel game button
	JButton start; // control panel start button
	JButton ready; // control panel ready button
	JButton reset; // control panel reset button
	JRadioButton[] rb; // control panel level button
	JTextArea chatbox; // chatbox panel chat room
	JTextField message; // chatbox panel input message field
	JButton enter; // chatbox panel deliver msg button
	MyMenuBar mmb; // menubar display
	
	Client client = null;
	Timer timer = null;
	
	String name="1",IP="1",port="1"; // login record
	
	int[] widthAmount={5,8,10,10}; // card row quantity
	int[] heightAmount={4,5,6,8}; // card column quantity
	int level = 0; // game difficulty
	int playerNumber = 0; // player serial number
	String[] playerName = {"player1","player2","player3","player4"}; // player name
	boolean[] readyState = {false,false,false,false}; // record current player status
	int[] playerScore = {0,0,0,0}; // player current score
	int correctScore = 10; // player add score when guess right
	boolean flop = false; // if player has authority to flop
	private boolean[] hasBeenOpened; // an array to determine if card has been opened
	private ImageIcon[] cardImage; // an array represent card
	private int firstOpen=-1; // every round first opened card
	private int secondOpen=-1; // every round second opened card
	
	public Game(Client client){
		this.client = client;
		LoginPane(main);
		setContentPane(main);
		setTimer();
		setTitle("MakeYouCry");
		setResizable(false);
		setVisible(true);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void LoginPane(Container pane) {
		// component initialize
		JLabel t1 = new JLabel("Name: ");
		JLabel t2 = new JLabel("IP: ");
		JLabel t3 = new JLabel("Port: ");
		PlayerName = new JTextField("Rick",20);
		IPAddress = new JTextField("192.168.2.105",20);
		PortNumber = new JTextField("6666",20);
		confirm = new JButton("OK");
		mmb = new MyMenuBar();
		// confirm button add actionlistener
		confirm.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				connect();
			}
		});
		// composing
		mmb.setmenu(this);
		pane.setLayout(new GridBagLayout());
		pane.setBackground(Color.YELLOW);
		pane.add(t1, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		pane.add(t2, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		pane.add(t3, new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		pane.add(PlayerName, new GridBagConstraints(1, 0, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		pane.add(IPAddress, new GridBagConstraints(1, 1, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		pane.add(PortNumber, new GridBagConstraints(1, 2, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		pane.add(confirm, new GridBagConstraints(0, 3, 3, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	}
	public void connect(){
		name = PlayerName.getText();
		IP = IPAddress.getText();
		port = PortNumber.getText();
		if (client.connect(IP,Integer.valueOf(port))){
			main.removeAll();
			addComponentsToMainPane(main);
			client.deliver("Name "+name);
			client.deliver("GetInformation");
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
		gaming_zone.setBorder(BorderFactory.createTitledBorder("Game"));
		gaming_zone.setLayout(new GridLayout(heightAmount[level],widthAmount[level]));
		gameBtn = new MyButton[heightAmount[level]*widthAmount[level]];
		cardImage = new ImageIcon[heightAmount[level]*widthAmount[level]];
		for(int i = 0;i<heightAmount[level];i++)
			for(int j = 0;j<widthAmount[level];j++)
				makeButton(gaming_zone, i*widthAmount[level]+j);
		return gaming_zone;
	}
	
	public void makeButton(Container pane, int number) {
        gameBtn[number] = new MyButton(number,Color.WHITE);
		gameBtn[number].addActionListener(new GameAction(this));
        pane.add(gameBtn[number]);
	}
	
	public JPanel getControl(){
		control = new JPanel();
		GridBagLayout gridBagLayout = new GridBagLayout();
		control.setLayout(gridBagLayout);

		start = new JButton("Start");
		start.addActionListener(new GameConfirmAction(this));
		start.setFocusable(false);
		
		ready = new JButton("Ready");
		ready.addActionListener(new GameConfirmAction(this));
		ready.setFocusable(false);
		
		reset = new JButton("Reset");
		reset.addActionListener(new GameConfirmAction(this));
		reset.setFocusable(false);
		
        JLabel levelLabel = new JLabel("Choose level");
		
        ButtonGroup buttonGroup = new ButtonGroup();
		rb = new JRadioButton[4];
        ItemListener degree = new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()==e.SELECTED){
                    if(e.getSource() == rb[0]) level = 0;
                    if(e.getSource() == rb[1]) level = 1;
                    if(e.getSource() == rb[2]) level = 2;
                    if(e.getSource() == rb[3]) level = 3;
					updateLevel();
                }
            }
        };
		for (int i=0;i<4;i++){
			rb[i] = new JRadioButton(String.valueOf(widthAmount[i]*heightAmount[i])+" cards");
			if (i==0) rb[i].setSelected(true);
			rb[i].addItemListener(degree);
			rb[i].setFocusable(false);
			buttonGroup.add(rb[i]);
		}
		
        control.add(levelLabel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		for (int i=0;i<4;i++)
			control.add(rb[i], new GridBagConstraints(i+1, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		control.add(reset, new GridBagConstraints(4, 1, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		control.add(start, new GridBagConstraints(2, 1, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		control.add(ready, new GridBagConstraints(3, 1, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		control.setBorder(BorderFactory.createTitledBorder("Control"));
		
		return control;
	}
	
	public JPanel getScoreboard(){
		scoreboard = new JPanel();
		GridBagLayout gridBagLayout = new GridBagLayout();
		scoreboard.setBorder(BorderFactory.createTitledBorder("Score"));
		scoreboard.setLayout(gridBagLayout);
		for (int i=0;i<4;i++){
			String state = readyState[i]?"Ready":"Unready";
			player[i] = new JLabel(playerName[i]+": ");
			score[i] = new JLabel(state);
			score[i].setHorizontalAlignment(JLabel.RIGHT);
			scoreboard.add(player[i], new GridBagConstraints(0, i, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
			scoreboard.add(score[i], new GridBagConstraints(1, i, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		}
		return scoreboard;
	}
	
	public JPanel getTextArea(){
		text_area = new JPanel();
		GridBagLayout gridBagLayout = new GridBagLayout();
		text_area.setBorder(BorderFactory.createTitledBorder("ChatBox"));
		text_area.setLayout(gridBagLayout);
		chatbox = new JTextArea(20,20);
		chatbox.setEditable(false); 
		message = new JTextField(20);
		message.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				if (e.getKeyCode()==KeyEvent.VK_ENTER) deliverMsg();
			}
		});
		enter = new JButton("Enter");
        enter.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                deliverMsg();
            }
        });
		JScrollPane chatbox_ScrollPane = new JScrollPane(chatbox);
		JScrollPane message_ScrollPane = new JScrollPane(message);
		text_area.add(chatbox_ScrollPane, new GridBagConstraints(0, 0, 2, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		text_area.add(message_ScrollPane, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.SOUTH, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		text_area.add(enter, new GridBagConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.SOUTH, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		return text_area;
	}
	private void deliverMsg(){
		String msg = message.getText();
		if (!msg.equals("")){
			chatbox.append(playerName[playerNumber]+": "+msg+"\n");
			client.deliver("Message "+playerNumber+" "+msg);
		}
		message.setText("");
	}
	
	public JPanel getPlayerIP(){
		player_IP = new JPanel();
		GridBagLayout gridBagLayout = new GridBagLayout();
		player_IP.setBorder(BorderFactory.createTitledBorder("IP&port&name"));
		player_IP.setLayout(gridBagLayout);
		JTextArea show = new JTextArea(2,2);
		show.append("IP: "+IP+"\nPort: "+port+"\nName: "+name);
		show.setEditable(false); 
		player_IP.add(show,new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		return player_IP;
	}
	
	public void addComponentsToMainPane(Container pane) {
		pane.setLayout(new GridBagLayout());
		pane.add(getGameZone(),new GridBagConstraints(0, 0, 5, 5, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		pane.add(getControl(),new GridBagConstraints(0, 5, 3, 1, 1, 1, GridBagConstraints.SOUTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		pane.add(getScoreboard(),new GridBagConstraints(5, 0, 1, 2, 1, 1, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		pane.add(getTextArea(),new GridBagConstraints(5, 2, 1, 3, 1, 1, GridBagConstraints.SOUTHEAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		pane.add(getPlayerIP(),new GridBagConstraints(5, 5, 1, 1, 1, 1, GridBagConstraints.SOUTHEAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	}
	
/****************************Control button action****************************/	
	public void start(){
		for (int i=1;i<4;i++) if (!readyState[i]) return;
		client.deliver("Start");
		cardImage = GenerateImage.generate(level,widthAmount[level]*heightAmount[level]);
		for (int i=0;i<gameBtn.length;i++){
			gameBtn[i].setImage(cardImage[i]);
			client.deliver("SetImage "+i+" "+cardImage[i].toString());
		}
		startGame();
	}
	public void ready(){
		readyState[playerNumber] = readyState[playerNumber]?false:true;
		String state = readyState[playerNumber]?"Ready":"Unready";
		player[playerNumber].setText(playerName[playerNumber]+": ");
		score[playerNumber].setText(state);
		client.deliver("Ready "+playerNumber+" "+String.valueOf(readyState[playerNumber]));
	}
	public void updateLevel(){
		renewLevelSize();
		client.deliver("Level "+level);
	}
	public void renewLevelSize(){
		gaming_zone.removeAll();
		main.add(getGameZone(),new GridBagConstraints(0, 0, 5, 5, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		gaming_zone.revalidate();
		gaming_zone.repaint();
		pack();
	}
	public void reset(){
		systemReset();
		client.deliver("Reset");
	}
/****************************Control button action****************************/	
/***************************Instruction from server***************************/
	public void setPlayerNumber(int n){
		playerNumber = n;
		if (playerNumber == 0) honorButtonOpenSet();
		if (playerNumber != 0) playerButtonOpenSet();
	}
	
	public void systemReset(){
		if (playerNumber==0) honorButtonOpenSet();
		else playerButtonOpenSet();
		for (int i=0;i<4;i++){
			readyState[i] = (i==0)?true:false;
			playerScore[i]=0;
			String state = readyState[i]?"Ready":"Unready";
			score[i].setText(state);
		}
		correctScore = 10;
		flop = false;
		firstOpen=-1;
		secondOpen=-1;
		renewLevelSize();
	}
		
	private void honorButtonOpenSet(){
		start.setEnabled(true);
		ready.setEnabled(false);
		reset.setEnabled(false);
		for (JRadioButton tmp: rb) tmp.setEnabled(true);
	}
	private void playerButtonOpenSet(){
		start.setEnabled(false);
		ready.setEnabled(true);
		reset.setEnabled(false);
		for (JRadioButton tmp: rb) tmp.setEnabled(false);
	}
	public void setPlayerName(int n, String name){
		playerName[n] = name;
	}
	public void setReadyState(int n, String b){
		readyState[n] = Boolean.valueOf(b);
		String state = readyState[n]?"Ready":"Unready";
		player[n].setText(playerName[n]+": ");
		score[n].setText(state);
	}
	public void setLevel(int n){
		level = n;
		renewLevelSize();
	}
	public void appendMessage(int n,String msg){
		chatbox.append(playerName[n]+": "+msg+"\n");
	}
	public void startGame(){
		for (MyButton tmp: gameBtn) tmp.setEnabled(true);
		for (int i=0;i<4;i++){
			player[i].setText(playerName[i]+": ");
			score[i].setText(String.valueOf(playerScore[i]));
		}
		if (playerNumber == 0) honorButtonCloseSet();
		if (playerNumber != 0) playerButtonCloseSet();
		hasBeenOpened = new boolean[gameBtn.length];
		Arrays.fill(hasBeenOpened,false);
	}
	private void honorButtonCloseSet(){
		start.setEnabled(false);
		reset.setEnabled(true);
		for (JRadioButton tmp: rb) tmp.setEnabled(false);
	}
	private void playerButtonCloseSet(){
		ready.setEnabled(false);
	}
	public void startFlop(int number,int round){
		if (playerNumber == number){
			flop = true;
			chatbox.append("System: round "+round+" your term\n");
		}
	}
	public void flopCard(int number){
		gameBtn[number].showPos();
		gameBtn[number].repaint();
		hasBeenOpened[number] = true;
	}
	public void flowCard(int number){
		gameBtn[number].showNeg();
		gameBtn[number].repaint();
		hasBeenOpened[number] = false;
	}
	public void addPoint(int n,int s){
		playerScore[n]+=s;
		score[n].setText(String.valueOf(playerScore[n]));
	}
	public void setButtonImage(int n,String url){
		cardImage[n] = new ImageIcon(url);
		gameBtn[n].setImage(cardImage[n]);
	}
/***************************Instruction from server***************************/
/***************************Start play game***********************************/
	public void setTimer(){
		timer = new Timer(500,new ActionListener(){
			public void actionPerformed(ActionEvent e){
				flow();
			}
		});
		timer.setRepeats(false);
	}
	public boolean canFlop(){
		return flop;
	}
	public boolean tryOpen(int n){
		if (hasBeenOpened[n]) return false;
		if (firstOpen==-1){
			flopCard(n);
			client.deliver("Flop "+n);
			firstOpen = n;
		}else{
			flopCard(n);
			client.deliver("Flop "+n);
			secondOpen = n;
			compareResult();
		}
		return true;
	}
	private void compareResult(){
		if (gameBtn[firstOpen].getImageURL().equals(gameBtn[secondOpen].getImageURL())){
			client.deliver("Add "+String.valueOf(playerNumber)+" "+String.valueOf(correctScore));
			addPoint(playerNumber,correctScore);
			correctScore+=10;
			firstOpen = -1;
			secondOpen = -1;
		}else{
			timer.start();
		}
	}
	private void flow(){
		flowCard(firstOpen);
		client.deliver("Flow "+firstOpen);
		flowCard(secondOpen);
		client.deliver("Flow "+secondOpen);
		flop = false;
		client.deliver("RoundEnd");
		firstOpen = -1;
		secondOpen = -1;
		correctScore = 10;
	}
/***************************Start play game***********************************/
}
class GameAction implements ActionListener{
	
	Game f;
	
	public GameAction(Game f){
		this.f = f;
	}
	
	public void actionPerformed(ActionEvent e){
		if (f.canFlop()){
			MyButton tmp = (MyButton)e.getSource();
			f.tryOpen(tmp.getNumber());
		}
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
		if (tmp.getText().equals("Reset")) f.reset();
	}
}