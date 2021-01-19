package com.sebastian.utoiu.cursvalutar.database;

import java.util.ArrayList;

abstract class Observable<T>
{


	public ArrayList<T> getObservers()
	{
		return observers;
	}
	protected ArrayList<T> observers = new  ArrayList();

	public void  registerObserver( T observer)
	{
		synchronized(observers) {
			if (observer != null && !observers.contains(observer))
			{
				observers.add(observer);
			}
		}
	}

	public void  unregisterObserver( T observer)
	{
		synchronized(observers) {
			if (observer != null && observers.contains(observer))
			{
				observers.remove(observer);
			}
		}
	}

}
