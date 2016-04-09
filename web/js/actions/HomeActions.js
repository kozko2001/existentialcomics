import {TITLE_CHANGED} from '../constants/ActionTypes';
import {COMIC_ON_LODING, COMIC_SUCCEDED, COMIC_FAILED} from '../constants/ActionTypes';
import 'whatwg-fetch';

export function changeTitle(text) {
  return {
    type: TITLE_CHANGED,
    text
  };
}

function onLoadingComics() {
  return {
    type: COMIC_ON_LODING
  };
}

function onComicsSucced(comics) {
  return {
    type: COMIC_SUCCEDED,
    data: comics
  };
}

function onComicFailed(err) {
  return {
    type: COMIC_FAILED,
    err
  };
}

export function fetchComics() {
  return function(dispatch, getState) {
    dispatch(onLoadingComics()); // sets the loading state

    fetch('http://comic.allocsoc.net/comics')
      .then(function(response) {
	return response.json();
      }).then(function(json) {
	dispatch(onComicsSucced(json));
      }).catch(function(err) {
	console.log(err);
	display(onComicFailed(err));
      });
  };
}
