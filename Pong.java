import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.util.Random;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
class Pair{
    public double x;
    public double y;
    
    public Pair(double initX, double initY){
	x = initX;
	y = initY;
    }

    public Pair add(Pair toAdd){
	return new Pair(x + toAdd.x, y + toAdd.y);
    }

    public Pair divide(double denom){
	return new Pair(x / denom, y / denom);
    }

    public Pair times(double val){
	return new Pair(x * val, y * val);
    }

    public void flipX(){
	x = -x;
    }
    
    public void flipY(){
	y = -y;
    }
}

class Sphere{
    Pair position;
    Pair velocity;
    Pair acceleration;
    double radius;
    double dampening;
    Color color;
    public Sphere()
    {
	Random rand = new Random(); 
	position = new Pair(500.0, 500.0);
	velocity = new Pair((double)(rand.nextInt(1000) - 500), (double)(rand.nextInt(1000) - 500));
	acceleration = new Pair(0.0, 200.0);
	radius = 25;
	dampening = 1.3;
	color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
    }
    public void update(World w, double time){
	position = position.add(velocity.times(time));
	velocity = velocity.add(acceleration.times(time));
	bounce(w);
    }
    
    public void setPosition(Pair p){
	position = p;
    }
    public void setVelocity(Pair v){
	velocity = v;
    }
    public void setAcceleration(Pair a){
	acceleration = a;
    } 
    public void draw(Graphics g){
	Color c = g.getColor();
	
	g.setColor(color);
	g.drawOval((int)(position.x - radius), (int)(position.y - radius), (int)(2*radius), (int)(2*radius));
	g.setColor(c);
    }
    private void bounce(World w){
	Boolean bounced = false;
	if (position.x - radius < 0){
	    velocity.flipX();
	    position.x = radius;
	    bounced = true;
	}
	else if (position.x + radius > w.width){
	    velocity.flipX();
	    position.x = w.width - radius;
	    bounced = true;
	}
	if (position.y - radius < 0){
	    velocity.flipY();
	    position.y = radius;
	    bounced = true;
	}
	else if(position.y + radius >  w.height){
	    velocity.flipY();
	    position.y = w.height - radius;
	    bounced = true;
	}
	if (bounced){
	    velocity = velocity.divide(dampening);
	}
    }
    
}

class Rectangle{
    Pair position;
    Pair velocity;
    double height;
    double width;
    Color color;
    public Rectangle(Pair inPosition){
        Random rand = new Random();
        position = inPosition;
        velocity = new Pair(0,0);
        height = 100;
        width = 20;
        position.y += height/2;
        color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
    }
    public void update(World w, double time){
    position = position.add(velocity.times(time));
    //velocity = velocity.add(acceleration.times(time));
    //bounce(w);
    }
    
    public void setPosition(Pair p){
    position = p;
    }
    public void setVelocity(Pair v){
    velocity = v;
    }
   /* public void setAcceleration(Pair a){
    acceleration = a;
    } */
    public void draw(Graphics g){
    Color c = g.getColor();
    
    g.setColor(color);
    g.drawRect((int)(position.x - width), (int)(position.y - height), (int)width,(int) height);
    g.setColor(c);
    }
}

class World{
    int height;
    int width;
    
    int numSpheres;
    Sphere spheres[];
    Rectangle rectangles[];

    public World(int initWidth, int initHeight, int initNumSpheres, Pair[] initialPosition){
	width = initWidth;
	height = initHeight;

	numSpheres = initNumSpheres;
	spheres  = new Sphere[numSpheres];
    rectangles = new Rectangle[2];
	for (int i = 0; i < 2; i ++)
        {
        rectangles[i] = new Rectangle(initialPosition[i]);
        }

	for (int i = 0; i < numSpheres; i ++)
	    {
		spheres[i] = new Sphere();
	    }

    }

    public void drawSpheres(Graphics g){
	for (int i = 0; i < numSpheres; i++){
	    spheres[i].draw(g);
	}
    }

    public void drawRectangles(Graphics g){
        for(int i = 0; i<2; i++){
            rectangles[i].draw(g);
        }
    }

