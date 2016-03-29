package net.coscolla.comicstrip.usecases;

import net.coscolla.comicstrip.entities.Strip;

import java.util.List;

import rx.Observable;

public interface ListStripsUseCase {

  /**
   * Observable that returns the current model when something changes about the strip entity
   */
  Observable<List<Strip>> observableModel(String comic);

  /**
   * Observable to make a request and refresh the model from the network if new data is retrieved
   * the model is updated and the observableModel is notified
   */
  Observable<List<Strip>> refresh(String comic);
}
