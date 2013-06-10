package game.objects;


import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import map.Camera;

import core.Board;
import core.GameManager;

public class ItemDrop extends Item{
	
	private ItemDrop itemDrop;
	
	private int duration;
	private int counter;
	private int[] itemIDData = new int[3];
	
	private int offsetX, offsetY;
	
	Thread runThread = new Thread(new RunTimer());
	ScheduledExecutorService execRun = Executors.newSingleThreadScheduledExecutor();
	
	private ItemDrop(int x, int y, int[] data, File file, int duration){
		System.out.println("--> construct new Item");
		itemDrop = this;
		
		//x - cam = x
		
		setX(x);
		setY(y);
	
		
		this.duration = duration;
		initializeItemDrop(data, file);
	}
	
	public void running(){
		
		//System.out.println(getUpLock()+","+getDownLock()+"__"+getRightLock()+","+getLeftLock());
		
		if(GameManager.cameraOn){
			if(!(getUpLock() || getDownLock()) && !(getLeftLock() || getRightLock())){
				System.err.println("case.1");
				setMovement(-Player.getInstance().getDX(), -Player.getInstance().getDY());
			} else if((getUpLock() || getDownLock()) && !(getLeftLock() || getRightLock())){
				System.err.println("case.2");
				setMovement(-Player.getInstance().getDX(), 0);
				
			} else if(!(getUpLock() ||  getDownLock()) && (getLeftLock() || getRightLock())){
				System.err.println("case.3");
				setMovement(0, -Player.getInstance().getDY());
				
			} else if((getUpLock() || getDownLock()) && (getLeftLock() || getRightLock())){
				System.err.println("case.4");
				setMovement(0, 0);
			}
		}
		
			
		
		//System.out.println("Item alive@"+counter+", to "+duration);
		//System.out.println("Item visible:"+getVisibleDrawable());
		
		setStaticSubSprite();
		
		if(Player.getInstance().getBoundCore().intersects(this.getBoundCore())){
			System.out.println("Player.intersectItem");
			Player.getInstance().addItem(itemIDData);
			setAlive(false);
		}
		
	}

	private void initializeItemDrop(int[] data, File file){
		
		/*
		 * itemData[0] = ID;
			itemData[1] = type;
			itemData[2] = member;
			itemData[3] = xRow;
			itemData[4] = yRow;
			itemData[5] = cycle;
		 */
		itemIDData[0] = data[0];
		itemIDData[1] = data[1];
		itemIDData[2] = data[2];
		
		setStaticX(data[3]);
		setStaticY(data[4]);
		setStaticCycle(data[5]);
		setFile(file);
		loadSprite();
	
		Thread runThread = new Thread(new RunTimer());
		ScheduledExecutorService execRun = Executors.newSingleThreadScheduledExecutor();
		execRun.scheduleWithFixedDelay(runThread, 10, 20, TimeUnit.MILLISECONDS);
		
		Board.getInstance().addDrawable(this);
	}
	
	public static ItemDrop addInstance(int xPosition, int yPosition, int[] data, File file,int duration){
		System.out.println("add Item@Pos "+xPosition+"x"+yPosition);
		return new ItemDrop(xPosition, yPosition, data, file, duration);
	}
	
	private class RunTimer implements Runnable{
		
		private RunTimer(){
			
		}
		
		public void run(){
			
			//System.out.println("runnerTimer");
			
			if(counter == duration){
				setAlive(false);
			}
			
			counter++;
	
			if(getAlive())
				running();
			
			else {
				setVisibleDrawable(false);
				itemDrop = null;
				execRun.shutdown();
				execRun = null;
				
			}
		}
		
	}
	
}