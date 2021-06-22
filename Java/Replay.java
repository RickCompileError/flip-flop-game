// File Name Replay.java
import java.net.*;
import java.io.*;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

class Replay extends JFrame {
	
	JPanel main = new JPanel();
	
	Timer timer = null;
	MyButton[] gameBtn; // game panel game button
	int[] widthAmount={5,8,10,10}; // card row quantity
	int[] heightAmount={4,5,6,8}; // card column quantity
	int level = 0; // game difficulty
	boolean flop = false; // if player has authority to flop
	private boolean[] hasBeenOpened; // an array to determine if card has been opened
	private ImageIcon[] cardImage; // an array represent card
	private int firstOpen=-1; // every round first opened card
	private int secondOpen=-1; // every round second opened card
	
	public Replay(){ //設置客戶端的遊戲畫面以及內容
		test();
		setContentPane(main);
		setTitle("Replay");
		setResizable(true);
		setVisible(true);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	


	public JPanel addComponentsToMainPane(Container pane) {  //設置遊戲區介面部分
		pane.setLayout(new GridLayout(heightAmount[level],widthAmount[level]));
		gameBtn = new MyButton[heightAmount[level]*widthAmount[level]];   
		cardImage = new ImageIcon[heightAmount[level]*widthAmount[level]];  
		for(int i = 0;i<heightAmount[level];i++)  //根據所選難度，去設置卡牌數量
			for(int j = 0;j<widthAmount[level];j++)
				makeButton(pane, i*widthAmount[level]+j);
	}
	
	public void makeButton(Container pane, int number) {    //建立卡牌
        gameBtn[number] = new MyButton(number,Color.WHITE);
		gameBtn[number].addActionListener(new GameAction(this));
        pane.add(gameBtn[number]);
	}
	
	public void test() throws IOException{
		FileReader fr = new FileReader("game_detail.txt");
		BufferedReader br = new BufferedReader(fr);
		Scanner scn =new Scanner(br);
		
		while(scn.hasNextLine()) {
			String[] s = scn.nextLine().split(" ");
			if(s[0].equals("Level"))
				setLevel(Integer.parseInt(s[1]));
			if(s[0].equals("Start")){
				//addComponentsToMainPane(main);
				start();
			}
		}
	}
	
	
	
	
	
	
	
	public void start(){  //按下start後的行為
		cardImage = GenerateImage.generate(level,widthAmount[level]*heightAmount[level]);  //產生與卡牌數量相同的圖片
		for (int i=0;i<gameBtn.length;i++){
			gameBtn[i].setImage(cardImage[i]);  //設置按鈕圖片
		}
		hasBeenOpened = new boolean[gameBtn.length];
		Arrays.fill(hasBeenOpened,false);
	}
	
	public void renewLevelSize(){  //重整遊戲區域介面，把按鈕數量設置為對應難度的卡牌數量
		main.removeAll();
		addComponentsToMainPane(main);
		main.revalidate();
		main.repaint();
		pack();
	}
	public void reset(){ //遊戲重置
		systemReset();
	}

	public void systemReset(){ //重置遊戲
		flop = false;
		firstOpen=-1;
		secondOpen=-1;
		renewLevelSize();
	}
		
	public void setLevel(int n){ //設置遊戲難度
		level = n;
		renewLevelSize();
	}

	public void flopCard(int number){ //翻牌
		gameBtn[number].showPos();
		gameBtn[number].repaint();
		hasBeenOpened[number] = true;
	}
	public void flowCard(int number){ //蓋牌
		gameBtn[number].showNeg();
		gameBtn[number].repaint();
		hasBeenOpened[number] = false;
	}
	
	public void setTimer(){
		timer = new Timer(500,new ActionListener(){
			public void actionPerformed(ActionEvent e){
				flow();
			}
		});
		timer.setRepeats(false);
	}
	public boolean canFlop(){ //判斷目前是否可翻牌
		return flop;
	}
	public boolean tryOpen(int n){ //翻牌
		if (hasBeenOpened[n]) return false;
		if (firstOpen==-1){
			flopCard(n);
			firstOpen = n;
		}else{
			flopCard(n);
			secondOpen = n;
			compareResult();
		}
		return true;
	}
	private void compareResult(){ //判斷兩張牌是否相同
		if (gameBtn[firstOpen].getImageURL().equals(gameBtn[secondOpen].getImageURL())){
			firstOpen = -1;
			secondOpen = -1;
		}else{
			timer.start();
		}
	}
	private void flow(){ //當兩張牌不一樣時則把牌蓋回去
		flowCard(firstOpen);
		flowCard(secondOpen);
		flop = false;
		firstOpen = -1;
		secondOpen = -1;
	}
}
				
/***************************Start play game***********************************/

//設置按鈕的action listener
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
