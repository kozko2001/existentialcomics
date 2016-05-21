import * as ActionTypes from '../constants/ActionTypes';

let defaultState = {
  loading: false,
  data: [], // [strip, strip, strip]
  last_error: undefined,
  selected: undefined,
  comic_id: undefined
}

export default function(state = defaultState, action) {
  switch (action.type) {
    case ActionTypes.STRIP_ON_LODING:
	var cache = JSON.parse(localStorage.getItem(action.comic_id))
	return { ...state, loading: true, selected: undefined, data: cache}
    case ActionTypes.STRIP_SUCCEDED:

	let strip_id = action && action.strip_id;
	let selectedStrip = action.data.filter((strip) => strip && strip._id === strip_id);

	return {
	    ...state,
	    data: action.data,
	    loading: false,
	    last_error: undefined,
	    selected: selectedStrip[0] || action.data[0],
	    comic_id: action.comic_id
	}
    case ActionTypes.STRIP_FAILED:
	return { ...state, last_error: action.err.message, loading: false}
    case ActionTypes.STRIP_SELECT:
	return {...state, selected: action.data}
    default:
	return state;
    }
}
