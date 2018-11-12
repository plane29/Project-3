//I will try to only comment things that are different this code may be a bit messier
//because it was an earlier version of our pong code
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.util.Random;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.Font;
class Pair{ 
    public double x; 
    public double y;  
    public double magnitude; 
    
    public Pair(double initX, double initY){  
	x = initX;
	y = initY;
    magnitude = Math.pow(Math.pow(x,2)+Math.pow(y,2),.5);
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
    Pair midpoint;
    public Sphere(int width, int height)
    {

	Random rand = new Random(); 
    midpoint = new Pair((double)(width/2), (double)(height/2));
	position = midpoint;
    int leftright=1;
    if(rand.nextInt(2)==0){
        leftright=-1;
    }
	velocity = new Pair(700*leftright, 0);
	acceleration = new Pair(0.0, 0.0);
	radius = 20;
	dampening = 1;
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
	
	g.setColor(Color.WHITE);
	g.fillOval((int)(position.x - radius), (int)(position.y - radius), (int)(2*radius), (int)(2*radius));
	g.setColor(c);
    }
    private void bounce(World w){
    Random rand = new Random();
	Boolean bounced = false;
    double angle;
	if (position.x - radius < 0){
	    bounced = true;
        w.score[1]++;
        position = midpoint;
        velocity = new Pair(-700, 0);
        w.rectangles[0].position= w.rectangles[0].startingPosition;
        w.rectangles[1].position= w.rectangles[1].startingPosition;
	}
	else if (position.x + radius > w.width ){ 
	    bounced = true;
        w.score[0]++;
        position = midpoint;
        velocity = new Pair(700, 0);
        w.rectangles[0].position= w.rectangles[0].startingPosition;
        w.rectangles[1].position= w.rectangles[1].startingPosition;
	}
    else if ((position.x - radius<= w.rectangles[0].position.x + w.rectangles[0].width/2) 
            && position.y + radius >= w.rectangles[0].position.y - w.rectangles[0].height/2 
            && position.y -radius <= w.rectangles[0].position.y + w.rectangles[0].height/2
            && position.x + radius >=w .rectangles[0].position.x-w.rectangles[0].width/2){  //extra condition for if ball was behind the paddle
        bounced=true;
        if (position.y == w.rectangles[0].position.y){
            velocity.x = velocity.magnitude;
            velocity.y = 0;
        }
        else if (position.y <= w.rectangles[0].position.y - w.rectangles[0].height/2){
            velocity.x = Math.cos(-1.309)*velocity.magnitude;
            velocity.y = Math.sin(-1.309)*velocity.magnitude;
        }
        else if (position.y >= w.rectangles[0].position.y + w.rectangles[0].height/2){
            velocity.x = Math.cos(1.309)*velocity.magnitude;
            velocity.y = Math.sin(1.309)*velocity.magnitude;
        }
        else{
            angle = ((position.y - w.rectangles[0].position.y)/(w.rectangles[0].height/2))*1.309;
            velocity.x = Math.cos(angle)*velocity.magnitude;
            velocity.y = Math.sin(angle)*velocity.magnitude;

        }

    }
    else if ((position.x + radius>= w.rectangles[1].position.x - w.rectangles[1].width/2) 
        && position.y + radius >= w.rectangles[1].position.y - w.rectangles[1].height/2 
        && position.y -radius <= w.rectangles[1].position.y + w.rectangles[1].height/2
        && position.x - radius <=w .rectangles[1].position.x-w.rectangles[1].width/2){  //extra condition for if ball is behind paddle
        bounced=true;
        if (position.y == w.rectangles[1].position.y){ 
            velocity.x = -velocity.magnitude;
            velocity.y = 0;
        }
        else if (position.y <= w.rectangles[1].position.y - w.rectangles[1].height/2){
            velocity.x = -1*Math.cos(1.309)*velocity.magnitude;
            velocity.y = Math.sin(-1.309)*velocity.magnitude;
        }
        else if (position.y >= w.rectangles[1].position.y + w.rectangles[1].height/2){
            velocity.x = -1*Math.cos(1.309)*velocity.magnitude;
            velocity.y = Math.sin(1.309)*velocity.magnitude;
        }
        else{
            angle = ((position.y - w.rectangles[1].position.y)/(w.rectangles[1].height/2))*1.309;
            velocity.x = -1*Math.cos(angle)*velocity.magnitude;
            velocity.y = Math.sin(angle)*velocity.magnitude;

        }
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
    Pair startingPosition;
    public Rectangle(Pair inPosition){
        startingPosition = inPosition;
        Random rand = new Random();
        position = inPosition;
        velocity = new Pair(0,0);
        height = 150;
        width = 20;
        color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
    }
    public void update(World w, double time){
    if(!((position.y-(height/2) <=0) && velocity.y<0) && !(position.y+(height/2)>=w.height && velocity.y>0) ){
    position = position.add(velocity.times(time));
    }
    }
    
    public void setPosition(Pair p){
    position = p;
    }
    public void setVelocity(Pair v){
    velocity = v;
    }
    public void draw(Graphics g){
    Color c = g.getColor();
    
    g.setColor(color);
    g.fillRect((int)(position.x - width/2), (int)(position.y - height/2), (int)(width),(int)(height));
    g.setColor(c);
    }

}

class Nets{  //new class primarily to draw the nets
    public static void drawNets(int x, Graphics g, int height, int start){  //takes in an x position graphics a height and a starting position for horizontal lines
        g.setColor(Color.WHITE);  //sets color equal to white
        int width = 15;  //sets width of goalpost equal to 15
        g.fillRect(x,0,width,height);  //fills that goalpost rectangle

        if(start==0){ //if we're at the left side draw rectangles from start
            width = 0;
        }
        for(int i = 1; i<24; i++){  //divides screen into 24 vertically
            g.fillRect(start + width,(height/24)*(i),60,2);
        }

        for(int i = 1; i<3; i++){    //divides screen into 3 horizontally
            g.fillRect(20*i+start+width,0,2,height);

        }
    }
}

class World{
    int height;
    int width;
    Font arial = new Font("Arial", Font.PLAIN, 60);
    int numSpheres;
    Sphere spheres[];
    Rectangle rectangles[];
    int[]score;

    public World(int initWidth, int initHeight, int initNumSpheres, Pair[] initialPosition){
	width = initWidth;
	height = initHeight;
    score = new int[2];
    score[0] = 0;
    score[1] = 0;
	numSpheres = initNumSpheres;
	spheres  = new Sphere[numSpheres];
    rectangles = new Rectangle[2];
	for (int i = 0; i < 2; i ++)
        {
        rectangles[i] = new Rectangle(initialPosition[i]);
        }

	for (int i = 0; i < numSpheres; i ++)
	    {
		spheres[i] = new Sphere(width,height);
	    }

    }

    public void drawScore(Graphics g){
        g.setFont(arial);
        g.setColor(Color.WHITE);
        for(int i =0; i < 2; i++){
            g.drawString(Integer.toString(score[i]),(width/4) + (width/2)*i -30, 90);
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

public class Pong2 extends JPanel implements KeyListener{
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



	if(c=='r' || c== 'u' || c== 'v' || c== 'n' || c=='f' || c=='j'){ 
		changeVelocity(c, world); 

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

    public Pong2(){
    Pair[] initialPosition = new Pair[2];
    initialPosition[0]= new Pair(95,HEIGHT/2);
    initialPosition[1]= new Pair(WIDTH-95,HEIGHT/2);
	world = new World(WIDTH, HEIGHT, 1, initialPosition);
	addKeyListener(this);
	this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
	Thread mainThread = new Thread(new Runner());
	mainThread.start();
    }
    
    public static void main(String[] args){
	JFrame frame = new JFrame("Physics!!!");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	Pong2 mainInstance= new Pong2();
	frame.setContentPane(mainInstance);
	frame.pack();
	frame.setVisible(true);
    }


    public void paintComponent(Graphics g) {
	super.paintComponent(g);    	

	g.setColor(Color.BLACK);
	g.fillRect(0, 0, WIDTH, HEIGHT);
    Nets.drawNets(60,g,HEIGHT,0);  //different positions because we now have nets
    Nets.drawNets(WIDTH-75,g,HEIGHT,WIDTH-75); //different positions because we now have nets 
	world.drawSpheres(g);
    world.drawRectangles(g);
    world.drawScore(g);

    }

    public void changeVelocity(char c, World w){
    	char keyPressed = c;
       	Pair velocityup = new Pair(0,-300);
        Pair velocitydown = new Pair(0,300);
        Pair velocitystop = new Pair(0,0);
    	
    	switch(keyPressed){ 
    		case 'r': 
    		w.rectangles[0].velocity=velocityup;

            break;

    		case 'f': 
    		w.rectangles[0].velocity=velocitystop;
            break;

    		case 'j': 
    		w.rectangles[1].velocity=velocitystop;
    		break;

    		case 'u': 
    		w.rectangles[1].velocity=velocityup;
            break;
            
            case 'v': 
            w.rectangles[0].velocity=velocitydown;
            break;

            case 'n': 
            w.rectangles[1].velocity=velocitydown;

    		break;
    	}

    
    }



    
}
