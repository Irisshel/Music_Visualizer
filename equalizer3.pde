void equalizer3(float total){
 float oldBandVal = fft.getBand(0);
  
  for(int i = 1; i < fft.specSize(); i++)
  {
    float bandVal = fft.getBand(i)*(1 + (i/50));
    
    //Stroke color based on bass, mid, treble value
    stroke(bass, mid, treble, 255-i);
    strokeWeight(1+(total/100)-2.5);
    
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
