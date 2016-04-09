import * as ActionTypes from '../constants/ActionTypes';

let defaultState = {
  loading: false,
  data: [], // array of comics
  last_error: undefined
}

export default function(state = defaultState, action) {
  switch (action.type) {
  case ActionTypes.COMIC_ON_LODING:
    return { ...state, loading: true}
  case ActionTypes.COMIC_SUCCEDED:
    return { ...state, data: action.data, loading: false}
  case ActionTypes.COMIC_FAILED:
    return { ...state, err: action.err}
  default:
    return state;
  }
}

