package map;

import game.objects.Guide;
import game.objects.MapObject;
import game.objects.MarioDark;
import game.objects.Moveable;
import game.objects.Player;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import core.Board;
import core.FileLink;
import core.GameManager;

public class OverWorldNavigator extends OverWorldCollision implements FileLink{
	
	private static OverWorldNavigator overWorldNavigator;
	
	
	private OverWorldNavigator(){
		System.err.println("construct OverWorldMap");
		setOverWorld(true);
	}
	
	public void initializeMap(int x, int y, int ID, int playerX, int playerY){
		
		GameManager.getInstance().mapLoaded = false;
		MarioDark.deleteAllInstances();
		MapObject.deleteAllInstances();
		OverWorldNavigator.getInstance().resetObjectManager();
		DungeonNavigator.getInstance().resetObjectManager();
		
		Board.getInstance().setTopMap(false, null);
		setID(ID);
		
		switch(ID){
		case(0):	loadMap(overWorldMapID00,overWorldDataID00);
					Board.getInstance().setTopMap(true, overWorldTOPID00);
					setWidthMap(2700);
					setHeightMap(2700);
					Player.getInstance().setDirectionLock(0);
					GameManager.getInstance().cameraOn = true;
					break;
					
		case(1):	loadMap(overWorldMapID01,overWorldDataID01);
					Board.getInstance().setTopMap(true, overWorldTOPID01);
					setWidthMap(1620);
					setHeightMap(1260);
					Player.getInstance().setDirectionLock(0);
					GameManager.getInstance().cameraOn = true;
					break;
		
					
		}
		
		setXCoordinate(-x);
		setYCoordinate(-y);
		Camera.getInstance().setX(x);
		Camera.getInstance().setY(y);
		Player.getInstance().setX(playerX);
		Player.getInstance().setY(playerY);
		Player.getInstance().setOldPosition();
		

		setEnemy();
		if(this.getID() == 0){
			System.out.println("=====>GUIDE@Cam:"+x+"x"+y);
			Guide.getInstance(1930-x, 530-y);
		}
		
		
		System.out.println("xy@"+x+"x"+y);
		System.out.println("Player@"+playerX+"x"+playerY);
		
		initializeBounds();
		
		GameManager.getInstance().mapLoaded = true;
	}
	

	public synchronized void navigate(){
		
		Camera.getInstance().setX(-getXCoordinate());
		Camera.getInstance().setY(-getYCoordinate());
		
		int x = getXCoordinate();
		int y = getYCoordinate();
		int width = getWidthMap();
		int height = getHeightMap();
		
		Rectangle[] mapNavigationBound = getNavigationBound();
		
		
			
			if(Player.getInstance().getBoundDirection(0).intersects(mapNavigationBound[0]) || GameManager.getInstance().scrollDirection == 1){
				if(y < 0)
					GameManager.getInstance().scrollDirection = 1;
				else{
					GameManager.getInstance().scrollDirection = 0;
					Player.getInstance().setY(Player.getInstance().getY() + 10);
				}	
			}
			
			
			
			if(Player.getInstance().getBoundDirection(0).intersects(mapNavigationBound[1]) ||GameManager.getInstance().scrollDirection == 3){
				if(x > -(width-810))
					GameManager.getInstance().scrollDirection = 3;
				else{
					GameManager.getInstance().scrollDirection = 0;
					Player.getInstance().setX(Player.getInstance().getX() - 10);
				}	
			}
			
			
			if(Player.getInstance().getBoundDirection(0).intersects(mapNavigationBound[2]) || GameManager.getInstance().scrollDirection == 5){
				if(y > -(height-630))
					GameManager.getInstance().scrollDirection = 5;
				else{
					GameManager.getInstance().scrollDirection = 0;
					Player.getInstance().setY(Player.getInstance().getY() - 10);
				}			
			}
			
			
			if(Player.getInstance().getBoundDirection(0).intersects(mapNavigationBound[3]) || GameManager.getInstance().scrollDirection == 7){
				if(x < 0)
					GameManager.getInstance().scrollDirection = 7;
				else{
					GameManager.getInstance().scrollDirection = 0;
					Player.getInstance().setX(Player.getInstance().getX() + 10);
				}	
			}

		
		
		if(GameManager.getInstance().scrollDirection != 0){
			Player.getInstance().setInputLock(true);
			Player.getInstance().slowDownHalf();
			Camera.getInstance().scrollCamera(this);
		}
		
		
		ArrayList<Rectangle> toExitRect = getToExitBound();
		ArrayList<int[]> toExitData = getNavigationToExitData();
		
		for(int index = 0; index < toExitRect.size(); index++){
			
			Camera cam = Camera.getInstance();
			Rectangle rect = toExitRect.get(index);
			//System.out.println("toEXIT==========>@"+toExitRect.get(index).x+"x"+toExitRect.get(index).y+", "+toExitRect.get(index).width+"x"+toExitRect.get(index).height);
			//System.out.println("cam@"+cam.getX()+"x"+cam.getY());
			
			if((Player.getInstance().getBoundDirection(0).intersects(rect.x-cam.getX(), rect.y-cam.getY(),rect.width,rect.height))){
				System.err.println("intersectExit");
				
				
				int[] data = toExitData.get(index);
				
				int mapType = data[0];
				int mapID = data[1];
				int xMap = data[2];
				int yMap = data[3];
				int xPlayer = data[4];
				int yPlayer = data[5];
				
				System.err.println("Player@DungeonLoadMap:"+xPlayer+"x"+yPlayer);
				
				if(mapType == 0){
					GameManager.getInstance().dungeon = true;
					GameManager.getInstance().overWorld = false;
					GameManager.getInstance().mapLoaded = false;
					DungeonNavigator.getInstance().initializeMap(xMap, yMap, mapID, xPlayer, yPlayer);
					System.out.println("dungeonMap_");
				}
					
				if(mapType == 1){
					GameManager.getInstance().dungeon = false;
					GameManager.getInstance().overWorld = true;
					GameManager.getInstance().mapLoaded = false;
					this.initializeMap(xMap-810, yMap-630, mapID, xPlayer, yPlayer);
					//OverWorldNavigator.getInstance().initializeMap(xMap-810, yMap-630, mapID, 400, 300);
					System.out.println("overworldMap_"+xMap+"x"+yMap);
				}
			}
			
		}
		
		
			
	}
	
	public static void resetInstance(){
		if(overWorldNavigator != null)
			overWorldNavigator = new OverWorldNavigator();
	}

	public static OverWorldNavigator getInstance(){
		if(overWorldNavigator == null)
			overWorldNavigator = new OverWorldNavigator();
		return overWorldNavigator;
	}

}
