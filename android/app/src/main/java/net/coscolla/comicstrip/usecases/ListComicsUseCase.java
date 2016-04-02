package net.coscolla.comicstrip.usecases;

import net.coscolla.comicstrip.entities.Comic;

import java.util.List;

import rx.Observable;

public interface ListComicsUseCase {

  /**
   * Observable that returns the current state of the model
   */
  Observable<List<Comic>> model();

  /**
   * Observable to make a request and refresh the model from the network if new data is retrieved
   * the model is updated and the current state of the model is returned
   */
  Observable<List<Comic>> refresh();

}
