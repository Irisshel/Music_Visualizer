/**
  * This program combines different music visualizing algorithms.
  * 12/13/2016
  * Andy Kim
  *
  */
import ddf.minim.*;
import ddf.minim.analysis.*;

Minim minim;
AudioPlayer song;
FFT fft;

float basslimit = 0.03;  //limit value of bass RANGE: 0 - 0.03
float midlimit = 0.1;  //limit value of mid RANGE: 0.03 - 0.1
float treblelimit = 0.3; //limit value of treble RANGE: 0.1 - 0.3
int numEdge = 2000; //number of edges, each side has numEdge/4
int numObjects; //number of objects

float bass = 0;
float mid = 0;
float treble = 0;
float prevBass = bass;
float prevMid = mid;
float prevTreble = treble;
Edge[] edge; //declaration of edge array value
Object[] objects; //declaration of object array value


boolean enableEdge, enableCube, enableSphere, enableEqualizer2,
        enableEqualizer3;

void setup()
{
  //setting up
  size(displayWidth, displayHeight-50, P3D);
  minim = new Minim(this);
  song = minim.loadFile("song.mp3");
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


void draw()
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
  
  float total = 0.4*bass+0.8*mid+treble; //total values used in speed of objects. bass*0.4 because bass value is relatively bigger than others
  
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
