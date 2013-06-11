package game.objects;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import map.Camera;

import core.Board;
import core.FileLink;
import core.GameManager;
import core.GameObjectManager;

public class MapObject extends Moveable implements Runnable, FileLink{

	private static ArrayList<MapObject> mapObjectList = new ArrayList<MapObject>();
	public static int listCounter;
	
	private MapObject mapObject;
	private int IDNumber;
	private int type;
	private int orientation;
	private int xMap, yMap, xPos, yPos;
	private int counter;
	private int x = 0;
	private int y = 0;
	
	private Thread runThread;
	ScheduledExecutorService execRun = Executors.newSingleThreadScheduledExecutor();
	
	private MapObject(int IDNumber, int type, int orientation, int xMap, int yMap, int xPos, int yPos){
		mapObject = this;
		this.IDNumber = IDNumber;
		this.type = type;
		this.xMap = xMap;
		this.yMap = yMap;
		this.xPos = xPos;
		this.yPos = yPos;
		this.orientation = orientation;
		System.err.println("=====> construct MapObject@ID "+IDNumber+", type:"+type+", orientation:"+orientation+", @Pos:"+xPos+"x"+yPos);
		initializeMapObject(type, orientation);
		
	}
	
	public void run(){
		//System.out.println(Player.getInstance().getKeyInventory());
		
		
		switch(type){
		case(0): handleDoor(); break;
		case(1): handleTrap(); break;
		}
		
		//System.out.println("Type@"+type);

	}
	
	private void handleDoor(){
		
		x = xPos + xMap * 810;
		y = yPos + yMap * 630;
		
		switch(orientation){
		case(1):	break;
		case(2):	x -= 95; break;
		case(3):	y -= 89; break;
		case(4):	x += 8; break;
		}
		
		
		setX((x-Camera.getInstance().getX()));
		setY((y-Camera.getInstance().getY()));
		
		if(getBoundCore().intersects(Player.getInstance().getBoundCore())){
			if(Player.getInstance().useKeyInventory())
				Player.getInstance().setObjectBack(10, 0, false, null);
			else{
				GameObjectManager.openDoor(IDNumber);
				stop();
			}
			
		}
	}
	
	private void handleTrap(){
		
		int shake = new Random().nextInt(2 - -2 + 1) + -2;
		

		
		setX((x-Camera.getInstance().getX()));
		setY((y-Camera.getInstance().getY()));
		
		if(getBoundCore().intersects(Player.getInstance().getBoundCore())){
			x = xPos + xMap * 810 + shake;
			y = yPos + yMap * 630 + shake;
			
			
			if(x > xPos + xMap * 810 + 10)
				x = xPos + xMap * 810;
					
			if(y > yPos + yMap * 630 + 10)
				y = yPos + yMap * 630;
		} else {
			x = xPos + xMap * 810;
			y = yPos + yMap * 630;
		}
		
		if(getBoundCore().contains(Player.getInstance().getBoundCore())){
			/*
			Player.getInstance().setX(Player.getInstance().getOldX());
			Player.getInstance().setY(Player.getInstance().getOldY());
			Player.getInstance().setLastDirection(Player.getInstance().getOldLastDirection());
			Player.getInstance().setLife(Player.getInstance().getLife() - 1);
			*/
			stop();
		}
		
	}
	
	
	
	private void stop(){
		setVisible(false);
		setAlive(false);
		Board.updateMapObjectList();
		execRun.shutdown();
		mapObject = null;
	}
	
	private void initializeMapObject(int type, int orientation){
		int[] data = new int[11];
		data = MapObjectList.getObjectData(type, orientation);

	
		System.err.println("==construct=>CameraPos@"+Camera.getInstance().getX()+"x"+Camera.getInstance().getY());
		
		
	
		setStaticX(data[2]);
		setStaticY(data[3]);
		setSubSpriteWidth(data[4]);
		setSubSpriteHeight(data[5]);
		
		setCoreX(data[6]);
		setCoreY(data[7]);
		setCoreWidth(data[8]);
		setCoreHeight(data[9]);
		
		setStaticCycle(data[10]);
		setFile(mapObjects00);
		loadSprite();
		
		
		setVisible(true);
		setMoveable(false);
		setAlive(true);
		
		Board.getInstance().addMapObject(this);
		setStaticSubSprite(1);
		runThread = new Thread(this);
		execRun.scheduleWithFixedDelay(runThread, 10, 10, TimeUnit.MILLISECONDS);
		

		
	}
	
	private int getIDNumber(){return IDNumber;}
	
	public static void deleteAllInstances(){
		
		
		for(int index = 0; index < mapObjectList.size(); index++){
			mapObjectList.get(index).stop();
		}
		mapObjectList.clear();
	}
	
	public static void deleteInstance(int IDNumber){
		
		for(int index = 0; index < mapObjectList.size(); index ++){
			
			if(mapObjectList.get(index).getIDNumber() == IDNumber){
				mapObjectList.remove(index);
				break;
			}
				
		}
	}
	
	public static void addInstance(int IDNumber, int type, int orientation, int xMap, int yMap, int xPosition, int yPosition){
	
		mapObjectList.add(new MapObject(IDNumber, type, orientation, xMap, yMap, xPosition, yPosition));
		listCounter++;

	}
	
	public static ArrayList<MapObject> getMapObjectList(){
		return mapObjectList;
	}
	
	
	private enum MapObjectList{
		
		DOORNORTH(0,1,360,0,180,90,0,0,150,80,0),
		DOOREAST(0,2,180,0,90,180,30,0,90,180,0),
		DOORSOUTH(0,3,0,90,180,90,0,30,150,90,0),
		DOORWEST(0,4,270,0,90,160,-60,0,90,160,0),
		
		BROKENFLOOR1(1,0,0,180,90,90,-20,-10,100,100,0),
		BROKENFLOOR2(1,1,90,180,90,90,0,0,90,90,0),
		
		TREASUREC(2,0,360,180,90,90,0,0,90,90,0),
		TREASUREO(2,1,450,180,90,90,0,0,90,90,0),
		
		BLOCKSTONE(3,0,630,0,180,180,0,0,180,180,0),
		
		WATER1(10,0,0,270,90,90,0,0,90,90,3),
		WATER2(10,1,0,360,90,90,0,0,90,90,3),
		
		LAVA1(11,0,270,270,90,90,0,0,90,90,3),
		LAVA2(11,1,270,360,90,90,0,0,90,90,3),
		
		GRASS1(12,0,0,450,90,90,0,0,90,90,3),
		GRASS2(12,1,0,540,90,90,0,0,90,90,3),
		GRASS3(12,2,270,450,90,90,0,0,90,90,3),
		GRASS4(12,3,270,540,90,90,0,0,90,90,3);
		
		private final int type;
		private final int orientation;
		private final int[] data = new int[11];
		
		private MapObjectList(int type, int orientation, int xPosition, int yPosition, int width, int height, int coreX, int coreY, int coreWidth, int coreHeight, int cycle){

			this.type = type;
			this.orientation = orientation;
			data[0] = type;
			data[1] = orientation;
			
			data[2] = xPosition;
			data[3] = yPosition;
			data[4] = width;
			data[5] = height;
			
			data[6] = coreX;
			data[7] = coreY;
			data[8] = coreWidth;
			data[9] = coreHeight;
			
			data[10] = cycle;
		}
		
	
		private static int[] getObjectData(int type, int orientation){
			
			int[] objectData = null;
			
			for(MapObjectList object : values()){
				
				if(object.type == type && object.orientation == orientation){
					objectData = object.data;
					break;
				}		
			}
			
			return objectData;
		}
		
	}
	
}