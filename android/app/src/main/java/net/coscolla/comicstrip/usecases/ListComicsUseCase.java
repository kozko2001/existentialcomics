package net.coscolla.comicstrip.usecases;

import net.coscolla.comicstrip.entities.Comic;
import net.coscolla.comicstrip.net.api.ComicResults;

import java.util.List;

import rx.Observable;

public interface ListComicsUseCase {

  Observable<List<Comic>> getComicsObservable();

  Observable<List<Comic>> refresh();

}
