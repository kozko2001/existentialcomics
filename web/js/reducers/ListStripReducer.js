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
    return { ...state, loading: true, selected: undefined}
  case ActionTypes.STRIP_SUCCEDED:
    return {
	...state,
      data: action.data,
      loading: false,
      last_error: undefined,
      selected: action.data[0],
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
