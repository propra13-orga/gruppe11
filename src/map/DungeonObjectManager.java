package map;


import java.awt.Rectangle;
import java.util.ArrayList;

import objects.MapObject;

import core.GameObjectManager;

abstract class DungeonObjectManager extends Map{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1322125265245622409L;
	private volatile int xMap;
	private volatile int yMap;
	private int mapRowX;
	private int mapRowY;
	private int mapID;
	
	private ArrayList<NavigationData<int[], Rectangle, int[]>> navigationData = new ArrayList<NavigationData<int[], Rectangle, int[]>>();
	private ArrayList<EnemyData<Integer, int[], int[]>> enemyData = new ArrayList<EnemyData<Integer, int[], int[]>>();
	private ArrayList<MapObjectData<Integer, Integer, Integer, Integer, Integer, Integer,Integer>> mapObjectData = new ArrayList<MapObjectData<Integer, Integer, Integer, Integer, Integer,Integer, Integer>>();
	
	private int[] tmpNavCoordinates = new int[3]; //0 = xMap, 1 = yMap, 2 = Orientation
	private Rectangle tmpNavRect = new Rectangle();
	private int[] tmpNavData = new int[6]; //dataType
	
	//doorIDCounter [xMap][yMap][element] = x (x = 1: open, x = 0: locked)
	
	
	/*
	 * 
	 * dataType 0 = mapTyp ( dungeon = 0; overWorld = 1)
	 * dataType 1 = mapID
	 * dataType 2 = xMap
	 * dataType 3 = yMap
	 * dataType 4 = xPlayer
	 * dataType 5 = yPlayer
	 * 
	 * orientation 0 = toExit
	 * orientation 1 = toNorth
	 * orientation 2 = toEast
	 * orientation 3 = toSouth
	 * orientation 4 = toWest
	 */
	

	//objectBound[xMap][yMap][layer][orientation][xTile][yTile][dimension]
	private Rectangle objectBound[][][][][][][];
	
	private final Rectangle NULLRECT = new Rectangle(0,0,0,0);
	
	protected DungeonObjectManager(){
		
	}
	
	protected void initializeBoundArrays(){
	
		tmpNavCoordinates = new int[3]; //0 = xMap, 1 = yMap, 2 = Orientation
		tmpNavRect = new Rectangle();
		tmpNavData = new int[6]; //comp. dataType
		
		objectBound = new Rectangle[mapRowX][mapRowY][7][13][9][7][3];
		
		
		clearObjectBounds();
	}
	

		protected void addNavigationBound(int xMapTmp, int yMapTmp, int orientation, Rectangle elementBound){
			
			//erase old tmpVariables, to avoid latent conflicts
			tmpNavCoordinates = new int[3];
			tmpNavRect = new Rectangle(0,0,0,0);
			
			tmpNavRect = elementBound;
			tmpNavCoordinates[0] = xMapTmp;
			tmpNavCoordinates[1] = yMapTmp;
			tmpNavCoordinates[2] = orientation;

			
		}

		
		//ObjectBounds
		protected void addObjectBound(int xMapTmp, int yMapTmp, int layer, int orientation, int xTile, int yTile, int dimension, Rectangle objectElement){
			objectBound[xMapTmp][yMapTmp][layer][orientation][xTile][yTile][dimension] = objectElement;
			
			
		}
		
		
		protected Rectangle[][][][][][][] getObjectBound(){
			return objectBound;
		}
		
		protected Rectangle[][][][][] getMapObjectBounds(int xMapTmp, int yMapTmp){
			
			Rectangle[][][][][] mapObjectBound = new Rectangle[7][13][9][7][3];
			
			for(int layer = 0; layer < 7; layer++){
				for(int orientation = 0; orientation < 13; orientation++){
					
					for(int yTile = 0; yTile < 7; yTile++){
						for(int xTile = 0; xTile < 9; xTile++){
							for(int element = 0; element < 3; element++){
								
								mapObjectBound[layer][orientation][xTile][yTile][element] = objectBound[xMapTmp][yMapTmp][layer][orientation][xTile][yTile][element];
								//mapObjectBound[layer][orientation][xTile][yTile][element] = objectBound[3][3][3][orientation][xTile][yTile][element];
								//addWallBoundPaint(mapObjectBound[layer][orientation][xTile][yTile][element]);
								
								////objectBound[xMap][yMap][layer][orientation][xTile][yTile][dimension]
								
							}
							
						}
						
					}
					
				}
			}
			
			return mapObjectBound;
		}
		
		void clearObjectBounds(){
			
			
			for(int yMapTmp = 0; yMapTmp < mapRowY; yMapTmp++){
				for(int xMapTmp = 0; xMapTmp < mapRowX; xMapTmp++){
					
					for(int layer = 0; layer < 7; layer++){
						for(int orientation = 0; orientation < 13; orientation++){
							
							for(int yTile = 0; yTile < 7; yTile++){
								for(int xTile = 0; xTile < 9; xTile++){
									for(int element = 0; element < 3; element++){
										objectBound[xMapTmp][yMapTmp][layer][orientation][xTile][yTile][element] = NULLRECT;
									}
									
								}
								
							}
							
						}
					}
				}
				
			}
			
		}
		
