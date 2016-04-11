import * as ActionTypes from '../constants/ActionTypes';

let defaultState = {
  loading: false,
  data: {}, // {comic_id: [strip, strip, strip}}
  last_error: undefined
}

export default function(state = defaultState, action) {
  switch (action.type) {
  case ActionTypes.STRIP_ON_LODING:
    return { ...state, loading: true}
  case ActionTypes.STRIP_SUCCEDED:
    var data = {...data}; // TODO: Search better way to do it!
    data[action.comic_id] = action.data;
    return { ...state, data, loading: false, last_error: undefined}
  case ActionTypes.STRIP_FAILED:
    return { ...state, last_error: action.err.message, loading: false}
  default:
    return state;
  }
}
