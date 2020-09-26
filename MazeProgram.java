import java.awt.*;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class MazeProgram extends JPanel implements KeyListener,MouseListener{
	JFrame frame;
	ArrayList<ArrayList<Wall>> walls;
	File name;
	Wall ceiling;
	Wall floor;
	Explorer exp;
	boolean right, left;
	String direction;
	ArrayList<String> dir;
	int angle;
	int rCount, lCount;
	int currentRow, currentCol;
	BufferedImage light;
	int lightRow, lightCol;
	int trapRow, trapCol;
	boolean haveLight;
	Location start, end;
	int sprayLeft;
	ArrayList<Location> paint;
	Color bg;
	float dim;
	Graphics2D g2d;

	public MazeProgram(){
		walls = new ArrayList<ArrayList<Wall>>();
		setBoard();
		frame = new JFrame();
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1500,800);
		frame.setVisible(true);
		frame.addKeyListener(this);
		exp = new Explorer(new Location(start.getX(), start.getY()));
		right = false;
		left = false;
		direction = "N";
		haveLight = false;
		angle = 180;
		dim = 0;
		sprayLeft = 5;
		paint = new ArrayList<>();
		dir = new ArrayList<>(Arrays.asList("N", "E", "S", "W"));

		//this.addMouseListener(this); //in case you need mouse clicking
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g2d = (Graphics2D) g;

		g.setColor(Color.BLACK);
		g.fillRect(0,0,1500,800);

		//drawBoard here!
		g.setColor(Color.WHITE);
		g.setFont(new Font("Times New Roman",Font.PLAIN,18));
		g.drawString("there is a hidden trap door that brings you back to beginning. be careful!",10,470);
		g.drawString("capture the light to see again",10,490);
		g.drawString("press space bar to spray paint a floor",10,510);
		g.drawString("number of sprays left: " + sprayLeft,10,530);

		draw2DMaze(walls, g, g2d);
		draw3DMaze(g, g2d);

		g.setColor(new Color(0,0,0,dim)); // 50% darker (change to 0.25f for 25% darker)
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	public void setBoard(){
		name = new File("MazeDesign1.txt");

		getMaze(name);

		setWalls();
	}

	public void setWalls(){
		//when you're ready for the 3D part
		int[] cX={600,1300,1050,850};
		int[] cY={50,50,300,300};
		int[] fX={850,1050,1300,600};
		int[] fY={500,500,750,750};

		ceiling = new Wall(cX,cY,4);
		floor = new Wall(fX,fY,4);
	}

	public void keyPressed(KeyEvent e){
		if(e.getKeyCode() == 37){									//left
			if (angle == 360)
				angle = 90;
			else
				angle += 90;

			if (dir.indexOf(direction) != 0)
				direction = dir.get(dir.indexOf(direction)-1);
			else
				direction = dir.get(3);
		}
		if(e.getKeyCode() == 38){									//up
			Location newLoc = new Location(0,0);

			if (angle == 180)
				newLoc = new Location(exp.getX(), exp.getY()-50);
			else if (angle == 90)
				newLoc = new Location(exp.getX()+50, exp.getY());
			else if (angle == 0 || angle == 360)
				newLoc = new Location(exp.getX(), exp.getY()+50);
			else if (angle == 270)
				newLoc = new Location(exp.getX()-50, exp.getY());

			if (walls.get(newLoc.getY()/50).get(newLoc.getX()/50) == null)
				exp.move(newLoc);


			if (!haveLight)
				dim+=0.025;


			System.out.println("light pos: " + lightRow + ", " + lightCol);
			System.out.println("start pos: " + start.getX() + ", " + start.getY());
			System.out.println("end pos: " + end.getX()+ ", " + end.getY());
			System.out.println("trap pos: " + trapRow+ ", " + trapCol);
			System.out.println("haveLight: " + haveLight);
			System.out.println("exp loc: " + exp.getY()/50 + ", " + exp.getX()/50);

		}
		if(e.getKeyCode() == 39){									//right
			if (angle == 0)
				angle = 270;
			else
				angle -= 90;

			if (dir.indexOf(direction) != 3)
				direction = dir.get(dir.indexOf(direction)+1);
			else
				direction = dir.get(0);

		}
		if(e.getKeyCode() == 40){									//down
			exp.move(new Location(exp.getX(), exp.getY()));
		}
		if(sprayLeft>0 && e.getKeyCode() == 32){						//enter
			sprayLeft-=1;
			paint.add(new Location(exp.getX(), exp.getY()));

			System.out.println("spray paint table: " + paint.get(paint.size()-1).getY()/50 + ", " + paint.get(paint.size()-1).getX()/50);
			System.out.println("spray paint frame: " + paint.get(paint.size()-1).getX() + ", " + paint.get(paint.size()-1).getY());
		}
		repaint();
	}

	public void getMaze(File name){
		try{
			BufferedReader input = new BufferedReader(new FileReader(name));
			String text = "";
			ArrayList<ArrayList<String>> maze = new ArrayList<ArrayList<String>>();
			while( (text=input.readLine())!= null){
				maze.add(new ArrayList<String>(Arrays.asList(text.split(""))));
			}
			for (int a=0; a<maze.size(); a++){
				walls.add(new ArrayList<Wall>());
				for (int b=0; b<maze.get(0).size(); b++){
					if (maze.get(a).get(b).equals("0"))
						walls.get(a).add(new Wall(new int[] {250,300,300,250}, new int[] {100,125,325,350}, 4));
					else{
						walls.get(a).add(null);
						if (maze.get(a).get(b).equals("S"))
							start = new Location(b*50,a*50);
						if (maze.get(a).get(b).equals("E"))
							end = new Location(b*50,a*50);
					}
				}
			}

			ArrayList<Integer> row = new ArrayList<>();
			ArrayList<Integer> col = new ArrayList<>();
			for (int a=0; a<walls.size(); a++){
				for (int b=0; b<walls.get(0).size(); b++){
					if (walls.get(a).get(b) == null){
						row.add(a);
						col.add(b);
					}
				}
			}
			int rand = (int)(Math.random()*row.size()-1)*1;
			lightRow = row.get(rand);
			lightCol = col.get(rand);
			int rand1 = (int)(Math.random()*row.size()-1)*1;
			trapRow = row.get(rand1);
			trapCol = col.get(rand1);

		}catch (IOException io){
			System.err.println("File error");
		}
	}

	public void draw2DMaze(ArrayList<ArrayList<Wall>> walls, Graphics g, Graphics2D g2d){
		try{
		  light = ImageIO.read(new File("light.png"));
		} catch (IOException io) {
			System.err.println("File error");
		}

		g.setColor(Color.WHITE);

		int c = 0;
		for (int a=0; a<walls.size(); a++){
			for (int b=0; b<walls.get(0).size(); b++){
				if (walls.get(a).get(b) == null){
					g.fillRect(c,a*50,50,50);
					if (a==lightRow && b==lightCol)
						 g.drawImage(light.getScaledInstance(50, 50, Image.SCALE_DEFAULT),lightCol*50,lightRow*50,null);
					c+=50;
				}
				else{
					g.drawRect(c,a*50,50,50);
					c+=50;
				}
			}
			c = 0;
		}

		if (lightCol*50==exp.getX() && lightRow*50==exp.getY()){
			lightCol = 1000;
			lightRow = 1000;
			haveLight = true;
			dim = 0f;
		}

		if (trapCol*50==exp.getX() && trapRow*50==exp.getY()){
			exp.move(new Location(start.getX(), start.getY()));
			trapCol = 1000;
			trapRow = 1000;
		}

		if (end.getX()==exp.getX() && end.getY()==exp.getY()){
			g.setColor(Color.RED);
			g.fillRect(50,175,400,100);
			g.setColor(Color.WHITE);
			g.setFont(new Font("Brush Script MT",Font.PLAIN,50));
			g.drawString("congratulations!",125,225);
			g.drawString("you made it out!",115,250);
			dim = 0f;
			haveLight = true;
			frame.removeKeyListener(this);
		}

		for (int a=0; a<paint.size(); a++){
			g.setColor(Color.RED);
			g.fillRect(paint.get(a).getX(),paint.get(a).getY(),50,50);
			g.setColor(Color.WHITE);
		}

		g2d.setColor(Color.yellow);


		if (right)
			g2d.fillArc(exp.getX(),exp.getY(),50,50,angle,180);
		else if (left)
			g2d.fillArc(exp.getX(),exp.getY(),50,50,angle,180);
		else
			g2d.fillArc(exp.getX(),exp.getY(),50,50,angle,180);
	}

	public void draw3DMaze(Graphics g, Graphics2D g2d){
		Color c1 = new Color(255, 255, 255);
		Color c2 = new Color(0, 0, 0);
		GradientPaint gp = new GradientPaint(0, 50, c1, 0, 250, c2);
		g2d.setPaint(gp);
		g.fillPolygon(ceiling.getXArr(), ceiling.getYArr(), ceiling.getPoints());

		c1 = new Color(0, 0, 0);
		c2 = new Color(255, 255, 255);
		gp = new GradientPaint(0, 500, c1, 0, 750, c2);
		g2d.setPaint(gp);
		g.fillPolygon(floor.getXArr(), floor.getYArr(), floor.getPoints());

		currentRow = exp.getY()/50;
		currentCol = exp.getX()/50;
		System.out.println("exp: " + currentRow + ", " + currentCol);

		for (int a=0; a<paint.size(); a++){
			if (currentRow==paint.get(a).getY()/50 && currentCol==paint.get(a).getX()/50){
				g.setColor(Color.RED);
				g.fillPolygon(floor.getXArr(), floor.getYArr(), floor.getPoints());
			}
		}


		for(int d=4;d>=0;d--){

			Color color = new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
			g.setColor(color);

			int[] lx={600+50*d,650+50*d,650+50*d,600+50*d};
			int[] ly={50+50*d,100+50*d,700-50*d,750-50*d};
			int[] rx={1250-50*d,1300-50*d,1300-50*d,1250-50*d};
			int[] ry={100+50*d,50+50*d,750-50*d,700-50*d};
			int[] wall={600+d*50,50+d*50,700-d*100,700-d*100};
			switch(direction){

				case "N":
					if(currentCol-1<0 || currentRow-d<0 || walls.get(currentRow-d).get(currentCol-1)!=null){
						g.setColor(color);
						g.fillPolygon(lx,ly,4);
					}
					else{
						g.setColor(Color.WHITE);
						g.fillPolygon(lx,ly,4);
					}
					if(currentCol+1>=walls.get(0).size() || currentRow-d<0 || walls.get(currentRow-d).get(currentCol+1)!=null){
						g.setColor(color);
						g.fillPolygon(rx,ry,4);
					}
					else{
						g.setColor(Color.WHITE);
						g.fillPolygon(rx,ry,4);
					}
					if(currentRow-d<0 || walls.get(currentRow-d).get(currentCol)!=null){
						g.setColor(color);
						g.fillRect(wall[0],wall[1],wall[2],wall[3]);
					}
					if (currentRow-d==lightRow && currentCol==lightCol)
						g.drawImage(light.getScaledInstance(wall[2], wall[3], Image.SCALE_DEFAULT),wall[0],wall[1],null);
					break;

				case "S":
					if(currentRow+d>=walls.size() || currentCol+1>=walls.get(0).size() || walls.get(currentRow+d).get(currentCol+1)!=null){
						g.setColor(color);
						g.fillPolygon(lx,ly,4);
					}
					else{
						g.setColor(Color.WHITE);
						g.fillPolygon(lx,ly,4);
					}

					if(currentRow+d>=walls.size() || currentCol-1<0 || walls.get(currentRow+d).get(currentCol-1)!=null){
						g.setColor(color);
						g.fillPolygon(rx,ry,4);
					}
					else{
						g.setColor(Color.WHITE);
						g.fillPolygon(rx,ry,4);
					}

					if(currentRow+d>=walls.size() || walls.get(currentRow+d).get(currentCol)!=null){
						g.setColor(color);
						g.fillRect(wall[0],wall[1],wall[2],wall[3]);
					}

					if (currentRow+d==lightRow && currentCol==lightCol)
						g.drawImage(light.getScaledInstance(wall[2], wall[3], Image.SCALE_DEFAULT),wall[0],wall[1],null);

					break;

				case "E":
					if(currentRow-1<0 || currentCol+d>=walls.get(0).size() || walls.get(currentRow-1).get(currentCol+d)!=null){
						g.setColor(color);
						g.fillPolygon(lx,ly,4);
					}
					else{
						g.setColor(Color.WHITE);
						g.fillPolygon(lx,ly,4);
					}

					if(currentRow+1>=walls.size() || currentCol+d>=walls.get(0).size() || walls.get(currentRow+1).get(currentCol+d)!=null){
						g.setColor(color);
						g.fillPolygon(rx,ry,4);
					}
					else{
						g.setColor(Color.WHITE);
						g.fillPolygon(rx,ry,4);
					}

					if(currentCol+d>=walls.get(0).size() || walls.get(currentRow).get(currentCol+d)!=null){
						g.setColor(color);
						g.fillRect(wall[0],wall[1],wall[2],wall[3]);
					}

					if (currentCol+d==lightCol && currentRow==lightRow)
						g.drawImage(light.getScaledInstance(wall[2], wall[3], Image.SCALE_DEFAULT),wall[0],wall[1],null);

					break;

				case "W":
					if(currentRow+1>=walls.size() || currentCol-d<0 || walls.get(currentRow+1).get(currentCol-d)!=null){
						g.setColor(color);
						g.fillPolygon(lx,ly,4);
					}
					else{
						g.setColor(Color.WHITE);
						g.fillPolygon(lx,ly,4);
					}

					if(currentCol-d<0 || currentRow-1<0 || walls.get(currentRow-1).get(currentCol-d)!=null){
						g.setColor(color);
						g.fillPolygon(rx,ry,4);
					}
					else{
						g.setColor(Color.WHITE);
						g.fillPolygon(rx,ry,4);
					}

					if(currentCol-d<0 || walls.get(currentRow).get(currentCol-d)!=null){
						g.setColor(color);
						g.fillRect(wall[0],wall[1],wall[2],wall[3]);
					}

					if (currentCol-d==lightCol && currentRow==lightRow)
						g.drawImage(light.getScaledInstance(wall[2], wall[3], Image.SCALE_DEFAULT),wall[0],wall[1],null);

					break;
			}

		}
	}

	public void keyReleased(KeyEvent e){
	}

	public void keyTyped(KeyEvent e){
	}

	public void mouseClicked(MouseEvent e){
	}

	public void mousePressed(MouseEvent e){
	}

	public void mouseReleased(MouseEvent e){
	}

	public void mouseEntered(MouseEvent e){
	}

	public void mouseExited(MouseEvent e){
	}

	public static void main(String args[]){
		MazeProgram app=new MazeProgram();
	}
}