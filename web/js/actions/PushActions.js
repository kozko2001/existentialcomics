import {SUBSCRIBE_CHANGED} from '../constants/ActionTypes';
import {API_ENDPOINT} from '../constants/Constants';
import * as push from '../push/push'

import 'isomorphic-fetch';

function onSubscribeChanged(topics) {
  return {
    type: SUBSCRIBE_CHANGED,
    topics
  };
}

export function subscribe(topic) {
  push.subscribe(topic).then(function(r){
    let topics = push.getTopicsSubscribed();
    dispatch(onSubscribeChanged(topics));
  });
}

export function unsubscribe(topic) {
  push.unsubscribe(topic).then(function(r){
    let topics = push.getTopicsSubscribed();
    dispatch(onSubscribeChanged(topics));
  });
}
