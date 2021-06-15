import javax.swing.*;
import java.awt.*;
import java.awt.color.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public class Settlement { 

	static JPanel panel = new JPanel();
	static JLabel rank1,rank2,rank3,rank4;
	static JLabel player1,player2,player3,player4;
	static JLabel score1,score2,score3,score4;
	 public Settlement(String[] name,int[] s){
		update(name,s);
		out();
	 }
	 
	public static void update(String[] name,int[] s){
		rank1 = new JLabel("No.1");
		rank2 = new JLabel("No.2");
		rank3 = new JLabel("No.3");
		rank4 = new JLabel("No.4");
		player1 = new JLabel(name[0]);
		player2 = new JLabel(name[1]);
		player3 = new JLabel(name[2]);
		player4 = new JLabel(name[3]);
		score1 = new JLabel(String.valueOf(s[0]));
		score2 = new JLabel(String.valueOf(s[1]));
		score3 = new JLabel(String.valueOf(s[2]));
		score4 = new JLabel(String.valueOf(s[3]));
	}
	
	public static JPanel getPanel(){
		panel.removeAll();
		panel.setLayout(new GridLayout(4,3));
		panel.add(rank1);
		panel.add(player1);
		panel.add(score1);
		panel.add(rank2);
		panel.add(player2);
		panel.add(score2);
		panel.add(rank3);
		panel.add(player3);
		panel.add(score3);
		panel.add(rank4);
		panel.add(player4);
		panel.add(score4);
		panel.revalidate();
		panel.repaint();
	return panel;	
	}
	
	public static void out(){
		JOptionPane.showMessageDialog(null,getPanel(),"result",JOptionPane.PLAIN_MESSAGE);
	}

}