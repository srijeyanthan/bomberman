package com.cmov.bomberman;


class Wall extends Cell
{
  public static final String IMAGE_FILENAME = "resource/wall.png";
  
  public Wall(int x, int y)
  {
    super(x, y);
  }
  
  public String getImageFilename()
  {
    return IMAGE_FILENAME;
  }
}
