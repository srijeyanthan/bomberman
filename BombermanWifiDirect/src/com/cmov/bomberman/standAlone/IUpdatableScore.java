package com.cmov.bomberman.standAlone;

public interface IUpdatableScore {

	 void UpdateScore(int numberOfRobotDied) ; // currently we are not passing anything , in future if we want to pass the type of the objects either
	                        // robot or opponent , we can specify them here.
}
