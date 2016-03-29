package net.coscolla.comicstrip.usecases;

import net.coscolla.comicstrip.entities.Strip;

import rx.Observable;
import rx.Single;

public interface DetailStripUseCase {

  String getStripImageUrl(Strip strip);

  Observable<Strip> getStripById(String id);
}
