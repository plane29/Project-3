import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.util.Random;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.Font;
import java.awt.FontMetrics;
class Pair{ //pair class to hold two doubles
    public double x; //x value of the pair
    public double y;  //y value of the pair
    public double magnitude; //the magnitude of the pair x^2+y^2 = magnitude^2
    
    public Pair(double initX, double initY){  //take in two doubles 
	x = initX;  //set our x value of pair equal to the one passed in
	y = initY;  //set our y value of pair equal to the one passed in
    magnitude = Math.pow(Math.pow(x,2)+Math.pow(y,2),.5); //this determines the magnitude(particularly important for velocity)
    }

    public Pair add(Pair toAdd){  //adds two pairs together component wise
	return new Pair(x + toAdd.x, y + toAdd.y);
    }

    public Pair divide(double denom){  //scalar multiplication on reals (division)
	return new Pair(x / denom, y / denom);
    }

    public Pair times(double val){  //scalar multiplication on reals
	return new Pair(x * val, y * val);
    }

    public void flipX(){  //flips x value particularly important for velocity
	x = -x;
    }
    
    public void flipY(){  //flips y value particularly important for velocity
	y = -y;
    }
}

class Sphere{  //Sphere class which we will use as our ball class
    Pair position;  //keeps track of position of the sphere
    Pair velocity;  //keeps track of velcoity of the sphere
    Pair acceleration;  //keeps track of acceleration of the sphere(not particularly important in this but could be with other uses)
    double radius;  //radius of sphere
    double dampening;  //if you want to add energy dissapation
    Color color;  //color of sphere
    Pair midpoint;  //midpoint of sphere
    public Sphere(int width, int height){  //constructor of sphere
    Random rand = new Random(); //some random value
    midpoint = new Pair((double)(width/2), (double)(height/2)); //finds midpoint of screen
	position = midpoint;  //sets position equal to midpoint of screen
    int leftright=1;  //determines which way to send sphere
    if(rand.nextInt(2)==0){  //if the random is = 0 send it left
        leftright=-1;
    }  //if not send it right
	velocity = new Pair(700*leftright, 0);  //send the ball either right or left to start game
	acceleration = new Pair(0.0, 0.0);  //let acceleration equal to 0
	radius = 20;  //set radius =20
	dampening = 1;  //no dissipation of energy
    }
    public void update(World w, double time, boolean pause, boolean win){
    if(!pause && !win){ //if the game has not started or somebody has won
	   position = position.add(velocity.times(time));  //change the position based upon velocity
	   velocity = velocity.add(acceleration.times(time));  //change the velocity based upon acceleration
	   bounce(w); //uses bounce method
        }
    }
    
