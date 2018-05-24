package com.oracle.chessgame.main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Game  extends  JFrame{
	private boolean isGaiwo=false;
	private JMenuBar  menubar;
	private JMenu  menu,message;
	private JMenuItem  connection,faqi,exit,close;
	private ServerSocket  server;
	private Socket client;
	private  boolean  isgaiwoxia=true;
	private  Set<Integer>  duifangqizi=new HashSet<>();
	private  Set<Integer>  myqizi=new HashSet<>();
	private Set<JLabel>  allQizi=new HashSet<>();
	private JLayeredPane  fenceng;
	private JPanel  qipan,qizi;
	private JLabel beijingtu;
	private Listener  l;
	private DataInputStream  in;
	private DataOutputStream  out;
	//gridLayOut
	public Game() {
		l=new Listener();
		this.setSize(570, 600);
		this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setLayout(new GridLayout(15, 15));
		for(int i = 0; i <225; i++) {
			JLabel  zi=new JLabel();
			zi.setName(i+1+"");
			zi.addMouseListener(l);
			allQizi.add(zi);
			this.add(zi);
		}
		
		menubar=new JMenuBar();
		menu=new JMenu("游戏");
		connection=new JMenuItem("链接");
		connection.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				String ip=JOptionPane.showInputDialog(null, "请输入要链接的玩家的IP地址", "127.0.0.1");
				try {
					client=new Socket(ip, 8888);
					out=new DataOutputStream(client.getOutputStream());
					in=new DataInputStream(client.getInputStream());
					message.setText("您使用白棋，您先下棋!");
					isGaiwo=true;
					new Thread() {
						public void run() {
							while(true)
							{
								try {
									int qizi=in.readInt();
									for(JLabel  l:allQizi) {
										if(Integer.parseInt(l.getName())==qizi)
										{
											l.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().createImage("images/black.png")));
											duifangqizi.add(qizi);
											boolean result=checkWin("black",qizi);
											if(result)
											{
												JOptionPane.showMessageDialog(null, "对方赢了!");
											}
											break;
										}
									}

									isGaiwo=true;
									message.setText("您使用白棋，现在改您下棋了!");
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							
						};
					}.start();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "无法链接游戏玩家，请检查网络！", "温馨提示", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		faqi=new JMenuItem("发起游戏");
		faqi.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				try {
					server=new ServerSocket(8888);
					Socket  connectionUser=server.accept();
					in=new DataInputStream(connectionUser.getInputStream());
					out=new DataOutputStream(connectionUser.getOutputStream());
					message.setText("您使用黑棋，对方先下棋，您稍作休息!");
					isGaiwo=false;
					new Thread() {
						public void run() {
							while(true)
							{
								try {
									int qizi=in.readInt();
									for(JLabel  l:allQizi) {
										if(Integer.parseInt(l.getName())==qizi)
										{
											l.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().createImage("images/white.png")));
											duifangqizi.add(qizi);
											boolean result=checkWin("white",qizi);
											if(result)
											{
												JOptionPane.showMessageDialog(null, "对方赢了!");
											}
											break;
										}
									}

									isGaiwo=true;
									message.setText("您使用黑棋，现在改您下棋了!");
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							
						};
					}.start();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "服务器启动失败！", "温馨提示", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		exit=new JMenuItem("退出游戏");
		close=new JMenuItem("关闭游戏");
		message=new JMenu("暂未链接玩家!");
		menu.add(connection);
		menu.addSeparator();
		menu.add(faqi);
		menu.addSeparator();
		menu.add(exit);
		menu.addSeparator();
		menu.add(close);
		menubar.add(menu);
		menubar.add(message);
		this.setJMenuBar(menubar);
		
		this.paintComponents(getGraphics());
		this.paintAll(getGraphics());
		this.validate();
	}
	public static void main(String[] args) {
		new Game();
	}
	
	class Listener  extends MouseAdapter{
		@Override
		public void mouseClicked(MouseEvent e) {
			if(!isGaiwo)
			{
				JOptionPane.showMessageDialog(Game.this, "还没改你下，你想干啥!", "温馨提示", JOptionPane.WARNING_MESSAGE);
				return;
			}
			JLabel  l=(JLabel)e.getSource();
			
			if(myqizi.contains(l.getName())||duifangqizi.contains(l.getName()))
			{
				JOptionPane.showMessageDialog(Game.this, "你想弄啥咧?", "温馨提示", JOptionPane.WARNING_MESSAGE);
			}else
			{
				
					if(server==null)
					{
						l.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().createImage("images/white.png")));
						duifangqizi.add(Integer.parseInt(l.getName()));//当我这个棋子落定之后，应该把这个棋子编号存到我下的棋子的集合里面，方便后盘判断输赢
						boolean  result=checkWin("white", Integer.parseInt(l.getName()));
						if(result)
						{
							JOptionPane.showMessageDialog(null, "你赢了!");
						}
					}else
					{
						l.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().createImage("images/black.png")));
						myqizi.add(Integer.parseInt(l.getName()));//当我这个棋子落定之后，应该把这个棋子编号存到我下的棋子的集合里面，方便后盘判断输赢
						boolean  result=checkWin("black", Integer.parseInt(l.getName()));
						if(result)
						{
							JOptionPane.showMessageDialog(null, "你赢了!");
						}
					}
					isGaiwo=false;
					if(server==null)
					{
						message.setText("您是白棋，现在该对方下了!");
					}else
					{
						message.setText("您是黑旗，现在该对方下了!");
						
					}
					try {
						out.writeInt(Integer.parseInt(l.getName()));
						out.flush();
					} catch (NumberFormatException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				
				//当你点击到一个可以落子位置的时候讲该棋子显式出来，
				// 调用你写的程序算法，执行机器下棋的功能
				
					 //jiqizou();
			}
		}
	}
	
	private void  jiqizou() {
		int randomQizi=new Random().nextInt(225)+1;
		while(myqizi.contains(randomQizi)||duifangqizi.contains(randomQizi)) {
			randomQizi=new Random().nextInt(225)+1;
		}
		for(JLabel  l:allQizi) {
			if(Integer.parseInt(l.getName())==randomQizi)
			{
				l.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().createImage("images/white.png")));
				duifangqizi.add(randomQizi);
				boolean result=checkWin("white",randomQizi);
				break;
			}
		}
	}
	private boolean checkWin(String checkWho,int thisQiziNum) {
		if(checkWho.equals("black"))
		{
			//这是判断无棋子输赢的业务代码
			//方法接受了两个参数，1个是那种颜色的棋要判断输赢，另一个是当前这种颜色的棋下的棋子的编号
			
			//分四种情况(四个棋子连线的方向)
			//1判断横向有5个连续的棋子
			boolean  win=true;
			boolean  leftToRight=true;
			int right=1;
			int left=0;
			while(true) {
				if(leftToRight)
				{
					if(myqizi.contains(thisQiziNum+right)) {
						right++;
						if(right==5) {
							return true;
						}
						continue;
					}else
					{
						leftToRight=false;
						left=5-right;
						continue;
					}

				}else
				{
					if(myqizi.contains(thisQiziNum-left))
					{
						left--;
						if(left==0) {
							return true;
						}
						continue;
					}else
					{
						return false;
					}
				}
				
			}
//			if(myqizi.contains(thisQiziNum+1)&&myqizi.contains(thisQiziNum+2)&&myqizi.contains(thisQiziNum+3)&&myqizi.contains(thisQiziNum+4))
//			{
//				return  true;
//			}else if(myqizi.contains(thisQiziNum-1)&&myqizi.contains(thisQiziNum+1)&&myqizi.contains(thisQiziNum+2)&&myqizi.contains(thisQiziNum+3))
//			{
//				return true;
//			}
			
			//2.判断纵向
//			if(myqizi.contains(thisQiziNum+15)&&myqizi.contains(thisQiziNum+30)&&myqizi.contains(thisQiziNum+45)&&myqizi.contains(thisQiziNum+60))
//			{
//				return true;
//			}
//				return false;
		}else
		{
			if(duifangqizi.contains(thisQiziNum+1)&&duifangqizi.contains(thisQiziNum+2)&&duifangqizi.contains(thisQiziNum+3)&&duifangqizi.contains(thisQiziNum+4))
			{
				return  true;
			}else if(duifangqizi.contains(thisQiziNum-1)&&duifangqizi.contains(thisQiziNum+1)&&duifangqizi.contains(thisQiziNum+2)&&duifangqizi.contains(thisQiziNum+3))
			{
				return true;
			}
			
			//2.判断纵向
			if(duifangqizi.contains(thisQiziNum+15)&&duifangqizi.contains(thisQiziNum+30)&&duifangqizi.contains(thisQiziNum+45)&&duifangqizi.contains(thisQiziNum+60))
			{
				return true;
			}
			return false;
			
		}
	}

}
