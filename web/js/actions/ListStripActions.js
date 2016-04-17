import {STRIP_SELECT, STRIP_ON_LODING, STRIP_SUCCEDED, STRIP_FAILED} from '../constants/ActionTypes';

import 'isomorphic-fetch';

function onLoadingStrips(comic_id) {
  return {
    type: STRIP_ON_LODING,
    comic_id
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
    
    dispatch(onLoadingStrips(comic_id)); // sets the loading state

    // just ask for the comics we have never listed
    let cache = JSON.parse(localStorage.getItem(comic_id))
    let last_item = cache ? cache[0] : undefined
    let second_param = last_item ? `/${last_item._id}`: ''

    return fetch('http://comic.allocsoc.net/comics/' + comic_id + second_param)
      .then(function(response) {
	return response.json();
      }).then(function(json) {
	let result = json['result'].concat(cache);
	localStorage.setItem(comic_id, JSON.stringify(result));
	dispatch(onStripsSucced(comic_id, result));
      }).catch(function(err) {
	dispatch(onStripFailed(err));
      });
  };
}
