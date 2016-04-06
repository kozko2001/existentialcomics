package net.coscolla.comicstrip.usecases;

import net.coscolla.comicstrip.entities.Comic;
import net.coscolla.comicstrip.entities.Strip;

import rx.Observable;

public interface PushReceiveUseCase {

  Observable<Strip> getLastStrip(String comic);

  Observable<Comic> getComic(String comicId);

}
