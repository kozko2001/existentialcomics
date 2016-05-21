import {COMIC_ON_LODING, COMIC_SUCCEDED, COMIC_FAILED} from '../constants/ActionTypes';
import {API_ENDPOINT} from '../constants/Constants.js'

import 'isomorphic-fetch';

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

    return fetch(`${API_ENDPOINT}comics`)
      .then(function(response) {
	return response.json();
      }).then(function(json) {
	dispatch(onComicsSucced(json['comics']));
      }).catch(function(err) {
	dispatch(onComicFailed(err));
      });
  };
}
