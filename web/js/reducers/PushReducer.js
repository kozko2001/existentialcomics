import * as ActionTypes from '../constants/ActionTypes'
import * as push from '../push/push'

export default function(state, action) {
  return {
    topics: push.getTopicsSubscribed()
  }
}