    public void updateSpheres(double time){
	for (int i = 0; i < numSpheres; i ++)
	    spheres[i].update(this, time);
    }

    public void updateRectangles(double time){
        for(int i = 0; i < 2; i++){
            rectangles[i].update(this, time);
        }
    }

}

public class Pong extends JPanel implements KeyListener{
    public static final int WIDTH = 1024;
    public static final int HEIGHT = 768;
    public static final int FPS = 60;
    World world;

    class Runner implements Runnable{
	public void run()
	{
	    while(true){
		world.updateSpheres(1.0 / (double)FPS);
        world.updateRectangles(1.0/ (double)FPS);
		repaint();
		try{
		    Thread.sleep(1000/FPS);
		}
		catch(InterruptedException e){}
	    }

	}
    
    }


    public void keyPressed(KeyEvent e) {
        char c=e.getKeyChar();
	System.out.println("You pressed down: " + c);



	if(c=='r' || c== 'u' || c== 'v' || c== 'n' || c=='f' || c=='j'){ //if the key pressed was one of these
		changeVelocity(c, world); //call this method to change the velocity of the rectangle

    }
}
    public void keyReleased(KeyEvent e) {
        char c=e.getKeyChar();
	System.out.println("\tYou let go of: " + c);
	
    }


    public void keyTyped(KeyEvent e) {
	char c = e.getKeyChar();
	System.out.println("You typed: " + c);
    }
     public void addNotify() {
        super.addNotify();
        requestFocus();
    }

    public Pong(){
    Pair[] initialPosition = new Pair[2];
    initialPosition[0]= new Pair(30.0,HEIGHT/2);
    initialPosition[1]= new Pair(WIDTH-10,HEIGHT/2);
	world = new World(WIDTH, HEIGHT, 1, initialPosition);
	addKeyListener(this);
	this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
	Thread mainThread = new Thread(new Runner());
	mainThread.start();
    }
    
    public static void main(String[] args){
	JFrame frame = new JFrame("Physics!!!");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	Pong mainInstance= new Pong();
	frame.setContentPane(mainInstance);
	frame.pack();
	frame.setVisible(true);
    }


    public void paintComponent(Graphics g) {
	super.paintComponent(g);    	

	g.setColor(Color.BLACK);
	g.fillRect(0, 0, WIDTH, HEIGHT);

	world.drawSpheres(g);
    world.drawRectangles(g);

    }

    public void changeVelocity(char c, World w){
    	char keyPressed = c;
       	Pair velocityup = new Pair(0,-100);
        Pair velocitydown = new Pair(0,100);
        Pair velocitystop = new Pair(0,0);
    	
    	switch(keyPressed){ //uses a switch with different cases to change gravity based upon which key is pressed
    		case 'r': //if w is pressed change gravity to the top of the screen
    		w.rectangles[0].velocity=velocityup;

            break;

    		case 'f':  //if a is pressed change gravity to the left of the screen
    		w.rectangles[0].velocity=velocitystop;
            break;

    		case 'j': //if s is pressed change gravity to the right of the screen
    		w.rectangles[1].velocity=velocitystop;
    		break;

    		case 'u': //if d is pressed change gravity to how we usually think of it, the bottom of the screen
    		w.rectangles[1].velocity=velocityup;
            break;
            
            case 'v': //if d is pressed change gravity to how we usually think of it, the bottom of the screen
            w.rectangles[0].setVelocity(velocitydown);
            break;

            case 'n': //if d is pressed change gravity to how we usually think of it, the bottom of the screen
            w.rectangles[1].velocity=velocitydown;

    		break;
    	}

    	//changeGravityHelper(velocity, w, paddle);//the swithc just changes the value of a pair so need to actually change gravity 
    }

    /*public void changeGravityHelper(Pair p, World w){ //changes the gravity of all of the spheres
    	for(int i=0; i<w.numSpheres; i++){ //for all of the spheres
    			w.spheres[i].setAcceleration(p); //change gravity to what we sent in
    		}
    }*/

    
}
