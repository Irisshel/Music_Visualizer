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
  
  void display(float bass, float mid, float treble, float bandVal, float total, boolean eCube, boolean eSphere) {
    color displayColor = color(bass/3, mid/3, treble/3, 10+bandVal*5);
    fill(displayColor);
    
    color strokeColor = color(255, 150-(20*bandVal));
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
    z+= 1+bandVal*0.2+total*total/10000;
        
    if (z >= close) {
       x = random(0, width);
       y = random(0, height);
       z = far;
    }
    
    if(keyPressed){
      
      if(key == '8'){
        //going up
        y+= (1+bandVal*0.2+total*total/22500);
        if (y >= height) {
          x = random(0, width);
          y = 0;
          z = random(far, close);
        }
      }
    if(key == '2'){
        //going down
        y-= (1+bandVal*0.2+total*total/22500);
    
        if (y <= 0) {
          x = random(0, width);
          y = height;
          z = random(far, close);
        }
      }
    if(key == '6'){
      //going right
      x-= (1+bandVal*0.2+total*total/22500);
    
      if (x <= 0) {
        x = width;
        y = random(0, height);
        z = random(far, close);
      }
    }
    //going left
    if(key == '4'){
      x+= (1+bandVal*0.2+total*total/22500);
    
      if (x >= width) {
        x = 0;
        y = random(0, height);
        z = random(far, close);
      }
    }
    }
  }
}
