package com.cmov.bomberman;

import java.util.ArrayList;
import java.util.List;

public class Game {
	private String gameName = null;
	private List<Player> players = new ArrayList<Player>();
	private LogicalWorld logicalWorld;
	private boolean running = false;

	public Game(String name) {
		this.gameName = name;
		this.logicalWorld = new LogicalWorld();
	}

	public boolean addPlayer(Player player) {
		player.setID(players.size() + 1);

		// Adds player to playground view, set starting position
		int x = ConfigReader.players.getXCor();
		int y = ConfigReader.players.getYCor();
		System.out.println("Player is going to insert in to map x "+ x +"|" + "y "+ y);
		

		this.logicalWorld.setElement(x, y, player.getID(), player);
		player.setPosition(x, y);

		players.add(player);
		player.setGame(this);
		System.out.println("Player" + player.getID()
				+ "added to logical world (" + player.getNickname() + ")");
		return true;
	}

	public int getPlayerCount() {
		return this.players.size();
	}

	public LogicalWorld getLogicalWorld() {
		return this.logicalWorld;
	}

	public List<Player> getPlayers() {
		return this.players;
	}

	public void removePlayer(int x, int y, Player player) {
		if (this.logicalWorld.getElement(x, y).equals(player)) // Removes only
																// the selected
																// player
			this.logicalWorld.setElement(x, y, player.getID(), null);
	}

	public boolean movePlayer(Player player, int dx, int dy) {
		if (dx == 0 && dy == 0)
			return true;

		// Check if we can move in that direction
		int nx = player.getWorldXCor() + dx;
		int ny = player.getWorldYCor() + dy;

		if (nx < 0 || ny < 0 || this.logicalWorld.getWidth() <= nx
				|| this.logicalWorld.getHeight() <= ny)
			return false;
		// player can not move if there is wall or obstacle , have to add here.
		Cell el = this.logicalWorld.getElement(nx, ny)[0];
		if (el == null) // oder Extra
		{
			// Set old position in Playground to null...
			ConfigReader.gridlayout[player.getWorldXCor()][player.getWorldYCor()] ='-';
			this.logicalWorld.setElement(player.getWorldXCor(),
					player.getWorldYCor(), player.getID(), null);
			// ...and set new position
			player.move(dx, dy);
			this.logicalWorld.setElement(player.getWorldXCor(),
					player.getWorldYCor(), player.getID(), player);
			ConfigReader.gridlayout[player.getWorldXCor()][player.getWorldYCor()] ='1';

			return true;
		} else
			return false;
	}

	@Override
	public String toString() {
		return this.gameName;
	}

	public void setPlayground(LogicalWorld logicalworld) {
		this.logicalWorld = logicalworld;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
}