    public void setPosition(Pair p){  //could be used to set position
	position = p;
    }
    public void setVelocity(Pair v){  //could be used to set velocity
	velocity = v;
    }
    public void setAcceleration(Pair a){  //could be used to set acceleration
	acceleration = a;
    } 
    public void draw(Graphics g){  //draws the sphere
	Color c = g.getColor();
	g.setColor(Color.WHITE); //sets color equal to white
	g.fillOval((int)(position.x - radius), (int)(position.y - radius), (int)(2*radius), (int)(2*radius));  //draws sphere with centerpoint as position
	g.setColor(c);
    }
    private void bounce(World w){ //determines how the sphere bounces
    Random rand = new Random();  //some random instance
	Boolean bounced = false;  //ball has not yet bounced 
    double angle;  //used to store angle at which ball bounces
	if (position.x - radius < 0){ //if the right player scores a goal
        w.score[1]++; //change right player's score
        if(w.score[1]==10){  //if the right player's score equals 10
            w.win=true; //right player has won
            w.pause=true;  //pause the game
        }
        position = midpoint;  //send the ball back to the middle of the screen
        velocity = new Pair(-700, 0);  //send the ball back to the other player
        w.rectangles[0].position= w.rectangles[0].startingPosition; //put the rectangles back where they
        w.rectangles[1].position= w.rectangles[1].startingPosition;
        w.rectangles[0].velocity= w.rectangles[0].startingVelocity; //set their velocities to 0
        w.rectangles[1].velocity= w.rectangles[1].startingVelocity;
	}
	else if (position.x + radius > w.width ){   //if the left player scores a goal
        w.score[0]++; //do the same as above but for the left player (player 1)
        if(w.score[0]==10){
            w.win = true;
            w.pause = true;
        }
        position = midpoint;
        velocity = new Pair(700, 0);
        w.rectangles[0].position= w.rectangles[0].startingPosition;
        w.rectangles[1].position= w.rectangles[1].startingPosition;
        w.rectangles[0].velocity= w.rectangles[0].startingVelocity;
        w.rectangles[1].velocity= w.rectangles[1].startingVelocity;
	}
    else if ((position.x - radius<= w.rectangles[0].position.x + w.rectangles[0].width/2) 
            && position.y + radius >= w.rectangles[0].position.y - w.rectangles[0].height/2 
            && position.y -radius <= w.rectangles[0].position.y + w.rectangles[0].height/2
            && position.x + radius >=w .rectangles[0].position.x-w.rectangles[0].width/2){  // if the ball hits the left paddle (extra condition for if ball was behind the paddle)
        bounced=true; //ball has bounced
        if (position.y == w.rectangles[0].position.y){ //if the ball hits directly in the middle
            velocity.x = velocity.magnitude; //ball bounces back straight with same magnitude
            velocity.y = 0;
        }
        else if (position.y <= w.rectangles[0].position.y - w.rectangles[0].height/2){  //if the ball hits the top of the paddle
            velocity.x = Math.cos(-1.309)*velocity.magnitude;  //bounce back at 75 degrees (75 degrees in radians is 1.309)
            velocity.y = Math.sin(-1.309)*velocity.magnitude;
        }
        else if (position.y >= w.rectangles[0].position.y + w.rectangles[0].height/2){  //same for bottom but opposite y
            velocity.x = Math.cos(1.309)*velocity.magnitude;
            velocity.y = Math.sin(1.309)*velocity.magnitude;
        }
        else{  //if it hits anywhere else in the middle
            angle = ((position.y - w.rectangles[0].position.y)/(w.rectangles[0].height/2))*1.309;   //calculate the angle based upon how far up or down the paddle it is
            velocity.x = Math.cos(angle)*velocity.magnitude;
            velocity.y = Math.sin(angle)*velocity.magnitude;  //note if it is below the midpoint this will be pos which is what we want

        }

    }
    else if ((position.x + radius>= w.rectangles[1].position.x - w.rectangles[1].width/2) 
        && position.y + radius >= w.rectangles[1].position.y - w.rectangles[1].height/2 
        && position.y -radius <= w.rectangles[1].position.y + w.rectangles[1].height/2
        && position.x - radius <=w .rectangles[1].position.x-w.rectangles[1].width/2){  //if the ball hits the right paddle (extra condition for if ball is behind paddle)
        bounced=true;  //basically do same as above with slight changes for the right paddle
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
	if (position.y - radius < 0){  //bounces off the top wall
	    velocity.flipY();  //flip the y velocity
	    position.y = radius; //move the ball back on the screen
	    bounced = true;  //ball has bounced
	}
	else if(position.y + radius >  w.height){  //bounces off of the bottom wall
	    velocity.flipY();  //same thing but for bottom wall
	    position.y = w.height - radius;
	    bounced = true;
	}
	if (bounced){  //if the ball has bounced
	    velocity = velocity.divide(dampening);  //dampen effect of bounce
	}
    }
    
}

class Rectangle{
    Pair position;  //position of rectangle
    Pair velocity;  //velocity of rectangle
    double height;  //height of rectangle
    double width;  //width of rectangle
    Color color;  //color of rectangle
    Pair startingPosition;  //starting position of rectangle
    Pair startingVelocity;  //starting velocity of rectangle
    public Rectangle(Pair inPosition){  //constructor of rectangle
        startingPosition = inPosition;  //set starting position equal to in position
        Random rand = new Random();  //some instance of random
        position = inPosition;  //set position equal to the position we sent in
        startingVelocity = new Pair(0,0);  //set starting velocity equal to 0
        velocity = new Pair(0,0);  //set velocity equal to 0
        height = 150;  //set height 
        width = 20;  //set width
        color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());  //set some random color
    }
    public void update(World w, double time, boolean pause, boolean win){
    if(!((position.y-(height/2) <=0) && velocity.y<0) 
    && !(position.y+(height/2)>=w.height && velocity.y>0) 
    && !pause
    && !win){  //if it isn't the case the rectangle is at the top or bottom of the screen and nobody has won or started the game
        position = position.add(velocity.times(time));  //update the positions of the rectangles
        }
    }
    
