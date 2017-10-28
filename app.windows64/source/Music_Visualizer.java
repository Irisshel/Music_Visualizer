import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 
import ddf.minim.analysis.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Music_Visualizer extends PApplet {

/**
  * This program combines different music visualizing algorithms.
  * 12/13/2016
  * Andy Kim
  *
  *
  * Press q,w,e,r,t to turn effects on
  * Press a,s,d,f,g to turn effects off
  * Play with limit value to change colors
  *
  */



Minim minim;
AudioPlayer song;
FFT fft;

float basslimit = 0.025f;  //limit value of bass RANGE: 0 - 0.03
float midlimit = 0.07f;  //limit value of mid RANGE: 0.03 - 0.1
float treblelimit = 0.2f; //limit value of treble RANGE: 0.1 - 0.3
int numEdge = 1000; //number of edges, each side has numEdge/4
int numObjects; //number of objects

float bass = 0;
float mid = 0;
float treble = 0;
float prevBass = bass;
float prevMid = mid;
float prevTreble = treble;
Edge[] edge; //declaration of edge array value
Object[] objects; //declaration of object array value


boolean enableEdge = true, enableCube=true, enableSphere, enableEqualizer2,
        enableEqualizer3 = true;

public void setup()
{
  //setting up
  
  minim = new Minim(this);
  song = minim.loadFile("war.aiff");
  fft = new FFT(song.bufferSize(), song.sampleRate());
  
  
  numObjects = (int)(fft.specSize()*treblelimit); //number of object is based on treble value and the specSize
  
  //creating arrays
  objects = new Object[numObjects];
  edge = new Edge[numEdge];
  
  
  for (int i = 0; i < numObjects; i++) {
   objects[i] = new Object(); 
  }
  
  //setting four sides of edges in an array
  for(int i = 0; i < numEdge; i+= 4){
   edge[i] = new Edge(0, height/2, 10, height-50);
   edge[i+1] = new Edge(width, height/2, 10, height-50); 
   edge[i+2] = new Edge(width/2, height, width-50, 10); 
   edge[i+3] = new Edge(width/2, 0, width-50, 10); 
  } 
  
  //background(0);
  song.play();
}


public void draw()
{
  fft.forward(song.mix);
  
  if(keyPressed){
    if(key == 'q') enableEdge = true;
    if(key == 'a') enableEdge = false;
    if(key == 'w') enableEqualizer2 = true;
    if(key == 's') enableEqualizer2 = false;
    if(key == 'e') enableEqualizer3 = true;
    if(key == 'd') enableEqualizer3 = false;
    if(key == 'r') enableCube = true;
    if(key == 'f') enableCube = false;
    if(key == 't') enableSphere = true;
    if(key == 'g') enableSphere = false;
  }
  
  prevBass = bass;
  prevMid = mid;
  prevTreble = treble;
  bass = 0;
  mid = 0;
  treble = 0;
  
  //setting the range of bass, mid, treble. add up all the band values in it.
  for(int i = 0; i < fft.specSize()*basslimit; i++)
  {
    bass += fft.getBand(i);
  }
  for(int i = (int)(fft.specSize()*basslimit); i < fft.specSize()*midlimit; i++)
  {
    mid += fft.getBand(i);
  }
  for(int i = (int)(fft.specSize()*midlimit); i < fft.specSize()*treblelimit; i++)
  {
    treble += fft.getBand(i);
  }
  
  float total = 0.4f*bass+0.8f*mid+treble; //total values used in speed of objects. bass*0.4 because bass value is relatively bigger than others
  
  background(bass/100, mid/100, treble/100); //set color 
  
  if(enableEqualizer2)  equalizer2();
  if(enableEqualizer3)  equalizer3(total);
 
  //3d-Objects
  for(int i = 0; i < numObjects; i++)
  {
    float bandVal = fft.getBand(i);
    objects[i].display(bass, mid, treble, bandVal, total, enableCube, enableSphere);
  }
  
  //border edges
  if(enableEdge){
    for(int i = 0; i < numEdge; i++)
    {
      edge[i].display(bass, mid, treble, total);
    }
  }
}
class Edge{
  float x,y,z,edgeWidth,edgeHeight;
  Edge(float x, float y, float edgeWidth, float edgeHeight){
    this.x = x;
    this.y = y;
    this.z = random(-8000, 0);
    this.edgeWidth = edgeWidth;
    this.edgeHeight = edgeHeight;
  }
  
  public void display(float vLow, float vMid, float vHi, float total){
    noStroke();
    //setting color of edges
    fill(color(vLow*0.3f, vMid*0.3f, vHi*0.3f, total), (255+z/25)*total*0.0002f);
    
    //move box, give size
    pushMatrix();
    translate(x, y, z);
    scale(edgeWidth, edgeHeight, 10);
    box(1);
    popMatrix();
    
    z+= total*total/10000; //add z value so it looks like the lines are coming
    
    //if too close, reset it to the farthest
    if (z >= 0)
      z = -8000;  
  }
}
public void equalizer2(){
  //equalizer 2d
  for(int i = 0; i < fft.specSize(); i++)
  {
    float bandVal = fft.getBand(i)*(1 + (i/50));
    if(bandVal > 90)
      bandVal = 90;
    stroke(bass/2, mid/2, treble/2, 255-i/2);
    line(width-i*8, 0, width-i*8, bandVal*5+20); //higher one
    line(i*8, height, i*8, height-bandVal*5-20); //lower one
  }
}
public void equalizer3(float total){
 float oldBandVal = fft.getBand(0);
  
  for(int i = 1; i < fft.specSize(); i++)
  {
    float bandVal = fft.getBand(i)*(1 + (i/50));
    
    //Stroke color based on bass, mid, treble value
    stroke(bass, mid, treble, 255-i);
    strokeWeight(1+(total/100)-2.5f);
    
    //low left
    line(0, oldBandVal*2, -25*(i-1), 0, bandVal*2, -25 * i);
    line(oldBandVal*2, 0, -25*(i-1), bandVal*2, 0, -25*i);
    line(0, oldBandVal*2, -25*(i-1), bandVal*2, 0, -25*i);
    
    //high left
    line(0, height-oldBandVal*2, -25*(i-1), 0, height-bandVal*2, -25*i);
    line(oldBandVal*2, height, -25*(i-1), bandVal*2, height, -25*i);
    line(0, height-oldBandVal*2, -25*(i-1), bandVal*2, height, -25*i);
    
    //low right
    line(width,height-oldBandVal*2, -25*(i-1),width, height-bandVal*2, -25*i);
    line(width-oldBandVal*2, height, -25*(i-1),width - bandVal*2, height, -25*i);
    line(width,height-oldBandVal*2 , -25 * (i-1), width - bandVal*2, height, -25*i);
    
    //high right
    line(width, oldBandVal*2, -25*(i-1), width, bandVal*2, -25*i);
    line(width-oldBandVal*2, 0, -25*(i-1), width-bandVal*2, 0, -25*i);
    line(width, oldBandVal*2, -25*(i-1), width - bandVal*2, 0, -25*i);
    
    oldBandVal = bandVal;
  }
}
class Object {
  float far = -10000;
  float close = 2000;
  
  float x, y, z;
  float rotX, rotY, rotZ;
  float sumRotX, sumRotY, sumRotZ;
  //float scale = 20;
  
  Object() {
    x = random(0, width);
    y = random(0, height);
    z = random(far, close);
    
    rotX = random(0, 1);
    rotY = random(0, 1);
    rotZ = random(0, 1);
  }
  
  public void display(float bass, float mid, float treble, float bandVal, float total, boolean eCube, boolean eSphere) {
    int displayColor = color(bass/3, mid/3, treble/3, 10+bandVal*5);
    fill(displayColor);
    
    int strokeColor = color(255, 150-(20*bandVal));
    stroke(strokeColor);
    
    pushMatrix();
    translate(x, y, z);
    
    //show sphere
    if(eSphere){    
      noStroke();
      lights();
      sphere(20);
    }
   
    //show Cube 
    else if(eCube){
      strokeWeight(1 + (total/1000));
      sumRotX += bandVal*(rotX/1000);
      sumRotY += bandVal*(rotY/1000);
      sumRotZ += bandVal*(rotZ/1000);
    
      rotateX(sumRotX);
      rotateY(sumRotY);
      rotateZ(sumRotZ);
    
      box(100 + bandVal);
    }
    
    
    popMatrix();
    z+= 1+bandVal*0.2f+total*total/10000;
        
    if (z >= close) {
       x = random(0, width);
       y = random(0, height);
       z = far;
    }
    
    if(keyPressed){
      
      if(key == '8'){
        //going up
        y+= (1+bandVal*0.2f+total*total/22500);
        if (y >= height) {
          x = random(0, width);
          y = 0;
          z = random(far, close);
        }
      }
    if(key == '2'){
        //going down
        y-= (1+bandVal*0.2f+total*total/22500);
    
        if (y <= 0) {
          x = random(0, width);
          y = height;
          z = random(far, close);
        }
      }
    if(key == '6'){
      //going right
      x-= (1+bandVal*0.2f+total*total/22500);
    
      if (x <= 0) {
        x = width;
        y = random(0, height);
        z = random(far, close);
      }
    }
    //going left
    if(key == '4'){
      x+= (1+bandVal*0.2f+total*total/22500);
    
      if (x >= width) {
        x = 0;
        y = random(0, height);
        z = random(far, close);
      }
    }
    }
  }
}
  public void settings() {  size(displayWidth, displayHeight, P3D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--stop-color=#cccccc", "Music_Visualizer" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
