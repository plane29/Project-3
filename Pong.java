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
    //velocity= new Pair (500.0, 0.0);
    int leftright=1;
    if(rand.nextInt(2)==0){
        leftright=-1;
    }
	velocity = new Pair(700*leftright, 0);
	acceleration = new Pair(0.0, 0.0);
	radius = 10;
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
	    //velocity.flipX();
	    //position.x = radius;
	    bounced = true;
        w.score[1]++;
        position = midpoint;
        velocity = new Pair(-700, 0);
        w.rectangles[0].position= w.rectangles[0].startingPosition;
        w.rectangles[1].position= w.rectangles[1].startingPosition;
	}
	else if (position.x + radius > w.width){  //NOTE CURRENTLY WE HAVE A PROBLEM WHEN THE BALL HITS THE WALL AND THEN WE GO OVER IT NEED TO FIX THIS HAS TO DO WITH SWITHCING VELOICITy
	    //velocity.flipX();
	    //position.x = w.width - radius;
	    bounced = true;
        w.score[0]++;
        position = midpoint;
        velocity = new Pair(700, 0);
        w.rectangles[0].position= w.rectangles[0].startingPosition;
        w.rectangles[1].position= w.rectangles[1].startingPosition;
	}
    else if ((position.x - radius<= w.rectangles[0].position.x + w.rectangles[0].width/2) && position.y + radius >= w.rectangles[0].position.y - w.rectangles[0].height/2 && position.y -radius <= w.rectangles[0].position.y + w.rectangles[0].height/2){
        if (position.y == w.rectangles[0].position.y){  //TESTING PURPOSES|| position.y -10 <= w.rectangles[0].position.y){
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
    else if ((position.x + radius>= w.rectangles[1].position.x - w.rectangles[1].width/2) && position.y + radius >= w.rectangles[1].position.y - w.rectangles[1].height/2 && position.y -radius <= w.rectangles[1].position.y + w.rectangles[1].height/2){
        if (position.y == w.rectangles[1].position.y){ //|| position.y -10 <= w.rectangles[0].position.y){
            velocity.x = -velocity.magnitude;
            velocity.y = 0;
        }
        else if (position.y <= w.rectangles[1].position.y - w.rectangles[1].height/2){
            velocity.x = -1*Math.cos(-1.309)*velocity.magnitude;
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
    //velocity = velocity.add(acceleration.times(time));
    }
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
    g.fillRect((int)(position.x - width/2), (int)(position.y - height/2), (int)(width),(int)(height));
    //System.out.println(position.x + " " + position.y);
    g.setColor(c);
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
    initialPosition[1]= new Pair(WIDTH-30,HEIGHT/2);
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
    world.drawScore(g);

    }

    public void changeVelocity(char c, World w){
    	char keyPressed = c;
       	Pair velocityup = new Pair(0,-300);
        Pair velocitydown = new Pair(0,300);
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