    public void setPosition(Pair p){ //could be used to set position
    position = p;
    }
    public void setVelocity(Pair v){   //could be used to set velocity
    velocity = v;
    }
    public void draw(Graphics g){  //draws rectangle
    Color c = g.getColor();   //gets a color
    g.setColor(color);  //sets color
    g.fillRect((int)(position.x - width/2), (int)(position.y - height/2), (int)(width),(int)(height));  //draws rectangle with position at center
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

class World{  //world class
    int height;  //height of world
    int width;  //width of world
    Font arial = new Font("Arial", Font.PLAIN, 60);  //new font arial with size 60
    Font arial2 = new Font("Arial", Font.PLAIN, 40);  //new font arial with size 40
    int numSpheres;  //number of spheres
    Sphere spheres[];  //spheres array if there are multiple spheres
    Rectangle rectangles[];  //rectangles array
    int[]score;  //score array
    boolean pause = true;  //boolean to help start the game
    boolean win = false;  //boolean if somebody has won

    public World(int initWidth, int initHeight, int initNumSpheres, Pair[] initialPosition){  //instance of our world
	width = initWidth;  //set width equal to passed in width
	height = initHeight;  //set height equal to passed in height
    score = new int[2];  //set length of score array equal to 2
    score[0] = 0;  //set score of player 1 to 0
    score[1] = 0;  //set score of player 2 to 0
	numSpheres = initNumSpheres;  //set the number of spheres equal to what's passed in
	spheres  = new Sphere[numSpheres];  //declare the length of the spheres array
    rectangles = new Rectangle[2];  //declare length of rectangles array
	for (int i = 0; i < 2; i ++){  //create the new rectangles sending in the width and height of the world
        rectangles[i] = new Rectangle(initialPosition[i]);
        }
    for (int i = 0; i < numSpheres; i ++){  //create the new spheres sending in the width and height of the world
		spheres[i] = new Sphere(width,height);
	    }

    }

    public void drawScore(Graphics g){  //draws score onto screen
        g.setFont(arial);  //uses arial text with size 60
        FontMetrics metrics = g.getFontMetrics(arial); //to help us center
        g.setColor(Color.WHITE); //set color equal to white
        for(int i =0; i < 2; i++){ //for both players draw score
            g.drawString(Integer.toString(score[i]),(width/4) + (width/2)*i - metrics.stringWidth(Integer.toString(score[i]))/2, 90);
        }  //the metrics.stringWidth() helps determine pixel width of the string so we can center 
    }

    public void drawSpheres(Graphics g){  //draw the spheres
	for (int i = 0; i < numSpheres; i++){
	    spheres[i].draw(g);
	}
    }

    public void drawRectangles(Graphics g){  //draw the rectangles
        for(int i = 0; i<2; i++){
            rectangles[i].draw(g);
        }
    }

    public void drawStartScreen(Graphics g){  //draws our start screen
        g.setFont(arial2);  //use size 40 font
        FontMetrics metrics = g.getFontMetrics(arial2);  //same deal as above
        String screen = "Hello! Press p to play pong soccer!";  
        g.drawString(screen, width/2 - metrics.stringWidth(screen)/2, height/2-150);
    }

    public void drawWinScreen(Graphics g){  //draws out screen if somebody won
        g.setFont(arial2);
        FontMetrics metrics = g.getFontMetrics(arial2);
        char winner = '1';
        if(score[1]>score[0]){
            winner = '2';
        }
        String screen = "Player "+ winner + " has won! Press p to play again!";
        g.drawString(screen, width/2 - metrics.stringWidth(screen)/2, height/2-150);
    }


    public void updateSpheres(double time){  //update position and velocity of spheres
	for (int i = 0; i < numSpheres; i ++)
	    spheres[i].update(this, time, pause, win);
    }

    public void updateRectangles(double time){  //update position of rectangles
        for(int i = 0; i < 2; i++){
            rectangles[i].update(this, time, pause, win);
        }
    }


}

public class Pong extends JPanel implements KeyListener{
    public static final int WIDTH = 1024;  //sets width of screen
    public static final int HEIGHT = 768;  //sets height of screen
    public static final int FPS = 60;  //sets frames per second
    World world;  //creates new world

    class Runner implements Runnable{
	public void run()  //runs to update the spheres and rectangles
	{
        while(true){
		world.updateSpheres(1.0 / (double)FPS);
        world.updateRectangles(1.0/ (double)FPS);
		repaint(); //paints again
		try{
		    Thread.sleep(1000/FPS);
		}
		catch(InterruptedException e){}
	    }
    
	}
    
    }


    public void keyPressed(KeyEvent e) {  //key pressed down
    char c=e.getKeyChar();
	System.out.println("You pressed down: " + c);



	if(c=='r' || c== 'u' || c== 'v' || c== 'n' || c=='f' || c=='j'){ //if the key pressed was one of these
		changeVelocity(c, world); // call method to change the velocity of the rectangles

    }

    if(c=='p'){  //if p is pressed
        world.pause=false;  //the world is no longer paused
        world.win = false;  //nobody has won the game
        if(world.score[0]==10 || world.score[1]==10){  //if the game was won
            world.score[0]=0; //scores reset to 0
            world.score[1]=0;
        }
    }

}
    public void keyReleased(KeyEvent e) {  //just to help w debuggin
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
    Pair[] initialPosition = new Pair[2];  //create new pair for initial positions of rects
    initialPosition[0]= new Pair(95,HEIGHT/2);  //set the initial position of the left rectangle
    initialPosition[1]= new Pair(WIDTH-95,HEIGHT/2);  //set the initial position of the right rectangle
	world = new World(WIDTH, HEIGHT, 1, initialPosition);  //create a new world with width, height, 1, sphere and these initial positions
	addKeyListener(this);  //adds key listener
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


    public void paintComponent(Graphics g) {  //paints each frame
	super.paintComponent(g);    	
    g.setColor(Color.BLACK);  //set color black
	g.fillRect(0, 0, WIDTH, HEIGHT);  //box size of width height
    Nets.drawNets(60,g,HEIGHT,0);  //draw left net
    Nets.drawNets(WIDTH-75,g,HEIGHT,WIDTH-75); //draw right net 
	if(world.pause && !world.win){ //if the game is paused and nobody has won(i.e. beginning of game)
        world.drawStartScreen(g); //draw the start screen
    }
    if(world.win){ //if somebody has won
        world.drawWinScreen(g);  //draw the win screen
    }
    world.drawSpheres(g);  //draw the spheres
    world.drawRectangles(g);  //draw the rectangles
    world.drawScore(g);  //draw the score

    }

    public void changeVelocity(char c, World w){  //to help change the velocity of our rectangles
    	char keyPressed = c;
       	Pair velocityup = new Pair(0,-300);  //upwards velocity
        Pair velocitydown = new Pair(0,300);  //downwards velocity
        Pair velocitystop = new Pair(0,0);  //stopped velocity
    	
    	switch(keyPressed){ // uses a switch with different cases to change the velocity of the rectangles based upon which key is pressed
    	
    		case 'f': //if f is pressed stop the left rectangle from moving
    		w.rectangles[0].velocity=velocitystop;
            break;

    		case 'j': //if f is pressed stop the right rectangle from moving
    		w.rectangles[1].velocity=velocitystop;
    		break;

            case 'r':  //if r is pressed move the left rectangle up
            w.rectangles[0].velocity=velocityup;
            break;

    		case 'u': //if u is pressed move the right rectangle up
    		w.rectangles[1].velocity=velocityup;
            break;
            
            case 'v': //if v is pressed move the left rectangle down
            w.rectangles[0].velocity=velocitydown;
            break;

            case 'n':  //if n is pressed move the right rectangle down
            w.rectangles[1].velocity=velocitydown;
    		break;
    	}

    
    }



    
}