		void clearMapObjectBounds(int xMapTmp, int yMapTmp){
			
			for(int layer = 0; layer < 7; layer++){
				
				for(int yTile = 0; yTile < 7; yTile++){
					for(int xTile = 0; xTile < 9; xTile++){
						
						for(int element = 0; element < 2; element++){
							for(int orientation = 0; orientation < 5; orientation++){
								objectBound[xMapTmp][yMapTmp][layer][orientation][xTile][yTile][element] = NULLRECT;
							}	
						}
					}
				}
			}
			this.clearPaintBounds();
		}
		
		//NavigationToExitData
		protected void addNavigationToExitData(int mapIDX, int mapIDY, int mapType, int mapID, int xMap, int yMap, int xPlayer, int yPlayer){
	
			tmpNavData = new int[6];
			
			tmpNavData[0] = mapType;
			tmpNavData[1] = mapID;
			tmpNavData[2] = xMap;
			tmpNavData[3] = yMap;
			tmpNavData[4] = xPlayer;
			tmpNavData[5] = yPlayer;
	
			checkAndSetNavigationData(mapIDX, mapIDY);
			
		}

		public synchronized int getXMap(){return xMap;}
		public synchronized int getYMap(){return yMap;}
		protected int getMapRowX(){return mapRowX;}
		protected int getMapRowY(){return mapRowY;}
		protected int getMapID(){return mapID;}
		
		//
		protected synchronized void setXMap(int xMap){this.xMap = xMap;}
		protected synchronized void setYMap(int yMap){this.yMap = yMap;}
		protected void setMapRowX(int mapRowX){this.mapRowX = mapRowX;}
		protected void setMapRowY(int mapRowY){this.mapRowY = mapRowY;}
		protected void setMapID(int mapID){this.mapID = mapID;}
		
		
		private void checkAndSetNavigationData(int mapIDX, int mapIDY){
			
			if(tmpNavCoordinates[0] == mapIDX && tmpNavCoordinates[1] == mapIDY){
				System.out.println("SetNavigationData");
				addNavigationData(tmpNavCoordinates, tmpNavRect, tmpNavData);
				
				
				System.out.println("_Map@"+tmpNavCoordinates[0]+"x"+tmpNavCoordinates[1]+".orientation:"+tmpNavCoordinates[2]);
				System.out.println("__Rect@"+tmpNavRect.x+"x"+tmpNavRect.y+", "+tmpNavRect.width+"x"+tmpNavRect.height);
				
			

				tmpNavCoordinates = null;
				tmpNavRect = null;
				tmpNavData = null;
			} else {
				System.err.println("DungeonObjectManager.Error: Wrong Checksum in NavigationData");
				System.exit(0);
			}
			
			
		}

		private void addNavigationData(int[] coordinates, Rectangle rect, int[] data){
			
			NavigationData<int[], Rectangle, int[]> dataBox = 
					new NavigationData<int[], Rectangle, int[]>(coordinates, rect, data);
					
			navigationData.add(dataBox);
			
		}
		
		protected ArrayList<NavigationData<int[], Rectangle, int[]>> getNavigationData(){
			return navigationData;
		}
		
		protected ArrayList<NavigationData<int[], Rectangle, int[]>> getNavigationDataMap(int xMapTmp, int yMapTmp){
			ArrayList<NavigationData<int[], Rectangle, int[]>> navigationDataMap = 
						new ArrayList<NavigationData<int[], Rectangle, int[]>>();
			 
			
			
			for(int index = 0; index < navigationData.size(); index++){
				
				int [] coordinates = navigationData.get(index).getCoordinates();
				
				if(coordinates[0] == xMapTmp && coordinates[1] == yMapTmp){
					navigationDataMap.add(navigationData.get(index));
				}
					
			}
			
			return navigationDataMap;
		}
		
		
		protected ArrayList<Rectangle> getNavigationRectMap(int xMapTmp, int yMapTmp){
			
		ArrayList<Rectangle> navigationRectMap = new ArrayList<Rectangle>();
	
		for(int index = 0; index < navigationData.size(); index++){
			
			int [] coordinates = navigationData.get(index).getCoordinates();
			
			if(coordinates[0] == xMapTmp && coordinates[1] == yMapTmp){
				navigationRectMap.add(navigationData.get(index).getRect());
			}
				
		}
		return navigationRectMap;
			
		}
		
