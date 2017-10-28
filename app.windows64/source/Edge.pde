class Edge{
  float x,y,z,edgeWidth,edgeHeight;
  Edge(float x, float y, float edgeWidth, float edgeHeight){
    this.x = x;
    this.y = y;
    this.z = random(-8000, 0);
    this.edgeWidth = edgeWidth;
    this.edgeHeight = edgeHeight;
  }
  
  void display(float vLow, float vMid, float vHi, float total){
    noStroke();
    //setting color of edges
    fill(color(vLow*0.3, vMid*0.3, vHi*0.3, total), (255+z/25)*total*0.0002);
    
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