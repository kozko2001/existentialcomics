import {STRIP_SELECT, STRIP_ON_LODING, STRIP_SUCCEDED, STRIP_FAILED} from '../constants/ActionTypes';

import 'isomorphic-fetch';

function onLoadingStrips() {
  return {
    type: STRIP_ON_LODING
  };
}


function onStripsSucced(comic_id, strips) {
  return {
    type: STRIP_SUCCEDED,
    data: strips,
    comic_id
  };
}

function onStripFailed(err) {
  return {
    type: STRIP_FAILED,
    err
  };
}

export function onChangeStripSelected(strip) {
  return {
    type: STRIP_SELECT,
    data: strip
  }
}

export function fetchStrips(comic_id) {
  return function(dispatch, getState) {
    dispatch(onLoadingStrips()); // sets the loading state

    return fetch('http://comic.allocsoc.net/comics/' + comic_id)
      .then(function(response) {
	return response.json();
      }).then(function(json) {
	dispatch(onStripsSucced(comic_id, json['result']));
      }).catch(function(err) {
	dispatch(onStripFailed(err));
      });
  };
}
