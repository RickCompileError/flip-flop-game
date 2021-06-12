// File Name Server.java
import java.net.*;
import java.io.*;
import java.util.*;

public class Server{
	
	protected static List<Socket> sockets = new Vector<>(); //將接收到的socket變成一個集合
	protected static int player = -1;
	protected static boolean[] readyState = {true,false,false,false};
	protected static String[] playerName = {"player1","player2","player3","player4"};
	protected static int playerAmount = 0;
	protected static int round = -1;
	protected static int rounds = 1;
	protected static int level = 0;
	protected static int[] cardAmount = {10,20,30,40};
	protected static int countAdd = 0;
	
	public static void main(String[] args) throws IOException{
		ServerSocket serverSocket = new ServerSocket(6666); //建立服務端
		while(playerAmount<=4) {
			try{
				System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
				Socket accept = serverSocket.accept(); //阻塞等待客戶端連線
				player++;
				playerAmount++;
				synchronized (sockets){
					sockets.add(accept); //接受客戶端請求
				}
				Thread thread = new Thread(new ServerThread(accept)); //多個伺服器執行緒進行對客戶端的響應
				thread.start(); //啟用執行緒
			}
			catch (IOException e) { //異常時顯示錯誤訊息
				e.printStackTrace();
				break;
			}
		}
		serverSocket.close(); //關閉伺服器
	}
}

class ServerThread extends Server implements Runnable {

	private Socket socket; //宣告名為socket的socket
	private String socketName;
	
	private BufferedReader reader; //宣告名為reader的BufferedReader

	public ServerThread(Socket socket){ //伺服器執行緒，主要處理多個客戶端請求
		this.socket = socket; //初始化socket內容
		System.out.println(playerName[player]+" joined the room."); //顯示哪一個客戶端已經加入
		socketName = playerName[player];
	}

	public void run(){
		try{
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while (true){
				String line = reader.readLine(); //設定line為接收到的內容
				System.out.println("From "+socketName+": "+line); //debug
				String[] instruction = line.split(" ");
				if (line == null) break; //當達成條件時則跳出迴圈
				switch (instruction[0]){
				case "GetInformation": // deliver another player information (name and state)
					transmitInfo();
					break;
				case "Name": // set user name
					playerName[player] = instruction[1]; 
					socketName = instruction[1];
					break;
				case "CheckStart":
					checkStart();
					break;
				case "Start":
					print(line);
					nextPlayer();
					break;
				case "Level":
					print(line);
					level = Integer.parseInt(instruction[1]);
					break;
				case "Ready":
					setReady(Integer.parseInt(instruction[1]),Boolean.valueOf(instruction[2]));
					break;
				case "RoundEnd":
					nextPlayer();
					break;
				case "Reset":
					print(line);
					reset();
					break;
				case "Add":
					print(line);
					detectEnd();
					break;
				default: //使用print function輸出內容
					print(line);
				}
			}
			closeConnect(); //斷開連線
		}
		catch (IOException e) { //異常時斷開連線
			closeConnect();
		}
	}
	
	public void print(String msg) throws IOException{
		PrintWriter out = null; //宣告out
		synchronized (sockets){ //把sockets鎖住，讓他專注執行下面的程序
			for (Socket sc : sockets){ //一個一個廣播出去
				if (socket == sc) continue; 
				out = new PrintWriter(sc.getOutputStream(),true); //設定out為socket輸出的內容
				out.println(msg); //把訊息放到out中
			}
		}
	}
	
	public void transmitInfo() throws IOException{
		PrintWriter out = null;
		out = new PrintWriter(socket.getOutputStream(),true);
		out.println("Number "+player);
		synchronized (sockets){
			for (Socket sc: sockets){
				out = new PrintWriter(sc.getOutputStream(),true);
				for (int i=0;i<4;i++){
					out.println("Name "+i+" "+playerName[i]);
					out.println("State "+i+" "+readyState[i]);
				}
			}
		}
	}
	
	public void checkStart() throws IOException{
		PrintWriter out = null;
		out = new PrintWriter(socket.getOutputStream(),true);
		for (int i=0;i<playerAmount;i++) if (!readyState[i]) return;
		out.println("CanStart");
	}
	
	public void nextPlayer() throws IOException{
		round++;
		if (round>playerAmount-1){
			rounds++;
			round=0;
		}
		PrintWriter out = null;
		synchronized (sockets){
			for (Socket sc: sockets){
				out = new PrintWriter(sc.getOutputStream(),true);
				out.println("RightOfFlop "+round+" "+rounds);
			}
		}
	}
	
	public void setReady(int i, boolean b) throws IOException{
		readyState[i] = b;
		print("Ready "+i+" "+b);
	}
	
	public void detectEnd() throws IOException{
		countAdd++;
		if (countAdd == cardAmount[level]){
			print("GameOver");
			reset();
		}
	}
	
	public void reset(){
		for (int i=0;i<4;i++) readyState[i] = (i==0)?true:false;
		round = -1;
		rounds = 1;
		countAdd=0;
	}
	
	public void closeConnect(){ //斷開連線的function
		try{
			System.out.println(socketName+" left the room."); //顯示哪一個客戶端離開
			synchronized (sockets){
				sockets.remove(socket); //移除server端與client所建立的socket
			}
			socket.close(); //關閉socket
			player--;
		}
		catch (IOException e){ //異常時顯示錯誤訊息
			System.out.println("Close not sucess.");
		}
	}
}