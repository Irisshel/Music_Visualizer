void equalizer2(){
  for(int i = 0; i < fft.specSize(); i++)
  {
    float bandVal = fft.getBand(i)*(1 + (i/50));
    if(bandVal > 90)
      bandVal = 90;
    stroke(bass/2, mid/2, treble/2, 255-i/2);
    line(width-i*8, 0, width-i*8, bandVal*5+20);
    line(i*8, height, i*8, height-bandVal*5-20);
  }
}