		protected void addMapObjectData(int type, int orientation, int xMapTmp, int yMapTmp, int xPosition, int yPosition){

			int ID = Integer.parseInt(String.valueOf(xMapTmp+""+yMapTmp+""+orientation));
			
		
			//Door
			if(type == 0){
				
				if(!GameObjectManager.getInstance().getDoorStatusOpen(ID)){
					MapObjectData<Integer, Integer, Integer, Integer, Integer,Integer, Integer> mapObject = new MapObjectData<Integer, Integer, Integer, Integer, Integer, Integer,Integer>(ID, type, orientation, xMapTmp, yMapTmp, xPosition, yPosition);
					mapObjectData.add(mapObject);
					MapObject.addInstance(ID, type, orientation, xMapTmp, yMapTmp, xPosition, yPosition);
					GameObjectManager.getInstance().constructDoor(ID);
		
				}
				
			} else {
				MapObjectData<Integer, Integer, Integer, Integer, Integer,Integer, Integer> mapObject = new MapObjectData<Integer, Integer, Integer, Integer, Integer, Integer,Integer>(ID, type, orientation, xMapTmp, yMapTmp, xPosition, yPosition);
				mapObjectData.add(mapObject);
				MapObject.addInstance(ID, type, orientation, xMapTmp, yMapTmp, xPosition, yPosition);
			}

		}
		

		protected void deleteMapObjectData(){
			mapObjectData.clear();
			MapObject.deleteAllInstances();
		}
		
		protected void addEnemyData(int type, int[] position, int[] attributes){
			
			Integer typeInteger = (Integer) type;
			EnemyData<Integer, int[], int[]> element = new EnemyData<Integer, int[], int[]>(typeInteger, position, attributes);
			
			
			enemyData.add(element);
			
			int[] pos = enemyData.get(0).getPosition();
			System.out.println("EnemyData@Map_"+pos[0]+"x"+pos[1]+", Pos_"+pos[2]+"x"+pos[3]);
			
		}
		
		protected ArrayList<EnemyData<Integer, int[], int[]>> getEnemyDataMap(int xMapTmp, int yMapTmp){
			
			ArrayList<EnemyData<Integer, int[], int[]>> enemyDataMap = new ArrayList<EnemyData<Integer, int[], int[]>>();
			
			for(int index = 0; index < enemyData.size(); index++){
				
				int[] position = enemyData.get(index).getPosition();
				
				if(position[0] == xMapTmp && position[1] == yMapTmp)
					enemyDataMap.add(enemyData.get(index));
				
			}
			
			return enemyDataMap;
			
		}
		
		public void resetObjectManager(){
			navigationData = new ArrayList<NavigationData<int[], Rectangle, int[]>>();
			enemyData = new ArrayList<EnemyData<Integer, int[], int[]>>();
			mapObjectData = new ArrayList<MapObjectData<Integer, Integer, Integer, Integer, Integer,Integer, Integer>>();
			
			tmpNavCoordinates = new int[3]; //0 = xMap, 1 = yMap, 2 = Orientation
			tmpNavRect = new Rectangle();
			tmpNavData = new int[6]; //dataType
			
		}
		
		
		
		
		protected class NavigationData<C, R, D>{
			public final C coordinates;
			public final R rectangle;
			public final D data;
			
			public NavigationData(C coordinates, R rectangle, D data){
				this.coordinates = coordinates;
				this.rectangle = rectangle;
				this.data = data;
				//System.out.println("____setNavigationData___");
			}
			
			public C getCoordinates(){
				return coordinates;
			}
			
			public R getRect(){
				return rectangle;
			}
			
			public D getData(){
				return data;
			}
			
		}

		protected class EnemyData<T, P, A>{
			public final T type;
			public final P position;
			public final A attributes;
			
			public EnemyData(T type, P position, A attributes){
				this.type = type;
				this.position = position;
				this.attributes = attributes;
				
			}
			
			public T getType(){
				return type;
			}
			
			public P getPosition(){
				return position;
			}
			
			public A getAttributes(){
				return attributes;
			}
			
		}
		
		protected class MapObjectData<I, T, O, XM, YM, XP, YP>{
			public final I ID;
			public final T type;
			public final O orientation;
			public final XM xMapPosition;
			public final YM yMapPosition;
			public final XP xPosition;
			public final YP yPosition;
			
			private MapObjectData(I ID, T type, O orientation, XM xMapPos, YM yMapPos, XP xPosition, YP yPosition){
				this.ID = ID;
				this.type = type;
				this.orientation = orientation;
				this.xMapPosition = xMapPos;
				this.yMapPosition = yMapPos;
				this.xPosition = xPosition;
				this.yPosition = yPosition;
			}
			public I getID(){return ID;}
			public T getType(){return type;}
			public O getOrientation(){return orientation;}
			public XM getXMapPosition(){return xMapPosition;}
			public YM getYMapPosition(){return yMapPosition;}
			public XP getXPosition(){return xPosition;}
			public YP getYPosition(){return yPosition;}
		}

}
