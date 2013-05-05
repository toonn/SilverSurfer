/**
 * een object van deze klasse kan maar 1x gebruikt worden!
 * velden worden ingevuld bij aanmaak object en kunnen niet meer veranderen.
 */

package mazeAlgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import mapping.Orientation;
import mapping.Seesaw;
import mapping.Tile;
import simulator.pilot.AbstractPilot;

public class ShortestPath {

	private MazeExplorer explorer;
	private final ArrayList<Tile> queue = new ArrayList<Tile>();
	private final Vector<Tile> tilesPath = new Vector<Tile>();
	private Vector<Tile> tiles;
	private Tile startTile;
	private Tile currentTileDuringException;
	private Tile endTile;
	private AbstractPilot pilot;
	private int extraCostSeesaw = 4;
	private boolean pathCalculated = false;
	private boolean quit = false;

	public ShortestPath(MazeExplorer explorer, final AbstractPilot pilot, final Tile startTile, final Tile endTile, final Vector<Tile> tiles) {
		this.explorer = explorer;
		this.pilot = pilot;
		this.startTile = startTile;
		this.endTile = endTile;
		this.tiles = tiles;
		for (final Tile tile : tiles) {
			tile.setMarkingShortestPath(false);
			tile.resetCost();
		}
	}
	
	protected Tile getCurrentTileDuringException() {
		return currentTileDuringException;
	}

	public void quit(boolean quit) {
		this.quit = quit;
	}
	
	public Vector<Tile> getTilesPath() {
		return tilesPath;
	}

	/**
	 * wordt opgeroepen als einddoel bereikt is in fillTilesPath. tilesPath
	 * bevat nu alle tiles die afgegaan zijn dus diegene die niet naar het doel
	 * leiden moeten nog verwijderd worden. dit gebeurt als volgt: ge begint
	 * vanaf uw voorlaatste tile en checkt of deze buren is met de volgende EN
	 * een kost heeft 1 minder als de kost van de volgende, indien dit niet zo
	 * is , wordt deze tile verwijderd en checkt men de tile ervoor, enz...
	 */
	private void deleteSuperfluousTiles() {
		for (int i = tilesPath.size() - 2; i > 0; i--) {
			if ((tilesPath.get(i).getCost() != tilesPath.get(i + 1).getCost() - 1) || !tilesPath.get(i).areReachableNeighboursIgnoringSeesaw(tilesPath.get(i + 1)))
				tilesPath.remove(i);
		}
	}
	
	private void fillTilesPath(final Tile currentTile) {
		tilesPath.add(currentTile);
		if ((currentTile.getContent() instanceof Seesaw && currentTile.getManhattanValue() == extraCostSeesaw) || currentTile.getManhattanValue() == 0) {
			deleteSuperfluousTiles();
			return;
		}

		// voeg neighbourTiles van de currentTile toe aan de queue
		for (final Tile neighbourTile : currentTile.getReachableNeighboursIgnoringSeesaw()) {
			if (tiles.contains(neighbourTile) && !neighbourTile.isMarkedShortestPath()) {
				neighbourTile.setCost(currentTile.getCost() + 1);
				queue.add(neighbourTile);
			}
		}

		// sorteer de queue: grootste vooraan
		Collections.sort(queue, new Comparator<Tile>() {
			@Override
			public int compare(final Tile o1, final Tile o2) {
				if (o1.getManhattanValue() + o1.getCost() < o2
						.getManhattanValue() + o2.getCost()) {
					return 1;
				} else if (o1.getManhattanValue() + o1.getCost() == o2
						.getManhattanValue() + o2.getCost()) {
					return 0;
				} else {
					return -1;
				}
			}
		});

		currentTile.setMarkingShortestPath(true);

		if (queue.size() > 0) {
			// remove the last tile from the queue and add it to the path
			final Tile nextTile = queue.get(queue.size() - 1);
			while (queue.contains(nextTile))
				queue.remove(nextTile);
			fillTilesPath(nextTile);
		}
	}

	/**
	 * De methode die moet opgeroepen worden en alle methodes in de juiste
	 * volgorde uitvoert. eerst worden de heuristieken gezet dan fillTilesPath,
	 * en aan de hand hiervan wordt naar de robot/simulator het commando
	 * gestuurd om deze tiles te "bewandelen". Op het einde wordt de kost van
	 * alle tiles terug op hun initiele waarde gezet.
	 */
	protected void goShortestPath1Tile(boolean specialBarcodeCase) throws CollisionAvoidedException {
		if(!pathCalculated)
			calculatePath();

		if (tilesPath.size() == 1 || quit) {
			for (final Object tile : tiles)
				((Tile) tile).resetCost();
			return;
		}
		
		Orientation orientation = null;
		for (Orientation ori : Orientation.values())
			if (tilesPath.get(0).getEdgeAt(ori) == tilesPath.get(1).getEdgeAt(ori.getOppositeOrientation()))
				orientation = ori;
		
		boolean readBarcodesBackup = pilot.getReadBarcodes();
		pilot.setReadBarcodes(false);
		pilot.rotate((int) explorer.getSmallestAngle((int) (orientation.getAngle() - pilot.getAngle())));
		pilot.updateTilesAndPosition();

		if(specialBarcodeCase && !tilesPath.get(1).isMarkedExploreMaze())
			pilot.setReadBarcodes(true);
		else if (tilesPath.size() <= 2 && !specialBarcodeCase)
			pilot.setReadBarcodes(readBarcodesBackup);
		try {
			pilot.alignOnWhiteLine();
			pilot.updateTilesAndPosition();
		} catch(CollisionAvoidedException e) {
			currentTileDuringException = tilesPath.get(0);
			pilot.setReadBarcodes(readBarcodesBackup);
			throw new CollisionAvoidedException();
		}
		pilot.setReadBarcodes(readBarcodesBackup);

		pilot.decreaseTilesBeforeAlign();
		tilesPath.remove(0);
	}

	private void calculatePath() {
		setHeuristics();
		startTile.setCost(0);
		fillTilesPath(startTile);
		pathCalculated = true;
	}

	/**
	 * zet de heuristiek op elke tile afhankelijk van de endTile die
	 * heuristiekwaarde 0 krijgt.
	 */
	private void setHeuristics() {
		for (final Tile tile : tiles) {
			int heuristic = (int) (Math.abs(endTile.getPosition().getX() - tile.getPosition().getX()) + Math.abs(endTile.getPosition().getY() - tile.getPosition().getY()));
			if (tile.getContent() instanceof Seesaw) {
				heuristic = heuristic + extraCostSeesaw;
			}
			tile.setManhattanValue(heuristic);
		}
	}

	public int getTilesAwayFromTargetPosition() {
		if(!pathCalculated)
			calculatePath();
		return tilesPath.size()-1;
	}
}