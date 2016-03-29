package net.coscolla.comicstrip.usecases;

import net.coscolla.comicstrip.entities.Comic;
import net.coscolla.comicstrip.net.api.ComicResults;

import java.util.List;

import rx.Observable;

public interface ListComicsUseCase {

  /**
   * Observable that returns the current model when something changes about the comic entity
   */
  Observable<List<Comic>> observableModel();

  /**
   * Observable to make a request and refresh the model from the network if new data is retrieved
   * the model is updated and the observableModel is notified
   */
  Observable<List<Comic>> refresh();

}
